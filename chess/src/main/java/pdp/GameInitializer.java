package pdp;

import static pdp.utils.Logging.DEBUG;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import pdp.exceptions.IllegalMoveException;
import pdp.exceptions.InvalidPositionException;
import pdp.exceptions.MoveParsingException;
import pdp.model.Game;
import pdp.model.ai.AlgorithmType;
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;
import pdp.model.ai.algorithms.MonteCarloTreeSearch;
import pdp.model.board.Move;
import pdp.model.parsers.BoardFileParser;
import pdp.model.parsers.FileBoard;
import pdp.utils.CommandLineOptions;
import pdp.utils.MoveHistoryParser;
import pdp.utils.OptionType;
import pdp.utils.Timer;

public abstract class GameInitializer {

  private static final Logger LOGGER = Logger.getLogger(CommandLineOptions.class.getName());

  // TODO Internationalization
  /**
   * Initialize the game with the given options.
   *
   * @param options The options to use to initialize the game.
   * @return A new Game instance.
   */
  public static Game initialize(HashMap<OptionType, String> options) {

    CommandLineOptions.validateAiOptions(options);

    DEBUG(LOGGER, "Initializing game with options: " + options);

    Timer timer = null;
    if (options.containsKey(OptionType.BLITZ)) {
      if (options.containsKey(OptionType.TIME)) {
        timer = new Timer(Long.parseLong(options.get(OptionType.TIME)) * 60 * 1000);
      } else {
        timer = new Timer((long) 30 * 60 * 1000);
      }
    }

    boolean isWhiteAi = false;
    boolean isBlackAi = false;
    Solver solverWhite = null;
    Solver solverBlack = null;

    if (options.containsKey(OptionType.AI)) {
      switch (options.get(OptionType.AI)) {
        case "W":
          isWhiteAi = true;
          break;
        case "B":
          isBlackAi = true;
          break;
        case "A":
          isWhiteAi = true;
          isBlackAi = true;
          break;
        default:
          System.err.println("Unknown AI option: " + options.get(OptionType.AI));
          System.err.println("Defaulting to AI playing White");
          isWhiteAi = true;
          break;
      }

      solverWhite = new Solver();
      solverBlack = new Solver();
      if (options.containsKey(OptionType.AI_MODE_W)) {
        try {
          AlgorithmType algorithmType = AlgorithmType.valueOf(options.get(OptionType.AI_MODE_W));
          solverWhite.setAlgorithm(algorithmType);
        } catch (Exception e) {
          System.err.println("Unknown AI mode option: " + options.get(OptionType.AI_MODE));
          System.err.println("Defaulting to ALPHABETA.");
          solverWhite.setAlgorithm(AlgorithmType.ALPHA_BETA);
        }
      }

      if (options.containsKey(OptionType.AI_MODE_B)) {
        try {
          AlgorithmType algorithmType = AlgorithmType.valueOf(options.get(OptionType.AI_MODE_B));
          solverBlack.setAlgorithm(algorithmType);
        } catch (Exception e) {
          System.err.println("Unknown AI mode option: " + options.get(OptionType.AI_MODE_B));
          System.err.println("Defaulting to ALPHABETA.");
          solverBlack.setAlgorithm(AlgorithmType.ALPHA_BETA);
        }
      }

      if (options.containsKey(OptionType.AI_HEURISTIC_W)) {
        try {
          if (options.containsKey(OptionType.AI_WEIGHT_W)
              && HeuristicType.valueOf(options.get(OptionType.AI_HEURISTIC_W))
                  .equals(HeuristicType.STANDARD)) {
            String weight = options.get(OptionType.AI_WEIGHT_W);
            String[] weights = weight.split(",");
            ArrayList<Float> weightsFloats = new ArrayList<>();
            for (String w : weights) {
              weightsFloats.add(Float.parseFloat(w));
            }
            if (weightsFloats.size() != 7) {
              throw new ParseException("Invalid number of weights", 0);
            }
            solverWhite.setHeuristic(
                HeuristicType.valueOf(options.get(OptionType.AI_HEURISTIC_W)), weightsFloats);
          } else {
            solverWhite.setHeuristic(HeuristicType.valueOf(options.get(OptionType.AI_HEURISTIC_W)));
          }

        } catch (IllegalArgumentException e) {
          System.err.println("Unknown Heuristic: " + options.get(OptionType.AI_HEURISTIC_W));
          System.err.println("Defaulting to Heuristic STANDARD");
          solverWhite.setHeuristic(HeuristicType.STANDARD);
        } catch (ParseException e) {
          System.err.println(
              "Weights problem: " + options.get(OptionType.AI_WEIGHT_W) + " -> " + e.getMessage());
          System.err.println("Defaulting to Unweighted Heuristic STANDARD");
          solverWhite.setHeuristic(HeuristicType.STANDARD);
        }
      }

      if (options.containsKey(OptionType.AI_HEURISTIC_B)) {
        try {
          if (options.containsKey(OptionType.AI_WEIGHT_B)
              && HeuristicType.valueOf(options.get(OptionType.AI_HEURISTIC_B))
                  .equals(HeuristicType.STANDARD)) {
            String weight = options.get(OptionType.AI_WEIGHT_B);
            String[] weights = weight.split(",");
            ArrayList<Float> weightsFloats = new ArrayList<>();
            for (String w : weights) {
              weightsFloats.add(Float.parseFloat(w));
            }
            if (weightsFloats.size() != 7) {
              throw new ParseException("Invalid number of weights", 0);
            }
            solverBlack.setHeuristic(
                HeuristicType.valueOf(options.get(OptionType.AI_HEURISTIC_B)), weightsFloats);
          } else {
            solverBlack.setHeuristic(HeuristicType.valueOf(options.get(OptionType.AI_HEURISTIC_B)));
          }
        } catch (IllegalArgumentException e) {
          System.err.println("Unknown Heuristic: " + options.get(OptionType.AI_HEURISTIC_B));
          System.err.println("Defaulting to Heuristic STANDARD");
          solverBlack.setHeuristic(HeuristicType.STANDARD);
        } catch (ParseException e) {
          System.err.println(
              "Weights problem: " + options.get(OptionType.AI_WEIGHT_B) + " -> " + e.getMessage());
          System.err.println("Defaulting to Unweighted Heuristic STANDARD");
          solverBlack.setHeuristic(HeuristicType.STANDARD);
        }
      }

      if (options.containsKey(OptionType.AI_DEPTH_W)
          && !(solverWhite.getAlgorithm() instanceof MonteCarloTreeSearch)) {
        try {
          int depth = Integer.parseInt(options.get(OptionType.AI_DEPTH_W));
          solverWhite.setDepth(depth);
        } catch (Exception e) {
          System.err.println("Not an integer for the depth of AI");
          System.err.println("Defaulting to depth " + solverWhite.getDepth());
        }
      }

      if (options.containsKey(OptionType.AI_DEPTH_B)
          && !(solverBlack.getAlgorithm() instanceof MonteCarloTreeSearch)) {
        try {
          int depth = Integer.parseInt(options.get(OptionType.AI_DEPTH_B));
          solverBlack.setDepth(depth);
        } catch (Exception e) {
          System.err.println("Not an integer for the depth of AI");
          System.err.println("Defaulting to depth " + solverBlack.getDepth());
        }
      }

      if (options.containsKey(OptionType.AI_SIMULATION_W)
          && solverWhite.getAlgorithm() instanceof MonteCarloTreeSearch) {
        try {
          int simulations = Integer.parseInt(options.get(OptionType.AI_SIMULATION_W));
          solverWhite.setMonteCarloAlgorithm(simulations);
        } catch (Exception e) {
          System.err.println("Not an integer for the simulations of AI");
          System.err.println(
              "Defaulting to depth "
                  + ((MonteCarloTreeSearch) solverWhite.getAlgorithm()).getSimulationLimit());
        }
      }

      if (options.containsKey(OptionType.AI_SIMULATION_B)
          && solverBlack.getAlgorithm() instanceof MonteCarloTreeSearch) {
        try {
          int simulations = Integer.parseInt(options.get(OptionType.AI_SIMULATION_B));
          solverBlack.setMonteCarloAlgorithm(simulations);
        } catch (Exception e) {
          System.err.println("Not an integer for the simulations of AI");
          System.err.println(
              "Defaulting to simulations "
                  + ((MonteCarloTreeSearch) solverBlack.getAlgorithm()).getSimulationLimit());
        }
      }

      if (options.containsKey(OptionType.AI_TIME)) {
        try {
          long time = Long.parseLong(options.get(OptionType.AI_TIME));
          solverWhite.setTime(time);
          solverBlack.setTime(time);
        } catch (Exception e) {
          System.err.println("Not a long for the time of AI (in seconds)");
          System.err.println("Defaulting to a 5 seconds timer");
        }
      }
      if (options.containsKey(OptionType.BLITZ)) {
        // If blitz, take the minimum between the blitz time and AI time
        long time =
            Long.min(
                Long.min(
                    solverWhite.getTimer().getTimeRemaining(),
                    solverBlack.getTimer().getTimeRemaining()),
                timer.getTimeRemaining() - 100);
        solverWhite.setTime(time / 1000);
        solverBlack.setTime(time / 1000);
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
          model =
              Game.initialize(
                  isWhiteAi, isBlackAi, solverWhite, solverBlack, timer, board, options);
        } else {

          List<Move> moves = new ArrayList<>();

          for (String move : moveStrings) {
            moves.add(Move.fromString(move.replace("x", "-")));
          }

          model =
              Game.fromHistory(
                  moves, isWhiteAi, isBlackAi, solverWhite, solverBlack, timer, options);
        }

      } catch (IOException
          | IllegalMoveException
          | InvalidPositionException
          | MoveParsingException e) {
        System.err.println(
            "Error while parsing file: " + e.getMessage()); // TODO use Internationalization
        System.err.println("Using the default game start");
        model = Game.initialize(isWhiteAi, isBlackAi, solverWhite, solverBlack, timer, options);
      }
    } else {
      model = Game.initialize(isWhiteAi, isBlackAi, solverWhite, solverBlack, timer, options);
    }

    return model;
  }
}
