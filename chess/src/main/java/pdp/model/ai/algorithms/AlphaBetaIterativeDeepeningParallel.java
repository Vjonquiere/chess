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

public class AlphaBetaIterativeDeepeningParallel implements SearchAlgorithm {
  private Solver solver;
  private static final Logger LOGGER = Logger.getLogger(Solver.class.getName());
  private AtomicBoolean stoppedEarly = new AtomicBoolean(false);

  static {
    Logging.configureLogging(LOGGER);
  }

  public AlphaBetaIterativeDeepeningParallel(Solver solver) {
    this.solver = solver;
  }

  @Override
  public AiMove findBestMove(Game game, int maxDepth, boolean player) {
    stoppedEarly.set(false);
    AiMove bestMove = null;
    GameAi gameAi = GameAi.fromGame(game);
    List<Move> rootMoves =
        new ArrayList<>(game.getBoard().getBoardRep().getAllAvailableMoves(player));

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

    List<Move> moves = game.getBoard().getBoardRep().getAllAvailableMoves(currentPlayer);
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

  private void sortMoves(List<Move> moves, GameAi game) {
    moves.sort(Comparator.comparingInt((Move m) -> -evaluateMove(m, game)));
  }

  private int evaluateMove(Move move, GameAi game) {
    ColoredPiece target =
        game.getBoard().getBoardRep().getPieceAt(move.getDest().x(), move.getDest().y());
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
