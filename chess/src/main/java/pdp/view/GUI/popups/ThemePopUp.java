package pdp.view.GUI.popups;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.ChangeTheme;
import pdp.utils.TextGetter;

public class ThemePopUp {
  public static void show() {
    Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle(TextGetter.getText("theme.title"));

    VBox layout = new VBox(10);
    Label themeLabel = new Label(TextGetter.getText("theme.select"));
    layout.getChildren().add(themeLabel);

    HBox buttonBox = new HBox();
    Button saveButton = new Button(TextGetter.getText("save"));
    saveButton.setId("saveButton");
    saveButton.setOnAction(
        e -> {
          try {
            // GUIView.theme = ColorTheme.valueOf(themeDropdown.getValue());
          } catch (IllegalArgumentException ex) {
          }
          BagOfCommands.getInstance().addCommand(new ChangeTheme());
        });
    Button cancelButton = new Button(TextGetter.getText("cancel"));
    cancelButton.setId("cancelButton");
    cancelButton.setOnAction(
        e -> {
          popupStage.close();
        });
    buttonBox.getChildren().addAll(saveButton, cancelButton);
    layout.getChildren().add(buttonBox);

    Scene scene = new Scene(layout, 200, 400);
    popupStage.setScene(scene);
    popupStage.showAndWait();
  }
}
