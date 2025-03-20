package pdp.model.board;

import java.util.List;
import java.util.logging.Logger;
import pdp.utils.Logging;
import pdp.utils.Position;

public class BitboardPieces {
  private static final Logger LOGGER = Logger.getLogger(BitboardPieces.class.getName());

  private BitboardPieces() {
    throw new UnsupportedOperationException("Cannot instantiate utility class");
  }

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Get the bitboard that contains all the white pieces, by or on all white pieces bitboards
   *
   * @return the bitboard containing all white pieces
   */
  public static Bitboard getWhiteBoard(BitboardRepresentation bitboardRepresentation) {
    return bitboardRepresentation
        .getBitboards()[0]
        .or(bitboardRepresentation.getBitboards()[1])
        .or(bitboardRepresentation.getBitboards()[2])
        .or(bitboardRepresentation.getBitboards()[3])
        .or(bitboardRepresentation.getBitboards()[4])
        .or(bitboardRepresentation.getBitboards()[5]);
  }

  /**
   * Get the bitboard that contains all the black pieces, by or on all black pieces bitboards
   *
   * @return the bitboard containing all black pieces
   */
  public static Bitboard getBlackBoard(BitboardRepresentation bitboardRepresentation) {
    return bitboardRepresentation
        .getBitboards()[6]
        .or(bitboardRepresentation.getBitboards()[7])
        .or(bitboardRepresentation.getBitboards()[8])
        .or(bitboardRepresentation.getBitboards()[9])
        .or(bitboardRepresentation.getBitboards()[10])
        .or(bitboardRepresentation.getBitboards()[11]);
  }

  /**
   * Get the positions of the pawns
   *
   * @param white if true -> white pawns, if false -> black pawns
   * @return A list of the pawns positions for the given color
   */
  public static List<Position> getPawns(
      boolean white, BitboardRepresentation bitboardRepresentation) {
    int bitmapIndex = white ? 5 : 11;
    return bitboardRepresentation.getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the positions of the rooks
   *
   * @param white if true -> white rooks, if false -> black rooks
   * @return A list of the rooks positions for the given color
   */
  public static List<Position> getRooks(
      boolean white, BitboardRepresentation bitboardRepresentation) {
    int bitmapIndex = white ? 3 : 9;
    return bitboardRepresentation.getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the positions of the bishops
   *
   * @param white if true -> white bishops, if false -> black bishops
   * @return A list of the bishops positions for the given color
   */
  public static List<Position> getBishops(
      boolean white, BitboardRepresentation bitboardRepresentation) {
    int bitmapIndex = white ? 2 : 8;
    return bitboardRepresentation.getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the positions of the knights
   *
   * @param white if true -> white knights, if false -> black knights
   * @return A list of the knights positions for the given color
   */
  public static List<Position> getKnights(
      boolean white, BitboardRepresentation bitboardRepresentation) {
    int bitmapIndex = white ? 4 : 10;
    return bitboardRepresentation.getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the positions of the queens
   *
   * @param white if true -> white queens, if false -> black queens
   * @return A list of the queens positions for the given color
   */
  public static List<Position> getQueens(
      boolean white, BitboardRepresentation bitboardRepresentation) {
    int bitmapIndex = white ? 1 : 7;
    return bitboardRepresentation.getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the positions of the king
   *
   * @param white if true -> white king, if false -> black king
   * @return A list containing the king position for the given color
   */
  public static List<Position> getKing(
      boolean white, BitboardRepresentation bitboardRepresentation) {
    int bitmapIndex = white ? 0 : 6;
    return bitboardRepresentation.getOccupiedSquares(bitmapIndex);
  }

  public static int getKingOpti(boolean white, BitboardRepresentation bitboardRepresentation) {
    int bitmapIndex = white ? 0 : 6;
    return bitboardRepresentation.getBitboards()[bitmapIndex].getSetBits().get(0);
  }

  /**
   * Checks and returns the number of remaining pieces on the board
   *
   * @return the number of remaining pieces on the board
   */
  public static int nbPiecesRemaining(BitboardRepresentation bitboardRepresentation) {
    int count = 0;
    int maxBoardIndex = 11;
    for (int i = 0; i <= maxBoardIndex; i++) {
      List<Position> occupiedSquares = bitboardRepresentation.getOccupiedSquares(i);
      count += occupiedSquares.size();
    }

    return count;
  }
}
