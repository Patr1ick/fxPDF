package viewer;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import util.PDF;
import viewer.event.CustomEvent;
import viewer.event.PageSwitchEventHandler;
import viewer.nodes.PagePreview;

public class PDFViewer extends BorderPane {

    //Panes
    private SplitPane splitPane;
    private VBox toolbar;


    //Nodes
    private Viewer viewer;
    private Label name;
    private Label pageNumber;
    private PagePreview pagePreview;

    //Menubar
    private MenuBar menuBar;

    private Menu file;
    private Menu control;

    private MenuItem menuItemLoadPDF;
    private MenuItem menuItemClose;

    private MenuItem menuItemFullscreen;

    //Stage
    private Stage stage;

    //
    private boolean menuBarEnable = false;

    public PDFViewer(Stage stage, PDF pdf) {
        if (stage != null && pdf != null) {
            this.stage = stage;
            //this
            this.getStylesheets().add("resource/css/style.css");
            //Nodes
            this.viewer = new Viewer(pdf);
            this.viewer.addEventHandler(CustomEvent.CUSTOM_EVENT_TYPE, new PageSwitchEventHandler() {
                @Override
                public void onPageSwitch(String param) {
                    pageNumber.setText(viewer.getCurrentPageNumber() + 1 + " / " + viewer.getPdf().getNumberOfPages());
                }
            });

            this.name = new Label();
            this.name.setText("Path: " + pdf.getAbsolutePath());
            this.name.getStyleClass().add("name");

            this.pageNumber = new Label();
            this.pageNumber.getStyleClass().add("pageNumber");
            this.pageNumber.setText(this.viewer.getCurrentPageNumber() + 1 + " / " + this.viewer.getPdf().getNumberOfPages());

            this.pagePreview = new PagePreview(pdf, this.viewer);

            //Panes
            this.splitPane = new SplitPane();
            this.splitPane.setOrientation(Orientation.HORIZONTAL);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    splitPane.setDividerPositions(0.05f);
                }
            });
            this.splitPane.getItems().addAll(this.pagePreview, this.viewer);

            this.toolbar = new VBox();

            //MenuBar
            this.menuBar = new MenuBar();

            this.file = new Menu("File");
            this.control = new Menu("Controls");

            this.menuItemLoadPDF = new MenuItem("Load PDF");
            this.menuItemLoadPDF.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
            this.menuItemLoadPDF.setOnAction(event -> {
                FileChooser f = new FileChooser();
                f.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
                File selected = f.showOpenDialog(stage);
                if (selected != null) {
                    PDF selectedPDF = new PDF(selected);
                    loadPDF(selectedPDF);
                } else {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setHeaderText("Please choose a .pdf file!");
                    a.setTitle("No file selected.");
                    a.showAndWait();
                }
            });

            this.menuItemClose = new MenuItem("Close Window");
            this.menuItemClose.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
            this.menuItemClose.setOnAction(event -> {
                Platform.exit();
                System.exit(0);
            });

            this.menuItemFullscreen = new MenuItem("Fullscreen");
            this.menuItemFullscreen.setAccelerator(new KeyCodeCombination(KeyCode.F11));
            this.menuItemFullscreen.setOnAction(event -> {
                if (this.stage.isFullScreen()) {
                    this.stage.setFullScreen(false);
                } else {
                    this.stage.setFullScreen(true);
                }
            });

            this.file.getItems().addAll(this.menuItemLoadPDF, this.menuItemClose);
            this.control.getItems().addAll(this.menuItemFullscreen);

            if (this.viewer.getViewerType() == ViewerType.IMAGE) {
                MenuItem menuItemLeftPage = new MenuItem("Left Page");
                menuItemLeftPage.setAccelerator(new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN));
                menuItemLeftPage.setOnAction(event -> {
                    this.viewer.leftPage();
                });
                MenuItem menuItemRightPage = new MenuItem("Left Page");
                menuItemRightPage.setAccelerator(new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN));
                menuItemRightPage.setOnAction(event -> {
                    this.viewer.rightPage();
                });

                this.control.getItems().addAll(new SeparatorMenuItem(), menuItemLeftPage, menuItemRightPage);
            }

            this.menuBar.getMenus().addAll(this.file, this.control);

            this.toolbar.getChildren().addAll(this.menuBar, this.name, this.pageNumber);
            this.setTop(this.toolbar);
            this.setCenter(this.splitPane);
        } else {
            throw new NullPointerException("The parameter pdf and stage is null. Please check this parameter in the constructor.");
        }
    }

    public void setMenuBarEnable(boolean menuBarEnable) {
        this.menuBarEnable = menuBarEnable;

        if (!menuBarEnable)
            this.toolbar.getChildren().remove(this.menuBar);
        else if (this.toolbar.getChildren().get(0) != this.menuBar)
            this.toolbar.getChildren().add(0, this.menuBar);
    }


    public void loadPDF(PDF pdf) {
        this.viewer.loadPDF(pdf);
        this.pagePreview.loadPDF(pdf);
        this.name.setText("Path: " + pdf.getAbsolutePath());
        this.pageNumber.setText(this.viewer.getCurrentPageNumber() + " / " + this.viewer.getPdf().getNumberOfPages());
    }
}