import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class Main extends Application {

    public static void main(String[] args){
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        FileChooser f = new FileChooser();
        f.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        File selected = f.showOpenDialog(primaryStage);
        PDF pdf = new PDF(selected);
        Viewer v;
        if (selected != null){
            v = new Viewer(pdf);
            Scene s = new Scene(v);
            primaryStage.setScene(s);
        }
        primaryStage.show();
    }
}
