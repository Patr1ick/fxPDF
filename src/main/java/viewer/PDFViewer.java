package viewer;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.PDF;
import viewer.nodes.PageChooser;
import viewer.nodes.PagePreview;

import java.io.File;

public class PDFViewer extends BorderPane {

    //Panes
    private SplitPane splitPane;
    private VBox toolbar;

    //Nodes
    private Viewer viewer;
    private PagePreview pagePreview;

    private Label name;
    private PageChooser pageChooser;

    //Menubar
    private MenuBar menuBar;

    private Menu file;
    private Menu control;

    private MenuItem menuItemLoadPDF;
    private MenuItem menuItemBlankText;
    private MenuItem menuItemClose;

    private MenuItem menuItemFullscreen;

    //Stage
    private Stage stage;

    // Other
    private boolean menuBarEnable = false;

    private PDF pdf;

    private ApperanceType apperanceType;
    private String path;

    public PDFViewer(Stage stage, PDF pdf) {
        if (stage != null || pdf != null) {
            this.pdf = pdf;
            //Stage
            this.stage = stage;
            this.stage.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    splitPane.setDividerPositions(0.1f);
                }
            });
            apperanceType = ApperanceType.DARK;
            path = null;
            init();
        } else {
            throw new NullPointerException("The parameter pdf or stage is null. Please check this parameter in the constructor.");
        }
    }

    public PDFViewer(Stage stage, PDF pdf, ApperanceType apperanceType, String path) {
        if (stage != null || pdf != null) {
            this.pdf = pdf;
            //Stage
            this.stage = stage;
            this.stage.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    splitPane.setDividerPositions(0.1f);
                }
            });
            this.apperanceType = apperanceType;
            this.path = path;
            init();
        } else {
            throw new NullPointerException("The parameter pdf or stage is null. Please check this parameter in the constructor.");
        }
    }

    public PDFViewer(PDFViewerBuilder builder) {
        if (builder.stage != null || builder.pdf != null) {
            this.pdf = builder.pdf;
            //Stage
            this.stage = builder.stage;
            this.stage.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    splitPane.setDividerPositions(0.1f);
                }
            });
            this.apperanceType = builder.apperanceType;
            this.path = builder.path;
            init();
        } else {
            throw new NullPointerException("The parameter pdf or stage is null.");
        }
    }

    public static class PDFViewerBuilder {
        private Stage stage;
        private PDF pdf;
        private ApperanceType apperanceType;
        private String path;

        public PDFViewerBuilder() {
            this.stage = null;
            this.pdf = null;
            this.apperanceType = ApperanceType.LIGHT;
            this.path = null;
        }

        public PDFViewerBuilder setStage(Stage stage) {
            this.stage = stage;
            return this;
        }

        public PDFViewerBuilder setPDF(PDF pdf) {
            this.pdf = pdf;
            return this;
        }

        public PDFViewerBuilder setApperanceType(ApperanceType apperanceType) {
            this.apperanceType = apperanceType;
            return this;
        }

        public PDFViewerBuilder setPath(String path) {
            this.path = path;
            return this;
        }

        public PDFViewer build() {
            return new PDFViewer(this);
        }
    }

    private void init() {
        //Viewer
        this.viewer = new Viewer.ViewerBuilder()
                .setPDF(this.pdf)
                .setAppearanceType(this.apperanceType)
                .setPath(this.path)
                .build();

        //CSS
        this.getStylesheets().add(this.viewer.getPath_css());

        //Labels
        this.name = new Label();
        this.name.setText("Path: " + this.pdf.getAbsolutePath());
        this.name.getStyleClass().add("path");

        //PagePreview

        new Thread(new Runnable() {
            @Override
            public void run() {
                pagePreview = new PagePreview(pdf, viewer);
            }
        }).start();

        //PageChooser
        this.pageChooser = new PageChooser(this.pdf, this.viewer);

        //PANES
        //Splitpane
        this.splitPane = new SplitPane();
        this.splitPane.getStyleClass().add("splitpane");
        this.splitPane.setOrientation(Orientation.HORIZONTAL);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                splitPane.setDividerPositions(0.1f);
            }
        });

        //Toolbar
        this.toolbar = new VBox();
        this.toolbar.setSpacing(5d);
        this.toolbar.setAlignment(Pos.CENTER);

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

        this.menuItemBlankText = new MenuItem("Show Blank Text");
        this.menuItemBlankText.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN));
        this.menuItemBlankText.setOnAction(event -> {
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

        this.file.getItems().addAll(this.menuItemLoadPDF/*, this.menuItemBlankText*/, this.menuItemClose);
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

        //Adding Nodes to Panes
        this.menuBar.getMenus().addAll(this.file, this.control);
        this.splitPane.getItems().addAll(this.pagePreview, this.viewer);
        this.toolbar.getChildren().addAll(this.menuBar, this.name, this.pageChooser);
        this.setTop(this.toolbar);
        BorderPane.setMargin(this.toolbar, new Insets(2, 1, 10, 1));
        this.setCenter(this.splitPane);
    }

    public void setMenuBarEnable(boolean menuBarEnable) {
        this.menuBarEnable = menuBarEnable;

        if (!menuBarEnable)
            this.toolbar.getChildren().remove(this.menuBar);
        else if (this.toolbar.getChildren().get(0) != this.menuBar)
            this.toolbar.getChildren().add(0, this.menuBar);
    }


    public void loadPDF(PDF pdf) {
        this.pdf = pdf;
        this.viewer.loadPDF(this.pdf);
        this.pagePreview.loadPDF(this.pdf);
        this.name.setText("Path: " + this.pdf.getAbsolutePath());
    }
}