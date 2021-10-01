package eu.patrickgeiger.fxpdf.nodes.viewer;

import eu.patrickgeiger.fxpdf.util.PDF;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@DisplayName("Tests for the MinimalViewer class")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinimalViewerTest {

    @BeforeAll
    void setupJavaFX() throws RuntimeException, IOException {
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel(); // initializes JavaFX environment
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Incorrect Initialisation")
    void initViewerIncorrect() {
        PDF pdfNull = null;
        Assertions.assertThrows(NullPointerException.class, () -> {
            MinimalViewer mv = new MinimalViewer(pdfNull);
        });
    }

    @Test
    @DisplayName("Instantiation of MinimalViewer")
    void initMinimalViewer() throws IOException {
        PDF pdf = new PDF(new File(getClass().getClassLoader().getResource("Lorem_ipsum.pdf").getFile()));
        Assertions.assertNotNull(pdf);
        MinimalViewer mv = new MinimalViewer(pdf);
        Assertions.assertNotNull(mv);
    }

    @Test
    @DisplayName("Test loadPDF()")
    void loadPDF() throws IOException {
        PDF pdf = new PDF(new File(getClass().getClassLoader().getResource("Lorem_ipsum.pdf").getFile()));
        Assertions.assertNotNull(pdf);
        MinimalViewer mv = new MinimalViewer(pdf);
        Assertions.assertNotNull(mv);

        mv.loadPDF(pdf);
    }

    @Test
    @DisplayName("Test with a null object loadPDF()")
    void test_loadNullPDF() throws IOException {
        PDF pdf = new PDF(new File(getClass().getClassLoader().getResource("Lorem_ipsum.pdf").getFile()));
        Assertions.assertNotNull(pdf);
        MinimalViewer mv = new MinimalViewer(pdf);
        Assertions.assertNotNull(mv);

        Assertions.assertThrows(NullPointerException.class, () -> {
            mv.loadPDF(null);
        });
    }

    @Test
    @DisplayName("Test for load a page with a wrong argument")
    void test_loadPage() throws IOException {
        PDF pdf = new PDF(new File(getClass().getClassLoader().getResource("Lorem_ipsum.pdf").getFile()));
        Assertions.assertNotNull(pdf);
        MinimalViewer mv = new MinimalViewer(pdf);
        Assertions.assertNotNull(mv);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mv.loadPage(Integer.MAX_VALUE);
        });
    }

    @Test
    void leftPage() throws IOException {
        PDF pdf = new PDF(new File(getClass().getClassLoader().getResource("Lorem_ipsum.pdf").getFile()));
        Assertions.assertNotNull(pdf);
        MinimalViewer mv = new MinimalViewer(pdf);
        Assertions.assertNotNull(mv);
        mv.leftPage();
    }

    @Test
    void rightPage() {
    }

    @Test
    void begin() {
    }

    @Test
    void end() {
    }

    @Test
    void setScaleFactor() {
    }
}
