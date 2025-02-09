package pdp.model;

import java.util.List;
import java.util.logging.Logger;
import pdp.events.EventObserver;
import pdp.events.EventType;
import pdp.events.Subject;
import pdp.exceptions.IllegalMoveException;
import pdp.model.ai.Solver;
import pdp.utils.Logging;
import pdp.utils.Position;
import pdp.utils.TextGetter;

public class Game extends Subject {
  private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
  private static Game instance;
  private GameState gameState;
  private Solver solver;
  private boolean isWhiteAI;
  private boolean isBlackAI;

  private Game(boolean isWhiteAI, boolean isBlackAI, Solver solver, GameState gameState) {
    Logging.configureLogging(LOGGER);
    this.isWhiteAI = isWhiteAI;
    this.isBlackAI = isBlackAI;
    this.solver = solver;
    this.gameState = gameState;
  }

  public Board getBoard() {
    return this.gameState.getBoard();
  }

  public GameState getGameState() {
    return this.gameState;
  }

  /**
   * Adds an observer to the game and immediately notifies a GAME_STARTED event.
   *
   * @param observer The observer to be added.
   */
  @Override
  public void addObserver(EventObserver observer) {
    super.addObserver(observer);
    this.notifyObserver(observer, EventType.GAME_STARTED);
  }

  /**
   * Creates a new instance of the Game class and stores it in the instance variable.
   *
   * @param isWhiteAI Whether the white player is an AI.
   * @param isBlackAI Whether the black player is an AI.
   * @param solver The solver to be used for AI moves.
   * @return The newly created instance of Game.
   */
  public static Game initialize(boolean isWhiteAI, boolean isBlackAI, Solver solver, Timer timer) {
    instance = new Game(isWhiteAI, isBlackAI, solver, new GameState());
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
      List<Move> availableMoves = this.gameState.getBoard().getAvailableMoves(sourcePosition);
      Move classicalMove =
          move.isMoveClassical(
              availableMoves); // throws exception if the initial move is not a "classical" move (
      // and we verify in the catch section if the move is a special move :
      // castling, en-passant)
      // here, the move is a "classical" move, but we must verify if the played piece is nailed or
      // not, if the king will be in check after this move, if a pawn have to be promoted..
      // veriufier clouage , echec, puis si pion promotion, puis si tout est bon alors jouer le move
      // dans la board ..

      // classicalMove.piece.isPinned()  verifie le clouage en jouant le move et verifiant si c'est
      // tjr check (permets egalement de refuser les mouvements qui ne defendent pas
      // -d'une attaque a leur roi ) donc appeler la fonction autre que isPinned ( par exemple
      // isCheckAfterMove) qui doit throws un illegalMoveException si le roi est echec apres le move
      // if classicalMove.piece == Pawn -> isPromoted()  verifie si un pion est arrivé en derniere
      // rangé
      // board.board.isCheck  pas besoin car la fonction isCheckAfterMove verifie deja cela

      this.gameState.getBoard().makeMove(classicalMove);
      // ajouter a l'historique le move

    } catch (Exception e) {
      // dans cette section la variable classicalMove n'est pas définie
      // verifie si echec et mat ou pat et plus generalement si la partie est finie, si oui terminer
      // partie en consequence

      // raisons pôur laquelle on se trouve ici, move joué : roque, en passant ou coup illégal
      // si getPieceAt(move.source.getX, move.source.getY) == king -> verifier si le coup joué etait
      // un roque en comaparant les positions de source et destination avec ceux connus des roques
      // et faire le roque si ca correspond en appelant la methode correspondante
      // si getPieceAt(move.source.getX, move.source.getY) == pawn -> verifier si un en passant est
      // possible en verifiant si le coup precedent etait un coup d'un pion avancant de deux cases
      // si c'est la cas alors comparé le move.dest( et surtout pas classicalMove.dest) avec (la
      // position du pion qui a avancé de deux cases)-1 en abcisses ou +1 ca depend du sens
      // (donc la case juste derriere ce pion par rapport a ce sens de marche) ci cette comparaison
      // est equals alors faire le en passant en appelant la methode correspondante
      // else throws message d'erreur

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

  /**
   * Returns a string representation of the game. Includes the ASCII representation of the board,
   * the time remaining (if timer is not null), and the color of the player to play.
   *
   * @return A string representation of the game.
   */
  public String getGameRepresentation() {
    char[][] board = this.gameState.getBoard().getAsciiRepresentation();
    StringBuilder sb = new StringBuilder();

    Timer timer = gameState.getMoveTimer();
    if (timer != null) {
      sb.append(TextGetter.getText("timeRemaining", timer.getTimeRemainingString()));
    }

    sb.append("\n");

    int size = board.length;

    for (int row = 0; row < size; row++) {
      sb.append(size - row).append(" | ");
      for (int col = 0; col < size; col++) {
        sb.append(board[row][col]).append(" ");
      }
      sb.append("\n");
    }

    sb.append("    "); // Offset for row numbers
    for (int i = 0; i < size; i++) {
      sb.append("-").append(" ");
    }
    sb.append("\n    ");
    for (char c = 'A'; c < 'A' + size; c++) {
      sb.append(c).append(" ");
    }
    sb.append("\n\n");

    sb.append(
        TextGetter.getText(
            "toPlay",
            gameState.isWhiteTurn() ? TextGetter.getText("white") : TextGetter.getText("black")));
    sb.append("\n");

    return sb.toString();
  }

  public static Game getInstance() {
    if (instance == null) {
      instance = new Game(false, false, null, new GameState());
    }
    return instance;
  }
}
