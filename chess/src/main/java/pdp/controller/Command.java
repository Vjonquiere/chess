package pdp.controller;

import java.util.Optional;
import pdp.model.Game;

public interface Command {
  public Optional<Exception> execute(Game model, GameController controller);
}
