package pdp.model.board;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import pdp.utils.Logging;
import pdp.utils.Position;

public class BitboardUtils {
  private static final Logger LOGGER = Logger.getLogger(BitboardUtils.class.getName());

  private BitboardUtils(BitboardRepresentation bitboardRepresentation) {
    throw new UnsupportedOperationException("Cannot instantiate utility class");
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
  public static List<Position> getOccupiedSquares(
      int bitBoardIndex, BitboardRepresentation bitboardRepresentation) {
    return squaresToPosition(bitboardRepresentation.getBitboards()[bitBoardIndex].getSetBits());
  }

  /**
   * Translate a squares (0..63) to a position (x,y)
   *
   * @param square The square to change to position
   * @return A Position containing the translations
   */
  public static Position squareToPosition(int square) {
    return new Position(square % 8, square / 8);
  }

  /**
   * Translate a list of squares (0..63) to a list of position (x,y)
   *
   * @param squares The list of squares to change to position
   * @return A new list containing the translations
   */
  public static List<Position> squaresToPosition(List<Integer> squares) {
    List<Position> positions = new ArrayList<>();
    for (Integer i : squares) {
      positions.add(new Position(i % 8, i / 8));
    }
    return positions;
  }

  /**
   * @return The horizontal size of the board
   */
  public static int getNbCols(BitboardRepresentation bitboardRepresentation) {
    return bitboardRepresentation.getNbCols();
  }

  /**
   * @return The vertical size of the board
   */
  public static int getNbRows(BitboardRepresentation bitboardRepresentation) {
    return bitboardRepresentation.getNbRows();
  }
}
