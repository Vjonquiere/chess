package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URL;
import org.junit.jupiter.api.Test;
import pdp.model.board.Bitboard;
import pdp.model.board.BitboardRepresentation;
import pdp.model.parsers.BoardFileParser;
import pdp.model.parsers.FileBoard;

public class GameFileParserTest {
  private BoardFileParser parser = new BoardFileParser();
  private ClassLoader classLoader = getClass().getClassLoader();

  @Test
  public void parseDefaultGameFile() {
    URL filePath = classLoader.getResource("gameBoards/defaultGame");
    FileBoard board = parser.parseGameFile(filePath.getPath());
    assertEquals(new BitboardRepresentation(), board.board());
    assertTrue(board.isWhiteTurn());
  }

  @Test
  public void parseEmptyGameFile() {
    /* Loading an empty game is not possible,
       the result should be a default board
    */
    URL filePath = classLoader.getResource("gameBoards/emptyGame");
    FileBoard board = parser.parseGameFile(filePath.getPath());
    assertNotEquals(
        new BitboardRepresentation(
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L)),
        board);
    assertEquals(new BitboardRepresentation(), board.board());
  }

  @Test
  public void parseWrongBoardFile() {
    URL filePath = classLoader.getResource("gameBoards/wrongBoard");
    FileBoard board = parser.parseGameFile(filePath.getPath());
    assertEquals(new BitboardRepresentation(), board.board());
  }

  @Test
  public void parseUnknownFile() {
    FileBoard board = parser.parseGameFile("Unknow/file/path");
    assertEquals(new BitboardRepresentation(), board.board());
  }

  @Test
  public void parseWrongPlayerFile() {
    URL filePath = classLoader.getResource("gameBoards/unknownPlayerGame");
    FileBoard board = parser.parseGameFile(filePath.getPath());
    assertEquals(new BitboardRepresentation(), board.board());
  }

  @Test
  public void parseWrongFormattedFile() {
    URL filePath = classLoader.getResource("gameBoards/wrongFormattedGame");
    FileBoard board = parser.parseGameFile(filePath.getPath());
    assertEquals(new BitboardRepresentation(), board.board());
  }

  @Test
  public void parseUnknownPieceFile() {
    URL filePath = classLoader.getResource("gameBoards/unknownPieceGame");
    FileBoard board = parser.parseGameFile(filePath.getPath());
    assertEquals(new BitboardRepresentation(), board.board());
  }
}
