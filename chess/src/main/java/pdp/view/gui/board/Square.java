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
  /** Base color of the square. */
  private final Color baseColor;

  /** Canvas to draw the square. */
  private final Canvas square;

  /** Canvas drawn if the square is reachable. */
  private final Canvas reachableSq;

  /** Canvas drawn if the square is a hint. */
  private final Canvas hintSq;

  /** Canvas drawn if the square is check. */
  private final Canvas checkSq;

  /** Piece to draw on the square. */
  private ColoredPiece currentPiece;

  /** Image of the piece to draw. */
  private ImageView pieceImage;

  /**
   * Build a square of the given color with the sprite of the given piece.
   *
   * @param piece The piece on the square
   * @param squareColor The default color of the square
   */
  public Square(final ColoredPiece piece, final boolean squareColor) {
    super();
    baseColor =
        squareColor
            ? Color.web(GuiView.getTheme().getSecondary())
            : Color.web(GuiView.getTheme().getPrimary());
    currentPiece = piece;
    square = new Canvas(100, 100);
    reachableSq = new Canvas(100, 100);
    hintSq = new Canvas(100, 100);
    checkSq = new Canvas(100, 100);
    final GraphicsContext graphicCtx = square.getGraphicsContext2D();
    graphicCtx.setFill(baseColor);
    graphicCtx.fillRect(0, 0, 100, 100);
    super.getChildren().add(square);
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
  public void updatePiece(final ColoredPiece piece) {
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
  public void setSelected(final boolean selected) {
    final GraphicsContext graphicCtx = square.getGraphicsContext2D();
    if (selected) {
      graphicCtx.setFill(Color.web(GuiView.getTheme().getAccent()));
    } else {
      graphicCtx.setFill(baseColor);
    }
    graphicCtx.fillRect(0, 0, 100, 100);
  }

  /**
   * Update the square depending on reachability dans take possibility.
   *
   * @param reachable The square reachability
   * @param isTake The square take possibility
   */
  public void setReachable(final boolean reachable, final boolean isTake) {
    final GraphicsContext graphicCtx = reachableSq.getGraphicsContext2D();
    graphicCtx.clearRect(0, 0, reachableSq.getWidth(), reachableSq.getHeight()); // Clear the canvas

    if (reachable && !isTake) {
      graphicCtx.setFill(Color.web(GuiView.getTheme().getAccent()));
      graphicCtx.fillOval(37.5, 37.5, 25, 25);
    } else if (isTake) {
      graphicCtx.setFill(Color.web(GuiView.getTheme().getAccent()));
      graphicCtx.fillOval(10, 10, 80, 80);
      graphicCtx.setFill(baseColor);
      graphicCtx.fillOval(15, 15, 70, 70);
    }
  }

  /**
   * Set the hint status of the square.
   *
   * @param hint Status of the hint state.
   */
  public void setHint(final boolean hint) {
    final GraphicsContext graphicCtx = hintSq.getGraphicsContext2D();
    graphicCtx.clearRect(0, 0, hintSq.getWidth(), hintSq.getHeight());
    if (hint) {
      graphicCtx.setFill(Color.web(GuiView.getTheme().getTertiary()));
      graphicCtx.fillRect(10, 10, 80, 80);
    }
  }

  /**
   * Draw a red rectangle on the king's square if he is in check or clear the square if he isn't in
   * check anymore.
   *
   * @param isCheck true if the king is check, false otherwise
   */
  public void setCheck(final boolean isCheck) {
    final GraphicsContext graphicCtx = hintSq.getGraphicsContext2D();
    graphicCtx.clearRect(0, 0, hintSq.getWidth(), hintSq.getHeight());
    if (isCheck) {
      graphicCtx.setFill(Color.rgb(255, 0, 0, 0.5));
      graphicCtx.fillRect(0, 0, 100, 100);
    }
  }

  /**
   * Removes the color from the square and add green if the square to display was in the last move
   * made.
   *
   * @param isLastMove true if the square was in the last move made.
   */
  public void setLastMove(final boolean isLastMove) {
    final GraphicsContext graphicCtx = hintSq.getGraphicsContext2D();
    graphicCtx.clearRect(0, 0, hintSq.getWidth(), hintSq.getHeight());
    if (isLastMove) {
      graphicCtx.setFill(Color.rgb(51, 153, 102, 0.5));
      graphicCtx.fillRect(0, 0, 100, 100);
    }
  }
}
