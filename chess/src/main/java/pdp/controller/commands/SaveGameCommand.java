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
    try {
      model.saveGame(filepath);
      return Optional.empty();
    } catch (Exception e) {
      return Optional.of(e);
    }
  }
}
