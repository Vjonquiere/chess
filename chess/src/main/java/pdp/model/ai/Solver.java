package pdp.model.ai;

import java.util.HashMap;
import java.util.logging.Logger;
import pdp.model.Game;
import pdp.model.ai.algorithms.AlphaBeta;
import pdp.model.ai.algorithms.Minimax;
import pdp.model.ai.algorithms.SearchAlgorithm;
import pdp.model.ai.heuristics.Heuristic;
import pdp.model.ai.heuristics.MaterialHeuristic;
import pdp.model.board.Board;
import pdp.model.board.ZobristHashing;
import pdp.utils.Logging;

public class Solver {
  private final Logger LOGGER = Logger.getLogger(Solver.class.getName());
  // Zobrist hashing to avoid recomputing the position evaluation for the same boards
  private ZobristHashing zobristHashing = new ZobristHashing();
  private HashMap<Long, Integer> evaluatedBoards;

  SearchAlgorithm algorithm;
  Heuristic heuristic;
  int depth = 3;
  int time = 500;

  public Solver() {
    Logging.configureLogging(LOGGER);
    evaluatedBoards = new HashMap<>();
    this.algorithm = new Minimax(this);
    this.heuristic = new MaterialHeuristic();
  }

  /**
   * Set the algorithm to be used.
   *
   * @param algorithm The algorithm to use.
   */
  public void setAlgorithm(AlgorithmType algorithm) {
    switch (algorithm) {
      case MINIMAX -> this.algorithm = new Minimax(this);
      case ALPHA_BETA -> this.algorithm = new AlphaBeta(this);
      case MCTS -> this.algorithm = null;
      default -> throw new IllegalArgumentException("No algorithm is set");
    }
  }

  /**
   * Set the heuristic to be used.
   *
   * @param heuristic The heuristic to use.
   */
  public void setHeuristic(HeuristicType heuristic) {
    switch (heuristic) {
      case MATERIAL -> this.heuristic = new MaterialHeuristic();
      case POSITIONAL -> this.heuristic = null;
      case KING_SAFETY -> this.heuristic = null;
      case SPACE_CONTROL -> this.heuristic = null;
      case PAWN_STRUCTURE -> this.heuristic = null;
      case PIECE_ACTIVITY -> this.heuristic = null;
      case ENDGAME -> this.heuristic = null;
      default -> throw new IllegalArgumentException("No heuristic is set");
    }
  }

  /**
   * Set the maximum depth the solver should explore.
   *
   * @param depth The depth to use.
   */
  public void setDepth(int depth) {
    this.depth = depth;
  }

  /**
   * Set the maximum time (in milliseconds) the solver should spend computing a move.
   *
   * @param time The time to use.
   */
  public void setTime(int time) {
    this.time = time;
  }

  /**
   * Uses the AI algorithm to find the best move and plays it.
   *
   * @param game current game
   */
  public void playAIMove(Game game) {
    if (algorithm == null) {
      throw new IllegalStateException("No algorithm has been set");
    }
    AIMove bestMove = algorithm.findBestMove(game, depth, game.getBoard().isWhite);
    game.playMove(bestMove.move());
  }

  /**
   * Evaluates the board based on the chosen heuristic.
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
