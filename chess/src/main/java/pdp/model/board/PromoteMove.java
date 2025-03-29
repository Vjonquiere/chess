package pdp.model.board;

import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

/** Special move representation for pawn promotion. */
public class PromoteMove extends Move {
  private Piece promPiece;

  /**
   * Constructs a new PromoteMove object with the specified source, destination and promoted piece.
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   * @param promPiece The piece that this move promotes to.
   */
  public PromoteMove(Position source, Position dest, Piece promPiece) {
    this(source, dest, promPiece, null, false, null);
  }

  public PromoteMove(
      Position source,
      Position dest,
      Piece promPiece,
      ColoredPiece piece,
      boolean isTake,
      ColoredPiece takenPiece) {
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
      Position source,
      Position dest,
      Piece promPiece,
      ColoredPiece piece,
      boolean isTake,
      ColoredPiece takenPiece,
      boolean isCheck,
      boolean isCheckMate) {
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
    String sourceStr = positionToString(this.getSource());
    String destinationStr = positionToString(this.getDest());
    String separator = this.isTake() ? "x" : "-";
    String annotation = this.isCheckMate() ? "#" : (this.isCheck() ? "+" : "");

    return sourceStr
        + separator
        + destinationStr
        + "="
        + this.promPiece.getCharRepresentation(true)
        + annotation;
  }

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
    return super.toString() + "=" + this.promPiece.getCharRepresentation(true);
  }
}
