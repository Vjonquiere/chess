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
  byte enPassant;
  boolean whiteShortCastle;
  boolean blackShortCastle;
  boolean whiteLongCastle;
  boolean blackLongCastle;

  public Board() {
    Logging.configureLogging(LOGGER);
    this.board = new BitboardRepresentation();
    this.isWhite = true;
    this.enPassant = 0;
    this.whiteShortCastle = true;
    this.blackShortCastle = true;
    this.whiteLongCastle = true;
    this.blackLongCastle = true;
  }

  public List<Move> getAvailableMoves(Position pos) {
    return board.getAvailableMoves(pos.getX(), pos.getY(), this);
  }

  public boolean makeMove(Move move) {
    // board.makeMove(move);  // jouer le coup dans la bitboard

    // mettre a jour les flags enpassant , si un roi ou une tour a bougé (= modifier le booleen des
    // castles)
    move.toString();
    // TODO
    return true;
  }

  public Board getCopy() {
    // TODO
    throw new UnsupportedOperationException();
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
}
