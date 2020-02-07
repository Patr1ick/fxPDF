package viewer.nodes;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import util.PDF;
import viewer.Viewer;

public class PagePreview extends ScrollPane {

    //Panes
    private VBox box;

    //Nodes
    private Button[] pages;
    private Viewer viewer;

    //PDF
    private PDF pdf;

    //Settings
    private int maxLoadPicture = 500;

    public PagePreview(PDF pdf, Viewer viewer) {
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setPannable(true);
        this.getStylesheets().add("resource/css/style.css");

        this.box = new VBox();
        this.box.setAlignment(Pos.CENTER);
        this.box.setSpacing(2d);
        this.box.setCache(true);
        this.box.setCacheShape(true);
        this.box.setCacheHint(CacheHint.SPEED);
        if (pdf != null && viewer != null) {
            this.pdf = pdf;
            this.viewer = viewer;

            this.pages = new Button[this.pdf.getNumberOfPages()];
            initButtons();
            this.setContent(this.box);
        } else
            throw new NullPointerException("PDF is null.");

    }

    private void initButtons() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < pages.length; i++) {
                    pages[i] = new Button(" Page " + (i + 1));
                    if (pages.length < maxLoadPicture){
                        ImageView imageView = new ImageView(pdf.getPageImage(i, 0.1f));
                        imageView.setSmooth(false);
                        imageView.setPreserveRatio(true);
                        imageView.setFitWidth(80d);
                        imageView.setFitHeight(80d);
                        imageView.setCache(true);
                        imageView.setCacheHint(CacheHint.SPEED);
                        pages[i].setGraphic(imageView);
                    }
                    pages[i].setPrefSize(150d, 80d);
                    pages[i].getStyleClass().add("previewButtons");
                    pages[i].setCache(true);
                    pages[i].setCacheHint(CacheHint.SPEED);
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

    public void setMaxLoadPicture(int max){
        this.maxLoadPicture = max;
    }

    public VBox getBox() {
        return box;
    }

    public Button[] getPages() {
        return pages;
    }
}
