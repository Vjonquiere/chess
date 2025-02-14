package pdp.utils;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class MoveHistoryParser {

  public static List<String> parseHistoryFile(InputStream inputStream) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    String line;

    List<String> movesList = new ArrayList<>();
    while ((line = reader.readLine()) != null) {
      movesList.addAll(parseMoves(line));
    }

    return movesList;
  }

  private static List<String> parseMoves(String line) {

    List<String> moves = new ArrayList<>();

    line = line.strip();
    if (line.startsWith("#")) {
      return moves;
    }

    String regex = "\\b[A-Z]?([a-h][1-8][-x][a-h][1-8]\\b)[+#]?";

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(line);

    while (matcher.find()) {
      String move = matcher.group(1);
      move = move.replace('x', '-');
      moves.add(move);
    }

    System.out.println(moves);
    return moves;
  }
}
