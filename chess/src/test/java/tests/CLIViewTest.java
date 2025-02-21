package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.CancelDrawCommand;
import pdp.controller.commands.CancelMoveCommand;
import pdp.controller.commands.PlayMoveCommand;
import pdp.controller.commands.ProposeDrawCommand;
import pdp.controller.commands.RestoreMoveCommand;
import pdp.controller.commands.SaveGameCommand;
import pdp.controller.commands.SurrenderCommand;
import pdp.events.EventType;
import pdp.exceptions.CommandNotAvailableNowException;
import pdp.exceptions.FailedRedoException;
import pdp.exceptions.FailedSaveException;
import pdp.exceptions.FailedUndoException;
import pdp.exceptions.IllegalMoveException;
import pdp.exceptions.InvalidPositionException;
import pdp.exceptions.InvalidPromoteFormatException;
import pdp.exceptions.MoveParsingException;
import pdp.model.Game;
import pdp.utils.TextGetter;
import pdp.utils.Timer;
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

    outputStream.reset();

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
    String simulatedInput = "move e2-e4\ndraw\nhelp\n";
    System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
    Thread inputThread = view.start();

    Thread.sleep(300);

    verify(mockBagOfCommands).addCommand(any(PlayMoveCommand.class));

    verify(mockBagOfCommands).addCommand(any(ProposeDrawCommand.class));

    assertTrue(outputStream.toString().contains("Available commands:"));

    inputThread.interrupt();
  }

  @Test
  void testMoveCommand() throws Exception {

    handleUserInputMethod.invoke(view, "move e2-e4");

    verify(mockBagOfCommands).addCommand(any(PlayMoveCommand.class));
  }

  @Test
  void testDrawCommand() throws Exception {

    handleUserInputMethod.invoke(view, "draw");

    verify(mockBagOfCommands).addCommand(any(ProposeDrawCommand.class));
  }

  @Test
  void testUndrawCommand() throws Exception {

    handleUserInputMethod.invoke(view, "undraw");

    verify(mockBagOfCommands).addCommand(any(CancelDrawCommand.class));
  }

  @Test
  void testSaveCommand() throws Exception {

    handleUserInputMethod.invoke(view, "save");

    verify(mockBagOfCommands).addCommand(any(SaveGameCommand.class));
  }

  @Test
  void testDisplayBoardCommand() throws Exception {

    handleUserInputMethod.invoke(view, "board");

    String output = outputStream.toString();
    assertTrue(output.contains(Game.getInstance().getGameRepresentation()));
  }

  @Test
  void testHistoryCommand() throws Exception {

    handleUserInputMethod.invoke(view, "history");

    String output = outputStream.toString();
    assertTrue(output.contains(Game.getInstance().getHistory().toString()));
  }

  @Test
  void testUndoCommand() throws Exception {
    handleUserInputMethod.invoke(view, "undo");
    verify(mockBagOfCommands).addCommand(any(CancelMoveCommand.class));
  }

  @Test
  void testRedoCommand() throws Exception {
    handleUserInputMethod.invoke(view, "redo");
    verify(mockBagOfCommands).addCommand(any(RestoreMoveCommand.class));
  }

  @Test
  void testSurrenderCommand() throws Exception {
    handleUserInputMethod.invoke(view, "surrender");
    verify(mockBagOfCommands).addCommand(any(SurrenderCommand.class));
  }

  @Test
  void testTimeCommand() throws Exception {

    Game game = Game.initialize(false, false, null, new Timer(3500000));

    Thread.sleep(100);

    game.getGameState().getMoveTimer().stop();

    handleUserInputMethod.invoke(view, "time");
    String output = outputStream.toString();
    Pattern pattern = Pattern.compile("\\d{2}:\\d{2}");
    Matcher matcher = pattern.matcher(output);

    assertTrue(matcher.find());
    assertTrue(output.contains(game.getGameState().getMoveTimer().getTimeRemainingString()));
  }

  @Test
  void testTimeCommandNoTimer() throws Exception {

    Game game = Game.initialize(false, false, null, null);

    handleUserInputMethod.invoke(view, "time");
    String output = outputStream.toString();
    assertTrue(output.contains(TextGetter.getText("noTimer")));
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
    assertTrue(output.contains("draw"));
    assertTrue(output.contains("undraw"));
    assertTrue(output.contains("help"));
    assertTrue(output.contains("quit"));
  }

  @Test
  void testOnErrorEventWithIllegalMoveException() {
    view.onErrorEvent(new IllegalMoveException("e2-e4"));

    assertTrue(outputStream.toString().contains("e2-e4"));
  }

  @Test
  public void testGameStartedEvent() {

    view.onGameEvent(EventType.GAME_STARTED);

    String output = outputStream.toString();

    assertTrue(output.contains(Game.getInstance().getGameRepresentation()));
  }

  @Test
  public void testMovePlayedEvent() {
    view.onGameEvent(EventType.MOVE_PLAYED);

    String output = outputStream.toString();

    assertTrue(output.contains(Game.getInstance().getGameRepresentation()));
  }

  @Test
  public void testWinWhiteEvent() {
    view.onGameEvent(EventType.WIN_WHITE);

    String output = outputStream.toString();

    assertTrue(output.contains(TextGetter.getText("whiteWin")));
  }

  @Test
  public void testWinBlackEvent() {
    view.onGameEvent(EventType.WIN_BLACK);

    String output = outputStream.toString();

    assertTrue(output.contains(TextGetter.getText("blackWin")));
  }

  @Test
  public void testUndoEvent() {
    view.onGameEvent(EventType.MOVE_UNDO);

    String output = outputStream.toString();

    assertTrue(output.contains(Game.getInstance().getGameRepresentation()));
  }

  @Test
  public void testRedoEvent() {
    view.onGameEvent(EventType.MOVE_REDO);

    String output = outputStream.toString();

    assertTrue(output.contains(Game.getInstance().getGameRepresentation()));
  }

  @Test
  public void testDrawEvent() {
    view.onGameEvent(EventType.DRAW);

    String output = outputStream.toString();

    assertTrue(output.contains(TextGetter.getText("onDraw")));
  }

  @Test
  public void testWhiteDrawProposalEvent() {
    view.onGameEvent(EventType.WHITE_DRAW_PROPOSAL);

    String output = outputStream.toString();
    String expected = TextGetter.getText("drawProposal", TextGetter.getText("white"));

    assertTrue(output.contains(expected));
  }

  @Test
  public void testBlackDrawProposalEvent() {
    view.onGameEvent(EventType.BLACK_DRAW_PROPOSAL);

    String output = outputStream.toString();
    String expected = TextGetter.getText("drawProposal", TextGetter.getText("black"));

    assertTrue(output.contains(expected));
  }

  @Test
  public void testWhiteUndrawEvent() {
    view.onGameEvent(EventType.WHITE_UNDRAW);

    String output = outputStream.toString();
    String expected = TextGetter.getText("cancelDrawProposal", TextGetter.getText("white"));

    assertTrue(output.contains(expected));
  }

  @Test
  public void testBlackUndrawEvent() {
    view.onGameEvent(EventType.BLACK_UNDRAW);

    String output = outputStream.toString();
    String expected = TextGetter.getText("cancelDrawProposal", TextGetter.getText("black"));

    assertTrue(output.contains(expected));
  }

  @Test
  public void testDrawAcceptedEvent() {
    view.onGameEvent(EventType.DRAW_ACCEPTED);

    String output = outputStream.toString();
    String expected = TextGetter.getText("drawAccepted");

    assertTrue(output.contains(expected));
  }

  @Test
  public void testGameSavedEvent() {
    view.onGameEvent(EventType.GAME_SAVED);

    String output = outputStream.toString();
    String expected = TextGetter.getText("gameSaved");

    assertTrue(output.contains(expected));
  }

  @Test
  public void testMoveUndoEvent() {
    view.onGameEvent(EventType.MOVE_UNDO);

    String output = outputStream.toString();
    String expected = TextGetter.getText("moveUndone");

    assertTrue(output.contains(expected));
    assertTrue(output.contains(Game.getInstance().getGameRepresentation()));
  }

  @Test
  public void testMoveRedoEvent() {
    view.onGameEvent(EventType.MOVE_REDO);

    String output = outputStream.toString();
    String expected = TextGetter.getText("moveRedone");

    assertTrue(output.contains(expected));
    assertTrue(output.contains(Game.getInstance().getGameRepresentation()));
  }

  @Test
  public void testTimeoutWhiteEvent() {
    view.onGameEvent(EventType.OUT_OF_TIME_WHITE);

    String output = outputStream.toString();
    String expected = TextGetter.getText("outOfTime", TextGetter.getText("white"));

    assertTrue(output.contains(expected));
  }

  @Test
  public void testTimeoutBlackEvent() {
    view.onGameEvent(EventType.OUT_OF_TIME_BLACK);

    String output = outputStream.toString();
    String expected = TextGetter.getText("outOfTime", TextGetter.getText("black"));

    assertTrue(output.contains(expected));
  }

  @Test
  public void testThreefoldEvent() {
    view.onGameEvent(EventType.THREEFOLD_REPETITION);

    String output = outputStream.toString();
    String expected = TextGetter.getText("threeFoldRepetition");

    assertTrue(output.contains(expected));
  }

  @Test
  public void testInsufficientMaterialEvent() {
    view.onGameEvent(EventType.INSUFFICIENT_MATERIAL);

    String output = outputStream.toString();
    String expected = TextGetter.getText("insufficientMaterial");

    assertTrue(output.contains(expected));
  }

  @Test
  public void testFiftyMoveEvent() {
    view.onGameEvent(EventType.FIFTY_MOVE_RULE);

    String output = outputStream.toString();
    String expected = TextGetter.getText("fiftyMoveRule");

    assertTrue(output.contains(expected));
  }

  @Test
  public void testWhiteResignsEvent() {
    view.onGameEvent(EventType.WHITE_RESIGNS);

    String output = outputStream.toString();
    String expected = TextGetter.getText("resigns", TextGetter.getText("white"));

    assertTrue(output.contains(expected));
  }

  @Test
  public void testBlackResignsEvent() {
    view.onGameEvent(EventType.BLACK_RESIGNS);

    String output = outputStream.toString();
    String expected = TextGetter.getText("resigns", TextGetter.getText("black"));

    assertTrue(output.contains(expected));
  }

  @Test
  public void testCheckmateWhiteEvent() {
    view.onGameEvent(EventType.CHECKMATE_WHITE);

    String output = outputStream.toString();
    String expected =
        TextGetter.getText("checkmate", TextGetter.getText("white"), TextGetter.getText("black"));

    assertTrue(output.contains(expected));
  }

  @Test
  public void testCheckmateBlackEvent() {
    view.onGameEvent(EventType.CHECKMATE_BLACK);

    String output = outputStream.toString();
    String expected =
        TextGetter.getText("checkmate", TextGetter.getText("black"), TextGetter.getText("white"));

    assertTrue(output.contains(expected));
  }

  @Test
  public void testStalemateEvent() {
    view.onGameEvent(EventType.STALEMATE);

    String output = outputStream.toString();
    String expected = TextGetter.getText("stalemate");

    assertTrue(output.contains(expected));
  }

  @Test
  public void testOnIllegalMoveException() {
    view.onErrorEvent(new IllegalMoveException("e2-e4"));

    String output = outputStream.toString();

    assertTrue(output.contains("e2-e4"));
  }

  @Test
  public void testOnMoveParsingException() {
    view.onErrorEvent(new MoveParsingException("parsing failed"));

    String output = outputStream.toString();

    assertTrue(output.contains("parsing failed"));
  }

  @Test
  public void testOnInvalidPositionException() {
    view.onErrorEvent(new InvalidPositionException("invalid position"));

    String output = outputStream.toString();

    assertTrue(output.contains("invalid position"));
  }

  @Test
  public void testOnFailedSaveException() {
    view.onErrorEvent(new FailedSaveException("savefile.txt"));

    String output = outputStream.toString();

    assertTrue(output.contains("savefile.txt"));
  }

  @Test
  public void testOnInvalidPromoteFormatException() {
    view.onErrorEvent(new InvalidPromoteFormatException());

    String output = outputStream.toString();

    assertTrue(output.contains(TextGetter.getText("invalidPromoteFormat", "e7-e8=Q")));
  }

  @Test
  public void testOnFailedUndoException() {
    view.onErrorEvent(new FailedUndoException());

    String output = outputStream.toString();

    assertTrue(output.contains(TextGetter.getText("failedUndo")));
  }

  @Test
  public void testOnFailedRedoException() {
    view.onErrorEvent(new FailedRedoException());

    String output = outputStream.toString();

    assertTrue(output.contains(TextGetter.getText("failedRedo")));
  }

  @Test
  public void testOnCommandNotAvailableNowException() {
    view.onErrorEvent(new CommandNotAvailableNowException());

    String output = outputStream.toString();

    assertTrue(output.contains(TextGetter.getText("commandNotAvailable")));
  }
}
