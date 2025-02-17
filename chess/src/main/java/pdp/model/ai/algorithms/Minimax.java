package pdp.model.ai.algorithms;

import java.util.List;
import pdp.model.Game;
import pdp.model.ai.AIMove;
import pdp.model.ai.Solver;
import pdp.model.board.Move;

public class Minimax implements SearchAlgorithm {
  Solver solver;

  public Minimax(Solver solver) {
    this.solver = solver;
  }

  /**
   * Determines the best move using the Minimax algorithm.
   *
   * @param game The current game state.
   * @param depth The number of moves to look ahead.
   * @param player The current player (true for white, false for black).
   * @return The best move for the player.
   */
  @Override
  public AIMove findBestMove(Game game, int depth, boolean player) {
    return maxMin(game, depth, player);
  }

  /**
   * Evaluates the board state by selecting the move that maximizes the player's score. This
   * represents the maximizing player's turn in the Minimax algorithm.
   *
   * @param game The current game
   * @param depth The number of moves remaining in the search
   * @param player The current player (true for white, false for black).
   * @return The best move with its evaluated score.
   */
  private AIMove maxMin(Game game, int depth, boolean player) {
    if (depth == 0 || game.isOver()) {
      return new AIMove(null, solver.evaluateBoard(game, player));
    }
    AIMove bestMove = new AIMove(null, Integer.MIN_VALUE);
    List<Move> moves = game.getBoard().getBoardRep().getAllAvailableMoves(player);
    for (Move move : moves) {
      game.playMove(move);
      AIMove currMove = minMax(game, depth - 1, !player);
      if (currMove.score() > bestMove.score()) {
        bestMove = new AIMove(move, currMove.score());
      }
      game.previousState();
    }
    return bestMove;
  }

  /**
   * Evaluates the board state by selecting the move that maximizes the player's score. This
   * represents the minimizing player's turn in the Minimax algorithm.
   *
   * @param game The current game
   * @param depth The number of moves remaining in the search
   * @param player The current player (true for white, false for black).
   * @return The best move with its evaluated score.
   */
  private AIMove minMax(Game game, int depth, boolean player) {
    if (depth == 0 || game.isOver()) {
      return new AIMove(null, solver.evaluateBoard(game, player));
    }
    AIMove bestMove = new AIMove(null, Integer.MAX_VALUE);
    List<Move> moves = game.getBoard().getBoardRep().getAllAvailableMoves(player);
    for (Move move : moves) {
      game.playMove(move);
      AIMove currMove = maxMin(game, depth - 1, !player);
      if (currMove.score() < bestMove.score()) {
        bestMove = new AIMove(move, currMove.score());
      }
      game.previousState();
    }
    return bestMove;
  }
}
