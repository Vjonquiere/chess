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
import pdp.model.piece.Color;
import pdp.utils.Logging;

public class BoardFileParser {
  private static final Logger LOGGER = Logger.getLogger(BoardFileParser.class.getName());

  public BoardFileParser() {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Get the string contained in the given file
   *
   * @param path The path to the file
   * @return The content of the given file has a String
   * @throws FileNotFoundException
   */
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

  /**
   * Generate the board and player turn from the given file. If something went wrong at any step,
   * the application exit with EXIT_FAILURE status. To be load, the board need to have a king by
   * side, and no one can be checkmate
   *
   * @param fileName The file path
   * @return The corresponding board and current player
   */
  public FileBoard parseGameFile(String fileName, Runtime runtime) {
    String content;
    try {
      content = readFile(fileName);
    } catch (FileNotFoundException e) {
      System.out.println("File not found, loading default game");
      runtime.exit(1);
      return new FileBoard(new BitboardRepresentation(), true, null);
    }
    content = content.split("1\\.")[0].trim(); // Removing history if present
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
      FileBoard result = listener.getResult();
      if (result.board().getKing(true).size() != 1
          || result.board().getKing(false).size() != 1
          || result.board().isCheckMate(Color.WHITE)
          || result.board().isCheckMate(Color.BLACK)) {
        throw new RuntimeException(
            "Board do not satisfy load requirements (no check mate and one king by player)");
      }
      return result;
    } catch (Exception e) {
      System.out.println("Failed to build board: " + e.getMessage());
      runtime.exit(1);
      return new FileBoard(new BitboardRepresentation(), true, null);
    }
  }
}
