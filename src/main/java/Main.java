import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.PDF;
import viewer.AppearanceType;
import viewer.SampleViewer;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        //FileChooser to open PDF
        FileChooser f = new FileChooser();
        f.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (.pdf)", "*.pdf"));
        File selected = f.showOpenDialog(primaryStage);

        if (selected != null) {
            //Create PDF
            PDF pdf = new PDF(selected);
            SampleViewer sampleViewer = new SampleViewer(pdf);
            sampleViewer.switchTheme(AppearanceType.DARK);
            //Scene and add PDFViewer
            Scene s = new Scene(sampleViewer);
            //Stage settings
            primaryStage.setScene(s);
            primaryStage.setMaximized(true);
            primaryStage.setTitle("fxPDF");
            primaryStage.show();
        } else {
            System.exit(0);
        }
    }
}
