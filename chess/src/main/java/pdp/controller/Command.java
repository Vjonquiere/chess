package pdp.controller;

import java.util.Optional;
import pdp.model.Game;

/** Interface needed for the design pattern Command. */
public interface Command {
  public Optional<Exception> execute(Game model, GameController controller);
}
