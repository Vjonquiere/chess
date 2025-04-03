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

  /**
   * Creates the buttons panel to simplify the user experience. Allows the user to ask for draw
   * proposal and remove it, resign, restart, undo and redo a move.
   */
  public ButtonsPanel() {
    super();
    setPadding(new Insets(10));
    setHgap(10);
    setVgap(10);
    setAlignment(Pos.CENTER);

    add(createDrawButton(), 0, 0);
    add(createUndoButton(), 1, 0);
    add(createResignButton(), 2, 0);

    add(createUndrawButton(), 0, 1);
    add(createRedoButton(), 1, 1);
    add(createRestartButton(), 2, 1);
  }

  /**
   * Creates the undo button and sets its action. When clicked, the button allows the player to undo
   * the last move. If neither player is controlled by AI, a confirmation popup appears, and the
   * move is undone if the player accepts.
   */
  private Button createUndoButton() {
    final Button undoButton = new Button(TextGetter.getText("undo"));
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
          if (Game.getInstance().isWhiteAi() && Game.getInstance().isBlackAi()) {
            InfoPopUp.show(TextGetter.getText("notAllowed"));
          }
        });
    return undoButton;
  }

  /**
   * Creates the redo button and sets its action. When clicked, the button allows the player to redo
   * the last undone move. If neither player is controlled by AI, a confirmation popup appears, and
   * the move is redone if the player accepts.
   */
  private Button createRedoButton() {
    final Button redoButton = new Button(TextGetter.getText("redo"));
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

          if (Game.getInstance().isWhiteAi() && Game.getInstance().isBlackAi()) {
            InfoPopUp.show(TextGetter.getText("notAllowed"));
          }
        });
    return redoButton;
  }

  /**
   * Creates the draw button and sets its action. When clicked, the button allows the player to
   * propose a draw. A confirmation popup appears for the player to confirm their proposal before
   * sending it.
   */
  private Button createDrawButton() {
    final Button drawButton = new Button(TextGetter.getText("draw"));
    drawButton.setMinWidth(100);
    drawButton.setOnAction(
        event -> {
          if (!Game.getInstance().isWhiteAi() && !Game.getInstance().isBlackAi()) {
            new YesNoPopUp(
                "drawInstructionsGui",
                new ProposeDrawCommand(Game.getInstance().getGameState().isWhiteTurn()),
                null);
          } else {
            if (Game.getInstance().isWhiteAi() && Game.getInstance().isBlackAi()) {
              InfoPopUp.show(TextGetter.getText("notAllowed"));
            }
          }
        });
    return drawButton;
  }

  /**
   * Creates the undraw button and sets its action. When clicked, the button allows the player to
   * cancel their draw proposal. A confirmation popup appears for the player to confirm the
   * cancellation.
   */
  private Button createUndrawButton() {
    final Button undrawButton = new Button(TextGetter.getText("undraw"));
    undrawButton.setMinWidth(100);
    undrawButton.setOnAction(
        event -> {
          if (!Game.getInstance().isWhiteAi() && !Game.getInstance().isBlackAi()) {
            new YesNoPopUp(
                "undrawInstructionsGui",
                new CancelDrawCommand(Game.getInstance().getGameState().isWhiteTurn()),
                null);
          } else {
            if (Game.getInstance().isWhiteAi() && Game.getInstance().isBlackAi()) {
              InfoPopUp.show(TextGetter.getText("notAllowed"));
            }
          }
        });
    return undrawButton;
  }

  /**
   * Creates the resignation button and sets its action. When clicked, the button allows the player
   * to resign from the game. A confirmation popup appears for the player to confirm their
   * resignation before ending the game.
   */
  private Button createResignButton() {
    final Button resignButton = new Button(TextGetter.getText("resign"));
    resignButton.setMinWidth(100);
    resignButton.setOnAction(
        event -> {
          if (!Game.getInstance().isWhiteAi() && !Game.getInstance().isBlackAi()) {
            new YesNoPopUp(
                "resignInstructionsGui",
                new SurrenderCommand(Game.getInstance().getGameState().isWhiteTurn()),
                null);
          } else {
            if (Game.getInstance().isWhiteAi() && Game.getInstance().isBlackAi()) {
              InfoPopUp.show(TextGetter.getText("notAllowed"));
            }
          }
        });
    return resignButton;
  }

  /**
   * Creates the restart button and sets its action. When clicked, the button allows the player to
   * restart the game. A confirmation popup appears for the player to confirm the restart before the
   * game is reset.
   */
  private Button createRestartButton() {
    final Button restartButton = new Button(TextGetter.getText("restart"));
    restartButton.setMinWidth(100);
    restartButton.setOnAction(
        event -> new YesNoPopUp("restartInstructionsGui", new RestartCommand(), null));
    return restartButton;
  }
}
