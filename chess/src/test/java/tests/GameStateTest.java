package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.events.EventType;
import pdp.model.*;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.BoardRepresentation;
import pdp.model.parsers.FenHeader;
import pdp.model.parsers.FileBoard;
import pdp.utils.Timer;

public class GameStateTest {
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @BeforeEach
  void setUpConsole() {
    Game.initialize(false, false, null, null, null, new HashMap<>());
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
  public void testInitialisationWithBoard() {
    FileBoard board = new FileBoard(new BitboardRepresentation(), true, null);

    GameState gameBlitzOffWithBoard = new GameState(board);
    assertNotNull(gameBlitzOffWithBoard.getBoard(), "The board should be initialized !");
    assertTrue(gameBlitzOffWithBoard.isWhiteTurn(), "The first player should be white !");
    assertNull(gameBlitzOffWithBoard.getMoveTimer(), "Timer should be null for non-blitz mode !");

    assertFalse(
        gameBlitzOffWithBoard.hasWhiteResigned(), "White should not have resigned initially !");
    assertFalse(
        gameBlitzOffWithBoard.hasBlackResigned(), "Black should not have resigned initially !");

    assertFalse(
        gameBlitzOffWithBoard.hasWhiteRequestedDraw(),
        "White should not have requested a draw initially !");
    assertFalse(
        gameBlitzOffWithBoard.hasBlackRequestedDraw(),
        "Black should not have requested a draw initially !");
    assertFalse(gameBlitzOffWithBoard.isGameOver(), "Game should not be over initially !");

    GameState gameBlitzOnWithBoard = new GameState(board, new Timer(30 * 60));
    assertNotNull(gameBlitzOnWithBoard.getBoard(), "The board should be initialized !");
    assertTrue(gameBlitzOnWithBoard.isWhiteTurn(), "The first player should be white !");
    assertNotNull(gameBlitzOnWithBoard.getMoveTimer(), "Timer should not be null for blitz mode !");

    assertFalse(
        gameBlitzOnWithBoard.hasWhiteResigned(), "White should not have resigned initially !");
    assertFalse(
        gameBlitzOnWithBoard.hasBlackResigned(), "Black should not have resigned initially !");
    assertFalse(
        gameBlitzOnWithBoard.hasWhiteLostOnTime(),
        "White should not lose on time when game starts !");
    assertFalse(
        gameBlitzOnWithBoard.hasBlackLostOnTime(),
        "Black should not lose on time when game starts !");
    assertFalse(
        gameBlitzOnWithBoard.hasWhiteRequestedDraw(),
        "White should not have requested a draw initially !");
    assertFalse(
        gameBlitzOnWithBoard.hasBlackRequestedDraw(),
        "Black should not have requested a draw initially !");
    assertFalse(gameBlitzOnWithBoard.isGameOver(), "Game should not be over initially !");

    board =
        new FileBoard(
            new BitboardRepresentation(), false, new FenHeader(true, true, true, true, null, 0, 5));
    GameState gameBlitzOffWithHeader = new GameState(board);
    assertNotNull(gameBlitzOffWithHeader.getBoard(), "The board should be initialized !");
    assertFalse(gameBlitzOffWithHeader.isWhiteTurn(), "The current player should be black !");
    assertNull(gameBlitzOffWithHeader.getMoveTimer(), "Timer should be null for non-blitz mode !");
    assertEquals(5, gameBlitzOffWithHeader.getFullTurn());

    GameState gameBlitzOnWithHeader = new GameState(board, new Timer(30 * 60));
    assertNotNull(gameBlitzOnWithHeader.getBoard(), "The board should be initialized !");
    assertFalse(gameBlitzOnWithHeader.isWhiteTurn(), "The current player should be black !");
    assertNotNull(
        gameBlitzOnWithHeader.getMoveTimer(), "Timer should not be null for blitz mode !");
    assertEquals(5, gameBlitzOnWithHeader.getFullTurn());
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
  public void testWhitePlayerLosesOnTime() {
    GameState gameState = spy(new GameState());

    gameState.playerOutOfTime(true);

    assertTrue(gameState.isGameOver());
    verify(gameState, times(1)).notifyObservers(EventType.OUT_OF_TIME_WHITE);
    verify(gameState, times(1)).notifyObservers(EventType.WIN_BLACK);
  }

  @Test
  public void testBlackPlayerLosesOnTime() {
    GameState gameState = spy(new GameState());

    gameState.playerOutOfTime(false);

    assertTrue(gameState.isGameOver());
    verify(gameState, times(1)).notifyObservers(EventType.OUT_OF_TIME_BLACK);
    verify(gameState, times(1)).notifyObservers(EventType.WIN_WHITE);
  }

  @Test
  public void testWhitePlayerDrawOnTime() {
    GameState gameState = spy(new GameState());
    BoardRepresentation board = mock(BoardRepresentation.class);
    when(gameState.getBoard()).thenReturn(board);
    when(board.getBoardRep()).thenReturn(board);
    when(board.hasEnoughMaterialToMate(false)).thenReturn(false);
    gameState.playerOutOfTime(true);

    assertTrue(gameState.isGameOver());
    verify(gameState, times(1)).notifyObservers(EventType.OUT_OF_TIME_WHITE);
    verify(gameState, times(1)).notifyObservers(EventType.DRAW);
  }

  @Test
  public void testBlackPlayerDrawOnTime() {
    GameState gameState = spy(new GameState());
    BoardRepresentation board = mock(BoardRepresentation.class);
    when(gameState.getBoard()).thenReturn(board);
    when(board.getBoardRep()).thenReturn(board);
    when(board.hasEnoughMaterialToMate(true)).thenReturn(false);
    gameState.playerOutOfTime(false);

    assertTrue(gameState.isGameOver());
    verify(gameState, times(1)).notifyObservers(EventType.OUT_OF_TIME_BLACK);
    verify(gameState, times(1)).notifyObservers(EventType.DRAW);
  }
}
