package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.Board;
import pdp.model.board.BoardRepresentation;
import pdp.utils.Position;

/** Heuristic based on the connection of pawns. The more connected the pawns are, the better. */
public class PawnChainHeuristic implements Heuristic {

  /**
   * Computes a score according to how strong pawns are connected. Heuristic used for endgames.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a score depending on the solidity of the pawn chains
   */
  @Override
  public float evaluate(Board board, boolean isWhite) {
    int score = 0;
    score += evaluatePawnChains(board, true) - evaluatePawnChains(board, false);
    return isWhite ? score : -score;
  }

  /**
   * Rewards pawn chains.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a positive score for pawn chains, 0 otherwise
   */
  private int evaluatePawnChains(Board board, boolean isWhite) {
    int reward = 5;
    int score = 0;
    BoardRepresentation bitboard = board.getBoardRep();
    List<Position> pawns = bitboard.getPawns(isWhite);

    for (Position pawn : pawns) {
      for (Position otherPawn : pawns) {
        if ((Math.abs(otherPawn.x() - pawn.x()) == 1 && Math.abs(otherPawn.y() - (pawn.y())) == 1)
            || (otherPawn.y() == pawn.y() && Math.abs(otherPawn.x() - pawn.x()) == 1)) {
          // Connected pawn
          score += reward;
        }
      }
    }
    return score;
  }
}
