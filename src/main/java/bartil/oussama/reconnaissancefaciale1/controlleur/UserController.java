package bartil.oussama.reconnaissancefaciale1.controlleur;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import javafx.event.ActionEvent;


import bartil.oussama.reconnaissancefaciale1.dao.entities.User;
import bartil.oussama.reconnaissancefaciale1.service.UserService;
import bartil.oussama.reconnaissancefaciale1.service.UserServiceImplementation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringJoiner;

public class UserController implements Initializable {

    @FXML private AnchorPane rootPane;
    @FXML private TextField nameField, emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox accessStatusField;
    @FXML private ImageView imageView, preview1, preview2, preview3, preview4, preview5;
    @FXML private Label progressLabel;
    @FXML private Button resetButton, addUserButton;

    private VideoCapture capture;
    private Mat frame;
    private boolean cameraActive;
    private List<ImageView> previews;
    private List<String> imagePaths = new ArrayList<>();
    private int imageCount = 0;

    private UserService userService = new UserServiceImplementation();

    // Reference to the parent controller
    private UserTableController parentController;

    // Setter method to receive parent controller reference
    public void setParentController(UserTableController parentController) {
        this.parentController = parentController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        OpenCV.loadLocally();
        capture = new VideoCapture();
        frame = new Mat();
        cameraActive = false;

        // Preview image views
        previews = List.of(preview1, preview2, preview3, preview4, preview5);
    }

    @FXML
    public void startCamera() {
        if (!cameraActive) {
            capture.open(0);
            if (capture.isOpened()) {
                cameraActive = true;
                new Thread(this::captureFrame).start();
            } else {
                showAlert("Erreur", "Impossible d'ouvrir la caméra.");
            }
        }
    }

    private void captureFrame() {
        while (cameraActive) {
            capture.read(frame);
            if (!frame.empty()) {
                MatOfByte buffer = new MatOfByte();
                Imgcodecs.imencode(".png", frame, buffer);
                Image image = new Image(new ByteArrayInputStream(buffer.toArray()));
                Platform.runLater(() -> imageView.setImage(image));
            }
        }
    }

    @FXML
    public void captureImage() throws IOException {
        if (imageCount < 5 && !frame.empty()) {
            Mat face = detectAndCropFace(frame);
            if (face != null) {
                // Resize the face to 160x160
                Mat resizedFace = new Mat();
                Size size = new Size(160, 160);
                Imgproc.resize(face, resizedFace, size);

                String path = saveImage(resizedFace);
                imagePaths.add(path);
                previews.get(imageCount).setImage(new Image(new File(path).toURI().toString()));
                imageCount++;
                progressLabel.setText(imageCount + "/5 Photos");
            } else {
                showAlert("Erreur", "Aucun visage détecté.");
            }
        }
    }

    @FXML
    public void resetImages() {
        imageCount = 0;
        imagePaths.clear();
        progressLabel.setText("0/5 Photos");
        previews.forEach(preview -> preview.setImage(null));
    }

    private Mat detectAndCropFace(Mat input) {
        Mat gray = new Mat();
        Imgproc.cvtColor(input, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(gray, gray);

        CascadeClassifier faceCascade = new CascadeClassifier(
                new File("opencv/haarcascade_frontalface_default.xml").getAbsolutePath()
        );

        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(gray, faces);

        for (Rect rect : faces.toArray()) {
            return new Mat(input, rect);
        }
        return null;
    }

    private String saveImage(Mat image) throws IOException {
        String path = "images/users/" + System.currentTimeMillis() + ".png";
        Imgcodecs.imwrite(path, image);
        return path;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void stopCamera() {
        if (cameraActive) {
            cameraActive = false;
            capture.release();
            imageView.setImage(null);
        }
    }

    @FXML
    public void addUser() {
        // Validate input fields
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        boolean accessStatus = accessStatusField.isSelected();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || imagePaths.size() < 5) {
            showAlert("Erreur", "Veuillez remplir tous les champs et capturer 5 photos.");
            return;
        }

        // Concatenate image paths
        StringJoiner imagePathJoiner = new StringJoiner(",");
        for (String path : imagePaths) {
            imagePathJoiner.add(path);
        }

        // Save user to database
        User newUser = new User(0, Date.valueOf(LocalDate.now()), accessStatus, imagePathJoiner.toString(), password, email, name);
        boolean success = userService.addUser(newUser);

        if (success) {
            showAlert("Succès", "Utilisateur ajouté avec succès !");

            // Refresh the parent table
            if (parentController != null) {
                parentController.refreshTable(new ActionEvent()); // Call refresh method
            }

            // Close the form
            Stage stage = (Stage) addUserButton.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Erreur", "Échec de l'ajout de l'utilisateur.");
        }
    }


}
