package pdp.controller.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.Game;
import pdp.model.ai.Solver;
import pdp.model.board.Move;

public class AskHintComand implements Command {
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
      Solver hintSolver = new Solver();
      Move hintMove =
          hintSolver
              .getAlgorithm()
              .findBestMove(Game.getInstance(), 4, Game.getInstance().getGameState().isWhiteTurn())
              .move();
      List<Integer> hintIntegers =
          new ArrayList<>(
              Arrays.asList(
                  hintMove.getSource().getX(),
                  hintMove.getSource().getY(),
                  hintMove.getDest().getX(),
                  hintMove.getDest().getY()));

      Game.getInstance().getGameState().setHintIntegers(hintIntegers);
      return Optional.empty();
    } catch (Exception e) {
      return Optional.of(e);
    }
  }
}
