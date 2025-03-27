package pdp;

import static pdp.utils.Logging.debug;

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

    if (options.containsKey(OptionType.CONTEST)) {
      String contestFile = options.get(OptionType.CONTEST);
      if (contestFile == null || contestFile.isEmpty()) {
        System.err.println("Error: --contest option requires a valid file path.");
      } else {
        try {
          InputStream inputStream = new FileInputStream(contestFile);
          List<String> moveStrings = MoveHistoryParser.parseHistoryFile(inputStream);
          List<Move> moves = new ArrayList<>();
          for (String move : moveStrings) {
            moves.add(Move.fromString(move.replace("x", "-")));
          }

          model =
              Game.fromHistory(
                  moves, isWhiteAi, isBlackAi, solverWhite, solverBlack, timer, options);

          // Init solver according to the current player
          Solver solver = null;
          if (model.getGameState().isWhiteTurn()) {
            Solver whiteSolver = new Solver();

            model.setWhiteSolver(whiteSolver);
            model.setWhiteAi(true);
            model.setBlackAi(false);

            processAiDepthContest(options, true, whiteSolver);
            processAiModeContest(options, true, whiteSolver);
            processAiEndGameHeuristicContest(options, true, whiteSolver);
            processAiHeuristicContest(options, true, whiteSolver);

            System.err.println("HERE IS THE DEPTH AFTER SET :" + model.getWhiteSolver().getDepth());

            solver = whiteSolver;
          } else {
            new Solver();
            Solver blackSolver = new Solver();

            model.setBlackSolver(blackSolver);
            model.setBlackAi(true);
            model.setWhiteAi(false);

            processAiDepthContest(options, false, blackSolver);
            processAiModeContest(options, false, blackSolver);
            processAiEndGameHeuristicContest(options, false, blackSolver);
            processAiHeuristicContest(options, false, blackSolver);

            System.err.println("HERE IS THE DEPTH AFTER SET :" + model.getBlackSolver().getDepth());

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
          System.err.println("Error loading contest file: " + e.getMessage());
          System.err.println("Starting a new game instead.");
          model = Game.initialize(isWhiteAi, isBlackAi, solverWhite, solverBlack, timer, options);
        }
      }
    }

    if (model == null) {
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
            model.setContestModeOnOrOff(false);
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
            model.setContestModeOnOrOff(false);
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
      HashMap<OptionType, String> options, boolean whiteTurn, Solver solver) {
    int depth = 0;
    if (whiteTurn) {
      if (options.containsKey(OptionType.AI_DEPTH_W)) {
        try {
          depth = Integer.parseInt(options.get(OptionType.AI_DEPTH_W));
        } catch (Exception e) {
          System.err.println("Not an integer for ai depth");
        }
      }
    } else {
      if (options.containsKey(OptionType.AI_DEPTH_B)) {
        try {
          depth = Integer.parseInt(options.get(OptionType.AI_DEPTH_B));
        } catch (Exception e) {
          System.err.println("Not an integer for ai depth");
        }
      }
    }

    if (depth != 0) {
      solver.setDepth(depth);
      System.err.println("SETTING DEPTH : " + depth);
    } else {
      solver.setDepth(4);
      System.err.println("SETTING DEFAULT DEPTH 4");
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
      HashMap<OptionType, String> options, boolean whiteTurn, Solver solver) {
    AlgorithmType algorithmTypeContest = null;
    if (whiteTurn) {
      if (options.containsKey(OptionType.AI_MODE_W)) {
        try {
          algorithmTypeContest = AlgorithmType.valueOf(options.get(OptionType.AI_MODE_W));
        } catch (Exception e) {
          System.err.println("Unknown AI mode option: " + options.get(OptionType.AI_MODE));
          System.err.println("Defaulting to ALPHABETA.");
        }
      }
    } else {
      if (options.containsKey(OptionType.AI_MODE_B)) {
        try {
          algorithmTypeContest = AlgorithmType.valueOf(options.get(OptionType.AI_MODE_B));
        } catch (Exception e) {
          System.err.println("Unknown AI mode option: " + options.get(OptionType.AI_MODE_B));
          System.err.println("Defaulting to ALPHABETA.");
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
            System.err.println("Not an integer for the simulations of AI");
          }
        }
      } else {
        if (options.containsKey(OptionType.AI_SIMULATION_B)) {
          try {
            simulations = Integer.parseInt(options.get(OptionType.AI_SIMULATION_B));
          } catch (Exception e) {
            System.err.println("Not an integer for the simulations of AI");
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
      HashMap<OptionType, String> options, boolean whiteTurn, Solver solver) {
    HeuristicType heuristicTypeEndGame = null;
    if (whiteTurn) {
      if (options.containsKey(OptionType.AI_ENDGAME_W)) {
        try {
          heuristicTypeEndGame = HeuristicType.valueOf(options.get(OptionType.AI_ENDGAME_W));
        } catch (IllegalArgumentException e) {
          System.err.println("Unknown Heuristic: " + options.get(OptionType.AI_ENDGAME_W));
          System.err.println("Defaulting to Endgame Heuristic STANDARD");
        }
      }
    } else {
      if (options.containsKey(OptionType.AI_ENDGAME_B)) {
        try {
          heuristicTypeEndGame = HeuristicType.valueOf(options.get(OptionType.AI_ENDGAME_B));
        } catch (IllegalArgumentException e) {
          System.err.println("Unknown Heuristic: " + options.get(OptionType.AI_ENDGAME_B));
          System.err.println("Defaulting to Endgame Heuristic STANDARD");
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
      HashMap<OptionType, String> options, boolean whiteTurn, Solver solver) {
    OptionType heuristicKey = whiteTurn ? OptionType.AI_HEURISTIC_W : OptionType.AI_HEURISTIC_B;
    OptionType weightKey = whiteTurn ? OptionType.AI_WEIGHT_W : OptionType.AI_WEIGHT_B;

    HeuristicType heuristicType = getHeuristicType(options, heuristicKey);
    ArrayList<Float> weights = getWeights(options, heuristicType, weightKey);

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
      HashMap<OptionType, String> options, OptionType heuristicKey) {
    if (options.containsKey(heuristicKey)) {
      try {
        return HeuristicType.valueOf(options.get(heuristicKey));
      } catch (IllegalArgumentException e) {
        System.err.println("Unknown Heuristic: " + options.get(heuristicKey));
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
  private static ArrayList<Float> getWeights(
      HashMap<OptionType, String> options, HeuristicType heuristicType, OptionType weightKey) {
    if (heuristicType.equals(HeuristicType.STANDARD) && options.containsKey(weightKey)) {
      try {
        String weight = options.get(weightKey);
        String[] weightsArray = weight.split(",");
        ArrayList<Float> weightsFloats = new ArrayList<>();

        for (String w : weightsArray) {
          weightsFloats.add(Float.parseFloat(w));
        }
        if (weightsFloats.size() != 7) {
          throw new ParseException("Invalid number of weights", 0);
        }
        return weightsFloats;
      } catch (ParseException | NumberFormatException e) {
        System.err.println("Weights problem: " + options.get(weightKey) + " -> " + e.getMessage());
        System.err.println("Defaulting to Unweighted Heuristic STANDARD");
      }
    }
    return null;
  }
}
