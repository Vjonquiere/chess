package pdp.model.board;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.model.piece.Piece;
import pdp.utils.Logging;
import pdp.utils.Position;

public class Board {
  private static final Logger LOGGER = Logger.getLogger(Board.class.getName());
  private BoardRepresentation board;
  public boolean isWhite;
  private boolean whiteShortCastle;
  private boolean blackShortCastle;
  private boolean whiteLongCastle;
  private boolean blackLongCastle;
  private Position enPassantPos;
  private boolean isLastMoveDoublePush;
  private boolean isEnPassantTake;
  public int nbMovesWithNoCaptureOrPawn;

  static {
    Logging.configureLogging(LOGGER);
  }

  /** Creates a default board. */
  public Board() {
    this.setBoard(new BitboardRepresentation());
    this.isWhite = true;
    this.enPassantPos = null;
    this.whiteShortCastle = true;
    this.blackShortCastle = true;
    this.whiteLongCastle = true;
    this.blackLongCastle = true;
    this.isLastMoveDoublePush = false;
    this.isEnPassantTake = false;
    this.nbMovesWithNoCaptureOrPawn = 0;
  }

  /**
   * Create a board from a given board state (support FileBoard header).
   *
   * @param board The board state to use
   */
  public Board(FileBoard board) {
    this.setBoard(board.board());
    this.isWhite = board.isWhiteTurn();

    if (board.header() != null) { // Initialize board with header values
      this.setEnPassantPos(board.header().enPassant());
      if (this.getEnPassantPos() != null) {
        this.setLastMoveDoublePush(true);
      }
      this.setWhiteShortCastle(board.header().whiteKingCastling());
      this.setBlackShortCastle(board.header().blackKingCastling());
      this.setWhiteLongCastle(board.header().whiteQueenCastling());
      this.setBlackLongCastle(board.header().blackQueenCastling());
      this.nbMovesWithNoCaptureOrPawn = board.header().fiftyMoveRule();
    } else { // No header -> default values
      this.setEnPassantPos(null);
      this.setWhiteShortCastle(true);
      this.setBlackShortCastle(true);
      this.setWhiteLongCastle(true);
      this.setBlackLongCastle(true);
      this.setLastMoveDoublePush(false);
      this.setEnPassantTake(false);
      this.nbMovesWithNoCaptureOrPawn = 0;
    }
  }

  public List<Move> getAvailableMoves(Position pos) {
    return getBoardRep().getAvailableMoves(pos.getX(), pos.getY(), false);
  }

  public boolean getPlayer() {
    return this.isWhite;
  }

  public void setPlayer(boolean isWhite) {
    this.isWhite = isWhite;
  }

