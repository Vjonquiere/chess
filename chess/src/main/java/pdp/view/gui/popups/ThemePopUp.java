package pdp.view.gui.popups;

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
import pdp.view.GuiView;
import pdp.view.gui.themes.ColorTheme;

public class ThemePopUp {

  /**
   * Creates and launches the Theme popup. Allows the user to choose different colors to customize
   * the application's theme.
   */
  public static void show() {
    Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle(TextGetter.getText("theme.title"));

    VBox layout = new VBox(10);
    layout.setId("vbox");
    Label primary = new Label(TextGetter.getText("theme.primary"));
    ColorPicker cpPrimary = new ColorPicker(Color.web(ColorTheme.CUSTOM.getPrimary()));
    cpPrimary.setId("primary");
    Label secondary = new Label(TextGetter.getText("theme.secondary"));
    ColorPicker cpSecondary = new ColorPicker(Color.web(ColorTheme.CUSTOM.getSecondary()));
    cpSecondary.setId("secondary");
    Label tertiary = new Label(TextGetter.getText("theme.tertiary"));
    ColorPicker cpTertiary = new ColorPicker(Color.web(ColorTheme.CUSTOM.getTertiary()));
    cpTertiary.setId("tertiary");
    Label accent = new Label(TextGetter.getText("theme.accent"));
    ColorPicker cpAccent = new ColorPicker(Color.web(ColorTheme.CUSTOM.getAccent()));
    cpAccent.setId("accent");
    Label background = new Label(TextGetter.getText("theme.background"));
    ColorPicker cpBackground = new ColorPicker(Color.web(ColorTheme.CUSTOM.getBackground()));
    cpBackground.setId("background");
    Label background2 = new Label(TextGetter.getText("theme.background2"));
    ColorPicker cpBackground2 = new ColorPicker(Color.web(ColorTheme.CUSTOM.getBackground2()));
    cpBackground2.setId("background2");
    Label text = new Label(TextGetter.getText("theme.text"));
    ColorPicker cpText = new ColorPicker(Color.web(ColorTheme.CUSTOM.getText()));
    cpText.setId("text");
    Label textInverted = new Label(TextGetter.getText("theme.textInverted"));
    ColorPicker cpTextInverted = new ColorPicker(Color.web(ColorTheme.CUSTOM.getTextInverted()));
    cpTextInverted.setId("textInverted");
    layout
        .getChildren()
        .addAll(
            primary,
            cpPrimary,
            secondary,
            cpSecondary,
            tertiary,
            cpTertiary,
            accent,
            cpAccent,
            background,
            cpBackground,
            background2,
            cpBackground2,
            text,
            cpText,
            textInverted,
            cpTextInverted);

    HBox buttonBox = new HBox();
    Button saveButton = new Button(TextGetter.getText("save"));
    saveButton.setId("saveButtonThemes");
    saveButton.setOnAction(
        e -> {
          try {
            ColorTheme.setCustom(
                toHexString(cpPrimary.getValue()),
                toHexString(cpSecondary.getValue()),
                toHexString(cpTertiary.getValue()),
                toHexString(cpAccent.getValue()),
                toHexString(cpBackground.getValue()),
                toHexString(cpBackground2.getValue()),
                toHexString(cpText.getValue()),
                toHexString(cpTextInverted.getValue()));
            GuiView.theme = ColorTheme.CUSTOM;
            BagOfCommands.getInstance().addCommand(new ChangeTheme());
          } catch (IllegalArgumentException ex) {
          }
        });

    Button cancelButton = new Button(TextGetter.getText("cancel"));
    cancelButton.setId("cancelButtonThemes");
    cancelButton.setOnAction(
        e -> {
          popupStage.close();
        });
    buttonBox.getChildren().addAll(saveButton, cancelButton);
    buttonBox.setSpacing(10);
    buttonBox.setAlignment(Pos.CENTER);
    layout.getChildren().add(buttonBox);

    Scene scene = new Scene(layout, 400, 700);
    GuiView.applyCss(scene);
    layout.setStyle("; -fx-padding: 10; -fx-alignment: center;");
    popupStage.setScene(scene);
    popupStage.setResizable(false);
    popupStage.showAndWait();
  }

  /**
   * Converts the Colors to a string on the hexadecimal format to be compatible with the Enum.
   *
   * @param color Color of the color picker.
   * @return String corresponding to the translation of a color to hexadecimal string.
   */
  public static String toHexString(Color color) {
    return String.format(
        "#%02X%02X%02X",
        (int) (color.getRed() * 255),
        (int) (color.getGreen() * 255),
        (int) (color.getBlue() * 255));
  }
}
