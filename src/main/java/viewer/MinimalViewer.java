package viewer;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.PDF;
import viewer.event.Parameter;
import viewer.event.ViewerActionEvent;
import viewer.event.ViewerEvent;
import viewer.event.ViewerEventHandler;

import java.io.File;

/**
 * The MinimalViewer is the base viewer
 *
 * @author Patr1ick
 */
public class MinimalViewer extends Pane {

    private static final Logger LOGGER = LogManager.getLogger(MinimalViewer.class);

    // Nodes
    private ScrollPane scrollPane;
    private StackPane stackPane;
    private ImageView imageView;

    // MinimalViewer
    @Getter
    private PDF pdf;

    @Getter
    private int currentpage;

    @Getter
    private float scaleFactor = 1.0f;
    @Getter
    private final float MAX_SCALE = 10.0f;
    @Getter
    private final float MIN_SCALE = 1.0f;

    // Appearance
    @Getter
    private AppearanceType appearanceType;
    @Getter
    @Setter
    private String custom_path_css;
    private final String PATH_DARK_CSS = "css/viewer/minimalviewer/viewer-night.css";
    private final String PATH_LIGHT_CSS = "css/viewer/minimalviewer/viewer.css";


    // KeyBinding
    @Setter
    @Getter
    private KeyCodeCombination keyCodeBegin;
    @Setter
    @Getter
    private KeyCodeCombination keyCodeEnd;

    @Setter
    @Getter
    private KeyCode keyCodeLeft;
    @Setter
    @Getter
    private KeyCode keyCodeRight;

    // Other
    @Getter
    @Setter
    private double scrollspeed = 3.0d;

    /**
     * @param pdf The pdf that will be displayed
     */
    public MinimalViewer(@NonNull PDF pdf) {
        this.pdf = pdf;
        initialize();
        this.requestFocus();
    }

    /**
     * Initialize the Viewer
     */
    private void initialize() {
        // ImageView
        this.imageView = new ImageView();

        // StackPane to center the Image
        this.stackPane = new StackPane();
        this.stackPane.getStyleClass().add("stackpane");
        this.stackPane.setOnScroll(scrollEvent -> {
            if (scrollEvent.isControlDown()) {
                float before = scaleFactor;
                if (scrollEvent.getDeltaY() < 0 && scaleFactor > 1.0f) {
                    scaleFactor -= 1.0f;
                } else if (scrollEvent.getDeltaY() > 0 && scaleFactor <= MAX_SCALE) {
                    scaleFactor += 1.0f;
                }
                if (before != scaleFactor) {
                    this.fireEvent(new ViewerActionEvent(Parameter.RENDER));
                }
            } else {
                double deltaY = scrollEvent.getDeltaY() * scrollspeed;
                double height = this.scrollPane.getContent().getBoundsInLocal().getHeight();
                this.scrollPane.setVvalue(this.scrollPane.getVvalue() + (-deltaY / height));
            }
        });
        this.stackPane.getChildren().add(this.imageView);
        StackPane.setAlignment(this.imageView, Pos.CENTER);

        // ScrollPane
        this.scrollPane = new ScrollPane();
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollPane.setPannable(true);
        this.scrollPane.setContent(this.stackPane);

        this.appearanceType = AppearanceType.DARK;
        setAppearanceType(this.appearanceType);
        this.getChildren().add(this.scrollPane);

        // Own settings
        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);
        this.widthProperty().addListener((observableValue, oldNumber, newNumber) -> {
            this.scrollPane.setPrefWidth(newNumber.doubleValue());
            this.stackPane.setPrefWidth(newNumber.doubleValue());
        });
        this.heightProperty().addListener((observableValue, oldNumber, newNumber) -> {
            this.scrollPane.setPrefHeight(newNumber.doubleValue());
            this.stackPane.setPrefHeight(newNumber.doubleValue());
        });

