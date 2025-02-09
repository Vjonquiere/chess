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
  }

  public List<Move> getAvailableMoves(Position pos) {
    return board.getAvailableMoves(pos.getX(), pos.getY(), false);
  }

  public void makeMove(Move move) {
    if (move.isTake == true) {
      board.deletePieceAt(move.dest.getX(), move.dest.getY());
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
    // verifier si enPassant
    move.toString();
  }

  public Board getCopy() {
    // TODO
    throw new UnsupportedOperationException();
  }

  public char[][] getAsciiRepresentation() {
    int rows = this.board.getNbRows();
    int cols = this.board.getNbCols();
    char[][] charBoard = new char[rows][cols];

    for (int i = 0; i < rows; i++) {
      Arrays.fill(charBoard[i], '_');
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

  private void placePiecesOnBoard(char[][] board, List<Position> positions, char rep) {
    for (Position pos : positions) {
      board[this.board.getNbRows() - 1 - pos.getY()][pos.getX()] = rep;
    }
  }
}
