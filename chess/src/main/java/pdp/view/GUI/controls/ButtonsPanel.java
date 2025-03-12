package pdp.view.GUI.controls;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import pdp.utils.TextGetter;

public class ButtonsPanel extends GridPane {
  private Button drawButton;
  private Button undoButton;
  private Button redoButton;
  private Button resignButton;
  private Button undrawButton;
  private Button restartButton;
  String buttonStyle =
      "-fx-background-color: #DAE0F2;\n"
          + "-fx-text-fill: #6D6FD9;\n"
          + "-fx-border-color: #6D6FD9;\n"
          + "-fx-font-size: 18px;\n"
          + "-fx-font-weight: bold;\n"
          + "-fx-padding: 10px 20px;\n"
          + "-fx-background-radius: 20;\n"
          + "-fx-border-radius: 20;\n";

  public ButtonsPanel() {
    setPadding(new Insets(10));
    setHgap(10);
    setVgap(10);
    setAlignment(Pos.CENTER);
    initButtons();

    add(drawButton, 0, 0);
    add(undoButton, 1, 0);
    add(resignButton, 2, 0);

    add(undrawButton, 0, 1);
    add(redoButton, 1, 1);
    add(restartButton, 2, 1);
  }

  private void initButtons() {
    initResignButton();
    initDrawButton();
    initUndrawButton();
    initRedoButton();
    initUndoButton();
    initRestartButton();
  }

  private void initUndoButton() {
    undoButton = new Button(TextGetter.getText("undo"));
    undoButton.setStyle(buttonStyle);
    undoButton.setMinWidth(100);
    // TODO: add action to button
  }

  private void initRedoButton() {
    redoButton = new Button(TextGetter.getText("redo"));
    redoButton.setStyle(buttonStyle);
    redoButton.setMinWidth(100);
    // TODO: add action to button
  }

  private void initDrawButton() {
    drawButton = new Button(TextGetter.getText("draw"));
    drawButton.setStyle(buttonStyle);
    drawButton.setMinWidth(100);
    // TODO: add action to button
  }

  private void initUndrawButton() {
    undrawButton = new Button(TextGetter.getText("undraw"));
    undrawButton.setStyle(buttonStyle);
    undrawButton.setMinWidth(100);
    // TODO: add action to button
  }

  private void initResignButton() {
    resignButton = new Button(TextGetter.getText("resign"));
    resignButton.setStyle(buttonStyle);
    resignButton.setMinWidth(100);
    // TODO: add action to button
  }

  private void initRestartButton() {
    restartButton = new Button(TextGetter.getText("restart"));
    restartButton.setStyle(buttonStyle);
    restartButton.setMinWidth(100);
    // TODO: add action to button
  }
}
