package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.utils.MoveHistoryParser;

class MoveHistoryParserTest {

  private Method parseMovesMethod;

  @BeforeEach
  void setUp() throws Exception {
    parseMovesMethod = MoveHistoryParser.class.getDeclaredMethod("parseMoves", String.class);
    parseMovesMethod.setAccessible(true);
  }

  @Test
  void testParseHistoryFile() throws Exception {
    String input =
        """
                # This is a comment e2-e4
                1. W e2-e4 B e7-e5
                2. W Nb1xc3 B O-O
                3. W Qe2xe4+ B e7xe8=Q
                """;

    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    List<String> moves = MoveHistoryParser.parseHistoryFile(inputStream);

    assertNotNull(moves);
    assertEquals(6, moves.size());
    assertEquals("e2-e4", moves.get(0));
    assertEquals("e7-e5", moves.get(1));
    assertEquals("b1-c3", moves.get(2));
    assertEquals("O-O", moves.get(3));
    assertEquals("e2-e4", moves.get(4));
    assertEquals("e7-e8=Q", moves.get(5));
  }

  @Test
  void testParseComments() throws Exception {
    String line = "# This is a comment";
    List<String> moves = (List<String>) parseMovesMethod.invoke(null, line);
    assertTrue(moves.isEmpty());
  }

  @Test
  void testParseCastling() throws Exception {
    String line = "O-O O-O-O";
    List<String> moves = (List<String>) parseMovesMethod.invoke(null, line);
    assertEquals(List.of("O-O", "O-O-O"), moves);
  }

  @Test
  void testParseCaptures() throws Exception {
    String line = "e2xe4 Nb1xc3";
    List<String> moves = (List<String>) parseMovesMethod.invoke(null, line);
    assertEquals(List.of("e2-e4", "b1-c3"), moves);
  }

  @Test
  void testParseMovesWithPromotions() throws Exception {
    String line = "e7xe8=Q e7-e8=R";
    List<String> moves = (List<String>) parseMovesMethod.invoke(null, line);
    assertEquals(List.of("e7-e8=Q", "e7-e8=R"), moves);
  }

  @Test
  void testParseMovesWithCheckAndMate() throws Exception {
    String line = "Qe2xe4+ Qh5xe8#";
    List<String> moves = (List<String>) parseMovesMethod.invoke(null, line);
    assertEquals(List.of("e2-e4", "h5-e8"), moves);
  }

  @Test
  void testEmptyFile() throws Exception {
    String input = "";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    List<String> moves = MoveHistoryParser.parseHistoryFile(inputStream);
    assertTrue(moves.isEmpty());
  }
}
