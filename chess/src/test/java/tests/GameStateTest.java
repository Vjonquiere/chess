package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.model.*;

public class GameStateTest {
  private GameState gameBlitzOff;
  private GameState gameBlitzOn;

  @BeforeEach
  public void setUp() {
    gameBlitzOff = new GameState();
    gameBlitzOn = new GameState(new Timer(30 * 60));
  }

  @Test
  public void testInitialisation() {
    assertNotNull(gameBlitzOff.getBoard(), "The board should be initialized !");
    assertNull(gameBlitzOff.getHistory(), "History should be null until implemented !");
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
    assertTrue(gameBlitzOff.isWhiteTurn(), "Game should start with White's turn !");
    gameBlitzOff.switchPlayerTurn();
    assertFalse(gameBlitzOff.isWhiteTurn(), "After White moves, it should be Black's turn !");
    gameBlitzOff.switchPlayerTurn();
    assertTrue(gameBlitzOff.isWhiteTurn(), "After Black moves, it should be White's turn !");
  }

  @Test
  public void testBlitzModeInitialization() {
    assertNotNull(gameBlitzOn.getMoveTimer(), "Blitz mode should have a timer initialized !");
  }

  @Test
  public void testWhiteRequestsDraw() {
    gameBlitzOff.whiteWantsToDraw();
    assertTrue(gameBlitzOff.hasWhiteRequestedDraw(), "White should have requested a draw !");
    assertFalse(
        gameBlitzOff.isGameOver(), "Game should not be over just because White requested a draw !");
  }

  @Test
  public void testBlackRequestsDraw() {
    gameBlitzOff.blackWantsToDraw();
    assertTrue(gameBlitzOff.hasBlackRequestedDraw(), "Black should have requested a draw !");
    assertFalse(
        gameBlitzOff.isGameOver(), "Game should not be over just because Black requested a draw !");
  }

  @Test
  public void testWhiteCancelsDrawRequest() {
    gameBlitzOff.whiteWantsToDraw();
    gameBlitzOff.whiteCancelsDrawRequest();
    assertFalse(gameBlitzOff.hasWhiteRequestedDraw(), "White's draw request should be canceled !");
    assertFalse(gameBlitzOff.isGameOver(), "Canceling draw should not end the game !");
  }

  @Test
  public void testBlackCancelsDrawRequest() {
    gameBlitzOff.blackWantsToDraw();
    gameBlitzOff.blackCancelsDrawRequest();
    assertFalse(gameBlitzOff.hasBlackRequestedDraw(), "Black's draw request should be canceled !");
    assertFalse(gameBlitzOff.isGameOver(), "Canceling draw should not end the game !");
  }

  @Test
  public void testMutualDrawAgreementShouldEndGame() {
    gameBlitzOff.whiteWantsToDraw();
    gameBlitzOff.blackWantsToDraw();
    assertTrue(gameBlitzOff.isGameOver(), "Game should be over if both players agree to a draw !");
  }

  @Test
  public void testWhiteResigns() {
    gameBlitzOff.whiteResigns();
    assertTrue(gameBlitzOff.hasWhiteResigned(), "White should be marked as resigned !");
    assertTrue(gameBlitzOff.isGameOver(), "Game should be over after White resigns !");
  }

  @Test
  public void testBlackResigns() {
    gameBlitzOff.blackResigns();
    assertTrue(gameBlitzOff.hasBlackResigned(), "Black should be marked as resigned !");
    assertTrue(gameBlitzOff.isGameOver(), "Game should be over after Black resigns !");
  }

  @Test
  public void testApplyFiftyMoveRule() {
    gameBlitzOff.applyFiftyMoveRule();
    assertTrue(
        gameBlitzOff.isGameOver(),
        "Game should end in a draw if the fifty move rule is triggered !");
  }

  @Test
  public void testPlayerLosesOnTime() {
    // TO DO when Time class is implemented
    // gameBlitzOn.playerLosesOnTime();
  }
}
