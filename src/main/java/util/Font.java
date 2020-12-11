package util;

import javafx.scene.control.Alert;
import org.apache.pdfbox.pdmodel.font.*;

import java.io.File;
import java.io.IOException;

public class Font {
    public enum StandardFont {
        Courier(PDType1Font.COURIER),
        CourierBold(PDType1Font.COURIER_BOLD),
        Helvetica(PDType1Font.HELVETICA),
        HelveticaBold(PDType1Font.HELVETICA_BOLD),
        TimesRoman(PDType1Font.TIMES_ROMAN);

        private PDFont font;

        private StandardFont(PDFont font) {
            this.font = font;
        }

        public PDFont getFont() {
            return this.font;
        }
    }

    private PDFont font;

    public Font(PDFont font) {
        this.font = font;
    }

    public Font(StandardFont font) {
        this.font = font.getFont();
    }

    public Font(EditablePDF pdf, File file) {
        try {
            this.font = PDTrueTypeFont.loadTTF(pdf.getDocument(), file);
        } catch (IOException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(e.getMessage());
            a.setTitle("Error in " + this.getClass().getName());
            a.showAndWait();
        }
    }

    public PDFont getFont() {
        return font;
    }
}