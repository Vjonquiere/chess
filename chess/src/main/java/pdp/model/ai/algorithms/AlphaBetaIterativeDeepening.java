package pdp.model.ai.algorithms;

import static pdp.utils.Logging.debug;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import pdp.exceptions.IllegalMoveException;
import pdp.model.Game;
import pdp.model.ai.AiMove;
import pdp.model.ai.Solver;
import pdp.model.board.Board;
import pdp.model.board.Move;
import pdp.model.board.PromoteMove;
import pdp.model.piece.ColoredPiece;
import pdp.utils.Logging;

/**
 * Algorithm of artificial intelligence Alpha beta pruning, with iterative deepening to have more
 * efficient search.
 */
public class AlphaBetaIterativeDeepening implements SearchAlgorithm {
  private Solver solver;
  private static final Logger LOGGER = Logger.getLogger(Solver.class.getName());
  private boolean stoppedEarly = false;

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Initializes the field solver with the one given in parameter.
   *
   * @param solver Solver needed to call the evaluation
   */
  public AlphaBetaIterativeDeepening(Solver solver) {
    this.solver = solver;
  }

  /**
   * Determines the best move using the AlphaBeta algorithm.
   *
   * @param game The current game state.
   * @param maxDepth The number of moves to look ahead.
   * @param player The current player (true for white, false for black).
   * @return The best move for the player.
   */
  @Override
  public AiMove findBestMove(Game game, int maxDepth, boolean player) {

    this.stoppedEarly = false;

    AiMove bestMove = null;
    List<Move> rootMoves =
        new ArrayList<>(game.getBoard().getBoardRep().getAllAvailableMoves(player));

    Board board = game.getBoard();
    rootMoves.addAll(
        game.getBoard()
            .getBoardRep()
            .getSpecialMoves(
                player,
                board.getEnPassantPos(),
                board.isLastMoveDoublePush(),
                board.isWhiteLongCastle(),
                board.isWhiteShortCastle(),
                board.isBlackLongCastle(),
                board.isBlackShortCastle()));

    for (int depth = 1; depth <= maxDepth; depth++) {
      if (solver.isSearchStopped()) {
        break;
      }

      if (bestMove != null && bestMove.move() != null) {
        rootMoves.remove(bestMove.move());
        rootMoves.add(0, bestMove.move());
      }

      AiMove currentBest =
          alphaBeta(game, depth, player, -Float.MAX_VALUE, Float.MAX_VALUE, player, rootMoves);
      if (currentBest != null && !this.stoppedEarly) {
        bestMove = currentBest;
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
      Game game,
      int depth,
      boolean currentPlayer,
      float alpha,
      float beta,
      boolean originalPlayer,
      List<Move> orderedMoves) {

    if (solver.isSearchStopped()) {
      this.stoppedEarly = true;
      return new AiMove(null, originalPlayer ? -Float.MAX_VALUE : Float.MAX_VALUE);
    }
    if (depth == 0 || game.isOver()) {
      float evaluation = solver.evaluateBoard(game.getBoard(), originalPlayer);
      return new AiMove(null, evaluation);
    }

    List<Move> moves = orderedMoves;
    if (moves == null) {
      moves = game.getBoard().getBoardRep().getAllAvailableMoves(currentPlayer);
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
                  board.isBlackShortCastle()));
    }

    if (orderedMoves == null) {
      sortMoves(moves, game);
    }

    AiMove bestMove =
        new AiMove(null, currentPlayer == originalPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE);
    for (Move move : moves) {
      if (solver.isSearchStopped()) {
        this.stoppedEarly = true;
        break;
      }
      try {
        move = AlgorithmHelpers.promoteMove(move);
        game.playMove(move);
        AiMove currMove =
            alphaBeta(game, depth - 1, !currentPlayer, alpha, beta, originalPlayer, null);
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

  private void sortMoves(List<Move> moves, Game game) {
    moves.sort(Comparator.comparingInt((Move m) -> -evaluateMove(m, game)));
  }

  private int evaluateMove(Move move, Game game) {
    ColoredPiece target =
        game.getBoard().getBoardRep().getPieceAt(move.getDest().x(), move.getDest().y());
    int score = 0;
    switch (target.getPiece()) {
      case PAWN:
        score += 1;
        break;
      case KNIGHT:
        score += 3;
        break;
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

    if (move instanceof PromoteMove) {
      score += 100;
    }

    return score;
  }
}
