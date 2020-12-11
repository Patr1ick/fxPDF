import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.PDF;
import viewer.ApperanceType;
import viewer.PDFViewer;

import java.io.File;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        //FileChooser to open PDF
        FileChooser f = new FileChooser();
        f.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File selected = f.showOpenDialog(primaryStage);

        if (selected != null) {
            //Create PDF
            PDF pdf = new PDF(selected);
            //PDFViewer
            PDFViewer v = new PDFViewer.PDFViewerBuilder()
                    .setStage(primaryStage)
                    .setPDF(pdf)
                    .setApperanceType(ApperanceType.LIGHT)
                    .setPath(null)
                    .build();
            /*Viewer v = new Viewer.ViewerBuilder()
                    .setPDF(pdf)
                    .setAppearanceType(ApperanceType.DARK)
                    .setPath(null)
                    .build();*/

            v.setMenuBarEnable(true);
            //Scene and add PDFViewer
            Scene s = new Scene(v);
            //Stage settings
            primaryStage.setScene(s);
            primaryStage.setMaximized(true);
            primaryStage.setTitle("fxPDF");
            primaryStage.show();
        } else
            System.exit(0);

    }
}