  /**
   * Executes a given move on the board, handling captures, en passant, castling, pawn promotion,
   *
   * @param move The move to be executed
   */
  public void makeMove(Move move) {
    this.nbMovesWithNoCaptureOrPawn++;
    if (getBoardRep().getPieceAt(move.source.getX(), move.source.getY()).piece == Piece.PAWN) {
      // Reset the number of moves with no pawn move
      this.nbMovesWithNoCaptureOrPawn = 0;
    }
    if (move.isTake) {
      // SAVE DELETED PIECE FOR HASHING
      if (!this.isEnPassantTake()) {
        getBoardRep().deletePieceAt(move.dest.getX(), move.dest.getY());
      }
      // Reset the number of moves with no capture
      this.nbMovesWithNoCaptureOrPawn = 0;
    }

    if (this.isEnPassantTake()) {
      this.setLastMoveDoublePush(false);
      this.setEnPassantTake(false);
      if (this.isWhite) {
        getBoardRep().deletePieceAt(move.dest.getX(), move.dest.getY() - 1);
      } else {
        getBoardRep().deletePieceAt(move.dest.getX(), move.dest.getY() + 1);
      }
    }

    getBoardRep().movePiece(move.source, move.dest);

    if (this.isWhiteLongCastle()
        && (move.source.equals(new Position(4, 0))
            || move.source.equals(new Position(0, 0)))) { // rook on a1 and king on e1
      this.setWhiteLongCastle(false);
    }
    if (this.isWhiteShortCastle()
        && (move.source.equals(new Position(4, 0))
            || move.source.equals(new Position(7, 0)))) { // rook on h1 and king on e1
      this.setWhiteShortCastle(false);
    }

    if (this.isBlackShortCastle()
        && (move.source.equals(new Position(4, 7))
            || move.source.equals(new Position(7, 7)))) { // rook on h8 and king on e8
      this.setBlackShortCastle(false);
    }
    if (this.isBlackLongCastle()
        && (move.source.equals(new Position(4, 7))
            || move.source.equals(new Position(0, 7)))) { // rook on a8 and king on e8
      this.setBlackLongCastle(false);
    }
    if (getBoardRep().isPawnPromoting(move.dest.getX(), move.dest.getY(), this.isWhite)) {
      Piece newPiece = ((PromoteMove) move).getPromPiece();
      getBoardRep()
          .promotePawn(
              move.dest.getX(),
              move.dest.getY(),
              this.isWhite,
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
    copy.isWhite = this.isWhite;
    copy.setWhiteShortCastle(this.isWhiteShortCastle());
    copy.setBlackShortCastle(this.isBlackShortCastle());
    copy.setWhiteLongCastle(this.isWhiteLongCastle());
    copy.setBlackLongCastle(this.isBlackLongCastle());
    copy.setEnPassantPos(
        (this.getEnPassantPos() != null) ? this.getEnPassantPos().getCopy() : null);
    copy.setLastMoveDoublePush(this.isLastMoveDoublePush());
    copy.setEnPassantTake(this.isEnPassantTake());
    copy.nbMovesWithNoCaptureOrPawn = this.nbMovesWithNoCaptureOrPawn;
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
      boolean color = (i == 0);

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
      board[this.getBoardRep().getNbRows() - 1 - pos.getY()][pos.getX()] = rep;
    }
  }

  public int getNbMovesWithNoCaptureOrPawn() {
    // Divide by 2 because fifty move rule is for full moves
    return this.nbMovesWithNoCaptureOrPawn / 2;
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
    return getBoardRep()
        .canCastle(
            color,
            shortCastle,
            this.isWhiteShortCastle(),
            this.isWhiteLongCastle(),
            this.isBlackShortCastle(),
            this.isBlackLongCastle());
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

  public boolean[] getCastlingRights() {
    return new boolean[] {
      isWhiteShortCastle(), isWhiteLongCastle(), isBlackShortCastle(), isBlackLongCastle()
    };
  }

  private void setBoard(BoardRepresentation board) {
    this.board = board;
  }

  public Position getEnPassantPos() {
    return enPassantPos;
  }

  public void setEnPassantPos(Position enPassantPos) {
    this.enPassantPos = enPassantPos;
  }

  public boolean isLastMoveDoublePush() {
    return isLastMoveDoublePush;
  }

  public void setLastMoveDoublePush(boolean lastMoveDoublePush) {
    isLastMoveDoublePush = lastMoveDoublePush;
  }

  public boolean isWhiteShortCastle() {
    return whiteShortCastle;
  }

  public void setWhiteShortCastle(boolean whiteShortCastle) {
    this.whiteShortCastle = whiteShortCastle;
  }

  public boolean isBlackShortCastle() {
    return blackShortCastle;
  }

  public void setBlackShortCastle(boolean blackShortCastle) {
    this.blackShortCastle = blackShortCastle;
  }

  public boolean isWhiteLongCastle() {
    return whiteLongCastle;
  }

  public void setWhiteLongCastle(boolean whiteLongCastle) {
    this.whiteLongCastle = whiteLongCastle;
  }

  public boolean isBlackLongCastle() {
    return blackLongCastle;
  }

  public void setBlackLongCastle(boolean blackLongCastle) {
    this.blackLongCastle = blackLongCastle;
  }

  public boolean isEnPassantTake() {
    return isEnPassantTake;
  }

  public void setEnPassantTake(boolean enPassantTake) {
    isEnPassantTake = enPassantTake;
  }
}
