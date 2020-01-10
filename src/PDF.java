import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;

public class PDF {

    private PDDocument document;
    private PDFRenderer renderer;

    private File f;

    public PDF(File pdf) {
        loadPDF( pdf);
    }

    public void loadPDF(File pdf){
        f = pdf;
        try {
            document = PDDocument.load(pdf);
            renderer = new PDFRenderer(document);

            System.out.println("Successfully loaded: " + pdf.getAbsolutePath());
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(e.getMessage());
            a.setTitle("Error");
            a.showAndWait();
        }
    }

    public Image getPageImage(int pageNumber, float scaleFactor) {
        BufferedImage pageImage = null;
        try {
            if (pageNumber <= getNumberOfPages()) {
                if (scaleFactor >= 0.1) {
                    pageImage = renderer.renderImage(pageNumber, scaleFactor);
                } else {
                    pageImage = renderer.renderImage(pageNumber);
                }
            }else return null;
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(e.getMessage());
            a.setTitle("Error");
            a.showAndWait();
        }

        return SwingFXUtils.toFXImage(pageImage, null);
    }

    public String getAbsolutePath(){
        return f.getAbsolutePath();
    }

    public int getNumberOfPages(){
        return document.getNumberOfPages();
    }

    public PDDocument getDocument() {
        return document;
    }
}
