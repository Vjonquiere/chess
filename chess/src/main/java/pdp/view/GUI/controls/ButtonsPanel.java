package pdp.view.GUI.controls;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.CancelMoveCommand;
import pdp.controller.commands.RestartCommand;
import pdp.controller.commands.RestoreMoveCommand;
import pdp.model.Game;
import pdp.utils.TextGetter;
import pdp.view.GUI.CustomButton;
import pdp.view.GUI.popups.RedoPopUp;
import pdp.view.GUI.popups.UndoPopUp;

public class ButtonsPanel extends GridPane {
  private Button drawButton;
  private Button undoButton;
  private Button redoButton;
  private Button resignButton;
  private Button undrawButton;
  private Button restartButton;
  private int buttonMinWidth = 100;

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
    undoButton = new CustomButton(TextGetter.getText("undo"), buttonMinWidth);
    undoButton.setOnAction(
        event -> {
          undoCommand("");
          if (!Game.getInstance().isWhiteAI() && !Game.getInstance().isBlackAI()) new UndoPopUp();
        });
  }

  private void initRedoButton() {
    redoButton = new CustomButton(TextGetter.getText("redo"), buttonMinWidth);
    redoButton.setOnAction(
        event -> {
          redoCommand("");
          if (!Game.getInstance().isWhiteAI() && !Game.getInstance().isBlackAI()) new RedoPopUp();
        });
  }

  private void initDrawButton() {
    drawButton = new CustomButton(TextGetter.getText("draw"), buttonMinWidth);
    // TODO: add action to button
  }

  private void initUndrawButton() {
    undrawButton = new CustomButton(TextGetter.getText("undraw"), buttonMinWidth);
    // TODO: add action to button
  }

  private void initResignButton() {
    resignButton = new CustomButton(TextGetter.getText("resign"), buttonMinWidth);
    // TODO: add action to button
  }

  private void initRestartButton() {
    restartButton = new CustomButton(TextGetter.getText("restart"), buttonMinWidth);
    restartButton.setOnAction(
        event -> {
          restartCommand("");
        });
  }

  /**
   * Handles the undo command by reverting the last move in history.
   *
   * @param args Unused argument
   */
  private void undoCommand(String args) {
    BagOfCommands.getInstance().addCommand(new CancelMoveCommand());
  }

  /**
   * Handles the redo command by re-executing a previously undone move.
   *
   * @param args Unused argument
   */
  private void redoCommand(String args) {
    BagOfCommands.getInstance().addCommand(new RestoreMoveCommand());
  }

  /**
   * Handles the restart command by restarting a new game.
   *
   * @param args Unused argument
   */
  private void restartCommand(String args) {
    BagOfCommands.getInstance().addCommand(new RestartCommand());
  }
}
