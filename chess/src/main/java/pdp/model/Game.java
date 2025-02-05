package pdp.model;

import java.util.List;
import pdp.events.Subject;
import pdp.exceptions.IllegalMoveException;
import pdp.model.ai.Solver;
import pdp.utils.Position;

public class Game extends Subject {
  private static Game instance;
  private Timer timer;
  private boolean isTimed;
  private Board board;
  private History history;
  private Solver solver;
  private boolean isWhiteAI;
  private boolean isBlackAI;

  private Game(
      boolean isWhiteAI,
      boolean isBlackAI,
      Solver solver,
      boolean isTimed,
      Timer timer,
      History history) {
    this.isWhiteAI = isWhiteAI;
    this.isBlackAI = isBlackAI;
    this.solver = solver;
    this.isTimed = isTimed;
    this.timer = timer;
    this.history = history;
  }

  /**
   * Creates a new instance of the Game class and stores it in the instance variable.
   *
   * @param isWhiteAI Whether the white player is an AI.
   * @param isBlackAI Whether the black player is an AI.
   * @param solver The solver to be used for AI moves.
   * @param isTimed Whether there is a time limit for the game.
   * @param timer The timer to be used if there is a time limit.
   * @param history The history of moves made during the game.
   * @return The newly created instance of Game.
   */
  public static Game initialize(
      boolean isWhiteAI,
      boolean isBlackAI,
      Solver solver,
      boolean isTimed,
      Timer timer,
      History history) {
    instance = new Game(isWhiteAI, isBlackAI, solver, isTimed, timer, history);
    return instance;
  }

  /**
   * Tries to play the given move on the game.
   *
   * @param move The move to be executed.
   * @throws IllegalMoveException If the move is not legal.
   */
  public void playMove(Move move) throws IllegalMoveException {
    Position sourcePosition = new Position(move.source.getY(), move.source.getX());
    try {
    List<Move> availableMoves = board.getAvailableMoves(sourcePosition);
    move.isLegal(availableMoves);  //throws exception if the initial move is not a "classical" move ( and we verify in the catch section if the move is a special move : castling, en-passant, )
      //here, the move is a "classical" move, but we must verify if the played piece is nailed or not, if the king will be in check after this move, if a pawn have to be promoted.. 
      //veriufier clouage , echec, puis si pion promotion, puis si tout est bon alors jouer le move dans la board ..

    board.makeMove(move);
    //ajouter a l'historique le move

    } catch (Exception e) {
      board.isCheck()
      // roque, en passant etc
      // TODO: handle exception
    }

    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public List<Move> getMovesHistory() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public String getStringHistory() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public void resetGame() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public boolean isOver() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public String getGameRepresentation() {
    StringBuilder sb = new StringBuilder();

    if (this.isTimed) {
      sb.append("Played with time remaining: ");
      sb.append(this.timer.timeRemainingString());
      sb.append("\n\n");
    }

    sb.append(this.board.getAsciiRepresentation());

    sb.append("\n\n");

    sb.append("To play: ");
    sb.append(this.board.isWhite ? "White" : "Black");

    return sb.toString();
  }

  public static Game getInstance() {
    // TODO
    throw new UnsupportedOperationException("Method not implemented");
  }
}
