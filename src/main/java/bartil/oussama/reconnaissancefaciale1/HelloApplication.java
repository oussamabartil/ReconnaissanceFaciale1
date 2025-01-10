package bartil.oussama.reconnaissancefaciale1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nu.pattern.OpenCV;
import org.opencv.core.Core;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("users.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 800); // Match the BorderPane size
        stage.setTitle("User Management");
        stage.setScene(scene);
        stage.show();


//        OpenCV.loadLocally();
//        System.out.println("OpenCV version: " + Core.VERSION); // Confirm OpenCV version loaded
//
//        // Load the FXML interface
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addUsers.fxml"));
//
//        // Create and set the scene
//        Scene scene = new Scene(fxmlLoader.load(), 800, 600); // Match the size of the new interface
//        stage.setTitle("Gestion des Utilisateurs - Reconnaissance Faciale");
//        stage.setScene(scene);
//        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }

}