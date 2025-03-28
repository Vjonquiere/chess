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
  private double squareSize;

  /**
   * Build a square of the given color with the sprite of the given piece.
   *
   * @param piece The piece on the square
   * @param squareColor The default color of the square
   */
  public Square(ColoredPiece piece, boolean squareColor, double squareSize) {
    this.squareSize = squareSize;
    this.setStyle("-fx-border-width: 0; -fx-padding: 0;");
    baseColor =
        squareColor
            ? Color.web(GuiView.getTheme().getSecondary())
            : Color.web(GuiView.getTheme().getPrimary());
    currentPiece = piece;
    sq = new Canvas(squareSize, squareSize);
    reachableSq = new Canvas(squareSize, squareSize);
    hintSq = new Canvas(squareSize, squareSize);
    checkSq = new Canvas(squareSize, squareSize);
    GraphicsContext gc = sq.getGraphicsContext2D();
    gc.setFill(baseColor);
    gc.fillRect(0, 0, squareSize, squareSize);
    super.getChildren().add(sq);
    super.getChildren().add(hintSq);
    super.getChildren().add(checkSq);
    super.getChildren().add(reachableSq);
    if (currentPiece != null && currentPiece.getPiece() != Piece.EMPTY) {
      pieceImage = new PieceImage(piece, squareSize / 2);
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
        pieceImage = new PieceImage(piece, squareSize / 2);
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
    gc.fillRect(0, 0, squareSize, squareSize);
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
      gc.fillOval(squareSize * 0.375, squareSize * 0.375, squareSize * 0.25, squareSize * 0.25);
    } else if (isTake) {
      gc.setFill(Color.web(GuiView.getTheme().getAccent()));
      gc.fillOval(squareSize * 0.1, squareSize * 0.1, squareSize * 0.8, squareSize * 0.8);
      gc.setFill(baseColor);
      gc.fillOval(squareSize * 0.15, squareSize * 0.15, squareSize * 0.7, squareSize * 0.7);
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
      gc.fillRect(squareSize * 0.1, squareSize * 0.1, squareSize * 0.8, squareSize * 0.8);
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
      gc.fillRect(0, 0, squareSize, squareSize);
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
      gc.fillRect(0, 0, squareSize, squareSize);
    }
  }
}
