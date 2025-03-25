package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.exceptions.CommandNotAvailableNowException;
import pdp.model.Game;

/**
 * Part of Command Design pattern. Creates a command for the proposition of a draw by the player of
 * the color given in parameters.
 */
public class ProposeDrawCommand implements Command {

  private final boolean isWhite;

  public ProposeDrawCommand(boolean isWhite) {
    this.isWhite = isWhite;
  }

  /**
   * Executes the ProposeDrawCommand which attempts to propose a draw in the game. If the game is
   * already over, it returns an exception CommandNotAvailableNowException.
   *
   * @param model the Game model on which the command is executed.
   * @param controller the GameController managing the game commands.
   * @return an Optional containing an exception if an error occured, otherwise an empty Optional.
   */
  @Override
  public Optional<Exception> execute(Game model, GameController controller) {
    if (model.getGameState().isGameOver()) {
      return Optional.of(new CommandNotAvailableNowException());
    }
    if (isWhite) {
      model.getGameState().whiteWantsToDraw();
    } else {
      model.getGameState().blackWantsToDraw();
    }
    return Optional.empty();
  }
}
