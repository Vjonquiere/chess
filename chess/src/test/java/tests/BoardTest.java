import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import pdp.model.*;
import pdp.utils.Position;

public class BoardTest {
  public Game game;

  @Test
  void testClassicMove() {
    game = Game.initialize(false, false, null, null);
    Move move = new Move(new Position(1, 4), new Position(2, 4)); // Pion avance d'une case
    game.getGameState().getBoard().makeMove(move);
    assertEquals(Piece.PAWN, game.getGameState().getBoard().getBoard().getPieceAt(4, 2).getPiece());
    assertEquals(
        game.getGameState().getBoard().getBoard().getPieceAt(1, 4).getPiece(),
        Piece.EMPTY); // L'ancienne position doit être vide
  }

  @Test
  void testDoublePushMove() {
    game = Game.initialize(false, false, null, null);
    Move move = new Move(new Position(1, 4), new Position(3, 4)); // Pion avance de deux cases
    game.getGameState().getBoard().makeMove(move);
    assertEquals(Piece.PAWN, game.getGameState().getBoard().getBoard().getPieceAt(4, 3).getPiece());
    assertEquals(
        game.getGameState().getBoard().getBoard().getPieceAt(1, 4).getPiece(),
        Piece.EMPTY); // L'ancienne position doit être vide
  }

  @Test
  void testCaptureMove() {
    game = Game.initialize(false, false, null, null);
    Move move = new Move(new Position(1, 4), new Position(3, 4));
    game.getGameState().getBoard().makeMove(move);
    Move move1 = new Move(new Position(6, 3), new Position(4, 3));
    game.getGameState().getBoard().makeMove(move1);

    Move move2 = new Move(new Position(3, 4), new Position(4, 3)); // capture
    Position sourcePosition = new Position(move2.getSource().getY(), move2.getSource().getX());
    List<Move> availableMoves = game.getGameState().getBoard().getAvailableMoves(sourcePosition);
    Move classicalMove = move2.isMoveClassical(availableMoves);
    game.getGameState().getBoard().makeMove(classicalMove);

    assertEquals(
        Piece.PAWN,
        game.getGameState()
            .getBoard()
            .getBoard()
            .getPieceAt(3, 4)
            .getPiece()); // Le pion blanc est maintenant ici
  }

  /*@Test
  void testEnPassant() {

  }

  @Test
  void testPawnPromotion() {

  }

  @Test
  void testCastleMove() {
  }*/

}
