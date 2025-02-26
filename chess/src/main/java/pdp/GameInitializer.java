package pdp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import pdp.exceptions.IllegalMoveException;
import pdp.exceptions.InvalidPositionException;
import pdp.exceptions.MoveParsingException;
import pdp.model.Game;
import pdp.model.ai.AlgorithmType;
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.model.parsers.BoardFileParser;
import pdp.model.parsers.FileBoard;
import pdp.utils.MoveHistoryParser;
import pdp.utils.OptionType;
import pdp.utils.Timer;

public abstract class GameInitializer {
  // TODO Internationalization
  /**
   * Initialize the game with the given options.
   *
   * @param options The options to use to initialize the game.
   * @return A new Game instance.
   */
  public static Game initialize(HashMap<OptionType, String> options) {

    Timer timer = null;
    if (options.containsKey(OptionType.BLITZ)) {
      if (options.containsKey(OptionType.TIME)) {
        timer = new Timer(Long.parseLong(options.get(OptionType.TIME)) * 60 * 1000);
      } else {
        timer = new Timer((long) 30 * 60 * 1000);
      }
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
      if (options.containsKey(OptionType.AI_MODE)) {
        try {
          solver.setAlgorithm(AlgorithmType.valueOf(options.get(OptionType.AI_MODE)));
        } catch (Exception e) {
          System.err.println("Unknown AI mode option: " + options.get(OptionType.AI_MODE));
          System.err.println("Defaulting to ALPHABETA.");
        }
      }
      if (options.containsKey(OptionType.AI_HEURISTIC)) {
        try {
          HeuristicType heuristicType = HeuristicType.valueOf(options.get(OptionType.AI_HEURISTIC));
          solver.setHeuristic(heuristicType);
        } catch (IllegalArgumentException e) {
          System.err.println("Unknown Heuristic: " + options.get(OptionType.AI_HEURISTIC));
          System.err.println("Defaulting to Heuristic STANDARD");
        }
      }

      if (options.containsKey(OptionType.AI_DEPTH)) {
        try {
          int depth = Integer.parseInt(options.get(OptionType.AI_DEPTH));
          solver.setDepth(depth);
        } catch (Exception e) {
          System.err.println("Not an integer for the depth of AI");
          System.err.println("Defaulting to depth " + solver.getDepth());
        }
      }

      if (options.containsKey(OptionType.AI_TIME)) {
        try {
          long time = Long.parseLong(options.get(OptionType.AI_TIME));
          solver.setTime(time);
        } catch (Exception e) {
          System.err.println("Not a long for the time of AI (in seconds)");
          System.err.println("Defaulting to a 5 seconds timer");
        }
      }
      if (options.containsKey(OptionType.BLITZ)) {
        // If blitz, take the minimum between the blitz time and AI time
        long time = Long.min(solver.getTimer().getTimeRemaining(), timer.getTimeRemaining() - 100);
        solver.setTime(time / 1000);
      }
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
          model = Game.initialize(isWhiteAI, isBlackAI, solver, timer, board, options);
        } else {

          List<Move> moves = new ArrayList<>();

          for (String move : moveStrings) {
            moves.add(Move.fromString(move.replace("x", "-")));
          }

          model = Game.fromHistory(moves, isWhiteAI, isBlackAI, solver, timer, options);
        }

      } catch (IOException
          | IllegalMoveException
          | InvalidPositionException
          | MoveParsingException e) {
        System.err.println(
            "Error while parsing file: " + e.getMessage()); // TODO use Internationalization
        System.err.println("Using the default game start");
        model = Game.initialize(isWhiteAI, isBlackAI, solver, timer, options);
      }
    } else {
      model = Game.initialize(isWhiteAI, isBlackAI, solver, timer, options);
    }

    return model;
  }
}
