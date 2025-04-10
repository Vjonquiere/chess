package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pdp.model.piece.Piece;

public class PiecesTest {

  @BeforeAll
  public static void setUpLocale() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @Test
  public void testPawnRep() {
    assertEquals(
        "" + Piece.PAWN.getCharRepresentation(true) + Piece.PAWN.getCharRepresentation(false),
        "Pp");
  }

  @Test
  public void testRookRep() {
    assertEquals(
        "" + Piece.ROOK.getCharRepresentation(true) + Piece.ROOK.getCharRepresentation(false),
        "Rr");
  }

  @Test
  public void testQueenRep() {
    assertEquals(
        "" + Piece.QUEEN.getCharRepresentation(true) + Piece.QUEEN.getCharRepresentation(false),
        "Qq");
  }

  @Test
  public void testKingRep() {
    assertEquals(
        "" + Piece.KING.getCharRepresentation(true) + Piece.KING.getCharRepresentation(false),
        "Kk");
  }

  @Test
  public void testBishopRep() {
    assertEquals(
        "" + Piece.BISHOP.getCharRepresentation(true) + Piece.BISHOP.getCharRepresentation(false),
        "Bb");
  }

  @Test
  public void testKnightRep() {
    assertEquals(
        "" + Piece.KNIGHT.getCharRepresentation(true) + Piece.KNIGHT.getCharRepresentation(false),
        "Nn");
  }
}
