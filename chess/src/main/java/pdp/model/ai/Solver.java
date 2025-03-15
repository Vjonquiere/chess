package pdp.model.ai;

import static pdp.utils.Logging.DEBUG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import pdp.events.EventType;
import pdp.model.Game;
import pdp.model.ai.algorithms.AlphaBeta;
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
import pdp.model.ai.heuristics.ShannonBasic;
import pdp.model.ai.heuristics.SpaceControlHeuristic;
import pdp.model.ai.heuristics.StandardHeuristic;
import pdp.model.board.Board;
import pdp.model.board.ZobristHashing;
import pdp.utils.Logging;
import pdp.utils.Timer;

public class Solver {
  private static final Logger LOGGER = Logger.getLogger(Solver.class.getName());
  // Zobrist hashing to avoid recomputing the position evaluation for the same boards
  private final ZobristHashing zobristHashing = new ZobristHashing();
  private final HashMap<Long, Integer> evaluatedBoards;

  SearchAlgorithm algorithm;
  Heuristic heuristic;
  int depth = 4;
  Timer timer;
  long time;

  static {
    Logging.configureLogging(LOGGER);
  }

  public Solver() {
    evaluatedBoards = new HashMap<>();
    this.algorithm = new AlphaBeta(this);
    this.heuristic = new StandardHeuristic();
  }

  /**
   * Set the algorithm to be used.;
   *
   * @param algorithm The algorithm to use.
   */
  public void setAlgorithm(AlgorithmType algorithm) {
    switch (algorithm) {
      case MINIMAX -> this.algorithm = new Minimax(this);
      case ALPHA_BETA -> this.algorithm = new AlphaBeta(this);
      case MCTS -> this.algorithm = new MonteCarloTreeSearch(this);
      default -> throw new IllegalArgumentException("No algorithm is set");
    }
    DEBUG(LOGGER, "Algorithm set to " + algorithm);
  }

  /**
   * Assigns a value (typed by the user in CLI) to the simulation limit for MonteCarloTreeSearch.
   * Method used in GameInitializer. StandardHeuristic
   *
   * @param numberSimulations the number of MonteCarloTreeSearch simulations wanted by the user
   */
  public void setMonteCarloAlgorithm(int numberSimulations) {
    this.algorithm = new MonteCarloTreeSearch(this, numberSimulations);
  }

  /**
   * Set the heuristic to be used.
   *
   * @param heuristic The heuristic to use.
   */
  public void setHeuristic(HeuristicType heuristic) {
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
      case ENDGAME -> this.heuristic = new EndGameHeuristic();
      default -> throw new IllegalArgumentException("No heuristic is set");
    }
    DEBUG(LOGGER, "Heuristic set to: " + this.heuristic);
  }

  /**
   * Set the heuristic to be used.
   *
   * @param heuristic The heuristic to use.
   */
  public void setHeuristic(HeuristicType heuristic, ArrayList<Integer> weight) {
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
      case STANDARD -> this.heuristic = new StandardHeuristic(weight);
      case ENDGAME -> this.heuristic = new EndGameHeuristic();
      default -> throw new IllegalArgumentException("No heuristic is set");
    }
    DEBUG(LOGGER, "Heuristic set to: " + this.heuristic);
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
  public void setDepth(int depth) {
    if (depth <= 0) {
      throw new IllegalArgumentException("Depth must be greater than 0");
    }
    DEBUG(LOGGER, "Depth set to " + depth);
    this.depth = depth;
  }

  /**
   * Set the maximum time (in seconds) the solver should spend computing a move.
   *
   * @param time The time to use.
   */
  public void setTime(long time) {
    if (time <= 0) {
      throw new IllegalArgumentException("Time must be greater than 0");
    }
    this.time = time * 1000;
    timer = new Timer(this.time);
    DEBUG(LOGGER, "Time set to " + this.time);
  }

  public Timer getTimer() {
    return timer;
  }

  public long getTime() {
    return time;
  }

  /**
   * Uses the AI algorithm to find the best move and plays it.
   *
   * @param game current game
   */
  public void playAIMove(Game game) {
    game.setExploration(true);
    if (timer != null) {
      timer.start();
    }
    AIMove bestMove = algorithm.findBestMove(game, depth, game.getBoard().isWhite);
    if (timer != null) {
      timer.stop();
    }

    DEBUG(LOGGER, "Best move " + bestMove);
    game.setExploration(false);
    try {
      game.playMove(bestMove.move());
    } catch (Exception e) {
      game.notifyObservers(EventType.AI_NOT_ENOUGH_TIME);
      System.err.println(e.getMessage());
      if (game.getBoard().isWhite) {
        game.getGameState().whiteResigns();
      } else {
        game.getGameState().blackResigns();
      }
    }
  }

  /**
   * Evaluates the board based on the chosen heuristic. Use Zobrist Hashing to avoid recalculating
   * scores.
   *
   * @param board Current board to evaluate
   * @param isWhite Current player
   * @return score corresponding to the position evaluation of the board.
   */
  public int evaluateBoard(Board board, boolean isWhite) {
    if (board == null) {
      throw new IllegalArgumentException("Board is null");
    }

    long hash = zobristHashing.generateHashFromBitboards(board);
    if (evaluatedBoards.containsKey(hash)) {
      return evaluatedBoards.get(hash);
    }

    int score = heuristic.evaluate(board, isWhite);
    evaluatedBoards.put(hash, score);
    return score;
  }
}
