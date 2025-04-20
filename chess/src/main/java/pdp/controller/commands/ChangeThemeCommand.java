package pdp.controller.commands;

import static pdp.utils.Logging.error;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.events.EventType;
import pdp.model.GameAbstract;

/**
 * Part of Command Design pattern. Creates a command to update the view (GUI) after a change of
 * theme.
 */
public class ChangeThemeCommand implements Command {

  /**
   * Change the theme in the GUI.
   *
   * @param model The game model on which the move is to be executed.
   * @param controller The game controller managing game commands.
   */
  @Override
  public Optional<Exception> execute(GameAbstract model, GameController controller) {
    try {
      model.notifyObservers(EventType.UPDATE_THEME);
      return Optional.empty();
    } catch (Exception e) {
      error(e.getMessage());
      return Optional.of(e);
    }
  }
}
