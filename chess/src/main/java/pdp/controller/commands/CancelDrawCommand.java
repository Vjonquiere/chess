package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.exceptions.CommandNotAvailableNowException;
import pdp.model.Game;

public class CancelDrawCommand implements Command {

  private boolean isWhite;

  public CancelDrawCommand(boolean isWhite) {
    this.setWhite(isWhite);
  }

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
  public Optional<Exception> execute(Game model, GameController controller) {
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

  public boolean isWhite() {
    return isWhite;
  }

  public void setWhite(boolean white) {
    isWhite = white;
  }
}
