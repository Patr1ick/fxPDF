import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class Viewer extends Pane {

    private PDF pdf;

    private Image currentPage;
    private ImageView imageView;

    private int currentPageNumber = 0;
    private float scaleFactor = 1.0f;

    private ScrollPane scrollPane;
    private StackPane stackPane;
    private VBox zoomTool;

    private Button zoomIn;
    private Button zoomOut;

    private Button nextPageLeft;
    private Button nextPageRight;

    private boolean disableZoomButtons = false;
    private boolean disableNextPageButtons = false;

    private final float MAXSCALE = 15.0f;

    private KeyCodeCombination hotkeyZoomIn;
    private KeyCodeCombination hotkeyZoomOut;

    private Image img_add;
    private Image img_remove;
    private Image img_left;
    private Image img_right;
    private Image img_last_page;
    private Image img_first_page;

    public Viewer(PDF pdf) {
        this.pdf = pdf;

        //Display current page
        this.currentPage = this.pdf.getPageImage(this.currentPageNumber, this.scaleFactor);
        this.imageView = new ImageView(this.currentPage);

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
        this.img_add = new Image("resource/img/baseline_add_black_48dp.png", 30, 30, true, false);
        this.img_remove = new Image("resource/img/baseline_remove_black_48dp.png", 30, 30, true, false);
        this.img_left = new Image("resource/img/baseline_keyboard_arrow_left_black_48dp.png", 30, 30, true, false);
        this.img_right = new Image("resource/img/baseline_keyboard_arrow_right_black_48dp.png", 30, 30, true, false);
        this.img_first_page = new Image("resource/img/baseline_first_page_black_48dp.png", 30, 30, true, false);
        this.img_last_page = new Image("resource/img/baseline_last_page_black_48dp.png", 30, 30, true, false);


        this.nextPageLeft = new Button();
        this.nextPageLeft.setPrefSize(50d, 50d);
        this.nextPageLeft.setVisible(false);
        this.nextPageLeft.setLayoutX(25d);
        this.nextPageLeft.setLayoutY((this.currentPage.getHeight() / 2d) - 25d);
        this.nextPageLeft.setGraphic(new ImageView(this.img_first_page));
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
        this.nextPageRight.setPrefSize(50d, 50d);
        this.nextPageRight.setVisible(false);
        this.nextPageRight.setGraphic(new ImageView(this.img_right));
        this.nextPageRight.setLayoutX(this.currentPage.getWidth() - 75d);
        this.nextPageRight.setLayoutY((this.currentPage.getHeight() / 2d) - 25d);
        this.nextPageRight.setOnAction(event -> {
            if (this.currentPageNumber == 0) {
                this.nextPageLeft.setGraphic(new ImageView(this.img_left));
                this.nextPageLeft.setDisable(false);
            }
            if (this.currentPageNumber == this.pdf.getNumberOfPages() - 1) {
                rightPage();
                this.nextPageRight.setGraphic(new ImageView(this.img_last_page));
                this.nextPageRight.setDisable(true);
            } else if (this.currentPageNumber < this.pdf.getNumberOfPages()) {
                rightPage();
                this.nextPageRight.setGraphic(new ImageView(this.img_right));
            }

        });

        this.zoomIn = new Button();
        this.zoomIn.setPrefSize(50d, 50d);
        this.zoomIn.setVisible(false);
        this.zoomIn.setGraphic(new ImageView(img_add));
        this.zoomIn.getStyleClass().add("zoom");
        this.zoomIn.setOnAction(event -> {
            if (this.scaleFactor <= this.MAXSCALE) {
                this.scaleFactor += 1.0f;
            }
            updatePage();
        });

        this.zoomOut = new Button();
        this.zoomOut.setPrefSize(50d, 50d);
        this.zoomOut.setVisible(false);
        this.zoomOut.setGraphic(new ImageView(img_remove));
        this.zoomOut.getStyleClass().add("zoom");
        this.zoomOut.setOnAction(event -> {
            if (this.scaleFactor > 1) {
                this.scaleFactor -= 1.0f;
            }
            updatePage();
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
            if (!disableNextPageButtons){
                this.nextPageRight.setVisible(true);
                this.nextPageLeft.setVisible(true);
            }
        });

        this.setOnMouseExited(event -> {
            if (!disableZoomButtons) {
                this.zoomIn.setVisible(false);
                this.zoomOut.setVisible(false);
            }
            if (!disableNextPageButtons){
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

        this.setOnKeyPressed(event -> {

            if (hotkeyZoomIn.match(event)) {
                if (scaleFactor <= MAXSCALE)
                    scaleFactor += 1.0f;
            }

            if (hotkeyZoomOut.match(event)) {
                if (scaleFactor > 1.0f)
                    scaleFactor -= 1.0f;
            }

            updatePage();
        });

        /*
        this.setOnScroll(event -> {
            System.out.println(event.getTotalDeltaY());
            if (controlPressed){
                if (event.getDeltaY() < 0 && scaleFactor > 1.0f){
                    scaleFactor -= 1.0f;
                }else if(scaleFactor <=  MAXSCALE){
                    scaleFactor += 1.0f;
                }
                updatePage();
            }
        });
        */

        System.out.println("Max Page Numbers: " + this.pdf.getNumberOfPages());

    }

    public void updatePage() {
        this.currentPage = this.pdf.getPageImage(this.currentPageNumber, this.scaleFactor);
        this.imageView.setImage(this.currentPage);
    }

    public void loadPage(int pageNumber) {
        if (pageNumber >= 0 && pageNumber <= this.pdf.getNumberOfPages())
            this.currentPageNumber = pageNumber;
        updatePage();
    }

    public void leftPage() {
        if (currentPageNumber >= 0 && currentPageNumber <= pdf.getNumberOfPages()) {
            currentPageNumber -= 1;
        }
        updatePage();
    }

    public void rightPage() {

        if (currentPageNumber >= 0 && currentPageNumber < pdf.getNumberOfPages()) {
            currentPageNumber += 1;
        }
        updatePage();
    }


    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
        updatePage();
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
}
