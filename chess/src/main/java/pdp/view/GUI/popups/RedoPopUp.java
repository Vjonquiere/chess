package pdp.view.GUI.popups;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.RestoreMoveCommand;
import pdp.model.Game;
import pdp.utils.TextGetter;
import pdp.view.GUIView;

public class RedoPopUp extends VBox {
  public RedoPopUp() {
    Stage popupStage = new Stage();
    popupStage.setTitle(TextGetter.getText("redoInstructionsGui"));
    popupStage.initModality(Modality.APPLICATION_MODAL);

    Button acceptButton = new Button(TextGetter.getText("accept"));

    Button refuseButton = new Button(TextGetter.getText("refuse"));

    acceptButton.setOnAction(
        e -> {
          BagOfCommands.getInstance().addCommand(new RestoreMoveCommand());
          popupStage.close();
        });

    refuseButton.setOnAction(
        e -> {
          Game.getInstance().getGameState().redoRequestReset();
          popupStage.close();
        });

    popupStage.setOnCloseRequest(
        event -> {
          Game.getInstance().getGameState().redoRequestReset();
        });

    HBox buttonContainer = new HBox(10, acceptButton, refuseButton);
    buttonContainer.setAlignment(Pos.CENTER);

    VBox layout = new VBox(15, buttonContainer);
    layout.setAlignment(Pos.CENTER);
    layout.setStyle("-fx-padding: 20;");

    Scene scene = new Scene(layout, 300, 150);
    GUIView.applyCSS(scene);
    popupStage.setScene(scene);
    popupStage.showAndWait();
  }
}
