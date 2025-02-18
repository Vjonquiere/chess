package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.Game;

public class CancelMoveCommand implements Command {
  @Override
  public Optional<Exception> execute(Game model, GameController controller) {
    try {
      model.previousState();
      return Optional.empty();
    } catch (Exception e) {
      return Optional.of(e);
    }
  }
}
