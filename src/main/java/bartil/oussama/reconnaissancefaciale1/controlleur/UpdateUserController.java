package bartil.oussama.reconnaissancefaciale1.controlleur;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

import bartil.oussama.reconnaissancefaciale1.dao.entities.User;
import bartil.oussama.reconnaissancefaciale1.service.UserService;
import bartil.oussama.reconnaissancefaciale1.service.UserServiceImplementation;
import org.opencv.videoio.Videoio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateUserController implements Initializable {

    @FXML private AnchorPane rootPane;
    @FXML private TextField nameField, emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox accessStatusField;
    @FXML private ImageView imageView, preview1, preview2, preview3, preview4, preview5;
    @FXML private Label progressLabel;
    @FXML private Button resetButton, updateUserButton;
    @FXML private Button delete1, delete2, delete3, delete4, delete5;

    private VideoCapture capture;
    private Mat frame;
    private boolean cameraActive;
    private List<ImageView> previews;
    private List<String> imagePaths = new ArrayList<>();
    private int imageCount = 0;

    private UserService userService = new UserServiceImplementation();
    private User selectedUser;

    private UserTableController parentController; // Reference to parent controller

    public void setParentController(UserTableController parentController) {
        this.parentController = parentController;
    }

    public void setSelectedUser(User user) {
        this.selectedUser = user;

        // Populate fields with user details
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        passwordField.setText(user.getPassword());
        accessStatusField.setSelected(user.isAccessStatus());

        // Clear any existing image previews
        imagePaths.clear();
        for (ImageView preview : previews) {
            preview.setImage(null);
        }
        for (Button deleteButton : List.of(delete1, delete2, delete3, delete4, delete5)) {
            deleteButton.setVisible(false);
        }

        // Handle image previews from the database
        String[] paths = user.getImagePath().split(",");
        for (int i = 0; i < Math.min(paths.length, previews.size()); i++) {
            try {
                String path = paths[i].trim();
                if (!path.isEmpty()) {
                    imagePaths.add(path);
                    previews.get(i).setImage(new Image(new File(path).toURI().toString()));
                    getDeleteButton(i).setVisible(true);
                }
            } catch (Exception e) {
                System.err.println("Error loading image from path: " + paths[i]);
                e.printStackTrace();
            }
        }

        // Update image count and progress label
        imageCount = imagePaths.size();
        progressLabel.setText(imageCount + "/5 Photos");
    }


    @FXML
    public void startCamera() {
        if (!cameraActive) {
            // Try to open the camera with different backends
            boolean cameraOpened = capture.open(0, Videoio.CAP_DSHOW) || capture.open(0, Videoio.CAP_MSMF);
            if (!cameraOpened) {
                showAlert("Erreur", "Impossible d'ouvrir la caméra.");
                return;
            }

            cameraActive = true;
            new Thread(this::captureFrame).start();
        }
    }


    private void captureFrame() {
        CascadeClassifier faceCascade = new CascadeClassifier(
                new File("opencv/haarcascade_frontalface_default.xml").getAbsolutePath()
        );

        while (cameraActive) {
            capture.read(frame);
            if (!frame.empty()) {
                Mat gray = new Mat();
                Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
                MatOfRect faces = new MatOfRect();
                faceCascade.detectMultiScale(gray, faces);

                for (Rect rect : faces.toArray()) {
                    Imgproc.rectangle(frame, new Point(rect.x, rect.y),
                            new Point(rect.x + rect.width, rect.y + rect.height),
                            new Scalar(0, 255, 0), 2);
                }

                MatOfByte buffer = new MatOfByte();
                Imgcodecs.imencode(".png", frame, buffer);
                Image image = new Image(new ByteArrayInputStream(buffer.toArray()));
                Platform.runLater(() -> imageView.setImage(image));
            }
        }
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
    public void captureImage() throws IOException {
        // Allow capturing a new photo only if space is available
        if (imagePaths.size() < 5 && !frame.empty()) {
            Mat face = detectAndCropFace(frame);
            if (face != null) {
                String path = saveImage(face);
                imagePaths.add(path);

                // Update the first available preview slot
                int nextSlot = imagePaths.size() - 1;
                previews.get(nextSlot).setImage(new Image(new File(path).toURI().toString()));
                getDeleteButton(nextSlot).setVisible(true);

                progressLabel.setText(imagePaths.size() + "/5 Photos");
            } else {
                showAlert("Erreur", "Aucun visage détecté.");
            }
        } else {
            showAlert("Erreur", "Vous avez déjà atteint la limite de 5 photos.");
        }
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

    @FXML
    public void resetImages() {
        // Clear all image paths, previews, and buttons
        imagePaths.clear();
        progressLabel.setText("0/5 Photos");
        imageCount = 0;

        // Reset image previews and hide delete buttons
        for (int i = 0; i < previews.size(); i++) {
            previews.get(i).setImage(null);
            getDeleteButton(i).setVisible(false);
        }

        showAlert("Succès", "Les images ont été réinitialisées !");
    }

    private String saveImage(Mat image) throws IOException {
        String path = "images/users/" + System.currentTimeMillis() + ".png";
        Imgcodecs.imwrite(path, image);
        return path;
    }

    @FXML
    public void updateUser() {
        selectedUser.setName(nameField.getText());
        selectedUser.setEmail(emailField.getText());
        selectedUser.setPassword(passwordField.getText());
        selectedUser.setAccessStatus(accessStatusField.isSelected());
        selectedUser.setImagePath(String.join(",", imagePaths));

        boolean success = userService.updateUser(selectedUser);

        if (success) {
            showAlert("Succès", "Utilisateur mis à jour avec succès !");
            if (parentController != null) {
                parentController.refreshTable(); // Refresh the table in the parent controller
            }
            closeWindow();
        } else {
            showAlert("Erreur", "Échec de la mise à jour de l'utilisateur.");
        }
    }


    @FXML
    public void cancelUpdate() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void deleteImage1() {
        deleteImage(0);
    }

    @FXML
    public void deleteImage2() {
        deleteImage(1);
    }

    @FXML
    public void deleteImage3() {
        deleteImage(2);
    }

    @FXML
    public void deleteImage4() {
        deleteImage(3);
    }

    @FXML
    public void deleteImage5() {
        deleteImage(4);
    }

    private void deleteImage(int index) {
        if (index < imagePaths.size()) {
            imagePaths.remove(index); // Remove the photo from the list
            previews.get(index).setImage(null); // Clear the preview slot
            getDeleteButton(index).setVisible(false); // Hide the delete button

            // Shift remaining photos and delete buttons forward
            for (int i = index; i < imagePaths.size(); i++) {
                previews.get(i).setImage(previews.get(i + 1).getImage());
                getDeleteButton(i).setVisible(getDeleteButton(i + 1).isVisible());
            }

            // Clear the last slot and hide its button
            if (imagePaths.size() < previews.size()) {
                previews.get(imagePaths.size()).setImage(null);
                getDeleteButton(imagePaths.size()).setVisible(false);
            }

            progressLabel.setText(imagePaths.size() + "/5 Photos");
        }
    }

    private Button getDeleteButton(int index) {
        return switch (index) {
            case 0 -> delete1;
            case 1 -> delete2;
            case 2 -> delete3;
            case 3 -> delete4;
            case 4 -> delete5;
            default -> null;
        };
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
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
}
