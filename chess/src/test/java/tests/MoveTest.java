package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.exceptions.InvalidPositionException;
import pdp.model.Game;
import pdp.model.board.Move;
import pdp.model.board.PromoteMove;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

public class MoveTest {

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @BeforeEach
  void setUpConsole() {
    System.setOut(new PrintStream(outputStream));
    System.setErr(new PrintStream(outputStream));
  }

  @AfterEach
  void tearDownConsole() {
    System.setOut(originalOut);
    System.setErr(originalErr);
    outputStream.reset();
  }

  @Test
  public void testEquals_sameMove() {
    Position source1 = new Position(4, 1); // "e2"
    Position dest1 = new Position(4, 3); // "e4"
    Move move1 = new Move(source1, dest1, null, true);

    Position source2 = new Position(4, 1); // "e2"
    Position dest2 = new Position(4, 3); // "e4"
    Move move2 = new Move(source2, dest2);

    assertTrue(move1.equals(move2));
  }

  @Test
  public void testEquals_differentMove() {
    Position source1 = new Position(4, 1); // "e2"
    Position dest1 = new Position(4, 3); // "e4"
    Move move1 = new Move(source1, dest1);

    Position source3 = new Position(1, 0); // "b1"
    Position dest3 = new Position(1, 2); // "b3"
    Move move3 = new Move(source3, dest3);

    assertFalse(move1.equals(move3));
  }

  @Test
  public void testEquals_null() {
    Position source1 = new Position(4, 1); // "e2"
    Position dest1 = new Position(4, 3); // "e4"
    Move move1 = new Move(source1, dest1);

    assertFalse(move1.equals(null));
  }

  @Test
  public void testEquals_differentClass() {
    Position source1 = new Position(4, 1); // "e2"
    Position dest1 = new Position(4, 3); // "e4"
    Move move1 = new Move(source1, dest1);

    assertFalse(move1.equals(new Object()));
  }

  @Test
  public void testFromString_validMove() {
    Move move = Move.fromString("e2-e4");
    Position expectedSource = new Position(4, 1); // "e2"
    Position expectedDest = new Position(4, 3); // "e4"

    assertEquals(expectedSource, move.getSource());
    assertEquals(expectedDest, move.getDest());
  }

  @Test
  public void testToString() {
    Position source = new Position(4, 1); // "e2"
    Position dest = new Position(4, 3); // "e4"
    Move move = new Move(source, dest);

    assertEquals("e2-e4", move.toString());
  }

  @Test
  public void testToAlgebraicString() {
    Position source = new Position(4, 1); // "e2"
    Position dest = new Position(3, 2); // "d3"
    Move move =
        new Move(
            source,
            dest,
            new ColoredPiece(Piece.QUEEN, Color.WHITE),
            true,
            new ColoredPiece(Piece.KNIGHT, Color.BLACK),
            true,
            false);

    assertEquals("Qe2xd3+", move.toAlgebraicString());

    source = new Position(4, 1); // "e2"
    dest = new Position(4, 3); // "e4"

    move =
        new Move(
            source, dest, new ColoredPiece(Piece.PAWN, Color.WHITE), false, null, false, false);

    assertEquals("e2-e4", move.toAlgebraicString());
  }

  @Test
  public void testStringToPosition() {
    Position position = Move.stringToPosition("e4");
    Position expected = new Position(4, 3); // "e4" corresponds to (3, 4)

    assertEquals(expected, position);
  }

  @Test
  public void testStringToPiece() {
    assertEquals(Piece.ROOK, Move.stringToPiece("R"));
    assertEquals(Piece.QUEEN, Move.stringToPiece("Q"));
    assertEquals(Piece.KNIGHT, Move.stringToPiece("N"));
    assertEquals(Piece.BISHOP, Move.stringToPiece("B"));
    assertEquals(Piece.PAWN, Move.stringToPiece("P"));
    assertEquals(Piece.KING, Move.stringToPiece("K"));
    assertThrows(IllegalArgumentException.class, () -> Move.stringToPiece("A"));
  }

  @Test
  public void testStringToPositionLengthError() {
    assertThrows(InvalidPositionException.class, () -> Move.stringToPosition("e3-e4-e5"));
  }

