package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.Game;

public class CancelMoveCommand implements Command {
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
      model.previousState();
      return Optional.empty();
    } catch (Exception e) {
      return Optional.of(e);
    }
  }
}
