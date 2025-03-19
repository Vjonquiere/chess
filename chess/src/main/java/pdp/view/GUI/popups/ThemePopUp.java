package pdp.view.GUI.popups;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.ChangeTheme;
import pdp.utils.TextGetter;
import pdp.view.GUI.themes.ColorTheme;
import pdp.view.GUIView;

public class ThemePopUp {
  public static void show() {
    Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle(TextGetter.getText("theme.title"));

    VBox layout = new VBox(10);
    layout.setId("root");
    Label primary = new Label(TextGetter.getText("theme.primary"));
    ColorPicker cp_primary = new ColorPicker(Color.web(ColorTheme.CUSTOM.getPrimary()));
    cp_primary.setId("primary");
    Label secondary = new Label(TextGetter.getText("theme.secondary"));
    ColorPicker cp_secondary = new ColorPicker(Color.web(ColorTheme.CUSTOM.getSecondary()));
    cp_secondary.setId("secondary");
    Label tertiary = new Label(TextGetter.getText("theme.tertiary"));
    ColorPicker cp_tertiary = new ColorPicker(Color.web(ColorTheme.CUSTOM.getTertiary()));
    cp_tertiary.setId("tertiary");
    Label accent = new Label(TextGetter.getText("theme.accent"));
    ColorPicker cp_accent = new ColorPicker(Color.web(ColorTheme.CUSTOM.getAccent()));
    cp_accent.setId("accent");
    Label background = new Label(TextGetter.getText("theme.background"));
    ColorPicker cp_background = new ColorPicker(Color.web(ColorTheme.CUSTOM.getBackground()));
    cp_background.setId("background");
    Label background2 = new Label(TextGetter.getText("theme.background2"));
    ColorPicker cp_background2 = new ColorPicker(Color.web(ColorTheme.CUSTOM.getBackground2()));
    cp_background2.setId("background2");
    Label text = new Label(TextGetter.getText("theme.text"));
    ColorPicker cp_text = new ColorPicker(Color.web(ColorTheme.CUSTOM.getText()));
    cp_text.setId("text");
    Label textInverted = new Label(TextGetter.getText("theme.textInverted"));
    ColorPicker cp_textInverted = new ColorPicker(Color.web(ColorTheme.CUSTOM.getTextInverted()));
    cp_textInverted.setId("textInverted");
    layout
        .getChildren()
        .addAll(
            primary,
            cp_primary,
            secondary,
            cp_secondary,
            tertiary,
            cp_tertiary,
            accent,
            cp_accent,
            background,
            cp_background,
            background2,
            cp_background2,
            text,
            cp_text,
            textInverted,
            cp_textInverted);

    HBox buttonBox = new HBox();
    Button saveButton = new Button(TextGetter.getText("save"));
    saveButton.setId("saveButton");
    saveButton.setOnAction(
        e -> {
          try {
            ColorTheme.setCustom(
                toHexString(cp_primary.getValue()),
                toHexString(cp_secondary.getValue()),
                toHexString(cp_tertiary.getValue()),
                toHexString(cp_accent.getValue()),
                toHexString(cp_background.getValue()),
                toHexString(cp_background2.getValue()),
                toHexString(cp_text.getValue()),
                toHexString(cp_textInverted.getValue()));
            GUIView.theme = ColorTheme.CUSTOM;
            BagOfCommands.getInstance().addCommand(new ChangeTheme());
          } catch (IllegalArgumentException ex) {
          }
        });

    Button cancelButton = new Button(TextGetter.getText("cancel"));
    cancelButton.setId("cancelButton");
    cancelButton.setOnAction(
        e -> {
          popupStage.close();
        });
    buttonBox.getChildren().addAll(saveButton, cancelButton);
    buttonBox.setSpacing(10);
    buttonBox.setAlignment(Pos.CENTER);
    layout.getChildren().add(buttonBox);

    Scene scene = new Scene(layout, 400, 700);
    GUIView.applyCSS(scene);
    layout.setStyle("; -fx-padding: 10; -fx-alignment: center;");
    popupStage.setScene(scene);
    popupStage.setResizable(false);
    popupStage.showAndWait();
  }

  private static String toHexString(Color color) {
    return String.format(
        "#%02X%02X%02X",
        (int) (color.getRed() * 255),
        (int) (color.getGreen() * 255),
        (int) (color.getBlue() * 255));
  }
}
