package bartil.oussama.reconnaissancefaciale1.controlleur;

import bartil.oussama.reconnaissancefaciale1.controlleur.OpenCVUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nu.pattern.OpenCV;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


public class RealTimeFaceRecognitionController {

    @FXML
    private ImageView cameraView;

    @FXML
    private Label recognitionLabel;

    private VideoCapture capture;
    private boolean running = true;

    private static final String PYTHON_SERVER_URL = "http://localhost:5000/recognize";

    public void initialize() {
        OpenCV.loadLocally(); // Load OpenCV dynamically
        System.out.println("OpenCV version: " + Core.VERSION);
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
                    Image image = OpenCVUtils.matToImage(frame);
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
            Path tempFile = Files.createTempFile("frame_" + UUID.randomUUID(), ".jpg");
            org.opencv.imgcodecs.Imgcodecs.imwrite(tempFile.toString(), frame);

            URL url = new URL(PYTHON_SERVER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            String boundary = "Boundary-" + UUID.randomUUID().toString();
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (OutputStream out = conn.getOutputStream()) {
                String header = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"frame\"; filename=\"" + tempFile.getFileName() + "\"\r\n"
                        + "Content-Type: image/jpeg\r\n\r\n";
                out.write(header.getBytes());
                Files.copy(tempFile, out);
                out.write(("\r\n--" + boundary + "--\r\n").getBytes());
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = new String(conn.getInputStream().readAllBytes());
                Files.delete(tempFile);

                // Parse the response to extract name and email
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray results = jsonResponse.getJSONArray("results");

                StringBuilder displayText = new StringBuilder("Recognition Results:\n");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    String name = result.getString("name");
                    String email = result.getString("email");
                    displayText.append(String.format("Name: %s, Email: %s\n", name, email));
                }

                return displayText.toString();
            } else {
                return "Error: Server responded with code " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Could not connect to recognition server.";
        }
    }


    private String parseResponse(String jsonResponse) {
        try {
            // Parse JSON response
            org.json.JSONObject jsonObject = new org.json.JSONObject(jsonResponse);
            boolean recognized = jsonObject.getBoolean("recognized");
            if (recognized) {
                StringBuilder result = new StringBuilder("Recognition Results:\n");
                org.json.JSONArray results = jsonObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    org.json.JSONObject resultObj = results.getJSONObject(i);
                    int userId = resultObj.optInt("user_id", -1);
                    double distance = resultObj.optDouble("distance", -1);
                    if (userId != -1) {
                        result.append("User ID: ").append(userId)
                                .append(", Distance: ").append(String.format("%.4f", distance))
                                .append("\n");
                    } else {
                        result.append("Face not recognized.\n");
                    }
                }
                return result.toString().trim();
            } else {
                return "No face recognized.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Unable to parse server response.";
        }
    }


    public void stop() {
        running = false;
        if (capture != null && capture.isOpened()) {
            capture.release();
        }
    }
}
