package pdp.model;

import java.util.ArrayList;
import java.util.List;

import pdp.utils.Position;

public class Board {
  BoardRepresentation board;
  boolean isWhite;
  byte enPassant;
  boolean whiteShortCastle;
  boolean blackShortCastle;
  boolean whiteLongCastle;
  boolean blackLongCastle;

  public Board() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public List<Move> getAvailableMoves() {
    // TODO
    throw new UnsupportedOperationException();
  }

  public boolean makeMove(Move move) {
    // TODO
    throw new UnsupportedOperationException();
  }

  public Board getCopy() {
    // TODO
    throw new UnsupportedOperationException();
  }

  public String getAsciiRepresentation() {

    ArrayList<ArrayList<Character>> board = new ArrayList<ArrayList<Character>>();
    for (int i = 0; i < 8; i++) {
      board.add(new ArrayList<Character>());
      for (int j = 0; j < 8; j++) {
        board.get(i).add('_');
      }
    }

    for (int i = 0; i < 2; i++) {
      boolean color = i == 0;
      char rep = Piece.PAWN.getCharRepresentation(color);
      for (Position pos : board.getPawns(color)) {
        board.get(pos.getY()).set(pos.getX(), rep);
      }
      rep = Piece.ROOK.getCharRepresentation(color);
      for (Position pos : board.getRooks(color)) {
        board.get(pos.getY()).set(pos.getX(), rep);
      }
      rep = Piece.KNIGHT.getCharRepresentation(color);
      for (Position pos : board.getKnights(color)) {
        board.get(pos.getY()).set(pos.getX(), rep);
      }
      rep = Piece.BISHOP.getCharRepresentation(color);
      for (Position pos : board.getBishops(color)) {
        board.get(pos.getY()).set(pos.getX(), rep);
      }
      rep = Piece.QUEEN.getCharRepresentation(color);
      for (Position pos : board.getQueens(color)) {
        board.get(pos.getY()).set(pos.getX(), rep);
      }
      rep = Piece.KING.getCharRepresentation(color);
      Position pos = board.getKing(color);
      board.get(pos.getY()).set(pos.getX(), rep);
    }

    StringBuilder sb = new StringBuilder();
    for (ArrayList<Character> row : board) {
        for (Character cell : row) {
            sb.append(cell);
        }
        sb.append('\n');
    }
    
    return(sb.toString());
  }
}
