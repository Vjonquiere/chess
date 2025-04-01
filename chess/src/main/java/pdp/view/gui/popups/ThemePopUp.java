package pdp.view.gui.popups;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import pdp.view.gui.themes.CustomColorTheme;

/** GUI popup to configure a custom theme. */
public class ThemePopUp {

  /**
   * Creates and launches the Theme popup. Allows the user to choose different colors to customize
   * the application's theme.
   */
  public static void show() {
    final Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle(TextGetter.getText("theme.title"));

    final VBox layout = new VBox(10);
    layout.setId("vbox");

    final Label nameLabel = new Label(TextGetter.getText("theme.name"));
    final TextField themeNameField = new TextField();
    themeNameField.setId("themeName");

    final Label primary = new Label(TextGetter.getText("theme.primary"));
    final ColorPicker cpPrimary = new ColorPicker();
    cpPrimary.setId("primary");
    final Label secondary = new Label(TextGetter.getText("theme.secondary"));
    final ColorPicker cpSecondary = new ColorPicker();
    cpSecondary.setId("secondary");
    final Label tertiary = new Label(TextGetter.getText("theme.tertiary"));
    final ColorPicker cpTertiary = new ColorPicker();
    cpTertiary.setId("tertiary");
    final Label accent = new Label(TextGetter.getText("theme.accent"));
    final ColorPicker cpAccent = new ColorPicker();
    cpAccent.setId("accent");
    final Label background = new Label(TextGetter.getText("theme.background"));
    final ColorPicker cpBackground = new ColorPicker();
    cpBackground.setId("background");
    final Label background2 = new Label(TextGetter.getText("theme.background2"));
    final ColorPicker cpBackground2 = new ColorPicker();
    cpBackground2.setId("background2");
    final Label text = new Label(TextGetter.getText("theme.text"));
    final ColorPicker cpText = new ColorPicker();
    cpText.setId("text");
    final Label textInverted = new Label(TextGetter.getText("theme.textInverted"));
    final ColorPicker cpTextInverted = new ColorPicker();
    cpTextInverted.setId("textInverted");

    layout
        .getChildren()
        .addAll(
            nameLabel, themeNameField,
            primary, cpPrimary,
            secondary, cpSecondary,
            tertiary, cpTertiary,
            accent, cpAccent,
            background, cpBackground,
            background2, cpBackground2,
            text, cpText,
            textInverted, cpTextInverted);

    final Button saveButton = new Button(TextGetter.getText("save"));
    saveButton.setId("saveButtonThemes");
    saveButton.setOnAction(
        e -> {
          try {
            final String themeName =
                themeNameField.getText().trim().isEmpty()
                    ? "Custom Theme"
                    : themeNameField.getText().trim();

            final CustomColorTheme newTheme =
                new CustomColorTheme(
                    themeName,
                    toHexString(cpPrimary.getValue()),
                    toHexString(cpSecondary.getValue()),
                    toHexString(cpTertiary.getValue()),
                    toHexString(cpAccent.getValue()),
                    toHexString(cpBackground.getValue()),
                    toHexString(cpBackground2.getValue()),
                    toHexString(cpText.getValue()),
                    toHexString(cpTextInverted.getValue()));

            ColorTheme.addTheme(themeName, newTheme);

            GuiView.setTheme(newTheme);
            BagOfCommands.getInstance().addCommand(new ChangeTheme());
            popupStage.close();
          } catch (IllegalArgumentException ignore) {
          }
        });

    final Button cancelButton = new Button(TextGetter.getText("cancel"));
    cancelButton.setId("cancelButtonThemes");
    cancelButton.setOnAction(e -> popupStage.close());

    final HBox buttonBox = new HBox(10, saveButton, cancelButton);
    buttonBox.setAlignment(Pos.CENTER);
    layout.getChildren().add(buttonBox);

    final Scene scene = new Scene(layout, 400, 800);
    GuiView.applyCss(scene);
    layout.setStyle("-fx-padding: 10; -fx-alignment: center;");
    popupStage.setScene(scene);
    popupStage.setResizable(false);
    popupStage.showAndWait();
  }

  /**
   * Converts the Colors to a string in hexadecimal format to be compatible with the Enum.
   *
   * @param color Color from the color picker.
   * @return Hexadecimal string representation of the color.
   */
  public static String toHexString(final Color color) {
    return String.format(
        "#%02X%02X%02X",
        (int) (color.getRed() * 255),
        (int) (color.getGreen() * 255),
        (int) (color.getBlue() * 255));
  }
}
