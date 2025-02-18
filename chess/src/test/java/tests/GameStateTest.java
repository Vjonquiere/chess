package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pdp.model.*;

public class GameStateTest {

  @Test
  public void testInitialisation() {
    GameState gameBlitzOff = new GameState();
    GameState gameBlitzOn = new GameState(new Timer(30 * 60));
    assertNotNull(gameBlitzOff.getBoard(), "The board should be initialized !");
    assertTrue(gameBlitzOff.isWhiteTurn(), "The first player should be white !");
    assertNull(gameBlitzOff.getMoveTimer(), "Timer should be null for non-blitz mode !");

    assertFalse(gameBlitzOff.hasWhiteResigned(), "White should not have resigned initially !");
    assertFalse(gameBlitzOff.hasBlackResigned(), "Black should not have resigned initially !");
    assertFalse(
        gameBlitzOn.hasWhiteLostOnTime(), "White should not lose on time when game starts !");
    assertFalse(
        gameBlitzOn.hasBlackLostOnTime(), "Black should not lose on time when game starts !");
    assertFalse(
        gameBlitzOff.hasWhiteRequestedDraw(), "White should not have requested a draw initially !");
    assertFalse(
        gameBlitzOff.hasBlackRequestedDraw(), "Black should not have requested a draw initially !");
    assertFalse(gameBlitzOff.isGameOver(), "Game should not be over initially !");
  }

  @Test
  public void testSwitchPlayerTurn() {
    GameState gameBlitzOff = new GameState();
    assertTrue(gameBlitzOff.isWhiteTurn(), "Game should start with White's turn !");
    gameBlitzOff.switchPlayerTurn();
    assertFalse(gameBlitzOff.isWhiteTurn(), "After White moves, it should be Black's turn !");
    gameBlitzOff.switchPlayerTurn();
    assertTrue(gameBlitzOff.isWhiteTurn(), "After Black moves, it should be White's turn !");
  }

  @Test
  public void testBlitzModeInitialization() {
    GameState gameBlitzOn = new GameState(new Timer(30 * 60));
    assertNotNull(gameBlitzOn.getMoveTimer(), "Blitz mode should have a timer initialized !");
  }

  @Test
  public void testWhiteRequestsDraw() {
    GameState gameBlitzOff = new GameState();
    gameBlitzOff.whiteWantsToDraw();
    assertTrue(gameBlitzOff.hasWhiteRequestedDraw(), "White should have requested a draw !");
    assertFalse(
        gameBlitzOff.isGameOver(), "Game should not be over just because White requested a draw !");
  }

  @Test
  public void testBlackRequestsDraw() {
    GameState gameBlitzOff = new GameState();
    gameBlitzOff.blackWantsToDraw();
    assertTrue(gameBlitzOff.hasBlackRequestedDraw(), "Black should have requested a draw !");
    assertFalse(
        gameBlitzOff.isGameOver(), "Game should not be over just because Black requested a draw !");
  }

  @Test
  public void testWhiteCancelsDrawRequest() {
    GameState gameBlitzOff = new GameState();
    gameBlitzOff.whiteWantsToDraw();
    gameBlitzOff.whiteCancelsDrawRequest();
    assertFalse(gameBlitzOff.hasWhiteRequestedDraw(), "White's draw request should be canceled !");
    assertFalse(gameBlitzOff.isGameOver(), "Canceling draw should not end the game !");
  }

  @Test
  public void testBlackCancelsDrawRequest() {
    GameState gameBlitzOff = new GameState();
    gameBlitzOff.blackWantsToDraw();
    gameBlitzOff.blackCancelsDrawRequest();
    assertFalse(gameBlitzOff.hasBlackRequestedDraw(), "Black's draw request should be canceled !");
    assertFalse(gameBlitzOff.isGameOver(), "Canceling draw should not end the game !");
  }

  @Test
  public void testMutualDrawAgreementShouldEndGame() {
    GameState gameBlitzOff = new GameState();
    gameBlitzOff.whiteWantsToDraw();
    gameBlitzOff.blackWantsToDraw();
    assertTrue(gameBlitzOff.isGameOver(), "Game should be over if both players agree to a draw !");
  }

  @Test
  public void testWhiteResigns() {
    GameState gameBlitzOff = new GameState();
    gameBlitzOff.whiteResigns();
    assertTrue(gameBlitzOff.hasWhiteResigned(), "White should be marked as resigned !");
    assertTrue(gameBlitzOff.isGameOver(), "Game should be over after White resigns !");
  }

  @Test
  public void testBlackResigns() {
    GameState gameBlitzOff = new GameState();
    gameBlitzOff.blackResigns();
    assertTrue(gameBlitzOff.hasBlackResigned(), "Black should be marked as resigned !");
    assertTrue(gameBlitzOff.isGameOver(), "Game should be over after Black resigns !");
  }

  @Test
  public void testApplyFiftyMoveRule() {
    GameState gameBlitzOff = new GameState();
    gameBlitzOff.applyFiftyMoveRule();
    assertTrue(
        gameBlitzOff.isGameOver(),
        "Game should end in a draw if the fifty move rule is triggered !");
  }

  @Test
  public void testCheckGameStatusCall() {
    GameState gameBlitzOff = new GameState();
    // Simulate cases
    gameBlitzOff.checkGameStatus();
    // Assert
  }

  @Test
  public void testPlayerLosesOnTime() {
    // TO DO when Time class is implemented
    // gameBlitzOn.playerLosesOnTime();
  }
}
