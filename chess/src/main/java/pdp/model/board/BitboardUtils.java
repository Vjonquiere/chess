package pdp.model.board;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import pdp.utils.Logging;
import pdp.utils.Position;

/** Utility class to remove complexity for BitboardRepresentation. */
public final class BitboardUtils {

  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(BitboardUtils.class.getName());

  /** Private constructor to avoid instantiation. */
  private BitboardUtils() {
    throw new UnsupportedOperationException("Cannot instantiate utility class");
  }

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Get the positions of all bits set to 1 in the given bitboard.
   *
   * @param bitBoardIndex The bitboard to lookUp
   * @return A list of positions
   */
  public static List<Position> getOccupiedSquares(
      final int bitBoardIndex, final BitboardRepresentation bitboardRepresentation) {
    return squaresToPosition(bitboardRepresentation.getBitboards()[bitBoardIndex].getSetBits());
  }

  /**
   * Translate a squares (0..63) to a position (x,y).
   *
   * @param square The square to change to position
   * @return A Position containing the translations
   */
  public static Position squareToPosition(final int square) {
    return new Position(square % 8, square / 8);
  }

  /**
   * Translate a list of squares (0..63) to a list of position (x,y).
   *
   * @param squares The list of squares to change to position
   * @return A new list containing the translations
   */
  public static List<Position> squaresToPosition(final List<Integer> squares) {
    final List<Position> positions = new ArrayList<>();
    for (final Integer i : squares) {
      positions.add(new Position(i % 8, i / 8));
    }
    return positions;
  }

  /**
   * Retrieves the number of columns of the board.
   *
   * @return The horizontal size of the board
   */
  public static int getNbCols(final BitboardRepresentation bitboardRepresentation) {
    return bitboardRepresentation.getNbCols();
  }

  /**
   * Retrieves the number of rows of the board.
   *
   * @return The vertical size of the board
   */
  public static int getNbRows(final BitboardRepresentation bitboardRepresentation) {
    return bitboardRepresentation.getNbRows();
  }
}
