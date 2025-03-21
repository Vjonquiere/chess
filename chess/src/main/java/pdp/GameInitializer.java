package pdp;

import static pdp.utils.Logging.debug;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

/** Initializes the game (model) with either the options given in the method initialize. */
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

    debug(LOGGER, "Initializing game with options: " + options);

    Timer timer = null;
    Integer blitzTime = 30 * 60;
    if (options.containsKey(OptionType.BLITZ)) {
      if (!options.containsKey(OptionType.TIME)) {
        options.put(OptionType.TIME, "30");
      }
      int time;
      try {
        time = Integer.parseInt(options.get(OptionType.TIME));
      } catch (Exception e) {
        System.err.println("Not an int for the blitz time");
        System.err.println("Defaulting to a 30 minutes timer");
        time = 30;
      }

      blitzTime = time * 60;
      timer = new Timer(blitzTime * 1000L);
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
          HeuristicType heuristicType =
              HeuristicType.valueOf(options.get(OptionType.AI_HEURISTIC_W));
          solverWhite.setHeuristic(heuristicType);
        } catch (IllegalArgumentException e) {
          System.err.println("Unknown Heuristic: " + options.get(OptionType.AI_HEURISTIC_W));
          System.err.println("Defaulting to Heuristic STANDARD");
          solverWhite.setHeuristic(HeuristicType.STANDARD);
        }
      }

      if (options.containsKey(OptionType.AI_HEURISTIC_B)) {
        try {
          HeuristicType heuristicType =
              HeuristicType.valueOf(options.get(OptionType.AI_HEURISTIC_B));
          solverBlack.setHeuristic(heuristicType);
        } catch (IllegalArgumentException e) {
          System.err.println("Unknown Heuristic: " + options.get(OptionType.AI_HEURISTIC_B));
          System.err.println("Defaulting to Heuristic STANDARD");
          solverBlack.setHeuristic(HeuristicType.STANDARD);
        }
      }

      if (options.containsKey(OptionType.AI_ENDGAME_W)) {
        System.out.println(options);
        try {
          HeuristicType heuristicType = HeuristicType.valueOf(options.get(OptionType.AI_ENDGAME_W));
          solverWhite.setEndgameHeuristic(heuristicType);
        } catch (IllegalArgumentException e) {
          System.err.println("Unknown Heuristic: " + options.get(OptionType.AI_ENDGAME_W));
          System.err.println("Defaulting to Endgame Heuristic STANDARD");
          solverWhite.setEndgameHeuristic(HeuristicType.ENDGAME);
        }
      }

      if (options.containsKey(OptionType.AI_ENDGAME_B)) {
        System.out.println(options);
        try {
          HeuristicType heuristicType = HeuristicType.valueOf(options.get(OptionType.AI_ENDGAME_B));
          solverBlack.setEndgameHeuristic(heuristicType);
        } catch (IllegalArgumentException e) {
          System.err.println("Unknown Heuristic: " + options.get(OptionType.AI_ENDGAME_B));
          System.err.println("Defaulting to Endgame Heuristic STANDARD");
          solverBlack.setEndgameHeuristic(HeuristicType.ENDGAME);
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
          int time = Integer.parseInt(options.get(OptionType.AI_TIME));
          if (options.containsKey(OptionType.BLITZ)) {
            time = Integer.min(time, blitzTime);
          }
          solverWhite.setTime(time);
          solverBlack.setTime(time);
        } catch (Exception e) {
          System.err.println("Not an int for the time of AI (in seconds)");
          System.err.println("Defaulting to a 5 seconds timer");
          solverWhite.setTime(5);
          solverBlack.setTime(5);
        }
      } else if (options.containsKey(OptionType.BLITZ)) {
        solverWhite.setTime(blitzTime);
        solverBlack.setTime(blitzTime);
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
          model.setLoadedFromFile();
          model.setLoadingFileHasHistory(false);
        } else {

          List<Move> moves = new ArrayList<>();

          boolean isWhite = true;
          for (String move : moveStrings) {
            moves.add(Move.fromString(move.replace("x", "-"), isWhite));
            isWhite = !isWhite;
          }

          model =
              Game.fromHistory(
                  moves, isWhiteAi, isBlackAi, solverWhite, solverBlack, timer, options);
          model.setLoadedFromFile();
          model.setLoadingFileHasHistory(true);
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
