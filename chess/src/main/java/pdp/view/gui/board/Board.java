package pdp.view.gui.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.animation.TranslateTransition;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.PlayMoveCommand;
import pdp.model.Game;
import pdp.model.GameAi;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.model.history.HistoryNode;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;
import pdp.view.GuiView;

/** GUI representation of game board. */
public class Board extends GridPane {
  /** Board representation of the game. */
  private BoardRepresentation boardRep;

  /** Number of columns of the board. */
  private final int boardColumns;

  /** Number of rows of the board. */
  private final int boardRows;

  /** First square clicked on to play a move. */
  private Position from;

  /** Map mapping the postions of the boardRep to a square. */
  private final Map<Position, Square> pieces = new HashMap<>();

  /** List containing the positions of the squares reachable from the the selected squares. */
  private List<Position> reachableSquares;

  /** List containing the position of the hint move. */
  private final List<Position> hintSquares = new LinkedList<>();

  /** List containing the positions of the last move played. */
  private final List<Position> moveSquares = new LinkedList<>();

  /** Position of the square where the king is check. */
  private Position checkSquare;

  /** Stage containing the Board. */
  private Stage stage;

  private double squareSize;

  /**
   * Build a new board from a game and a given stage.
   *
   * @param game The game to get the board data.
   * @param stage The stage to add the board.
   */
  public Board(final Game game, final Stage stage) {
    this.boardRep = game.getBoard();
    this.boardColumns = boardRep.getNbCols();
    this.boardRows = boardRep.getNbRows();
    this.stage = stage;
    buildBoard();
  }

  /** Build the board for the first time. Init all squares and setup them. */
  public void buildBoard() {
    char letter = 65;
    char number = 56;
    if (stage != null) {
      double maxWidth = stage.getWidth() * 2.0 / 3.0 / 8.0;
      double maxHeight = (stage.getHeight() - 75) / 8.0;
      squareSize = Math.min(maxWidth, maxHeight);
    }

    super.getChildren().clear();
    for (int x = 0; x < boardColumns; x++) {
      for (int y = 0; y < boardRows; y++) {
        final ColoredPiece piece = boardRep.getPieceAt(x, boardRows - 1 - y);
        final Square square;
        if (x % 2 == 0 && y % 2 == 0) {
          square = new Square(piece, true, squareSize);
        } else if (x % 2 == 0 && y % 2 == 1) {
          square = new Square(piece, false, squareSize);
        } else if (x % 2 == 1 && y % 2 == 0) {
          square = new Square(piece, false, squareSize);
        } else {
          square = new Square(piece, true, squareSize);
        }
        final int finalY = boardRows - 1 - y;
        final int finalX = x;
        square.setOnMouseClicked(
            event -> {
              switchSelectedSquare(finalX, finalY);
            });
        square.setId("square" + finalX + finalY);
        pieces.put(new Position(x, boardRows - 1 - y), square);
        super.add(square, x, y);
        if (boardRows - 1 - y == 0) {
          Label label = new Label(String.valueOf(letter));
          label.setStyle(
              "-fx-text-fill: "
                  + GuiView.getTheme().getText()
                  + ";-fx-padding: 0 5 0 0;-fx-font-weight: bold;");
          GridPane.setHalignment(label, HPos.RIGHT);
          GridPane.setValignment(label, VPos.BOTTOM);
          super.add(label, x, y);
          letter++;
        }
        if (x == 7) {
          Label label = new Label(String.valueOf(number));
          label.setStyle(
              "-fx-text-fill: "
                  + GuiView.getTheme().getText()
                  + ";-fx-padding: 0 5 0 0;-fx-font-weight: bold;");
          GridPane.setHalignment(label, HPos.RIGHT);
          GridPane.setValignment(label, VPos.TOP);
          super.add(label, x, y);
          number--;
        }
      }
    }
  }

  /** Update the pieces sprites of all squares. */
  public void updateBoard() {
    cleanHintSquares();
    clearCheckSquare();
    clearLastMoveSquares();
    Game.getInstance().getHistory().getCurrentMove().ifPresent(this::movePiece); // TODO:
    // Re-activate after tests.
    // updateAfterAnimation();
  }

