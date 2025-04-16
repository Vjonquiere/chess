package pdp.model.ai.algorithms;

import static pdp.utils.Logging.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import pdp.exceptions.IllegalMoveException;
import pdp.model.Game;
import pdp.model.GameAi;
import pdp.model.ai.AiMove;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.utils.Logging;

/**
 * Algorithm of artificial intelligence Alpha beta pruning, with iterative deepening to have more
 * efficient search.
 */
public class AlphaBetaIterativeDeepening extends SearchAlgorithm {
  /** Solver used for calling the evaluation of the board once depth is reached or time is up. */
  private final Solver solver;

  /** Logger of the class. */
  private static final Logger LOGGER =
      Logger.getLogger(AlphaBetaIterativeDeepening.class.getName());

  /** Boolean to indicate whether the search has been stopped before reaching the depth asked. */
  private boolean stoppedEarly;

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Initializes the field solver with the one given in parameter.
   *
   * @param solver Solver needed to call the evaluation
   */
  public AlphaBetaIterativeDeepening(final Solver solver) {
    super();
    this.solver = solver;
  }

  /**
   * Determines the best move using the AlphaBeta algorithm.
   *
   * @param game The current game state.
   * @param maxDepth The number of moves to look ahead.
   * @param player The current player (true for white, false for black).
   * @return The best move for the player.
   */
  @Override
  public AiMove findBestMove(final Game game, final int maxDepth, final boolean player) {

    final GameAi gameAi = GameAi.fromGame(game);

    this.stoppedEarly = false;

    AiMove bestMove = null;
    final List<Move> rootMoves = new ArrayList<>(gameAi.getBoard().getAllAvailableMoves(player));

    for (int depth = 1; depth <= maxDepth; depth++) {
      if (solver.isSearchStopped()) {
        break;
      }

      if (bestMove != null && bestMove.move() != null) {
        rootMoves.remove(bestMove.move());
        MoveOrdering.moveOrder(rootMoves);
        rootMoves.add(0, bestMove.move());
      }

      final AiMove currentBest =
          alphaBeta(gameAi, depth, player, -Float.MAX_VALUE, Float.MAX_VALUE, player, rootMoves);
      if (currentBest != null && !this.stoppedEarly) {
        bestMove = currentBest;
      }
    }

    if (bestMove == null) {
      bestMove = new AiMove(rootMoves.get(0), 0);
    }

    debug(LOGGER, "Best move: " + bestMove);
    final long visitedNodes = getVisitedNodes();
    clearNode();
    debug(LOGGER, "This search: " + visitedNodes + ", mean: " + getMean());
    return bestMove;
  }

  /**
   * Finds the best move for the given player. It cuts the uninteresting branches with the
   * AlphaBetaPruning.
   *
   * <p>The method evaluates recursively the game state to select the optimal move.
   *
   * @param game The current game
   * @param depth The number of moves remaining in the search
   * @param currentPlayer The current player (true for white, false for black).
   * @param alpha The best option for the maximizing player
   * @param beta The best option for the minimizing player
   * @param originalPlayer The player at root
   * @return The best move with its evaluated score.
   */
  private AiMove alphaBeta(
      final GameAi game,
      final int depth,
      final boolean currentPlayer,
      float alpha,
      float beta,
      final boolean originalPlayer,
      final List<Move> orderedMoves) {
    addNode();
    if (solver.isSearchStopped()) {
      this.stoppedEarly = true;
      return new AiMove(
          null, currentPlayer == originalPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE);
    }
    if (depth == 0 || game.isOver()) {
      final float evaluation = solver.evaluateBoard(game.getGameState(), originalPlayer);
      return new AiMove(null, evaluation);
    }

    List<Move> moves = orderedMoves;
    if (moves == null) {
      moves = game.getBoard().getAllAvailableMoves(currentPlayer);
      MoveOrdering.moveOrder(moves);
    }

    AiMove bestMove =
        new AiMove(null, currentPlayer == originalPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE);
    for (final Move move : moves) {
      if (solver.isSearchStopped()) {
        this.stoppedEarly = true;
        break;
      }
      try {
        game.playMove(move);
        final AiMove currMove =
            alphaBeta(game, depth - 1, !currentPlayer, alpha, beta, originalPlayer, null);
        game.previousState();
        if (currentPlayer == originalPlayer) { // Maximizing
          if (currMove.score() > bestMove.score()) {
            bestMove = new AiMove(move, currMove.score());
          }
          alpha = Math.max(alpha, bestMove.score());
        } else { // Minimizing
          if (currMove.score() < bestMove.score()) {
            bestMove = new AiMove(move, currMove.score());
          }
          beta = Math.min(beta, bestMove.score());
        }
        if (alpha >= beta) {
          break;
        }
      } catch (IllegalMoveException expected) {
        // Skipping illegal move
      }
    }
    return bestMove;
  }

  @Override
  public String toString() {
    return "Alpha-Beta Iterative Deepening";
  }
}
