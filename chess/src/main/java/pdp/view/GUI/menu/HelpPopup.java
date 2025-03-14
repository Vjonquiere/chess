package pdp.view.GUI.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pdp.utils.TextGetter;

public class HelpPopup extends VBox {
  public HelpPopup() {
    Stage popupStage = new Stage();
    popupStage.setTitle(TextGetter.getText("help.title"));
    popupStage.initModality(Modality.APPLICATION_MODAL);
    Label manualLabel = getLabel();

    Button closeButton = new Button(TextGetter.getText("close"));
    closeButton.setOnAction(e -> popupStage.close());

    VBox layout = new VBox(10, manualLabel, closeButton);
    layout.setAlignment(Pos.TOP_CENTER);
    layout.setStyle("-fx-padding: 15;");
    Scene scene = new Scene(layout);
    popupStage.setScene(scene);
    popupStage.showAndWait();
  }

  private Label getLabel() {
    Label manualLabel = new Label(TextGetter.getText("help.message"));
    manualLabel.setWrapText(true);
    return manualLabel;
  }
}
