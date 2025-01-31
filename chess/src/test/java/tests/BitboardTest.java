package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pdp.Model.Bitboard;

public class BitboardTest {

  @Test
  /*
   * Test if the bitboard is created with all bits set to 0
   * */
  public void testInitialisation() {
    Bitboard bitboard = new Bitboard();
    assertEquals(0L, bitboard.getBits());
  }

  @Test
  /*
   * Test a given bit have been rightly set to True
   * */
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
  /*
   *
   * */
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
}
