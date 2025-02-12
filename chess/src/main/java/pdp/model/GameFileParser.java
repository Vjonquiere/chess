package pdp.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;
import javax.annotation.processing.FilerException;

public class GameFileParser {

  private Pattern commentPattern = Pattern.compile("#.*$"); // Pattern.compile("^\\s*#.*$");
  private Pattern player = Pattern.compile("^[WB]$");
  private Pattern boardLine = Pattern.compile("^([RNBQKPrnbqkp]\\s){7}([RNBQKPrnbqkp])$");

  public BitboardRepresentation parseGameFile(String fileName)
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
  }
}
