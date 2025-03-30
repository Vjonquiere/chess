package pdp.model.ai;

import static pdp.utils.Logging.debug;
import static pdp.utils.Logging.error;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import pdp.events.EventType;
import pdp.model.Game;
import pdp.model.ai.algorithms.AlphaBeta;
import pdp.model.ai.algorithms.AlphaBetaIterativeDeepening;
import pdp.model.ai.algorithms.AlphaBetaIterativeDeepeningParallel;
import pdp.model.ai.algorithms.AlphaBetaParallel;
import pdp.model.ai.algorithms.Minimax;
import pdp.model.ai.algorithms.MonteCarloTreeSearch;
import pdp.model.ai.algorithms.SearchAlgorithm;
import pdp.model.ai.heuristics.BadPawnsHeuristic;
import pdp.model.ai.heuristics.BishopEndgameHeuristic;
import pdp.model.ai.heuristics.DevelopmentHeuristic;
import pdp.model.ai.heuristics.EndGameHeuristic;
import pdp.model.ai.heuristics.GameStatus;
import pdp.model.ai.heuristics.Heuristic;
import pdp.model.ai.heuristics.KingActivityHeuristic;
import pdp.model.ai.heuristics.KingOppositionHeuristic;
import pdp.model.ai.heuristics.KingSafetyHeuristic;
import pdp.model.ai.heuristics.MaterialHeuristic;
import pdp.model.ai.heuristics.MobilityHeuristic;
import pdp.model.ai.heuristics.PawnChainHeuristic;
import pdp.model.ai.heuristics.PromotionHeuristic;
import pdp.model.ai.heuristics.ShannonBasic;
import pdp.model.ai.heuristics.SpaceControlHeuristic;
import pdp.model.ai.heuristics.StandardHeuristic;
import pdp.model.ai.heuristics.StandardLightHeuristic;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.model.board.ZobristHashing;
import pdp.model.piece.Color;
import pdp.utils.Logging;
import pdp.utils.Timer;

/** Solver corresponding to an AI player. */
public class Solver {
  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(Solver.class.getName());

  /** Zobrist hashing to avoid recomputing the position evaluation for the same boards. */
  private final ZobristHashing zobristHashing = new ZobristHashing();

  /** Map containing evaluations of boards, stored thanks to zobrist. */
  private ConcurrentHashMap<Long, Float> evaluatedBoards;

  /** AI algorithm to find the best move. */
  private SearchAlgorithm algorithm;

  /** Heuristic to compute the score of a board. */
  private Heuristic heuristic;

  /** Heuristic currently used. */
  private HeuristicType currentHeuristic;

  /** Heuristic chosen for the start and middle phase of the game. */
  private HeuristicType startHeuristic;

  /** Heuristic chosen for the endgame phase of the game. */
  private HeuristicType endgameHeuristic;

  /** The last move reflexion time in nanoseconds. */
  private long lastMoveTime;

  /**
   * Depth for the SearchAlgorithm. The algorithm will play depth consecutive moves before
   * evaluation.
   */
  private int depth = 4;

  /** Timer for bounded time. */
  private Timer timer;

  /** Time allocated for the search of a move. */
  private long time;

  /** Boolean to indicate whether the algorithm is searching for a move. */
  private boolean searchStopped = false;

  /** Boolean to indicate if the move needs to be played. Used for the hint of gui. */
  private boolean isMoveToPlay = true;

  static {
    Logging.configureLogging(LOGGER);
  }

  /** Initializes the solver with the default heuristic and algorithm. */
  public Solver() {
    evaluatedBoards = new ConcurrentHashMap<>();
    this.algorithm = new AlphaBeta(this);
    this.heuristic = new StandardHeuristic();
  }

  /**
   * Set the algorithm to be used.
   *
   * @param algorithm The algorithm to use.
   */
  public void setAlgorithm(final AlgorithmType algorithm) {
    switch (algorithm) {
      case MINIMAX -> this.algorithm = new Minimax(this);
      case ALPHA_BETA -> this.algorithm = new AlphaBeta(this);
      case ALPHA_BETA_ID -> this.algorithm = new AlphaBetaIterativeDeepening(this);
      case ALPHA_BETA_PARALLEL -> this.algorithm = new AlphaBetaParallel(this);
      case ALPHA_BETA_ID_PARALLEL -> this.algorithm = new AlphaBetaIterativeDeepeningParallel(this);
      case MCTS -> this.algorithm = new MonteCarloTreeSearch(this);
      default -> throw new IllegalArgumentException("No algorithm is set");
    }
    debug(LOGGER, "Algorithm set to " + algorithm);
  }

