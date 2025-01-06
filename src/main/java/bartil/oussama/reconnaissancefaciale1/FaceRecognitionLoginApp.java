//package bartil.oussama.reconnaissancefaciale1;
//
//import javafx.application.Application;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.layout.StackPane;
//import javafx.stage.Stage;
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.Rect;
//import org.opencv.face.FaceRecognizer;
//import org.opencv.face.EigenFaceRecognizer;
//import org.opencv.objdetect.CascadeClassifier;
//import org.opencv.videoio.VideoCapture;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.VBox;
//
//public class FaceRecognitionLoginApp extends Application {
//
//    private static final String CASCADE_PATH = "C:\\Users\\lahcen bartil\\Desktop\\Enset-M\\S3\\java\\ReconnaissanceFaciale1\\opencv\\haarcascade_frontalface_default.xml"; // Mettez ici le bon chemin du fichier cascade
//    private CascadeClassifier faceDetector;
//    private VideoCapture capture;
//    private FaceRecognizer faceRecognizer;
//
//    public static void main(String[] args) {
//        // Charge la bibliothèque OpenCV
//        nu.pattern.OpenCV.loadLocally();
//        System.out.println("OpenCV chargé : " + Core.VERSION);
//
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage primaryStage) {
//        // Initialiser le détecteur de visages
//        faceDetector = new CascadeClassifier(CASCADE_PATH);
//
//        if (faceDetector.empty()) {
//            System.out.println("Erreur : Impossible de charger le classificateur de visage.");
//            return;
//        }
//
//        // Initialiser la caméra
//        capture = new VideoCapture(0); // Utiliser la caméra par défaut
//        if (!capture.isOpened()) {
//            System.out.println("Erreur : Impossible d'ouvrir la webcam.");
//            return;
//        }
//
//        // Charger le modèle de reconnaissance faciale (exemple avec EigenFaceRecognizer)
//        faceRecognizer = EigenFaceRecognizer.create();
//
//        // Charger un modèle de reconnaissance faciale pré-entraîné (optionnel si vous avez un modèle)
//        // faceRecognizer.read("chemin_vers_votre_model.xml");
//
//        // Créer une interface JavaFX
//        VBox vbox = new VBox(10);
//        vbox.setAlignment(Pos.CENTER);
//
//        Button loginButton = new Button("Se connecter avec la reconnaissance faciale");
//        loginButton.setOnAction(e -> startFaceRecognition());
//
//        vbox.getChildren().addAll(loginButton);
//
//        Scene scene = new Scene(vbox, 300, 200);
//        primaryStage.setTitle("Login avec Reconnaissance Faciale");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    private void startFaceRecognition() {
//        new Thread(() -> {
//            Mat frame = new Mat();
//            ImageView imageView = new ImageView();
//
//            while (capture.read(frame)) {
//                // Convertir l'image en niveau de gris
//                Mat gray = new Mat();
//                org.opencv.imgproc.Imgproc.cvtColor(frame, gray, org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY);
//
//                // Détecter les visages
//                java.util.List<Rect> faces = new java.util.ArrayList<>();
//                faceDetector.detectMultiScale(gray, faces);
//
//                // Pour chaque visage détecté, reconnaître
//                for (Rect face : faces) {
//                    // Extraire la région du visage
//                    Mat faceROI = new Mat(frame, face);
//                    int predictedLabel = faceRecognizer.predict_label(faceROI);
//                    // Ici, nous pourrions afficher une alerte ou une fenêtre de login si la reconnaissance est réussie
//                    System.out.println("Visage reconnu avec label : " + predictedLabel);
//                }
//
//                // Afficher l'image dans l'interface JavaFX
//                Image image = matToImage(frame);
//                javafx.application.Platform.runLater(() -> imageView.setImage(image));
//            }
//        }).start();
//    }
//
//    private Image matToImage(Mat mat) {
//        // Convertir une Mat en Image pour l'afficher avec JavaFX
//        // Ceci peut nécessiter des ajustements pour convertir correctement
//        // Une solution simple pourrait être d'utiliser un BufferedImage comme intermédiaire
//        return new Image("file:/path/to/your/image.jpg");
//    }
//}
