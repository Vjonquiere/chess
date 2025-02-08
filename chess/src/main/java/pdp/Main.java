package pdp;

import java.util.HashMap;
import java.util.logging.Logger;
import pdp.controller.BagOfCommands;
import pdp.controller.GameController;
import pdp.model.Game;
import pdp.model.Timer;
import pdp.model.ai.Solver;
import pdp.utils.CLIOptions;
import pdp.utils.Logging;
import pdp.utils.OptionType;
import pdp.view.CLIView;
import pdp.view.GameView;
import pdp.view.View;

public class Main {
  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {
    HashMap<OptionType, String> options = CLIOptions.parseOptions(args, Runtime.getRuntime());
    Logging.configureLogging(LOGGER);
    System.out.println(options.toString());

    if (options.containsKey(OptionType.CONTEST)) {
      throw new UnsupportedOperationException("Contest mode not implemented");
    }

    View view;
    if (options.containsKey(OptionType.GUI)) {
      view = new GameView();
    } else {
      view = new CLIView();
    }

    Timer timer = null;
    if (options.containsKey(OptionType.BLITZ)) {
      if (options.containsKey(OptionType.TIME)) {
        timer = new Timer(Integer.parseInt(options.get(OptionType.TIME)));
      } else {
        timer = new Timer(30 * 60);
      }
      System.err.println("Option time not implemented, defaulting to a game without time limit");
    }

    boolean isWhiteAI = false;
    boolean isBlackAI = false;
    Solver solver = null;

    if (options.containsKey(OptionType.AI)) {

      switch (options.get(OptionType.AI)) {
        case "W":
          isWhiteAI = true;
          break;
        case "B":
          isBlackAI = false;
          break;
        case "A":
          isWhiteAI = true;
          isBlackAI = true;
          break;
        default:
          System.err.println("Unknown AI option: " + options.get(OptionType.AI));
          System.err.println("Using default AI mode: W");
          isWhiteAI = true;
          break;
      }

      solver = new Solver();
      if (options.containsKey(OptionType.AI_MODE)) {
        // switch to set solver mode
      } else {
        // Set to default (ALPHABETA)
      }

      if (options.containsKey(OptionType.AI_HEURISTIC)) {
        // switch to set heuristic
      } else {
        // Set to default
      }

      if (options.containsKey(OptionType.AI_DEPTH)) {
        // set depth
      } else {
        // Set to default
      }

      if (options.containsKey(OptionType.AI_TIME)) {
        // set time
      } else {
        // Set to default
      }

      throw new UnsupportedOperationException("AI mode not implemented");
    }

    Game model = Game.initialize(isWhiteAI, isBlackAI, solver, timer);
    BagOfCommands bagOfCommands = BagOfCommands.getInstance();
    GameController controller = new GameController(model, view, bagOfCommands);
    Thread viewThread = view.start();

    try {
      viewThread.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
