package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.exceptions.FailedUndoException;
import pdp.model.Game;

/**
 * Part of Command Design pattern. Creates a command to cancel several moves. Corresponds to
 * repeated undo.
 */
public class UndoMultipleMoveCommand implements Command {

  /** Number of moves to undo. */
  private final int nbMoveToUndo;

  /**
   * Creates the proposal of draw command. Will then propose a draw from the given player.
   *
   * @param nbMoveToUndo Number of moves to undo.
   */
  public UndoMultipleMoveCommand(final int nbMoveToUndo) {
    this.nbMoveToUndo = nbMoveToUndo;
  }

  /**
   * Cancels the nbMoveToUndo last move in the game.
   *
   * @param model The game model on which the command is executed.
   * @param controller The game controller managing game commands.
   * @return An Optional containing an exception if an error occurred, or empty if successful
   */
  @Override
  public Optional<Exception> execute(final Game model, GameController controller) {
    try {
      if (model.isBlackAi() || model.isWhiteAi()) {
        for (int i = 0; i < nbMoveToUndo; i++) {
          model.previousState();
        }

        try {
          model.previousState();
        } catch (FailedUndoException ignored) {
        }
        if (model.isBlackAi() && !model.getGameState().isWhiteTurn()) {
          model.getBlackSolver().playAiMove(model);
        }
        if (model.isWhiteAi() && model.getGameState().isWhiteTurn()) {
          model.getWhiteSolver().playAiMove(model);
        }
      } else {
        if (model.getGameState().getUndoRequestTurnNumber() == model.getGameState().getFullTurn()) {
          for (int i = 0; i < nbMoveToUndo; i++) {
            model.previousState();
          }
        } else {
          model.getGameState().undoRequest();
        }
      }
      return Optional.empty();
    } catch (Exception e) {
      return Optional.of(e);
    }
  }
}
