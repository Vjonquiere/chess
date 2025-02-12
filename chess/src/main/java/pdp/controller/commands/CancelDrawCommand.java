package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.exceptions.CommandNotAvailableNowException;
import pdp.model.Game;

public class CancelDrawCommand implements Command {

  boolean isWhite;

  public CancelDrawCommand(boolean isWhite) {
    this.isWhite = isWhite;
  }

  @Override
  public Optional<Exception> execute(Game model, GameController controller) {
    if (model.getGameState().isGameOver()) {
      return Optional.of(new CommandNotAvailableNowException());
    }
    if (isWhite) {
      model.getGameState().whiteCancelsDrawRequest();
    } else {
      model.getGameState().blackCancelsDrawRequest();
    }
    return Optional.empty();
  }
}
