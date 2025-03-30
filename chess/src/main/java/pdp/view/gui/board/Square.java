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

  private double squareSize;

  /**
   * Build a square of the given color with the sprite of the given piece.
   *
   * @param piece The piece on the square
   * @param squareColor The default color of the square
   */
  public Square(final ColoredPiece piece, final boolean squareColor, final double squareSize) {
    super();
    this.squareSize = squareSize;
    this.setStyle("-fx-border-width: 0; -fx-padding: 0;");
    baseColor =
        squareColor
            ? Color.web(GuiView.getTheme().getSecondary())
            : Color.web(GuiView.getTheme().getPrimary());
    currentPiece = piece;
    square = new Canvas(squareSize, squareSize);
    reachableSq = new Canvas(squareSize, squareSize);
    hintSq = new Canvas(squareSize, squareSize);
    checkSq = new Canvas(squareSize, squareSize);
    final GraphicsContext graphicCtx = square.getGraphicsContext2D();
    graphicCtx.setFill(baseColor);
    graphicCtx.fillRect(0, 0, squareSize, squareSize);
    super.getChildren().add(square);
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
  public void updatePiece(final ColoredPiece piece) {
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
  public void setSelected(final boolean selected) {
    final GraphicsContext graphicCtx = square.getGraphicsContext2D();
    if (selected) {
      graphicCtx.setFill(Color.web(GuiView.getTheme().getAccent()));
    } else {
      graphicCtx.setFill(baseColor);
    }
    graphicCtx.fillRect(0, 0, squareSize, squareSize);
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
      graphicCtx.fillOval(
          squareSize * 0.375, squareSize * 0.375, squareSize * 0.25, squareSize * 0.25);
    } else if (isTake) {
      graphicCtx.setFill(Color.web(GuiView.getTheme().getAccent()));
      graphicCtx.fillOval(squareSize * 0.1, squareSize * 0.1, squareSize * 0.8, squareSize * 0.8);
      graphicCtx.setFill(baseColor);
      graphicCtx.fillOval(squareSize * 0.15, squareSize * 0.15, squareSize * 0.7, squareSize * 0.7);
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
      graphicCtx.fillRect(squareSize * 0.1, squareSize * 0.1, squareSize * 0.8, squareSize * 0.8);
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
      graphicCtx.fillRect(0, 0, squareSize, squareSize);
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
      graphicCtx.fillRect(0, 0, squareSize, squareSize);
    }
  }
}
