package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.Game;

/** Part of Command Design pattern. Creates a command used to save the game into a file. */
public class SaveGameCommand implements Command {
  /** Default file path to save a game into. */
  private static final String DEFAULT_FILE_PATH = "save.txt";

  /** Path where the file will be saved. */
  private final String filepath;

  /**
   * Assigns the filepath to save the game into.
   *
   * @param filepath string containing a path to save the game or empty string to use default
   */
  public SaveGameCommand(final String filepath) {
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
  public Optional<Exception> execute(final Game model, GameController controller) {
    try {
      model.saveGame(filepath);
      return Optional.empty();
    } catch (Exception e) {
      return Optional.of(e);
    }
  }
}
