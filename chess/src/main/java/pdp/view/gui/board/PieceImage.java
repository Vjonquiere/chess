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
   */
  public PieceImage(ColoredPiece piece) {
    String color = piece.color == pdp.model.piece.Color.WHITE ? "white" : "black";
    String path =
        "/assets/pieces/" + color + "/" + piece.piece.getCharRepresentation(false) + ".png";
    Image image = new Image(getClass().getResourceAsStream(path));
    this.setImage(image);
    this.setFitWidth(50);
    this.setFitHeight(50);
  }
}
