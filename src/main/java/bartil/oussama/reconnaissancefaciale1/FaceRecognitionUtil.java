//package bartil.oussama.reconnaissancefaciale1;
//
//import javafx.scene.control.Alert;
//import javafx.stage.Stage;
//import nu.pattern.OpenCV;
//import org.opencv.core.Mat;
//import org.opencv.videoio.VideoCapture;
//
//public class FaceRecognitionUtil {
//    public static void recognizeFace(Stage stage) {
//        OpenCV.loadLocally();
//
//        // Initialisation de la webcam
//        VideoCapture camera = new VideoCapture(0);
//
//        if (!camera.isOpened()) {
//            showError("Impossible d'ouvrir la caméra.");
//            return;
//        }
//
//        Mat frame = new Mat();
//        if (camera.read(frame)) {
//            // Simule une reconnaissance faciale réussie
//            // Ici, vous pouvez intégrer votre algorithme de reconnaissance faciale
//            showInfo("Reconnaissance faciale réussie !");
//            stage.close(); // Ferme la fenêtre de login
//        } else {
//            showError("Échec de la capture d'image.");
//        }
//
//        camera.release();
//    }
//
//    static void showInfo(String message) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Information");
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//
//    static void showError(String message) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Erreur");
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//    public static String captureFaceData() {
//        // Simule la capture et l'extraction des caractéristiques faciales
//        // Dans une vraie application, utilisez OpenCV pour extraire les caractéristiques
//        return "dummy_face_data"; // Remplacez par un véritable algorithme
//    }
//
//}


package bartil.oussama.reconnaissancefaciale1;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.util.UUID;

public class FaceRecognitionUtil {

    public static String captureFaceData() {
        nu.pattern.OpenCV.loadLocally(); // Charge OpenCV
        System.out.println("OpenCV chargé : " + Core.VERSION);

        // Charger le classificateur de visage
        String haarCascadePath = "C:\\Users\\lahcen bartil\\Desktop\\Enset-M\\S3\\java\\ReconnaissanceFaciale1\\opencv\\haarcascade_frontalface_default.xml"; // Remplacez par le chemin du fichier
        CascadeClassifier faceDetector = new CascadeClassifier(haarCascadePath);

        // Ouvrir la webcam
        VideoCapture webcam = new VideoCapture(0);
        if (!webcam.isOpened()) {
            System.err.println("Erreur : Impossible d'accéder à la webcam.");
            return null;
        }

        Mat frame = new Mat();
        System.out.println("Webcam démarrée. Appuyez sur 'CTRL+C' pour arrêter.");

        while (true) {
            // Capture un cadre de la webcam
            if (webcam.read(frame)) {
                // Convertir en niveaux de gris
                Mat grayFrame = new Mat();
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                // Détection de visage
                MatOfRect faces = new MatOfRect();
                faceDetector.detectMultiScale(grayFrame, faces);

                // Dessiner des rectangles autour des visages détectés
                for (Rect rect : faces.toArray()) {
                    Imgproc.rectangle(frame, rect, new Scalar(0, 255, 0), 2);
                }

                // Sauvegarder l'image contenant le visage détecté
                if (faces.toArray().length > 0) {
                    // Générer un identifiant unique
                    String faceDataId = UUID.randomUUID().toString();
                    String filePath = "captured_faces/" + faceDataId + ".png";

                    // Sauvegarder l'image du visage
                    Imgcodecs.imwrite(filePath, frame);
                    System.out.println("Image du visage capturée et sauvegardée : " + filePath);

                    // Fermer la webcam et retourner l'identifiant
                    webcam.release();
                    return faceDataId;
                }
            }
        }
    }
}
