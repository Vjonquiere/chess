package pdp;

import java.util.HashMap;
import pdp.controller.BagOfCommands;
import pdp.controller.GameController;
import pdp.model.Game;
import pdp.model.Timer;
import pdp.model.ai.Solver;
import pdp.utils.OptionType;
import pdp.view.CLIView;
import pdp.view.GameView;
import pdp.view.View;

public abstract class GameInitializer {
  public static GameController initialize(HashMap<OptionType, String> options) {

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
      timer = null;
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
          isBlackAI = true;
          break;
        case "A":
          isWhiteAI = true;
          isBlackAI = true;
          break;
        default:
          System.err.println("Unknown AI option: " + options.get(OptionType.AI));
          System.err.println("Defaulting to AI playing White");
          isWhiteAI = true;
          break;
      }

      solver = new Solver();
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
    return new GameController(model, view, bagOfCommands);
  }
}
