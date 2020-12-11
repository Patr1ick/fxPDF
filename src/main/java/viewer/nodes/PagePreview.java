package viewer.nodes;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import util.PDF;
import viewer.Viewer;

public class PagePreview extends Pane {

    //Panes
    @Getter
    private VBox box;
    private StackPane stackPane;
    private ScrollPane scrollPane;

    //Nodes
    @Getter
    private Button[] pages;
    private Viewer viewer;

    //PDF
    private PDF pdf;

    //Settings
    @Setter
    private int maxLoadPicture = 500;

    public PagePreview(PDF pdf, Viewer viewer) {
        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);
        if (pdf != null && viewer != null) {
            this.getStylesheets().add(viewer.getPath_css());

            this.pdf = pdf;
            this.viewer = viewer;

            this.box = new VBox();
            this.box.setAlignment(Pos.CENTER);
            this.box.getStyleClass().add("preview-vbox");
            this.box.setSpacing(2d);
            this.box.setCache(true);
            this.box.setCacheShape(true);
            this.box.setCacheHint(CacheHint.SPEED);

            this.pages = new Button[this.pdf.getNumberOfPages()];
            initButtons();

            this.stackPane = new StackPane();
            this.stackPane.getStyleClass().add("stackpane");
            this.stackPane.getChildren().add(this.box);
            StackPane.setAlignment(this.box, Pos.CENTER);

            this.scrollPane = new ScrollPane();
            this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            this.scrollPane.setPannable(true);
            this.scrollPane.setContent(this.stackPane);

            this.getChildren().add(this.scrollPane);

            this.widthProperty().addListener((observable, oldValue, newValue) -> {
                this.scrollPane.setPrefWidth(newValue.doubleValue());
                this.stackPane.setPrefWidth(newValue.doubleValue());
            });

            this.heightProperty().addListener((observable, oldValue, newValue) -> {
                this.scrollPane.setPrefHeight(newValue.doubleValue());
                this.stackPane.setPrefHeight(newValue.doubleValue());
            });

        } else
            throw new NullPointerException("PDF is null.");

    }

    private void initButtons() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < pages.length; i++) {
                    pages[i] = new Button(" Page " + (i + 1));
                    if (pages.length < maxLoadPicture) {
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
}
