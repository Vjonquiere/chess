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

  @Override
  public void execute(Game model, GameController controller) {
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }
}
