package eu.patrickgeiger.fxpdf.nodes.viewer;

import eu.patrickgeiger.fxpdf.event.Parameter;
import eu.patrickgeiger.fxpdf.event.ViewerActionEvent;
import eu.patrickgeiger.fxpdf.event.ViewerEvent;
import eu.patrickgeiger.fxpdf.event.ViewerEventHandler;
import eu.patrickgeiger.fxpdf.util.ImageTools;
import eu.patrickgeiger.fxpdf.util.PDF;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.DecimalFormat;

import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

/**
 * The MinimalViewer is the base viewer for other viewers
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

    public static final double DEFAULT_SCALE = 0.5;
    public static final double MAXSCALE = 1.0;
    public static final double MINSCALE = 0.1;
    public static final double SCALE_STEP = 0.1;
    @Getter
    private double scaleFactor = DEFAULT_SCALE;

    // Appearance
    @Getter
    private AppearanceType appearanceType = AppearanceType.DARK;
    @Getter
    @Setter
    private String customPathCSS;
    private static final String PATH_DARK_CSS = "css/viewer/minimalviewer/viewer-night.css";
    private static final String PATH_LIGHT_CSS = "css/viewer/minimalviewer/viewer.css";


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
     * The MinimalViewer constructor
     *
     * @param pdf The pdf that will be displayed
     */
    public MinimalViewer(@NonNull PDF pdf) {
        this.pdf = pdf;
        initialize();
        this.requestFocus();
    }

    /**
     * The MinimalViewer constructor for the MinimalViewerBuilder
     *
     * @param builder A MinimalViewerBuilder object
     */
    private MinimalViewer(@NonNull MinimalViewerBuilder builder) {
        this.pdf = builder.pdf;
        this.appearanceType = builder.appearanceType;
        if (this.customPathCSS != null) {
            this.customPathCSS = builder.customPath;
        }
        initialize();
    }

    /**
     * The MinimalViewerBuilder class
     */
    public static class MinimalViewerBuilder {
        private PDF pdf;
        private AppearanceType appearanceType;
        private String customPath;

        /**
         * The MinimalViewerBuilder constructor
         */
        public MinimalViewerBuilder() {
            this.pdf = null;
            this.appearanceType = AppearanceType.DARK;
            this.customPath = null;
        }

        /**
         * Set the pdf that will be displayed
         *
         * @param pdf The new pdf object
         * @return The MinimalViewerBuilder object
         */
        public MinimalViewerBuilder setPDF(@NonNull PDF pdf) {
            this.pdf = pdf;
            return this;
        }

        /**
         * Set the initial appearance type for the viewer
         *
         * @param appearanceType The appearance type
         * @return The MinimalViewerBuilder object
         */
        public MinimalViewerBuilder setAppearanceType(@NonNull AppearanceType appearanceType) {
            this.appearanceType = appearanceType;
            return this;
        }

        /**
         * Set the custom path for a custom theme
         *
         * @param custompath The custom path to a .css file
         * @return The MinimalViewerBuilder object
         */
        public MinimalViewerBuilder setCustomPath(@NonNull String custompath) {
            this.customPath = custompath;
            return this;
        }

        /**
         * Generate the object
         *
         * @return The MinimalViewer object
         */
        public MinimalViewer build() {
            return new MinimalViewer(this);
        }
    }

    /**
     * Initialize the Viewer
     */
    private void initialize() {
        // ImageView
        this.imageView = new ImageView();

        this.initStackPane();
        this.initScrollPane();

        this.setAppearanceType(this.appearanceType);

        // Own settings
        this.getChildren().add(this.scrollPane);
        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);
        this.widthProperty().addListener((observableValue, oldNumber, newNumber) -> this.scrollPane.setPrefWidth(newNumber.doubleValue()));
        this.heightProperty().addListener((observableValue, oldNumber, newNumber) -> this.scrollPane.setPrefHeight(newNumber.doubleValue()));

        // KeyBindings
        // KeyBinds
        this.keyCodeEnd = new KeyCodeCombination(KeyCode.RIGHT, CONTROL_DOWN);
        this.keyCodeBegin = new KeyCodeCombination(KeyCode.LEFT, CONTROL_DOWN);
        this.keyCodeLeft = KeyCode.LEFT;
        this.keyCodeRight = KeyCode.RIGHT;

        this.setEvents();

        this.fireEvent(new ViewerActionEvent(Parameter.RENDER));
    }

    private void initStackPane() {
        // StackPane to center the Image
        this.stackPane = new StackPane();
        this.stackPane.getStyleClass().add("stackpane");
        this.stackPane.setOnScroll(scrollEvent -> {
            if (scrollEvent.isControlDown()) {
                double before = scaleFactor;
                if (scrollEvent.getDeltaY() < 0 && scaleFactor > MINSCALE) {
                    // Zoom out
                    scaleFactor -= SCALE_STEP;
                } else if (scrollEvent.getDeltaY() > 0 && scaleFactor < MAXSCALE) {
                    // Zoom in
                    scaleFactor += SCALE_STEP;
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
    }

    private void initScrollPane() {
        // ScrollPane
        this.scrollPane = new ScrollPane();
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollPane.setFitToWidth(true);
        this.scrollPane.setFitToHeight(true);
        this.scrollPane.setPannable(true);
        this.scrollPane.setContent(this.stackPane);

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
        this.scrollPane.setOnSwipeLeft(swipeEvent -> this.fireEvent(new ViewerActionEvent(Parameter.LEFT)));

        this.scrollPane.setOnSwipeRight(swipeEvent -> this.fireEvent(new ViewerActionEvent(Parameter.LEFT)));

    }

    /**
     * Set all needed events to this node
     */
    private void setEvents() {
        this.addEventHandler(ViewerEvent.VIEWER_EVENT_TYPE, new ViewerEventHandler() {
            @Override
            public void onViewerEvent(Parameter parameter) {
                new Thread(() ->
                        Platform.runLater(() -> {
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
                                    eventRender();
                                    break;
                                default:
                                    LOGGER.debug("Wrong parameter is given");
                            }
                        })
                ).start();

            }
        });

        // Drag and Drop
        this.setOnDragOver(dragEvent -> {
            var db = dragEvent.getDragboard();
            boolean accepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".pdf");
            if (db.hasFiles() && accepted && db.getFiles().size() == 1) {
                dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            } else {
                dragEvent.consume();
            }
        });

        this.setOnDragDropped(dragEvent -> {
            var db = dragEvent.getDragboard();
            var success = false;
            if (db.hasFiles()) {
                success = true;
                var file = db.getFiles().get(0);
                new Thread(() ->
                        Platform.runLater(() -> {
                            try {
                                loadPDF(new PDF(file));
                            } catch (IOException e) {
                                LOGGER.error(e.getMessage());
                            }
                        })
                ).start();
            }
            dragEvent.setDropCompleted(success);
            dragEvent.consume();
        });
    }

    /**
     * Load a new pdf
     *
     * @param pdf The new pdf that will be displayed
     */
    public void loadPDF(@NonNull PDF pdf) {
        this.pdf = pdf;
        this.currentpage = 0;
        this.fireEvent(new ViewerActionEvent(Parameter.PDF_LOADED));
        this.fireEvent(new ViewerActionEvent(Parameter.RENDER));
    }

    /**
     * @param pageNumber
     */
    public void loadPage(@NonNull int pageNumber) {
        if (pageNumber >= 0 && pageNumber < this.pdf.getNumberOfPages()) {
            this.currentpage = pageNumber;
            this.fireEvent(new ViewerActionEvent(Parameter.RENDER));
        } else {
            throw new IllegalArgumentException("The page number given is outside that of the PDF.");
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
            var imgPage = this.pdf.getSwingImage(this.currentpage);
            imgPage = ImageTools.scaleBufferedImage(imgPage, (float) scaleFactor);
            this.imageView.setImage(ImageTools.convertToFXImage(imgPage));
        }
    }

    /**
     * Set the scale factor and trigger the render event
     *
     * @param scaleFactor the new scale factor
     */
    public void setScaleFactor(double scaleFactor) {
        if (scaleFactor >= MINSCALE && scaleFactor <= MAXSCALE) {
            this.scaleFactor = Double.parseDouble(new DecimalFormat("0.00").format(scaleFactor));
            this.fireEvent(new ViewerActionEvent(Parameter.RENDER));
        }
    }

    public void scaleByValue(double scaleFactor) {
        double newValue = this.scaleFactor + scaleFactor;
        newValue = Double.parseDouble(new DecimalFormat("0.00").format(newValue));
        if (newValue >= MINSCALE && newValue <= MAXSCALE) {
            this.scaleFactor = newValue;
            this.fireEvent(new ViewerActionEvent(Parameter.RENDER));
        }
    }

    /**
     * Set the appearance type of the eu.patrickgeiger.fxpdf.nodes.viewer
     *
     * @param appearanceType The appearance type of the eu.patrickgeiger.fxpdf.nodes.viewer to DARK, LIGHT or CUSTOM
     */
    public void setAppearanceType(@NonNull AppearanceType appearanceType) {
        this.appearanceType = appearanceType;
        switch (this.appearanceType) {
            case DARK:
                this.getStylesheets().remove(0, this.getStylesheets().size());
                this.getStylesheets().add(PATH_DARK_CSS);
                break;
            case LIGHT:
                this.getStylesheets().remove(0, this.getStylesheets().size());
                this.getStylesheets().add(PATH_LIGHT_CSS);
                break;
            case CUSTOM:
                LOGGER.info("Not implemented yet");
                this.getStylesheets().remove(0, this.getStylesheets().size());
                if (customPathCSS != null) {
                    this.getStylesheets().add(customPathCSS);
                } else {
                    LOGGER.error("The custom path is null");
                    throw new NullPointerException("The custom path is null");
                }
                break;
            default:
                LOGGER.warn("Not supported parameter is given.");
                break;
        }
        this.fireEvent(new ViewerActionEvent(Parameter.THEME_CHANGED));
    }
}
