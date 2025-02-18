package pdp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import pdp.controller.BagOfCommands;
import pdp.controller.GameController;
import pdp.exceptions.IllegalMoveException;
import pdp.exceptions.InvalidPositionException;
import pdp.exceptions.MoveParsingException;
import pdp.model.Game;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.model.parsers.BoardFileParser;
import pdp.model.parsers.FileBoard;
import pdp.utils.MoveHistoryParser;
import pdp.utils.OptionType;
import pdp.utils.Timer;
import pdp.view.CLIView;
import pdp.view.GameView;
import pdp.view.View;

public abstract class GameInitializer {
  // TODO Internationalization
  /**
   * Initialize the game with the given options.
   *
   * @param options The options to use to initialize the game.
   * @return A new GameController instance.
   */
  public static GameController initialize(HashMap<OptionType, String> options) {

    Timer timer = null;
    if (options.containsKey(OptionType.BLITZ)) {
      if (options.containsKey(OptionType.TIME)) {
        timer = new Timer(Long.parseLong(options.get(OptionType.TIME)) * 60 * 1000);
      } else {
        timer = new Timer((long) 30 * 60 * 1000);
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

    Game model = null;

    if (options.containsKey(OptionType.LOAD)) {
      InputStream inputStream = null;
      try {
        inputStream = new FileInputStream(options.get(OptionType.LOAD));

        List<String> moveStrings = MoveHistoryParser.parseHistoryFile(inputStream);
        if (moveStrings.isEmpty()) {
          BoardFileParser parser = new BoardFileParser();
          FileBoard board =
              parser.parseGameFile(options.get(OptionType.LOAD), Runtime.getRuntime());
          model = Game.initialize(isWhiteAI, isBlackAI, solver, timer, board);
        } else {

          List<Move> moves = new ArrayList<>();

          for (String move : moveStrings) {
            moves.add(Move.fromString(move.replace("x", "-")));
          }

          model = Game.fromHistory(moves, isWhiteAI, isBlackAI, solver);
        }

      } catch (IOException
          | IllegalMoveException
          | InvalidPositionException
          | MoveParsingException e) {
        System.err.println(
            "Error while parsing file: " + e.getMessage()); // TODO use Internationalization
        System.err.println("Using the default game start");
        model = Game.initialize(isWhiteAI, isBlackAI, solver, timer);
      }
    } else {
      model = Game.initialize(isWhiteAI, isBlackAI, solver, timer);
    }

    View view;
    if (options.containsKey(OptionType.GUI)) {
      view = new GameView();
    } else {
      view = new CLIView();
    }
    BagOfCommands bagOfCommands = BagOfCommands.getInstance();
    return new GameController(model, view, bagOfCommands);
  }
}
