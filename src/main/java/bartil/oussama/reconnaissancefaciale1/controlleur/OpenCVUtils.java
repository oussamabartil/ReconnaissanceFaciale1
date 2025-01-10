package bartil.oussama.reconnaissancefaciale1.controlleur;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.opencv.core.Mat;

public class OpenCVUtils {

    public static Image matToImage(Mat mat) {
        if (mat.empty()) {
            System.err.println("Error: Empty Mat.");
            return null;
        }

        int width = mat.cols();
        int height = mat.rows();
        int channels = mat.channels();

        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        byte[] buffer = new byte[width * height * channels];
        mat.get(0, 0, buffer); // Extract pixel data from the Mat

        if (channels == 3) {
            // Convert BGR to RGB
            byte[] rgbBuffer = new byte[width * height * 3];
            for (int i = 0; i < buffer.length; i += 3) {
                rgbBuffer[i] = buffer[i + 2];     // R
                rgbBuffer[i + 1] = buffer[i + 1]; // G
                rgbBuffer[i + 2] = buffer[i];     // B
            }
            pixelWriter.setPixels(0, 0, width, height, javafx.scene.image.PixelFormat.getByteRgbInstance(), rgbBuffer, 0, width * 3);
        } else if (channels == 1) {
            // Convert Grayscale to RGB
            byte[] grayToRgbBuffer = new byte[width * height * 3];
            for (int i = 0; i < buffer.length; i++) {
                grayToRgbBuffer[i * 3] = buffer[i];     // R
                grayToRgbBuffer[i * 3 + 1] = buffer[i]; // G
                grayToRgbBuffer[i * 3 + 2] = buffer[i]; // B
            }
            pixelWriter.setPixels(0, 0, width, height, javafx.scene.image.PixelFormat.getByteRgbInstance(), grayToRgbBuffer, 0, width * 3);
        } else {
            System.err.println("Unsupported number of channels: " + channels);
            return null;
        }

        return writableImage;
    }
}
