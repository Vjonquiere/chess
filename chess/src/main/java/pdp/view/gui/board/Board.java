package pdp.view.gui.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.PlayMoveCommand;
import pdp.model.Game;
import pdp.model.GameAi;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

/** GUI representation of game board. */
public class Board extends GridPane {
  private BoardRepresentation board;
  private final int boardColumns;
  private final int boardRows;
  private Position from;
  private final Map<Position, Square> pieces = new HashMap<>();
  private List<Position> reachableSquares;
  private final List<Position> hintSquares = new LinkedList<>();
  private final List<Position> moveSquares = new LinkedList<>();
  private Position checkSquare;
  private Stage stage;

  /**
   * Build a new board from a game and a given stage.
   *
   * @param game The game to get the board data.
   * @param stage The stage to add the board.
   */
  public Board(Game game, Stage stage) {
    this.board = game.getBoard().getBoardRep();
    this.boardColumns = board.getNbCols();
    this.boardRows = board.getNbRows();
    this.stage = stage;
    buildBoard();
  }

  /** Build the board for the first time. Init all squares and setup them. */
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
        sq.setId("square" + finalx + finaly);
        pieces.put(new Position(x, boardRows - 1 - y), sq);
        super.add(sq, x, y);
      }
    }
  }

  /** Update the pieces sprites of all squares. */
  public void updateBoard() {
    cleanHintSquares();
    clearCheckSquare();
    clearLastMoveSquares();
    board = Game.getInstance().getBoard().getBoardRep();
    for (int x = 0; x < boardColumns; x++) {
      for (int y = 0; y < boardRows; y++) {
        ColoredPiece piece = board.getPieceAt(x, boardRows - 1 - y);
        pieces.get(new Position(x, boardRows - 1 - y)).updatePiece(piece);
      }
    }
    Game g = Game.getInstance();
    if (board.isCheck(g.getGameState().isWhiteTurn() ? Color.WHITE : Color.BLACK)) {
      checkSquare = board.getKing(g.getGameState().isWhiteTurn()).get(0);
      setCheckSquare(checkSquare);
    }
    g.getHistory()
        .getCurrentMove()
        .ifPresent(
            (move) -> {
              setLastMoveSquares(move.getState().getMove().source, move.getState().getMove().dest);
            });
  }

  /**
   * Define the selected square (color + command).
   *
   * @param x x coordinate of the selected square
   * @param y y coordinate of the selected square
   */
  private void switchSelectedSquare(int x, int y) {
    boolean isWhiteTurn = Game.getInstance().getGameState().isWhiteTurn();
    Color squareColor = Game.getInstance().getBoard().getBoardRep().getPieceAt(x, y).color;
    if (from == null) {
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
        String move = Move.positionToString(from) + "-" + Move.positionToString(new Position(x, y));
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
  public void setReachableSquares(int x, int y) {
    reachableSquares = new ArrayList<>();
    List<Move> moves = Game.getInstance().getBoard().getBoardRep().getAvailableMoves(x, y, false);
    List<Move> specialMoves =
        Game.getInstance()
            .getBoard()
            .getBoardRep()
            .getSpecialMoves(
                Game.getInstance().getGameState().isWhiteTurn(),
                Game.getInstance().getBoard().getEnPassantPos(),
                Game.getInstance().getBoard().isLastMoveDoublePush(),
                Game.getInstance().getBoard().isWhiteLongCastle(),
                Game.getInstance().getBoard().isWhiteShortCastle(),
                Game.getInstance().getBoard().isBlackLongCastle(),
                Game.getInstance().getBoard().isBlackShortCastle());
    for (Move move : specialMoves) {
      if (move.getSource().x() == x && move.getSource().y() == y) {
        moves.add(move);
      }
    }
    for (Move move : moves) {
      GameAi g = GameAi.fromGame(Game.getInstance());
      try {
        g.playMove(move);
        pieces.get(move.dest).setReachable(true, move.isTake);
        reachableSquares.add(move.dest);
      } catch (Exception e) {
        // e.printStackTrace();
      }
    }
  }

  /** Update the squares that can be captured to their initial states. */
  public void clearReachableSquares() {
    if (reachableSquares != null) {
      for (Position p : reachableSquares) {
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
  public boolean processPawnPromoting(int x, int y) {
    ColoredPiece piece = Game.getInstance().getBoard().getBoardRep().getPieceAt(from.x(), from.y());
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

  /**
   * Set the stage.
   *
   * @param stage The stage.
   */
  public void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * Sets hint squares by highlighting the start and destination positions.
   *
   * @param from The starting position.
   * @param to The destination position.
   */
  public void setHintSquares(Position from, Position to) {
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
    for (Position sq : hintSquares) {
      pieces.get(sq).setHint(false);
      hintSquares.remove(sq);
    }
  }

  private void setCheckSquare(Position pos) {
    checkSquare = pos;
    pieces.get(checkSquare).setCheck(true);
  }

  private void clearCheckSquare() {
    if (checkSquare == null) return;
    pieces.get(checkSquare).setCheck(false);
    checkSquare = null;
  }

  public void setLastMoveSquares(Position from, Position to) {
    if (from.getY() == -1 || from.getX() == -1 || to.getY() == -1 || to.getX() == -1) return;
    moveSquares.add(from);
    moveSquares.add(to);
    pieces.get(from).setLastMove(true);
    pieces.get(to).setLastMove(true);
  }

  private void clearLastMoveSquares() {
    if (moveSquares.isEmpty()) return;
    while (!moveSquares.isEmpty()) {
      pieces.get(moveSquares.get(0)).setLastMove(false);
      moveSquares.remove(moveSquares.get(0));
      // System.out.println(sq);

    }
  }
}
