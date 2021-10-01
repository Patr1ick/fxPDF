package eu.patrickgeiger.fxpdf.util;

import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * The PDF class
 *
 * @author Patr1ick
 */
public class PDF {

    // Logger
    private static final Logger LOGGER = LogManager.getLogger(PDF.class);

    @Getter
    private PDDocument document;
    private PDFRenderer renderer;
    private PDDocumentInformation pdDocumentInformation;

    private File file;

    @Getter
    private String pdfText;

    /**
     * @param pdf The file to the pdf
     */
    public PDF(@NonNull File pdf) throws IOException {
        loadPDF(pdf);
    }

    /**
     * Load a new File
     *
     * @param pdf The file to the pdf
     */
    public void loadPDF(@NonNull File pdf) throws IOException {
        file = pdf;
        this.document = PDDocument.load(pdf);
        this.renderer = new PDFRenderer(this.document);
        this.pdDocumentInformation = document.getDocumentInformation();
        LOGGER.info("Successfully loaded: {}", pdf.getAbsolutePath());
    }

    /**
     * Generates a BufferedImage with the specified page number and scaling factor.
     * If the page number is outside that of the PDF then a null object is returned.
     * The DPI is 300 by default.
     *
     * @param pageNumber The number of the page to be generated
     * @return A BufferedImage object of the given page
     */
    public BufferedImage renderPage(int pageNumber) {
        try {
            if (pageNumber <= getNumberOfPages()) {
                return renderer.renderImageWithDPI(pageNumber, 300);
            } else return null;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    /**
     * Generates a BufferedImage with the specified page number and scaling factor.
     * If the page number is outside that of the PDF then a null object is returned.
     *
     * @param pageNumber The number of the page to be generated
     * @param dpi        The DPI with which the PDF is generated
     * @return A BufferedImage object of the given page
     */
    public BufferedImage renderPage(int pageNumber, int dpi) {
        try {
            if (pageNumber <= getNumberOfPages()) {
                return renderer.renderImageWithDPI(pageNumber, dpi);
            } else return null;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    /**
     * @return A string with the author of the pdf
     */
    public String getAuthor() {
        return this.pdDocumentInformation.getAuthor();
    }

    /**
     * @return The title of the pdf
     */
    public String getTitle() {
        return this.pdDocumentInformation.getTitle();
    }

    /**
     * @return The keywords of the pdf
     */
    public String getKeywords() {
        return this.pdDocumentInformation.getKeywords();
    }

    /**
     * @return The absolute path of the pdf
     */
    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    /**
     * @return The version of the pdf
     */
    public float getVersion() {
        return document.getVersion();
    }

    /**
     * @return The number of pages of the pdf
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
