package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.exceptions.CommandNotAvailableNowException;
import pdp.model.Game;

public class SurrenderCommand implements Command {

  private boolean isWhite;

  public SurrenderCommand(boolean isWhite) {
    this.setWhite(isWhite);
  }

  /**
   * Executes the SurrenderCommand which attempts to make the current player lose the game.
   *
   * @param model the Game model on which the command is executed.
   * @param controller the GameController managing the game commands.
   * @return an Optional containing an exception if the command is not available or empty if
   *     successful.
   */
  @Override
  public Optional<Exception> execute(Game model, GameController controller) {
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

  public boolean isWhite() {
    return isWhite;
  }

  public void setWhite(boolean white) {
    isWhite = white;
  }
}
