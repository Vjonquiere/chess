package pdp.model;

import static pdp.utils.Logging.DEBUG;

import java.util.HashMap;
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
  private static ZobristHashing zobristHashing = new ZobristHashing();
  private GameState gameState;
  private Solver solver;
  private boolean isWhiteAI;
  private boolean isBlackAI;
  private History history;
  private HashMap<Long, Integer> stateCount;

  private Game(
      boolean isWhiteAI, boolean isBlackAI, Solver solver, GameState gameState, History history) {
    Logging.configureLogging(LOGGER);
    this.isWhiteAI = isWhiteAI;
    this.isBlackAI = isBlackAI;
    this.solver = solver;
    this.gameState = gameState;
    this.history = history;
    this.stateCount = new HashMap<>();
    // this.gameState.setZobristHashing(zobristHashing.generateHashFromBitboards(this.gameState.getBoard()));
    this.gameState.setSimplifiedZobristHashing(
        zobristHashing.generateSimplifiedHashFromBitboards(this.gameState.getBoard()));
    this.addStateToCount(this.gameState.getSimplifiedZobristHashing());
  }

  private boolean addStateToCount(long simplifiedZobristHashing) {
    if (this.stateCount.containsKey(simplifiedZobristHashing)) {
      this.stateCount.put(
          simplifiedZobristHashing, this.stateCount.get(simplifiedZobristHashing) + 1);
      if (this.stateCount.get(simplifiedZobristHashing) == 3) {
        DEBUG(LOGGER, "State with hash " + simplifiedZobristHashing + " has been repeated 3 times");
        return true;
      }
      return false;
    } else {
      this.stateCount.put(simplifiedZobristHashing, 1);
      return false;
    }
  }

  public Board getBoard() {
    return this.gameState.getBoard();
  }

  public GameState getGameState() {
    return this.gameState;
  }

  public History getHistory() {
    return this.history;
  }

  /**
   * Adds an observer to the game and game state and immediately notifies a GAME_STARTED event.
   *
   * @param observer The observer to be added.
   */
  @Override
  public void addObserver(EventObserver observer) {
    super.addObserver(observer);
    if (gameState != null) {
      this.gameState.addObserver(observer);
    }
    this.notifyObserver(observer, EventType.GAME_STARTED);
  }

  /**
   * Adds an observer to the game and game state that listens for error events.
   *
   * @param observer The observer to be added.
   */
  @Override
  public void addErrorObserver(EventObserver observer) {
    super.addErrorObserver(observer);
    if (gameState != null) {
      this.gameState.addErrorObserver(observer);
    }
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
    instance = new Game(isWhiteAI, isBlackAI, solver, new GameState(), new History());
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
    Position destPosition = new Position(move.dest.getY(), move.dest.getX());
    try {
      if ((this.gameState.getBoard().board.getPieceAt(move.source.getX(), move.source.getY()).color
                  == Color.WHITE
              && !this.gameState.getBoard().isWhite)
          || this.gameState
                      .getBoard()
                      .board
                      .getPieceAt(move.source.getX(), move.source.getY())
                      .color
                  == Color.BLACK
              && this.gameState.getBoard().isWhite) {
        throw new IllegalMoveException("Not your piece " + move.toString());
      }

      List<Move> availableMoves = this.gameState.getBoard().getAvailableMoves(sourcePosition);
      Move classicalMove = move.isMoveClassical(availableMoves);

      // throws exception if the initial move is not a "classical" move (
      // and we verify in the catch section if the move is a special move :
      // castling, en-passant, doublePushPawn)
      // here, the move is a "classical" move, but we must verify if the king will
      // be in check after the move

      if (this.gameState
          .getBoard()
          .board
          .isCheckAfterMove(
              this.gameState.getBoard().isWhite ? Color.WHITE : Color.BLACK, classicalMove)) {
        throw new IllegalMoveException("Move puts the king in check " + classicalMove.toString());
      }

      if (this.gameState.getBoard().isWhite) {
        this.gameState.incrementsFullTurn();
      }

      this.history.addMove(
          new HistoryState(
              classicalMove.toString(),
              this.gameState.getFullTurn(),
              this.gameState.getBoard().isWhite));
      this.gameState.getBoard().makeMove(classicalMove);

      this.gameState.setSimplifiedZobristHashing(
          zobristHashing.updateSimplifiedHashFromBitboards(
              this.gameState.getSimplifiedZobristHashing(), getBoard(), classicalMove));

      boolean threefoldRepetition =
          this.addStateToCount(this.gameState.getSimplifiedZobristHashing());
      if (threefoldRepetition) {
        this.gameState.activateThreefold();
      }
      // Check game status after the classical move was played
      this.gameState.switchPlayerTurn();
      this.gameState.checkGameStatus();
      this.notifyObservers(EventType.MOVE_PLAYED);

    } catch (Exception e) {
      boolean isSpecialMove = false;

      // Castle move
      Piece isPieceKing =
          this.gameState
              .getBoard()
              .board
              .getPieceAt(sourcePosition.getX(), sourcePosition.getY())
              .piece;
      if (isPieceKing == Piece.KING) {
        if ((Math.abs(destPosition.getX() - sourcePosition.getX()) == 2
                && sourcePosition.getY() == 0
                && destPosition.getY() == 0)
            || (Math.abs(destPosition.getX() - sourcePosition.getX()) == 2
                && sourcePosition.getY() == 7
                && destPosition.getY() == 7)) {
          boolean shortCastleIsAsked = destPosition.getX() > sourcePosition.getX();
          Color color = this.gameState.getBoard().isWhite ? Color.WHITE : Color.BLACK;
          // Check if castle is possible
          if (this.gameState.getBoard().canCastle(color, shortCastleIsAsked)) {
            // If castle is possible then apply changes
            if (this.gameState.getBoard().isWhite) {
              this.gameState.incrementsFullTurn();
            }
            this.gameState.getBoard().applyCastle(color, shortCastleIsAsked);
            isSpecialMove = true;

            this.history.addMove(
                new HistoryState(
                    move.toString(),
                    this.gameState.getFullTurn(),
                    this.gameState.getBoard().isWhite));
          }
        }
      }

      // enPassant move
      if (this.gameState.getBoard().isLastMoveDoublePush
          && this.gameState
              .getBoard()
              .board
              .isEnPassant(
                  this.gameState.getBoard().enPassantPos.getX(),
                  this.gameState.getBoard().enPassantPos.getY(),
                  move,
                  this.gameState.getBoard().isWhite)) {
        if (this.gameState
            .getBoard()
            .board
            .isCheckAfterMove(
                this.gameState.getBoard().isWhite ? Color.WHITE : Color.BLACK, move)) {
          throw new IllegalMoveException(
              "enPassant move puts the king in check " + move.toString());
        }

        isSpecialMove = true;
        this.gameState.getBoard().enPassantPos = null;
        this.gameState.getBoard().isEnPassantTake = true;

        if (this.gameState.getBoard().isWhite) {
          this.gameState.incrementsFullTurn();
        }
        this.history.addMove(
            new HistoryState(
                move.toString(), this.gameState.getFullTurn(), this.gameState.getBoard().isWhite));
        this.gameState.getBoard().makeMove(move);
      }

      // DoublePawnPush move
      if (this.gameState
          .getBoard()
          .board
          .isDoublePushPossible(move, this.gameState.getBoard().isWhite)) {
        if (this.gameState
            .getBoard()
            .board
            .isCheckAfterMove(
                this.gameState.getBoard().isWhite ? Color.WHITE : Color.BLACK, move)) {
          throw new IllegalMoveException(
              "This doublePawnPush puts the king in check " + move.toString());
        }

        isSpecialMove = true;
        this.gameState.getBoard().enPassantPos =
            this.gameState.getBoard().isWhite
                ? new Position(move.dest.getY() - 1, move.dest.getX())
                : new Position(move.dest.getY() + 1, move.dest.getX());
        if (this.gameState.getBoard().isWhite) {
          this.gameState.incrementsFullTurn();
        }
        history.addMove(
            new HistoryState(
                move.toString(), this.gameState.getFullTurn(), this.gameState.getBoard().isWhite));
        this.gameState.getBoard().makeMove(move);

        this.gameState.getBoard().isLastMoveDoublePush = true;
      }

      this.gameState.setSimplifiedZobristHashing(
          zobristHashing.generateSimplifiedHashFromBitboards(this.gameState.getBoard()));

      boolean threefoldRepetition =
          this.addStateToCount(this.gameState.getSimplifiedZobristHashing());
      if (threefoldRepetition) {
        this.gameState.activateThreefold();
      }

      // Check game status after the special move was played
      this.gameState.switchPlayerTurn();
      this.gameState.checkGameStatus();
      this.notifyObservers(EventType.MOVE_PLAYED);

      if (!isSpecialMove) {
        throw new IllegalMoveException(e.getMessage() + " and not a special move");
        // throw new IllegalMoveException(e.getMessage(), e );
      }
      // In this section, the variable 'classicalMove' is not defined.
      // Checks if it is checkmate or stalemate, and more generally if the game is over. If so, end
      // the game accordingly.

      // Reasons for being here: the move played could be castling, en passant,doublePawnPush or an
      // illegal move.

    }
  }

  /**
   * Retrieves the history of moves in the current game as a formatted string.
   *
   * @return A string representation of the game's move history.
   */
  public String getStringHistory() {
    return this.history.toString();
  }

  public void resetGame() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public boolean isOver() {
    return this.gameState.isGameOver();
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

    if (!this.gameState.isGameOver()) {
      sb.append(
          TextGetter.getText(
              "toPlay",
              gameState.isWhiteTurn() ? TextGetter.getText("white") : TextGetter.getText("black")));
    } else {
      sb.append(TextGetter.getText("gameOver"));
    }

    sb.append("\n");

    return sb.toString();
  }

  public static Game getInstance() {
    if (instance == null) {
      instance = new Game(false, false, null, new GameState(), new History());
    }
    return instance;
  }
}
