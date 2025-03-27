package pdp.view.gui.controls;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.CancelDrawCommand;
import pdp.controller.commands.CancelMoveCommand;
import pdp.controller.commands.ProposeDrawCommand;
import pdp.controller.commands.RestartCommand;
import pdp.controller.commands.RestoreMoveCommand;
import pdp.controller.commands.SurrenderCommand;
import pdp.model.Game;
import pdp.utils.TextGetter;
import pdp.view.gui.popups.InfoPopUp;
import pdp.view.gui.popups.YesNoPopUp;

/** GUI Buttons panel for game commands. */
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

  /**
   * Initializes the undo button and sets its action. When clicked, the button allows the player to
   * undo the last move. If neither player is controlled by AI, a confirmation popup appears, and
   * the move is undone if the player accepts.
   */
  private void initUndoButton() {
    undoButton = new Button(TextGetter.getText("undo"));
    undoButton.setMinWidth(100);
    undoButton.setOnAction(
        event -> {
          if (Game.getInstance().getGameState().getFullTurn() > 0) {
            BagOfCommands.getInstance().addCommand(new CancelMoveCommand());
            if (!Game.getInstance().isWhiteAi() && !Game.getInstance().isBlackAi()) {
              new YesNoPopUp(
                  "undoInstructionsGui",
                  new CancelMoveCommand(),
                  () -> Game.getInstance().getGameState().undoRequestReset());
            }
          } else {
            InfoPopUp.show(TextGetter.getText("notAllowed"));
          }
          if (Game.getInstance().isWhiteAi() && Game.getInstance().isBlackAi())
            InfoPopUp.show(TextGetter.getText("notAllowed"));
        });
  }

  /**
   * Initializes the redo button and sets its action. When clicked, the button allows the player to
   * redo the last undone move. If neither player is controlled by AI, a confirmation popup appears,
   * and the move is redone if the player accepts.
   */
  private void initRedoButton() {
    redoButton = new Button(TextGetter.getText("redo"));
    redoButton.setMinWidth(100);
    redoButton.setOnAction(
        event -> {
          if (Game.getInstance().getHistory().getCurrentMove().orElse(null) != null
              && Game.getInstance().getHistory().getCurrentMove().get().getNext().orElse(null)
                  != null) {
            BagOfCommands.getInstance().addCommand(new RestoreMoveCommand());
            if (!Game.getInstance().isWhiteAi() && !Game.getInstance().isBlackAi()) {
              new YesNoPopUp(
                  "redoInstructionsGui",
                  new RestoreMoveCommand(),
                  () -> Game.getInstance().getGameState().redoRequestReset());
            }
          } else {
            InfoPopUp.show(TextGetter.getText("notAllowed"));
          }

          if (Game.getInstance().isWhiteAi() && Game.getInstance().isBlackAi())
            InfoPopUp.show(TextGetter.getText("notAllowed"));
        });
  }

  /**
   * Initializes the draw button and sets its action. When clicked, the button allows the player to
   * propose a draw. A confirmation popup appears for the player to confirm their proposal before
   * sending it.
   */
  private void initDrawButton() {
    drawButton = new Button(TextGetter.getText("draw"));
    drawButton.setMinWidth(100);
    drawButton.setOnAction(
        event -> {
          if (!Game.getInstance().isWhiteAi() && !Game.getInstance().isBlackAi()) {
            new YesNoPopUp(
                "drawInstructionsGui",
                new ProposeDrawCommand(Game.getInstance().getGameState().isWhiteTurn()),
                null);
          } else {
            if (Game.getInstance().isWhiteAi() && Game.getInstance().isBlackAi())
              InfoPopUp.show(TextGetter.getText("notAllowed"));
          }
        });
  }

  /**
   * Initializes the undraw button and sets its action. When clicked, the button allows the player
   * to cancel their draw proposal. A confirmation popup appears for the player to confirm the
   * cancellation.
   */
  private void initUndrawButton() {
    undrawButton = new Button(TextGetter.getText("undraw"));
    undrawButton.setMinWidth(100);
    undrawButton.setOnAction(
        event -> {
          if (!Game.getInstance().isWhiteAi() && !Game.getInstance().isBlackAi()) {
            new YesNoPopUp(
                "undrawInstructionsGui",
                new CancelDrawCommand(Game.getInstance().getGameState().isWhiteTurn()),
                null);
          } else {
            if (Game.getInstance().isWhiteAi() && Game.getInstance().isBlackAi())
              InfoPopUp.show(TextGetter.getText("notAllowed"));
          }
        });
  }

  /**
   * Initializes the resign button and sets its action. When clicked, the button allows the player
   * to resign from the game. A confirmation popup appears for the player to confirm their
   * resignation before ending the game.
   */
  private void initResignButton() {
    resignButton = new Button(TextGetter.getText("resign"));
    resignButton.setMinWidth(100);
    resignButton.setOnAction(
        event -> {
          if (!Game.getInstance().isWhiteAi() && !Game.getInstance().isBlackAi()) {
            new YesNoPopUp(
                "resignInstructionsGui",
                new SurrenderCommand(Game.getInstance().getGameState().isWhiteTurn()),
                null);
          } else {
            if (Game.getInstance().isWhiteAi() && Game.getInstance().isBlackAi())
              InfoPopUp.show(TextGetter.getText("notAllowed"));
          }
        });
  }

  /**
   * Initializes the restart button and sets its action. When clicked, the button allows the player
   * to restart the game. A confirmation popup appears for the player to confirm the restart before
   * the game is reset.
   */
  private void initRestartButton() {
    restartButton = new Button(TextGetter.getText("restart"));
    restartButton.setMinWidth(100);
    restartButton.setOnAction(
        event -> {
          new YesNoPopUp("restartInstructionsGui", new RestartCommand(), null);
        });
  }
}
