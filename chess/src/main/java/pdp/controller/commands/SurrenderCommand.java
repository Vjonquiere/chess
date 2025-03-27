package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.exceptions.CommandNotAvailableNowException;
import pdp.model.Game;

/**
 * Part of Command Design pattern. Creates a command for the surrender of the player of the color
 * given in parameters.
 *
 * @param isWhite true if the player is white, false if he is black
 */
public record SurrenderCommand(boolean isWhite) implements Command {

  /**
   * Executes the SurrenderCommand which attempts to make the current player lose the game.
   *
   * @param model the Game model on which the command is executed.
   * @param controller the GameController managing the game commands.
   * @return an Optional containing an exception if the command is not available or empty if
   *     successful.
   */
  @Override
  public Optional<Exception> execute(final Game model, GameController controller) {
    if (model.getGameState().isGameOver()) {
      return Optional.of(new CommandNotAvailableNowException());
    }
    if (isWhite()) {
      model.getGameState().whiteResigns();
    } else {
      model.getGameState().blackResigns();
    }
    return Optional.empty();
  }
}
