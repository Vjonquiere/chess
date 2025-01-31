package pdp.controller.commands;

import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.Game;
import pdp.utils.Position;

public class PlayMoveCommand implements Command {
  private Position source;
  private Position dest;

  @Override
  public void execute(Game model, GameController controller) {
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }
}