  /** Used to update the board after the move animation finished. */
  private void updateAfterAnimation() {
    boardRep = Game.getInstance().getBoard();
    for (int x = 0; x < boardColumns; x++) {
      for (int y = 0; y < boardRows; y++) {
        final ColoredPiece piece = boardRep.getPieceAt(x, boardRows - 1 - y);
        pieces.get(new Position(x, boardRows - 1 - y)).updatePiece(piece);
      }
    }
    final Game game = Game.getInstance();
    if (boardRep.isCheck(game.getGameState().isWhiteTurn() ? Color.WHITE : Color.BLACK)) {
      checkSquare = boardRep.getKing(game.getGameState().isWhiteTurn()).get(0);
      setCheckSquare(checkSquare);
    }
    game.getHistory()
        .getCurrentMove()
        .ifPresent(
            (move) -> {
              setLastMoveSquares(
                  move.getState().getMove().getSource(), move.getState().getMove().getDest());
            });
  }

  /**
   * Play an animation corresponding to the move contained in the history node.
   *
   * @param historyNode The history to extract the move.
   */
  private void movePiece(final HistoryNode historyNode) {
    if (Game.getInstance().getGameState().isWhiteTurn()
        && Game.getInstance().isBlackAi()
        && Game.getInstance().getBlackSolver().getLastMoveTime() < 2000000000L) {
      updateAfterAnimation();
      return;
    }
    if (!Game.getInstance().getGameState().isWhiteTurn()
        && Game.getInstance().isWhiteAi()
        && Game.getInstance().getWhiteSolver().getLastMoveTime() < 2000000000L) {
      updateAfterAnimation();
      return;
    }
    final Move move = historyNode.getState().getMove();
    pieces.get(move.getSource()).updatePiece(new ColoredPiece(Piece.EMPTY, Color.EMPTY));
    pieces.get(move.getDest()).updatePiece(new ColoredPiece(Piece.EMPTY, Color.EMPTY));

    PieceImage piece = new PieceImage(move.getPiece(), squareSize / 2);
    piece.setLayoutX(move.getSource().x() * squareSize + 25);
    piece.setLayoutY((boardRows - 1 - move.getSource().y()) * squareSize);
    super.getChildren().add(piece);

    TranslateTransition transition = new TranslateTransition();
    transition.setNode(piece);
    transition.setDuration(javafx.util.Duration.seconds(0.1));
    transition.setFromX(move.getSource().x() * squareSize + squareSize * 0.25);
    transition.setFromY((boardRows - 1 - move.getSource().y()) * squareSize);
    transition.setToX(move.getDest().x() * squareSize + squareSize * 0.25);
    transition.setToY((boardRows - 1 - move.getDest().y()) * squareSize);

    transition.setOnFinished(
        (event) -> {
          super.getChildren().remove(piece);
          updateAfterAnimation();
        });
    transition.play();
  }

  /**
   * Define the selected square (color + command).
   *
   * @param x x coordinate of the selected square
   * @param y y coordinate of the selected square
   */
  private void switchSelectedSquare(final int x, final int y) {
    final boolean isWhiteTurn = Game.getInstance().getGameState().isWhiteTurn();
    final Color squareColor = Game.getInstance().getBoard().getPieceAt(x, y).getColor();
    if (from == null) {
      if (isWhiteTurn && Game.getInstance().isWhiteAi() && squareColor == Color.WHITE) {
        return;
      }
      if (!isWhiteTurn && Game.getInstance().isBlackAi() && squareColor == Color.BLACK) {
        return;
      }
      if ((isWhiteTurn && squareColor != Color.WHITE)
          || (!isWhiteTurn && squareColor != Color.BLACK)) {
        return;
      }
      from = new Position(x, y);
      pieces.get(from).setSelected(true);
      clearReachableSquares();
      setReachableSquares(x, y);
    } else {
      pieces.get(from).setSelected(false);
      if ((isWhiteTurn && squareColor == Color.WHITE)
          || (!isWhiteTurn && squareColor == Color.BLACK)) {
        from = new Position(x, y);
        pieces.get(from).setSelected(true);
        clearReachableSquares();
        setReachableSquares(x, y);
        return;
      }
      try {
        final String move =
            Move.positionToString(from) + "-" + Move.positionToString(new Position(x, y));
        if (processPawnPromoting(x, y)) {
          return;
        }
        BagOfCommands.getInstance().addCommand(new PlayMoveCommand(move));
        clearReachableSquares();
        from = null;
      } catch (Exception e) {
        clearReachableSquares();
        from = null;
      }
    }
  }

