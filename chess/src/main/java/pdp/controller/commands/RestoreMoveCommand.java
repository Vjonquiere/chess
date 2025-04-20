package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.GameAbstract;

/**
 * Part of Command Design pattern. Creates a command to replay the last move. Corresponds to a redo.
 */
public class RestoreMoveCommand implements Command {

  /**
   * Reverts the last cancelled move in the game model if possible.
   *
   * @param model The game model on which the command is executed.
   * @param controller The game controller managing game commands.
   * @return An Optional containing an exception if an error occurred or empty if successful.
   */
  @Override
  public Optional<Exception> execute(final GameAbstract model, GameController controller) {
    try {
      if (model.getGameState().getRedoRequestTurnNumber() == model.getGameState().getFullTurn()) {
        model.nextState();
        if (model.isBlackAi() && !model.isWhiteTurn()) {
          model.getBlackSolver().playAiMove(model);
        }
        if (model.isWhiteAi() && model.getGameState().isWhiteTurn()) {
          model.getWhiteSolver().playAiMove(model);
        }
      } else {
        if (model.isBlackAi() || model.isWhiteAi()) {
          model.nextState();
          if (model.isBlackAi() && !model.getGameState().isWhiteTurn()) {
            model.getBlackSolver().playAiMove(model);
          }
          if (model.isWhiteAi() && model.getGameState().isWhiteTurn()) {
            model.getWhiteSolver().playAiMove(model);
          }
        } else {
          model.getGameState().redoRequest();
        }
      }
      return Optional.empty();
    } catch (Exception e) {
      return Optional.of(e);
    }
  }
}
