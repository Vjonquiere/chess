package pdp.model;

import java.util.List;
import pdp.utils.Position;

public interface BoardRepresentation extends Rules {
  public List<Position> getPawns(boolean white);

  public List<Position> getRooks(boolean white);

  public List<Position> getBishops(boolean white);

  public List<Position> getKnights(boolean white);

  public List<Position> getQueens(boolean white);

  public Position getKing(boolean white);

  public Piece getPieceAt(int x, int y);
}
