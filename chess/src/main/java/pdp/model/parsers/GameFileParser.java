package pdp.model.parsers;

import static pdp.utils.Logging.DEBUG;
import static pdp.utils.Logging.VERBOSE;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Logger;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import pdp.BoardLoaderLexer;
import pdp.BoardLoaderParser;
import pdp.model.board.BitboardRepresentation;
import pdp.utils.Logging;

public class GameFileParser {
  private static final Logger LOGGER = Logger.getLogger(GameFileParser.class.getName());

  public GameFileParser() {
    Logging.configureLogging(LOGGER);
  }

  public String readFile(String path) throws FileNotFoundException {
    DEBUG(LOGGER, "Loading file: " + path);
    StringBuilder fileContent = new StringBuilder();
    File myObj = new File(path);
    Scanner myReader = new Scanner(myObj);
    while (myReader.hasNextLine()) {
      fileContent.append(myReader.nextLine());
      fileContent.append("\n");
    }
    myReader.close();
    VERBOSE(LOGGER, "File content: " + fileContent);
    return fileContent.toString();
  }

  public BitboardRepresentation parseGameFile(String fileName) {
    String content;
    try {
      content = readFile(fileName);
    } catch (FileNotFoundException e) {
      System.out.println("File not found, loading default game");
      return new BitboardRepresentation();
    }
    try {
      DEBUG(LOGGER, "Converting file to charStream...");
      CharStream charStream = CharStreams.fromString(content);
      DEBUG(LOGGER, "Lexing...");
      BoardLoaderLexer lexer = new BoardLoaderLexer(charStream);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      DEBUG(LOGGER, "Parsing...");
      BoardLoaderParser parser = new BoardLoaderParser(tokens);
      parser.setErrorHandler(new BailErrorStrategy()); // force parser to throw error
      ParseTree tree = parser.board();
      DEBUG(LOGGER, "Building board...");
      ParseTreeWalker walker = new ParseTreeWalker();
      BoardLoaderListener listener = new BoardLoaderListener();
      walker.walk(listener, tree);
      DEBUG(LOGGER, "Board built successfully");
      return listener.getResult();
    } catch (Exception e) {
      System.out.println("Failed to parse file: " + e.getMessage());
      return new BitboardRepresentation();
    }
  }
}
