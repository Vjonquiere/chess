package pdp.model;

public class ColoredPiece<Piece, Color> {
  private Piece piece;
  private Color color;

  public ColoredPiece(Piece piece, Color color) {
    this.piece = piece;
    this.color = color;
  }

  public Piece getPiece() {
    return piece;
  }

  public Color getColor() {
    return color;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ColoredPiece) {
      return this.piece.equals(((ColoredPiece<?, ?>) o).piece)
          && this.color.equals(((ColoredPiece<?, ?>) o).color);
    }
    return false;
  }
}
