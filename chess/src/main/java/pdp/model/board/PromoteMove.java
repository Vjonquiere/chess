package pdp.model.board;

import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

public class PromoteMove extends Move {
  private Piece promPiece;

  public PromoteMove(Position source, Position dest, Piece promPiece) {
    super(source, dest);
    this.promPiece = promPiece;
  }

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

  public Piece getPromPiece() {
    return promPiece;
  }

  @Override
  public String toAlgebraicString() {
    String sourceStr = positionToString(this.source);
    String destinationStr = positionToString(this.dest);
    String separator = this.isTake ? "x" : "-";
    String annotation = this.isCheckMate ? "#" : (this.isCheck ? "+" : "");

    return sourceStr
        + separator
        + destinationStr
        + "="
        + this.promPiece.getCharRepresentation(true)
        + annotation;
  }

  @Override
  public String toString() {
    return super.toString() + "=" + this.promPiece.getCharRepresentation(true);
  }
}
