package tests;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {
  /*
  private GameState gameBlitzOff;
  private GameState gameBlitzOn;

  @BeforeEach
  void setUp() {
      gameBlitzOff = new GameState();
      gameBlitzOn = new GameState(true);
  }

  @Test
  void testInitialisation() {
      assertNotNull(gameBlitzOff.getBoard(), "The board of the game should be correctly instantiated inside the constructor !");
      assertNotNull(gameBlitzOff.getHistory(), "The history of the game should be correctly instantiated inside the constructor !");
      assertTrue(gameBlitzOff.isWhiteTurn(), "The first player to move should be white for every new game !");

      assertNull(gameBlitzOff.getWhiteTimer(), "If blitz mode is off, timer should be null !");
  }

  @Test
  void testSwitchPlayerTurn() {
      assertTrue(gameBlitzOff.isWhiteTurn(), "The first player to move should be white for every new game !");
      gameBlitzOff.switchPlayerTurn();
      assertFalse(gameBlitzOff.isWhiteTurn(), "After white made the first move, it should be black to play !");
      gameBlitzOff.switchPlayerTurn();
      assertTrue(gameBlitzOff.isWhiteTurn(), "After black made their move, it should be white's turn to play !");
  }

  @Test
  void testBlitzModeInitialization() {
      assertNotNull(gameBlitzOn.getWhiteTimer(), "Timer should be correctly instantiated for blitz games !");
  }
  */
}
