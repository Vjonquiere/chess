package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.exceptions.CommandNotAvailableNowException;
import pdp.model.GameInterface;

/**
 * Part of Command Design pattern. Creates a command for the cancellation of the draw request of the
 * player of the color given in parameters.
 *
 * @param isWhite true if the player is white, false if he is black
 */
public record CancelDrawCommand(boolean isWhite) implements Command {

  /**
   * Executes the CancelDrawCommand which attempts to cancel a draw request in the game. If the game
   * is already over, it returns an exception CommandNotAvailableNowException.
   *
   * @param model the Game model on which the command is executed
   * @param controller the GameController managing the game commands
   * @return an Optional containing an exception if the command is not available, otherwise an empty
   *     Optional
   */
  @Override
  public Optional<Exception> execute(final GameInterface model, GameController controller) {
    if (model.getGameState().isGameOver()) {
      return Optional.of(new CommandNotAvailableNowException());
    }
    if (isWhite()) {
      model.getGameState().whiteCancelsDrawRequest();
    } else {
      model.getGameState().blackCancelsDrawRequest();
    }
    return Optional.empty();
  }
}
