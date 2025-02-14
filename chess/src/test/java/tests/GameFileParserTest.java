package tests;

import java.net.URL;
import org.junit.jupiter.api.Test;
import pdp.model.parsers.GameFileParser;

public class GameFileParserTest {

  @Test
  public void parseGameFile() {
    GameFileParser parser = new GameFileParser();
    ClassLoader classLoader = getClass().getClassLoader();
    URL filePath = classLoader.getResource("gameFile");
    try {
      System.out.println(parser.parseGameFile(filePath.getPath()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
