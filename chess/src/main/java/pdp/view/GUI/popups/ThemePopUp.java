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
import pdp.view.GUI.themes.ColorTheme;
import pdp.view.GUIView;

public class ThemePopUp {
  public static void show(GUIView view) {
    Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle(TextGetter.getText("theme.title"));

    VBox layout = new VBox(10);
    layout.setStyle(
        "-fx-background-color: "
            + GUIView.theme.getBackground()
            + "; -fx-padding: 10; -fx-text-fill: black;");
    Label themeLabel = new Label(TextGetter.getText("theme.select"));
    layout.getChildren().add(themeLabel);

    ComboBox<String> themeDropdown = new ComboBox<>();
    themeDropdown.setId("themeDropdown");
    for (ColorTheme theme : ColorTheme.values()) {
      themeDropdown.getItems().add(theme.name());
    }
    // themeDropdown.getItems().add("Custom");
    themeDropdown.setValue(ColorTheme.SIMPLE.name());

    layout.getChildren().add(themeDropdown);

    HBox buttonBox = new HBox();
    Button saveButton = new Button(TextGetter.getText("save"));
    saveButton.setId("saveButton");
    saveButton.setOnAction(
        e -> {
          try {
            GUIView.theme = ColorTheme.valueOf(themeDropdown.getValue());
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
