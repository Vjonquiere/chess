package pdp.model.ai.algorithms;

import static pdp.utils.Logging.debug;

import java.util.ArrayList;
import java.util.Comparator;
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
import pdp.model.board.PromoteMove;
import pdp.model.piece.ColoredPiece;
import pdp.utils.Logging;

/**
 * Algorithm of artificial intelligence Alpha beta pruning, with parallelization (threads) and
 * iterative deepening to have more efficient search.
 */
public class AlphaBetaIterativeDeepeningParallel extends SearchAlgorithm {

  /** Solver used for calling the evaluation of the board once depth is reached or time is up. */
  private Solver solver;

  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(Solver.class.getName());

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
  public AlphaBetaIterativeDeepeningParallel(Solver solver) {
    this.solver = solver;
  }

  /**
   * Determines the best move using the AlphaBeta algorithm with iterative deepening.
   *
   * @param game The current game state.
   * @param depth The number of moves to look ahead.
   * @param player The current player (true for white, false for black).
   * @return The best move for the player.
   */
  @Override
  public AiMove findBestMove(Game game, int maxDepth, boolean player) {
    stoppedEarly.set(false);
    AiMove bestMove = null;
    GameAi gameAi = GameAi.fromGame(game);
    List<Move> rootMoves = new ArrayList<>(game.getBoard().getAllAvailableMoves(player));

    for (int depth = 1; depth <= maxDepth; depth++) {
      if (solver.isSearchStopped()) {
        break;
      }

      if (bestMove != null && bestMove.move() != null) {
        rootMoves.remove(bestMove.move());
        rootMoves.add(0, bestMove.move());
      }

      int numThreads = Runtime.getRuntime().availableProcessors() / 2;
      ExecutorService executor = Executors.newFixedThreadPool(numThreads);
      List<Future<AiMove>> futures = new CopyOnWriteArrayList<>();

      for (Move move : rootMoves) {
        final int currentDepth = depth; // Create a final copy of depth
        futures.add(
            executor.submit(
                () -> {
                  GameAi gameCopy = gameAi.copy();
                  try {
                    Move promotedMove = AlgorithmHelpers.promoteMove(move);
                    gameCopy.playMove(promotedMove);
                    AiMove result =
                        alphaBeta(
                            gameCopy,
                            currentDepth - 1,
                            !player,
                            -Float.MAX_VALUE,
                            Float.MAX_VALUE,
                            player);
                    return new AiMove(promotedMove, result.score());
                  } catch (IllegalMoveException e) {
                    return new AiMove(null, player ? -Float.MAX_VALUE : Float.MAX_VALUE);
                  }
                }));
      }

      AiMove currentBest = null;
      for (Future<AiMove> future : futures) {
        try {
          AiMove candidate = future.get();
          if (candidate.move() != null) {
            if (currentBest == null
                || (player && candidate.score() > currentBest.score())
                || (!player && candidate.score() < currentBest.score())) {
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
      GameAi game,
      int depth,
      boolean currentPlayer,
      float alpha,
      float beta,
      boolean originalPlayer) {

    if (solver.isSearchStopped()) {
      stoppedEarly.set(true);
      return new AiMove(null, originalPlayer ? -Float.MAX_VALUE : Float.MAX_VALUE);
    }

    if (depth == 0 || game.isOver()) {
      float evaluation = solver.evaluateBoard(game.getBoard(), originalPlayer);
      return new AiMove(null, evaluation);
    }

    List<Move> moves = game.getBoard().getAllAvailableMoves(currentPlayer);
    sortMoves(moves, game);

    AiMove bestMove =
        new AiMove(null, currentPlayer == originalPlayer ? -Float.MAX_VALUE : Float.MAX_VALUE);

    for (Move move : moves) {
      if (solver.isSearchStopped()) {
        stoppedEarly.set(true);
        break;
      }

      try {
        move = AlgorithmHelpers.promoteMove(move);
        game.playMove(move);
        AiMove currMove = alphaBeta(game, depth - 1, !currentPlayer, alpha, beta, originalPlayer);
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

  /**
   * Sorts the moves list based on the score of each move. The score is determined by the
   * evaluateMove function.
   *
   * @param moves The list of moves to be sorted.
   * @param game The game state.
   */
  private void sortMoves(List<Move> moves, GameAi game) {
    moves.sort(Comparator.comparingInt((Move m) -> -evaluateMove(m, game)));
  }

  /**
   * Evaluates the given move by giving a score based on the captured piece. The score is as
   * follows:
   *
   * <ul>
   *   <li>Pawn: 1
   *   <li>Knights and Bishops: 3
   *   <li>Rooks: 5
   *   <li>Queens: 9
   * </ul>
   *
   * If the move is a promotion, an additional 100 points is given.
   *
   * @param move The move to be evaluated.
   * @param game The current game state.
   * @return The score of the move.
   */
  private int evaluateMove(Move move, GameAi game) {
    ColoredPiece target = game.getBoard().getPieceAt(move.getDest().x(), move.getDest().y());
    int score = 0;

    if (target != null) {
      switch (target.getPiece()) {
        case PAWN:
          score += 1;
          break;
        case KNIGHT:
        case BISHOP:
          score += 3;
          break;
        case ROOK:
          score += 5;
          break;
        case QUEEN:
          score += 9;
          break;
        default:
          break;
      }
    }

    if (move instanceof PromoteMove) {
      score += 100;
    }

    return score;
  }
}
