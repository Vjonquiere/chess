package pdp.model.board;

import pdp.model.piece.ColoredPiece;
import pdp.utils.Position;

/** Special move representation for castling moves. */
public class CastlingMove extends Move {

  boolean isShortCastle;

  /**
   * Constructs a new CastlingMove object with the specified source, destination and castling flags.
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   * @param isShortCastle A boolean indicating whether the move is a short castle (true if it's a
   *     short castle, false if it's a long castle).
   * @param isWhite A boolean indicating whether the move is a white castle (true if it's a white
   *     castle, false if it's a black castle).
   */
  public CastlingMove(Position source, Position dest, ColoredPiece piece, boolean isShortCastle) {
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
   * @param isWhite A boolean indicating whether the move is a white castle (true if it's a white
   *     castle, false if it's a black castle).
   * @param isCheck A boolean indicating whether the move results in a check (true if it's a check,
   *     false otherwise).
   * @param isCheckMate A boolean indicating whether the move results in a checkmate (true if it's a
   *     checkmate, false otherwise).
   */
  public CastlingMove(
      Position source,
      Position dest,
      ColoredPiece piece,
      boolean isShortCastle,
      boolean isCheck,
      boolean isCheckMate) {
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

    String moveString = isShortCastle ? "O-O" : "O-O-O";
    String annotation = this.isCheckMate() ? "#" : (this.isCheck() ? "+" : "");

    return moveString + annotation;
  }

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
