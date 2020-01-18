import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

public class PDFViewer extends BorderPane {

    //Panes
    private SplitPane splitPane;

    //Nodes
    private Viewer viewer;
    private Label name;
    private PagePreview pagePreview;


    public PDFViewer(PDF pdf) {
        //this
        this.getStylesheets().add("resource/css/style.css");
        //Nodes
        this.viewer = new Viewer(pdf);

        this.name = new Label(pdf.getAbsolutePath());
        this.name.getStyleClass().add("name");

        this.pagePreview = new PagePreview(pdf, this.viewer);

        //Panes
        this.splitPane = new SplitPane();
        this.splitPane.setOrientation(Orientation.HORIZONTAL);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                splitPane.setDividerPositions(0.05f);
            }
        });
        this.splitPane.getItems().addAll(this.pagePreview, this.viewer);

        this.setTop(this.name);
        this.setCenter(this.splitPane);
    }
}