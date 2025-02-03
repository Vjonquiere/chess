package pdp.model;

import java.util.List;
import java.util.logging.Logger;
import pdp.utils.Logging;
import pdp.utils.Position;

public class BitboardRepresentation implements BoardRepresentation {
  private static final Logger LOGGER = Logger.getLogger(BitboardRepresentation.class.getName());
  private Bitboard[] board;
  private int nbCols = 8;
  private int nbRows = 8;

  public BitboardRepresentation() {
    Logging.configureLogging(LOGGER);
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  @Override
  public List<Position> getPawns(boolean white) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  @Override
  public List<Position> getRooks(boolean white) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  @Override
  public List<Position> getBishops(boolean white) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  @Override
  public List<Position> getKnights(boolean white) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  @Override
  public List<Position> getQueens(boolean white) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  @Override
  public Position getKing(boolean white) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  @Override
  public Piece getPieceAt(int x, int y) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public int getNbCols() {
    return nbCols;
  }

  public int getNbRows() {
    return nbRows;
  }

  @Override
  public List<Move> getAvailableMoves(int x, int y, Board board) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  @Override
  public boolean isAttacked(int x, int y, Board board) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  @Override
  public boolean isCheck(Board board) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  @Override
  public boolean isCheckMate(Board board) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }
}
