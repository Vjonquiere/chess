package pdp.model;

import java.util.ArrayList;
import java.util.List;
import pdp.utils.Position;

public class Board{
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

  public List<Move> getAvailableMoves(Position pos) {
    return board.getAvailableMoves(pos.getX(), pos.getY(), this);
  }

  public boolean makeMove(Move move) {
    board.makeMove(move);
    
    // mettre a jour les flags enpassant , si un roi ou une tour a bougé (= modifier le booleen des castles)
      move.toString();
    // TODO
    throw new UnsupportedOperationException();
  }

  public Board getCopy() {
    // TODO
    throw new UnsupportedOperationException();
  }

  public String getAsciiRepresentation() {

    ArrayList<ArrayList<Character>> charBoard = new ArrayList<ArrayList<Character>>();
    for (int i = 0; i < this.board.getNbCols(); i++) {
      charBoard.add(new ArrayList<Character>());
      for (int j = 0; j < this.board.getNbRows(); j++) {
        charBoard.get(i).add('_');
      }
    }

    for (int i = 0; i < 2; i++) {
      boolean color = i == 0;
      char rep = Piece.PAWN.getCharRepresentation(color);
      for (Position pos : this.board.getPawns(color)) {
        charBoard.get(pos.getY()).set(pos.getX(), rep);
      }
      rep = Piece.ROOK.getCharRepresentation(color);
      for (Position pos : this.board.getRooks(color)) {
        charBoard.get(pos.getY()).set(pos.getX(), rep);
      }
      rep = Piece.KNIGHT.getCharRepresentation(color);
      for (Position pos : this.board.getKnights(color)) {
        charBoard.get(pos.getY()).set(pos.getX(), rep);
      }
      rep = Piece.BISHOP.getCharRepresentation(color);
      for (Position pos : this.board.getBishops(color)) {
        charBoard.get(pos.getY()).set(pos.getX(), rep);
      }
      rep = Piece.QUEEN.getCharRepresentation(color);
      for (Position pos : this.board.getQueens(color)) {
        charBoard.get(pos.getY()).set(pos.getX(), rep);
      }
      rep = Piece.KING.getCharRepresentation(color);
      Position pos = this.board.getKing(color);
      charBoard.get(pos.getY()).set(pos.getX(), rep);
    }

    StringBuilder sb = new StringBuilder();
    for (ArrayList<Character> row : charBoard) {
      for (Character cell : row) {
        sb.append(cell);
      }
      sb.append('\n');
    }

    return (sb.toString());
  }
}
