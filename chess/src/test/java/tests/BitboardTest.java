package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pdp.model.board.Bitboard;

public class BitboardTest {

  @BeforeAll
  public static void setUpLocale() {
      Locale.setDefault(Locale.ENGLISH);
  }

  @Test
  public void testInitialisation() {
    Bitboard bitboard = new Bitboard();
    assertEquals(0L, bitboard.getBits()); // Test if all bits are set to 0
  }

  @Test
  public void testClearBits() {
    Bitboard bitboard = new Bitboard();
    assertEquals(0L, bitboard.getBits());
    bitboard.setBit(1);
    bitboard.setBit(54);
    assertNotEquals(0L, bitboard.getBits()); // should be equal to 0x40000000000002
    bitboard.clearBits();
    assertEquals(0L, bitboard.getBits());
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
  public void testGetSetBits() {
    Bitboard bitboard = new Bitboard();
    bitboard.setBit(3);
    assertTrue(bitboard.getBit(3));
    assertEquals(List.of(3), bitboard.getSetBits());
    bitboard.setBit(10);
    assertTrue(bitboard.getBit(10));
    assertEquals(List.of(3, 10), bitboard.getSetBits());
  }

  @Test
  public void testAnd() {
    Bitboard bitboard = new Bitboard(1095216660480L);
    Bitboard bitboard2 = new Bitboard(1026497183744L);
    assertEquals(1026497183744L, bitboard.and(bitboard2).getBits()); // Classic AND

    Bitboard bitboard3 = new Bitboard(255L);
    Bitboard bitboard4 = new Bitboard(65280L);
    assertEquals(0L, bitboard3.and(bitboard4).getBits()); // Empty AND

    Bitboard bitboard5 = new Bitboard(4278190080L);
    Bitboard bitboard6 = new Bitboard(4278190080L);
    assertEquals(4278190080L, bitboard5.and(bitboard6).getBits()); // Same bitboards
  }

  @Test
  public void testOr() {
    Bitboard bitboard = new Bitboard(1095216660480L);
    Bitboard bitboard2 = new Bitboard(1026497183744L);
    assertEquals(1095216660480L, bitboard.or(bitboard2).getBits()); // Classic OR

    Bitboard bitboard3 = new Bitboard(255L);
    Bitboard bitboard4 = new Bitboard(65280L);
    assertEquals(65535L, bitboard3.or(bitboard4).getBits());

    Bitboard bitboard5 = new Bitboard(4278190080L);
    Bitboard bitboard6 = new Bitboard(4278190080L);
    assertEquals(4278190080L, bitboard5.or(bitboard6).getBits()); // Same bitboards
  }

  @Test
  public void testXor() {
    Bitboard bitboard = new Bitboard(1095216660480L);
    Bitboard bitboard2 = new Bitboard(1026497183744L);
    assertEquals(68719476736L, bitboard.xor(bitboard2).getBits()); // Classic XOR

    Bitboard bitboard3 = new Bitboard(255L);
    Bitboard bitboard4 = new Bitboard(65280L);
    assertEquals(65535L, bitboard3.xor(bitboard4).getBits()); // Same as OR

    Bitboard bitboard5 = new Bitboard(4278190080L);
    Bitboard bitboard6 = new Bitboard(4278190080L);
    assertEquals(0L, bitboard5.xor(bitboard6).getBits()); // Same bitboards
  }

  @Test
  public void testBitCount() {
    Bitboard bitboard = new Bitboard();
    assertEquals(0, bitboard.bitCount());
    bitboard.setBit(3);
    assertEquals(1, bitboard.bitCount());
    bitboard.setBit(0);
    assertEquals(2, bitboard.bitCount());
    bitboard.clearBit(3);
    assertEquals(1, bitboard.bitCount());
    bitboard.clearBits();
    assertEquals(0, bitboard.bitCount());
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

  @Test
  public void testMoveDown() {
    Bitboard bitboard = new Bitboard();
    bitboard.setBit(11);
    assertTrue(bitboard.getBit(11));
    assertFalse(bitboard.moveDown().getBit(11));
    assertTrue(bitboard.moveDown().getBit(3));
    bitboard.clearBits();

    // Test on overflow (bit is on the first row)
    bitboard.setBit(7);
    assertTrue(bitboard.getBit(7));
    assertFalse(bitboard.moveDown().getBit(7));
    assertFalse(bitboard.moveDown().getBit(63));
    bitboard.clearBits();

    // Test on multiple bits to be moved
    bitboard.setBit(15);
    assertTrue(bitboard.getBit(15));
    bitboard.setBit(16);
    assertTrue(bitboard.getBit(16));
    Bitboard tmpBitboard = bitboard.moveDown();
    assertFalse(tmpBitboard.getBit(15));
    assertFalse(tmpBitboard.getBit(16));
    assertTrue(tmpBitboard.getBit(7));
    assertTrue(tmpBitboard.getBit(8));
    bitboard.clearBits();
  }

  @Test
  public void testMoveLeft() {
    Bitboard bitboard = new Bitboard();
    bitboard.setBit(11);
    assertTrue(bitboard.getBit(11));
    assertFalse(bitboard.moveLeft().getBit(11));
    assertTrue(bitboard.moveLeft().getBit(10));
    bitboard.clearBits();

    // Test on overflow (bit is on the first column)
    bitboard.setBit(8);
    assertTrue(bitboard.getBit(8));
    assertFalse(bitboard.moveLeft().getBit(8));
    assertFalse(bitboard.moveLeft().getBit(15));
    bitboard.clearBits();

    // Test on multiple bits to be moved
    bitboard.setBit(15);
    assertTrue(bitboard.getBit(15));
    bitboard.setBit(7);
    assertTrue(bitboard.getBit(7));
    Bitboard tmpBitboard = bitboard.moveLeft();
    assertFalse(tmpBitboard.getBit(15));
    assertFalse(tmpBitboard.getBit(7));
    assertTrue(tmpBitboard.getBit(14));
    assertTrue(tmpBitboard.getBit(6));
    bitboard.clearBits();
  }

  @Test
  public void testMoveRight() {
    Bitboard bitboard = new Bitboard();
    bitboard.setBit(11);
    assertTrue(bitboard.getBit(11));
    assertFalse(bitboard.moveRight().getBit(11));
    assertTrue(bitboard.moveRight().getBit(12));
    bitboard.clearBits();

    // Test on overflow (bit is on the last column)
    bitboard.setBit(7);
    assertTrue(bitboard.getBit(7));
    assertFalse(bitboard.moveRight().getBit(7));
    assertFalse(bitboard.moveRight().getBit(0));
    bitboard.clearBits();

    // Test on multiple bits to be moved
    bitboard.setBit(12);
    assertTrue(bitboard.getBit(12));
    bitboard.setBit(4);
    assertTrue(bitboard.getBit(4));
    Bitboard tmpBitboard = bitboard.moveRight();
    assertFalse(tmpBitboard.getBit(12));
    assertFalse(tmpBitboard.getBit(4));
    assertTrue(tmpBitboard.getBit(13));
    assertTrue(tmpBitboard.getBit(5));
    bitboard.clearBits();
  }

  // TODO: Can enforce the 4 following tests
  //  by adding a test for only one side overflow
  //  (because for now it's only for the worst case)
  @Test
  public void testMoveUpRight() {
    Bitboard bitboard = new Bitboard();
    bitboard.setBit(11);
    assertTrue(bitboard.getBit(11));
    assertFalse(bitboard.moveUpRight().getBit(11));
    assertTrue(bitboard.moveUpRight().getBit(20));
    bitboard.clearBits();

    // Test on overflow (bit is on the last square)
    bitboard.setBit(63);
    assertTrue(bitboard.getBit(63));
    assertFalse(bitboard.moveUpRight().getBit(63));
    assertFalse(bitboard.moveUpRight().getBit(0));
    bitboard.clearBits();

    // Test on multiple bits to be moved
    bitboard.setBit(12);
    assertTrue(bitboard.getBit(12));
    bitboard.setBit(4);
    assertTrue(bitboard.getBit(4));
    Bitboard tmpBitboard = bitboard.moveUpRight();
    assertFalse(tmpBitboard.getBit(12));
    assertFalse(tmpBitboard.getBit(4));
    assertTrue(tmpBitboard.getBit(21));
    assertTrue(tmpBitboard.getBit(13));
    bitboard.clearBits();
  }

  @Test
  public void testMoveUpLeft() {
    Bitboard bitboard = new Bitboard();
    bitboard.setBit(11);
    assertTrue(bitboard.getBit(11));
    assertFalse(bitboard.moveUpLeft().getBit(11));
    assertTrue(bitboard.moveUpLeft().getBit(18));
    bitboard.clearBits();

    // Test on overflow (bit is on the last row and first column)
    bitboard.setBit(56);
    assertTrue(bitboard.getBit(56));
    assertFalse(bitboard.moveUpLeft().getBit(56));
    assertFalse(bitboard.moveUpLeft().getBit(7));
    bitboard.clearBits();

    // Test on multiple bits to be moved
    bitboard.setBit(12);
    assertTrue(bitboard.getBit(12));
    bitboard.setBit(4);
    assertTrue(bitboard.getBit(4));
    Bitboard tmpBitboard = bitboard.moveUpLeft();
    assertFalse(tmpBitboard.getBit(12));
    assertFalse(tmpBitboard.getBit(4));
    assertTrue(tmpBitboard.getBit(11));
    assertTrue(tmpBitboard.getBit(19));
    bitboard.clearBits();
  }

  @Test
  public void testMoveDownRight() {
    Bitboard bitboard = new Bitboard();
    bitboard.setBit(11);
    assertTrue(bitboard.getBit(11));
    assertFalse(bitboard.moveDownRight().getBit(11));
    assertTrue(bitboard.moveDownRight().getBit(4));
    bitboard.clearBits();

    // Test on overflow (bit is on the fist row and last column)
    bitboard.setBit(7);
    assertTrue(bitboard.getBit(7));
    assertFalse(bitboard.moveDownRight().getBit(7));
    assertFalse(bitboard.moveDownRight().getBit(56));
    bitboard.clearBits();

    // Test on multiple bits to be moved
    bitboard.setBit(12);
    assertTrue(bitboard.getBit(12));
    bitboard.setBit(17);
    assertTrue(bitboard.getBit(17));
    Bitboard tmpBitboard = bitboard.moveDownRight();
    assertFalse(tmpBitboard.getBit(12));
    assertFalse(tmpBitboard.getBit(17));
    assertTrue(tmpBitboard.getBit(5));
    assertTrue(tmpBitboard.getBit(10));
    bitboard.clearBits();
  }

  @Test
  public void testMoveDownLeft() {
    Bitboard bitboard = new Bitboard();
    bitboard.setBit(11);
    assertTrue(bitboard.getBit(11));
    assertFalse(bitboard.moveDownLeft().getBit(11));
    assertTrue(bitboard.moveDownLeft().getBit(2));
    bitboard.clearBits();

    // Test on overflow (bit is on the fist row and first column)
    bitboard.setBit(0);
    assertTrue(bitboard.getBit(0));
    assertFalse(bitboard.moveDownLeft().getBit(0));
    assertFalse(bitboard.moveDownLeft().getBit(63));
    bitboard.clearBits();

    // Test on multiple bits to be moved
    bitboard.setBit(12);
    assertTrue(bitboard.getBit(12));
    bitboard.setBit(17);
    assertTrue(bitboard.getBit(17));
    Bitboard tmpBitboard = bitboard.moveDownLeft();
    assertFalse(tmpBitboard.getBit(12));
    assertFalse(tmpBitboard.getBit(17));
    assertTrue(tmpBitboard.getBit(3));
    assertTrue(tmpBitboard.getBit(8));
    bitboard.clearBits();
  }

  @Test
  public void testToString() {
    Bitboard bitboard = new Bitboard();
    String notInit =
        "Bitboard = 0x0\n0|0|0|0|0|0|0|0\n0|0|0|0|0|0|0|0\n0|0|0|0|0|0|0|0\n0|0|0|0|0|0|0|0\n0|0|0|0|0|0|0|0\n0|0|0|0|0|0|0|0\n0|0|0|0|0|0|0|0\n0|0|0|0|0|0|0|0\n";
    assertEquals(notInit, bitboard.toString());
    bitboard.setBit(1);
  }
}
