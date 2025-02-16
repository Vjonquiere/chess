package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.net.URL;
import org.junit.jupiter.api.Test;
import pdp.model.board.Bitboard;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.BoardRepresentation;
import pdp.model.parsers.GameFileParser;

public class GameFileParserTest {

  @Test
  public void parseDefaultGameFile() {
    GameFileParser parser = new GameFileParser();
    ClassLoader classLoader = getClass().getClassLoader();
    URL filePath = classLoader.getResource("gameBoards/defaultGame");
    BoardRepresentation board = parser.parseGameFile(filePath.getPath());
    assertEquals(new BitboardRepresentation(), board);
  }

  @Test
  public void parseEmptyGameFile() {
    GameFileParser parser = new GameFileParser();
    ClassLoader classLoader = getClass().getClassLoader();
    URL filePath = classLoader.getResource("gameBoards/emptyGame");
    BoardRepresentation board = parser.parseGameFile(filePath.getPath());
    assertNotEquals(new BitboardRepresentation(), board);
    assertEquals(
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
  }

  @Test
  public void parseUnknownFile() {
    GameFileParser parser = new GameFileParser();
    BoardRepresentation board = parser.parseGameFile("Unknow/file/path");
    assertEquals(new BitboardRepresentation(), board);
  }

  @Test
  public void parseWrongPlayerFile() {
    GameFileParser parser = new GameFileParser();
    ClassLoader classLoader = getClass().getClassLoader();
    URL filePath = classLoader.getResource("gameBoards/unknownPlayerGame");
    BoardRepresentation board = parser.parseGameFile(filePath.getPath());
    assertEquals(new BitboardRepresentation(), board);
  }

  @Test
  public void parseWrongFormattedFile() {
    GameFileParser parser = new GameFileParser();
    ClassLoader classLoader = getClass().getClassLoader();
    URL filePath = classLoader.getResource("gameBoards/wrongFormattedGame");
    BoardRepresentation board = parser.parseGameFile(filePath.getPath());
    assertEquals(new BitboardRepresentation(), board);
  }

  @Test
  public void parseUnknownPieceFile() {
    GameFileParser parser = new GameFileParser();
    ClassLoader classLoader = getClass().getClassLoader();
    URL filePath = classLoader.getResource("gameBoards/unknownPieceGame");
    BoardRepresentation board = parser.parseGameFile(filePath.getPath());
    assertEquals(new BitboardRepresentation(), board);
  }
}
