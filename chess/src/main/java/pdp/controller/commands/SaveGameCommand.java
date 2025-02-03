package pdp.controller.commands;

import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.Game;

public class SaveGameCommand implements Command {
  private String filepath;

  public SaveGameCommand(String filepath) {
    this.filepath = filepath;
  }

  @Override
  public void execute(Game model, GameController controller) {
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }
}
