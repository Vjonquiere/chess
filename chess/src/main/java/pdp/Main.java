package pdp;

import java.util.HashMap;
import java.util.logging.Logger;
import pdp.controller.GameController;
import pdp.model.parsers.BoardFileParser;
import pdp.model.parsers.FileBoard;
import pdp.utils.CLIOptions;
import pdp.utils.Logging;
import pdp.utils.OptionType;
import pdp.utils.TextGetter;

public class Main {
  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {
    HashMap<OptionType, String> options = CLIOptions.parseOptions(args, Runtime.getRuntime());
    Logging.configureLogging(LOGGER);
    System.out.println(TextGetter.getText("title"));
    System.out.println("options: " + options.toString());

    FileBoard board = null;
    if (args[args.length - 1].charAt(0) != '-') {
      BoardFileParser parser = new BoardFileParser();
      board = parser.parseGameFile(args[args.length - 1]);
    }

    if (options.containsKey(OptionType.CONTEST)) {
      throw new UnsupportedOperationException("Contest mode not implemented");
    }

    GameController controller = GameInitializer.initialize(options, board);
    Thread viewThread = controller.getView().start();

    try {
      viewThread.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
