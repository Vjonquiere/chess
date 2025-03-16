package pdp.view.GUI.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.PlayMoveCommand;
import pdp.model.Game;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

public class Board extends GridPane {
  private BoardRepresentation board;
  private final int boardColumns;
  private final int boardRows;
  private Position from;
  private final Map<Position, Square> pieces = new HashMap<>();
  private List<Position> reachableSquares;
  private Stage stage;

  public Board(Game game, Stage stage) {
    this.board = game.getBoard().getBoardRep();
    this.boardColumns = board.getNbCols();
    this.boardRows = board.getNbRows();
    this.stage = stage;
    buildBoard();
  }

  /** Build the board for the first time. Init all squares and setup them */
  public void buildBoard() {
    super.getChildren().clear();
    for (int x = 0; x < boardColumns; x++) {
      for (int y = 0; y < boardRows; y++) {
        ColoredPiece piece = board.getPieceAt(x, boardRows - 1 - y);
        Square sq;
        if (x % 2 == 0 && y % 2 == 0) {
          sq = new Square(piece, true);
        } else if (x % 2 == 0 && y % 2 == 1) {
          sq = new Square(piece, false);
        } else if (x % 2 == 1 && y % 2 == 0) {
          sq = new Square(piece, false);
        } else {
          sq = new Square(piece, true);
        }
        int finaly = boardRows - 1 - y;
        int finalx = x;
        sq.setOnMouseClicked(
            event -> {
              switchSelectedSquare(finalx, finaly);
            });
        pieces.put(new Position(x, boardRows - 1 - y), sq);
        super.add(sq, x, y);
      }
    }
  }

  /** Update the pieces sprites of all squares */
  public void updateBoard() {
    board = Game.getInstance().getBoard().getBoardRep();
    for (int x = 0; x < boardColumns; x++) {
      for (int y = 0; y < boardRows; y++) {
        ColoredPiece piece = board.getPieceAt(x, boardRows - 1 - y);
        pieces.get(new Position(x, boardRows - 1 - y)).updatePiece(piece);
      }
    }
  }

  /**
   * Define the selected square (color + command)
   *
   * @param x x coordinate of the selected square
   * @param y y coordinate of the selected square
   */
  private void switchSelectedSquare(int x, int y) {
    boolean isWhiteTurn = Game.getInstance().getGameState().isWhiteTurn();
    Color squareColor = Game.getInstance().getBoard().getBoardRep().getPieceAt(x, y).color;
    if (from == null) {
      if ((isWhiteTurn && squareColor != Color.WHITE)
          || (!isWhiteTurn && squareColor != Color.BLACK)) return;
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
        String move = Move.positionToString(from) + "-" + Move.positionToString(new Position(x, y));
        if (processPawnPromoting(x, y)) return;
        BagOfCommands.getInstance().addCommand(new PlayMoveCommand(move));
        clearReachableSquares();
        from = null;
      } catch (Exception e) {
        clearReachableSquares();
        from = null;
        System.out.println("wrong move:" + e.getMessage());
      }
    }
  }

  /**
   * Update the given square to display a capture possibility
   *
   * @param x The x coordinate of the square
   * @param y The y coordinate of the square
   */
  public void setReachableSquares(int x, int y) {
    reachableSquares = new ArrayList<>();
    List<Move> moves = Game.getInstance().getBoard().getBoardRep().getAvailableMoves(x, y, false);
    for (Move move : moves) {
      pieces.get(move.dest).setReachable(true, move.isTake);
      reachableSquares.add(move.dest);
    }
  }

  /** Update the squares that can be captured to their initial states */
  public void clearReachableSquares() {
    if (reachableSquares != null) {
      for (Position p : reachableSquares) {
        pieces.get(p).setReachable(false, false);
      }
      reachableSquares = null;
    }
  }

  /**
   * Get the move with command creation ready format
   *
   * @param x The destination x coordinate
   * @param y The destination y coordinate
   * @return Move as string format
   */
  public boolean processPawnPromoting(int x, int y) {
    ColoredPiece piece =
        Game.getInstance().getBoard().getBoardRep().getPieceAt(from.getX(), from.getY());
    if (piece.piece == Piece.PAWN && piece.color == Color.BLACK && y == 0) { // Black pawn promote
      new PromotionPieceSelectionPopUp(stage, from, new Position(x, y));
      return true;
    }
    if (piece.piece == Piece.PAWN && piece.color == Color.WHITE && y == 7) { // White pawn promote
      new PromotionPieceSelectionPopUp(stage, from, new Position(x, y));
      return true;
    }
    return false;
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }
}
