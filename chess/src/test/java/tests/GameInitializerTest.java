package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.GameControllerInit;
import pdp.controller.GameController;
import pdp.model.ai.algorithms.AlphaBeta;
import pdp.model.ai.algorithms.Minimax;
import pdp.model.ai.heuristics.MobilityHeuristic;
import pdp.model.ai.heuristics.StandardHeuristic;
import pdp.model.board.Move;
import pdp.model.parsers.BoardFileParser;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Piece;
import pdp.utils.OptionType;
import pdp.utils.Position;
import pdp.view.CliView;

class GameInitializerTest {
  private HashMap<OptionType, String> options;
  private Path tempFile;
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @BeforeEach
  void setUp() {
    System.setOut(new PrintStream(outputStream));
    System.setErr(new PrintStream(outputStream));
    options = new HashMap<>();
  }

  @AfterEach
  void tearDown() throws IOException {
    // Clean up temporary file if it was created.
    if (tempFile != null && Files.exists(tempFile)) {
      Files.delete(tempFile);
    }
    System.setOut(originalOut);
    System.setErr(originalErr);
    outputStream.reset();
  }

  @Test
  void testGameInitializationCLI() {
    GameController controller = GameControllerInit.initialize(options);
    assertNotNull(controller);
    assertTrue(controller.getView() instanceof CliView);
  }

  /*
  @Test
  void testGameInitializationGUI() {
    options.put(OptionType.GUI, "");
    GameController controller = GameControllerInit.initialize(options);
    assertNotNull(controller);
    assertTrue(controller.getView() instanceof GameView);
  }
    */

  /*
  @Test
  void testGameInitializationBlitzMode300() {
    options.put(OptionType.BLITZ, "");
    GameController controller = GameControllerInit.initialize(options);
    assertNotNull(controller);
    assertNotNull(controller.getModel().getGameState().getMoveTimer());
  }
    */

  /*
  @Test
  void testGameInitializationBlitzMode300() {
    options.put(OptionType.BLITZ, "");
    options.put(OptionType.TIME, "300");
    GameController controller = GameControllerInit.initialize(options);
    assertNotNull(controller);
    assertNotNull(controller.getModel().getGameState().getMoveTimer());
    assertEquals(controller.getModel().getGameState().getMoveTimer().getTimeRemaining(), 300);
  }
    */

  @Test
  void testGameInitializationAIWhite() {
    options.put(OptionType.AI, "W");
    GameController controller = GameControllerInit.initialize(options);
    assertNotNull(controller);
    assertTrue(controller.getModel().isWhiteAi());
    assertFalse(controller.getModel().isBlackAi());
    assertNotNull(controller.getModel().getWhiteSolver());
  }

  @Test
  void testGameInitializationAIBlack() {
    options.put(OptionType.AI, "B");
    GameController controller = GameControllerInit.initialize(options);
    assertNotNull(controller);
    assertTrue(controller.getModel().isBlackAi());
    assertFalse(controller.getModel().isWhiteAi());
    assertNotNull(controller.getModel().getBlackSolver());
  }

  /*
    @Test
    void testGameInitializationAIAll() {
      options.put(OptionType.AI, "A");
      GameController controller = GameControllerInit.initialize(options);
      assertNotNull(controller);
      assertTrue(controller.getModel().isWhiteAI());
      assertTrue(controller.getModel().isBlackAI());
      assertNotNull(controller.getModel().getWhiteSolver());
      assertNotNull(controller.getModel().getBlackSolver());
    }
  */
  @Test
  void testGameInitializationAIIncorrect() {
    options.put(OptionType.AI, "X");
    GameController controller = GameControllerInit.initialize(options);
    assertTrue(outputStream.toString().contains("Unknown AI option: X"));
    assertTrue(outputStream.toString().contains("Defaulting to AI playing White"));
    assertNotNull(controller);
    assertTrue(controller.getModel().isWhiteAi());
    assertFalse(controller.getModel().isBlackAi());
    assertNotNull(controller.getModel().getWhiteSolver());
  }

  @Test
  void testGameInitializationAIModeMinimax() {
    options.put(OptionType.AI, "W");
    options.put(OptionType.AI_MODE, "MINIMAX");
    GameController controller = GameControllerInit.initialize(options);
    assertNotNull(controller);
    assertTrue(controller.getModel().isWhiteAi());
    assertFalse(controller.getModel().isBlackAi());
    assertNotNull(controller.getModel().getWhiteSolver());
    assertInstanceOf(Minimax.class, controller.getModel().getWhiteSolver().getAlgorithm());
  }

