package pdp.model.board;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import pdp.model.piece.Color;
import pdp.model.piece.Piece;
import pdp.utils.Logging;
import pdp.utils.Position;

public class Board {
  private static final Logger LOGGER = Logger.getLogger(Board.class.getName());
  public BoardRepresentation board;
  public boolean isWhite;
  boolean whiteShortCastle;
  boolean blackShortCastle;
  boolean whiteLongCastle;
  boolean blackLongCastle;
  public Position enPassantPos;
  public boolean isLastMoveDoublePush;
  public boolean isEnPassantTake;
  int nbMovesWithNoCaptureOrPawn;
  int doubleMovePawnBlack;
  int doubleMovePawnWhite;

  public Board() {
    Logging.configureLogging(LOGGER);
    this.board = new BitboardRepresentation();
    this.isWhite = true;
    this.enPassantPos = null;
    this.whiteShortCastle = true;
    this.blackShortCastle = true;
    this.whiteLongCastle = true;
    this.blackLongCastle = true;
    this.isLastMoveDoublePush = false;
    this.isEnPassantTake = false;
    this.doubleMovePawnBlack = 0;
    this.doubleMovePawnWhite = 0;
    this.nbMovesWithNoCaptureOrPawn = 0;
  }

  public List<Move> getAvailableMoves(Position pos) {
    return board.getAvailableMoves(pos.getX(), pos.getY(), false);
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
    if (board.getPieceAt(move.source.getX(), move.source.getY()).piece == Piece.PAWN) {
      // Reset the number of moves with no pawn move
      this.nbMovesWithNoCaptureOrPawn = 0;
    }
    if (move.isTake) {
      // SAVE DELETED PIECE FOR HASHING
      if (!this.isEnPassantTake) {
        board.deletePieceAt(move.dest.getX(), move.dest.getY());
      }
      // Reset the number of moves with no capture
      this.nbMovesWithNoCaptureOrPawn = 0;
    }

    if (this.isEnPassantTake) {
      this.isLastMoveDoublePush = false;
      this.isEnPassantTake = false;
      if (this.isWhite) {
        board.deletePieceAt(move.dest.getX(), move.dest.getY() - 1);
      } else {
        board.deletePieceAt(move.dest.getX(), move.dest.getY() + 1);
      }
    }

    board.movePiece(move.source, move.dest);

    if (this.whiteShortCastle
        && (move.source.equals(new Position(4, 0))
            || move.source.equals(new Position(0, 0)))) { // rook on a1 and king on e1
      this.whiteShortCastle = false;
    }
    if (this.whiteLongCastle
        && (move.source.equals(new Position(4, 0))
            || move.source.equals(new Position(7, 0)))) { // rook on h1 and king on e1
      this.whiteLongCastle = false;
    }

    if (this.blackShortCastle
        && (move.source.equals(new Position(4, 7))
            || move.source.equals(new Position(7, 7)))) { // rook on h8 and king on e8
      this.blackShortCastle = false;
    }
    if (this.blackLongCastle
        && (move.source.equals(new Position(4, 7))
            || move.source.equals(new Position(0, 7)))) { // rook on a8 and king on e8
      this.blackLongCastle = false;
    }
    if (board.isPawnPromoting(move.dest.getX(), move.dest.getY(), this.isWhite)) {
      Piece newPiece = ((PromoteMove) move).getPromPiece();
      board.promotePawn(
          move.dest.getX(),
          move.dest.getY(),
          this.isWhite,
          newPiece); // replace Piece.QUEEN by newPiece
    }

    if (isLastMoveDoublePush) {
      this.isLastMoveDoublePush = false;
    }

    if (this.isEnPassantTake) {
      this.isLastMoveDoublePush = false;
    }
  }

  public Board getCopy() {
    // TODO
    throw new UnsupportedOperationException();
  }

