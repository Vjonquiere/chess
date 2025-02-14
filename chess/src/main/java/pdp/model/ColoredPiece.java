package pdp.model;

import java.util.Objects;

public class ColoredPiece {
  public Piece piece;
  public Color color;

  public ColoredPiece(Piece piece, Color color) {
    this.piece = piece;
    this.color = color;
  }

  @Override
  public String toString() {
    return "ColoredPiece[piece=" + piece + ", color=" + color + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ColoredPiece other) {
      return piece.equals(other.piece) && color.equals(other.color);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(piece, color);
  }
}
