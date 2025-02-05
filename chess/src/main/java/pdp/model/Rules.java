package pdp.model;

import java.util.List;

public interface Rules {
  public List<Move> getAvailableMoves(int x, int y, Board board);

  public boolean isAttacked(int x, int y, Board board);

  public boolean isCheck(Board board);

  public boolean isCheckMate(Board board);

  public boolean isPawnPromoting(int x, int y, boolean white, Board board);

  public void promotePawn(int x, int y, boolean white, Piece newPiece, Board board);
}
