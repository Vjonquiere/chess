package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.exceptions.FailedUndoException;
import pdp.model.Game;

public class UndoMultipleMoveCommand implements Command {

  private final int nbMoveToUndo;

  public UndoMultipleMoveCommand(int nbMoveToUndo) {
    this.nbMoveToUndo = nbMoveToUndo;
  }

  @Override
  public Optional<Exception> execute(final Game model, GameController controller) {
    try {
      if (model.isBlackAi() || model.isWhiteAi()) {
        for (int i = 0; i < nbMoveToUndo; i++) {
          model.previousState();
        }

        try {
          model.previousState();
        } catch (FailedUndoException e) {
          // TODO: add an event to send to the view
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
          System.out.println("Undo Move Command");
          model.getGameState().undoRequest();
        }
      }
      return Optional.empty();
    } catch (Exception e) {
      return Optional.of(e);
    }
  }
}
