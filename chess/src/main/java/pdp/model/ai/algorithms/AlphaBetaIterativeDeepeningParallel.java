package pdp.model.ai.algorithms;

import static pdp.utils.Logging.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import pdp.exceptions.IllegalMoveException;
import pdp.model.Game;
import pdp.model.GameAi;
import pdp.model.ai.AiMove;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.utils.Logging;

/**
 * Algorithm of artificial intelligence Alpha beta pruning, with parallelization (threads) and
 * iterative deepening to have more efficient search.
 */
public class AlphaBetaIterativeDeepeningParallel extends SearchAlgorithm {

  /** Solver used for calling the evaluation of the board once depth is reached or time is up. */
  private final Solver solver;

  /** Logger of the class. */
  private static final Logger LOGGER =
      Logger.getLogger(AlphaBetaIterativeDeepeningParallel.class.getName());

  /** Boolean to indicate whether the search has been stopped before reaching the depth asked. */
  private AtomicBoolean stoppedEarly = new AtomicBoolean(false);

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Initializes the field solver with the one given in parameter.
   *
   * @param solver Solver needed to call the evaluation
   */
  public AlphaBetaIterativeDeepeningParallel(final Solver solver) {
    this.solver = solver;
  }

  /**
   * Determines the best move using the AlphaBeta algorithm with iterative deepening.
   *
   * @param game The current game state.
   * @param maxDepth The number of moves to look ahead.
   * @param player The current player (true for white, false for black).
   * @return The best move for the player.
   */
  @Override
  public AiMove findBestMove(final Game game, final int maxDepth, final boolean player) {
    stoppedEarly.set(false);
    AiMove bestMove = null;
    final GameAi gameAi = GameAi.fromGame(game);
    final List<Move> rootMoves = new ArrayList<>(game.getBoard().getAllAvailableMoves(player));

    for (int depth = 1; depth <= maxDepth; depth++) {
      if (solver.isSearchStopped()) {
        break;
      }

      if (bestMove != null && bestMove.move() != null) {
        rootMoves.remove(bestMove.move());
        MoveOrdering.moveOrder(rootMoves);
        rootMoves.add(0, bestMove.move());
      }

      final int numThreads = Runtime.getRuntime().availableProcessors() / 2;
      final ExecutorService executor = Executors.newFixedThreadPool(numThreads);
      final List<Future<AiMove>> futures = new CopyOnWriteArrayList<>();

      AiMove currentBest = new AiMove(null, -Float.MAX_VALUE);

      if (!rootMoves.isEmpty()) {
        final Move firstMove = rootMoves.get(0);
        try {
          final GameAi firstGameCopy = gameAi.copy();
          firstGameCopy.playMove(firstMove);
          final AiMove firstResult =
              alphaBeta(
                  firstGameCopy, depth - 1, !player, -Float.MAX_VALUE, Float.MAX_VALUE, player);
          currentBest = new AiMove(firstMove, firstResult.score());
        } catch (IllegalMoveException e) {
          // Illegal move, normal search
        }
      }

      final float initialAlpha = currentBest.score();

      for (int i = 1; i < rootMoves.size(); i++) {
        final Move move = rootMoves.get(i);
        final int currentDepth = depth; // Create a final copy of depth
        futures.add(
            executor.submit(
                () -> {
                  final GameAi gameCopy = gameAi.copy();
                  try {
                    gameCopy.playMove(move);
                    final AiMove result =
                        alphaBeta(
                            gameCopy,
                            currentDepth - 1,
                            !player,
                            initialAlpha,
                            Float.MAX_VALUE,
                            player);
                    return new AiMove(move, result.score());
                  } catch (IllegalMoveException e) {
                    return new AiMove(null, -Float.MAX_VALUE);
                  }
                }));
      }

      for (final Future<AiMove> future : futures) {
        try {
          final AiMove candidate = future.get();
          if (candidate.move() != null) {
            if (currentBest == null || candidate.score() > currentBest.score()) {
              currentBest = candidate;
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      executor.shutdown();

      if (!stoppedEarly.get() && currentBest != null) {
        bestMove = currentBest;
      } else {
        break;
      }
    }

    if (bestMove == null) {
      bestMove = new AiMove(rootMoves.get(0), 0);
    }

    debug(LOGGER, "Best move: " + bestMove);
    long visitedNodes = getVisitedNodes();
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
      final boolean originalPlayer) {
    addNode();
    if (solver.isSearchStopped()) {
      stoppedEarly.set(true);
      return new AiMove(null, currentPlayer == originalPlayer ? -Float.MAX_VALUE : Float.MAX_VALUE);
    }

    if (depth == 0 || game.isOver()) {
      final float evaluation = solver.evaluateBoard(game.getGameState(), originalPlayer);
      return new AiMove(null, evaluation);
    }

    final List<Move> moves = game.getBoard().getAllAvailableMoves(currentPlayer);
    MoveOrdering.moveOrder(moves);

    AiMove bestMove =
        new AiMove(null, currentPlayer == originalPlayer ? -Float.MAX_VALUE : Float.MAX_VALUE);

    for (final Move move : moves) {
      if (solver.isSearchStopped()) {
        stoppedEarly.set(true);
        break;
      }

      try {
        game.playMove(move);
        final AiMove currMove =
            alphaBeta(game, depth - 1, !currentPlayer, alpha, beta, originalPlayer);
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
      } catch (IllegalMoveException e) {
        // Skipping illegal move
      }
    }

    return bestMove;
  }

  @Override
  public String toString() {
    return "Alpha-Beta Iterative Deepening Parallel";
  }
}
