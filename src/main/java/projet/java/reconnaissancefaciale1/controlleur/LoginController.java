package projet.java.reconnaissancefaciale1.controlleur;

import projet.java.reconnaissancefaciale1.DataBase;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class LoginController {


    @FXML
    private Button login_authentifier;

    @FXML
    private CheckBox login_chechBox;

    @FXML
    private PasswordField login_password;

    @FXML
    private Button login_reconnaissanceFaciale;

    @FXML
    private TextField login_username;

    @FXML
    public void initialize() {
        // Action pour le bouton d'authentification
        login_authentifier.setOnAction(event -> authenticateUser());

        // Action pour le bouton de reconnaissance faciale
        login_reconnaissanceFaciale.setOnAction(event -> startFacialRecognition());
    }
    private Connection conn;
    private PreparedStatement stmt;
    private ResultSet rs;

    private void authenticateUser() {
        String username = login_username.getText();
        String password = login_password.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Nom d'utilisateur ou mot de passe vide.");
            return;
        }else {
            try {
                conn = DataBase.connection();

                String sql = "SELECT * FROM users WHERE userName = ? AND password = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);  // In a production app, hash the password before comparing

                // Execute the query and check if the user exists
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Authentication successful
                    showAlert("Succès", "Connexion réussie.");
                    // Proceed with the application flow (e.g., open a new window)
                } else {
                    // Authentication failed
                    showAlert("Erreur", "Nom d'utilisateur ou mot de passe incorrect.");
                }
                rs.close();
                stmt.close();
                conn.close();
            }catch (Exception e) {
                showAlert("Erreur", "Une erreur est survenue: " + e.getMessage());
                e.printStackTrace();
            }
        }


    }

    private void startFacialRecognition() {
        // Lancer la reconnaissance faciale
        showAlert("Info", "La reconnaissance faciale a été démarrée.");
        // Ajoutez ici la logique pour capturer les visages et comparer avec la base de données
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        // Action pour le bouton d'authentification
//        login_authentifier.setOnAction(event -> authenticateUser());
//
//        // Action pour le bouton de reconnaissance faciale
//        login_reconnaissanceFaciale.setOnAction(event -> startFacialRecognition());
//    }
}
