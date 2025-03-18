package pdp.model.ai.algorithms;

import static pdp.utils.Logging.DEBUG;

import java.util.List;
import java.util.logging.Logger;
import pdp.exceptions.IllegalMoveException;
import pdp.model.Game;
import pdp.model.ai.AIMove;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.utils.Logging;

public class AlphaBeta implements SearchAlgorithm {
  Solver solver;
  private static final Logger LOGGER = Logger.getLogger(Solver.class.getName());

  static {
    Logging.configureLogging(LOGGER);
  }

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
  public AIMove findBestMove(Game game, int depth, boolean player) {
    AIMove bestMove = alphaBeta(game, depth, player, Integer.MIN_VALUE, Integer.MAX_VALUE, player);
    DEBUG(LOGGER, "Best move: " + bestMove);
    System.out.println(
        "Positions Calculated for player "
            + (player ? "White " : "Black ")
            + this.solver.positionsCalculated);
    System.out.println(
        "Positions from saved for player "
            + (player ? "White " : "Black ")
            + this.solver.positionsFromEvaluated);
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
      Game game, int depth, boolean currentPlayer, int alpha, int beta, boolean originalPlayer) {

    if (solver.isSearchStopped()) {
      return new AIMove(null, originalPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE);
    }
    if (depth == 0 || game.isOver()) {
      int evaluation = solver.evaluateBoard(game.getBoard(), originalPlayer);
      return new AIMove(null, evaluation);
    }
    AIMove bestMove =
        new AIMove(null, currentPlayer == originalPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE);
    List<Move> moves = game.getBoard().getBoardRep().getAllAvailableMoves(currentPlayer);
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
          if (currMove.score() > bestMove.score()) {
            bestMove = new AIMove(move, currMove.score());
          }
          alpha = Math.max(alpha, bestMove.score());
        } else { // Minimizing
          if (currMove.score() < bestMove.score()) {
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
