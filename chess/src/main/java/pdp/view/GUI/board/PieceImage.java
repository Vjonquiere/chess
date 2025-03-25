package pdp.view.GUI.board;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pdp.model.piece.ColoredPiece;

public class PieceImage extends ImageView {
  public PieceImage(ColoredPiece piece) {
    String color = piece.getColor() == pdp.model.piece.Color.WHITE ? "white" : "black";
    String path =
        "/assets/pieces/" + color + "/" + piece.getPiece().getCharRepresentation(false) + ".png";
    Image image = new Image(getClass().getResourceAsStream(path));
    this.setImage(image);
    this.setFitWidth(50);
    this.setFitHeight(50);
  }
}
