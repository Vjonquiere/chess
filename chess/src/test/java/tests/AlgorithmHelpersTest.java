package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import pdp.model.ai.algorithms.AlgorithmHelpers;
import pdp.model.board.Move;
import pdp.model.board.PromoteMove;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

public class AlgorithmHelpersTest {

  @Test
  public void tryPromoteCorrect() {
    Move move1 =
        new Move(
            new Position(0, 6),
            new Position(0, 7),
            new ColoredPiece(Piece.PAWN, Color.WHITE),
            false);
    PromoteMove promoteMove1 = new PromoteMove(new Position(0, 6), new Position(0, 7), Piece.QUEEN);
    assertEquals(promoteMove1, AlgorithmHelpers.promoteMove(move1));

    Move move2 =
        new Move(
            new Position(0, 1),
            new Position(0, 0),
            new ColoredPiece(Piece.PAWN, Color.BLACK),
            false);
    PromoteMove promoteMove2 = new PromoteMove(new Position(0, 1), new Position(0, 0), Piece.QUEEN);
    assertEquals(promoteMove2, AlgorithmHelpers.promoteMove(move2));
  }

  @Test
  public void tryPromoteIncorrect() {
    Move move1 =
        new Move(
            new Position(0, 6),
            new Position(0, 7),
            new ColoredPiece(Piece.PAWN, Color.BLACK),
            false);
    assertEquals(move1, AlgorithmHelpers.promoteMove(move1));

    Move move2 =
        new Move(
            new Position(0, 1),
            new Position(0, 0),
            new ColoredPiece(Piece.PAWN, Color.WHITE),
            false);
    assertEquals(move2, AlgorithmHelpers.promoteMove(move2));

    Move move3 =
        new Move(
            new Position(0, 1),
            new Position(0, 3),
            new ColoredPiece(Piece.PAWN, Color.WHITE),
            false);
    assertEquals(move3, AlgorithmHelpers.promoteMove(move3));
  }
}
