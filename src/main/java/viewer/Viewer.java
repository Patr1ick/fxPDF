package viewer;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import util.PDF;
import viewer.event.PageSwitchEvent;

public class Viewer extends Pane {

    //Panes
    private ScrollPane scrollPane;
    private StackPane stackPane;
    private VBox zoomTool;

    //viewer.ViewerType LIST:
    private VBox listVBox;
    private ImageView[] pdfList;

    //Nodes
    private Image currentPage;
    private ImageView imageView;

    private Button zoomIn;
    private Button zoomOut;

    private Button nextPageLeft;
    private Button nextPageRight;

    //util.PDF
    @Getter
    private PDF pdf;

    //Other Variables
    @Getter
    @Setter
    private boolean disableZoomButtons = false;
    @Getter
    @Setter
    private boolean disableNextPageButtons = false;

    private ApperanceType apperanceType;

    @Getter
    private String path_css;
    private final String PATH_DARK_CSS = "css/style-dark.css";
    private final String PATH_LIGHT_CSS = "css/style.css";

    private final float MINSCALE = 0.5f;
    private final float MAXSCALE = 15.0f;

    @Getter
    private int currentPageNumber = 0;
    @Getter
    private float scaleFactor = 1.0f;

    //KeyCodeCombination (HotKeys)
    private KeyCodeCombination hotkeyZoomIn;
    private KeyCodeCombination hotkeyZoomOut;

    private KeyCodeCombination hotkeyLeft;
    private KeyCodeCombination hotkeyRight;

    //Images
    private Image img_zoom_in;
    private Image img_zoom_out;
    private Image img_left;
    private Image img_right;

    //viewer.ViewerType
    @Getter
    private ViewerType viewerType = ViewerType.IMAGE;

