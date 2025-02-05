package pdp.model;

import java.util.List;

import javafx.geometry.Pos;
import pdp.utils.Position;

public class BitboardRepresentation implements BoardRepresentation {
  private Bitboard[] board;
  private int nbCols = 8;
  private int nbRows = 8;

  public BitboardRepresentation() {
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

  /**
   * Checks if the given pawn in argument is promoting (has reached the last row)
   *
   * @param x The x postion of the pawn that is potentially promoting (checking for this pawn)
   * @param y The y postion of the pawn that is potentially promoting (checking for this pawn)
   * @param white boolean value to know if the pawn is white
   * @param board The board of the game
   * @return true if the pawn given as an argument is promoting, false otherwise.
   */
  @Override
  public boolean isPawnPromoting(int x, int y, boolean white, Board board) {
    // Retrieve bitboard B1 of the pawn that was just moved
    // Check if bit at Position(x,y) is at 1. If yes --> return true, else false

    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  /**
   * Replaces pawnToPromote with newPiece. Bitboards get changed
   *
   * @param x The x position of the pawn that has reached the last row and gets promoted
   * @param x The y position of the pawn that has reached the last row and gets promoted
   * @param white boolean value to know if the pawn is white
   * @param newPiece The piece asked by the player that is replacing the promoting pawn
   * @param board The board of the game
   */
  @Override
  public void promotePawn(int x, int y, boolean white, Piece newPiece, Board board) {
    // Retrieve the bitboard B1 corresponding to the pawns of color {white}
    // Retrieve the bitboard B2 corresponding to the pieces of type {newPiece} and of color {white}

    // Change 1 to 0 in B1 at Position(x,y)
    // Change 0 to 1 in B2 at Position(x,y)

    // TO DO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }
}
