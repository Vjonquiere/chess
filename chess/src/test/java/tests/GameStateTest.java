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
    assertNotNull(gameBlitzOff.getBoard(), "The board should be initialized !");
    assertNull(gameBlitzOff.getHistory(), "History should be null until implemented !");
    assertTrue(gameBlitzOff.isWhiteTurn(), "The first player should be white !");
    assertNull(gameBlitzOff.getMoveTimer(), "Timer should be null for non-blitz mode !");

    assertFalse(gameBlitzOff.hasWhiteResigned(), "White should not have resigned initially !");
    assertFalse(gameBlitzOff.hasBlackResigned(), "Black should not have resigned initially !");
    assertFalse(
        gameBlitzOff.hasWhiteRequestedDraw(), "White should not have requested a draw initially !");
    assertFalse(
        gameBlitzOff.hasBlackRequestedDraw(), "Black should not have requested a draw initially !");
    assertFalse(gameBlitzOff.isGameOver(), "Game should not be over initially !");
  }

  @Test
  void testSwitchPlayerTurn() {
    assertTrue(gameBlitzOff.isWhiteTurn(), "Game should start with White's turn !");
    gameBlitzOff.switchPlayerTurn();
    assertFalse(gameBlitzOff.isWhiteTurn(), "After White moves, it should be Black's turn !");
    gameBlitzOff.switchPlayerTurn();
    assertTrue(gameBlitzOff.isWhiteTurn(), "After Black moves, it should be White's turn !");
  }

  @Test
  void testBlitzModeInitialization() {
    assertNotNull(gameBlitzOn.getMoveTimer(), "Blitz mode should have a timer initialized !");
  }

  @Test
  void testWhiteRequestsDraw() {
    gameBlitzOff.whiteWantsToDraw();
    assertTrue(gameBlitzOff.hasWhiteRequestedDraw(), "White should have requested a draw !");
    assertFalse(
        gameBlitzOff.isGameOver(), "Game should not be over just because White requested a draw !");
  }

  @Test
  void testBlackRequestsDraw() {
    gameBlitzOff.blackWantsToDraw();
    assertTrue(gameBlitzOff.hasBlackRequestedDraw(), "Black should have requested a draw !");
    assertFalse(
        gameBlitzOff.isGameOver(), "Game should not be over just because Black requested a draw !");
  }

  @Test
  void testWhiteCancelsDrawRequest() {
    gameBlitzOff.whiteWantsToDraw();
    gameBlitzOff.whiteCancelsDrawRequest();
    assertFalse(gameBlitzOff.hasWhiteRequestedDraw(), "White's draw request should be canceled !");
    assertFalse(gameBlitzOff.isGameOver(), "Canceling draw should not end the game !");
  }

  @Test
  void testBlackCancelsDrawRequest() {
    gameBlitzOff.blackWantsToDraw();
    gameBlitzOff.blackCancelsDrawRequest();
    assertFalse(gameBlitzOff.hasBlackRequestedDraw(), "Black's draw request should be canceled !");
    assertFalse(gameBlitzOff.isGameOver(), "Canceling draw should not end the game !");
  }

  @Test
  void testMutualDrawAgreementShouldEndGame() {
    gameBlitzOff.whiteWantsToDraw();
    gameBlitzOff.blackWantsToDraw();
    assertTrue(gameBlitzOff.isGameOver(), "Game should be over if both players agree to a draw !");
  }

  @Test
  void testWhiteResigns() {
    gameBlitzOff.whiteResigns();
    assertTrue(gameBlitzOff.hasWhiteResigned(), "White should be marked as resigned !");
    assertTrue(gameBlitzOff.isGameOver(), "Game should be over after White resigns !");
  }

  @Test
  void testBlackResigns() {
    gameBlitzOff.blackResigns();
    assertTrue(gameBlitzOff.hasBlackResigned(), "Black should be marked as resigned !");
    assertTrue(gameBlitzOff.isGameOver(), "Game should be over after Black resigns !");
  }
}
