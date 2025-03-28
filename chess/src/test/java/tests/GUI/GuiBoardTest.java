package tests.GUI;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import pdp.GameInitializer;
import pdp.model.Game;
import pdp.model.board.BitboardRepresentation;
import pdp.model.parsers.BoardFileParser;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.OptionType;
import pdp.view.gui.board.Board;
import pdp.view.gui.board.Square;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GuiBoardTest extends ApplicationTest {

  private HashMap<OptionType, String> options;
  private Board board;
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @AfterAll
  void tearDownConsole() {
    System.setOut(originalOut);
    System.setErr(originalErr);
    outputStream.reset();
  }

  @BeforeAll
  public void setup() {
    System.setOut(new PrintStream(outputStream));
    System.setErr(new PrintStream(outputStream));
    options = new HashMap<>();
    GameInitializer.initialize(options);
  }

  @Override
  public void start(Stage stage) {
    Platform.runLater(
        () -> {
          board = new Board(Game.getInstance(), stage);
          Scene scene = new Scene(board);
          stage.setWidth(800);
          stage.setHeight(800);
          stage.setScene(scene);
          stage.show();
        });
  }

  @Test
  @Tag("gui")
  public void testMovePiece() {
    Game.initialize(false, false, null, null, null, options);
    Platform.runLater(() -> board.updateBoard());
    assertEquals(
        new ColoredPiece(Piece.PAWN, Color.WHITE), Game.getInstance().getBoard().getPieceAt(0, 1));
    Square square1 = lookup("#square01").query();
    Square square2 = lookup("#square02").query();
    clickOn(square1);
    clickOn(square2);
    assertEquals(
        new ColoredPiece(Piece.EMPTY, Color.EMPTY), Game.getInstance().getBoard().getPieceAt(0, 1));
    assertEquals(
        new ColoredPiece(Piece.PAWN, Color.WHITE), Game.getInstance().getBoard().getPieceAt(0, 2));
  }

  @Test
  @Tag("gui")
  public void testIllegalMovePiece() {
    Game.initialize(false, false, null, null, null, options);
    Platform.runLater(() -> board.updateBoard());
    Square square01 = lookup("#square01").query();
    Square square02 = lookup("#square02").query();
    clickOn(square01);
    clickOn(square02);
    assertEquals(
        new ColoredPiece(Piece.PAWN, Color.BLACK), Game.getInstance().getBoard().getPieceAt(0, 6));
    Square square1 = lookup("#square06").query();
    Square square2 = lookup("#square03").query();
    clickOn(square1);
    clickOn(square2);
    assertEquals(
        new ColoredPiece(Piece.PAWN, Color.BLACK), Game.getInstance().getBoard().getPieceAt(0, 6));
    assertEquals(
        new ColoredPiece(Piece.EMPTY, Color.EMPTY), Game.getInstance().getBoard().getPieceAt(0, 3));
  }

  @Test
  @Tag("gui")
  public void switchSelectedPieceWhite() {
    Game.initialize(false, false, null, null, null, options);
    Platform.runLater(() -> board.updateBoard());
    assertEquals(
        new ColoredPiece(Piece.PAWN, Color.WHITE), Game.getInstance().getBoard().getPieceAt(5, 1));
    assertEquals(
        new ColoredPiece(Piece.PAWN, Color.WHITE), Game.getInstance().getBoard().getPieceAt(6, 1));
    Square square1 = lookup("#square51").query();
    Square square2 = lookup("#square61").query();
    clickOn(square1);
    clickOn(square2);
    assertEquals(
        new ColoredPiece(Piece.PAWN, Color.WHITE), Game.getInstance().getBoard().getPieceAt(5, 1));
    assertEquals(
        new ColoredPiece(Piece.PAWN, Color.WHITE), Game.getInstance().getBoard().getPieceAt(6, 1));
    Square square3 = lookup("#square63").query();
    clickOn(square3);
    assertEquals(
        new ColoredPiece(Piece.EMPTY, Color.EMPTY), Game.getInstance().getBoard().getPieceAt(6, 1));
    assertEquals(
        new ColoredPiece(Piece.PAWN, Color.WHITE), Game.getInstance().getBoard().getPieceAt(6, 3));
  }

  @Test
  @Tag("gui")
  public void switchSelectedPieceBlack() {
    Game.initialize(false, false, null, null, null, options);
    Platform.runLater(() -> board.updateBoard());
    clickOn((Square) lookup("#square51").query());
    clickOn((Square) lookup("#square52").query());

    assertEquals(
        new ColoredPiece(Piece.ROOK, Color.BLACK), Game.getInstance().getBoard().getPieceAt(0, 7));
    assertEquals(
        new ColoredPiece(Piece.PAWN, Color.BLACK), Game.getInstance().getBoard().getPieceAt(6, 6));
    Square square1 = lookup("#square07").query();
    Square square2 = lookup("#square66").query();
    clickOn(square1);
    clickOn(square2);
    assertEquals(
        new ColoredPiece(Piece.ROOK, Color.BLACK), Game.getInstance().getBoard().getPieceAt(0, 7));
    assertEquals(
        new ColoredPiece(Piece.PAWN, Color.BLACK), Game.getInstance().getBoard().getPieceAt(6, 6));
    Square square3 = lookup("#square65").query();
    clickOn(square3);
    assertEquals(
        new ColoredPiece(Piece.EMPTY, Color.EMPTY), Game.getInstance().getBoard().getPieceAt(6, 6));
    assertEquals(
        new ColoredPiece(Piece.PAWN, Color.BLACK), Game.getInstance().getBoard().getPieceAt(6, 5));
  }

  @Test
  @Tag("gui")
  public void testWhiteClickOnNonWhiteSquare() {
    Game.initialize(false, false, null, null, null, options);
    Platform.runLater(() -> board.updateBoard());
    Square square1 = lookup("#square07").query(); // click on black piece
    Square square2 = lookup("#square05").query(); // click on empty square
    clickOn(square1);
    clickOn(square2);
    assertEquals(new BitboardRepresentation(), Game.getInstance().getBoard());
  }

  @Test
  @Tag("gui")
  public void testBlackClickOnNonBlackSquare() {
    Game.initialize(false, false, null, null, null, options);
    Platform.runLater(() -> board.updateBoard());
    Square square1 = lookup("#square40").query(); // click on black piece
    Square square2 = lookup("#square05").query(); // click on empty square
    clickOn(square1);
    clickOn(square2);
    assertEquals(new BitboardRepresentation(), Game.getInstance().getBoard());
  }

  @Test
  @Tag("gui")
  public void testPromoteWhitePawnToRook() {
    URL filePath = getClass().getClassLoader().getResource("gameBoards/whitePawnPromote");
    HashMap<OptionType, String> options = new HashMap<>();
    options.put(OptionType.LOAD, filePath.getPath());
    BoardFileParser parser = new BoardFileParser();
    FileBoard loadedBoard = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    Game.initialize(false, false, null, null, null, loadedBoard, options);
    Platform.runLater(() -> board.updateBoard());
    Square square1 = lookup("#square66").query();
    Square square2 = lookup("#square67").query();
    clickOn(square1);
    clickOn(square2);
    VBox vb = lookup("#rookButton").query();
    clickOn(vb);
    assertEquals(
        new ColoredPiece(Piece.ROOK, Color.WHITE), Game.getInstance().getBoard().getPieceAt(6, 7));
  }

  @Test
  @Tag("gui")
  public void testPromoteWhitePawnToBishop() {
    URL filePath = getClass().getClassLoader().getResource("gameBoards/whitePawnPromote");
    HashMap<OptionType, String> options = new HashMap<>();
    options.put(OptionType.LOAD, filePath.getPath());
    BoardFileParser parser = new BoardFileParser();
    FileBoard loadedBoard = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    Game.initialize(false, false, null, null, null, loadedBoard, options);
    Platform.runLater(() -> board.updateBoard());
    Square square1 = lookup("#square66").query();
    Square square2 = lookup("#square67").query();
    clickOn(square1);
    clickOn(square2);
    VBox vb = lookup("#bishopButton").query();
    clickOn(vb);
    assertEquals(
        new ColoredPiece(Piece.BISHOP, Color.WHITE),
        Game.getInstance().getBoard().getPieceAt(6, 7));
  }

  @Test
  @Tag("gui")
  public void testPromoteWhitePawnToKnight() {
    URL filePath = getClass().getClassLoader().getResource("gameBoards/whitePawnPromote");
    HashMap<OptionType, String> options = new HashMap<>();
    options.put(OptionType.LOAD, filePath.getPath());
    BoardFileParser parser = new BoardFileParser();
    FileBoard loadedBoard = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    Game.initialize(false, false, null, null, null, loadedBoard, options);
    Platform.runLater(() -> board.updateBoard());
    Square square1 = lookup("#square66").query();
    Square square2 = lookup("#square67").query();
    clickOn(square1);
    clickOn(square2);
    VBox vb = lookup("#knightButton").query();
    clickOn(vb);
    assertEquals(
        new ColoredPiece(Piece.KNIGHT, Color.WHITE),
        Game.getInstance().getBoard().getPieceAt(6, 7));
  }

  @Test
  @Tag("gui")
  public void testPromoteWhitePawnToQueen() {
    URL filePath = getClass().getClassLoader().getResource("gameBoards/whitePawnPromote");
    HashMap<OptionType, String> options = new HashMap<>();
    options.put(OptionType.LOAD, filePath.getPath());
    BoardFileParser parser = new BoardFileParser();
    FileBoard loadedBoard = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    Game.initialize(false, false, null, null, null, loadedBoard, options);
    Platform.runLater(() -> board.updateBoard());
    Square square1 = lookup("#square66").query();
    Square square2 = lookup("#square67").query();
    clickOn(square1);
    clickOn(square2);
    VBox vb = lookup("#queenButton").query();
    clickOn(vb);
    assertEquals(
        new ColoredPiece(Piece.QUEEN, Color.WHITE), Game.getInstance().getBoard().getPieceAt(6, 7));
  }

  @Test
  @Tag("gui")
  public void testDiagonalPromoteWhitePawnToQueen() {
    URL filePath = getClass().getClassLoader().getResource("gameBoards/whitePawnPromote");
    HashMap<OptionType, String> options = new HashMap<>();
    options.put(OptionType.LOAD, filePath.getPath());
    BoardFileParser parser = new BoardFileParser();
    FileBoard loadedBoard = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    Game.initialize(false, false, null, null, null, loadedBoard, options);
    Platform.runLater(() -> board.updateBoard());
    Square square1 = lookup("#square66").query();
    Square square2 = lookup("#square77").query();
    clickOn(square1);
    clickOn(square2);
    VBox vb = lookup("#queenButton").query();
    clickOn(vb);
    assertEquals(
        new ColoredPiece(Piece.QUEEN, Color.WHITE), Game.getInstance().getBoard().getPieceAt(7, 7));
  }

  @Test
  @Tag("gui")
  public void testPromoteBlackPawnToQueen() {
    URL filePath = getClass().getClassLoader().getResource("gameBoards/blackPawnPromote");
    HashMap<OptionType, String> options = new HashMap<>();
    options.put(OptionType.LOAD, filePath.getPath());
    BoardFileParser parser = new BoardFileParser();
    FileBoard loadedBoard = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    Game.initialize(false, false, null, null, null, loadedBoard, options);
    Platform.runLater(() -> board.updateBoard());
    Square square1 = lookup("#square11").query();
    Square square2 = lookup("#square10").query();
    clickOn(square1);
    clickOn(square2);
    VBox vb = lookup("#queenButton").query();
    clickOn(vb);
    assertEquals(
        new ColoredPiece(Piece.QUEEN, Color.BLACK), Game.getInstance().getBoard().getPieceAt(1, 0));
  }
}
