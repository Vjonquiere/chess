package pdp.view.GUI.board;

import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import pdp.model.Game;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.model.piece.ColoredPiece;
import pdp.utils.Position;

public class Board extends GridPane {

  private BoardRepresentation board = Game.getInstance().getBoard().board;
  private Position from;

  public Board() {
    for (int x = 0; x < 8; x++) {
      for (int y = 0; y < 8; y++) {
        System.out.println(x * 7 + 7 - y);
        ColoredPiece piece = board.getPieceAt(x, 7 - y);
        Square sq = new Square(null, false);
        if (x % 2 == 0 && y % 2 == 0) {
          sq = new Square(piece, true);
        } else if (x % 2 == 0 && y % 2 == 1) {
          sq = new Square(piece, false);
        } else if (x % 2 == 1 && y % 2 == 0) {
          sq = new Square(piece, false);
        } else {
          sq = new Square(piece, true);
        }
        int finaly = 7 - y;
        int finalx = x;
        sq.setOnMouseClicked(
            event -> {
              if (from == null) {
                from = new Position(finalx, finaly);
              } else {
                try {
                  Game.getInstance().playMove(new Move(from, new Position(finalx, finaly)));
                  from = null;
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
            });
        super.add(sq, x, y);
        super.add(new Text(Integer.toString(x + y * 8)), x, y);
      }
    }
  }
}
