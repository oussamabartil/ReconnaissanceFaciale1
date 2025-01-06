//package bartil.oussama.reconnaissancefaciale1;
//
//
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.GridPane;
//import javafx.stage.Stage;
//
//public class Main extends Application {
//    @Override
//    public void start(Stage primaryStage) {
//        primaryStage.setTitle("Login");
//
//        // Layout de la page de login
//        GridPane gridPane = new GridPane();
//        gridPane.setHgap(10);
//        gridPane.setVgap(10);
//
//        // Champs utilisateur et mot de passe
//        TextField usernameField = new TextField();
//        usernameField.setPromptText("Nom d'utilisateur");
//
//        PasswordField passwordField = new PasswordField();
//        passwordField.setPromptText("Mot de passe");
//
//        // Boutons
//        Button loginButton = new Button("Se connecter");
//        Button faceRecognitionButton = new Button("Reconnaissance Faciale");
//
//        // Ajout des éléments au layout
//        gridPane.add(new Label("Nom d'utilisateur:"), 0, 0);
//        gridPane.add(usernameField, 1, 0);
//        gridPane.add(new Label("Mot de passe:"), 0, 1);
//        gridPane.add(passwordField, 1, 1);
//        gridPane.add(loginButton, 0, 2);
//        gridPane.add(faceRecognitionButton, 1, 2);
//
//        // Action du bouton "Reconnaissance Faciale"
//        faceRecognitionButton.setOnAction(event -> {
//            FaceRecognitionUtil.recognizeFace(primaryStage);
//        });
//
//        // Scene
//        Scene scene = new Scene(gridPane, 400, 200);
//        primaryStage.setScene(scene);
//        primaryStage.show();
//
//        loginButton.setOnAction(event -> {
//            String username = usernameField.getText();
//            String password = passwordField.getText();
//
//            if (DatabaseUtil.validateUser(username, password)) {
//                FaceRecognitionUtil.showInfo("Connexion réussie !");
//            } else {
//                FaceRecognitionUtil.showError("Nom d'utilisateur ou mot de passe incorrect.");
//            }
//        });
//
//        faceRecognitionButton.setOnAction(event -> {
//            String username = usernameField.getText();
//            String savedFaceData = DatabaseUtil.getFaceData(username);
//
//            if (savedFaceData == null) {
//                FaceRecognitionUtil.showError("Utilisateur non trouvé ou données faciales non enregistrées.");
//                return;
//            }
//
//            // Capture de l'image via la webcam
//            String capturedFaceData = FaceRecognitionUtil.captureFaceData();
//
//            // Simule une comparaison
//            if (capturedFaceData.equals(savedFaceData)) {
//                FaceRecognitionUtil.showInfo("Reconnaissance faciale réussie !");
//            } else {
//                FaceRecognitionUtil.showError("Reconnaissance faciale échouée.");
//            }
//        });
//
//
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
//

package bartil.oussama.reconnaissancefaciale1;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Charger la bibliothèque OpenCV
        nu.pattern.OpenCV.loadLocally();
        System.out.println("OpenCV chargé : " + org.opencv.core.Core.VERSION);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            // Afficher le menu
            System.out.println("\n--- Menu de Reconnaissance Faciale ---");
            System.out.println("1. Capturer des données faciales");
            System.out.println("2. Quitter");

            System.out.print("Choisissez une option : ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n--- Capture des Données Faciales ---");
                    String faceDataId = FaceRecognitionUtil.captureFaceData();
                    if (faceDataId != null) {
                        System.out.println("Données faciales capturées avec succès. ID : " + faceDataId);
                        // Vous pouvez insérer faceDataId dans SQLite ici
                    } else {
                        System.out.println("Erreur lors de la capture des données faciales.");
                    }
                    break;

                case 2:
                    System.out.println("Fermeture de l'application...");
                    running = false;
                    break;

                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }

        scanner.close();
    }
}