  @Test
  public void testStringToPositionColAndRow() {
    assertThrows(InvalidPositionException.class, () -> Move.stringToPosition("e2-e9"));
    assertThrows(InvalidPositionException.class, () -> Move.stringToPosition("e0-e4"));
    assertThrows(InvalidPositionException.class, () -> Move.stringToPosition("!2-e4"));
    assertThrows(InvalidPositionException.class, () -> Move.stringToPosition("i2-e7"));
  }

  @Test
  public void testGetPiece() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);

    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getPiece().getPiece(),
        Piece.PAWN);
  }

  @Test
  public void testIsTake() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);
    Move move3 = new Move(new Position(3, 1), new Position(3, 3));
    game.playMove(move3);
    Move move4 = new Move(new Position(4, 4), new Position(3, 3));
    game.playMove(move4);

    assertEquals(game.getHistory().getCurrentMove().get().getState().getMove().isTake(), true);
  }

  @Test
  public void testIsCheck() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);
    Move move3 = new Move(new Position(3, 1), new Position(3, 3));
    game.playMove(move3);
    Move move4 = new Move(new Position(4, 4), new Position(3, 3));
    game.playMove(move4);

    assertEquals(game.getHistory().getCurrentMove().get().getState().getMove().isCheck(), false);
  }

  @Test
  public void testSetCheck() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);
    Move move3 = new Move(new Position(3, 1), new Position(3, 3));
    game.playMove(move3);
    Move move4 = new Move(new Position(4, 4), new Position(3, 3));
    game.playMove(move4);

    assertEquals(game.getHistory().getCurrentMove().get().getState().getMove().isTake(), true);
    game.getHistory().getCurrentMove().get().getState().getMove().setTake(false);
    assertEquals(game.getHistory().getCurrentMove().get().getState().getMove().isTake(), false);
  }

  @Test
  public void testIsCheckMate() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);
    Move move3 = new Move(new Position(3, 1), new Position(3, 3));
    game.playMove(move3);
    Move move4 = new Move(new Position(4, 4), new Position(3, 3));
    game.playMove(move4);

    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().isCheckMate(), false);
  }

  @Test
  public void testMoveToUCI() {
    Move move = new Move(new Position(4, 1), new Position(4, 3));
    assertEquals("e2e4", move.toUciString());
    assertNotEquals(move.toString(), move.toUciString());

    move = new Move(new Position(4, 6), new Position(4, 4));
    assertEquals("e7e5", move.toUciString());
  }

  @Test
  public void testPromoteMoveToUCI() {
    Move move = new PromoteMove(new Position(4, 6), new Position(4, 7), Piece.ROOK);
    assertEquals("e7e8R", move.toUciString());

    move = new PromoteMove(new Position(4, 1), new Position(4, 0), Piece.ROOK);
    assertEquals("e2e1R", move.toUciString());
    assertNotEquals(move.toString(), move.toUciString());
  }

  @Test
  public void testMoveFromUCIString() {
    Move move = Move.fromUciString("e2e4");
    assertEquals(new Position(4, 1), move.getSource());
    assertEquals(new Position(4, 3), move.getDest());
    assertEquals(Move.fromString("e2-e4"), move);

    move = Move.fromUciString("e7e5");
    assertEquals(new Position(4, 6), move.getSource());
    assertEquals(new Position(4, 4), move.getDest());
    assertEquals(Move.fromString("e7-e5"), move);

    assertThrows(InvalidPositionException.class, () -> Move.fromUciString("j7e8R"));
  }

  @Test
  public void testPromoteMoveFromUCIString() {
    Move move = Move.fromUciString("e2e4Q");
    assertEquals(new Position(4, 1), move.getSource());
    assertEquals(new Position(4, 3), move.getDest());
    assertEquals(Move.fromString("e2-e4"), move);

    /*move = Move.fromUciString("e7e8R");
    assertEquals(new Position(4, 6), move.getSource());
    assertEquals(new Position(4, 4), move.getDest());
    assertEquals(Move.fromString("e7-e5"), move);*/
  }
}
