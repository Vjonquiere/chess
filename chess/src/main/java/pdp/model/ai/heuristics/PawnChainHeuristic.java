package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.BoardRepresentation;
import pdp.utils.Position;

/** Heuristic based on the connection of pawns. The more connected the pawns are, the better. */
public class PawnChainHeuristic implements Heuristic {

  /** Score cap for the heuristic (absolute value cap). */
  private static final float SCORE_CAP = 100f;

  /** Bonus score added when pawns are connected. */
  private static final float REWARD = 5f;

  /** The multiplier used to keep the values under SCORE_CAP. */
  private static final float MULTIPLIER = (SCORE_CAP / (28 * REWARD));

  /**
   * Computes a score according to how strong pawns are connected. Heuristic used for endgames.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score depending on the solidity of the pawn chains
   */
  @Override
  public float evaluate(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;
    score += evaluatePawnChains(board, true) - evaluatePawnChains(board, false);
    score *= MULTIPLIER;
    return isWhite ? score : -score;
  }

  /**
   * Rewards pawn chains.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a positive score for pawn chains, 0 otherwise
   */
  private float evaluatePawnChains(final BoardRepresentation board, final boolean isWhite) {
    float score = 0;
    final List<Position> pawns = board.getPawns(isWhite);

    for (final Position pawn : pawns) {
      for (final Position otherPawn : pawns) {
        if ((Math.abs(otherPawn.x() - pawn.x()) == 1 && Math.abs(otherPawn.y() - (pawn.y())) == 1)
            || (otherPawn.y() == pawn.y() && Math.abs(otherPawn.x() - pawn.x()) == 1)) {
          // Connected pawn
          score += REWARD;
        }
      }
    }

    return score;
  }
}
