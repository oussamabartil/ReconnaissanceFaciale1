package bartil.oussama.reconnaissancefaciale1;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class OpenCVTest {
    public static void main(String[] args) {
        // Charge la bibliothèque OpenCV


//        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);

        nu.pattern.OpenCV.loadLocally();
        System.out.println("OpenCV loaded version:"+Core.VERSION);
        // Test simple : Lire une image
        Mat image = Imgcodecs.imread("C:/Users/lahcen bartil/Pictures/Screenshots/Screenshot.png");
        if (image.empty()) {
            System.out.println("Erreur lors du chargement de l'image.");
        } else {
            System.out.println("Image chargée avec succès.");
        }
    }
}
