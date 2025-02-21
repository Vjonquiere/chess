package pdp.model.ai.algorithms;

import java.util.List;
import pdp.exceptions.IllegalMoveException;
import pdp.model.Game;
import pdp.model.ai.AIMove;
import pdp.model.ai.Solver;
import pdp.model.board.Move;

public class AlphaBeta implements SearchAlgorithm {
  Solver solver;

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
    return alphaBeta(game, depth, player, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
  }

  /**
   * Finds the best move for the given player. It cuts the uninteresting branches with the
   * AlphaBetaPruning.
   *
   * <p>The method evaluates recursively the game state to select the optimal move.
   *
   * @param game The current game
   * @param depth The number of moves remaining in the search
   * @param player The current player (true for white, false for black).
   * @param alpha The best option for the maximizing player
   * @param beta The best option for the minimizing player
   * @param isMinimizing True if the current level of recursion minimizes the score, false if
   *     maximizing.
   * @return The best move with its evaluated score.
   */
  private AIMove alphaBeta(
      Game game, int depth, boolean player, int alpha, int beta, boolean isMinimizing) {
    if (depth == 0 || game.isOver()) {
      return new AIMove(null, solver.evaluateBoard(game.getBoard(), !player));
    }
    AIMove bestMove = new AIMove(null, isMinimizing ? Integer.MAX_VALUE : Integer.MIN_VALUE);
    List<Move> moves = game.getBoard().getBoardRep().getAllAvailableMoves(player);
    for (Move move : moves) {
      try {
        move = AlgorithmHelpers.promoteMove(move);
        game.playMove(move);
        AIMove currMove = alphaBeta(game, depth - 1, !player, alpha, beta, !isMinimizing);
        game.previousState();
        if (isMinimizing) {
          if (currMove.score() < bestMove.score()) {
            bestMove = new AIMove(move, currMove.score());
          }
          beta = Math.min(beta, currMove.score());
        } else {
          if (currMove.score() > bestMove.score()) {
            bestMove = new AIMove(move, currMove.score());
          }
          alpha = Math.max(alpha, currMove.score());
        }
        if (alpha >= beta) {
          break;
        }
      } catch (IllegalMoveException e) {
        // illegal move caught
      }
    }
    return bestMove;
  }
}
