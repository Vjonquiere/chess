package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.Board;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.utils.Position;

public class BishopEndgameHeuristic implements Heuristic {

  /**
   * Computes a score according to how performant bishops are for an endgame position. Heuristic
   * used for endgames.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score depending on the progress of the pawns
   */
  @Override
  public int evaluate(Board board, boolean isWhite) {
    int score = 0;
    score += evaluateBishopMobility(board, true) - evaluateBishopMobility(board, false);
    score +=
        evaluateSameColorBishopsSamePlayer(board, true)
            - evaluateSameColorBishopsSamePlayer(board, false);
    score +=
        evaluateSameColorBishopsOpponent(board, true)
            - evaluateSameColorBishopsOpponent(board, false);
    score += evaluateCentralization(board, true) - evaluateCentralization(board, false);
    score += evaluateBadBishop(board, true) - evaluateBadBishop(board, false);
    return isWhite ? score : -score;
  }

  /**
   * Increase score for bishops that have more activity
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return score based on how many moves bishops can make
   */
  private int evaluateBishopMobility(Board board, boolean isWhite) {
    int score = 0;
    BoardRepresentation bitboard = board.getBoardRep();
    int scoreForEachMove = 2;
    List<Move> bishopMoves = bitboard.retrieveBishopMoves(isWhite);

    score += bishopMoves.size() * scoreForEachMove;
    return score;
  }

  /**
   * Penalizes having two bishops on the same color squares (bad in endgames).
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return Negative score if bishops are on the same color
   */
  private int evaluateSameColorBishopsSamePlayer(Board board, boolean isWhite) {
    List<Position> bishops = board.getBoardRep().getBishops(isWhite);
    if (bishops.size() < 2) {
      return 0;
    }

    boolean sameColorBishops = false;
    if (bishops.size() > 0) {
      sameColorBishops =
          (bishops.get(0).getX() + bishops.get(0).getY()) % 2
              == (bishops.get(1).getX() + bishops.get(1).getY()) % 2;
    }

    if (sameColorBishops) {
      return -10;
    } else {
      return 0;
    }
  }

  /**
   * Increases score having a bishop on the same color squares as the bishop's opponent
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return Positive score if bishops are on the same color, 0 otherwise
   */
  private int evaluateSameColorBishopsOpponent(Board board, boolean isWhite) {
    List<Position> bishopsPlayer1 = board.getBoardRep().getBishops(isWhite);
    List<Position> bishopsPlayer2 = board.getBoardRep().getBishops(!isWhite);
    if (bishopsPlayer1.size() > 1 || bishopsPlayer2.size() > 1) {
      return 0;
    }

    boolean sameColorBishops = false;
    if (!bishopsPlayer1.isEmpty() && !bishopsPlayer2.isEmpty()) {
      sameColorBishops =
          (bishopsPlayer1.get(0).getX() + bishopsPlayer1.get(0).getY()) % 2
              == (bishopsPlayer2.get(0).getX() + bishopsPlayer2.get(0).getY()) % 2;
    }

    if (sameColorBishops) {
      return 5;
    } else {
      return 0;
    }
  }

  /**
   * Increase score for bishops that are centralized since they control more of the board
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return score for bishops that are close to the center
   */
  private int evaluateCentralization(Board board, boolean isWhite) {
    int score = 0;
    List<Position> bishops = board.getBoardRep().getBishops(isWhite);
    int noBonus = 0;

    for (Position bishop : bishops) {
      int fromCenter = Math.abs(bishop.getX() - 3) + Math.abs(bishop.getY() - 3);
      score += Math.max(noBonus, 10 - (fromCenter * 2));
    }

    return score;
  }

  /**
   * Penalizes bishops that are stuck behind their own pawns (bad bishops)
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return Negative score if bishop is blocked by pawns
   */
  private int evaluateBadBishop(Board board, boolean isWhite) {
    int score = 0;
    List<Position> bishops = board.getBoardRep().getBishops(isWhite);
    List<Position> pawns = board.getBoardRep().getPawns(isWhite);

    for (Position bishop : bishops) {
      for (Position pawn : pawns) {
        if ((bishop.getX() + bishop.getY()) % 2 == (pawn.getX() + pawn.getY()) % 2) {
          score -= 5;
        }
      }
    }

    return score;
  }
}
