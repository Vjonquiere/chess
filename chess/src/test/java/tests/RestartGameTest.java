package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.model.*;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.utils.Position;

public class RestartGameTest {
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

  /* Game game = Game.initialize(false, false, null, null, new HashMap<>());
     Move move = new Move(new Position(4, 1), new Position(4, 3));
     game.playMove(move);
     Move move2 = new Move(new Position(4, 6), new Position(4, 4));
     game.playMove(move2);
     Move move3 = new Move(new Position(3, 1), new Position(3, 2));
     game.playMove(move3);
     Move move4 = new Move(new Position(3, 6), new Position(3, 5));
     game.playMove(move4);
  */
  @Test
  public void testResetIsOver() {
    Game game = Game.initialize(false, false, null, null, new HashMap<>());
    game.getGameState().applyFiftyMoveRule();

    game.restartGame();

    assertFalse(game.getGameState().isGameOver());
  }

  @Test
  public void testResetIsOverWithWAi() {
    Game game = Game.initialize(true, false, new Solver(), null, new HashMap<>());
    game.getGameState().applyFiftyMoveRule();

    game.restartGame();

    assertFalse(game.getGameState().isGameOver());
  }

  @Test
  public void testRestartGame_ResetsHistory() {
    Game game = Game.initialize(false, false, null, null, new HashMap<>());
    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);

    assertEquals(
        move2.getSource(),
        game.getHistory().getCurrentMove().get().getState().getMove().getSource());
    assertEquals(
        move2.getDest(), game.getHistory().getCurrentMove().get().getState().getMove().getDest());
    game.restartGame();
    assertEquals(
        new Position(-1, -1),
        game.getHistory().getCurrentMove().get().getState().getMove().getSource());
    assertEquals(
        new Position(-1, -1),
        game.getHistory().getCurrentMove().get().getState().getMove().getDest());
  }

  /* @Test
  public void testRestartGame_ResetsHistoryWithWAi() {
    Game game = Game.initialize(false, false, null, null, new HashMap<>());
    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);

    assertEquals(
        move2.getSource(),
        game.getHistory().getCurrentMove().get().getState().getMove().getSource());
    assertEquals(
        move2.getDest(), game.getHistory().getCurrentMove().get().getState().getMove().getDest());
    game.restartGame();
    assertNotEquals(
        new Position(-1, -1),
        game.getHistory().getCurrentMove().get().getState().getMove().getSource());
    assertNotEquals(
        new Position(-1, -1),
        game.getHistory().getCurrentMove().get().getState().getMove().getDest());
  } */

  @Test
  public void testResetThreefold() {
    Game game = Game.initialize(false, false, null, null, new HashMap<>());
    Move move = new Move(new Position(6, 0), new Position(5, 2));
    game.playMove(move);
    Move move2 = new Move(new Position(6, 7), new Position(5, 5));
    game.playMove(move2);

    Move move3 = new Move(new Position(5, 2), new Position(6, 0));
    game.playMove(move3);
    Move move4 = new Move(new Position(5, 5), new Position(6, 7));
    game.playMove(move4);

    Move move5 = new Move(new Position(6, 0), new Position(5, 2));
    game.playMove(move5);
    Move move6 = new Move(new Position(6, 7), new Position(5, 5));
    game.playMove(move6);

    Move move7 = new Move(new Position(5, 2), new Position(6, 0));
    game.playMove(move7);
    /* Move move8 = new Move(new Position(5, 5) ,new Position(6, 7));
    game.playMove(move8); */

    assertFalse(game.isOver());
    game.restartGame();
    assertFalse(game.isOver());
  }

  @Test
  public void testResetFullTurn() {
    Game game = Game.initialize(false, false, null, null, new HashMap<>());
    Move move = new Move(new Position(6, 0), new Position(5, 2));
    game.playMove(move);
    Move move2 = new Move(new Position(6, 7), new Position(5, 5));
    game.playMove(move2);

    assertEquals(game.getGameState().getFullTurn(), 1);
    game.restartGame();
    assertEquals(game.getGameState().getFullTurn(), 0);
  }

  @Test
  public void testResetWhiteTurn() {
    Game game = Game.initialize(false, false, null, null, new HashMap<>());
    Move move = new Move(new Position(6, 0), new Position(5, 2));
    game.playMove(move);

    assertFalse(game.getGameState().isWhiteTurn());
    game.restartGame();
    assertTrue(game.getGameState().isWhiteTurn());
  }
}