  /**
   * Assigns a value (typed by the user in CLI) to the simulation limit for MonteCarloTreeSearch.
   * Method used in GameInitializer.
   *
   * @param numberSimulations the number of MonteCarloTreeSearch simulations wanted by the user
   */
  public void setMonteCarloAlgorithm(final int numberSimulations) {
    this.algorithm = new MonteCarloTreeSearch(this, numberSimulations);
  }

  /**
   * Set the heuristic to be used.
   *
   * @param heuristic The heuristic to use.
   */
  public void setHeuristic(final HeuristicType heuristic) {
    switch (heuristic) {
      case MATERIAL -> this.heuristic = new MaterialHeuristic();
      case KING_SAFETY -> this.heuristic = new KingSafetyHeuristic();
      case SPACE_CONTROL -> this.heuristic = new SpaceControlHeuristic();
      case DEVELOPMENT -> this.heuristic = new DevelopmentHeuristic();
      case PAWN_CHAIN -> this.heuristic = new PawnChainHeuristic();
      case MOBILITY -> this.heuristic = new MobilityHeuristic();
      case BAD_PAWNS -> this.heuristic = new BadPawnsHeuristic();
      case SHANNON -> this.heuristic = new ShannonBasic();
      case GAME_STATUS -> this.heuristic = new GameStatus();
      case KING_ACTIVITY -> this.heuristic = new KingActivityHeuristic();
      case BISHOP_ENDGAME -> this.heuristic = new BishopEndgameHeuristic();
      case KING_OPPOSITION -> this.heuristic = new KingOppositionHeuristic();
      case STANDARD -> this.heuristic = new StandardHeuristic();
      case STANDARD_LIGHT -> this.heuristic = new StandardLightHeuristic();
      case ENDGAME -> this.heuristic = new EndGameHeuristic();
      case PROMOTION -> this.heuristic = new PromotionHeuristic();
      default -> throw new IllegalArgumentException("No heuristic is set");
    }
    this.currentHeuristic = heuristic;
    if (this.startHeuristic == null) {
      this.startHeuristic = heuristic;
    }
    this.evaluatedBoards = new ConcurrentHashMap<>();
    debug(LOGGER, "Heuristic set to: " + this.heuristic);
  }

  /**
   * Set the heuristic to be used.
   *
   * @param heuristic The heuristic to use.
   */
  public void setHeuristic(final HeuristicType heuristic, final List<Float> weight) {
    if (heuristic == HeuristicType.STANDARD) {
      this.heuristic = new StandardHeuristic(weight);
      if (this.startHeuristic == null) {
        this.startHeuristic = heuristic;
      }
      evaluatedBoards = new ConcurrentHashMap<>();
      this.currentHeuristic = heuristic;
      debug(LOGGER, "Heuristic set to: " + this.heuristic);
    } else {
      setHeuristic(heuristic);
    }
  }

  /**
   * Sets the field endgame heuristic to the one in the parameters.
   *
   * @param heuristic endgame heuristic to set
   */
  public void setEndgameHeuristic(final HeuristicType heuristic) {
    this.endgameHeuristic = heuristic;
  }

  /**
   * Retrieves the endgame heuristic.
   *
   * @return current endgame heuristic
   */
  public HeuristicType getEndgameHeuristic() {
    return this.endgameHeuristic;
  }

  public void setStartHeuristic(final HeuristicType heuristic) {
    this.startHeuristic = heuristic;
  }

  public HeuristicType getStartHeuristic() {
    return this.startHeuristic;
  }

  /**
   * Retrieves the current heuristic.
   *
   * @return current heuristic
   */
  public HeuristicType getCurrentHeuristic() {
    return this.currentHeuristic;
  }

  /**
   * Retrieve the current heuristic.
   *
   * @return the current heuristic that the solver uses
   */
  public Heuristic getHeuristic() {
    return this.heuristic;
  }

  /**
   * Retrieve the current algorithm.
   *
   * @return the current SearchAlgorithm that the solver uses
   */
  public SearchAlgorithm getAlgorithm() {
    return this.algorithm;
  }

  /**
   * Retrieve the maximum depth of AI exploration.
   *
   * @return maximum depth
   */
  public int getDepth() {
    return depth;
  }

