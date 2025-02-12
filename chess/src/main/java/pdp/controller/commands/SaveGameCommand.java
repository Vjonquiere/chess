package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.Game;

public class SaveGameCommand implements Command {
  private String filepath;

  public SaveGameCommand(String filepath) {
    this.filepath = filepath;
  }

  @Override
  public Optional<Exception> execute(Game model, GameController controller) {
    return Optional.of(
        new UnsupportedOperationException(
            "Method not implemented in " + this.getClass().getName()));
  }
}
