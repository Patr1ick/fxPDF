package util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;

public class PDF {

    private PDDocument document;
    private PDFRenderer renderer;
    private PDDocumentInformation pdDocumentInformation;

    private File f;

    public PDF(File pdf) {
        if (pdf != null) {
            loadPDF(pdf);
        } else
            throw new NullPointerException("EditablePDF editablePDF is null");
    }

    public void loadPDF(File pdf) {
        f = pdf;
        try {
            this.document = PDDocument.load(pdf);
            this.renderer = new PDFRenderer(this.document);
            this.pdDocumentInformation = document.getDocumentInformation();

            System.out.println("Successfully loaded: " + pdf.getAbsolutePath());
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(e.getMessage());
            a.setTitle("Error in " + this.getClass().getName());
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
            } else return null;
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(e.getMessage());
            a.setTitle("Error in " + this.getClass().getName());
            a.showAndWait();
        }

        return SwingFXUtils.toFXImage(pageImage, null);
    }

    public String getAuthor(){
        return this.pdDocumentInformation.getAuthor();
    }

    public String getTitle(){
        return this.pdDocumentInformation.getTitle();
    }

    public String getKeywords(){
        return this.pdDocumentInformation.getKeywords();
    }

    public String getAbsolutePath() {
        return f.getAbsolutePath();
    }

    public float getVersion() {
        return document.getVersion();
    }

    public int getNumberOfPages() {
        return document.getNumberOfPages();
    }

    public PDDocument getDocument() {
        return document;
    }
}
