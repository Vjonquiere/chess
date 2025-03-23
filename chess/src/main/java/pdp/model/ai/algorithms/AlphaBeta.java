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
import pdp.model.board.Board;
import pdp.model.board.Move;
import pdp.utils.Logging;

/** Algorithm of artificial intelligence Alpha beta pruning. */
public class AlphaBeta implements SearchAlgorithm {
  private final Solver solver;
  private static final Logger LOGGER = Logger.getLogger(Solver.class.getName());

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Initializes the field solver with the one given in parameter.
   *
   * @param solver Solver needed to call the evaluation
   */
  public AlphaBeta(Solver solver) {
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
  public AiMove findBestMove(Game game, int depth, boolean player) {
    GameAi aiGame = GameAi.fromGame(game);
    ExecutorService executor =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    List<Future<AiMove>> futures = new CopyOnWriteArrayList<>();

    futures.add(
        executor.submit(
            () -> {
              GameAi gameCopy = aiGame.copy();
              return alphaBeta(
                  gameCopy, depth, player, Integer.MIN_VALUE, Integer.MAX_VALUE, player);
            }));

    AiMove bestMove = null;
    try {
      bestMove = futures.get(0).get();
    } catch (Exception e) {
      e.printStackTrace();
    }

    executor.shutdown();

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
      return new AiMove(null, originalPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE);
    }
    if (depth == 0 || game.isOver()) {
      float evaluation = solver.evaluateBoard(game.getBoard(), originalPlayer);
      return new AiMove(null, evaluation);
    }
    AiMove bestMove =
        new AiMove(null, currentPlayer == originalPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE);
    List<Move> moves = game.getBoard().getBoardRep().getAllAvailableMoves(currentPlayer);
    Board board = game.getBoard();
    moves.addAll(
        game.getBoard()
            .getBoardRep()
            .getSpecialMoves(
                currentPlayer,
                board.getEnPassantPos(),
                board.isLastMoveDoublePush(),
                board.isWhiteLongCastle(),
                board.isWhiteShortCastle(),
                board.isBlackLongCastle(),
                board.isWhiteLongCastle()));
    for (Move move : moves) {
      if (solver.isSearchStopped()) {
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
}
