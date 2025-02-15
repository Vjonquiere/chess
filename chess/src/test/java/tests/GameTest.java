package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pdp.exceptions.IllegalMoveException;
import pdp.model.Game;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.Move;
import pdp.utils.Position;

public class GameTest {
  @Test
  public void playMoveTest() {

    // Correctly play move
    Game game = Game.initialize(false, false, null, null);
    BitboardRepresentation bitboards = new BitboardRepresentation();
    game.playMove(new Move(new Position(0, 1), new Position(0, 2)));
    bitboards.movePiece(new Position(0, 1), new Position(0, 2));
    assertEquals(game.getBoard().getBoardRep(), bitboards);

    // Play move only in game
    game.playMove(new Move(new Position(1, 6), new Position(1, 5)));
    assertNotEquals(game.getBoard().getBoardRep(), bitboards);
    bitboards.movePiece(new Position(1, 6), new Position(1, 5)); // Play move in bitboards
    assertEquals(game.getBoard().getBoardRep(), bitboards);

    // Tests to add
    // 6. try a move that is not a classical or special move
  }

  @Test
  public void playEnPassantTest() {
    Game game = Game.initialize(false, false, null, null);
    game.playMove(new Move(new Position(0, 1), new Position(0, 3)));
    game.playMove(new Move(new Position(0, 6), new Position(0, 5)));
    game.playMove(new Move(new Position(0, 3), new Position(0, 4)));
    game.playMove(new Move(new Position(1, 6), new Position(1, 4)));
    game.playMove(new Move(new Position(0, 4), new Position(1, 5)));
    BitboardRepresentation bitboards = new BitboardRepresentation();
    bitboards.deletePieceAt(1, 6);
    bitboards.movePiece(new Position(0, 1), new Position(1, 5));
    bitboards.movePiece(new Position(0, 6), new Position(0, 5));
    assertEquals(bitboards, game.getBoard().getBoardRep());
  }

  @Test
  public void playEnPassantIsCheckTest() {
    Game game = Game.initialize(false, false, null, null);
    game.playMove(new Move(new Position(4, 1), new Position(4, 2)));
    game.playMove(new Move(new Position(7, 6), new Position(7, 4)));
    game.playMove(new Move(new Position(4, 2), new Position(4, 3)));
    game.playMove(new Move(new Position(7, 7), new Position(7, 5)));
    game.playMove(new Move(new Position(4, 3), new Position(4, 4)));
    game.playMove(new Move(new Position(7, 5), new Position(4, 5)));
    game.playMove(new Move(new Position(7, 1), new Position(7, 2)));
    game.playMove(new Move(new Position(3, 6), new Position(3, 4)));

    assertThrows(
        IllegalMoveException.class,
        () -> {
          game.playMove(new Move(new Position(4, 4), new Position(3, 5)));
        });
  }

  @Test
  public void playDoublePushTest() {
    Game game = Game.initialize(false, false, null, null);
    game.playMove(new Move(new Position(1, 1), new Position(1, 3)));
    BitboardRepresentation bitboards = new BitboardRepresentation();
    bitboards.movePiece(new Position(1, 1), new Position(1, 3));
    assertEquals(bitboards, game.getBoard().getBoardRep());
  }

  @Test
  public void playDoublePushIsCheckTest() {
    Game game = Game.initialize(false, false, null, null);
    game.playMove(new Move(new Position(0, 1), new Position(0, 2)));
    game.playMove(new Move(new Position(3, 6), new Position(3, 4)));
    game.playMove(new Move(new Position(0, 2), new Position(0, 3)));
    game.playMove(new Move(new Position(3, 7), new Position(3, 6)));
    game.playMove(new Move(new Position(0, 3), new Position(0, 4)));
    game.playMove(new Move(new Position(3, 6), new Position(3, 5)));
    game.playMove(new Move(new Position(1, 1), new Position(1, 2)));
    game.playMove(new Move(new Position(3, 5), new Position(1, 3)));
    assertThrows(
        IllegalMoveException.class,
        () -> {
          game.playMove(new Move(new Position(3, 1), new Position(3, 3)));
        });
  }

  @Test
  public void wrongTurnForWhitePlayerTest() {
    Game game = Game.initialize(false, false, null, null);
    game.playMove(new Move(new Position(0, 1), new Position(0, 2)));
    assertThrows(
        IllegalMoveException.class,
        () -> {
          game.playMove(new Move(new Position(0, 2), new Position(0, 3)));
        });
  }

  @Test
  public void wrongTurnForBlackPlayerTest() {
    Game game = Game.initialize(false, false, null, null);
    game.playMove(new Move(new Position(0, 1), new Position(0, 2)));
    game.playMove(new Move(new Position(0, 6), new Position(0, 5)));
    assertThrows(
        IllegalMoveException.class,
        () -> {
          game.playMove(new Move(new Position(0, 5), new Position(0, 4)));
        });
  }

  @Test
  public void pinnedPieceTest() {
    Game game = Game.initialize(false, false, null, null);
    game.playMove(new Move(new Position(0, 1), new Position(0, 2)));
    game.playMove(new Move(new Position(2, 6), new Position(2, 4)));
    game.playMove(new Move(new Position(0, 2), new Position(0, 3)));
    game.playMove(new Move(new Position(3, 7), new Position(0, 4)));
    assertThrows(
        IllegalMoveException.class,
        () -> {
          game.playMove(new Move(new Position(3, 1), new Position(3, 2)));
        });
  }

  @Test
  public void threefoldRepetitionTest() {
    Game game = Game.initialize(false, false, null, null);
    game.playMove(Move.fromString("b1-c3"));
    game.playMove(Move.fromString("g8-f6"));
    game.playMove(Move.fromString("c3-b1"));
    game.playMove(Move.fromString("f6-g8"));
    game.playMove(Move.fromString("b1-c3"));
    game.playMove(Move.fromString("g8-f6"));
    game.playMove(Move.fromString("c3-b1"));
    game.playMove(Move.fromString("f6-g8"));
    game.playMove(Move.fromString("b1-c3"));
    game.playMove(Move.fromString("g8-f6"));
    game.playMove(Move.fromString("c3-b1"));
    game.playMove(Move.fromString("f6-g8"));
    assertTrue(game.getGameState().isThreefoldRepetition());
    assertTrue(game.isOver());
  }

  @Test
  public void noThreefoldRepetitionOnIllegalClassicalTest() {
    Game game = Game.initialize(false, false, null, null);
    try {
      game.playMove(Move.fromString("h1-h3"));
    } catch (IllegalMoveException e) {

    }
    try {
      game.playMove(Move.fromString("h1-h3"));
    } catch (IllegalMoveException e) {

    }
    assertFalse(game.getGameState().isThreefoldRepetition());
    assertFalse(game.isOver());
  }

  @Test
  public void noThreefoldRepetitionOnIllegalSpecialTest() {
    Game game = Game.initialize(false, false, null, null);
    try {
      game.playMove(Move.fromString("o-o-o"));
    } catch (IllegalMoveException e) {

    }
    try {
      game.playMove(Move.fromString("o-o-o"));
    } catch (IllegalMoveException e) {

    }
    assertFalse(game.getGameState().isThreefoldRepetition());
    assertFalse(game.isOver());
  }
}
