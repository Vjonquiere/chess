package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.Game;

public class RestartCommand implements Command {
  /**
   * Executes the RestartCommand which attempts to restart the game.
   *
   * @param model the Game model on which the command is executed.
   * @param controller the GameController managing the game commands.
   * @return an Optional containing an exception if an error occurred, or empty if successful
   */
  @Override
  public Optional<Exception> execute(Game model, GameController controller) {
    try {
      model.restartGame();
      return Optional.empty();
    } catch (Exception e) {
      return Optional.of(e);
    }
  }
}
