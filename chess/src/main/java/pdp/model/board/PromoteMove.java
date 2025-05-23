package pdp.model.board;

import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

/** Special move representation for pawn promotion. */
public class PromoteMove extends Move {
  /** Piece for the pawn to be promoted to. */
  private final Piece promPiece;

  /**
   * Constructs a new PromoteMove object with the specified source, destination and promoted piece.
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   * @param promPiece The piece that this move promotes to.
   */
  public PromoteMove(final Position source, final Position dest, final Piece promPiece) {
    this(source, dest, promPiece, null, false, null);
  }

  /**
   * Constructs a new PromoteMove object with the specified source, destination, promoted piece,
   * piece to promote, boolean to indicate if the move is a capture and the piece taken and .
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   * @param promPiece The piece that this move promotes to.
   * @param piece Piece making the move.
   * @param isTake True if a piece ius taken during the move.
   * @param takenPiece Piece taken during the move
   */
  public PromoteMove(
      final Position source,
      final Position dest,
      final Piece promPiece,
      final ColoredPiece piece,
      final boolean isTake,
      final ColoredPiece takenPiece) {
    super(source, dest, piece, isTake, takenPiece);
    this.promPiece = promPiece;
  }

  /**
   * Constructs a new PromoteMove object with the specified source and destination positions, the
   * promoted piece, the piece being moved, a flag indicating whether the move is a capture, the
   * captured piece (if any), and flags for check and checkmate.
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   * @param piece The ColoredPiece being moved.
   * @param isTake A boolean indicating whether the move is a capture (true if it's a capture, false
   *     otherwise).
   * @param takenPiece The ColoredPiece that was captured, or null if no piece was captured.
   * @param isCheck A boolean indicating whether the move results in a check (true if it's a check,
   *     false otherwise).
   * @param isCheckMate A boolean indicating whether the move results in a checkmate (true if it's a
   *     checkmate, false otherwise).
   */
  public PromoteMove(
      final Position source,
      final Position dest,
      final Piece promPiece,
      final ColoredPiece piece,
      final boolean isTake,
      final ColoredPiece takenPiece,
      final boolean isCheck,
      final boolean isCheckMate) {
    super(source, dest, piece, isTake, takenPiece, isCheck, isCheckMate);
    this.promPiece = promPiece;
  }

  /**
   * Gets the piece that this move promotes to.
   *
   * @return The promoted piece.
   */
  public Piece getPromPiece() {
    return promPiece;
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
    final String separator = this.isTake() ? "x" : "-";
    final String annotation = this.isCheckMate() ? "#" : this.isCheck() ? "+" : "";

    return sourceStr
        + separator
        + destinationStr
        + "="
        + this.promPiece.getCharRepresentation(true)
        + annotation;
  }

  @Override
  public String toUciString() {
    return super.toUciString() + this.promPiece.getCharRepresentation(true);
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
