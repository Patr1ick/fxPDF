package util;

import javafx.scene.control.Alert;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.IOException;

public class EditablePDF {

    private PDDocument document;

    private PDPage[] pages;

    private PDDocumentInformation pdDocumentInformation;

    public EditablePDF() {
        this.document = new PDDocument();
        this.pdDocumentInformation = this.document.getDocumentInformation();

    }

    public void save(String name) {
        try {
            this.document.setDocumentInformation(this.pdDocumentInformation);
            this.document.save(name + ".pdf");
            this.document.close();
        } catch (IOException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(e.getMessage());
            a.setTitle("Error in " + this.getClass().getName());
            a.showAndWait();
        }
    }

    public void addPages(EditablePage[] pages) {
        for (int i = 0; i < pages.length; i++) {
            document.addPage(pages[i].getPage());
        }
    }

    public void addPage(EditablePage page) {
        document.addPage(page.getPage());
    }

    public void removePage(int page) {
        if (page >= 0 && page < document.getNumberOfPages())
            document.removePage(page);
    }

    public void removeAllPages() {
        for (int i = 0; i < document.getNumberOfPages(); i++)
            document.removePage(i);
    }

    public void setAuthor(String author) {
        this.pdDocumentInformation.setAuthor(author);
    }

    public void setTitle(String title) {
        this.pdDocumentInformation.setTitle(title);
    }

    public PDDocument getDocument() {
        return document;
    }
}
