package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import pdp.exceptions.IllegalMoveException;
import pdp.model.Game;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.BoardRepresentation;
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

  @Test
  public void testIsEndGamePhaseShouldBeFalse() {
    Game game = Game.initialize(false, false, null, null);

    assertFalse(game.isEndGamePhase());
  }

  @Test
  public void testIsEndGamePhaseShouldBeTrue() {
    Game game = Game.initialize(false, false, null, null);

    // Move pawns
    game.playMove(Move.fromString("a2-a4"));
    game.playMove(Move.fromString("a7-a5"));
    game.playMove(Move.fromString("b2-b4"));
    game.playMove(Move.fromString("b7-b5"));
    game.playMove(Move.fromString("c2-c4"));
    game.playMove(Move.fromString("c7-c5"));
    game.playMove(Move.fromString("d2-d4"));
    game.playMove(Move.fromString("d7-d5"));
    game.playMove(Move.fromString("e2-e4"));
    game.playMove(Move.fromString("e7-e5"));
    game.playMove(Move.fromString("f2-f4"));
    game.playMove(Move.fromString("f7-f5"));
    game.playMove(Move.fromString("g2-g4"));
    game.playMove(Move.fromString("g7-g5"));
    game.playMove(Move.fromString("h2-h4"));
    game.playMove(Move.fromString("h7-h5"));

    // Move kings
    game.playMove(Move.fromString("e1-e2"));
    game.playMove(Move.fromString("e8-e7"));

    // Get queens off the board
    game.playMove(Move.fromString("d4-e5"));
    game.playMove(Move.fromString("d5-e4"));
    game.playMove(Move.fromString("d1-d8"));
    game.playMove(Move.fromString("e7-d8"));

    // Play a few more moves to reach conditions
    game.playMove(Move.fromString("a1-a3"));
    game.playMove(Move.fromString("a8-a6"));
    game.playMove(Move.fromString("h1-h3"));
    game.playMove(Move.fromString("h8-h6"));
    game.playMove(Move.fromString("h3-f3"));
    game.playMove(Move.fromString("h6-f6"));
    game.playMove(Move.fromString("a3-c3"));
    game.playMove(Move.fromString("a6-c6"));

    assertTrue(game.isEndGamePhase());
  }

  @Test
  public void testCheckGameStatusMateFromBlack() {
    Game game = Game.initialize(false, false, null, null);

    game.playMove(Move.fromString("f2-f4"));
    game.playMove(Move.fromString("e7-e6"));
    game.playMove(Move.fromString("g2-g4"));
    game.playMove(Move.fromString("d8-h4"));

    assertTrue(game.isOver());
  }

  @Test
  public void testCheckGameStatusCheckDrawByAgreement() {
    Game game = Game.initialize(false, false, null, null);

    game.playMove(Move.fromString("f2-f4"));
    game.playMove(Move.fromString("e7-e6"));

    game.getGameState().blackWantsToDraw();
    game.getGameState().whiteWantsToDraw();

    game.playMove(Move.fromString("a2-a3"));

    assertTrue(game.isOver());
  }

  @Test
  public void testCheckGameStatusHasWhiteResigned() {
    Game game = Game.initialize(false, false, null, null);

    game.playMove(Move.fromString("f2-f4"));
    game.playMove(Move.fromString("e7-e6"));

    game.getGameState().whiteResigns();

    game.playMove(Move.fromString("a2-a3"));

    assertTrue(game.isOver());
  }

  @Test
  public void testCheckGameStatusHasBlackResigned() {
    Game game = Game.initialize(false, false, null, null);

    game.playMove(Move.fromString("f2-f4"));
    game.playMove(Move.fromString("e7-e6"));

    game.getGameState().blackResigns();

    game.playMove(Move.fromString("a2-a3"));

    assertTrue(game.isOver());
  }

  @Test
  public void testCheckGameStatusDrawByInsufficientMaterial() {
    Game game = Game.initialize(false, false, null, null);

    BoardRepresentation board = game.getBoard().getBoardRep();

    Position initWhiteKingPos = new Position(4, 0);
    Position initBlackKingPos = new Position(4, 7);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListBlack.add(initBlackKingPos);

    BitboardRepresentationTest.deleteAllPiecesExceptThosePositionsBoard(
        board, posListWhite, posListBlack);

    game.playMove(Move.fromString("e1-e2"));

    assertTrue(game.isOver());
  }

  @Test
  public void testCheckGameStatusStaleMate() {
    Game game = Game.initialize(false, false, null, null);

    BoardRepresentation board = game.getBoard().getBoardRep();

    Position initWhiteKingPos = new Position(4, 0);
    Position initBlackKingPos = new Position(4, 7);
    Position initBlackRookPos = new Position(0, 7);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListBlack.add(initBlackKingPos);
    posListBlack.add(initBlackRookPos);

    BitboardRepresentationTest.deleteAllPiecesExceptThosePositionsBoard(
        board, posListWhite, posListBlack);

    assertFalse(game.isOver());
    game.playMove(Move.fromString("e1-f1"));
    assertFalse(game.isOver());
    game.playMove(Move.fromString("e8-f7"));
    game.playMove(Move.fromString("f1-g2"));
    game.playMove(Move.fromString("f7-f6"));
    game.playMove(Move.fromString("g2-f2"));
    game.playMove(Move.fromString("f6-f5"));
    game.playMove(Move.fromString("f2-g1"));
    game.playMove(Move.fromString("f5-f4"));
    game.playMove(Move.fromString("g1-h1"));
    game.playMove(Move.fromString("f4-f3"));
    game.playMove(Move.fromString("h1-h2"));
    game.playMove(Move.fromString("f3-f2"));
    game.playMove(Move.fromString("h2-h1"));
    game.playMove(Move.fromString("a8-g8"));
    game.playMove(Move.fromString("h1-h2"));
    game.playMove(Move.fromString("g8-g6"));
    game.playMove(Move.fromString("h2-h1"));
    assertFalse(game.isOver());
    game.playMove(Move.fromString("g6-g2"));

    assertTrue(game.isOver());
  }

  @Test
  public void testCheckGameStatusFiftyMoveRule() {
    Game game = Game.initialize(false, false, null, null);

    BoardRepresentation board = game.getBoard().getBoardRep();

    Position initWhiteKingPos = new Position(4, 0);
    Position initWhiteRookPos = new Position(7, 0);
    Position initBlackKingPos = new Position(4, 7);
    Position initBlackRookPos = new Position(0, 7);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListWhite.add(initWhiteRookPos);
    posListBlack.add(initBlackKingPos);
    posListBlack.add(initBlackRookPos);

    BitboardRepresentationTest.deleteAllPiecesExceptThosePositionsBoard(
        board, posListWhite, posListBlack);

    // Simulate game where 50 move rule can be applied

    assertFalse(game.isOver());
  }
}
