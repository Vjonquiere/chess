package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.GameInitializer;
import pdp.controller.GameController;
import pdp.model.board.Move;
import pdp.model.piece.Piece;
import pdp.utils.OptionType;
import pdp.utils.Position;
import pdp.view.CLIView;

class GameInitializerTest {
  private HashMap<OptionType, String> options;
  private Path tempFile;

  @BeforeEach
  void setUp() {
    options = new HashMap<>();
  }

  @AfterEach
  void tearDown() throws IOException {
    // Clean up temporary file if it was created.
    if (tempFile != null && Files.exists(tempFile)) {
      Files.delete(tempFile);
    }
  }

  @Test
  void testGameInitializationCLI() {
    GameController controller = GameInitializer.initialize(options, null);
    assertNotNull(controller);
    assertTrue(controller.getView() instanceof CLIView);
  }

  /*
  @Test
  void testGameInitializationGUI() {
    options.put(OptionType.GUI, "");
    GameController controller = GameInitializer.initialize(options);
    assertNotNull(controller);
    assertTrue(controller.getView() instanceof GameView);
  }
    */

  /*
  @Test
  void testGameInitializationBlitzMode300() {
    options.put(OptionType.BLITZ, "");
    GameController controller = GameInitializer.initialize(options);
    assertNotNull(controller);
    assertNotNull(controller.getModel().getGameState().getMoveTimer());
  }
    */

  /*
  @Test
  void testGameInitializationBlitzMode300() {
    options.put(OptionType.BLITZ, "");
    options.put(OptionType.TIME, "300");
    GameController controller = GameInitializer.initialize(options);
    assertNotNull(controller);
    assertNotNull(controller.getModel().getGameState().getMoveTimer());
    assertEquals(controller.getModel().getGameState().getMoveTimer().getTimeRemaining(), 300);
  }
    */

  /*
    @Test
    void testGameInitializationAIWhite() {
      options.put(OptionType.AI, "W");
      GameController controller = GameInitializer.initialize(options);
      assertNotNull(controller);
    }

    @Test
    void testGameInitializationAIBlack() {
      options.put(OptionType.AI, "B");
      GameController controller = GameInitializer.initialize(options);
      assertNotNull(controller);
    }

    @Test
    void testGameInitializationAIAll() {
      options.put(OptionType.AI, "A");
      GameController controller = GameInitializer.initialize(options);
      assertNotNull(controller);
    }
  */

  @Test
  void testGameInitializationLoadSuccess() throws IOException {
    tempFile = Files.createTempFile("moveHistory", ".txt");
    Files.write(tempFile, Arrays.asList("e2-e4"));

    options.put(OptionType.LOAD, tempFile.toString());

    GameController controller = GameInitializer.initialize(options, null);

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
  void testGameInitializationLoadFallback() {
    options.put(OptionType.LOAD, "non_existent_file.txt");

    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    PrintStream originalErr = System.err;
    System.setErr(new PrintStream(errContent));

    GameController controller = null;
    controller = GameInitializer.initialize(options, null);

    String errorOutput = errContent.toString();

    System.setErr(originalErr);

    assertTrue(errorOutput.contains("Using the default game start"));

    assertNotNull(controller);
  }
}
