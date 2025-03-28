package pdp.model.board;

import java.util.List;
import java.util.logging.Logger;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.utils.Logging;
import pdp.utils.Position;

/** Board structure to be used in game states. */
public class Board {
  private static final Logger LOGGER = Logger.getLogger(Board.class.getName());
  private BoardRepresentation board;

  static {
    Logging.configureLogging(LOGGER);
  }

  /** Creates a default board. */
  public Board() {
    this.setBoard(new BitboardRepresentation());
  }

  /**
   * Create a board from a given board state (support FileBoard header).
   *
   * @param board The board state to use
   */
  public Board(FileBoard board) {
    this.setBoard(new BitboardRepresentation(board));
  }

  public List<Move> getAvailableMoves(Position pos) {
    return board.getAvailableMoves(pos);
  }

  public boolean getPlayer() {
    return this.board.getPlayer();
  }

  public void setPlayer(boolean isWhite) {
    this.board.setPlayer(isWhite);
  }

  /**
   * Executes a given move on the board, handling captures, en passant, castling, pawn promotion.
   *
   * @param move The move to be executed
   */
  public void makeMove(Move move) {
    board.makeMove(move);
  }

  /**
   * Creates a deep copy of this Board object. Copies all attributes to create a new independent
   * Board instance.
   *
   * @return A new instance of Board with the same state as the current object.
   */
  public Board getCopy() {
    Board copy = new Board();
    copy.setBoard(this.getBoardRep().getCopy());
    return copy;
  }

  public BoardRepresentation getBoardRep() {
    return this.board;
  }

  /**
   * Generates an ASCII representation of the chess board.
   *
   * <p>White pieces are represented by uppercase characters and black pieces by lowercase
   * characters. A1 is the bottom-left corner of the board ([7][0]).
   *
   * @return a 2D array of characters representing the chess board.
   */
  public char[][] getAsciiRepresentation() {
    return board.getAsciiRepresentation();
  }

  public int getNbFullMovesWithNoCaptureOrPawn() {
    return board.getNbFullMovesWithNoCaptureOrPawn();
  }

  /**
   * Checks if castle (long or short in parameter) for one side is possible or not. No need to fetch
   * king position because if king has moved, then boolean attributes for castling rights are false
   *
   * @param color the color of the player we want to test castle for
   * @param shortCastle boolean value to indicate if we're looking for the short castle right or
   *     long castle right
   * @return true if castle {shortCastle} is possible for player of Color {color}. false otherwise
   */
  public boolean canCastle(Color color, boolean shortCastle) {
    return getBoardRep().canCastle(color, shortCastle);
  }

  /**
   * Applies long or short castle to {color} according to the boolean value given in parameter.
   * Assumes castle is possible
   *
   * @param color color for which castling move is applied
   */
  public void applyCastle(Color color, boolean shortCastle) {
    board.applyCastle(color, shortCastle);
  }

  /**
   * Get the castling rights of the board.
   *
   * @return An array that contains castling rights
   */
  public boolean[] getCastlingRights() {
    return board.getCastlingRights();
  }

  private void setBoard(BoardRepresentation board) {
    this.board = board;
  }

  public Position getEnPassantPos() {
    return board.getEnPassantPos();
  }

  public void setEnPassantPos(Position enPassantPos) {
    board.setEnPassantPos(enPassantPos);
  }

  public boolean isLastMoveDoublePush() {
    return board.isLastMoveDoublePush();
  }

  public void setLastMoveDoublePush(boolean lastMoveDoublePush) {
    board.setLastMoveDoublePush(lastMoveDoublePush);
  }

  public boolean isWhiteShortCastle() {
    return board.isWhiteShortCastle();
  }

  public void setWhiteShortCastle(boolean whiteShortCastle) {
    board.setWhiteShortCastle(whiteShortCastle);
  }

  public boolean isBlackShortCastle() {
    return board.isBlackShortCastle();
  }

  public void setBlackShortCastle(boolean blackShortCastle) {
    board.setBlackShortCastle(blackShortCastle);
  }

  public boolean isWhiteLongCastle() {
    return board.isWhiteLongCastle();
  }

  public void setWhiteLongCastle(boolean whiteLongCastle) {
    board.setWhiteLongCastle(whiteLongCastle);
  }

  public boolean isBlackLongCastle() {
    return board.isBlackLongCastle();
  }

  public void setBlackLongCastle(boolean blackLongCastle) {
    board.setBlackLongCastle(blackLongCastle);
  }

  public boolean isEnPassantTake() {
    return board.isEnPassantTake();
  }

  public void setEnPassantTake(boolean enPassantTake) {
    board.setEnPassantTake(enPassantTake);
  }

  public int getNbMovesWithNoCaptureOrPawn() {
    return board.getNbMovesWithNoCaptureOrPawn();
  }

  public void setNbMovesWithNoCaptureOrPawn(int newVal) {
    board.setNbMovesWithNoCaptureOrPawn(newVal);
  }
}
