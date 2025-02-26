package pdp.view.GUI.board;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;

public class Square extends StackPane {
  Color baseColor;
  Canvas sq;
  ColoredPiece currentPiece;
  ImageView pieceImage;

  /**
   * Build a square of the given color with the sprite of the given piece
   *
   * @param piece The piece on the square
   * @param squareColor The default color of the square
   */
  public Square(ColoredPiece piece, boolean squareColor) {
    baseColor = squareColor ? Color.web("#DAE0F2") : Color.web("#6D6FD9");
    currentPiece = piece;
    sq = new Canvas(100, 100);
    GraphicsContext gc = sq.getGraphicsContext2D();
    gc.setFill(baseColor);
    gc.fillRect(0, 0, 100, 100);
    super.getChildren().add(sq);
    if (currentPiece != null && currentPiece.piece != Piece.EMPTY) {
      pieceImage = addPiece(piece);
      super.getChildren().add(pieceImage);
    }
  }

  /**
   * Load the sprite of the given piece
   *
   * @param piece the piece to get the sprite
   * @return The ImageView with the sprite
   */
  private ImageView addPiece(ColoredPiece piece) {
    String color = piece.color == pdp.model.piece.Color.WHITE ? "white" : "black";
    String path =
        "/assets/pieces/" + color + "/" + piece.piece.getCharRepresentation(false) + ".png";
    Image image = new Image(getClass().getResourceAsStream(path));
    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(50);
    imageView.setFitHeight(50);
    return imageView;
  }

  /**
   * Update the sprite displayed on the square with the given piece
   *
   * @param piece The piece to display
   */
  public void updatePiece(ColoredPiece piece) {
    if (!(piece.equals(currentPiece))) {
      currentPiece = piece;
      if (pieceImage != null && super.getChildren().contains(pieceImage)) {
        super.getChildren().remove(pieceImage);
      }
      super.getChildren().remove(pieceImage);
      if (currentPiece != null && currentPiece.piece != Piece.EMPTY) {
        pieceImage = addPiece(piece);
        super.getChildren().add(pieceImage);
      }
    }
  }

  /**
   * Update the square color depending on selection state
   *
   * @param selected The selected state of the square
   */
  public void setSelected(boolean selected) {
    GraphicsContext gc = sq.getGraphicsContext2D();
    if (selected) {
      gc.setFill(Color.web("#F9CFF2"));
    } else {
      gc.setFill(baseColor);
    }
    gc.fillRect(0, 0, 100, 100);
  }
}
