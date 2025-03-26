package pdp.controller.commands;

import java.util.Optional;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.exceptions.CommandNotAvailableNowException;
import pdp.model.Game;
import pdp.model.board.Move;

/** Part of Command Design pattern. Creates a command to play the given move. */
public class PlayMoveCommand implements Command {
  private final String move;

  public PlayMoveCommand(String move) {
    this.move = move;
  }

  /**
   * Executes the move on the game model.
   *
   * @param model The game model on which the move is to be executed.
   * @param controller The game controller managing game commands.
   */
  @Override
  public Optional<Exception> execute(Game model, GameController controller) {
    if (model.getGameState().isGameOver()) {
      return Optional.of(new CommandNotAvailableNowException());
    }
    try {
      model.playMove(Move.fromString(this.move, Game.getInstance().getGameState().isWhiteTurn()));
      return Optional.empty();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return Optional.of(e);
    }
  }
}
