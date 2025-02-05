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
}
