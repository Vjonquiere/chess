package pdp.model.board;

import java.util.List;
import java.util.logging.Logger;
import pdp.utils.Logging;
import pdp.utils.Position;

/** Utility class to remove complexity for BitboardRepresentation. */
public final class BitboardPieces {
  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(BitboardPieces.class.getName());

  /** Private constructor to avoid instantiation. */
  private BitboardPieces() {
    throw new UnsupportedOperationException("Cannot instantiate utility class");
  }

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Get the bitboard that contains all the white pieces, by or on all white pieces bitboards.
   *
   * @return the bitboard containing all white pieces
   */
  public static Bitboard getWhiteBoard(final BitboardRepresentation bitboardRep) {
    return bitboardRep
        .getBitboards()[0]
        .or(bitboardRep.getBitboards()[1])
        .or(bitboardRep.getBitboards()[2])
        .or(bitboardRep.getBitboards()[3])
        .or(bitboardRep.getBitboards()[4])
        .or(bitboardRep.getBitboards()[5]);
  }

  /**
   * Get the bitboard that contains all the black pieces, by or on all black pieces bitboards.
   *
   * @return the bitboard containing all black pieces
   */
  public static Bitboard getBlackBoard(final BitboardRepresentation bitboardRep) {
    return bitboardRep
        .getBitboards()[6]
        .or(bitboardRep.getBitboards()[7])
        .or(bitboardRep.getBitboards()[8])
        .or(bitboardRep.getBitboards()[9])
        .or(bitboardRep.getBitboards()[10])
        .or(bitboardRep.getBitboards()[11]);
  }

  /**
   * Get the positions of the pawns.
   *
   * @param white if true -> white pawns, if false -> black pawns
   * @return A list of the pawns positions for the given color
   */
  public static List<Position> getPawns(
      final boolean white, final BitboardRepresentation bitboardRep) {
    final int bitmapIndex = white ? 5 : 11;
    return bitboardRep.getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the positions of the rooks.
   *
   * @param white if true -> white rooks, if false -> black rooks
   * @return A list of the rooks positions for the given color
   */
  public static List<Position> getRooks(
      final boolean white, final BitboardRepresentation bitboardRep) {
    final int bitmapIndex = white ? 3 : 9;
    return bitboardRep.getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the positions of the bishops.
   *
   * @param white if true -> white bishops, if false -> black bishops
   * @return A list of the bishops positions for the given color
   */
  public static List<Position> getBishops(
      final boolean white, final BitboardRepresentation bitboardRep) {
    final int bitmapIndex = white ? 2 : 8;
    return bitboardRep.getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the positions of the knights.
   *
   * @param white if true -> white knights, if false -> black knights
   * @return A list of the knights positions for the given color
   */
  public static List<Position> getKnights(
      final boolean white, final BitboardRepresentation bitboardRep) {
    final int bitmapIndex = white ? 4 : 10;
    return bitboardRep.getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the positions of the queens.
   *
   * @param white if true -> white queens, if false -> black queens
   * @return A list of the queens positions for the given color
   */
  public static List<Position> getQueens(
      final boolean white, final BitboardRepresentation bitboardRep) {
    final int bitmapIndex = white ? 1 : 7;
    return bitboardRep.getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the positions of the king.
   *
   * @param white if true -> white king, if false -> black king
   * @return A list containing the king position for the given color
   */
  public static List<Position> getKing(
      final boolean white, final BitboardRepresentation bitboardRep) {
    final int bitmapIndex = white ? 0 : 6;
    return bitboardRep.getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the positions of the king. Function optimized with calculations made on bitboards.
   *
   * @param white if true -> white king, if false -> black king
   * @param bitboardRep bitboard representation of the board
   * @return A list containing the king position for the given color
   */
  public static int getKingOpti(final boolean white, final BitboardRepresentation bitboardRep) {
    final int bitmapIndex = white ? 0 : 6;
    return bitboardRep.getBitboards()[bitmapIndex].getSetBits().get(0);
  }

  /**
   * Checks and returns the number of remaining pieces on the board.
   *
   * @return the number of remaining pieces on the board
   */
  public static int nbPiecesRemaining(final BitboardRepresentation bitboardRep) {
    int count = 0;
    final int maxBoardIndex = 11;
    for (int i = 0; i <= maxBoardIndex; i++) {
      final List<Position> occupiedSquares = bitboardRep.getOccupiedSquares(i);
      count += occupiedSquares.size();
    }

    return count;
  }
}
