package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.Game;

public class RestoreMoveCommand implements Command {
  /**
   * Reverts the last cancelled move in the game model if possible.
   *
   * @param model The game model on which the command is executed.
   * @param controller The game controller managing game commands.
   * @return An Optional containing an exception if an error occurred or empty if successful.
   */
  @Override
  public Optional<Exception> execute(Game model, GameController controller) {
    try {
      model.nextState();
      return Optional.empty();
    } catch (Exception e) {
      return Optional.of(e);
    }
  }
}
