package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.model.*;

public class GameStateTest {
  private GameState gameBlitzOff;
  private GameState gameBlitzOn;

  @BeforeEach
  void setUp() {
    gameBlitzOff = new GameState();
    gameBlitzOn = new GameState(new Timer(30 * 60));
  }

  @Test
  void testInitialisation() {
    assertNotNull(
        gameBlitzOff.getBoard(),
        "The board of the game should be correctly instantiated inside the constructor !");
    // assertNotNull(
    //    gameBlitzOff.getHistory(),
    //    "The history of the game should be correctly instantiated inside the constructor !");
    assertTrue(
        gameBlitzOff.isWhiteTurn(),
        "The first player to move should be white for every new game !");

    assertNull(gameBlitzOff.getMoveTimer(), "If blitz mode is off, timer should be null !");
  }

  @Test
  void testSwitchPlayerTurn() {
    assertTrue(
        gameBlitzOff.isWhiteTurn(),
        "The first player to move should be white for every new game !");
    gameBlitzOff.switchPlayerTurn();
    assertFalse(
        gameBlitzOff.isWhiteTurn(),
        "After white made the first move, it should be black to play !");
    gameBlitzOff.switchPlayerTurn();
    assertTrue(
        gameBlitzOff.isWhiteTurn(),
        "After black made their move, it should be white's turn to play !");
  }

  @Test
  void testBlitzModeInitialization() {
    assertNotNull(
        gameBlitzOn.getMoveTimer(), "Timer should be correctly instantiated for blitz games !");
  }

  @Test
  void testWhiteRequestsDraw() {
    gameBlitzOff.whiteWantsToDraw();
    assertTrue(gameBlitzOff.hasWhiteRequestedDraw(), "White should still have the turn !");
  }

  @Test
  void testBlackRequestsDraw() {
    gameBlitzOff.blackWantsToDraw();
    assertTrue(gameBlitzOff.hasBlackRequestedDraw(), "Black should have requested a draw !");
  }

  @Test
  void testWhiteCancelsDrawRequest() {
    gameBlitzOff.whiteWantsToDraw();
    gameBlitzOff.whiteCancelsDrawRequest();
    assertFalse(gameBlitzOff.isGameOver(), "Canceling draw should not end the game !");
  }

  @Test
  void testBlackCancelsDrawRequest() {
    gameBlitzOff.blackWantsToDraw();
    gameBlitzOff.blackCancelsDrawRequest();
    assertFalse(gameBlitzOff.isGameOver(), "Canceling draw should not end the game !");
  }

  @Test
  void testMutualDrawAgreementShoudlEndGame() {
    gameBlitzOff.whiteWantsToDraw();
    gameBlitzOff.blackWantsToDraw();
    assertTrue(
        gameBlitzOff.isGameOver(), "The game should be over if both players agree to a draw !");
  }
}
