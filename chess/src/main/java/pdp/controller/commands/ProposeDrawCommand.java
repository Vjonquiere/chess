package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.exceptions.CommandNotAvailableNowException;
import pdp.model.Game;

public class ProposeDrawCommand implements Command {

  boolean isWhite;

  public ProposeDrawCommand(boolean isWhite) {
    this.isWhite = isWhite;
  }

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
