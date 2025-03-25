package pdp.model.ai.algorithms;

import static pdp.utils.Logging.DEBUG;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import pdp.exceptions.IllegalMoveException;
import pdp.model.Game;
import pdp.model.GameAi;
import pdp.model.ai.AIMove;
import pdp.model.ai.Solver;
import pdp.model.board.Board;
import pdp.model.board.Move;
import pdp.utils.Logging;

public class AlphaBetaParallel implements SearchAlgorithm {
  private Solver solver;
  private static final Logger LOGGER = Logger.getLogger(Solver.class.getName());

  static {
    Logging.configureLogging(LOGGER);
  }

  public AlphaBetaParallel(Solver solver) {
    this.solver = solver;
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
  public AIMove findBestMove(Game game, int depth, boolean player) {
    GameAi aiGame = GameAi.fromGame(game);
    int nbThreads = Runtime.getRuntime().availableProcessors() / 2;
    ExecutorService executor = Executors.newFixedThreadPool(nbThreads);
    List<Future<AIMove>> futures = new CopyOnWriteArrayList<>();

    List<Move> moves = aiGame.getBoard().getBoardRep().getAllAvailableMoves(player);
    Board board = aiGame.getBoard();
    moves.addAll(
        aiGame
            .getBoard()
            .getBoardRep()
            .getSpecialMoves(
                player,
                board.getEnPassantPos(),
                board.isLastMoveDoublePush(),
                board.isWhiteLongCastle(),
                board.isWhiteShortCastle(),
                board.isBlackLongCastle(),
                board.isBlackShortCastle()));

    for (Move move : moves) {
      futures.add(
          executor.submit(
              () -> {
                GameAi gameCopy = aiGame.copy();
                try {
                  Move promoteMove = AlgorithmHelpers.promoteMove(move);
                  gameCopy.playMove(promoteMove);
                  AIMove result =
                      alphaBeta(
                          gameCopy, depth - 1, !player, -Float.MAX_VALUE, Float.MAX_VALUE, player);
                  return new AIMove(promoteMove, result.score());
                } catch (IllegalMoveException e) {
                  return new AIMove(null, -Float.MAX_VALUE);
                }
              }));
    }

    AIMove bestMove = new AIMove(null, -Float.MAX_VALUE);

    try {
      for (Future<AIMove> future : futures) {
        AIMove candidateMove = future.get();
        if (candidateMove.move() != null) {
          if (candidateMove.score() > bestMove.score()) {
            bestMove = candidateMove;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    executor.shutdown();
    DEBUG(LOGGER, "Best move: " + bestMove);
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
  private AIMove alphaBeta(
      GameAi game,
      int depth,
      boolean currentPlayer,
      float alpha,
      float beta,
      boolean originalPlayer) {
    if (solver.isSearchStopped()) {
      return new AIMove(null, originalPlayer ? -Float.MAX_VALUE : Float.MAX_VALUE);
    }
    if (depth == 0 || game.isOver()) {
      float evaluation = solver.evaluateBoard(game.getBoard(), originalPlayer);
      return new AIMove(null, evaluation);
    }

    AIMove bestMove =
        new AIMove(null, currentPlayer == originalPlayer ? -Float.MAX_VALUE : Float.MAX_VALUE);
    List<Move> moves = game.getBoard().getBoardRep().getAllAvailableMoves(currentPlayer);
    Board board = game.getBoard();
    moves.addAll(
        board
            .getBoardRep()
            .getSpecialMoves(
                currentPlayer,
                board.getEnPassantPos(),
                board.isLastMoveDoublePush(),
                board.isWhiteLongCastle(),
                board.isWhiteShortCastle(),
                board.isBlackLongCastle(),
                board.isBlackShortCastle()));

    for (Move move : moves) {
      if (solver.isSearchStopped()) {
        break;
      }
      try {
        move = AlgorithmHelpers.promoteMove(move);
        game.playMove(move);
        AIMove currMove = alphaBeta(game, depth - 1, !currentPlayer, alpha, beta, originalPlayer);

        game.previousState();

        if (currentPlayer == originalPlayer) { // Maximizing
          if (currMove.score() > bestMove.score() || bestMove.move() == null) {
            bestMove = new AIMove(move, currMove.score());
          }
          alpha = Math.max(alpha, bestMove.score());
        } else { // Minimizing
          if (currMove.score() < bestMove.score() || bestMove.move() == null) {
            bestMove = new AIMove(move, currMove.score());
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
}