  @Test
  void testGameInitializationAIModeIncorrect() {
    options.put(OptionType.AI, "W");
    options.put(OptionType.AI_MODE, "minimax");
    GameController controller = GameControllerInit.initialize(options);
    assertTrue(outputStream.toString().contains("Unknown AI mode option: minimax"));
    assertTrue(outputStream.toString().contains("Defaulting to ALPHABETA"));
    assertNotNull(controller);
    assertTrue(controller.getModel().isWhiteAi());
    assertFalse(controller.getModel().isBlackAi());
    assertNotNull(controller.getModel().getWhiteSolver());
    assertInstanceOf(AlphaBeta.class, controller.getModel().getWhiteSolver().getAlgorithm());
  }

  @Test
  void testGameInitializationAIHeuristic() {
    options.put(OptionType.AI, "W");
    options.put(OptionType.AI_HEURISTIC, "MOBILITY");
    GameController controller = GameControllerInit.initialize(options);
    assertNotNull(controller);
    assertTrue(controller.getModel().isWhiteAi());
    assertFalse(controller.getModel().isBlackAi());
    assertNotNull(controller.getModel().getWhiteSolver());
    assertInstanceOf(
        MobilityHeuristic.class, controller.getModel().getWhiteSolver().getHeuristic());
  }

  @Test
  void testGameInitializationAIHeuristicIncorrect() {

    options.put(OptionType.AI, "W");
    options.put(OptionType.AI_HEURISTIC, "MINIMAX");
    GameController controller = GameControllerInit.initialize(options);
    assertTrue(outputStream.toString().contains("Unknown Heuristic: MINIMAX"));
    assertTrue(outputStream.toString().contains("Defaulting to Heuristic STANDARD"));
    assertNotNull(controller);
    assertTrue(controller.getModel().isWhiteAi());
    assertFalse(controller.getModel().isBlackAi());
    assertNotNull(controller.getModel().getWhiteSolver());
    assertInstanceOf(
        StandardHeuristic.class, controller.getModel().getWhiteSolver().getHeuristic());
  }

  @Test
  void testGameInitializationAIDepth() {
    options.put(OptionType.AI, "W");
    options.put(OptionType.AI_DEPTH, "1");
    GameController controller = GameControllerInit.initialize(options);
    assertTrue(controller.getModel().isWhiteAi());
    assertFalse(controller.getModel().isBlackAi());
    assertNotNull(controller.getModel().getWhiteSolver());
    assertEquals(1, controller.getModel().getWhiteSolver().getDepth());
  }

  @Test
  void testGameInitializationAIDepthIncorrect() {

    options.put(OptionType.AI, "W");
    options.put(OptionType.AI_DEPTH, "abc");
    GameController controller = GameControllerInit.initialize(options);
    assertTrue(outputStream.toString().contains("Not an integer for the depth of AI"));
    assertTrue(
        outputStream
            .toString()
            .contains("Defaulting to depth " + controller.getModel().getWhiteSolver().getDepth()));
    assertTrue(controller.getModel().isWhiteAi());
    assertFalse(controller.getModel().isBlackAi());
    assertNotNull(controller.getModel().getWhiteSolver());
  }

  @Test
  void testGameInitializationAIDepthIncorrect2() {

    options.put(OptionType.AI, "W");
    options.put(OptionType.AI_DEPTH, "3.1");
    GameController controller = GameControllerInit.initialize(options);
    assertTrue(outputStream.toString().contains("Not an integer for the depth of AI"));
    assertTrue(
        outputStream
            .toString()
            .contains("Defaulting to depth " + controller.getModel().getWhiteSolver().getDepth()));
    assertTrue(controller.getModel().isWhiteAi());
    assertFalse(controller.getModel().isBlackAi());
    assertNotNull(controller.getModel().getWhiteSolver());
  }

  @Test
  void testGameInitializationAITime() {
    options.put(OptionType.AI, "W");
    options.put(OptionType.AI_TIME, "1");
    GameController controller = GameControllerInit.initialize(options);
    assertTrue(controller.getModel().isWhiteAi());
    assertFalse(controller.getModel().isBlackAi());
    assertNotNull(controller.getModel().getWhiteSolver());
    assertEquals(1000, controller.getModel().getWhiteSolver().getTime());
  }

  @Test
  void testGameInitializationAITimeIncorrect() {

    options.put(OptionType.AI, "W");
    options.put(OptionType.AI_TIME, "abc");
    GameController controller = GameControllerInit.initialize(options);
    assertTrue(outputStream.toString().contains("Not an int for the time of AI (in seconds)"));
    assertTrue(outputStream.toString().contains("Defaulting to a 5 seconds timer"));
    assertTrue(controller.getModel().isWhiteAi());
    assertFalse(controller.getModel().isBlackAi());
    assertNotNull(controller.getModel().getWhiteSolver());
  }

