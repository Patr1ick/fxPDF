import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.File;

public class PDFViewer extends VBox {

    private Viewer viewer;

    private Label name;

    public PDFViewer(PDF pdf){
        this.setAlignment(Pos.CENTER);

        this.viewer = new Viewer(pdf);

        this.name = new Label(pdf.getAbsolutePath());
        this.name.getStyleClass().add("name");

        super.getChildren().addAll(this.name, this.viewer);

    }
}