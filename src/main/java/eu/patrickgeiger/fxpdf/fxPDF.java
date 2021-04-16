package eu.patrickgeiger.fxpdf;

import eu.patrickgeiger.fxpdf.util.PDF;
import eu.patrickgeiger.fxpdf.viewer.AppearanceType;
import eu.patrickgeiger.fxpdf.viewer.MinimalViewer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class fxPDF extends Application {

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
            MinimalViewer mv = new MinimalViewer.MinimalViewerBuilder()
                    .setPDF(pdf)
                    .setAppearanceType(AppearanceType.LIGHT)
                    .build();
            //Scene and add PDFViewer
            Scene s = new Scene(mv);
            //Stage settings
            primaryStage.setScene(s);
            primaryStage.setMaximized(true);
            primaryStage.setTitle("eu.patrickgeiger.fxpdf.fxPDF");
            primaryStage.show();
        } else {
            System.exit(0);
        }
    }
}
