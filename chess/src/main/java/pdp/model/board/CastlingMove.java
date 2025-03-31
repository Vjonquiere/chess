package pdp.model.board;

import pdp.model.piece.ColoredPiece;
import pdp.utils.Position;

/** Special move representation for castling moves. */
public class CastlingMove extends Move {

  /** Boolean to indicate whether the move is a short castle or not. */
  private final boolean isShortCastle;

  /**
   * Constructs a new CastlingMove object with the specified source, destination and castling flags.
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   * @param isShortCastle A boolean indicating whether the move is a short castle (true if it's a
   *     short castle, false if it's a long castle).
   */
  public CastlingMove(
      final Position source,
      final Position dest,
      final ColoredPiece piece,
      final boolean isShortCastle) {
    this(source, dest, piece, isShortCastle, false, false);
  }

  /**
   * Constructs a new CastlingMove object with the specified source and destination positions,
   * castling flags, and check and checkmate flags.
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   * @param isShortCastle A boolean indicating whether the move is a short castle (true if it's a
   *     short castle, false if it's a long castle).
   * @param isCheck A boolean indicating whether the move results in a check (true if it's a check,
   *     false otherwise).
   * @param isCheckMate A boolean indicating whether the move results in a checkmate (true if it's a
   *     checkmate, false otherwise).
   */
  public CastlingMove(
      final Position source,
      final Position dest,
      final ColoredPiece piece,
      final boolean isShortCastle,
      final boolean isCheck,
      final boolean isCheckMate) {
    super(source, dest, piece, false, null, isCheck, isCheckMate);
    this.isShortCastle = isShortCastle;
  }

  /**
   * Converts the move to its algebraic notation.
   *
   * @return The algebraic string representation.
   */
  @Override
  public String toAlgebraicString() {

    final String moveString = isShortCastle ? "O-O" : "O-O-O";
    final String annotation = this.isCheckMate() ? "#" : (this.isCheck() ? "+" : "");

    return moveString + annotation;
  }

  /**
   * Retrieves a boolean to indicate if the move is a short or long castle.
   *
   * @return true for a short castle, false for a long caste.
   */
  public boolean isShortCastle() {

    return this.isShortCastle;
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
