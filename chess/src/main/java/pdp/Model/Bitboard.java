package pdp.Model;

public class Bitboard {
  long bitboard;

  public Bitboard() {}

  /** */
  public void setBit(int square) {
    bitboard = bitboard | (1L << square);
  }

  public void clearBit(int square) {
    bitboard = bitboard & ~(1L << square);
  }

  public void toggleBit(int square) {
    bitboard = bitboard ^ (1L << square);
  }

  public boolean getBit(int square) {
    return (bitboard & (1L << square)) != 0;
  }

  public long getBits() {
    return bitboard;
  }
}