  @Test
  void testGameInitializationAITimeIncorrect2() {

    options.put(OptionType.AI, "W");
    options.put(OptionType.AI_TIME, "3.1");
    GameController controller = GameControllerInit.initialize(options);
    assertTrue(outputStream.toString().contains("Not an int for the time of AI (in seconds)"));
    assertTrue(outputStream.toString().contains("Defaulting to a 5 seconds timer"));
    assertTrue(controller.getModel().isWhiteAi());
    assertFalse(controller.getModel().isBlackAi());
    assertNotNull(controller.getModel().getWhiteSolver());
  }

  @Test
  void testGameInitializationAITimeBlitz1() {
    options.put(OptionType.AI, "W");
    options.put(OptionType.AI_TIME, "60");
    options.put(OptionType.BLITZ, "");
    options.put(OptionType.TIME, "1");
    GameController controller = GameControllerInit.initialize(options);
    assertTrue(controller.getModel().isWhiteAi());
    assertFalse(controller.getModel().isBlackAi());
    assertNotNull(controller.getModel().getWhiteSolver());
    assertEquals(60000, controller.getModel().getWhiteSolver().getTime());
  }

  @Test
  void testGameInitializationAITimeBlitz2() {
    options.put(OptionType.AI, "W");
    options.put(OptionType.AI_TIME, "1");
    options.put(OptionType.BLITZ, "");
    options.put(OptionType.TIME, "1");
    GameController controller = GameControllerInit.initialize(options);
    assertTrue(controller.getModel().isWhiteAi());
    assertFalse(controller.getModel().isBlackAi());
    assertNotNull(controller.getModel().getWhiteSolver());
    assertEquals(1000, controller.getModel().getWhiteSolver().getTime());
  }

  @Test
  void testGameInitializationAITimeBlitz3() {
    options.put(OptionType.AI, "W");
    options.put(OptionType.AI_TIME, "100000");
    options.put(OptionType.BLITZ, "");
    options.put(OptionType.TIME, "1");
    GameController controller = GameControllerInit.initialize(options);
    assertTrue(controller.getModel().isWhiteAi());
    assertFalse(controller.getModel().isBlackAi());
    assertNotNull(controller.getModel().getWhiteSolver());
    // Blitz time
    assertEquals(60000, controller.getModel().getWhiteSolver().getTime());
  }

  @Test
  void testGameInitializationLoadSuccess() throws IOException {
    tempFile = Files.createTempFile("moveHistory", ".txt");
    Files.write(tempFile, Arrays.asList("e2-e4"));

    options.put(OptionType.LOAD, tempFile.toString());

    GameController controller = GameControllerInit.initialize(options);

    Position newPosition = Move.stringToPosition("e4");

    assertNotNull(controller);
    assertEquals(
        controller
            .getModel()
            .getBoard()
            .getBoardRep()
            .getPieceAt(newPosition.getX(), newPosition.getY())
            .piece,
        Piece.PAWN);
  }

  @Test
  void testGameInitializationFullGame() throws IOException {
    tempFile = Files.createTempFile("moveHistory", ".txt");
    String text =
        "B\n"
            + //
            "r _ b q k b _ r \n"
            + //
            "p p p p _ Q p p \n"
            + //
            "_ _ n _ _ n _ _ \n"
            + //
            "_ _ _ _ p _ _ _ \n"
            + //
            "_ _ B _ P _ _ _ \n"
            + //
            "_ _ _ _ _ _ _ _ \n"
            + //
            "P P P P _ P P P \n"
            + //
            "R N B _ K _ N R \n"
            + //
            "\n"
            + //
            "1. W e2-e4 B e7-e5\n"
            + //
            "2. W d1-h5 B b8-c6\n"
            + //
            "3. W f1-c4 B g8-f6\n"
            + //
            "4. W h5xf7";
    Files.writeString(tempFile, text);

    options.put(OptionType.LOAD, tempFile.toString());

    GameController controller = GameControllerInit.initialize(options);

    assertTrue(controller.getModel().getGameState().isGameOver());
  }

  @Test
  void testGameInitializationLoadFallback() {
    options.put(OptionType.LOAD, "non_existent_file.txt");

    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    PrintStream originalErr = System.err;
    System.setErr(new PrintStream(errContent));

    GameController controller = null;
    controller = GameControllerInit.initialize(options);

    String errorOutput = errContent.toString();

    System.setErr(originalErr);

    assertTrue(errorOutput.contains("Using the default game start"));

    assertNotNull(controller);
  }

  @Test
  void loadGameWithNoHistoryTest() {
    // The loaded board should correspond to the board in the given file
    BoardFileParser parser = new BoardFileParser();
    ClassLoader classLoader = getClass().getClassLoader();
    URL filePath = classLoader.getResource("gameBoards/defaultGameWithBlackTurn");
    FileBoard board = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    options.put(OptionType.LOAD, filePath.getPath());
    GameController controller = GameControllerInit.initialize(options);
    assertEquals(controller.getModel().getBoard().getBoardRep(), board.board());
    assertEquals(controller.getModel().getGameState().isWhiteTurn(), board.isWhiteTurn());
  }
}
