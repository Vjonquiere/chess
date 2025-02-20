package pdp.model.ai.algorithms;

import java.util.List;
import pdp.exceptions.IllegalMoveException;
import pdp.model.Game;
import pdp.model.ai.AIMove;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.model.board.PromoteMove;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;

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
    return maxValue(game, depth, player, Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  /**
   * Evaluates the board state by selecting the move that maximizes the player's score. It cuts the
   * uninteresting branches with the AlphaBetaPruning .
   *
   * @param game The current game
   * @param depth The number of moves remaining in the search
   * @param player The current player (true for white, false for black).
   * @param alpha The best option for the maximizing player
   * @param beta The best option for the minimizing player
   * @return The best move with its evaluated score.
   */
  private AIMove maxValue(Game game, int depth, boolean player, int alpha, int beta) {
    if (depth == 0 || game.isOver()) {
      return new AIMove(null, solver.evaluateBoard(game.getBoard(), !player));
    }
    AIMove bestMove = new AIMove(null, Integer.MIN_VALUE);
    List<Move> moves = game.getBoard().getBoardRep().getAllAvailableMoves(player);
    for (Move move : moves) {
      try {
        move = promoteMove(move);
        game.playMove(move);
        AIMove currMove = minValue(game, depth - 1, !player, alpha, beta);
        game.previousState();
        if (currMove.score() > bestMove.score()) {
          bestMove = new AIMove(move, currMove.score());
        }
        alpha = Math.max(alpha, currMove.score());
        if (alpha >= beta) {
          break;
        }
      } catch (IllegalMoveException e) {
        // illegal move caught
      }
    }
    return bestMove;
  }

  /**
   * Evaluates the board state by selecting the move that minimizes the player's score. It cuts the
   * uninteresting branches with the AlphaBetaPruning .
   *
   * @param game The current game
   * @param depth The number of moves remaining in the search
   * @param player The current player (true for white, false for black).
   * @param alpha The best option for the maximizing player
   * @param beta The best option for the minimizing player
   * @return The best move with its evaluated score.
   */
  private AIMove minValue(Game game, int depth, boolean player, int alpha, int beta) {
    if (depth == 0 || game.isOver()) {
      return new AIMove(null, solver.evaluateBoard(game.getBoard(), !player));
    }
    AIMove bestMove = new AIMove(null, Integer.MAX_VALUE);
    List<Move> moves = game.getBoard().getBoardRep().getAllAvailableMoves(player);
    for (Move move : moves) {
      try {
        move = promoteMove(move);
        game.playMove(move);
        AIMove currMove = maxValue(game, depth - 1, !player, alpha, beta);
        game.previousState();
        if (currMove.score() < bestMove.score()) {
          bestMove = new AIMove(move, currMove.score());
        }
        beta = Math.min(beta, currMove.score());
        if (alpha >= beta) {
          break;
        }
      } catch (IllegalMoveException e) {
        // illegal move caught
      }
    }
    return bestMove;
  }

  private Move promoteMove(Move move) {
    ColoredPiece piece = move.getPiece();
    if (piece.piece == Piece.PAWN && piece.color == Color.BLACK && move.dest.getY() == 0) {
      move = new PromoteMove(move.source, move.dest, Piece.QUEEN);
    }
    if (piece.piece == Piece.PAWN && piece.color == Color.WHITE && move.dest.getY() == 7) {
      move = new PromoteMove(move.source, move.dest, Piece.QUEEN);
    }
    return move;
  }
}
