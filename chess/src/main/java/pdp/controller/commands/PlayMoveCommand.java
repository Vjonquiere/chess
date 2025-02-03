package pdp.controller.commands;

import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.Game;
import pdp.model.Move;

public class PlayMoveCommand implements Command {
  private Move move;

  public PlayMoveCommand(String move) {
    this.move = Move.fromString(move);
  }

  /**
   * Executes the move on the game model.
   *
   * @param model The game model on which the move is to be executed.
   * @param controller The game controller managing game operations.
   */
  @Override
  public Optional<Exception> execute(Game model, GameController controller) {
    boolean result = model.playMove(this.move);
    if (result) {
      return Optional.empty();
    }
    return Optional.of(new IllegalMoveException("Illegal move: " + this.move));
  }
}
