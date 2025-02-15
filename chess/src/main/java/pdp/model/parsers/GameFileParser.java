package pdp.model.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;
import javax.annotation.processing.FilerException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import pdp.BoardLoaderLexer;
import pdp.BoardLoaderParser;
import pdp.model.board.BitboardRepresentation;

public class GameFileParser {

  private Pattern commentPattern = Pattern.compile("#.*$"); // Pattern.compile("^\\s*#.*$");
  private Pattern player = Pattern.compile("^[WB]$");
  private Pattern boardLine = Pattern.compile("^([RNBQKPrnbqkp]\\s){7}([RNBQKPrnbqkp])$");

  /*public BitboardRepresentation parseGameFile(String fileName)
      throws FileNotFoundException, FilerException {
    System.out.println("loading file " + fileName);
    File file = new File(fileName);
    System.out.println("file loaded");
    Scanner sc = new Scanner(file);
    System.out.println("reading file");
    if (!sc.hasNext(player)) throw new FilerException("Can't find player");
    String currentPlayer = sc.next(player);
    for (int i = 0; i < 8; i++) {
      if (!sc.hasNext(boardLine)) {
        throw new FilerException("Uknown file fomat");
      }
      sc.next(boardLine);
    }
    sc.next(boardLine);
    return new BitboardRepresentation();
  }*/
  public String readFile(String path) {
    StringBuilder fileContent = new StringBuilder();
    try {
      File myObj = new File(path);
      Scanner myReader = new Scanner(myObj);
      while (myReader.hasNextLine()) {
        fileContent.append(myReader.nextLine());
        fileContent.append("\n");
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    return fileContent.toString();
  }

  public BitboardRepresentation parseGameFile(String fileName)
      throws FileNotFoundException, FilerException {
    System.out.println("loading file " + fileName);
    String content = readFile(fileName);
    System.out.println("file loaded");
    System.out.println(content);
    CharStream charStream = CharStreams.fromString(content);
    BoardLoaderLexer lexer = new BoardLoaderLexer(charStream);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    BoardLoaderParser parser = new BoardLoaderParser(tokens);
    ParseTree tree = parser.board();
    System.out.println("parsing tree " + tree.toStringTree(parser));
    ParseTreeWalker walker = new ParseTreeWalker();
    BoardLoaderListener listener = new BoardLoaderListener();
    walker.walk(listener, tree);
    return listener.getResult();
  }
}
