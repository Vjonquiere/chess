package pdp.view.GUI.board;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.view.GUIView;

public class Square extends StackPane {
  Color baseColor;
  Canvas sq;
  Canvas reachableSq;
  Canvas hintSq;
  ColoredPiece currentPiece;
  ImageView pieceImage;

  /**
   * Build a square of the given color with the sprite of the given piece
   *
   * @param piece The piece on the square
   * @param squareColor The default color of the square
   */
  public Square(ColoredPiece piece, boolean squareColor) {
    baseColor =
        squareColor
            ? Color.web(GUIView.theme.getSecondary())
            : Color.web(GUIView.theme.getPrimary());
    currentPiece = piece;
    sq = new Canvas(100, 100);
    reachableSq = new Canvas(100, 100);
    hintSq = new Canvas(100, 100);
    GraphicsContext gc = sq.getGraphicsContext2D();
    gc.setFill(baseColor);
    gc.fillRect(0, 0, 100, 100);
    super.getChildren().add(sq);
    super.getChildren().add(reachableSq);
    super.getChildren().add(hintSq);
    if (currentPiece != null && currentPiece.piece != Piece.EMPTY) {
      pieceImage = new PieceImage(piece);
      super.getChildren().add(pieceImage);
    }
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
        pieceImage = new PieceImage(piece);
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
      gc.setFill(Color.web(GUIView.theme.getAccent()));
    } else {
      gc.setFill(baseColor);
    }
    gc.fillRect(0, 0, 100, 100);
  }

  /**
   * Update the square depending on reachability dans take possibility
   *
   * @param reachable The square reachability
   * @param isTake The square take possibility
   */
  public void setReachable(boolean reachable, boolean isTake) {
    GraphicsContext gc = reachableSq.getGraphicsContext2D();
    gc.clearRect(0, 0, reachableSq.getWidth(), reachableSq.getHeight()); // Clear the canvas

    if (reachable && !isTake) {
      gc.setFill(Color.web(GUIView.theme.getAccent()));
      gc.fillOval(37.5, 37.5, 25, 25);
    } else if (isTake) {
      gc.setFill(Color.web(GUIView.theme.getAccent()));
      gc.fillOval(10, 10, 80, 80);
      gc.setFill(baseColor);
      gc.fillOval(15, 15, 70, 70);
    }
  }

  public void setHint(boolean hint) {
    GraphicsContext gc = hintSq.getGraphicsContext2D();
    gc.clearRect(0, 0, hintSq.getWidth(), hintSq.getHeight());
    if (hint) {
      gc.setFill(Color.web(GUIView.theme.getTertiary()));
      gc.fillRect(10, 10, 80, 80);
    }
  }
}
