package pdp;

import static pdp.utils.Logging.debug;
import static pdp.utils.Logging.error;
import static pdp.utils.Logging.print;

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

/** Initializes the game (model) with either the options given in the method initialize. */
public abstract class GameInitializer {

  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(GameInitializer.class.getName());

  /**
   * Initialize the game with the given options.
   *
   * @param options The options to use to initialize the game.
   * @return A new Game instance.
   */
  public static Game initialize(final HashMap<OptionType, String> options) {

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
        error("Not an int for the blitz time");
        error("Defaulting to a 30 minutes timer");
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
          error("Unknown AI option: " + options.get(OptionType.AI));
          error("Defaulting to AI playing White");
          isWhiteAi = true;
          break;
      }

      solverWhite = new Solver();
      solverBlack = new Solver();
      if (options.containsKey(OptionType.AI_MODE_W)) {
        try {
          final AlgorithmType algorithmType =
              AlgorithmType.valueOf(options.get(OptionType.AI_MODE_W));
          solverWhite.setAlgorithm(algorithmType);
        } catch (Exception e) {
          error("Unknown AI mode option: " + options.get(OptionType.AI_MODE));
          error("Defaulting to ALPHABETA.");
          solverWhite.setAlgorithm(AlgorithmType.ALPHA_BETA);
        }
      }

      if (options.containsKey(OptionType.AI_MODE_B)) {
        try {
          final AlgorithmType algorithmType =
              AlgorithmType.valueOf(options.get(OptionType.AI_MODE_B));
          solverBlack.setAlgorithm(algorithmType);
        } catch (Exception e) {
          error("Unknown AI mode option: " + options.get(OptionType.AI_MODE_B));
          error("Defaulting to ALPHABETA.");
          solverBlack.setAlgorithm(AlgorithmType.ALPHA_BETA);
        }
      }

