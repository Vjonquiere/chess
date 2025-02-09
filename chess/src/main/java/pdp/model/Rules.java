package pdp.model;

import java.util.List;

public interface Rules {
  public List<Move> getAvailableMoves(int x, int y, boolean kingReachable);

  public boolean isAttacked(int x, int y, Color by);

  public boolean isCheck(Color color);

  public boolean isCheckAfterMove(Color color, Move move);

  public boolean isCheckMate(Color color);

  public boolean isStaleMate(Color color, Color colorTurnToPlay);

  public boolean isDrawByInsufficientMaterial();

  public boolean isPawnPromoting(int x, int y, boolean white);

  public void promotePawn(int x, int y, boolean white, Piece newPiece);

  public boolean isDoublePushPossible(Move move, boolean white);

  public boolean isEnPassant(int x, int y, Move move, boolean white);
}
