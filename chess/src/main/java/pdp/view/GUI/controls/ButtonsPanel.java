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
import pdp.view.GUI.popups.RedoPopUp;
import pdp.view.GUI.popups.UndoPopUp;

public class ButtonsPanel extends GridPane {
  private Button drawButton;
  private Button undoButton;
  private Button redoButton;
  private Button resignButton;
  private Button undrawButton;
  private Button restartButton;

  /**
   * Creates the buttons panel to simplify the user experience. Allows the user to ask for draw
   * proposal and remove it, resign, restart, undo and redo a move.
   */
  public ButtonsPanel() {
    setPadding(new Insets(10));
    setHgap(10);
    setVgap(10);
    setAlignment(Pos.CENTER);

    initResignButton();
    initDrawButton();
    initUndrawButton();
    initRedoButton();
    initUndoButton();
    initRestartButton();

    add(drawButton, 0, 0);
    add(undoButton, 1, 0);
    add(resignButton, 2, 0);

    add(undrawButton, 0, 1);
    add(redoButton, 1, 1);
    add(restartButton, 2, 1);
  }

  /** Initializes the undo button. */
  private void initUndoButton() {
    undoButton = new Button(TextGetter.getText("undo"));
    undoButton.setMinWidth(100);
    undoButton.setOnAction(
        event -> {
          undoCommand("");
          if (!Game.getInstance().isWhiteAI() && !Game.getInstance().isBlackAI()) new UndoPopUp();
        });
  }

  /** Initializes the redo button. */
  private void initRedoButton() {
    redoButton = new Button(TextGetter.getText("redo"));
    redoButton.setMinWidth(100);
    redoButton.setOnAction(
        event -> {
          redoCommand("");
          if (!Game.getInstance().isWhiteAI() && !Game.getInstance().isBlackAI()) new RedoPopUp();
        });
  }

  /** Initializes the draw button. */
  private void initDrawButton() {
    drawButton = new Button(TextGetter.getText("draw"));
    drawButton.setMinWidth(100);
    // TODO: add action to button
  }

  /** Initializes the undraw button. */
  private void initUndrawButton() {
    undrawButton = new Button(TextGetter.getText("undraw"));
    undrawButton.setMinWidth(100);
    // TODO: add action to button
  }

  /** Initializes the resign button. */
  private void initResignButton() {
    resignButton = new Button(TextGetter.getText("resign"));
    resignButton.setMinWidth(100);
    // TODO: add action to button
  }

  /** Initializes the restart button. */
  private void initRestartButton() {
    restartButton = new Button(TextGetter.getText("restart"));
    restartButton.setMinWidth(100);
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