  /**
   * Update the given square to display a capture possibility.
   *
   * @param x The x coordinate of the square
   * @param y The y coordinate of the square
   */
  public void setReachableSquares(final int x, final int y) {
    reachableSquares = new ArrayList<>();
    final List<Move> moves = Game.getInstance().getBoard().getAvailableMoves(x, y, false);
    for (final Move move : moves) {
      final GameAi game = GameAi.fromGame(Game.getInstance());
      try {
        game.playMove(move);
        pieces.get(move.getDest()).setReachable(true, move.isTake());
        reachableSquares.add(move.getDest());
      } catch (Exception ignored) {
        // e.printStackTrace();
      }
    }
  }

  /** Update the squares that can be captured to their initial states. */
  public void clearReachableSquares() {
    if (reachableSquares != null) {
      for (final Position p : reachableSquares) {
        pieces.get(p).setReachable(false, false);
      }
      reachableSquares = null;
    }
  }

  /**
   * Get the move with command creation ready format.
   *
   * @param x The destination x coordinate
   * @param y The destination y coordinate
   * @return Move as string format
   */
  public boolean processPawnPromoting(final int x, final int y) {
    final ColoredPiece piece = Game.getInstance().getBoard().getPieceAt(from.x(), from.y());
    if (piece.getPiece() == Piece.PAWN
        && piece.getColor() == Color.BLACK
        && y == 0) { // Black pawn promote
      new PromotionPieceSelectionPopUp(stage, from, new Position(x, y));
      return true;
    }
    if (piece.getPiece() == Piece.PAWN
        && piece.getColor() == Color.WHITE
        && y == 7) { // White pawn promote
      new PromotionPieceSelectionPopUp(stage, from, new Position(x, y));
      return true;
    }
    return false;
  }

  /**
   * Set the stage.
   *
   * @param stage The stage.
   */
  public void setStage(final Stage stage) {
    this.stage = stage;
  }

  /**
   * Sets hint squares by highlighting the start and destination positions.
   *
   * @param from The starting position.
   * @param to The destination position.
   */
  public void setHintSquares(final Position from, final Position to) {
    hintSquares.add(from);
    hintSquares.add(to);
    pieces.get(from).setHint(true);
    pieces.get(to).setHint(true);
  }

  /** Clean all squares that have been set to hint. */
  private void cleanHintSquares() {
    if (hintSquares.isEmpty()) {
      return;
    }
    for (final Position sq : hintSquares) {
      pieces.get(sq).setHint(false);
      hintSquares.remove(sq);
    }
  }

  private void setCheckSquare(final Position pos) {
    checkSquare = pos;
    pieces.get(checkSquare).setCheck(true);
  }

  private void clearCheckSquare() {
    if (checkSquare == null) {
      return;
    }
    pieces.get(checkSquare).setCheck(false);
    checkSquare = null;
  }

  /**
   * Displays the last move by adding a color on top of the from and to squares of the last move.
   *
   * @param from start position of the last move
   * @param to end position of the last move
   */
  public void setLastMoveSquares(final Position from, final Position to) {
    if (from.y() == -1 || from.x() == -1 || to.y() == -1 || to.x() == -1) {
      return;
    }
    moveSquares.add(from);
    moveSquares.add(to);
    pieces.get(from).setLastMove(true);
    pieces.get(to).setLastMove(true);
  }

  private void clearLastMoveSquares() {
    if (moveSquares.isEmpty()) {
      return;
    }
    while (!moveSquares.isEmpty()) {
      pieces.get(moveSquares.get(0)).setLastMove(false);
      moveSquares.remove(moveSquares.get(0));
      // System.out.println(sq);

    }
  }
}
