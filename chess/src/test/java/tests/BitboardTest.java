package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pdp.Model.Bitboard;

public class BitboardTest {

  @Test
  public void testInitialisation() {
    Bitboard bitboard = new Bitboard();
    assertEquals(0L, bitboard.getBits()); // Test if all bits are set to 0
  }

  @Test
  public void testSetBit() {
    Bitboard bitboard = new Bitboard();
    bitboard.setBit(3);
    assertTrue(bitboard.getBit(3));

    bitboard.setBit(65); // Test the cyclic aspect
    assertTrue(bitboard.getBit(1));

    bitboard.setBit(0);
    assertTrue(bitboard.getBit(0));

    bitboard.setBit(-1); // Test reference to negative index
    assertTrue(bitboard.getBit(63));
  }

  @Test
  public void testClearBit() {
    Bitboard bitboard = new Bitboard();

    bitboard.setBit(3);
    assertTrue(bitboard.getBit(3));
    bitboard.clearBit(3);
    assertFalse(bitboard.getBit(3));

    bitboard.clearBit(0); // Clear on an already False bit
    assertFalse(bitboard.getBit(0));

    bitboard.setBit(-1);
    assertTrue(bitboard.getBit(63));
    bitboard.clearBit(-1); // Cyclic clear test
    assertFalse(bitboard.getBit(63));
  }

  @Test
  public void testToggleBit() {
    Bitboard bitboard = new Bitboard();
    bitboard.setBit(3);
    assertTrue(bitboard.getBit(3));
    bitboard.toggleBit(3);
    assertFalse(bitboard.getBit(3));
    bitboard.toggleBit(3);
    assertTrue(bitboard.getBit(3));
  }

  @Test
  public void testMoveUp() {
    Bitboard bitboard = new Bitboard();
    bitboard.setBit(3);
    assertTrue(bitboard.getBit(3));
    assertFalse(bitboard.moveUp().getBit(3));
    assertTrue(bitboard.moveUp().getBit(11));
    bitboard.clearBits();

    // Test on overflow (bit is on the last row)
    bitboard.setBit(63);
    assertTrue(bitboard.getBit(63));
    assertFalse(bitboard.moveUp().getBit(63));
    assertFalse(bitboard.moveUp().getBit(7));
    bitboard.clearBits();

    // Test on multiple bits to be moved
    bitboard.setBit(7);
    assertTrue(bitboard.getBit(7));
    bitboard.setBit(8);
    assertTrue(bitboard.getBit(8));
    Bitboard tmpBitboard = bitboard.moveUp();
    assertFalse(tmpBitboard.getBit(7));
    assertFalse(tmpBitboard.getBit(8));
    assertTrue(tmpBitboard.getBit(16));
    assertTrue(tmpBitboard.getBit(15));
    bitboard.clearBits();
  }
}
