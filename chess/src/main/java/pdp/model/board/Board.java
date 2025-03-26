package pdp.model.board;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
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
    this.setBoard(board.board());
    this.setPlayer(board.isWhiteTurn());

    if (board.header() != null) { // Initialize board with header values
      this.setEnPassantPos(board.header().enPassant());
      if (this.getEnPassantPos() != null) {
        this.setLastMoveDoublePush(true);
      }
      this.setWhiteShortCastle(board.header().whiteKingCastling());
      this.setBlackShortCastle(board.header().blackKingCastling());
      this.setWhiteLongCastle(board.header().whiteQueenCastling());
      this.setBlackLongCastle(board.header().blackQueenCastling());
      this.setNbMovesWithNoCaptureOrPawn(board.header().fiftyMoveRule());
    } else { // No header -> default values
      this.setEnPassantPos(null);
      this.setLastMoveDoublePush(false);
      this.setWhiteShortCastle(
          board.board().getPieceAt(7, 0).equals(new ColoredPiece(Piece.ROOK, Color.WHITE))
              && board.board().getPieceAt(4, 0).equals(new ColoredPiece(Piece.KING, Color.WHITE)));
      this.setBlackShortCastle(
          board.board().getPieceAt(7, 7).equals(new ColoredPiece(Piece.ROOK, Color.BLACK))
              && board.board().getPieceAt(4, 7).equals(new ColoredPiece(Piece.KING, Color.BLACK)));
      this.setWhiteLongCastle(
          board.board().getPieceAt(0, 0).equals(new ColoredPiece(Piece.ROOK, Color.WHITE))
              && board.board().getPieceAt(4, 0).equals(new ColoredPiece(Piece.KING, Color.WHITE)));
      this.setBlackLongCastle(
          board.board().getPieceAt(0, 7).equals(new ColoredPiece(Piece.ROOK, Color.BLACK))
              && board.board().getPieceAt(4, 7).equals(new ColoredPiece(Piece.KING, Color.BLACK)));
      this.setNbMovesWithNoCaptureOrPawn(0);
    }
  }

  public List<Move> getAvailableMoves(Position pos) {
    return getBoardRep().getAvailableMoves(pos.x(), pos.y(), false);
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

    this.board.setNbMovesWithNoCaptureOrPawn(board.getNbMovesWithNoCaptureOrPawn() + 1);
    if (getBoardRep().getPieceAt(move.getSource().x(), move.getSource().y()).getPiece()
        == Piece.PAWN) {
      // Reset the number of moves with no pawn move
      this.board.setNbMovesWithNoCaptureOrPawn(0);
    }
    if (move.isTake()) {
      // SAVE DELETED PIECE FOR HASHING
      if (!this.isEnPassantTake()) {
        getBoardRep().deletePieceAt(move.getDest().x(), move.getDest().y());
      }
      // Reset the number of moves with no capture
      this.board.setNbMovesWithNoCaptureOrPawn(0);
    }

    if (this.isEnPassantTake()) {
      this.setLastMoveDoublePush(false);
      this.setEnPassantTake(false);
      if (this.getPlayer()) {
        getBoardRep().deletePieceAt(move.getDest().x(), move.getDest().y() - 1);
      } else {
        getBoardRep().deletePieceAt(move.getDest().x(), move.getDest().y() + 1);
      }
    }

    getBoardRep().movePiece(move.getSource(), move.getDest());

    if (this.isWhiteLongCastle()
        && (move.getSource().equals(new Position(4, 0))
            || move.getSource().equals(new Position(0, 0)))) { // rook on a1 and king on e1
      this.setWhiteLongCastle(false);
    }
    if (this.isWhiteShortCastle()
        && (move.getSource().equals(new Position(4, 0))
            || move.getSource().equals(new Position(7, 0)))) { // rook on h1 and king on e1
      this.setWhiteShortCastle(false);
    }

    if (this.isBlackShortCastle()
        && (move.getSource().equals(new Position(4, 7))
            || move.getSource().equals(new Position(7, 7)))) { // rook on h8 and king on e8
      this.setBlackShortCastle(false);
    }
    if (this.isBlackLongCastle()
        && (move.getSource().equals(new Position(4, 7))
            || move.getSource().equals(new Position(0, 7)))) { // rook on a8 and king on e8
      this.setBlackLongCastle(false);
    }
    if (getBoardRep().isPawnPromoting(move.getDest().x(), move.getDest().y(), this.getPlayer())) {
      Piece newPiece = ((PromoteMove) move).getPromPiece();
      getBoardRep()
          .promotePawn(
              move.getDest().x(),
              move.getDest().y(),
              this.getPlayer(),
              newPiece); // replace Piece.QUEEN by newPiece
    }

    if (isLastMoveDoublePush()) {
      this.setLastMoveDoublePush(false);
    }

    if (this.isEnPassantTake()) {
      this.setLastMoveDoublePush(false);
    }
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
    copy.setPlayer(this.getPlayer());
    copy.setWhiteShortCastle(this.isWhiteShortCastle());
    copy.setBlackShortCastle(this.isBlackShortCastle());
    copy.setWhiteLongCastle(this.isWhiteLongCastle());
    copy.setBlackLongCastle(this.isBlackLongCastle());
    copy.setEnPassantPos(
        (this.getEnPassantPos() != null) ? this.getEnPassantPos().getCopy() : null);
    copy.setLastMoveDoublePush(this.isLastMoveDoublePush());
    copy.setEnPassantTake(this.isEnPassantTake());
    copy.setNbMovesWithNoCaptureOrPawn(this.getNbMovesWithNoCaptureOrPawn());
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
    int rows = this.getBoardRep().getNbRows();
    int cols = this.getBoardRep().getNbCols();
    char[][] charBoard = new char[rows][cols];

    for (int i = 0; i < rows; i++) {
      Arrays.fill(charBoard[i], Piece.EMPTY.getCharRepresentation(true));
    }

    for (int i = 0; i < 2; i++) {
      boolean color = i == 0;

      placePiecesOnBoard(
          charBoard, this.getBoardRep().getPawns(color), Piece.PAWN.getCharRepresentation(color));
      placePiecesOnBoard(
          charBoard, this.getBoardRep().getRooks(color), Piece.ROOK.getCharRepresentation(color));
      placePiecesOnBoard(
          charBoard,
          this.getBoardRep().getKnights(color),
          Piece.KNIGHT.getCharRepresentation(color));
      placePiecesOnBoard(
          charBoard,
          this.getBoardRep().getBishops(color),
          Piece.BISHOP.getCharRepresentation(color));
      placePiecesOnBoard(
          charBoard, this.getBoardRep().getQueens(color), Piece.QUEEN.getCharRepresentation(color));
      placePiecesOnBoard(
          charBoard, this.getBoardRep().getKing(color), Piece.KING.getCharRepresentation(color));
    }

    return charBoard;
  }

  /**
   * Places the pieces on the given board at the given positions. The y-coordinate of the position
   * is inverted to match the 0-indexed array representation of the board (bottom to top).
   *
   * @param board the current ASCII representation of the board
   * @param positions the positions where the pieces should be placed
   * @param rep the character to use to represent the pieces
   */
  private void placePiecesOnBoard(char[][] board, List<Position> positions, char rep) {
    for (Position pos : positions) {
      board[this.getBoardRep().getNbRows() - 1 - pos.y()][pos.x()] = rep;
    }
  }

  public int getNbFullMovesWithNoCaptureOrPawn() {
    // Divide by 2 because fifty move rule is for full moves
    return board.getNbMovesWithNoCaptureOrPawn() / 2;
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

    if (shortCastle) {
      getBoardRep().applyShortCastle(color);
    } else {
      getBoardRep().applyLongCastle(color);
    }
  }

  /**
   * Get the castling rights of the board.
   *
   * @return An array that contains castling rights
   */
  public boolean[] getCastlingRights() {
    return new boolean[] {
      isWhiteShortCastle(), isWhiteLongCastle(), isBlackShortCastle(), isBlackLongCastle()
    };
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
