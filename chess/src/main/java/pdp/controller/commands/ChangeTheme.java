package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.events.EventType;
import pdp.model.Game;

public class ChangeTheme implements Command {

  public ChangeTheme() {}

  /**
   * Executes the move on the game model.
   *
   * @param model The game model on which the move is to be executed.
   * @param controller The game controller managing game commands.
   */
  @Override
  public Optional<Exception> execute(Game model, GameController controller) {
    try {
      Game.getInstance().notifyObservers(EventType.UPDATE_THEME);
      return Optional.empty();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return Optional.of(e);
    }
  }
}
