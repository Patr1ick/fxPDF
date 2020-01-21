package viewer.nodes;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import util.PDF;
import viewer.Viewer;
import viewer.event.CustomEvent;
import viewer.event.PageSwitchEventHandler;

public class PageChooser extends HBox {

    private Label label;
    private TextField textField;

    private Viewer viewer;
    private PDF pdf;

    public PageChooser(PDF pdf, Viewer viewer) {
        this.setAlignment(Pos.CENTER);
        this.getStylesheets().add("resource/css/style.css");
        if (pdf != null && viewer != null) {
            this.pdf = pdf;
            this.viewer = viewer;
            this.viewer.addEventHandler(CustomEvent.CUSTOM_EVENT_TYPE, new PageSwitchEventHandler() {
                @Override
                public void onPageSwitch(String param) {
                    textField.setText(Integer.toString(viewer.getCurrentPageNumber() + 1));
                }
            });

            //Label
            this.label = new Label(" / " + this.pdf.getNumberOfPages());
            this.label.getStyleClass().add("labelPageChooser");

            //TextField
            this.textField = new TextField();
            this.textField.setText(Integer.toString(this.viewer.getCurrentPageNumber() + 1));
            this.textField.setPrefSize(50d, 5d);
            this.textField.setMaxHeight(5d);
            this.textField.getStyleClass().add("textFieldPageChooser");
            this.textField.setOnAction(event -> {
                viewer.loadPage(Integer.parseInt(this.textField.getText()));
            });

            this.getChildren().addAll(this.textField, this.label);
        }
    }
}
