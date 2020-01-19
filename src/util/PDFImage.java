package util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;

public class PDFImage {

    private PDImageXObject imageXObject;


    public PDFImage(Image image) {
        try {
            this.imageXObject = JPEGFactory.createFromImage(null, SwingFXUtils.fromFXImage(image, null));
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(e.getMessage());
            a.setTitle("Error in " + this.getClass().getName());
            a.showAndWait();
        }
    }

    public PDFImage(BufferedImage bufferedImage) {
        try {
            this.imageXObject = JPEGFactory.createFromImage(null, bufferedImage);
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(e.getMessage());
            a.setTitle("Error in " + this.getClass().getName());
            a.showAndWait();
        }
    }

    public PDFImage(PDImageXObject image) {
        this.imageXObject = image;
    }

    public PDFImage(String path) {
        try {
            this.imageXObject = PDImageXObject.createFromFile(path, null);
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(e.getMessage());
            a.setTitle("Error in " + this.getClass().getName());
            a.showAndWait();
        }
    }

    public PDImageXObject getImage() {
        return this.imageXObject;
    }
}
