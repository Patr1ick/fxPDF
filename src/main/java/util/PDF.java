package util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PDF {

    // Logger
    private static final Logger LOGGER = LogManager.getLogger(PDF.class);

    @Getter
    private PDDocument document;
    private PDFRenderer renderer;
    private PDDocumentInformation pdDocumentInformation;

    private File f;

    @Getter
    private String pdfText;

    public PDF(File pdf) {
        if (pdf != null) {
            loadPDF(pdf);
        } else {
            LOGGER.error("The parameter pdf is null.");
            throw new NullPointerException("The parameter pdf is null");
        }
    }

    public void loadPDF(File pdf) {
        f = pdf;
        try {
            this.document = PDDocument.load(pdf);
            this.renderer = new PDFRenderer(this.document);
            this.pdDocumentInformation = document.getDocumentInformation();
            LOGGER.info("Successfully loaded: " + pdf.getAbsolutePath());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(e.getMessage());
            a.setTitle("Error in " + this.getClass().getName());
            a.showAndWait();
        }
    }

    public Image convertToFXImage(BufferedImage img) {
        return SwingFXUtils.toFXImage(img, null);
    }

    public Image getFxImage(int pageNumber, float scaleFactor) {
        BufferedImage img = getSwingImage(pageNumber, scaleFactor);
        return convertToFXImage(img);
    }

    public BufferedImage getSwingImage(int pageNumber, float scaleFactor) {
        try {
            if (pageNumber <= getNumberOfPages()) {
                if (scaleFactor >= 0.1) {
                    return renderer.renderImage(pageNumber, scaleFactor);
                } else {
                    return renderer.renderImage(pageNumber);
                }
            } else return null;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public String getAuthor() {
        return this.pdDocumentInformation.getAuthor();
    }

    public String getTitle() {
        return this.pdDocumentInformation.getTitle();
    }

    public String getKeywords() {
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

    public void closeDocument() {
        try {
            this.document.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

}
