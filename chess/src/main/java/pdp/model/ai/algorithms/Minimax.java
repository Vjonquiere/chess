package pdp.model.ai.algorithms;

import java.util.List;
import pdp.model.Game;
import pdp.model.GameAi;
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
    GameAi aiGame = GameAi.fromGame(game);
    return minimax(aiGame, depth, player, player);
  }

  /**
   * Finds the best move for the given player with the Minimax Algorithm.
   *
   * <p>The method evaluates recursively the game state to select the optimal move.
   *
   * @param game The current game
   * @param depth The number of moves remaining in the search
   * @param originalPlayer The original player
   * @param currentPlayer The current player (true for white, false for black).
   * @return The best move with its evaluated score.
   */
  private AIMove minimax(GameAi game, int depth, boolean currentPlayer, boolean originalPlayer) {
    if (solver.isSearchStopped()) {
      boolean isMinimizing = (currentPlayer != originalPlayer);
      return new AIMove(null, isMinimizing ? Integer.MAX_VALUE : Integer.MIN_VALUE);
    }
    if (depth == 0 || game.isOver()) {
      int evaluation = solver.evaluateBoard(game.getBoard(), originalPlayer);
      return new AIMove(null, evaluation);
    }

    boolean isMinimizing = (currentPlayer != originalPlayer);
    AIMove bestMove = new AIMove(null, isMinimizing ? Integer.MAX_VALUE : Integer.MIN_VALUE);
    List<Move> moves = game.getBoard().getBoardRep().getAllAvailableMoves(currentPlayer);
    for (Move move : moves) {
      if (solver.isSearchStopped()) {
        break;
      }
      try {
        move = AlgorithmHelpers.promoteMove(move);
        game.playMove(move);
        AIMove currMove = minimax(game, depth - 1, !currentPlayer, originalPlayer);
        game.previousState();

        if (isMinimizing) {
          if (currMove.score() < bestMove.score()) {
            bestMove = new AIMove(move, currMove.score());
          }
        } else {
          if (currMove.score() > bestMove.score()) {
            bestMove = new AIMove(move, currMove.score());
          }
        }
      } catch (Exception e) {
        // Handle illegal move
      }
    }
    return bestMove;
  }
}
