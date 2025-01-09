package bartil.oussama.reconnaissancefaciale1.controlleur;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RealTimeFaceRecognitionController {

    @FXML
    private ImageView cameraView;

    @FXML
    private Label recognitionLabel;

    private VideoCapture capture;
    private boolean running = true;

    private static final String PYTHON_SERVER_URL = "http://localhost:5000/recognize";

    public void initialize() {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
        capture = new VideoCapture(0); // Open webcam
        if (!capture.isOpened()) {
            recognitionLabel.setText("Error: Cannot open camera.");
            return;
        }

        new Thread(() -> {
            Mat frame = new Mat();
            while (running) {
                if (capture.read(frame)) {
                    // Convert Mat to JavaFX Image
                    Image image = Utils.mat2Image(frame);
                    Platform.runLater(() -> cameraView.setImage(image));

                    // Send frame to Python backend for recognition
                    String response = sendFrameToBackend(frame);
                    Platform.runLater(() -> recognitionLabel.setText(response));
                }
            }
        }).start();
    }

    private String sendFrameToBackend(Mat frame) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(Utils.matToBufferedImage(frame), "jpg", baos);
            byte[] imageData = baos.toByteArray();

            URL url = new URL(PYTHON_SERVER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "image/jpeg");
            OutputStream os = conn.getOutputStream();
            os.write(imageData);
            os.flush();
            os.close();

            // Read response
            return new String(conn.getInputStream().readAllBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Could not connect to recognition server.";
        }
    }

    public void stop() {
        running = false;
        if (capture != null && capture.isOpened()) {
            capture.release();
        }
    }
}