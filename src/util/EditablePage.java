package util;

import javafx.scene.control.Alert;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class EditablePage {

    private PDDocument document;
    private PDPage page;

    public EditablePage(PDDocument document) {
        this.document = document;
        this.page = new PDPage();
    }

    public EditablePage(EditablePDF editablePDF) {
        this.document = editablePDF.getDocument();
        this.page = new PDPage();
    }

    public EditablePage(EditablePDF editablePDF, PDRectangle pdRectangle) {
        this.document = editablePDF.getDocument();
        this.page = new PDPage(pdRectangle);
    }

    public void setSize(PDRectangle size){
        this.page.setMediaBox(size);
    }

    public void writeText(String text, float size, Font font, float x, float y) {
        PDPageContentStream contentStream = null;
        try {
            contentStream = new PDPageContentStream(this.document, this.page);
            contentStream.beginText();
            contentStream.setFont(font.getFont(), size);
            contentStream.moveTextPositionByAmount(x, y);
            contentStream.drawString(text);
            contentStream.endText();
            contentStream.close();
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(e.getMessage());
            a.setTitle("Error in " + this.getClass().getName());
            a.showAndWait();
        }
    }

    public void drawImage(PDFImage image, float x, float y) {
        PDPageContentStream contentStream = null;
        try {
            contentStream = new PDPageContentStream(this.document, this.page);
            contentStream.drawImage(image.getImage(), x, y);
            contentStream.close();
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(e.getMessage());
            a.setTitle("Error in " + this.getClass().getName());
            a.showAndWait();
        }
    }

    public PDPage getPage() {
        return page;
    }
}
