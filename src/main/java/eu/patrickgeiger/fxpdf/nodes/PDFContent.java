package eu.patrickgeiger.fxpdf.nodes;

import eu.patrickgeiger.fxpdf.event.Parameter;
import eu.patrickgeiger.fxpdf.event.ViewerEvent;
import eu.patrickgeiger.fxpdf.event.ViewerEventHandler;
import eu.patrickgeiger.fxpdf.nodes.viewer.AppearanceType;
import eu.patrickgeiger.fxpdf.nodes.viewer.MinimalViewer;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The PDFContent Node views all chapter and you can navigate to them
 *
 * @author Patr1ick
 */
public class PDFContent extends Pane {

    //Logger
    private static final Logger LOGGER = LogManager.getLogger(PDFContent.class);

    private final MinimalViewer minimalViewer;

    // Tree
    private TreeView<Hyperlink> treeView;
    private ArrayList<TreeItem<Hyperlink>> contents;

    // Appearance
    @Getter
    private AppearanceType appearanceType;
    @Getter
    @Setter
    private String customPathCSS;
    private static final String PATH_DARK_CSS = "css/nodes/pdfcontent/pdfcontent-night.css";
    private static final String PATH_LIGHT_CSS = "css/nodes/pdfcontent/pdfcontent.css";

    /**
     * The PDFContent constructor
     *
     * @param minimalViewer The MinimalViewer object that will the PDFContent listen to
     * @throws IOException Can throw an IO Exception
     */
    public PDFContent(@NonNull MinimalViewer minimalViewer) throws IOException {
        this.minimalViewer = minimalViewer;
        initialize();
    }

    /**
     * Initialize the PDFContent object
     *
     * @throws IOException Can throw an IO Exception
     */
    private void initialize() throws IOException {
        // The TreeView
        this.treeView = new TreeView<>();
        this.treeView.setEditable(false);
        this.treeView.setShowRoot(false);
        // EventHandler
        this.minimalViewer.addEventHandler(ViewerEvent.VIEWER_EVENT_TYPE, new ViewerEventHandler() {
            @Override
            public void onViewerEvent(@NonNull Parameter parameter) {
                switch (parameter) {
                    case THEME_CHANGED:
                        setAppearanceType(minimalViewer.getAppearanceType());
                        break;
                    case PDF_LOADED:
                        try {
                            createTreeView();
                        } catch (IOException e) {
                            LOGGER.error(e.getMessage());
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        // This
        this.getStyleClass().add("pdfcontent");
        this.widthProperty().addListener((observableValue, oldNumber, newNumber) -> this.treeView.setPrefWidth(newNumber.doubleValue()));
        this.heightProperty().addListener((observableValue, oldNumber, newNumber) -> this.treeView.setPrefHeight(newNumber.doubleValue()));
        this.getChildren().add(this.treeView);
        // Generate TreeView
        createTreeView();
    }

    /**
     * Creates the TreeView for the loaded pdf
     *
     * @throws IOException Can throw an IO Exception
     */
    private void createTreeView() throws IOException {
        this.contents = new ArrayList<>();
        PDDocumentOutline outline = this.minimalViewer.getPdf().getDocument().getDocumentCatalog().getDocumentOutline();
        if (outline != null) {
            generateTree(outline, null);
        }
        // Add all items to the root element and add it to the TreeView
        // Root Item
        TreeItem<Hyperlink> root = new TreeItem<>(new Hyperlink());
        for (TreeItem<Hyperlink> treeItem : this.contents) {
            root.getChildren().add(treeItem);
        }
        this.treeView.setRoot(root);
    }

    /**
     * This will generate the Tree recursively
     *
     * @param outline An PDOutlineNode
     * @param parent  The parent TreeItem
     * @throws IOException Can throw an IO Exception
     */
    private void generateTree(PDOutlineNode outline, TreeItem<Hyperlink> parent) throws IOException {
        PDOutlineItem currentItem = outline.getFirstChild();
        while (currentItem != null) {
            var pageNumber = 1;
            PDPageTree pages = this.minimalViewer.getPdf().getDocument().getDocumentCatalog().getPages();
            for (PDPage page : pages) {
                if (page.equals(currentItem.findDestinationPage(this.minimalViewer.getPdf().getDocument()))) {
                    break;
                }
                pageNumber++;
            }
            // Create Hyperlink and TreeItem
            var hyperlink = new Hyperlink();
            hyperlink.setText(currentItem.getTitle());
            int finalNumber = pageNumber - 1;
            hyperlink.setOnAction(actionEvent -> minimalViewer.loadPage(finalNumber));
            TreeItem<Hyperlink> treeItem = new TreeItem<>();
            treeItem.setExpanded(true);
            treeItem.setValue(hyperlink);

            // Set to parent
            if (parent == null) {
                this.contents.add(treeItem);
            } else {
                parent.getChildren().add(treeItem);
            }
            // Recursive call
            generateTree(currentItem, treeItem);
            currentItem = currentItem.getNextSibling();
        }
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
