package eu.patrickgeiger.fxpdf.viewer;

import eu.patrickgeiger.fxpdf.event.Parameter;
import eu.patrickgeiger.fxpdf.event.ViewerEvent;
import eu.patrickgeiger.fxpdf.event.ViewerEventHandler;
import eu.patrickgeiger.fxpdf.nodes.PDFContent;
import eu.patrickgeiger.fxpdf.nodes.PageChooser;
import eu.patrickgeiger.fxpdf.nodes.ZoomTool;
import eu.patrickgeiger.fxpdf.util.PDF;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * The SampleViewer is an example how to integrate all nodes of the library
 *
 * @author Patr1ick
 */
public class SampleViewer extends BorderPane {

    // Logger
    private static final Logger LOGGER = LogManager.getLogger(SampleViewer.class);

    // Nodes
    private SplitPane splitPane;
    private HBox toolBar;

    private Separator separator;

    private PageChooser pageChooser;
    private ZoomTool zoomTool;
    private PDFContent pdfContent;

    // Viewer
    @Getter
    private MinimalViewer minimalViewer;
    @Getter
    private PDF pdf;

    // Appearance
    @Getter
    private AppearanceType appearanceType;
    @Getter
    @Setter
    private String custom_path_css;
    private final String PATH_DARK_CSS = "css/viewer/sampleviewer/sampleviewer-night.css";
    private final String PATH_LIGHT_CSS = "css/viewer/sampleviewer/sampleviewer.css";

    /**
     * The SampleViewer constructor
     *
     * @param pdf The pdf that will be displayed
     */
    public SampleViewer(@NonNull PDF pdf) throws IOException {
        this.pdf = pdf;
        initialize();
    }

    /**
     * Initialize the viewer
     */
    private void initialize() throws IOException {
        // MinimalViewer
        this.minimalViewer = new MinimalViewer(this.pdf);
        this.minimalViewer.addEventHandler(ViewerEvent.VIEWER_EVENT_TYPE, new ViewerEventHandler() {
            @Override
            public void onViewerEvent(@NonNull Parameter parameter) {
                if (parameter == Parameter.THEME_CHANGED) {
                    setAppearanceType(minimalViewer.getAppearanceType());
                }
            }
        });

        // Other Nodes
        this.pageChooser = new PageChooser(this.minimalViewer);

        this.zoomTool = new ZoomTool(this.minimalViewer);

        this.pdfContent = new PDFContent(this.minimalViewer);

        this.separator = new Separator();
        this.separator.setOrientation(Orientation.VERTICAL);
        this.separator.setMaxHeight(30d);

        // Panes
        this.splitPane = new SplitPane();
        this.splitPane.setOrientation(Orientation.HORIZONTAL);
        this.splitPane.getItems().addAll(this.pdfContent, this.minimalViewer);
        Platform.runLater(() -> {
            splitPane.setDividerPositions(0.2f);
        });

        this.toolBar = new HBox();
        // Set css, bc empty by default
        this.toolBar.getStyleClass().add("toolbar");
        this.toolBar.setPrefHeight(75d);
        this.toolBar.setSpacing(20d);
        this.toolBar.setAlignment(Pos.CENTER);
        this.toolBar.getChildren().addAll(this.pageChooser, this.separator, this.zoomTool);

        // Own settings
        this.getStyleClass().add("sample-viewer");
        // Set nodes
        this.setTop(this.toolBar);
        this.setCenter(this.splitPane);
    }

    /**
     * Load a new pdf
     *
     * @param pdf The pdf that will be displayed
     */
    public void loadPDF(@NonNull PDF pdf) {
        this.pdf = pdf;
        this.minimalViewer.loadPDF(this.pdf);
    }

    /**
     * Set the appearance type of this node
     *
     * @param appearanceType The appearance type
     */
    private void setAppearanceType(@NonNull AppearanceType appearanceType) {
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

    /**
     * Switch the appearance type of this node
     *
     * @param appearanceType The new appearance type
     */
    public void switchTheme(@NonNull AppearanceType appearanceType) {
        this.minimalViewer.setAppearanceType(appearanceType);
    }

}
