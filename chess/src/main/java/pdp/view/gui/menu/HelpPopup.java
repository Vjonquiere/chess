package pdp.view.gui.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pdp.utils.TextGetter;
import pdp.view.GuiView;

/** GUI widget with basic help message. */
public class HelpPopup extends VBox {
  /** Build a new popup. */
  public HelpPopup() {
    super();
    final Stage popupStage = new Stage();
    popupStage.setTitle(TextGetter.getText("help.title"));
    popupStage.initModality(Modality.APPLICATION_MODAL);
    final Label manualLabel = getLabel();

    final Button closeButton = new Button(TextGetter.getText("close"));
    closeButton.setOnAction(e -> popupStage.close());

    final VBox layout = new VBox(10, manualLabel, closeButton);
    layout.setAlignment(Pos.TOP_CENTER);
    layout.setStyle("-fx-padding: 15;");
    final Scene scene = new Scene(layout);
    GuiView.applyCss(scene);
    popupStage.setScene(scene);
    popupStage.showAndWait();
  }

  /**
   * Get the string from TextGetter and build a Label from it.
   *
   * @return A label containing the help message.
   */
  private Label getLabel() {
    final Label manualLabel = new Label(TextGetter.getText("help.message"));
    manualLabel.setWrapText(true);
    return manualLabel;
  }
}
