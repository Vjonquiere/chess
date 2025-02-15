package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.exceptions.CommandNotAvailableNowException;
import pdp.model.Game;
import pdp.model.board.Move;

public class PlayMoveCommand implements Command {
  private String move;

  public PlayMoveCommand(String move) {
    this.move = move;
  }

  /**
   * Executes the move on the game model.
   *
   * @param model The game model on which the move is to be executed.
   * @param controller The game controller managing game operations.
   */
  @Override
  public Optional<Exception> execute(Game model, GameController controller) {
    if (model.getGameState().isGameOver()) {
      return Optional.of(new CommandNotAvailableNowException());
    }
    try {
      model.playMove(Move.fromString(this.move));
      return Optional.empty();
    } catch (Exception e) {
      return Optional.of(e);
    }
  }
}
