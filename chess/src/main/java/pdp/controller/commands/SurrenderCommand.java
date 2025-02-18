package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.exceptions.CommandNotAvailableNowException;
import pdp.model.Game;

public class SurrenderCommand implements Command {

  boolean isWhite;

  public SurrenderCommand(boolean isWhite) {
    this.isWhite = isWhite;
  }

  @Override
  public Optional<Exception> execute(Game model, GameController controller) {
    if (model.getGameState().isGameOver()) {
      return Optional.of(new CommandNotAvailableNowException());
    }
    if (isWhite) {
      model.getGameState().whiteResigns();
    } else {
      model.getGameState().blackResigns();
    }
    return Optional.empty();
  }
}
