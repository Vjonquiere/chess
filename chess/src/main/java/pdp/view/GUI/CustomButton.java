package pdp.view.GUI;

import javafx.scene.control.Button;
import pdp.view.GUIView;

public class CustomButton extends Button {
  private final String buttonStyle =
      "-fx-background-color: "
          + GUIView.theme.getSecondary()
          + ";"
          + "-fx-text-fill: "
          + GUIView.theme.getPrimary()
          + ";"
          + "-fx-border-color: "
          + GUIView.theme.getPrimary()
          + ";"
          + "-fx-font-size: 18px;"
          + "-fx-font-weight: bold;"
          + "-fx-padding: 15;"
          + "-fx-background-radius: 20;"
          + "-fx-border-radius: 20;";

  public CustomButton(String name) {
    super(name);
    applyStyle(buttonStyle);
    this.setMinWidth(100);
  }

  public CustomButton(String name, String style) {
    super(name);
    applyStyle(style);
    this.setMinWidth(100);
  }

  private void applyStyle(String buttonStyle) {
    this.setStyle(buttonStyle);
    this.setOnMouseEntered(
        e ->
            this.setStyle(
                "-fx-background-color: "
                    + GUIView.theme.getPrimary()
                    + ";"
                    + "-fx-text-fill: "
                    + GUIView.theme.getSecondary()
                    + ";"
                    + "-fx-border-color: "
                    + GUIView.theme.getSecondary()
                    + ";"
                    + "-fx-font-size: 18px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-padding: 15;"
                    + "-fx-background-radius: 20;"
                    + "-fx-border-radius: 20;"));
    this.setOnMouseExited(e -> this.setStyle(buttonStyle));
  }
}
