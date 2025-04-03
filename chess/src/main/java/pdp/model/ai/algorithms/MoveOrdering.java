package pdp.model.ai.algorithms;

import java.util.Comparator;
import java.util.List;
import pdp.model.board.Move;
import pdp.model.board.PromoteMove;
import pdp.model.piece.ColoredPiece;

/** An algorithm to order moves on several parameters to maximise the Alpha-Beta cuts. */
public class MoveOrdering {

  /** Comparison function for moves. */
  public static Comparator<Move> moveOrderingComparator =
      (m1, m2) -> {
        final int captureComparison = Integer.compare(getMVVLVA(m2), getMVVLVA(m1));
        if (captureComparison != 0) {
          return captureComparison;
        }

        final int promotionComparison =
            Boolean.compare(m2 instanceof PromoteMove, m1 instanceof PromoteMove);
        if (promotionComparison != 0) {
          return promotionComparison;
        }

        final int checkComparison = Boolean.compare(m2.isCheck(), m1.isCheck());
        if (checkComparison != 0) {
          return checkComparison;
        }

        return 0;
      };

  /**
   * Order the given moves by executing the sort algorithm.
   *
   * @param moves A list of moves to sort.
   * @return The sorted moves.
   */
  public static List<Move> moveOrder(final List<Move> moves) {
    moves.sort(moveOrderingComparator);
    return moves;
  }

  /**
   * Get the value of a piece.
   *
   * @param piece The piece to get the value.
   * @return The value of the piece.
   */
  public static int getValue(final ColoredPiece piece) {
    return switch (piece.getPiece()) {
      case PAWN -> 1;
      case ROOK -> 5;
      case BISHOP, KNIGHT -> 3;
      case QUEEN -> 9;
      case KING -> 1000;
      case EMPTY -> 0;
    };
  }

  /**
   * Compute the MVVLVA score of a move.
   *
   * @param move The move to get the score.
   * @return The score corresponding to the move.
   */
  public static int getMVVLVA(final Move move) {
    if (move.getPieceTaken() == null) {
      return 0;
    }
    return (getValue(move.getPieceTaken()) * 10) - getValue(move.getPiece());
  }
}
