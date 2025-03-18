package pdp.view.GUI.popups;

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

public class YesNoPopUp extends VBox {

  public YesNoPopUp(String title, Command command, Runnable action) {
    Stage popupStage = new Stage();
    popupStage.setTitle(TextGetter.getText(title));
    popupStage.initModality(Modality.APPLICATION_MODAL);

    Button acceptButton = new Button(TextGetter.getText("accept"));

    Button refuseButton = new Button(TextGetter.getText("refuse"));

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

    HBox buttonContainer = new HBox(10, acceptButton, refuseButton);
    buttonContainer.setAlignment(Pos.CENTER);

    VBox layout = new VBox(15, buttonContainer);
    layout.setAlignment(Pos.CENTER);
    layout.setStyle("-fx-padding: 20;");

    Scene scene = new Scene(layout, 300, 150);
    popupStage.setScene(scene);
    popupStage.showAndWait();
  }
}
