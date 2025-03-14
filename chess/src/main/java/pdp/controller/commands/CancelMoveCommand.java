package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.Game;

public class CancelMoveCommand implements Command {
  /**
   * Cancels the last move in the game.
   *
   * @param model The game model on which the command is executed.
   * @param controller The game controller managing game commands.
   * @return An Optional containing an exception if an error occurred, or empty if successful
   */
  @Override
  public Optional<Exception> execute(Game model, GameController controller) {
    try {
      if (model.isBlackAI() || model.isWhiteAI()) {
        model.previousState();
        try {
          model.previousState();
        } catch (Exception e) {
        }
        if (model.isBlackAI() && !model.getGameState().isWhiteTurn()) {
          model.getBlackSolver().playAIMove(model);
        }
        if (model.isWhiteAI() && model.getGameState().isWhiteTurn()) {
          model.getWhiteSolver().playAIMove(model);
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
