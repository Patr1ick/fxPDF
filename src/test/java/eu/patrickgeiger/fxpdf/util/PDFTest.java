package eu.patrickgeiger.fxpdf.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@DisplayName("Tests for the PDF class")
class PDFTest {

    @Test
    @DisplayName("Initialisation of a PDF object")
    void initPDF() throws IOException {
        PDF pdf = new PDF(new File(getClass().getClassLoader().getResource("Lorem_ipsum.pdf").getFile()));
        Assertions.assertNotNull(pdf);
        pdf.closeDocument();
    }

    @Test
    @DisplayName("Incorrect Initialisation of a PDF object")
    void initPDFIncorrect() {
        Assertions.assertThrows(NullPointerException.class, () -> new PDF(null));
    }

    @Test
    @DisplayName("Generate BufferedImages")
    void generateSwingImages() throws IOException {
        PDF pdf = new PDF(new File(getClass().getClassLoader().getResource("Lorem_ipsum.pdf").getFile()));
        Assertions.assertNotNull(pdf);
        for (int i = 0; i < pdf.getNumberOfPages(); i++) {
            BufferedImage img = pdf.getSwingImage(i);
            Assertions.assertNotNull(img);
        }
        pdf.closeDocument();
    }
}
