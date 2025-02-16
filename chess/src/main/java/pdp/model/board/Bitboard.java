package pdp.model.board;

import java.util.ArrayList;

public class Bitboard {
  long bitboard;

  public Bitboard() {}

  public Bitboard(long bitboard) {
    this.bitboard = bitboard;
  }

  /**
   * Set the given bit to True
   *
   * @param square Position of the bit to set
   */
  public void setBit(int square) {
    bitboard = bitboard | (1L << square);
  }

  /**
   * Set the given bit to False
   *
   * @param square Position of the bit to set
   */
  public void clearBit(int square) {
    bitboard = bitboard & ~(1L << square);
  }

  /**
   * Toggle the value of the given bit (False => True, True => False)
   *
   * @param square Position of the bit to set
   */
  public void toggleBit(int square) {
    bitboard = bitboard ^ (1L << square);
  }

  /**
   * Get the value of the given bit
   *
   * @param square Position of the bit
   * @return Value of the given bit
   */
  public boolean getBit(int square) {
    return (bitboard & (1L << square)) != 0;
  }

  /**
   * Get the current value of the bitboard
   *
   * @return The complete bitboard
   */
  public long getBits() {
    return bitboard;
  }

  /**
   * Move up all the bits of the bitboard
   *
   * @return A new bitboard with the bits correctly sets
   */
  public Bitboard moveUp() {
    return new Bitboard((bitboard << 8));
  }

  /**
   * Move down all the bits of the bitboard
   *
   * @return A new bitboard with the bits correctly sets
   */
  public Bitboard moveDown() {
    return new Bitboard((bitboard >>> 8));
  }

  /**
   * Move right all the bits of the bitboard
   *
   * @return A new bitboard with the bits correctly sets
   */
  public Bitboard moveRight() {
    return new Bitboard((bitboard << 1) & 0xFEFEFEFEFEFEFEFEL);
  }

  /**
   * Move left all the bits of the bitboard
   *
   * @return A new bitboard with the bits correctly sets
   */
  public Bitboard moveLeft() {
    return new Bitboard((bitboard >>> 1) & 0x7F7F7F7F7F7F7F7FL);
  }

  /**
   * Move up-right all the bits of the bitboard
   *
   * @return A new bitboard with the bits correctly sets
   */
  public Bitboard moveUpRight() {
    return new Bitboard(bitboard).moveUp().moveRight();
  }

  /**
   * Move up-left all the bits of the bitboard
   *
   * @return A new bitboard with the bits correctly sets
   */
  public Bitboard moveUpLeft() {
    return new Bitboard(bitboard).moveUp().moveLeft();
  }

  /**
   * Move down-right all the bits of the bitboard
   *
   * @return A new bitboard with the bits correctly sets
   */
  public Bitboard moveDownRight() {
    return new Bitboard(bitboard).moveDown().moveRight();
  }

  /**
   * Move down-left all the bits of the bitboard
   *
   * @return A new bitboard with the bits correctly sets
   */
  public Bitboard moveDownLeft() {
    return new Bitboard(bitboard).moveDown().moveLeft();
  }

  /** Set all the bits to False */
  public void clearBits() {
    bitboard = 0L;
  }

  /**
   * Count the number of bits set to 1
   *
   * @return The number of bits set to true
   */
  public int bitCount() {
    return Long.bitCount(bitboard);
  }

  /**
   * Get the bits set to 1
   *
   * @return Array list containing the square set to 1
   */
  public ArrayList<Integer> getSetBits() {
    ArrayList<Integer> setBits = new ArrayList<>();
    for (int square = 0; square < 64; square++) {
      if (getBit(square)) {
        setBits.add(square);
      }
    }
    return setBits;
  }

  /**
   * Make a bitwise AND between the two bitboards
   *
   * @param b the second bitboard
   * @return A new bitboard containing the AND operation
   */
  public Bitboard and(Bitboard b) {
    return new Bitboard(bitboard & b.getBits());
  }

  /**
   * Make a bitwise OR between the two bitboards
   *
   * @param b the second bitboard
   * @return A new bitboard containing the OR operation
   */
  public Bitboard or(Bitboard b) {
    return new Bitboard(bitboard | b.getBits());
  }

  /**
   * Make a bitwise XOR between the two bitboards
   *
   * @param b the second bitboard
   * @return A new bitboard containing the XOR operation
   */
  public Bitboard xor(Bitboard b) {
    return new Bitboard(bitboard ^ b.getBits());
  }

  /**
   * Invert all the bits of the bitboard
   *
   * @return A new bitboard with the inverted bits
   */
  public Bitboard not() {
    return new Bitboard(~bitboard);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Bitboard = 0x").append(Long.toHexString(bitboard)).append("\n");
    for (int x = 7; x >= 0; x--) {
      for (int y = 0; y < 8; y++) {
        sb.append(getBit(x * 8 + y) ? '1' : '0');
        if (y != 7) sb.append("|");
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Bitboard) {
      return this.bitboard == ((Bitboard) obj).bitboard;
    }
    return false;
  }

  public Bitboard getCopy() {
    return new Bitboard(this.bitboard);
  }
}
