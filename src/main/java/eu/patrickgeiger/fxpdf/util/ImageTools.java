package eu.patrickgeiger.fxpdf.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import lombok.NonNull;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * @author Patr1ick
 */
public class ImageTools {

    private ImageTools() {
        throw new IllegalStateException("Utility Class");
    }

    /**
     * Convert a given BufferedImage to a JavaFX Image
     *
     * @param img A BufferedImage
     * @return A JavaFX Image
     */
    public static Image convertToFXImage(@NonNull BufferedImage img) {
        return SwingFXUtils.toFXImage(img, null);
    }


    /**
     * Scales the given BufferedImage about the scaleFactor
     *
     * @param img         The BufferedImage that should be scaled
     * @param scaleFactor The scaleFactor that should be used
     * @return The scaled BufferedImage
     */
    public static BufferedImage scaleBufferedImage(BufferedImage img, float scaleFactor) {
        var scaledImage = new BufferedImage((int) (img.getWidth() * scaleFactor), (int) (img.getHeight() * scaleFactor), BufferedImage.TYPE_INT_ARGB);
        var transform = new AffineTransform();
        transform.scale(scaleFactor, scaleFactor);
        var op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        op.filter(img, scaledImage);
        return scaledImage;
    }

}
