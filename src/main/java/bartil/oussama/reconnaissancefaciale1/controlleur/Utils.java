package bartil.oussama.reconnaissancefaciale1.controlleur;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class Utils {

    public static javafx.scene.image.Image mat2Image(Mat mat) {
        BufferedImage bufferedImage = matToBufferedImage(mat);
        return javafx.embed.swing.SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public static BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            Mat matRgb = new Mat();
            Imgproc.cvtColor(mat, matRgb, Imgproc.COLOR_BGR2RGB);
            mat = matRgb;
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        mat.get(0, 0, ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData());
        return image;
    }
}