    public Viewer(PDF pdf) {
        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);
        if (pdf != null) {
            try {
                this.pdf = pdf;
                init();
            } catch (Exception e) {
                e.printStackTrace();
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText(e.getMessage());
                a.setTitle("Error");
                a.showAndWait();
            }
        } else {
            throw new NullPointerException("The parameter pdf is null. Please check this parameter in the constructor.");
        }
    }

    public Viewer(PDF pdf, ApperanceType apperanceType, String path) {
        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);
        if (pdf != null) {
            try {
                this.pdf = pdf;
                if (apperanceType == ApperanceType.Custom) {
                    if (path != null || path != "") {
                        this.apperanceType = apperanceType;
                        this.path_css = path;
                    } else {
                        throw new NullPointerException("The given path is null.");
                    }
                } else {
                    this.apperanceType = apperanceType;
                }
                init();
            } catch (Exception e) {
                e.printStackTrace();
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText(e.getMessage());
                a.setTitle("Error");
                a.showAndWait();
            }
        } else {
            throw new NullPointerException("The parameter pdf is null. Please check this parameter in the constructor.");
        }
    }

    private Viewer(ViewerBuilder builder) {
        if (builder.pdf != null) {
            this.pdf = builder.pdf;
            this.apperanceType = builder.apperanceType;
            if (this.apperanceType == ApperanceType.Custom)
                if (builder.path != null)
                    path_css = builder.path;
                else
                    throw new NullPointerException("The parameter path is null");
            init();
        } else
            throw new NullPointerException("The parameter pdf is null.");

    }

    public static class ViewerBuilder {
        private PDF pdf;
        private ApperanceType apperanceType;
        private String path;

        public ViewerBuilder() {
            this.pdf = null;
            this.apperanceType = ApperanceType.LIGHT;
            this.path = null;
        }

        public ViewerBuilder setPDF(PDF pdf) {
            this.pdf = pdf;
            return this;
        }

        public ViewerBuilder setAppearanceType(ApperanceType apperanceType) {
            this.apperanceType = apperanceType;
            return this;
        }

        public ViewerBuilder setPath(String path) {
            this.path = path;
            return this;
        }

        public Viewer build() {
            return new Viewer(this);
        }
    }

    private void init() {
        //Display current page
        this.currentPage = this.pdf.getPageImage(this.currentPageNumber, this.scaleFactor);
        this.imageView = new ImageView(this.currentPage);

        //viewer.ViewerType List Display
        this.listVBox = new VBox();
        this.listVBox.setAlignment(Pos.CENTER);
        this.listVBox.setSpacing(5d);

        if (this.viewerType == ViewerType.LIST)
            loadPDFasList();

        //Panes
        this.stackPane = new StackPane();
        this.stackPane.getStyleClass().add("stackpane");
        this.stackPane.getChildren().add(this.imageView);
        StackPane.setAlignment(this.imageView, Pos.CENTER);

        this.scrollPane = new ScrollPane();
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollPane.setPannable(true);
        this.scrollPane.setContent(this.stackPane);

        //CSS
        switch (this.apperanceType) {
            case DARK:
                this.getStylesheets().add("css/style-dark.css");
                path_css = "css/style-dark.css";
                break;
            case LIGHT:
                this.getStylesheets().add("css/style.css");
                path_css = "css/style.css";
                break;
            case Custom:
                this.getStylesheets().add(path_css);
                this.stackPane.getStylesheets().add(path_css);
                break;
        }

        //Images
        this.img_zoom_in = new Image(getClass().getResourceAsStream("/img/zoom_in.png"));
        this.img_zoom_out = new Image(getClass().getResourceAsStream("/img/zoom_out.png"));
        this.img_left = new Image(getClass().getResourceAsStream("/img/left.png"));
        this.img_right = new Image(getClass().getResourceAsStream("/img/right.png"));

        this.nextPageLeft = new Button();
        this.nextPageLeft.setMaxSize(50d, 50d);
        this.nextPageLeft.setLayoutX(25d);
        this.nextPageLeft.getStyleClass().add("buttons");
        this.nextPageLeft.setLayoutY((this.currentPage.getHeight() / 2d) - 25d);
        this.nextPageLeft.setGraphic(new ImageView(this.img_left));
        this.nextPageLeft.setDisable(true);
        this.nextPageLeft.setOnAction(event -> {
            if (this.currentPageNumber == this.pdf.getDocument().getNumberOfPages()) {
                this.nextPageRight.setDisable(false);
                this.nextPageRight.setGraphic(new ImageView(this.img_right));
            }
            if (this.currentPageNumber == 1) {
                leftPage();
                this.nextPageLeft.setDisable(true);
            } else if (this.currentPageNumber > 0) {
                leftPage();
                this.nextPageLeft.setGraphic(new ImageView(this.img_left));
            }
        });

        this.nextPageRight = new Button();
        this.nextPageRight.setMaxSize(50d, 50d);
        this.nextPageRight.getStyleClass().add("buttons");
        this.nextPageRight.setGraphic(new ImageView(this.img_right));
        if (this.currentPageNumber == this.pdf.getNumberOfPages() - 1) { // 1 page pdf: numberofpages: 1 & currentpagenumber 0
            this.nextPageRight.setDisable(true);
        }
        this.nextPageRight.setLayoutX(this.currentPage.getWidth() - 75d);
        this.nextPageRight.setLayoutY((this.currentPage.getHeight() / 2d) - 25d);
        this.nextPageRight.setOnAction(event -> {
            if (this.currentPageNumber == 0) {
                rightPage();
                this.nextPageLeft.setGraphic(new ImageView(this.img_left));
                this.nextPageLeft.setDisable(false);
            } else if (this.currentPageNumber < this.pdf.getNumberOfPages()) {
                rightPage();
                this.nextPageRight.setGraphic(new ImageView(this.img_right));
                if (this.currentPageNumber == this.pdf.getNumberOfPages() - 1) {
                    this.nextPageRight.setDisable(true);
                }
            }

        });

        this.zoomIn = new Button();
        this.zoomIn.setPrefSize(50d, 50d);
        this.zoomIn.setGraphic(new ImageView(img_zoom_in));
        this.zoomIn.getStyleClass().add("buttons");
        this.zoomIn.setOnAction(event -> {
            if (this.scaleFactor <= this.MAXSCALE) {
                if (this.scaleFactor < 0.0f) {
                    this.scaleFactor = 0.0f;
                } else
                    this.scaleFactor += 1.0f;
            }
            updateViewer();
        });

        this.zoomOut = new Button();
        this.zoomOut.setPrefSize(50d, 50d);
        this.zoomOut.setGraphic(new ImageView(img_zoom_out));
        this.zoomOut.getStyleClass().add("buttons");
        this.zoomOut.setOnAction(event -> {
            if (scaleFactor > 1.0f) {
                scaleFactor -= 1.0f;
            } else if (scaleFactor > MINSCALE) {
                scaleFactor -= 0.25f;
            }
            updateViewer();
        });

        this.zoomTool = new VBox();
        this.zoomTool.setAlignment(Pos.CENTER);
        this.zoomTool.setSpacing(10d);
        this.zoomTool.setPadding(new Insets(10));
        this.zoomTool.getChildren().addAll(this.zoomIn, this.zoomOut);

        this.getChildren().addAll(this.scrollPane, this.zoomTool, this.nextPageLeft, this.nextPageRight);
        //Events
        this.setOnMouseEntered(event -> {
            if (!disableZoomButtons) {
                this.fadeZoomButton(0, 1);
            }
            if (!disableNextPageButtons) {
                this.fadeNavigateButton(0, 1);
            }
        });

        this.setOnMouseExited(event -> {
            if (!disableZoomButtons) {
                this.fadeZoomButton(1, 0);
            }
            if (!disableNextPageButtons) {
                this.fadeNavigateButton(1, 0);
            }
        });

        this.widthProperty().addListener((observable, oldValue, newValue) -> {
            this.scrollPane.setPrefWidth(newValue.doubleValue());
            this.stackPane.setPrefWidth(newValue.doubleValue());
            this.zoomTool.setLayoutX(newValue.doubleValue() - 100d);
            this.nextPageRight.setLayoutX(newValue.doubleValue() - 75d);
        });

        this.heightProperty().addListener((observable, oldValue, newValue) -> {
            this.scrollPane.setPrefHeight(newValue.doubleValue());
            this.stackPane.setPrefHeight(newValue.doubleValue());
            this.zoomTool.setLayoutY(newValue.doubleValue() - 150d);
            this.nextPageLeft.setLayoutY((newValue.doubleValue() / 2d) - 25d);
            this.nextPageRight.setLayoutY((newValue.doubleValue() / 2d) - 25d);
        });

        hotkeyZoomIn = new KeyCodeCombination(KeyCode.ADD, KeyCombination.CONTROL_DOWN);
        hotkeyZoomOut = new KeyCodeCombination(KeyCode.SUBTRACT, KeyCombination.CONTROL_DOWN);
        hotkeyLeft = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN);
        hotkeyRight = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);

        this.setOnKeyPressed(event -> {

            if (hotkeyZoomIn.match(event)) {
                if (scaleFactor <= MAXSCALE)
                    scaleFactor += 1.0f;
            }

            if (hotkeyZoomOut.match(event)) {
                if (scaleFactor > 1.0f) {
                    scaleFactor -= 1.0f;
                } else if (scaleFactor > MINSCALE) {
                    scaleFactor -= 0.25f;
                }
            }

            if (hotkeyLeft.match(event))
                leftPage();

            if (hotkeyRight.match(event))
                rightPage();

            updateViewer();
        });

        this.imageView.setOnScroll(event -> {
            if (event.isControlDown()) {
                if (event.getDeltaY() < 0 && scaleFactor > 1.0f) {
                    scaleFactor -= 1.0f;
                } else if (event.getDeltaY() > 0 && scaleFactor <= MAXSCALE) {
                    scaleFactor += 1.0f;
                }
                updateViewer();
            }
        });

        System.out.println("Max Page Numbers: " + this.pdf.getNumberOfPages());
    }


    public void setViewerType(ViewerType viewerType) {
        if (viewerType == ViewerType.IMAGE) {
            this.getChildren().addAll(this.nextPageRight, this.nextPageLeft, this.zoomTool);
            this.stackPane.getChildren().remove(this.listVBox);
            this.stackPane.getChildren().add(this.imageView);
        } else if (viewerType == ViewerType.LIST) {
            this.getChildren().removeAll(this.nextPageRight, this.nextPageLeft, this.zoomTool);
            this.stackPane.getChildren().remove(this.imageView);
            this.stackPane.getChildren().add(this.listVBox);
            loadPDFasList();
        }
    }

    public void loadPDFasList() {
        new Thread(() -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    pdfList = new ImageView[pdf.getNumberOfPages()];
                    for (int i = 0; i < pdfList.length; i++) {
                        pdfList[i] = new ImageView(pdf.getPageImage(i, scaleFactor));
                        listVBox.getChildren().add(pdfList[i]);

                    }
                }
            });
        }).start();

    }

    public void loadPDF(PDF pdf) {
        if (pdf != null) {
            this.pdf = pdf;
            this.currentPageNumber = 0;
            updateViewer();
            System.out.println("Max Page Numbers: " + this.pdf.getNumberOfPages());
        } else {
            throw new NullPointerException("The parameter pdf is null. Please check this parameter in the constructor.");
        }
    }

    public void updateViewer() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (viewerType == ViewerType.IMAGE) {
                    //Refresh page
                    currentPage = pdf.getPageImage(currentPageNumber, scaleFactor);
                    imageView.setImage(currentPage);
                    //Update buttons
                    if (currentPageNumber == 0) { // First Page
                        nextPageLeft.setDisable(true);
                        if (currentPageNumber == pdf.getNumberOfPages() - 1) {
                            nextPageRight.setDisable(true);
                        } else {
                            nextPageRight.setDisable(false);
                        }
                    } else if (currentPageNumber == pdf.getNumberOfPages() - 1) { // Last Page
                        nextPageLeft.setDisable(false);
                        nextPageRight.setDisable(true);
                    } else {
                        nextPageRight.setDisable(false);
                        nextPageLeft.setDisable(false);
                    }
                } else if (viewerType == ViewerType.LIST) {
                    listVBox.getChildren().remove(pdfList);
                    for (int i = 0; i < pdfList.length; i++) {
                        pdfList[i] = new ImageView(pdf.getPageImage(i, scaleFactor));
                        listVBox.getChildren().add(pdfList[i]);
                    }
                }
            }
        });

    }

    public void loadPage(int pageNumber) {
        if (this.viewerType == ViewerType.IMAGE) {
            if (pageNumber >= 0 && pageNumber <= this.pdf.getNumberOfPages())
                this.currentPageNumber = pageNumber;
            this.fireEvent(new PageSwitchEvent("LOADED"));
            updateViewer();
        } else if (this.viewerType == ViewerType.LIST) {
            //this.scrollPane.setVvalue((1d / this.pdf.getNumberOfPages() * pageNumber)); // currently not working
        }
    }

    public void leftPage() {
        if (currentPageNumber > 0) {
            currentPageNumber -= 1;
            this.fireEvent(new PageSwitchEvent("LEFT"));
        }
        updateViewer();
    }

    public void rightPage() {
        if (currentPageNumber < pdf.getNumberOfPages() - 1) {
            currentPageNumber += 1;
            this.fireEvent(new PageSwitchEvent("RIGHT"));
        }
        updateViewer();
    }

    private void fadeNavigateButton(double from, double to) {
        FadeTransition ft = new FadeTransition();
        ft.setFromValue(from);
        ft.setToValue(to);
        ft.setDuration(new Duration(200));
        ft.setNode(this.nextPageRight);
        FadeTransition ft1 = new FadeTransition();
        ft1.setFromValue(from);
        ft1.setToValue(to);
        ft1.setDuration(new Duration(200));
        ft1.setNode(this.nextPageLeft);
        ParallelTransition pt = new ParallelTransition(ft, ft1);
        pt.setInterpolator(Interpolator.EASE_BOTH);
        pt.playFromStart();
    }

    private void fadeZoomButton(double from, double to) {
        FadeTransition ft = new FadeTransition();
        ft.setFromValue(from);
        ft.setToValue(to);
        ft.setDuration(new Duration(200));
        ft.setNode(this.zoomTool);
        ft.playFromStart();
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
        updateViewer();
    }
}

