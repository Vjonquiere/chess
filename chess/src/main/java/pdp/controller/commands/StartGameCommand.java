package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.Game;

/**
 * Part of Command Design pattern. Starts the AI player. Useful in GUI where the AI player white is
 * not playing until asked to do so at the start of the game.
 */
public class StartGameCommand implements Command {
  @Override
  public Optional<Exception> execute(final Game model, GameController controller) {
    try {
      model.startAi();
      return Optional.empty();
    } catch (Exception e) {
      return Optional.of(e);
    }
  }
}
