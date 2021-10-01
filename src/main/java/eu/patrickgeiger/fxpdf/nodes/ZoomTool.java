package eu.patrickgeiger.fxpdf.nodes;

import eu.patrickgeiger.fxpdf.event.Parameter;
import eu.patrickgeiger.fxpdf.event.ViewerEvent;
import eu.patrickgeiger.fxpdf.event.ViewerEventHandler;
import eu.patrickgeiger.fxpdf.nodes.viewer.AppearanceType;
import eu.patrickgeiger.fxpdf.nodes.viewer.MinimalViewer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;

/**
 * The ZoomTool helps to control the zoom of viewer
 *
 * @author Patr1ick
 */
public class ZoomTool extends HBox {

    // Logger
    private static final Logger LOGGER = LogManager.getLogger(ZoomTool.class);

    private TextField textFieldZoom;

    private final MinimalViewer minimalViewer;

    // Appearance
    @Getter
    private AppearanceType appearanceType;
    @Getter
    @Setter
    private String customPathCSS;
    private static final String PATH_DARK_CSS = "css/nodes/zoomtool/zoomtool-night.css";
    private static final String PATH_LIGHT_CSS = "css/nodes/zoomtool/zoomtool.css";

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
        // Nodes
        var btnReset = new Button("Reset");
        btnReset.setOnAction(actionEvent -> this.minimalViewer.setScaleFactor(MinimalViewer.MINSCALE));

        var btnZoomIn = new Button("+");
        btnZoomIn.setOnAction(actionEvent -> {
            LOGGER.info(this.minimalViewer.getScaleFactor());
            if (this.minimalViewer.getScaleFactor() <= MinimalViewer.MAXSCALE) {
                this.minimalViewer.scaleByValue(MinimalViewer.SCALE_STEP);
            }
        });

        var btnZoomOut = new Button("-");
        btnZoomOut.setOnAction(actionEvent -> {
            if (this.minimalViewer.getScaleFactor() > MinimalViewer.MINSCALE) {
                this.minimalViewer.scaleByValue(MinimalViewer.SCALE_STEP * -1);
            }
        });

        // TextField
        this.textFieldZoom = new TextField();
        this.textFieldZoom.setText(new DecimalFormat("0").format(this.minimalViewer.getScaleFactor() * 100));
        this.textFieldZoom.setPrefWidth(50d);
        this.textFieldZoom.setOnAction(actionEvent -> {
            double value = Double.parseDouble(this.textFieldZoom.getText()) / 100;
            if (value >= MinimalViewer.MINSCALE && value <= MinimalViewer.MAXSCALE) {
                this.minimalViewer.setScaleFactor(value);
            }
        });

        // EventHandler
        this.minimalViewer.addEventHandler(ViewerEvent.VIEWER_EVENT_TYPE, new ViewerEventHandler() {
            @Override
            public void onViewerEvent(Parameter parameter) {
                switch (parameter) {
                    case RENDER:
                        textFieldZoom.setText(new DecimalFormat("0").format(minimalViewer.getScaleFactor() * 100));
                        break;
                    case THEME_CHANGED:
                        setAppearanceType(minimalViewer.getAppearanceType());
                        break;
                    default:
                        break;
                }
            }
        });

        // Add nodes
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10d);
        this.getChildren().addAll(btnZoomOut, this.textFieldZoom, btnZoomIn, btnReset);
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
            case CUSTOM:
                this.getStylesheets().remove(0, this.getStylesheets().size());
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
    }
}