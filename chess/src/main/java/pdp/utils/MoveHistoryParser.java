package pdp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for the history of loaded files. Translates the moves from the history into the string
 * format of the Move class.
 */
public class MoveHistoryParser {

  /**
   * Reads and parses a history file containing chess moves in format "a1-a2", "a1xb2", "Qe2xe4+",
   * etc.
   *
   * @param inputStream The input stream of the history file.
   * @return A list of moves extracted from the file.
   * @throws IOException If an I/O error occurs while reading the input stream.
   */
  public static List<String> parseHistoryFile(InputStream inputStream) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    String line;

    List<String> movesList = new ArrayList<>();
    while ((line = reader.readLine()) != null) {
      movesList.addAll(parseMoves(line));
    }

    return movesList;
  }

  /**
   * Parses a line of moves and returns the list of moves in the format "a1-a2". If the line starts
   * with "#" (comment), the line is ignored.
   *
   * @param line a line of moves in the format "a1-a2", "a1xb2", "Qe2xe4+", etc
   * @return a list of moves in the format "a1-a2"
   */
  private static List<String> parseMoves(String line) {

    List<String> moves = new ArrayList<>();

    line = line.strip();
    if (line.startsWith("#")) {
      return moves;
    }

    String regex =
        "\\b(?:[KQRBN])?((?:O-O(?:-O)?|[a-h][1-8](?:[-x])[a-h][1-8](?:=[QRBN])?))(?:[+#])?";

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(line);

    while (matcher.find()) {
      String move = matcher.group(1);
      move = move.replace('x', '-');
      moves.add(move);
    }

    return moves;
  }
}