  /**
   * Set the maximum depth the solver should explore.
   *
   * @param depth The depth to use.
   */
  public void setDepth(final int depth) {
    if (depth <= 0) {
      throw new IllegalArgumentException("Depth must be greater than 0");
    }
    debug(LOGGER, "Depth set to " + depth);

    this.depth = depth;
  }

  /**
   * Set the maximum time (in seconds) the solver should spend computing a move.
   *
   * @param time The time to use.
   */
  public void setTime(final long time) {
    if (time <= 0) {
      throw new IllegalArgumentException("Time must be greater than 0");
    }
    this.time = time * 1000;
    timer = new Timer(this.time);
    timer.setCallback(() -> this.stopSearch(true));
    debug(LOGGER, "Time set to " + this.time);
  }

  /**
   * Retrieves the timer of the solver.
   *
   * @return timer of the solver
   */
  public Timer getTimer() {
    return timer;
  }

  /**
   * Retrieves the time by default of the timer.
   *
   * @return time set for the timer
   */
  public long getTime() {
    return time;
  }

  /**
   * Stops the search of the best move and sets the field isMoveToPlay to the boolean in parameter.
   *
   * @param playMove boolean
   */
  public void stopSearch(final boolean playMove) {
    searchStopped = true;
    isMoveToPlay = playMove;
  }

  /**
   * Indicates whether the search of AI move is over or not.
   *
   * @return true if the search is stopped, false otherwise
   */
  public boolean isSearchStopped() {
    return searchStopped;
  }

  /**
   * Uses the AI algorithm to find the best move and plays it.
   *
   * @param game current game
   */
  public void playAiMove(final Game game) {
    game.setExploration(true);
    if (timer != null) {
      timer.start();
    }
    final long startTime = System.nanoTime();
    searchStopped = false;
    isMoveToPlay = true;
    game.setAiPlayedItsLastMove(false);
    final AiMove bestMove = algorithm.findBestMove(game, depth, game.getGameState().isWhiteTurn());
    game.setAiPlayedItsLastMove(true);
    if (timer != null) {
      timer.stop();
    }
    lastMoveTime = System.nanoTime() - startTime;

    debug(LOGGER, "Best move " + bestMove);

    game.setExploration(false);

    if (isMoveToPlay) {
      try {
        game.playMove(bestMove.move());
      } catch (Exception e) {
        game.notifyObservers(EventType.AI_NOT_ENOUGH_TIME);
        error(e.getMessage());
        if (game.getGameState().isWhiteTurn()) {
          game.getGameState().whiteResigns();
        } else {
          game.getGameState().blackResigns();
        }
      }
    }
  }

  /**
   * Retrieves the best move for the given game.
   *
   * @param game Game to find the best move in
   * @return best move according to the game in parameter
   */
  public Move getBestMove(final Game game) {
    game.setExploration(true);
    if (timer != null) {
      timer.start();
    }
    final AiMove bestMove = algorithm.findBestMove(game, depth, game.getBoard().getPlayer());
    if (timer != null) {
      timer.stop();
    }

    debug(LOGGER, "Best move " + bestMove);
    game.setExploration(false);
    return bestMove.move();
  }

  /**
   * Evaluates the board based on the chosen heuristic. Use Zobrist Hashing to avoid recalculating
   * scores.
   *
   * @param board Current board to evaluate
   * @param isWhite Current player
   * @return score corresponding to the position evaluation of the board.
   */
  public float evaluateBoard(final BoardRepresentation board, final boolean isWhite) {
    if (board == null) {
      throw new IllegalArgumentException("Board is null");
    }

    final long hash = zobristHashing.generateHashFromBitboards(board);
    float score;
    if (evaluatedBoards.containsKey(hash)) {
      score = evaluatedBoards.get(hash);
    } else {
      score = heuristic.evaluate(board, isWhite);
      evaluatedBoards.put(hash, score);
    }

    final Color player = isWhite ? Color.WHITE : Color.BLACK;

    if (Game.getInstance().getGameState().isThreefoldRepetition()
        || board.isStaleMate(player, player)
        || board.getNbFullMovesWithNoCaptureOrPawn() >= 50) {
      score = 0;
    }

    return score;
  }

  /**
   * Get the reflexion time for the last AI move.
   *
   * @return A long corresponding to the time in nanoseconds.
   */
  public long getLastMoveTime() {
    return lastMoveTime;
  }
}
