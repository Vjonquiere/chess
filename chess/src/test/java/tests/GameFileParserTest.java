package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.net.URL;
import org.junit.jupiter.api.Test;
import pdp.model.board.BitboardRepresentation;
import pdp.model.parsers.BoardFileParser;
import pdp.model.parsers.FileBoard;

public class GameFileParserTest {
  private BoardFileParser parser = new BoardFileParser();
  private ClassLoader classLoader = getClass().getClassLoader();

  @Test
  public void parseDefaultGameFile() {
    URL filePath = classLoader.getResource("gameBoards/defaultGame");
    FileBoard board = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    assertEquals(new BitboardRepresentation(), board.board());
    assertTrue(board.isWhiteTurn());
  }

  @Test
  public void parseEmptyGameFile() {
    /* Loading an empty game is not possible,
       the result should be a default board
    */
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/emptyGame");
    FileBoard board = parser.parseGameFile(filePath.getPath(), mockRuntime);
    mockRuntime.exit(1);
  }

  @Test
  public void parseWrongBoardFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/wrongBoard");
    FileBoard board = parser.parseGameFile(filePath.getPath(), mockRuntime);
    mockRuntime.exit(1);
  }

  @Test
  public void parseUnknownFile() {
    Runtime mockRuntime = mock(Runtime.class);
    FileBoard board = parser.parseGameFile("Unknow/file/path", mockRuntime);
    mockRuntime.exit(1);
  }

  @Test
  public void parseWrongPlayerFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/unknownPlayerGame");
    FileBoard board = parser.parseGameFile(filePath.getPath(), mockRuntime);
    mockRuntime.exit(1);
  }

  @Test
  public void parseWrongFormattedFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/wrongFormattedGame");
    FileBoard board = parser.parseGameFile(filePath.getPath(), mockRuntime);
    mockRuntime.exit(1);
  }

  @Test
  public void parseUnknownPieceFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/unknownPieceGame");
    FileBoard board = parser.parseGameFile(filePath.getPath(), mockRuntime);
    mockRuntime.exit(1);
  }
}
