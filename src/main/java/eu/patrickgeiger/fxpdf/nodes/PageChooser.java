package eu.patrickgeiger.fxpdf.nodes;

import eu.patrickgeiger.fxpdf.event.Parameter;
import eu.patrickgeiger.fxpdf.event.ViewerEvent;
import eu.patrickgeiger.fxpdf.event.ViewerEventHandler;
import eu.patrickgeiger.fxpdf.nodes.viewer.AppearanceType;
import eu.patrickgeiger.fxpdf.nodes.viewer.MinimalViewer;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The PageChooser views the current page number and navigate to a specific page
 *
 * @author Patr1ick
 */
public class PageChooser extends HBox {

    //Logger
    private static final Logger LOGGER = LogManager.getLogger(PageChooser.class);

    // Nodes
    private Label label;
    private TextField textField;

    private final MinimalViewer viewer;

    // Appearance
    @Getter
    private AppearanceType appearanceType;
    @Getter
    @Setter
    private String customPathCSS;
    private static final String PATH_DARK_CSS = "css/nodes/pagechooser/pagechooser-night.css";
    private static final String PATH_LIGHT_CSS = "css/nodes/pagechooser/pagechooser.css";

    /**
     * The PageChooser constructor
     *
     * @param viewer The minimum viewer that the PageChooser should listen to
     */
    public PageChooser(@NonNull MinimalViewer viewer) {
        this.viewer = viewer;
        initialize();
    }

    /**
     * Initialize the PageChooser node
     */
    private void initialize() {
        this.viewer.addEventHandler(ViewerEvent.VIEWER_EVENT_TYPE, new ViewerEventHandler() {
            @Override
            public void onViewerEvent(Parameter parameter) {
                switch (parameter) {
                    case RENDER:
                        textField.setText(Integer.toString(viewer.getCurrentpage() + 1));
                        break;
                    case THEME_CHANGED:
                        setAppearanceType(viewer.getAppearanceType());
                        break;
                    case PDF_LOADED:
                        textField.setText(Integer.toString(viewer.getCurrentpage() + 1));
                        label.setText(" / " + viewer.getPdf().getNumberOfPages());
                        break;
                    default:
                        break;
                }
            }
        });

        //Label
        this.label = new Label(" / " + this.viewer.getPdf().getNumberOfPages());

        //TextField
        this.textField = new TextField();
        this.textField.setText(Integer.toString(this.viewer.getCurrentpage() + 1));
        this.textField.setPrefSize(40d, 5d);
        this.textField.setOnAction(event -> viewer.loadPage(Integer.parseInt(this.textField.getText()) - 1));

        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(this.textField, this.label);
    }

    /**
     * Set the appearance type of this node
     *
     * @param appearanceType The new appearance type
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
