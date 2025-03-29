package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.exceptions.FailedUndoException;
import pdp.model.Game;

/**
 * Part of Command Design pattern. Creates a command to cancel the last move. Corresponds to an
 * undo.
 */
public class CancelMoveCommand implements Command {
  /**
   * Cancels the last move in the game.
   *
   * @param model The game model on which the command is executed.
   * @param controller The game controller managing game commands.
   * @return An Optional containing an exception if an error occurred, or empty if successful
   */
  @Override
  public Optional<Exception> execute(final Game model, GameController controller) {
    try {
      if (model.isBlackAi() || model.isWhiteAi()) {
        model.previousState();
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
          model.previousState();
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
