package pdp.model.board;

import pdp.model.piece.ColoredPiece;
import pdp.utils.Position;

/** Special move representation for en passant moves. */
public class EnPassantMove extends Move {

  /**
   * Constructs a new PromoteMove object with the specified source, destination and promoted piece.
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   */
  public EnPassantMove(
      final Position source,
      final Position dest,
      final ColoredPiece piece,
      final Position takeDest,
      final ColoredPiece takenPiece) {
    this(source, dest, piece, takeDest, takenPiece, false, false);
  }

  /**
   * Constructs a new PromoteMove object with the specified source and destination positions, the
   * promoted piece, the piece being moved, a flag indicating whether the move is a capture, the
   * captured piece (if any), and flags for check and checkmate.
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   * @param piece The ColoredPiece being moved.
   * @param takenPiece The ColoredPiece that was captured, or null if no piece was captured.
   * @param isCheck A boolean indicating whether the move results in a check (true if it's a check,
   *     false otherwise).
   * @param isCheckMate A boolean indicating whether the move results in a checkmate (true if it's a
   *     checkmate, false otherwise).
   */
  public EnPassantMove(
      final Position source,
      final Position dest,
      final ColoredPiece piece,
      final Position takeDest,
      final ColoredPiece takenPiece,
      final boolean isCheck,
      final boolean isCheckMate) {
    super(source, dest, piece, true, takenPiece, takeDest, isCheck, isCheckMate);
  }

  /**
   * Converts the move to its algebraic notation.
   *
   * @return The algebraic string representation.
   */
  @Override
  public String toAlgebraicString() {
    final String sourceStr = positionToString(this.getSource());
    final String destinationStr = positionToString(this.getDest());
    final String separator = "x";
    final String annotation = this.isCheckMate() ? "#" : (this.isCheck() ? "+" : "");

    return sourceStr + separator + destinationStr + annotation;
  }

  @Override
  public String toUciString() {
    return super.toUciString();
  }

  /**
   * Converts the move to a string representation of type "e7-e8=Q".
   *
   * @return The string representation of the move.
   */
  @Override
  public String toString() {
    return this.toAlgebraicString();
  }
}
