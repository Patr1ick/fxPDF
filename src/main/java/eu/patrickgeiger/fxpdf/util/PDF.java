package eu.patrickgeiger.fxpdf.util;

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

    /**
     * @param pdf The file to the pdf
     */
    public PDF(File pdf) {
        if (pdf != null) {
            loadPDF(pdf);
        } else {
            LOGGER.error("The parameter pdf is null.");
            throw new NullPointerException("The parameter pdf is null");
        }
    }

    /**
     * Load a new PDF
     *
     * @param pdf The file to the pdf
     */
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

    /**
     * Convert a given BufferedImage to the JavaFX Image
     *
     * @param img A BufferedImage
     * @return A JavaFX Image
     */
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

    /**
     * @return
     */
    public String getAuthor() {
        return this.pdDocumentInformation.getAuthor();
    }

    /**
     * @return Return the title of the pdf
     */
    public String getTitle() {
        return this.pdDocumentInformation.getTitle();
    }

    public String getKeywords() {
        return this.pdDocumentInformation.getKeywords();
    }

    /**
     * @return Return the absolute path of the pdf
     */
    public String getAbsolutePath() {
        return f.getAbsolutePath();
    }

    /**
     * @return Return the version of the pdf
     */
    public float getVersion() {
        return document.getVersion();
    }

    /**
     * @return Return the number of pages of the pdf
     */
    public int getNumberOfPages() {
        return document.getNumberOfPages();
    }

    /**
     * Close the Document
     */
    public void closeDocument() {
        try {
            this.document.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

}