      if (options.containsKey(OptionType.AI_HEURISTIC_W)) {
        try {
          if (options.containsKey(OptionType.AI_WEIGHT_W)
              && HeuristicType.valueOf(options.get(OptionType.AI_HEURISTIC_W))
                  .equals(HeuristicType.STANDARD)) {
            final String weight = options.get(OptionType.AI_WEIGHT_W);
            final String[] weights = weight.split(",");
            final List<Float> weightsFloats = new ArrayList<>();
            for (final String w : weights) {
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
          error("Unknown Heuristic: " + options.get(OptionType.AI_HEURISTIC_W));
          error("Defaulting to Heuristic STANDARD");
          solverWhite.setHeuristic(HeuristicType.STANDARD);
        } catch (ParseException e) {
          error(
              "Weights problem: " + options.get(OptionType.AI_WEIGHT_W) + " -> " + e.getMessage());
          error("Defaulting to Unweighted Heuristic STANDARD");
          solverWhite.setHeuristic(HeuristicType.STANDARD);
        }
      }

      if (options.containsKey(OptionType.AI_HEURISTIC_B)) {
        try {
          if (options.containsKey(OptionType.AI_WEIGHT_B)
              && HeuristicType.valueOf(options.get(OptionType.AI_HEURISTIC_B))
                  .equals(HeuristicType.STANDARD)) {
            final String weight = options.get(OptionType.AI_WEIGHT_B);
            final String[] weights = weight.split(",");
            final List<Float> weightsFloats = new ArrayList<>();
            for (final String w : weights) {
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
          error("Unknown Heuristic: " + options.get(OptionType.AI_HEURISTIC_B));
          error("Defaulting to Heuristic STANDARD");
          solverBlack.setHeuristic(HeuristicType.STANDARD);
        } catch (ParseException e) {
          error(
              "Weights problem: " + options.get(OptionType.AI_WEIGHT_B) + " -> " + e.getMessage());
          error("Defaulting to Unweighted Heuristic STANDARD");
          solverBlack.setHeuristic(HeuristicType.STANDARD);
        }
      }

      if (options.containsKey(OptionType.AI_ENDGAME_W)) {
        print(String.valueOf(options));
        try {
          final HeuristicType heuristicType =
              HeuristicType.valueOf(options.get(OptionType.AI_ENDGAME_W));
          solverWhite.setEndgameHeuristic(heuristicType);
        } catch (IllegalArgumentException e) {
          error("Unknown Heuristic: " + options.get(OptionType.AI_ENDGAME_W));
          error("Defaulting to Endgame Heuristic STANDARD");
          solverWhite.setEndgameHeuristic(HeuristicType.ENDGAME);
        }
      }

      if (options.containsKey(OptionType.AI_ENDGAME_B)) {
        print(String.valueOf(options));
        try {
          final HeuristicType heuristicType =
              HeuristicType.valueOf(options.get(OptionType.AI_ENDGAME_B));
          solverBlack.setEndgameHeuristic(heuristicType);
        } catch (IllegalArgumentException e) {
          error("Unknown Heuristic: " + options.get(OptionType.AI_ENDGAME_B));
          error("Defaulting to Endgame Heuristic STANDARD");
          solverBlack.setEndgameHeuristic(HeuristicType.ENDGAME);
        }
      }

      if (options.containsKey(OptionType.AI_DEPTH_W)
          && !(solverWhite.getAlgorithm() instanceof MonteCarloTreeSearch)) {
        try {
          final int depth = Integer.parseInt(options.get(OptionType.AI_DEPTH_W));
          solverWhite.setDepth(depth);
        } catch (Exception e) {
          error("Not an integer for the depth of white AI");
          error("Defaulting to depth " + solverWhite.getDepth());
        }
      }

      if (options.containsKey(OptionType.AI_DEPTH_B)
          && !(solverBlack.getAlgorithm() instanceof MonteCarloTreeSearch)) {
        try {
          final int depth = Integer.parseInt(options.get(OptionType.AI_DEPTH_B));
          solverBlack.setDepth(depth);
        } catch (Exception e) {
          error("Not an integer for the depth of black AI");
          error("Defaulting to depth " + solverBlack.getDepth());
        }
      }

      if (options.containsKey(OptionType.AI_SIMULATION_W)
          && solverWhite.getAlgorithm() instanceof MonteCarloTreeSearch) {
        try {
          final int simulations = Integer.parseInt(options.get(OptionType.AI_SIMULATION_W));
          solverWhite.setMonteCarloAlgorithm(simulations);
        } catch (Exception e) {
          error("Not an integer for the simulations of AI");
          error(
              "Defaulting to depth "
                  + ((MonteCarloTreeSearch) solverWhite.getAlgorithm()).getSimulationLimit());
        }
      }

      if (options.containsKey(OptionType.AI_SIMULATION_B)
          && solverBlack.getAlgorithm() instanceof MonteCarloTreeSearch) {
        try {
          final int simulations = Integer.parseInt(options.get(OptionType.AI_SIMULATION_B));
          solverBlack.setMonteCarloAlgorithm(simulations);
        } catch (Exception e) {
          error("Not an integer for the simulations of AI");
          error(
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
          error("Not an int for the time of AI (in seconds)");
          error("Defaulting to a 5 seconds timer");
          solverWhite.setTime(5);
          solverBlack.setTime(5);
        }
      } else if (options.containsKey(OptionType.BLITZ)) {
        solverWhite.setTime(blitzTime);
        solverBlack.setTime(blitzTime);
      }
    }

    Game model = null;

    if (options.containsKey(OptionType.CONTEST)) {
      final String contestFile = options.get(OptionType.CONTEST);
      if (contestFile == null || contestFile.isEmpty()) {
        error("Error: --contest option requires a valid file path.");
      } else {
        try {
          final InputStream inputStream = new FileInputStream(contestFile);
          final List<String> moveStrings = MoveHistoryParser.parseHistoryFile(inputStream);
          final List<Move> moves = new ArrayList<>();
          for (final String move : moveStrings) {
            moves.add(Move.fromString(move.replace("x", "-")));
          }

          model =
              Game.fromHistory(
                  moves, isWhiteAi, isBlackAi, solverWhite, solverBlack, timer, options);

          // Init solver according to the current player
          final Solver solver;
          if (model.getGameState().isWhiteTurn()) {
            final Solver whiteSolver = new Solver();

            model.setWhiteSolver(whiteSolver);
            model.setWhiteAi(true);
            model.setBlackAi(false);

            processAiDepthContest(options, true, whiteSolver);
            processAiModeContest(options, true, whiteSolver);
            processAiEndGameHeuristicContest(options, true, whiteSolver);
            processAiHeuristicContest(options, true, whiteSolver);

            solver = whiteSolver;
          } else {
            final Solver blackSolver = new Solver();

            model.setBlackSolver(blackSolver);
            model.setBlackAi(true);
            model.setWhiteAi(false);

            processAiDepthContest(options, false, blackSolver);
            processAiModeContest(options, false, blackSolver);
            processAiEndGameHeuristicContest(options, false, blackSolver);
            processAiHeuristicContest(options, false, blackSolver);

            solver = blackSolver;
          }

          model.setLoadedFromFile();
          if (moves.isEmpty()) {
            model.setLoadingFileHasHistory(false);
          } else {
            model.setLoadingFileHasHistory(true);
          }
          model.setContestModeOnOrOff(true);
          debug(LOGGER, "Game was init with contest mode");

          solver.playAiMove(model);
          debug(LOGGER, "AI move played and recorded in: " + contestFile);
        } catch (IOException
            | IllegalMoveException
            | InvalidPositionException
            | MoveParsingException e) {
          error("Error loading contest file: " + e.getMessage());
          error("Starting a new game instead.");
          model = Game.initialize(isWhiteAi, isBlackAi, solverWhite, solverBlack, timer, options);
        }
      }
    }

    if (model == null) {
      if (options.containsKey(OptionType.LOAD)) {
        final InputStream inputStream;
        try {
          inputStream = new FileInputStream(options.get(OptionType.LOAD));

          final List<String> moveStrings = MoveHistoryParser.parseHistoryFile(inputStream);
          if (moveStrings.isEmpty()) {
            final BoardFileParser parser = new BoardFileParser();
            final FileBoard board =
                parser.parseGameFile(options.get(OptionType.LOAD), Runtime.getRuntime());
            model =
                Game.initialize(
                    isWhiteAi, isBlackAi, solverWhite, solverBlack, timer, board, options);
            model.setLoadedFromFile();
            model.setLoadingFileHasHistory(false);
            model.setContestModeOnOrOff(false);
          } else {

            final List<Move> moves = new ArrayList<>();

            boolean isWhite = true;
            for (final String move : moveStrings) {
              moves.add(Move.fromString(move.replace("x", "-"), isWhite));
              isWhite = !isWhite;
            }

            model =
                Game.fromHistory(
                    moves, isWhiteAi, isBlackAi, solverWhite, solverBlack, timer, options);
            model.setLoadedFromFile();
            model.setLoadingFileHasHistory(true);
            model.setContestModeOnOrOff(false);
          }

        } catch (IOException
            | IllegalMoveException
            | InvalidPositionException
            | MoveParsingException e) {
          error("Error while parsing file: " + e.getMessage()); // TODO use Internationalization
          error("Using the default game start");
          model = Game.initialize(isWhiteAi, isBlackAi, solverWhite, solverBlack, timer, options);
        }
      } else {
        model = Game.initialize(isWhiteAi, isBlackAi, solverWhite, solverBlack, timer, options);
      }
    }

    return model;
  }

  /**
   * Processes the AI depth setting for contest mode based on the current player's turn. Retrieves
   * the depth value from the options and applies it to the solver. Defaults to a depth of 4 if no
   * valid value is provided. Used for Contest mode.
   *
   * @param options The map containing AI configuration options.
   * @param whiteTurn Indicates whether it is White's turn.
   * @param solver The solver instance for the current player.
   */
  private static void processAiDepthContest(
      final HashMap<OptionType, String> options, final boolean whiteTurn, final Solver solver) {
    int depth = 0;
    if (whiteTurn) {
      if (options.containsKey(OptionType.AI_DEPTH_W)) {
        try {
          depth = Integer.parseInt(options.get(OptionType.AI_DEPTH_W));
        } catch (Exception e) {
          error("Not an integer for ai depth");
        }
      }
    } else {
      if (options.containsKey(OptionType.AI_DEPTH_B)) {
        try {
          depth = Integer.parseInt(options.get(OptionType.AI_DEPTH_B));
        } catch (Exception e) {
          error("Not an integer for ai depth");
        }
      }
    }

    if (depth != 0) {
      solver.setDepth(depth);
    } else {
      solver.setDepth(4);
    }
  }

  /**
   * Configures the AI mode for contest mode based on the current player's turn. Determines whether
   * the AI should use Minimax, Alpha-Beta, or Monte Carlo Tree Search (MCTS). If MCTS is selected,
   * retrieves the number of simulations from the options. Defaults to Alpha-Beta if no valid mode
   * is specified. Used for Contest mode.
   *
   * @param options The map containing AI configuration options.
   * @param whiteTurn Indicates whether it is White's turn.
   * @param solver The solver instance for the current player.
   */
  private static void processAiModeContest(
      final HashMap<OptionType, String> options, final boolean whiteTurn, final Solver solver) {
    AlgorithmType algorithmTypeContest = null;
    if (whiteTurn) {
      if (options.containsKey(OptionType.AI_MODE_W)) {
        try {
          algorithmTypeContest = AlgorithmType.valueOf(options.get(OptionType.AI_MODE_W));
        } catch (Exception e) {
          error("Unknown AI mode option: " + options.get(OptionType.AI_MODE));
          error("Defaulting to ALPHABETA.");
        }
      }
    } else {
      if (options.containsKey(OptionType.AI_MODE_B)) {
        try {
          algorithmTypeContest = AlgorithmType.valueOf(options.get(OptionType.AI_MODE_B));
        } catch (Exception e) {
          error("Unknown AI mode option: " + options.get(OptionType.AI_MODE_B));
          error("Defaulting to ALPHABETA.");
        }
      }
    }

    // Check if MCTS
    int simulations = 0;
    if (algorithmTypeContest == AlgorithmType.MCTS) {
      if (whiteTurn) {
        if (options.containsKey(OptionType.AI_SIMULATION_W)) {
          try {
            simulations = Integer.parseInt(options.get(OptionType.AI_SIMULATION_W));
          } catch (Exception e) {
            error("Not an integer for the simulations of AI");
          }
        }
      } else {
        if (options.containsKey(OptionType.AI_SIMULATION_B)) {
          try {
            simulations = Integer.parseInt(options.get(OptionType.AI_SIMULATION_B));
          } catch (Exception e) {
            error("Not an integer for the simulations of AI");
          }
        }
      }
    }

    if (algorithmTypeContest != null) {
      if (algorithmTypeContest == AlgorithmType.MCTS) {
        if (simulations != 0) {
          solver.setMonteCarloAlgorithm(simulations);
        }
      } else { // ALPHA_BETA or MINIMAX
        solver.setAlgorithm(algorithmTypeContest);
      }
    } else { // DEFAULT IS ALPHA_BETA
      solver.setAlgorithm(AlgorithmType.ALPHA_BETA);
    }
  }

  /**
   * Processes the AI endgame heuristic for contest mode based on the current player's turn.
   * Retrieves the heuristic type from the options and applies it to the solver. Defaults to the
   * STANDARD endgame heuristic if no valid heuristic is provided. Used for Contest mode.
   *
   * @param options The map containing AI configuration options.
   * @param whiteTurn Indicates whether it is White's turn.
   * @param solver The solver instance for the current player.
   */
  private static void processAiEndGameHeuristicContest(
      final HashMap<OptionType, String> options, final boolean whiteTurn, final Solver solver) {
    HeuristicType heuristicTypeEndGame = null;
    if (whiteTurn) {
      if (options.containsKey(OptionType.AI_ENDGAME_W)) {
        try {
          heuristicTypeEndGame = HeuristicType.valueOf(options.get(OptionType.AI_ENDGAME_W));
        } catch (IllegalArgumentException e) {
          error("Unknown Heuristic: " + options.get(OptionType.AI_ENDGAME_W));
          error("Defaulting to Endgame Heuristic STANDARD");
        }
      }
    } else {
      if (options.containsKey(OptionType.AI_ENDGAME_B)) {
        try {
          heuristicTypeEndGame = HeuristicType.valueOf(options.get(OptionType.AI_ENDGAME_B));
        } catch (IllegalArgumentException e) {
          error("Unknown Heuristic: " + options.get(OptionType.AI_ENDGAME_B));
          error("Defaulting to Endgame Heuristic STANDARD");
        }
      }
    }

    if (heuristicTypeEndGame != null) {
      solver.setEndgameHeuristic(heuristicTypeEndGame);
    }
  }

  /**
   * Processes the AI heuristic for contest mode based on the current player's turn. Retrieves the
   * heuristic type and associated weights (if applicable) from the options and applies them to the
   * solver. Used for Contest mode.
   *
   * @param options The map containing AI configuration options.
   * @param whiteTurn Indicates whether it is White's turn.
   * @param solver The solver instance for the current player.
   */
  private static void processAiHeuristicContest(
      final HashMap<OptionType, String> options, final boolean whiteTurn, final Solver solver) {
    final OptionType heuristicKey =
        whiteTurn ? OptionType.AI_HEURISTIC_W : OptionType.AI_HEURISTIC_B;
    final OptionType weightKey = whiteTurn ? OptionType.AI_WEIGHT_W : OptionType.AI_WEIGHT_B;

    final HeuristicType heuristicType = getHeuristicType(options, heuristicKey);
    final List<Float> weights = getWeights(options, heuristicType, weightKey);

    if (weights != null) {
      solver.setHeuristic(heuristicType, weights);
    } else {
      solver.setHeuristic(heuristicType);
    }
  }

  /**
   * Helper method. Retrieves the heuristic type from the provided options. Defaults to STANDARD if
   * the heuristic type is invalid or unspecified. Used for Contest mode.
   *
   * @param options The map containing AI configuration options.
   * @param heuristicKey The key corresponding to the heuristic option for the current player.
   * @return The heuristic type to be used.
   */
  private static HeuristicType getHeuristicType(
      final HashMap<OptionType, String> options, final OptionType heuristicKey) {
    if (options.containsKey(heuristicKey)) {
      try {
        return HeuristicType.valueOf(options.get(heuristicKey));
      } catch (IllegalArgumentException e) {
        error("Unknown Heuristic: " + options.get(heuristicKey));
      }
    }
    return HeuristicType.STANDARD;
  }

  /**
   * Helper method. Retrieves the weight values for the heuristic if applicable. Only applies
   * weights if the heuristic type is STANDARD. Returns null if weights are invalid or missing. Used
   * for Contest mode.
   *
   * @param options The map containing AI configuration options.
   * @param heuristicType The heuristic type being used.
   * @param weightKey The key corresponding to the weight option for the current player.
   * @return A list of float values representing the heuristic weights, or null if unavailable.
   */
  private static List<Float> getWeights(
      final HashMap<OptionType, String> options,
      final HeuristicType heuristicType,
      final OptionType weightKey) {
    if (heuristicType.equals(HeuristicType.STANDARD) && options.containsKey(weightKey)) {
      try {
        final String weight = options.get(weightKey);
        final String[] weightsArray = weight.split(",");
        final List<Float> weightsFloats = new ArrayList<>();

        for (final String w : weightsArray) {
          weightsFloats.add(Float.parseFloat(w));
        }
        if (weightsFloats.size() != 7) {
          throw new ParseException("Invalid number of weights", 0);
        }
        return weightsFloats;
      } catch (ParseException | NumberFormatException e) {
        error("Weights problem: " + options.get(weightKey) + " -> " + e.getMessage());
        error("Defaulting to Unweighted Heuristic STANDARD");
      }
    }
    return null;
  }
}
