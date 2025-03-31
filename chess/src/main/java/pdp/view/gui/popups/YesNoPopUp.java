package pdp.view.gui.popups;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pdp.controller.BagOfCommands;
import pdp.controller.Command;
import pdp.utils.TextGetter;
import pdp.view.GuiView;

/** GUI popup with only validate or cancel possibility. */
public class YesNoPopUp extends VBox {

  /**
   * Creates a Yes/No confirmation popup with two buttons: accept and refuse.
   *
   * @param title The title of the popup window.
   * @param command The command to be executed if the accept button is clicked. Can be null.
   * @param action The action to be executed if the refuse button is clicked or the popup is closed.
   *     Can be null.
   */
  public YesNoPopUp(final String title, final Command command, final Runnable action) {
    final Stage popupStage = new Stage();
    popupStage.setTitle(TextGetter.getText(title));
    popupStage.initModality(Modality.APPLICATION_MODAL);

    final Button acceptButton = new Button(TextGetter.getText("accept"));

    final Button refuseButton = new Button(TextGetter.getText("refuse"));

    acceptButton.setOnAction(
        e -> {
          if (command != null) {
            BagOfCommands.getInstance().addCommand(command);
          }
          popupStage.close();
        });

    refuseButton.setOnAction(
        e -> {
          if (action != null) {
            action.run();
          }
          popupStage.close();
        });

    popupStage.setOnCloseRequest(
        event -> {
          if (action != null) {
            action.run();
          }
          popupStage.close();
        });

    final HBox buttonContainer = new HBox(10, acceptButton, refuseButton);
    buttonContainer.setAlignment(Pos.CENTER);

    final VBox layout = new VBox(15, buttonContainer);
    layout.setAlignment(Pos.CENTER);
    layout.setStyle("-fx-padding: 20;");

    final Scene scene = new Scene(layout, 300, 150);
    GuiView.applyCss(scene);
    popupStage.setScene(scene);
    popupStage.showAndWait();
  }
}
