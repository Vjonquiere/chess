package pdp.model.parsers;

import static pdp.utils.Logging.debug;
import static pdp.utils.Logging.error;
import static pdp.utils.Logging.verbose;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Logger;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import pdp.BoardLoaderLexer;
import pdp.BoardLoaderParser;
import pdp.exceptions.ChessException;
import pdp.model.board.BitboardRepresentation;
import pdp.model.piece.Color;
import pdp.utils.Logging;

/** Parser that produce board objects from given file format (support FEN header). */
public class BoardFileParser {
  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(BoardFileParser.class.getName());

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Get the string contained in the given file.
   *
   * @param path The path to the file
   * @return The content of the given file has a String
   * @throws FileNotFoundException if the path is not valid
   */
  public String readFile(final String path) throws FileNotFoundException {
    debug(LOGGER, "Loading file: " + path);
    final StringBuilder fileContent = new StringBuilder();
    final File myObj = new File(path);
    final Scanner myReader = new Scanner(myObj);
    while (myReader.hasNextLine()) {
      fileContent.append(myReader.nextLine());
      fileContent.append('\n');
    }
    myReader.close();
    verbose(LOGGER, "File content: " + fileContent);
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
  public FileBoard parseGameFile(final String fileName, final Runtime runtime) {
    String content;
    try {
      content = readFile(fileName);
    } catch (FileNotFoundException e) {
      error("File not found, loading default game");
      runtime.exit(1);
      return new FileBoard(new BitboardRepresentation(), true, null);
    }
    content = content.split("\\d+\\.")[0].trim(); // Removing history if present
    BoardLoaderParser parser = null;
    try {
      debug(LOGGER, "Converting file to charStream...");
      final CharStream charStream = CharStreams.fromString(content);
      debug(LOGGER, "Lexing...");
      final BoardLoaderLexer lexer = new BoardLoaderLexer(charStream);
      final CommonTokenStream tokens = new CommonTokenStream(lexer);
      debug(LOGGER, "Parsing...");
      parser = new BoardLoaderParser(tokens);
      parser.setErrorHandler(new BailErrorStrategy()); // force parser to throw error
      final ParseTree tree = parser.board();
      debug(LOGGER, "Building board...");
      final ParseTreeWalker walker = new ParseTreeWalker();
      final BoardLoaderListener listener = new BoardLoaderListener();
      walker.walk(listener, tree);
      debug(LOGGER, "Board built successfully");
      final FileBoard result = listener.getResult();
      if (result.board().getKing(true).size() != 1
          || result.board().getKing(false).size() != 1
          || result.board().isCheckMate(Color.WHITE)
          || result.board().isCheckMate(Color.BLACK)) {
        throw new ChessException(
            "Board do not satisfy load requirements (no check mate and one king by player)");
      }
      return result;
    } catch (RuntimeException e) {
      if (e.getCause() instanceof InputMismatchException) {
        final Token offendingToken = parser.getCurrentToken();
        error(
            "Failed to parse board at line "
                + offendingToken.getLine()
                + ", position "
                + offendingToken.getCharPositionInLine()
                + ": unexpected token '"
                + offendingToken.getText()
                + "'");
      } else {
        error(e.getMessage());
      }
      runtime.exit(1);
      return new FileBoard(new BitboardRepresentation(), true, null);
    }
  }
}
