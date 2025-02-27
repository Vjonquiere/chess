package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.Game;

public class SaveGameCommand implements Command {
  private static String DEFAULT_FILE_PATH = "save.txt";

  private String filepath;

  public SaveGameCommand(String filepath) {
    if (filepath.isEmpty()) {
      this.filepath = DEFAULT_FILE_PATH;
    } else {
      this.filepath = filepath;
    }
  }

  /**
   * Executes the SaveGameCommand which attempts to save the current game to a file.
   *
   * @param model The game model which is to be saved.
   * @param controller The game controller which is currently managing the commands.
   * @return An Optional containing an exception if an error occurred, otherwise an empty Optional
   *     if the save was successful.
   */
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
