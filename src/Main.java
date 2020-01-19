import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import util.EditablePDF;
import util.EditablePage;
import util.Font;
import util.PDF;
import viewer.PDFViewer;
import viewer.Viewer;

import java.io.File;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        FileChooser f = new FileChooser();
        f.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        File selected = f.showOpenDialog(primaryStage);
        if (selected != null) {
            /* Create a new PDF and write some text in it...
            EditablePDF editablePDF = new EditablePDF();
            EditablePage e = new EditablePage(editablePDF);
            Font font = new Font(Font.StandardFont.Courier);
            editablePDF.addPage(e);
            e.writeText("Hello World", 13, font, 100, 100);
            e.setSize(PDRectangle.A0);
            editablePDF.save("Test");
            */

            PDF pdf = new PDF(selected);
            PDFViewer v;
            v = new PDFViewer( primaryStage, pdf);
            v.setMenuBarEnable(true);
            //Viewer v = new Viewer(pdf);
            Scene s = new Scene(v);
            primaryStage.setScene(s);
            primaryStage.setMaximized(true);
            primaryStage.setTitle("fxPDF");
            primaryStage.show();
        } else
            System.exit(0);

    }
}
