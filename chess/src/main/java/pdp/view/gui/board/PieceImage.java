package pdp.view.gui.board;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pdp.model.piece.ColoredPiece;

/** Image of pieces from piece and its color. */
public class PieceImage extends ImageView {
  /**
   * Get an ImageView corresponding to the piece.
   *
   * @param piece The piece to search image for.
   * @param size The size of the image.
   */
  public PieceImage(final ColoredPiece piece, final double size) {
    super();
    final String color = piece.getColor() == pdp.model.piece.Color.WHITE ? "white" : "black";
    final String path =
        "/assets/pieces/" + color + "/" + piece.getPiece().getCharRepresentation(false) + ".png";
    final Image image = new Image(getClass().getResourceAsStream(path));
    this.setImage(image);
    this.setFitWidth(size);
    this.setFitHeight(size);
  }
}
