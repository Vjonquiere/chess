package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pdp.exceptions.IllegalMoveException;
import pdp.model.BitboardRepresentation;
import pdp.model.Game;
import pdp.model.Move;
import pdp.utils.Position;

public class GameTest {
  @Test
  public void playMoveTest() {

    // Correctly play move
    Game game = Game.initialize(false, false, null, null);
    BitboardRepresentation bitboards = new BitboardRepresentation();
    game.playMove(new Move(new Position(1, 0), new Position(2, 0)));
    bitboards.movePiece(new Position(1, 0), new Position(2, 0));
    assertEquals(game.getBoard().getBoard(), bitboards);

    // Play move only in game
    game.playMove(new Move(new Position(6, 1), new Position(5, 1)));
    assertNotEquals(game.getBoard().getBoard(), bitboards);
    bitboards.movePiece(new Position(6, 1), new Position(5, 1)); // Play move in bitboards
    assertEquals(game.getBoard().getBoard(), bitboards);

    // Tests to add
    // 6. try a move that is not a classical or special move
  }

  @Test
  public void playEnPassantTest() {
    Game game = Game.initialize(false, false, null, null);
    game.playMove(new Move(new Position(1, 0), new Position(3, 0)));
    game.playMove(new Move(new Position(6, 0), new Position(5, 0)));
    game.playMove(new Move(new Position(3, 0), new Position(4, 0)));
    game.playMove(new Move(new Position(6, 1), new Position(4, 1)));
    game.playMove(new Move(new Position(4, 0), new Position(5, 1)));
    BitboardRepresentation bitboards = new BitboardRepresentation();
    bitboards.deletePieceAt(1, 6);
    bitboards.movePiece(new Position(1, 0), new Position(5, 1));
    bitboards.movePiece(new Position(6, 0), new Position(5, 0));
    assertEquals(bitboards, game.getBoard().getBoard());
  }

  @Test
  public void playEnPassantIsCheckTest() {
    Game game = Game.initialize(false, false, null, null);
    game.playMove(new Move(new Position(1, 4), new Position(2, 4)));
    game.playMove(new Move(new Position(6, 7), new Position(4, 7)));
    game.playMove(new Move(new Position(2, 4), new Position(3, 4)));
    game.playMove(new Move(new Position(7, 7), new Position(5, 7)));
    game.playMove(new Move(new Position(3, 4), new Position(4, 4)));
    game.playMove(new Move(new Position(5, 7), new Position(5, 4)));
    game.playMove(new Move(new Position(1, 7), new Position(2, 7)));
    game.playMove(new Move(new Position(6, 3), new Position(4, 3)));

    assertThrows(
        IllegalMoveException.class,
        () -> {
          game.playMove(new Move(new Position(4, 4), new Position(5, 3)));
        });
  }

  @Test
  public void playDoublePushTest() {
    Game game = Game.initialize(false, false, null, null);
    game.playMove(new Move(new Position(1, 1), new Position(3, 1)));
    BitboardRepresentation bitboards = new BitboardRepresentation();
    bitboards.movePiece(new Position(1, 1), new Position(3, 1));
    assertEquals(bitboards, game.getBoard().getBoard());
  }

  @Test
  public void playDoublePushIsCheckTest() {
    Game game = Game.initialize(false, false, null, null);
    game.playMove(new Move(new Position(1, 0), new Position(2, 0)));
    game.playMove(new Move(new Position(6, 3), new Position(4, 3)));
    game.playMove(new Move(new Position(2, 0), new Position(3, 0)));
    game.playMove(new Move(new Position(7, 3), new Position(6, 3)));
    game.playMove(new Move(new Position(3, 0), new Position(4, 0)));
    game.playMove(new Move(new Position(6, 3), new Position(5, 3)));
    game.playMove(new Move(new Position(1, 1), new Position(2, 1)));
    game.playMove(new Move(new Position(5, 3), new Position(3, 1)));
    assertThrows(
        IllegalMoveException.class,
        () -> {
          game.playMove(new Move(new Position(1, 3), new Position(3, 3)));
        });
  }

  @Test
  public void wrongTurnForWhitePlayerTest() {
    Game game = Game.initialize(false, false, null, null);
    game.playMove(new Move(new Position(1, 0), new Position(2, 0)));
    assertThrows(
        IllegalMoveException.class,
        () -> {
          game.playMove(new Move(new Position(2, 0), new Position(3, 0)));
        });
  }

  @Test
  public void wrongTurnForBlackPlayerTest() {
    Game game = Game.initialize(false, false, null, null);
    game.playMove(new Move(new Position(1, 0), new Position(2, 0)));
    game.playMove(new Move(new Position(6, 0), new Position(5, 0)));
    assertThrows(
        IllegalMoveException.class,
        () -> {
          game.playMove(new Move(new Position(5, 0), new Position(4, 0)));
        });
  }

  @Test
  public void pinnedPieceTest() {
    Game game = Game.initialize(false, false, null, null);
    game.playMove(new Move(new Position(1, 0), new Position(2, 0)));
    game.playMove(new Move(new Position(6, 2), new Position(4, 2)));
    game.playMove(new Move(new Position(2, 0), new Position(3, 0)));
    game.playMove(new Move(new Position(7, 3), new Position(4, 0)));
    assertThrows(
        IllegalMoveException.class,
        () -> {
          game.playMove(new Move(new Position(1, 3), new Position(2, 3)));
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
    assertEquals(true, game.getGameState().isThreefoldRepetition());
    assertEquals(true, game.isOver());
  }
}
