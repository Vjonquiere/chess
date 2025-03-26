package pdp.view.gui.board;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.view.GuiView;

/** GUI representation of a chess square. */
public class Square extends StackPane {
  private Color baseColor;
  private Canvas sq;
  private Canvas reachableSq;
  private Canvas hintSq;
  private Canvas checkSq;
  private ColoredPiece currentPiece;
  private ImageView pieceImage;

  /**
   * Build a square of the given color with the sprite of the given piece.
   *
   * @param piece The piece on the square
   * @param squareColor The default color of the square
   */
  public Square(ColoredPiece piece, boolean squareColor) {
    baseColor =
        squareColor
            ? Color.web(GuiView.getTheme().getSecondary())
            : Color.web(GuiView.getTheme().getPrimary());
    currentPiece = piece;
    sq = new Canvas(100, 100);
    reachableSq = new Canvas(100, 100);
    hintSq = new Canvas(100, 100);
    checkSq = new Canvas(100, 100);
    GraphicsContext gc = sq.getGraphicsContext2D();
    gc.setFill(baseColor);
    gc.fillRect(0, 0, 100, 100);
    super.getChildren().add(sq);
    super.getChildren().add(hintSq);
    super.getChildren().add(checkSq);
    super.getChildren().add(reachableSq);
    if (currentPiece != null && currentPiece.getPiece() != Piece.EMPTY) {
      pieceImage = new PieceImage(piece);
      super.getChildren().add(pieceImage);
    }
  }

  /**
   * Update the sprite displayed on the square with the given piece.
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
      if (currentPiece != null && currentPiece.getPiece() != Piece.EMPTY) {
        pieceImage = new PieceImage(piece);
        super.getChildren().add(pieceImage);
      }
    }
  }

  /**
   * Update the square color depending on selection state.
   *
   * @param selected The selected state of the square
   */
  public void setSelected(boolean selected) {
    GraphicsContext gc = sq.getGraphicsContext2D();
    if (selected) {
      gc.setFill(Color.web(GuiView.getTheme().getAccent()));
    } else {
      gc.setFill(baseColor);
    }
    gc.fillRect(0, 0, 100, 100);
  }

  /**
   * Update the square depending on reachability dans take possibility.
   *
   * @param reachable The square reachability
   * @param isTake The square take possibility
   */
  public void setReachable(boolean reachable, boolean isTake) {
    GraphicsContext gc = reachableSq.getGraphicsContext2D();
    gc.clearRect(0, 0, reachableSq.getWidth(), reachableSq.getHeight()); // Clear the canvas

    if (reachable && !isTake) {
      gc.setFill(Color.web(GuiView.getTheme().getAccent()));
      gc.fillOval(37.5, 37.5, 25, 25);
    } else if (isTake) {
      gc.setFill(Color.web(GuiView.getTheme().getAccent()));
      gc.fillOval(10, 10, 80, 80);
      gc.setFill(baseColor);
      gc.fillOval(15, 15, 70, 70);
    }
  }

  /**
   * Set the hint status of the square.
   *
   * @param hint Status of the hint state.
   */
  public void setHint(boolean hint) {
    GraphicsContext gc = hintSq.getGraphicsContext2D();
    gc.clearRect(0, 0, hintSq.getWidth(), hintSq.getHeight());
    if (hint) {
      gc.setFill(Color.web(GuiView.getTheme().getTertiary()));
      gc.fillRect(10, 10, 80, 80);
    }
  }

  /**
   * Draw a red rectangle on the king's square if he is in check or clear the square if he isn't in
   * check anymore.
   *
   * @param isCheck true if the king is check, false otherwise
   */
  public void setCheck(boolean isCheck) {
    GraphicsContext gc = hintSq.getGraphicsContext2D();
    gc.clearRect(0, 0, hintSq.getWidth(), hintSq.getHeight());
    if (isCheck) {
      gc.setFill(Color.rgb(255, 0, 0, 0.5));
      gc.fillRect(0, 0, 100, 100);
    }
  }

  /**
   * Removes the color from the square and add green if the square to display was in the last move
   * made.
   *
   * @param isLastMove true if the square was in the last move made.
   */
  public void setLastMove(boolean isLastMove) {
    GraphicsContext gc = hintSq.getGraphicsContext2D();
    gc.clearRect(0, 0, hintSq.getWidth(), hintSq.getHeight());
    if (isLastMove) {
      gc.setFill(Color.rgb(51, 153, 102, 0.5));
      gc.fillRect(0, 0, 100, 100);
    }
  }
}
