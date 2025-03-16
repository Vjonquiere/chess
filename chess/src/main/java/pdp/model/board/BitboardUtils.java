package pdp.model.board;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import pdp.utils.Logging;
import pdp.utils.Position;

public class BitboardUtils {
  private static final Logger LOGGER = Logger.getLogger(BitboardUtils.class.getName());
  private BitboardRepresentation bitboardRepresentation;

  public BitboardUtils(BitboardRepresentation bitboardRepresentation) {
    this.bitboardRepresentation = bitboardRepresentation;
  }

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Get the positions of all bits set to 1 in the given bitboard
   *
   * @param bitBoardIndex The bitboard to lookUp
   * @return A list of positions
   */
  protected List<Position> getOccupiedSquares(int bitBoardIndex) {
    return squaresToPosition(
        this.bitboardRepresentation.getBitboards()[bitBoardIndex].getSetBits());
  }

  /**
   * Translate a squares (0..63) to a position (x,y)
   *
   * @param square The square to change to position
   * @return A Position containing the translations
   */
  protected Position squareToPosition(int square) {
    return new Position(square % 8, square / 8);
  }

  /**
   * Translate a list of squares (0..63) to a list of position (x,y)
   *
   * @param squares The list of squares to change to position
   * @return A new list containing the translations
   */
  protected List<Position> squaresToPosition(List<Integer> squares) {
    List<Position> positions = new ArrayList<>();
    for (Integer i : squares) {
      positions.add(new Position(i % 8, i / 8));
    }
    return positions;
  }

  /**
   * @return The horizontal size of the board
   */
  public int getNbCols() {
    return this.bitboardRepresentation.nbCols;
  }

  /**
   * @return The vertical size of the board
   */
  public int getNbRows() {
    return this.bitboardRepresentation.nbRows;
  }
}