  public BoardRepresentation getBoardRep() {
    return board;
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
    int rows = this.board.getNbRows();
    int cols = this.board.getNbCols();
    char[][] charBoard = new char[rows][cols];

    for (int i = 0; i < rows; i++) {
      Arrays.fill(charBoard[i], Piece.EMPTY.getCharRepresentation(true));
    }

    for (int i = 0; i < 2; i++) {
      boolean color = (i == 0);

      placePiecesOnBoard(
          charBoard, this.board.getPawns(color), Piece.PAWN.getCharRepresentation(color));
      placePiecesOnBoard(
          charBoard, this.board.getRooks(color), Piece.ROOK.getCharRepresentation(color));
      placePiecesOnBoard(
          charBoard, this.board.getKnights(color), Piece.KNIGHT.getCharRepresentation(color));
      placePiecesOnBoard(
          charBoard, this.board.getBishops(color), Piece.BISHOP.getCharRepresentation(color));
      placePiecesOnBoard(
          charBoard, this.board.getQueens(color), Piece.QUEEN.getCharRepresentation(color));
      placePiecesOnBoard(
          charBoard, this.board.getKing(color), Piece.KING.getCharRepresentation(color));
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
      board[this.board.getNbRows() - 1 - pos.getY()][pos.getX()] = rep;
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
    if (color == Color.WHITE) {
      if (shortCastle && !this.whiteShortCastle) return false;
      if (!shortCastle && !this.whiteLongCastle) return false;

      Position f1Square = new Position(5, 0);
      Position g1Square = new Position(6, 0);

      Position d1Square = new Position(3, 0);
      Position c1Square = new Position(2, 0);
      Position b1Square = new Position(1, 0);

      if (shortCastle) {
        if ((board.getPieceAt(f1Square.getX(), f1Square.getY()).piece != Piece.EMPTY)
            || (board.getPieceAt(g1Square.getX(), g1Square.getY()).piece != Piece.EMPTY)) {
          return false;
        }
        // Squares are empty so now ensure king is not in check and does not move through check
        if (board.isCheck(Color.WHITE)
            || board.isAttacked(5, 0, Color.BLACK)
            || board.isAttacked(6, 0, Color.BLACK)) {
          return false;
        }
      } else {
        if ((board.getPieceAt(d1Square.getX(), d1Square.getY()).piece != Piece.EMPTY)
            || (board.getPieceAt(c1Square.getX(), c1Square.getY()).piece != Piece.EMPTY)
            || (board.getPieceAt(b1Square.getX(), b1Square.getY()).piece != Piece.EMPTY)) {
          return false;
        }
        // Squares are empty so now ensure king is not in check and does not move through check
        if (board.isCheck(Color.WHITE)
            || board.isAttacked(3, 0, Color.BLACK)
            || board.isAttacked(2, 0, Color.BLACK)) {
          return false;
        }
      }
      return true;
    } else {
      if (shortCastle && !this.blackShortCastle) return false;
      if (!shortCastle && !this.blackLongCastle) return false;

      Position f8Square = new Position(5, 7);
      Position g8Square = new Position(6, 7);

      Position d8Square = new Position(3, 7);
      Position c8Square = new Position(2, 7);
      Position b8Square = new Position(1, 7);

      if (shortCastle) {
        if ((board.getPieceAt(f8Square.getX(), f8Square.getY()).piece != Piece.EMPTY)
            || (board.getPieceAt(g8Square.getX(), g8Square.getY()).piece != Piece.EMPTY)) {
          return false;
        }
        // Squares are empty so now ensure king is not in check and does not move through check
        if (board.isCheck(Color.BLACK)
            || board.isAttacked(5, 7, Color.WHITE)
            || board.isAttacked(6, 7, Color.WHITE)) {
          return false;
        }
      } else {
        if ((board.getPieceAt(d8Square.getX(), d8Square.getY()).piece != Piece.EMPTY)
            || (board.getPieceAt(c8Square.getX(), c8Square.getY()).piece != Piece.EMPTY)
            || (board.getPieceAt(b8Square.getX(), b8Square.getY()).piece != Piece.EMPTY)) {
          return false;
        }
        // Squares are empty so now ensure king is not in check and does not move through check
        if (board.isCheck(Color.BLACK)
            || board.isAttacked(3, 7, Color.WHITE)
            || board.isAttacked(2, 7, Color.WHITE)) {
          return false;
        }
      }
      return true;
    }
  }

  /**
   * Applies short castle for color {color}. Changes bitboards. Assumes castle is possible
   *
   * @param color color for which castling move is applied
   */
  public void applyShortCastle(Color color) {
    if (color == Color.WHITE) {
      Position e1Square = new Position(4, 0);
      Position f1Square = new Position(5, 0);
      Position g1Square = new Position(6, 0);
      Position h1Square = new Position(7, 0);
      // Move king
      this.board.movePiece(e1Square, g1Square);
      // Move rook
      this.board.movePiece(h1Square, f1Square);

    } else {
      Position e8Square = new Position(4, 7);
      Position f8Square = new Position(5, 7);
      Position g8Square = new Position(6, 7);
      Position h8Square = new Position(7, 7);
      // Move king
      this.board.movePiece(e8Square, g8Square);
      // Move rook
      this.board.movePiece(h8Square, f8Square);
    }
  }

  /**
   * Applies long castle for color {color}. Changes bitboards. Assumes castle is possible
   *
   * @param color color for which castling move is applied
   */
  public void applyLongCastle(Color color) {
    if (color == Color.WHITE) {
      Position e1Square = new Position(4, 0);
      Position d1Square = new Position(3, 0);
      Position c1Square = new Position(2, 0);
      Position a1Square = new Position(0, 0);
      // Move king
      this.board.movePiece(e1Square, c1Square);
      // Move rook
      this.board.movePiece(a1Square, d1Square);
    } else {
      Position e8Square = new Position(4, 7);
      Position d8Square = new Position(3, 7);
      Position c8Square = new Position(2, 7);
      Position a8Square = new Position(0, 7);
      // Move king
      this.board.movePiece(e8Square, c8Square);
      // Move rook
      this.board.movePiece(a8Square, d8Square);
    }
  }

  /**
   * Applies long or short castle to {color} according to the boolean value given in parameter.
   * Assumes castle is possible
   *
   * @param color color for which castling move is applied
   */
  public void applyCastle(Color color, boolean shortCastle) {
    if (shortCastle) {
      applyShortCastle(color);
    } else {
      applyLongCastle(color);
    }
  }
}
