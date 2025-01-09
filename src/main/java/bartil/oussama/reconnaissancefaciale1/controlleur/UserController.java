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
    @FXML private ImageView imageView, preview1, preview2, preview3, preview4, preview5,preview6,preview7,preview8,preview9,preview10,preview11;
    @FXML private Label progressLabel;
    @FXML private Button resetButton, addUserButton;
    @FXML private Button delete1, delete2, delete3, delete4, delete5, delete6, delete7, delete8, delete9, delete10, delete11;


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
        previews = List.of(preview1, preview2, preview3, preview4, preview5, preview6, preview7, preview8, preview9, preview10, preview11);

        // Stop camera when the window is closed
        Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setOnCloseRequest(event -> stopCamera());
        });
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
        CascadeClassifier faceCascade = new CascadeClassifier(
                new File("opencv/haarcascade_frontalface_default.xml").getAbsolutePath()
        );

        while (cameraActive) {
            capture.read(frame);
            if (!frame.empty()) {
                // Convert frame to grayscale
                Mat gray = new Mat();
                Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
                Imgproc.equalizeHist(gray, gray);

                // Detect faces
                MatOfRect faces = new MatOfRect();
                faceCascade.detectMultiScale(gray, faces, 1.1, 5, 0,
                        new Size(30, 30), new Size(300, 300));

                // Draw rectangles around detected faces
                for (Rect rect : faces.toArray()) {
                    Imgproc.rectangle(frame,                          // Image
                            new Point(rect.x, rect.y),        // Top-left point
                            new Point(rect.x + rect.width,    // Bottom-right point
                                    rect.y + rect.height),
                            new Scalar(0, 255, 0),            // Green color
                            2);                               // Thickness
                }

                // Encode the frame with rectangles for displaying
                MatOfByte buffer = new MatOfByte();
                Imgcodecs.imencode(".png", frame, buffer);
                Image image = new Image(new ByteArrayInputStream(buffer.toArray()));

                // Update UI with the image
                Platform.runLater(() -> imageView.setImage(image));
            }
        }
    }

    @FXML
    public void captureImage() throws IOException {
        if (imageCount < 11 && !frame.empty()) {
            Mat face = detectAndCropFace(frame);
            if (face != null) {
                // Resize the face to 160x160
                Mat resizedFace = new Mat();
                Size size = new Size(160, 160);
                Imgproc.resize(face, resizedFace, size);

                String path = saveImage(resizedFace);
                imagePaths.add(path);
                previews.get(imageCount).setImage(new Image(new File(path).toURI().toString()));

                // Show X button for the added image
                getDeleteButton(imageCount).setVisible(true);

                imageCount++;
                progressLabel.setText(imageCount + "/11 Photos");
            } else {
                showAlert("Erreur", "Aucun visage détecté.");
            }
        }
    }


    @FXML
    public void resetImages() {
        imageCount = 0;
        imagePaths.clear();
        progressLabel.setText("0/11 Photos");

        // Clear image previews
        previews.forEach(preview -> preview.setImage(null));

        // Hide all delete buttons
        delete1.setVisible(false);
        delete2.setVisible(false);
        delete3.setVisible(false);
        delete4.setVisible(false);
        delete5.setVisible(false);
        delete6.setVisible(false);
        delete7.setVisible(false);
        delete8.setVisible(false);
        delete9.setVisible(false);
        delete10.setVisible(false);
        delete11.setVisible(false);
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
            if (capture.isOpened()) {
                capture.release(); // Libérer la caméra
                System.out.println("Caméra arrêtée et libérée.");
            }
            imageView.setImage(null); // Réinitialiser l'image de la caméra
        }
    }

    @FXML
    public void addUser() {
        // Validate input fields
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        boolean accessStatus = accessStatusField.isSelected();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || imagePaths.size() < 11) {
            showAlert("Erreur", "Veuillez remplir tous les champs et capturer 11 photos.");
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

    @FXML
    void deleteImage1(ActionEvent event) { deleteImage(0); }

    @FXML
    void deleteImage2(ActionEvent event) { deleteImage(1); }

    @FXML
    void deleteImage3(ActionEvent event) { deleteImage(2); }

    @FXML
    void deleteImage4(ActionEvent event) { deleteImage(3); }

    @FXML
    void deleteImage5(ActionEvent event) { deleteImage(4); }

    @FXML
    void deleteImage6(ActionEvent event) { deleteImage(5); }

    @FXML
    void deleteImage7(ActionEvent event) { deleteImage(6); }

    @FXML
    void deleteImage8(ActionEvent event) { deleteImage(7); }

    @FXML
    void deleteImage9(ActionEvent event) { deleteImage(8); }

    @FXML
    void deleteImage10(ActionEvent event) { deleteImage(9); }

    @FXML
    void deleteImage11(ActionEvent event) { deleteImage(10); }

    // Delete image method
    private void deleteImage(int index) {
        if (index < imagePaths.size()) {
            // Remove image path and preview
            imagePaths.remove(index);
            previews.get(index).setImage(null);

            // Hide X button
            getDeleteButton(index).setVisible(false);

            // Shift images forward
            for (int i = index; i < imagePaths.size(); i++) {
                previews.get(i).setImage(previews.get(i + 1).getImage());
                getDeleteButton(i).setVisible(getDeleteButton(i + 1).isVisible());
            }

            // Clear last slot and hide its button
            if (imagePaths.size() < previews.size()) {
                previews.get(imagePaths.size()).setImage(null);
                getDeleteButton(imagePaths.size()).setVisible(false);
            }

            // Update progress label
            imageCount = imagePaths.size();
            progressLabel.setText(imageCount + "/11 Photos");
        }
    }

    private Button getDeleteButton(int index) {
        switch (index) {
            case 0: return delete1;
            case 1: return delete2;
            case 2: return delete3;
            case 3: return delete4;
            case 4: return delete5;
            case 5: return delete6;
            case 6: return delete7;
            case 7: return delete8;
            case 8: return delete9;
            case 9: return delete10;
            case 10: return delete11;
            default: return null;
        }
    }



}
