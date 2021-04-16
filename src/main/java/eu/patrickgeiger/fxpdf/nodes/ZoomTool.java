package eu.patrickgeiger.fxpdf.nodes;


import eu.patrickgeiger.fxpdf.event.Parameter;
import eu.patrickgeiger.fxpdf.event.ViewerEvent;
import eu.patrickgeiger.fxpdf.event.ViewerEventHandler;
import eu.patrickgeiger.fxpdf.viewer.AppearanceType;
import eu.patrickgeiger.fxpdf.viewer.MinimalViewer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The ZoomTool helps to control the zoom of viewer
 *
 * @author Patr1ick
 */
public class ZoomTool extends HBox {

    // Logger
    private static final Logger LOGGER = LogManager.getLogger(MinimalViewer.class);

    // Nodes
    private Button btnReset;
    private Button btnZoomIn;
    private Button btnZoomOut;

    private TextField textFieldZoom;

    private final MinimalViewer minimalViewer;

    // Appearance
    @Getter
    private AppearanceType appearanceType;
    @Getter
    @Setter
    private String custom_path_css;
    private final String PATH_DARK_CSS = "css/nodes/zoomtool/zoomtool-night.css";
    private final String PATH_LIGHT_CSS = "css/nodes/zoomtool/zoomtool.css";

    /**
     * The ZoomTool constructor
     *
     * @param minimalViewer The minimal viewer that the ZoomTool should listen to
     */
    public ZoomTool(@NonNull MinimalViewer minimalViewer) {
        this.minimalViewer = minimalViewer;
        initialize();
    }

    /**
     * Initialize the zoom tool
     */
    private void initialize() {
        // Buttons
        this.btnReset = new Button("Reset");
        this.btnReset.setOnAction(actionEvent -> {
            this.minimalViewer.setScaleFactor(this.minimalViewer.getMIN_SCALE());
        });

        this.btnZoomIn = new Button("+");
        this.btnZoomIn.setOnAction(actionEvent -> {
            LOGGER.info(this.minimalViewer.getScaleFactor());
            if (this.minimalViewer.getScaleFactor() <= this.minimalViewer.getMAX_SCALE()) {
                this.minimalViewer.setScaleFactor(this.minimalViewer.getScaleFactor() + 1.0f);
            }
        });

        this.btnZoomOut = new Button("-");
        this.btnZoomOut.setOnAction(actionEvent -> {
            if (this.minimalViewer.getScaleFactor() > this.minimalViewer.getMIN_SCALE()) {
                this.minimalViewer.setScaleFactor(this.minimalViewer.getScaleFactor() - 1.0f);
            }
        });

        // TextField
        this.textFieldZoom = new TextField();
        this.textFieldZoom.setText(Float.toString(this.minimalViewer.getScaleFactor() * 10));
        this.textFieldZoom.setPrefWidth(50d);
        this.textFieldZoom.setOnAction(actionEvent -> {
            float value = Float.parseFloat(this.textFieldZoom.getText()) / 10;
            if (value >= this.minimalViewer.getMIN_SCALE() && value <= this.minimalViewer.getMAX_SCALE()) {
                this.minimalViewer.setScaleFactor(value);
            }
        });

        // EventHandler
        this.minimalViewer.addEventHandler(ViewerEvent.VIEWER_EVENT_TYPE, new ViewerEventHandler() {
            @Override
            public void onViewerEvent(Parameter parameter) {
                switch (parameter) {
                    case RENDER:
                        textFieldZoom.setText(Float.toString(minimalViewer.getScaleFactor() * 10));
                        break;
                    case THEME_CHANGED:
                        setAppearanceType(minimalViewer.getAppearanceType());
                        break;
                }
            }
        });

        // Add nodes
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10d);
        this.getChildren().addAll(this.btnZoomOut, this.textFieldZoom, this.btnZoomIn, this.btnReset);
    }

    /**
     * Set the appearance type of this node
     *
     * @param appearanceType The appearance type
     */
    public void setAppearanceType(@NonNull AppearanceType appearanceType) {
        this.appearanceType = appearanceType;
        switch (this.appearanceType) {
            case LIGHT:
                this.getStylesheets().remove(0, this.getStylesheets().size());
                this.getStylesheets().add(PATH_LIGHT_CSS);
                break;
            case DARK:
                this.getStylesheets().remove(0, this.getStylesheets().size());
                this.getStylesheets().add(PATH_DARK_CSS);
                break;
            case Custom:
                this.getStylesheets().remove(0, this.getStylesheets().size());
                this.getStylesheets().remove(0, this.getStylesheets().size());
                if (custom_path_css != null) {
                    this.getStylesheets().add(custom_path_css);
                } else {
                    LOGGER.error("The custom path is null");
                    throw new NullPointerException("The custom path is null");
                }
                break;
        }
    }
}