        // KeyBinds
        this.keyCodeEnd = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);
        this.keyCodeBegin = new KeyCodeCombination(KeyCode.LEFT, KeyCodeCombination.CONTROL_DOWN);
        this.keyCodeLeft = KeyCode.LEFT;
        this.keyCodeRight = KeyCode.RIGHT;

        this.scrollPane.setOnKeyPressed(keyEvent -> {
            LOGGER.info(keyEvent.getCode());
            if (keyCodeBegin.match(keyEvent)) {
                begin();
            }
            if (keyCodeEnd.match(keyEvent)) {
                end();
            }
            if (keyCodeLeft.equals(keyEvent.getCode())) {
                leftPage();
            }
            if (keyCodeRight.equals(keyEvent.getCode())) {
                rightPage();
            }
        });

        // Scrolling
        this.scrollPane.setOnScroll(scrollEvent -> {
            // Check if the "border" is reached
            if (this.scrollPane.getVvalue() == this.scrollPane.getVmax()) {
                this.fireEvent(new ViewerActionEvent(Parameter.RIGHT));
            }
            if (this.scrollPane.getVvalue() == this.scrollPane.getVmin()) {
                this.fireEvent(new ViewerActionEvent(Parameter.LEFT));
            }
        });

        // Not tested!
        this.scrollPane.setOnSwipeLeft(swipeEvent -> {
            this.fireEvent(new ViewerActionEvent(Parameter.LEFT));
        });

        this.scrollPane.setOnSwipeRight(swipeEvent -> {
            this.fireEvent(new ViewerActionEvent(Parameter.LEFT));
        });

        // EventHandler
        this.addEventHandler(ViewerEvent.VIEWER_EVENT_TYPE, new ViewerEventHandler() {
            @Override
            public void onViewerEvent(Parameter parameter) {
                LOGGER.info(parameter);
                switch (parameter) {
                    case LEFT:
                        eventLeftPage();
                        break;
                    case RIGHT:
                        eventRightPage();
                        break;
                    case BEGIN:
                        eventBegin();
                        break;
                    case END:
                        eventEnd();
                        break;
                    case RENDER:
                        new Thread(() -> {
                            Platform.runLater(() -> {
                                eventRender();
                            });
                        }).start();
                        break;
                    default:
                        LOGGER.debug("Wrong parameter is given");
                }
            }
        });

        // Drag and Drop
        this.setOnDragOver(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            boolean accepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".pdf");
            if (db.hasFiles() && accepted && db.getFiles().size() == 1) {
                dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            } else {
                dragEvent.consume();
            }
        });

        this.setOnDragDropped(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                File file = db.getFiles().get(0);
                new Thread(() -> {
                    Platform.runLater(() -> {
                        loadPDF(new PDF(file));
                    });
                }).start();
            }
            dragEvent.setDropCompleted(success);
            dragEvent.consume();
        });


        this.fireEvent(new ViewerActionEvent(Parameter.RENDER));
    }

    /**
     * @param pdf The new pdf that will be displayed
     */
    public void loadPDF(@NonNull PDF pdf) {
        this.pdf = pdf;
        this.currentpage = 0;
        this.fireEvent(new ViewerActionEvent(Parameter.PDF_LOADED));
        this.fireEvent(new ViewerActionEvent(Parameter.RENDER));
    }

    public void loadPage(int pageNumber) {
        if (pageNumber >= 0 && pageNumber < this.pdf.getNumberOfPages()) {
            this.currentpage = pageNumber;
            this.fireEvent(new ViewerActionEvent(Parameter.RENDER));
        }
    }

    /**
     * Switches to the previous page
     */
    public void leftPage() {
        this.fireEvent(new ViewerActionEvent(Parameter.LEFT));
    }

    /**
     * Switches to the next page
     */
    public void rightPage() {
        this.fireEvent(new ViewerActionEvent(Parameter.RIGHT));
    }

    /**
     * Got to the first page
     */
    public void begin() {
        this.fireEvent(new ViewerActionEvent(Parameter.BEGIN));
    }

    /**
     * Go to the last page
     */
    public void end() {
        this.fireEvent(new ViewerActionEvent(Parameter.END));
    }

    /**
     * Private method to switch page
     */
    private void eventLeftPage() {
        if (this.currentpage > 0) {
            this.currentpage--;
            this.fireEvent(new ViewerActionEvent(Parameter.RENDER));
            this.scrollPane.setVvalue(this.scrollPane.getVmax() - 0.001);
        }
    }

    /**
     * Private method to switch page
     */
    private void eventRightPage() {
        if (this.currentpage < this.pdf.getNumberOfPages() - 1) {
            this.currentpage++;
            this.fireEvent(new ViewerActionEvent(Parameter.RENDER));
            this.scrollPane.setVvalue(this.scrollPane.getVmin() + 0.001);
        }
    }

    /**
     * Private method to switch page
     */
    private void eventBegin() {
        this.currentpage = 0;
        this.fireEvent(new ViewerActionEvent(Parameter.RENDER));
        this.scrollPane.setVvalue(this.scrollPane.getVmin());
    }

    /**
     * Private method to switch page
     */
    private void eventEnd() {
        this.currentpage = this.pdf.getNumberOfPages() - 1;
        this.fireEvent(new ViewerActionEvent(Parameter.RENDER));
        this.scrollPane.setVvalue(this.scrollPane.getVmax());
    }

    /**
     * Private Method to load the page
     */
    private void eventRender() {
        if (this.currentpage >= 0 && this.currentpage < this.pdf.getNumberOfPages()) {
            Image imgPage = this.pdf.getFxImage(this.currentpage, scaleFactor);
            this.imageView.setFitWidth(imgPage.getWidth());
            this.imageView.setFitHeight(imgPage.getHeight());
            this.imageView.setImage(imgPage);
        }

    }

    /**
     * @param scaleFactor the new scale factor
     */
    public void setScaleFactor(@NonNull float scaleFactor) {
        if (scaleFactor >= MIN_SCALE && scaleFactor <= MAX_SCALE) {
            this.scaleFactor = scaleFactor;
            this.fireEvent(new ViewerActionEvent(Parameter.RENDER));
        }
    }

    /**
     * Set the appearance type of the viewer
     *
     * @param appearanceType The appearance type of the viewer to DARK, LIGHT or CUSTOM
     */
    public void setAppearanceType(@NonNull AppearanceType appearanceType) {
        this.appearanceType = appearanceType;
        switch (this.appearanceType) {
            case DARK:
                this.getStylesheets().remove(0, this.getStylesheets().size());
                this.getStylesheets().add(this.PATH_DARK_CSS);
                break;
            case LIGHT:
                this.getStylesheets().remove(0, this.getStylesheets().size());
                this.getStylesheets().add(this.PATH_LIGHT_CSS);
                break;
            case Custom:
                LOGGER.info("Not implemented yet");
                this.getStylesheets().remove(0, this.getStylesheets().size());
                if (custom_path_css != null) {
                    this.getStylesheets().add(custom_path_css);
                } else {
                    LOGGER.error("The custom path is null");
                    throw new NullPointerException("The custom path is null");
                }
                break;
        }
        this.fireEvent(new ViewerActionEvent(Parameter.THEME_CHANGED));
    }
}
