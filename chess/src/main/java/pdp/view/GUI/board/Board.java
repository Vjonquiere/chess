package pdp.view.GUI.board;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.layout.GridPane;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.PlayMoveCommand;
import pdp.model.Game;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.model.piece.ColoredPiece;
import pdp.utils.Position;

public class Board extends GridPane {
  private BoardRepresentation board;
  private final int boardColumns;
  private final int boardRows;
  private Position from;
  private final Map<Position, Square> pieces = new HashMap<>();

  public Board(Game game) {
    this.board = game.getBoard().board;
    this.boardColumns = board.getNbCols();
    this.boardRows = board.getNbRows();

    buildBoard();
  }

  public void buildBoard() {
    super.getChildren().clear();
    for (int x = 0; x < boardColumns; x++) {
      for (int y = 0; y < boardRows; y++) {
        // System.out.println(x * 7 + 7 - y);
        ColoredPiece piece = board.getPieceAt(x, boardRows - 1 - y);
        Square sq;
        boolean selected = from != null && from.getX() == x && from.getY() == boardRows - 1 - y;
        if (x % 2 == 0 && y % 2 == 0) {
          sq = new Square(piece, true, selected);
        } else if (x % 2 == 0 && y % 2 == 1) {
          sq = new Square(piece, false, selected);
        } else if (x % 2 == 1 && y % 2 == 0) {
          sq = new Square(piece, false, selected);
        } else {
          sq = new Square(piece, true, selected);
        }
        int finaly = boardRows - 1 - y;
        int finalx = x;
        sq.setOnMouseClicked(
            event -> {
              switchSelectedSquare(finalx, finaly);
            });
        pieces.put(new Position(x, boardRows - 1 - y), sq);
        super.add(sq, x, y);
        // super.add(new Text(Integer.toString(x + y * 8)), x, y);
      }
    }
  }

  public void updateBoard() {
    board = Game.getInstance().getBoard().board;
    for (int x = 0; x < boardColumns; x++) {
      for (int y = 0; y < boardRows; y++) {
        ColoredPiece piece = board.getPieceAt(x, boardRows - 1 - y);
        pieces.get(new Position(x, boardRows - 1 - y)).updatePiece(piece);
      }
    }
  }

  private void switchSelectedSquare(int x, int y) {
    if (from == null) {
      from = new Position(x, y);
      pieces.get(from).setSelected(true);
    } else {
      pieces.get(from).setSelected(false);
      try {
        String move = Move.positionToString(from) + "-" + Move.positionToString(new Position(x, y));
        BagOfCommands.getInstance().addCommand(new PlayMoveCommand(move));
        from = null;
      } catch (Exception e) {
        from = null;
        System.out.println("wrong move:" + e.getMessage());
      }
    }
  }
}
