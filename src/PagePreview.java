import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class PagePreview extends ScrollPane {

    //Panes
    private VBox box;

    //Nodes
    private Button[] pages;
    private Viewer viewer;

    //PDF
    private PDF pdf;

    public PagePreview(PDF pdf, Viewer viewer) {
        this.pdf = pdf;
        this.viewer = viewer;

        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setPannable(true);
        this.getStylesheets().add("resource/css/style.css");


        this.box = new VBox();
        this.box.setAlignment(Pos.CENTER);
        this.box.setSpacing(1d);

        this.pages = new Button[this.pdf.getNumberOfPages()];
        initButtons();
        this.setContent(this.box);

    }

    private void initButtons(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < pages.length; i++) {
                    pages[i] = new Button();
                    ImageView imageView = new ImageView(pdf.getPageImage(i, 1.0f));
                    imageView.setSmooth(false);
                    imageView.setPreserveRatio(true);
                    imageView.setFitWidth(75d);
                    imageView.setFitHeight(75d);
                    pages[i].setGraphic(imageView);
                    pages[i].getStyleClass().add("previewButtons");
                    int value = i;
                    pages[i].setOnAction(event -> {
                        viewer.loadPage(value);
                    });
                    box.getChildren().add(pages[i]);

                }
            }
        });
    }

    public void loadPDF(PDF pdf) {
        this.pdf = pdf;
        this.box.getChildren().removeAll(this.pages);
        this.pages = new Button[this.pdf.getNumberOfPages()];
        initButtons();
    }

    public VBox getBox() {
        return box;
    }
}
