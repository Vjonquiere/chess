package pdp.model.piece;

import java.util.Objects;

/** Combination of piece and color to represent a chess piece. */
public class ColoredPiece {
  /** Type of piece. */
  private final Piece piece;

  /** Color of the piece. */
  private final Color color;

  /**
   * Creates a color piece of the given type and color.
   *
   * @param piece type of the piece.
   * @param color color of the piece.
   */
  public ColoredPiece(final Piece piece, final Color color) {
    this.piece = piece;
    this.color = color;
  }

  public Piece getPiece() {
    return this.piece;
  }

  public Color getColor() {
    return this.color;
  }

  @Override
  public String toString() {
    return "ColoredPiece[piece=" + piece + ", color=" + color + "]";
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof ColoredPiece other) {
      return piece.equals(other.piece) && color.equals(other.color);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(piece, color);
  }
}
