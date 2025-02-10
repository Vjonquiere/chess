package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.PlayMoveCommand;
import pdp.controller.commands.SaveGameCommand;
import pdp.exceptions.IllegalMoveException;
import pdp.model.Game;
import pdp.view.CLIView;

public class CLIViewTest {

  private CLIView view;
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;
  private BagOfCommands mockBagOfCommands;
  private Method handleUserInputMethod;

  @Test
  public void testBoardToASCII() {
    Game game = Game.initialize(false, false, null, null);

    char[][] expectedBoard = {
      {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
      {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
      {'_', '_', '_', '_', '_', '_', '_', '_'},
      {'_', '_', '_', '_', '_', '_', '_', '_'},
      {'_', '_', '_', '_', '_', '_', '_', '_'},
      {'_', '_', '_', '_', '_', '_', '_', '_'},
      {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
      {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
    };

    assertTrue(Arrays.deepEquals(expectedBoard, game.getBoard().getAsciiRepresentation()));
  }

  @BeforeEach
  void setUp() throws Exception {
    System.setOut(new PrintStream(outputStream));
    System.setErr(new PrintStream(outputStream));

    mockBagOfCommands = mock(BagOfCommands.class);
    BagOfCommands.setInstance(mockBagOfCommands);

    view = new CLIView();

    handleUserInputMethod = CLIView.class.getDeclaredMethod("handleUserInput", String.class);
    handleUserInputMethod.setAccessible(true); // Allows access to private method
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  @Test
  void testInputListener() throws InterruptedException {
    String simulatedInput = "move e2-e4\nsave game.txt\nhelp\n";
    System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
    Thread inputThread = view.start();

    Thread.sleep(100);

    verify(mockBagOfCommands).addCommand(any(PlayMoveCommand.class));

    verify(mockBagOfCommands).addCommand(any(SaveGameCommand.class));

    assertTrue(outputStream.toString().contains("Available commands:"));

    inputThread.interrupt();
  }

  @Test
  void testMoveCommand() throws Exception {

    handleUserInputMethod.invoke(view, "move e2-e4");

    verify(mockBagOfCommands).addCommand(any(PlayMoveCommand.class));
  }

  @Test
  void testSaveCommand() throws Exception {

    handleUserInputMethod.invoke(view, "save game.txt");

    verify(mockBagOfCommands).addCommand(any(SaveGameCommand.class));
  }

  @Test
  void testUnknownCommand() throws Exception {
    handleUserInputMethod.invoke(view, "unknown");

    String output = outputStream.toString();
    assertTrue(output.contains("Unknown command: unknown"));
    assertTrue(output.contains("Available commands:"));
  }

  @Test
  void testHelpCommand() throws Exception {
    handleUserInputMethod.invoke(view, "help");

    String output = outputStream.toString();
    assertTrue(output.contains("Available commands:"));
    assertTrue(output.contains("move"));
    assertTrue(output.contains("save"));
    assertTrue(output.contains("help"));
    assertTrue(output.contains("quit"));
  }

  @Test
  void testOnErrorEventWithIllegalMoveException() {
    view.onErrorEvent(new IllegalMoveException("Invalid move!"));

    assertTrue(outputStream.toString().contains("Invalid move!"));
  }
}
