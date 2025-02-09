package pdp.model;

import java.util.List;
import pdp.utils.Position;

public interface BoardRepresentation extends Rules {
  public List<Position> getPawns(boolean white);

  public List<Position> getRooks(boolean white);

  public List<Position> getBishops(boolean white);

  public List<Position> getKnights(boolean white);

  public List<Position> getQueens(boolean white);

  public List<Position> getKing(boolean white);

  public ColoredPiece<Piece, Color> getPieceAt(int x, int y);

  public int getNbCols();

  public int getNbRows();

  public void movePiece(Position from, Position to);

  public void deletePieceAt(int x, int y);
}
