package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.Game;

public class LoadGameCommand implements Command {
  private String filepath;

  @Override
  public Optional<Exception> execute(Game model, GameController controller) {
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }
}
