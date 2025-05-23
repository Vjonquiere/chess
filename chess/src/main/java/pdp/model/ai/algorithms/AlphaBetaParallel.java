package pdp.model.ai.algorithms;

import static pdp.utils.Logging.debug;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import pdp.exceptions.IllegalMoveException;
import pdp.model.Game;
import pdp.model.GameAi;
import pdp.model.ai.AiMove;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.utils.Logging;

/**
 * Algorithm of artificial intelligence Alpha beta pruning, with parallelization (threads) to have
 * more efficient search.
 */
public class AlphaBetaParallel extends AlphaBeta {
  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(AlphaBetaParallel.class.getName());

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Initializes the field solver with the one given in parameter.
   *
   * @param solver Solver needed to call the evaluation
   */
  public AlphaBetaParallel(final Solver solver) {
    super(solver);
  }

  /**
   * Determines the best move using the AlphaBeta algorithm.
   *
   * @param game The current game state.
   * @param depth The number of moves to look ahead.
   * @param player The current player (true for white, false for black).
   * @return The best move for the player.
   */
  @Override
  public AiMove findBestMove(final Game game, final int depth, final boolean player) {
    final GameAi aiGame = GameAi.fromGame(game);
    final int nbThreads = Runtime.getRuntime().availableProcessors() / 2;
    final ExecutorService executor = Executors.newFixedThreadPool(nbThreads);
    final List<Future<AiMove>> futures = new CopyOnWriteArrayList<>();

    final List<Move> moves = aiGame.getBoard().getAllAvailableMoves(player);
    MoveOrdering.moveOrder(moves);

    AiMove bestMove = new AiMove(moves.get(0), -Float.MAX_VALUE);

    if (!moves.isEmpty()) {
      final Move firstMove = moves.get(0);
      try {
        final GameAi firstGameCopy = aiGame.copy();
        firstGameCopy.playMove(firstMove);
        final AiMove firstResult =
            alphaBeta(firstGameCopy, depth - 1, !player, -Float.MAX_VALUE, Float.MAX_VALUE, player);
        bestMove = new AiMove(firstMove, firstResult.score());
      } catch (IllegalMoveException ignored) {
        // Illegal move, normal search
      }

      final float initialAlpha = bestMove.score();

      for (int i = 1; i < moves.size(); i++) {
        final Move move = moves.get(i);
        futures.add(
            executor.submit(
                () -> {
                  final GameAi gameCopy = aiGame.copy();
                  try {
                    gameCopy.playMove(move);
                    final AiMove result =
                        alphaBeta(
                            gameCopy, depth - 1, !player, initialAlpha, Float.MAX_VALUE, player);
                    return new AiMove(move, result.score());
                  } catch (IllegalMoveException e) {
                    return new AiMove(null, -Float.MAX_VALUE);
                  }
                }));
      }
    }

    try {
      for (final Future<AiMove> future : futures) {
        final AiMove candidateMove = future.get();
        if (candidateMove.move() != null && candidateMove.score() > bestMove.score()) {
          bestMove = candidateMove;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    executor.shutdown();
    debug(LOGGER, "Best move: " + bestMove);
    final long visitedNodes = getVisitedNodes();
    clearNode();
    debug(LOGGER, "This search: " + visitedNodes + ", mean: " + getMean());
    return bestMove;
  }

  @Override
  public String toString() {
    return "Alpha-Beta Parallel";
  }
}
