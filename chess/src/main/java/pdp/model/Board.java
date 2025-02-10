package pdp.model;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import pdp.utils.Logging;
import pdp.utils.Position;

public class Board {
  private static final Logger LOGGER = Logger.getLogger(Board.class.getName());
  BoardRepresentation board;
  boolean isWhite;
  boolean whiteShortCastle;
  boolean blackShortCastle;
  boolean whiteLongCastle;
  boolean blackLongCastle;
  Position enPassantPos;
  boolean isLastMoveDoublePush;
  boolean isEnPassantTake;
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

  /**
   * Executes a given move on the board, handling captures, en passant, castling, pawn promotion,
   * and turn switching
   *
   * @param move The move to be executed
   */
  public void makeMove(Move move) {
    if (move.isTake == true) {
      board.deletePieceAt(move.dest.getX(), move.dest.getY());
      // Reset the number of moves with no capture
      this.nbMovesWithNoCaptureOrPawn = 0;
    }
    if (board.getPieceAt(move.source.getX(), move.source.getY()).piece == Piece.PAWN) {
      // Reset the number of moves with no pawn move
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

    this.nbMovesWithNoCaptureOrPawn++;

    if (this.isWhite) {
      this.isWhite = false;
    } else {
      this.isWhite = true;
    }

    if (this.whiteShortCastle == true
        && (move.source.equals(new Position(0, 4))
            || move.source.equals(new Position(0, 0)))) { // rook on a1 and king on e1
      this.whiteShortCastle = false;
    }
    if (this.whiteLongCastle == true
        && (move.source.equals(new Position(0, 4))
            || move.source.equals(new Position(0, 7)))) { // rook on h1 and king on e1
      this.whiteLongCastle = false;
    }

    if (this.blackShortCastle == true
        && (move.source.equals(new Position(7, 4))
            || move.source.equals(new Position(7, 7)))) { // rook on h8 and king on e8
      this.blackShortCastle = false;
    }
    if (this.blackLongCastle == true
        && (move.source.equals(new Position(7, 4))
            || move.source.equals(new Position(7, 0)))) { // rook on a8 and king on e8
      this.blackLongCastle = false;
    }
    if (board.isPawnPromoting(move.dest.getX(), move.dest.getY(), this.isWhite)) {
      // Piece newPiece = new Piece(ask to what the user want to promote his pawn)
      board.promotePawn(
          move.dest.getX(),
          move.dest.getY(),
          this.isWhite,
          Piece.QUEEN); // remplacez Piece.QUEEN par newPiece une fois que ca sera implementé
    }

    if (isLastMoveDoublePush) {
      this.isLastMoveDoublePush = false;
    }

    if (this.isEnPassantTake) {
      this.isLastMoveDoublePush = false;
    }
    move.toString();
  }

  public Board getCopy() {
    // TODO
    throw new UnsupportedOperationException();
  }

  public BoardRepresentation getBoard() {
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
   * @param color the color of the player we want to
   * @param shortCastle boolean value to indicate if we're looking for the short castle right or
   *     long castle right
   * @return true if castle {shortCastle} is possible for player of Color {color}. false otherwise
   */
  public boolean canCastle(Color color, boolean shortCastle) {
    if (color == Color.WHITE) {
      if (shortCastle && !this.whiteShortCastle) return false;
      if (!shortCastle && !this.whiteLongCastle) return false;

      Position f8Square = new Position(0, 5);
      Position g8Square = new Position(0, 6);

      Position d8Square = new Position(0, 3);
      Position c8Square = new Position(0, 2);
      Position b8Square = new Position(0, 1);

      if (shortCastle) {
        if ((board.getPieceAt(f8Square.getX(), f8Square.getY()).piece != Piece.EMPTY)
            || (board.getPieceAt(g8Square.getX(), g8Square.getY()).piece != Piece.EMPTY)) {
          return false;
        }
        // Squares are empty so now ensure king is not in check and does not move through check
        if (board.isCheck(Color.WHITE)
            || board.isAttacked(5, 0, Color.BLACK)
            || board.isAttacked(6, 0, Color.BLACK)) {
          return false;
        }
      } else {
        if ((board.getPieceAt(d8Square.getX(), d8Square.getY()).piece != Piece.EMPTY)
            || (board.getPieceAt(c8Square.getX(), c8Square.getY()).piece != Piece.EMPTY)
            || (board.getPieceAt(b8Square.getX(), b8Square.getY()).piece != Piece.EMPTY)) {
          return false;
        }
        // Squares are empty so now ensure king is not in check and does not move through check
        if (board.isCheck(Color.WHITE)
            || board.isAttacked(3, 0, Color.BLACK)
            || board.isAttacked(2, 0, Color.BLACK)
            || board.isAttacked(1, 0, Color.BLACK)) {
          return false;
        }
      }
      return true;
    } else {
      if (shortCastle && !this.blackShortCastle) return false;
      if (!shortCastle && !this.blackLongCastle) return false;

      Position f8Square = new Position(7, 5);
      Position g8Square = new Position(7, 6);

      Position d8Square = new Position(7, 3);
      Position c8Square = new Position(7, 2);
      Position b8Square = new Position(7, 1);

      if (shortCastle) {
        if ((board.getPieceAt(f8Square.getX(), f8Square.getY()).piece != Piece.EMPTY)
            || (board.getPieceAt(g8Square.getX(), g8Square.getY()).piece != Piece.EMPTY)) {
          return false;
        }
        // Squares are empty so now ensure king is not in check and does not move through check
        if (board.isCheck(Color.WHITE)
            || board.isAttacked(5, 7, Color.BLACK)
            || board.isAttacked(6, 7, Color.BLACK)) {
          return false;
        }
      } else {
        if ((board.getPieceAt(d8Square.getX(), d8Square.getY()).piece != Piece.EMPTY)
            || (board.getPieceAt(c8Square.getX(), c8Square.getY()).piece != Piece.EMPTY)
            || (board.getPieceAt(b8Square.getX(), b8Square.getY()).piece != Piece.EMPTY)) {
          return false;
        }
        // Squares are empty so now ensure king is not in check and does not move through check
        if (board.isCheck(Color.WHITE)
            || board.isAttacked(3, 7, Color.BLACK)
            || board.isAttacked(2, 7, Color.BLACK)
            || board.isAttacked(1, 7, Color.BLACK)) {
          return false;
        }
      }
      return true;
    }
  }

  public void applyShortCastle(Color color) {}
}
