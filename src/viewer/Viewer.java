package viewer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    private PDF pdf;

    //Other Variables
    private boolean disableZoomButtons = false;
    private boolean disableNextPageButtons = false;

    private final float MINSCALE = 0.5f;
    private final float MAXSCALE = 15.0f;

    private int currentPageNumber = 0;
    private float scaleFactor = 1.0f;

    //KeyCodeCombination (HotKeys)
    private KeyCodeCombination hotkeyZoomIn;
    private KeyCodeCombination hotkeyZoomOut;

    private KeyCodeCombination hotkeyLeft;
    private KeyCodeCombination hotkeyRight;

    //Images
    private Image img_add;
    private Image img_remove;
    private Image img_left;
    private Image img_right;
    private Image img_last_page;
    private Image img_first_page;

    //viewer.ViewerType
    private ViewerType viewerType = ViewerType.IMAGE;

    public Viewer(PDF pdf) {
        if (pdf != null) {
            try {
                this.pdf = pdf;

                //Display current page
                this.currentPage = this.pdf.getPageImage(this.currentPageNumber, this.scaleFactor);
                this.imageView = new ImageView(this.currentPage);

                //viewer.ViewerType List Display
                this.listVBox = new VBox();
                this.listVBox.setAlignment(Pos.CENTER);
                this.listVBox.setSpacing(5d);

                this.pdfList = new ImageView[this.pdf.getNumberOfPages()];
                for (int i = 0; i < this.pdfList.length; i++) {
                    this.pdfList[i] = new ImageView(this.pdf.getPageImage(i, this.scaleFactor));
                    this.listVBox.getChildren().add(this.pdfList[i]);
                }

                //Panes
                this.stackPane = new StackPane();
                this.stackPane.getChildren().add(this.imageView);
                StackPane.setAlignment(this.imageView, Pos.CENTER);

                this.scrollPane = new ScrollPane();
                this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                this.scrollPane.setPannable(true);
                this.scrollPane.setContent(this.stackPane);


                //CSS
                this.getStylesheets().add("resource/css/style.css");

                //Images
                this.img_add = new Image("resource/img/baseline_add_black_48dp.png", 40, 40, true, false);
                this.img_remove = new Image("resource/img/baseline_remove_black_48dp.png", 40, 40, true, false);
                this.img_left = new Image("resource/img/baseline_keyboard_arrow_left_black_48dp.png", 48, 48, true, false);
                this.img_right = new Image("resource/img/baseline_keyboard_arrow_right_black_48dp.png", 48, 48, true, false);
                this.img_first_page = new Image("resource/img/baseline_first_page_black_48dp.png", 48, 48, true, false);
                this.img_last_page = new Image("resource/img/baseline_last_page_black_48dp.png", 48, 48, true, false);

                //Nodes
                this.nextPageLeft = new Button();
                this.nextPageLeft.setMaxSize(50d, 50d);
                this.nextPageLeft.setVisible(false);
                this.nextPageLeft.setLayoutX(25d);
                this.nextPageLeft.getStyleClass().add("buttons");
                this.nextPageLeft.setLayoutY((this.currentPage.getHeight() / 2d) - 25d);
                this.nextPageLeft.setGraphic(new ImageView(this.img_first_page));
                this.nextPageLeft.setDisable(true);
                this.nextPageLeft.setOnAction(event -> {
                    if (this.currentPageNumber == this.pdf.getDocument().getNumberOfPages()) {
                        this.nextPageRight.setDisable(false);
                        this.nextPageRight.setGraphic(new ImageView(this.img_right));
                    }
                    if (this.currentPageNumber == 1) {
                        leftPage();
                        this.nextPageLeft.setGraphic(new ImageView(this.img_first_page));
                        this.nextPageLeft.setDisable(true);
                    } else if (this.currentPageNumber > 0) {
                        leftPage();
                        this.nextPageLeft.setGraphic(new ImageView(this.img_left));
                    }
                });

                this.nextPageRight = new Button();
                this.nextPageRight.setMaxSize(50d, 50d);
                this.nextPageRight.setVisible(false);
                this.nextPageRight.getStyleClass().add("buttons");
                if (this.currentPageNumber == this.pdf.getNumberOfPages() - 1) { // 1 page pdf: numberofpages: 1 & currentpagenumber 0
                    this.nextPageRight.setGraphic(new ImageView(this.img_last_page));
                    this.nextPageRight.setDisable(true);
                } else {
                    this.nextPageRight.setGraphic(new ImageView(this.img_right));
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
                            this.nextPageRight.setGraphic(new ImageView(this.img_last_page));
                            this.nextPageRight.setDisable(true);
                        }
                    }

                });

                this.zoomIn = new Button();
                this.zoomIn.setPrefSize(50d, 50d);
                this.zoomIn.setVisible(false);
                this.zoomIn.setGraphic(new ImageView(img_add));
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
                this.zoomOut.setVisible(false);
                this.zoomOut.setGraphic(new ImageView(img_remove));
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
                        this.zoomIn.setVisible(true);
                        this.zoomOut.setVisible(true);
                    }
                    if (!disableNextPageButtons) {
                        this.nextPageRight.setVisible(true);
                        this.nextPageLeft.setVisible(true);
                    }
                });

                this.setOnMouseExited(event -> {
                    if (!disableZoomButtons) {
                        this.zoomIn.setVisible(false);
                        this.zoomOut.setVisible(false);
                    }
                    if (!disableNextPageButtons) {
                        this.nextPageRight.setVisible(false);
                        this.nextPageLeft.setVisible(false);
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


            } catch (Exception e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText(e.getMessage());
                a.setTitle("Error");
                a.showAndWait();
            }
        } else {
            throw new NullPointerException("The parameter pdf is null. Please check this parameter in the constructor.");
        }
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
        }
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
                        nextPageLeft.setGraphic(new ImageView(img_first_page));
                        if (currentPageNumber == pdf.getNumberOfPages() - 1) {
                            nextPageRight.setDisable(true);
                            nextPageRight.setGraphic(new ImageView(img_last_page));
                        } else {
                            nextPageRight.setDisable(false);
                            nextPageRight.setGraphic(new ImageView(img_right));
                        }
                    } else if (currentPageNumber == pdf.getNumberOfPages() - 1) { // Last Page
                        nextPageLeft.setDisable(false);
                        nextPageLeft.setGraphic(new ImageView(img_left));
                        nextPageRight.setDisable(true);
                        nextPageRight.setGraphic(new ImageView(img_last_page));
                    } else {
                        nextPageRight.setDisable(false);
                        nextPageRight.setGraphic(new ImageView(img_right));
                        nextPageLeft.setDisable(false);
                        nextPageLeft.setGraphic(new ImageView(img_left));

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


    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
        updateViewer();
    }

    public boolean isDisableZoomButtons() {
        return disableZoomButtons;
    }

    public void setDisableZoomButtons(boolean disableZoomButtons) {
        this.disableZoomButtons = disableZoomButtons;
    }

    public boolean isDisableNextPageButtons() {
        return disableNextPageButtons;
    }

    public void setDisableNextPageButtons(boolean disableNextPageButtons) {
        this.disableNextPageButtons = disableNextPageButtons;
    }

    public PDF getPdf() {
        return pdf;
    }

    public ViewerType getViewerType() {
        return viewerType;
    }


}

