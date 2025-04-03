package tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.model.Game;
import pdp.model.board.Move;
import pdp.model.parsers.BoardFileParser;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.model.savers.BoardSaver;
import pdp.utils.Position;

public class BoardSaverTest {
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @BeforeEach
  void setUpConsole() {
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
  public void testDefaultBoardSave() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    String boardString =
        BoardSaver.saveBoard(
            new FileBoard(game.getBoard(), game.getGameState().isWhiteTurn(), null));
    String[] expectedBoardString = {
      "W",
      "r n b q k b n r",
      "p p p p p p p p",
      "_ _ _ _ _ _ _ _",
      "_ _ _ _ _ _ _ _",
      "_ _ _ _ _ _ _ _",
      "_ _ _ _ _ _ _ _",
      "P P P P P P P P",
      "R N B Q K B N R"
    };
    for (String s : expectedBoardString) {
      assertTrue(boardString.contains(s));
    }
  }

  @Test
  public void testBoardSaveAfterOneMovePlayed() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    game.playMove(
        new Move(
            new Position(0, 1),
            new Position(0, 3),
            new ColoredPiece(Piece.PAWN, Color.WHITE),
            false));
    String boardString =
        BoardSaver.saveBoard(
            new FileBoard(game.getBoard(), game.getGameState().isWhiteTurn(), null));
    String[] expectedBoardString = {
      "B",
      "r n b q k b n r",
      "p p p p p p p p",
      "_ _ _ _ _ _ _ _",
      "_ _ _ _ _ _ _ _",
      "P _ _ _ _ _ _ _",
      "_ _ _ _ _ _ _ _",
      "_ P P P P P P P",
      "R N B Q K B N R"
    };
    for (String s : expectedBoardString) {
      assertTrue(boardString.contains(s));
    }
  }

  @Test
  public void testBoardSaveOnKnownGameBoard() {
    BoardFileParser parser = new BoardFileParser();
    ClassLoader classLoader = getClass().getClassLoader();
    URL filePath = classLoader.getResource("gameBoards/gameExample1WithHistory");
    FileBoard board = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    Game game = Game.initialize(false, false, null, null, null, board, new HashMap<>());
    String boardString =
        BoardSaver.saveBoard(
            new FileBoard(game.getBoard(), game.getGameState().isWhiteTurn(), null));
    String[] expectedBoardString = {
      "B",
      "r n b q k b _ r",
      "p p p p p p p p",
      "_ _ _ _ _ _ _ _",
      "_ _ _ _ _ _ _ _",
      "P _ _ _ _ _ n _",
      "_ _ _ _ _ _ _ B",
      "_ P P P P P _ P",
      "R N B Q K _ N R",
    };
    for (String s : expectedBoardString) {
      assertTrue(boardString.contains(s));
    }
  }
}
