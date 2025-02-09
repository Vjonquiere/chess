package pdp.model;

import java.util.List;

public interface Rules {
  public List<Move> getAvailableMoves(int x, int y, boolean kingReachable);

  public boolean isAttacked(int x, int y, Color by);

  public boolean isCheck(Color color);

  public boolean isCheckMate(Color color);

  public boolean isStaleMate(Color color);

  public boolean isPawnPromoting(int x, int y, boolean white);

  public void promotePawn(int x, int y, boolean white, Piece newPiece);
}
