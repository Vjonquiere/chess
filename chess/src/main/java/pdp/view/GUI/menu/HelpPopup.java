package pdp.view.GUI.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HelpPopup extends VBox {
  public HelpPopup() {
    Stage popupStage = new Stage();
    popupStage.setTitle("How to use this Software?");
    popupStage.initModality(Modality.APPLICATION_MODAL);
    Label manualLabel = getLabel();

    Button closeButton = new Button("Close");
    closeButton.setOnAction(e -> popupStage.close());

    VBox layout = new VBox(10, manualLabel, closeButton);
    layout.setAlignment(Pos.TOP_CENTER);
    layout.setStyle("-fx-padding: 15;");
    Scene scene = new Scene(layout);
    popupStage.setScene(scene);
    popupStage.showAndWait();
  }

  private Label getLabel() {
    Label manualLabel =
        new Label(
            """
                        Welcome to chess Software!

                        1. How to configure a new game ?
                        \tGo to file->new Game and setup your game as you want

                        2. How to load a game from a file ?
                        \tGo to file->load and select your file

                        3. ?
                        \t

                        For more details, Check the -h to know all configurable options.""");
    manualLabel.setWrapText(true);
    return manualLabel;
  }
}
