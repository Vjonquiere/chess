package pdp.model.ai.algorithms;

import java.util.List;
import pdp.model.Game;
import pdp.model.GameAi;
import pdp.model.ai.AiMove;
import pdp.model.ai.Solver;
import pdp.model.board.Move;

/** Algorithm of artificial intelligence Minimax. */
public class Minimax implements SearchAlgorithm {
  /** Solver used for calling the evaluation of the board once depth is reached or time is up. */
  private final Solver solver;

  /**
   * Initializes the field solver with the one given in parameter.
   *
   * @param solver Solver needed to call the evaluation
   */
  public Minimax(final Solver solver) {
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
  public AiMove findBestMove(final Game game, final int depth, final boolean player) {
    final GameAi aiGame = GameAi.fromGame(game);
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
  private AiMove minimax(
      final GameAi game,
      final int depth,
      final boolean currentPlayer,
      final boolean originalPlayer) {
    if (solver.isSearchStopped()) {
      final boolean isMinimizing = currentPlayer != originalPlayer;
      return new AiMove(null, isMinimizing ? Integer.MAX_VALUE : Integer.MIN_VALUE);
    }
    if (depth == 0 || game.isOver()) {
      final float evaluation = solver.evaluateBoard(game.getBoard(), originalPlayer);
      return new AiMove(null, evaluation);
    }

    final boolean isMinimizing = currentPlayer != originalPlayer;
    AiMove bestMove = new AiMove(null, isMinimizing ? Integer.MAX_VALUE : Integer.MIN_VALUE);
    final List<Move> moves = game.getBoard().getBoardRep().getAllAvailableMoves(currentPlayer);
    for (Move move : moves) {
      if (solver.isSearchStopped()) {
        break;
      }
      try {
        move = AlgorithmHelpers.promoteMove(move);
        game.playMove(move);
        final AiMove currMove = minimax(game, depth - 1, !currentPlayer, originalPlayer);
        game.previousState();

        if (isMinimizing) {
          if (currMove.score() < bestMove.score()) {
            bestMove = new AiMove(move, currMove.score());
          }
        } else {
          if (currMove.score() > bestMove.score()) {
            bestMove = new AiMove(move, currMove.score());
          }
        }
      } catch (Exception ignored) {
        // Handle illegal move
      }
    }
    return bestMove;
  }
}
