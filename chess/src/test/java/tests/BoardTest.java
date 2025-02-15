package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pdp.model.*;
import pdp.model.board.Move;
import pdp.model.piece.Color;
import pdp.model.piece.Piece;
import pdp.utils.Position;

public class BoardTest {
  public Game game;

  @Test
  public void testClassicMove() {
    game = Game.initialize(false, false, null, null);
    Move move = new Move(new Position(4, 1), new Position(4, 2));
    game.playMove(move);
    assertEquals(Piece.PAWN, game.getGameState().getBoard().getBoard().getPieceAt(4, 2).piece);
    assertEquals(game.getGameState().getBoard().getBoard().getPieceAt(1, 4).piece, Piece.EMPTY);
  }

  @Test
  public void testDoublePushMove() {
    game = Game.initialize(false, false, null, null);
    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    assertEquals(Piece.PAWN, game.getGameState().getBoard().getBoard().getPieceAt(4, 3).piece);
    assertEquals(game.getGameState().getBoard().getBoard().getPieceAt(1, 4).piece, Piece.EMPTY);
  }

  @Test
  public void testCaptureMove() {
    game = Game.initialize(false, false, null, null);
    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move1 = new Move(new Position(3, 6), new Position(3, 4));
    game.playMove(move1);

    Move move2 = new Move(new Position(4, 3), new Position(3, 4));
    game.playMove(move2);

    assertEquals(Piece.PAWN, game.getGameState().getBoard().getBoard().getPieceAt(3, 4).piece);
  }

  @Test
  public void testCanCastleWhenGameStartsShouldBeFalse() {
    game = Game.initialize(false, false, null, null);
    assertFalse(
        game.getBoard().canCastle(Color.WHITE, true),
        "Short castle should not be possible for white at initial position !");
    assertFalse(
        game.getBoard().canCastle(Color.BLACK, true),
        "Short castle should not be possible for black at initial position !");
    assertFalse(
        game.getBoard().canCastle(Color.WHITE, false),
        "Long castle should not be possible for white at initial position !");
    assertFalse(
        game.getBoard().canCastle(Color.BLACK, false),
        "Long castle should not be possible for black at initial position !");
  }

  @Test
  public void testCanCastleWhenBlackInCheckShouldBeFalse() {
    game = Game.initialize(false, false, null, null);

    // e2-e4  white
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.getGameState().getBoard().makeMove(move1);
    // d7-d5  black
    Move move2 = new Move(new Position(3, 6), new Position(3, 4));
    game.getGameState().getBoard().makeMove(move2);
    // Ng1-Nf3  white
    Move move3 = new Move(new Position(6, 0), new Position(5, 2));
    game.getGameState().getBoard().makeMove(move3);
    // b7-b6  black
    Move move4 = new Move(new Position(1, 6), new Position(1, 5));
    game.getGameState().getBoard().makeMove(move4);
    // Bf1-Bb5  white
    Move move5 = new Move(new Position(5, 0), new Position(1, 4));
    game.getGameState().getBoard().makeMove(move5);

    assertFalse(
        game.getBoard().canCastle(Color.BLACK, true),
        "Short castle should not be possible for black when king is in check !");
    assertFalse(
        game.getBoard().canCastle(Color.BLACK, false),
        "Long castle should not be possible for black when king is in check !");
  }

  @Test
  public void testCanCastleWhenWhiteInCheckShouldBeFalse() {
    game = Game.initialize(false, false, null, null);

    // e2-e4  white
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.getGameState().getBoard().makeMove(move1);
    // e7-e5  black
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.getGameState().getBoard().makeMove(move2);
    // f2-f4  white
    Move move3 = new Move(new Position(5, 1), new Position(5, 3));
    game.getGameState().getBoard().makeMove(move3);
    // Qd8-Qh4  black
    Move move4 = new Move(new Position(3, 7), new Position(7, 3));
    game.getGameState().getBoard().makeMove(move4);

    assertFalse(
        game.getBoard().canCastle(Color.WHITE, true),
        "Short castle should not be possible for white when king is in check !");
    assertFalse(
        game.getBoard().canCastle(Color.WHITE, false),
        "Long castle should not be possible for white when king is in check !");
  }

  @Test
  public void testCanCastleShortWhenPieceInBetweenShouldBeFalse() {
    game = Game.initialize(false, false, null, null);

    // e2-e4  white
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.getGameState().getBoard().makeMove(move1);
    // e7-e5  black
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.getGameState().getBoard().makeMove(move2);
    // Bf1-Bc4  white
    Move move3 = new Move(new Position(5, 0), new Position(2, 3));
    game.getGameState().getBoard().makeMove(move3);
    // Bf8-Bc5  black
    Move move4 = new Move(new Position(5, 7), new Position(2, 4));
    game.getGameState().getBoard().makeMove(move4);

    assertFalse(
        game.getBoard().canCastle(Color.BLACK, true),
        "Short castle should not be possible for black when a piece is blocking !");
    assertFalse(
        game.getBoard().canCastle(Color.WHITE, true),
        "Short castle should not be possible for white when a piece is blocking !");
  }

  @Test
  public void testCanCastleLongWhenPieceInBetweenShouldBeFalse() {
    game = Game.initialize(false, false, null, null);

    // d2-d4  white
    Move move1 = new Move(new Position(3, 1), new Position(3, 3));
    game.getGameState().getBoard().makeMove(move1);
    // d7-d5  black
    Move move2 = new Move(new Position(3, 6), new Position(3, 4));
    game.getGameState().getBoard().makeMove(move2);
    // Bc1-Bf4  white
    Move move3 = new Move(new Position(2, 0), new Position(5, 3));
    game.getGameState().getBoard().makeMove(move3);
    // Bc8-Bf5  black
    Move move4 = new Move(new Position(2, 7), new Position(5, 4));
    game.getGameState().getBoard().makeMove(move4);
    // Qd1-Qd2 white
    Move move5 = new Move(new Position(3, 0), new Position(3, 1));
    game.getGameState().getBoard().makeMove(move5);
    // Qd8-Qd7 black
    Move move6 = new Move(new Position(3, 7), new Position(3, 7));
    game.getGameState().getBoard().makeMove(move6);

    assertFalse(
        game.getBoard().canCastle(Color.BLACK, false),
        "Long castle should not be possible for black when a piece is blocking !");
    assertFalse(
        game.getBoard().canCastle(Color.WHITE, false),
        "Long castle should not be possible for white when a piece is blocking !");
  }

  @Test
  public void testCanCastleShortWhenSquareAttackedShouldBeFalse() {
    game = Game.initialize(false, false, null, null);

    // e2-e4  white
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.getGameState().getBoard().makeMove(move1);
    // e7-e5  black
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.getGameState().getBoard().makeMove(move2);
    // f2-f4  white
    Move move3 = new Move(new Position(5, 1), new Position(5, 3));
    game.getGameState().getBoard().makeMove(move3);
    // f7-f5  black
    Move move4 = new Move(new Position(5, 6), new Position(5, 4));
    game.getGameState().getBoard().makeMove(move4);
    // Ng1-Nf3 white
    Move move5 = new Move(new Position(6, 0), new Position(5, 2));
    game.getGameState().getBoard().makeMove(move5);
    // Ng8-Nf6 black
    Move move6 = new Move(new Position(6, 7), new Position(5, 5));
    game.getGameState().getBoard().makeMove(move6);
    // Bf1-Bc4  white
    Move move7 = new Move(new Position(5, 0), new Position(2, 3));
    game.getGameState().getBoard().makeMove(move7);
    // Bf8-Bc5  black
    Move move8 = new Move(new Position(5, 7), new Position(2, 4));
    game.getGameState().getBoard().makeMove(move8);

    assertFalse(
        game.getBoard().canCastle(Color.BLACK, true),
        "Short castle should not be possible for black when a square is attacked !");
    assertFalse(
        game.getBoard().canCastle(Color.WHITE, true),
        "Short castle should not be possible for white when a square is attacked !");
  }

  @Test
  public void testCanCastleWhenKingHasMovedShouldBeFalse() {
    game = Game.initialize(false, false, null, null);

    // e2-e4  white
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.getGameState().getBoard().makeMove(move1);
    // e7-e5  black
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.getGameState().getBoard().makeMove(move2);
    // Bf1-Bb5  white
    Move move3 = new Move(new Position(5, 0), new Position(1, 4));
    game.getGameState().getBoard().makeMove(move3);
    // Bf8-Bb4  black
    Move move4 = new Move(new Position(5, 7), new Position(1, 3));
    game.getGameState().getBoard().makeMove(move4);
    // Ng1-Nf3 white
    Move move5 = new Move(new Position(6, 0), new Position(5, 2));
    game.getGameState().getBoard().makeMove(move5);
    // Ng8-Nf6 black
    Move move6 = new Move(new Position(6, 7), new Position(5, 5));
    game.getGameState().getBoard().makeMove(move6);
    // Ke1-Ke2 white
    Move move7 = new Move(new Position(4, 0), new Position(4, 1));
    game.getGameState().getBoard().makeMove(move7);
    // Ke8-Ke7 black
    Move move8 = new Move(new Position(4, 7), new Position(4, 6));
    game.getGameState().getBoard().makeMove(move8);
    // Ke2-Ke1 white
    Move move9 = new Move(new Position(4, 1), new Position(4, 0));
    game.getGameState().getBoard().makeMove(move9);
    // Ke7-Ke8 black
    Move move10 = new Move(new Position(4, 6), new Position(4, 7));
    game.getGameState().getBoard().makeMove(move10);

    assertFalse(
        game.getBoard().canCastle(Color.WHITE, true),
        "Castling should not be possible for white if the king has moved !");
    assertFalse(
        game.getBoard().canCastle(Color.BLACK, true),
        "Castling should not be possible for black if the king has moved !");
  }

  @Test
  public void testCanCastleWhenRookHasMovedShouldBeFalse() {
    game = Game.initialize(false, false, null, null);

    // d2-d4  white
    Move move1 = new Move(new Position(3, 1), new Position(3, 3));
    game.getGameState().getBoard().makeMove(move1);
    // d7-d5  black
    Move move2 = new Move(new Position(3, 6), new Position(3, 4));
    game.getGameState().getBoard().makeMove(move2);
    // Bc1-Bf4  white
    Move move3 = new Move(new Position(2, 0), new Position(5, 3));
    game.getGameState().getBoard().makeMove(move3);
    // Bc8-Bf5  black
    Move move4 = new Move(new Position(2, 7), new Position(5, 4));
    game.getGameState().getBoard().makeMove(move4);
    // Nb1-Nc3 white
    Move move5 = new Move(new Position(1, 0), new Position(2, 2));
    game.getGameState().getBoard().makeMove(move5);
    // Nb8-Nc6 black
    Move move6 = new Move(new Position(1, 7), new Position(2, 5));
    game.getGameState().getBoard().makeMove(move6);
    // Qd1-Qd2 white
    Move move7 = new Move(new Position(3, 0), new Position(3, 1));
    game.getGameState().getBoard().makeMove(move7);
    // Qd8-Qd7 black
    Move move8 = new Move(new Position(3, 7), new Position(3, 6));
    game.getGameState().getBoard().makeMove(move8);
    // Ra1-Rb1 white
    Move move9 = new Move(new Position(0, 0), new Position(1, 0));
    game.getGameState().getBoard().makeMove(move9);
    // Ra8-Bb8 black
    Move move10 = new Move(new Position(0, 7), new Position(1, 7));
    game.getGameState().getBoard().makeMove(move10);
    // Rb1-Ra1 white
    Move move11 = new Move(new Position(1, 0), new Position(0, 0));
    game.getGameState().getBoard().makeMove(move11);
    // Rb8-Ra8 black
    Move move12 = new Move(new Position(1, 7), new Position(0, 7));
    game.getGameState().getBoard().makeMove(move12);

    assertFalse(
        game.getBoard().canCastle(Color.WHITE, true),
        "Castling should not be possible for white if the rook has moved !");
    assertFalse(
        game.getBoard().canCastle(Color.BLACK, true),
        "Castling should not be possible for black if the rook has moved !");
  }

  @Test
  public void testCanCastleShortShouldBeTrue() {
    game = Game.initialize(false, false, null, null);

    // e2-e4  white
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.getGameState().getBoard().makeMove(move1);
    // e7-e5  black
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.getGameState().getBoard().makeMove(move2);
    // Bf1-Bb5  white
    Move move3 = new Move(new Position(5, 0), new Position(1, 4));
    game.getGameState().getBoard().makeMove(move3);
    // Bf8-Bb4  black
    Move move4 = new Move(new Position(5, 7), new Position(1, 3));
    game.getGameState().getBoard().makeMove(move4);
    // Ng1-Nf3 white
    Move move5 = new Move(new Position(6, 0), new Position(5, 2));
    game.getGameState().getBoard().makeMove(move5);
    // Ng8-Nf6 black
    Move move6 = new Move(new Position(6, 7), new Position(5, 5));
    game.getGameState().getBoard().makeMove(move6);

    assertTrue(
        game.getBoard().canCastle(Color.WHITE, true),
        "Short castle should be possible for white in this position !");
    assertTrue(
        game.getBoard().canCastle(Color.BLACK, true),
        "Short castle should be possible for black in this position !");
  }

  @Test
  public void testCanCastleLongShouldBeTrue() {
    game = Game.initialize(false, false, null, null);

    // d2-d4  white
    Move move1 = new Move(new Position(3, 1), new Position(3, 3));
    game.getGameState().getBoard().makeMove(move1);
    // d7-d5  black
    Move move2 = new Move(new Position(3, 6), new Position(3, 4));
    game.getGameState().getBoard().makeMove(move2);
    // Bc1-Bf4  white
    Move move3 = new Move(new Position(2, 0), new Position(5, 3));
    game.getGameState().getBoard().makeMove(move3);
    // Bc8-Bf5  black
    Move move4 = new Move(new Position(2, 7), new Position(5, 4));
    game.getGameState().getBoard().makeMove(move4);
    // Nb1-Nc3 white
    Move move5 = new Move(new Position(1, 0), new Position(2, 2));
    game.getGameState().getBoard().makeMove(move5);
    // Nb8-Nc6 black
    Move move6 = new Move(new Position(1, 7), new Position(2, 5));
    game.getGameState().getBoard().makeMove(move6);
    // Qd1-Qd2 white
    Move move7 = new Move(new Position(3, 0), new Position(3, 1));
    game.getGameState().getBoard().makeMove(move7);
    // Qd8-Qd7 black
    Move move8 = new Move(new Position(3, 7), new Position(3, 6));
    game.getGameState().getBoard().makeMove(move8);

    assertTrue(
        game.getBoard().canCastle(Color.WHITE, false),
        "Long castle should be possible for white in this position !");
    assertTrue(
        game.getBoard().canCastle(Color.BLACK, false),
        "Long castle should be possible for black in this position !");
  }

  @Test
  public void testApplyShortCastleShouldBeSuccess() {
    game = Game.initialize(false, false, null, null);

    // e2-e4  white
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.getGameState().getBoard().makeMove(move1);
    // e7-e5  black
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.getGameState().getBoard().makeMove(move2);
    // Bf1-Bb5  white
    Move move3 = new Move(new Position(5, 0), new Position(1, 4));
    game.getGameState().getBoard().makeMove(move3);
    // Bf8-Bb4  black
    Move move4 = new Move(new Position(5, 7), new Position(1, 3));
    game.getGameState().getBoard().makeMove(move4);
    // Ng1-Nf3 white
    Move move5 = new Move(new Position(6, 0), new Position(5, 2));
    game.getGameState().getBoard().makeMove(move5);
    // Ng8-Nf6 black
    Move move6 = new Move(new Position(6, 7), new Position(5, 5));
    game.getGameState().getBoard().makeMove(move6);
    // apply short castle white
    game.getBoard().applyCastle(Color.WHITE, true);
    // apply short castle black
    game.getBoard().applyCastle(Color.BLACK, true);

    // ensure white king ended up on g1
    assertTrue(
        game.getBoard().getBoard().getPieceAt(6, 0).piece == Piece.KING,
        "White king should end up on g1 square after short castle !");
    // ensure black king ended up on g8
    assertTrue(
        game.getBoard().getBoard().getPieceAt(6, 7).piece == Piece.KING,
        "Black king should end up on g8 square after short castle !");
    // ensure white rook ended up on f1
    assertTrue(
        game.getBoard().getBoard().getPieceAt(5, 0).piece == Piece.ROOK,
        "White rook should end up on f1 square after short castle !");
    // ensure black rook ended up on f8
    assertTrue(
        game.getBoard().getBoard().getPieceAt(5, 7).piece == Piece.ROOK,
        "Black rook should end up on f8 square after short castle !");

    // ensure h1 square is empty
    assertTrue(
        game.getBoard().getBoard().getPieceAt(7, 0).piece == Piece.EMPTY,
        "H1 square should be empty after short castle for white !");
    // ensure e1 square is empty
    assertTrue(
        game.getBoard().getBoard().getPieceAt(4, 0).piece == Piece.EMPTY,
        "E1 square should be empty after short castle for white !");
    // ensure h8 square is empty
    assertTrue(
        game.getBoard().getBoard().getPieceAt(7, 7).piece == Piece.EMPTY,
        "H8 square should be empty after short castle for black !");
    // ensure e8 square is empty
    assertTrue(
        game.getBoard().getBoard().getPieceAt(4, 7).piece == Piece.EMPTY,
        "E8 square should be empty after short castle for black !");
  }

  @Test
  public void testApplyLongCastleShouldBeSuccess() {
    game = Game.initialize(false, false, null, null);

    // d2-d4  white
    Move move1 = new Move(new Position(3, 1), new Position(3, 3));
    game.getGameState().getBoard().makeMove(move1);
    // d7-d5  black
    Move move2 = new Move(new Position(3, 6), new Position(3, 4));
    game.getGameState().getBoard().makeMove(move2);
    // Bc1-Bf4  white
    Move move3 = new Move(new Position(2, 0), new Position(5, 3));
    game.getGameState().getBoard().makeMove(move3);
    // Bc8-Bf5  black
    Move move4 = new Move(new Position(5, 7), new Position(5, 4));
    game.getGameState().getBoard().makeMove(move4);
    // Nb1-Nc3 white
    Move move5 = new Move(new Position(1, 0), new Position(2, 2));
    game.getGameState().getBoard().makeMove(move5);
    // Nb8-Nc6 black
    Move move6 = new Move(new Position(1, 7), new Position(2, 5));
    game.getGameState().getBoard().makeMove(move6);
    // Qd1-Qd2 white
    Move move7 = new Move(new Position(3, 0), new Position(3, 1));
    game.getGameState().getBoard().makeMove(move7);
    // Qd8-Qd7 black
    Move move8 = new Move(new Position(3, 7), new Position(3, 6));
    game.getGameState().getBoard().makeMove(move8);
    // apply long castle white
    game.getBoard().applyCastle(Color.WHITE, false);
    // apply long castle black
    game.getBoard().applyCastle(Color.BLACK, false);

    // ensure white king ended up on c1
    assertTrue(
        game.getBoard().getBoard().getPieceAt(2, 0).piece == Piece.KING,
        "White king should end up on c1 square after long castle !");
    // ensure black king ended up on c8
    assertTrue(
        game.getBoard().getBoard().getPieceAt(2, 7).piece == Piece.KING,
        "Black king should end up on c8 square after long castle !");
    // ensure white rook ended up on d1
    assertTrue(
        game.getBoard().getBoard().getPieceAt(3, 0).piece == Piece.ROOK,
        "White rook should end up on d1 square after long castle !");
    // ensure black rook ended up on d8
    assertTrue(
        game.getBoard().getBoard().getPieceAt(3, 7).piece == Piece.ROOK,
        "White rook should end up on d1 square after long castle !");

    // ensure a1 square is empty
    assertTrue(
        game.getBoard().getBoard().getPieceAt(0, 0).piece == Piece.EMPTY,
        "A1 square should be empty after long castle for white !");
    // ensure e1 square is empty
    assertTrue(
        game.getBoard().getBoard().getPieceAt(4, 0).piece == Piece.EMPTY,
        "E1 square should be empty after long castle for white !");
    // ensure b1 square is empty
    assertTrue(
        game.getBoard().getBoard().getPieceAt(1, 0).piece == Piece.EMPTY,
        "B1 square should be empty after long castle for white !");
    // ensure a8 square is empty
    assertTrue(
        game.getBoard().getBoard().getPieceAt(0, 7).piece == Piece.EMPTY,
        "A8 square should be empty after long castle for black !");
    // ensure e8 square is empty
    assertTrue(
        game.getBoard().getBoard().getPieceAt(4, 7).piece == Piece.EMPTY,
        "E8 square should be empty after long castle for black !");
    // ensure b8 square is empty
    assertTrue(
        game.getBoard().getBoard().getPieceAt(1, 7).piece == Piece.EMPTY,
        "B8 square should be empty after long castle for black !");
  }

  @Test
  public void testCanCastleLongWhenSquareAttackedShouldBeFalse() {
    game = Game.initialize(false, false, null, null);

    // e2-e4  white
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.getGameState().getBoard().makeMove(move1);
    // e7-e5  black
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.getGameState().getBoard().makeMove(move2);
    // d2-d4  white
    Move move3 = new Move(new Position(3, 1), new Position(3, 3));
    game.getGameState().getBoard().makeMove(move3);
    // d7-d5  black
    Move move4 = new Move(new Position(3, 6), new Position(3, 4));
    game.getGameState().getBoard().makeMove(move4);
    // Bc1-Bg5  white
    Move move5 = new Move(new Position(2, 0), new Position(6, 4));
    game.getGameState().getBoard().makeMove(move5);
    // Bc8-Bg4  black
    Move move6 = new Move(new Position(2, 7), new Position(6, 3));
    game.getGameState().getBoard().makeMove(move6);
    // Qd1-Qg4  white
    Move move7 = new Move(new Position(3, 0), new Position(6, 3));
    game.playMove(move7);
    // Qd8-Qg5  black
    Move move8 = new Move(new Position(3, 7), new Position(6, 4));
    game.playMove(move8);
    // Nb1-Nc3  white
    Move move9 = new Move(new Position(1, 0), new Position(2, 2));
    game.getGameState().getBoard().makeMove(move9);
    // Nb8-Nc6  black
    Move move10 = new Move(new Position(1, 7), new Position(2, 5));
    game.getGameState().getBoard().makeMove(move10);

    // c8 is attacked
    assertTrue(game.getBoard().getBoard().isAttacked(2, 7, Color.WHITE));
    // c1 is attacked
    assertTrue(game.getBoard().getBoard().isAttacked(2, 0, Color.BLACK));

    assertFalse(
        game.getBoard().canCastle(Color.BLACK, false),
        "Long castle should not be possible for black when a square is attacked!");
    assertFalse(
        game.getBoard().canCastle(Color.WHITE, false),
        "Long castle should not be possible for white when a square is attacked !");
  }

  @Test
  public void testEnPassant() {
    game = Game.initialize(false, false, null, null);
    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move1 = new Move(new Position(1, 6), new Position(1, 5));
    game.playMove(move1);
    Move move2 = new Move(new Position(4, 3), new Position(4, 4));
    game.playMove(move2);
    Move move3 = new Move(new Position(3, 6), new Position(3, 4));
    game.playMove(move3);

    assertEquals(Piece.EMPTY, game.getGameState().getBoard().getBoard().getPieceAt(3, 5).piece);
    Move move4 = new Move(new Position(4, 4), new Position(3, 5));
    game.playMove(move4);
    assertEquals(Piece.EMPTY, game.getGameState().getBoard().getBoard().getPieceAt(3, 4).piece);
    assertEquals(Piece.PAWN, game.getGameState().getBoard().getBoard().getPieceAt(3, 5).piece);
  }

  @Test // must promote a pawn to Queen
  public void boardPromotionWhiteQueenTest() {
    game = Game.initialize(false, false, null, null);
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move1);
    Move move2 = new Move(new Position(4, 6), new Position(4, 5));
    game.playMove(move2);

    Move move3 = new Move(new Position(4, 3), new Position(4, 4));
    game.playMove(move3);
    Move move4 = new Move(new Position(3, 6), new Position(3, 4));
    game.playMove(move4);

    Move move5 = new Move(new Position(4, 4), new Position(3, 5));
    game.playMove(move5);
    Move move6 = new Move(new Position(5, 6), new Position(5, 4));
    game.playMove(move6);

    Move move7 = new Move(new Position(3, 5), new Position(2, 6));
    game.playMove(move7);
    Move move8 = new Move(new Position(6, 6), new Position(6, 5));
    game.playMove(move8);

    Move move9 = new Move(new Position(2, 6), new Position(1, 7));
    game.playMove(move9);

    assertEquals(Piece.QUEEN, game.getGameState().getBoard().getBoard().getPieceAt(1, 7).piece);
    Move move10 = new Move(new Position(5, 4), new Position(5, 3));
    game.playMove(move10);
    Move move11 = new Move(new Position(1, 7), new Position(4, 4));
    game.playMove(move11);
    assertEquals(Piece.EMPTY, game.getGameState().getBoard().getBoard().getPieceAt(1, 7).piece);
    assertEquals(Piece.QUEEN, game.getGameState().getBoard().getBoard().getPieceAt(4, 4).piece);
  }

  @Test // must promote a pawn to Queen
  public void boardPromotionQueenBlackTest() {
    game = Game.initialize(false, false, null, null);

    Move move1 = new Move(new Position(4, 1), new Position(4, 3)); // b
    game.playMove(move1);
    Move move2 = new Move(new Position(3, 6), new Position(3, 4)); // n
    game.playMove(move2);

    Move move3 = new Move(new Position(4, 3), new Position(4, 4)); // b
    game.playMove(move3);
    Move move4 = new Move(new Position(3, 4), new Position(3, 3)); // n
    game.playMove(move4);

    Move move5 = new Move(new Position(2, 1), new Position(2, 3)); // b
    game.playMove(move5);
    Move move6 = new Move(new Position(3, 3), new Position(2, 2)); // n
    game.playMove(move6);

    Move move7 = new Move(new Position(3, 1), new Position(3, 2)); // b
    game.playMove(move7);
    Move move8 = new Move(new Position(2, 2), new Position(2, 1)); // n
    game.playMove(move8);

    Move move9 = new Move(new Position(3, 2), new Position(3, 3)); // b
    game.playMove(move9);
    Move move10 = new Move(new Position(2, 1), new Position(1, 0)); // n
    game.playMove(move10);

    assertEquals(Piece.QUEEN, game.getGameState().getBoard().getBoard().getPieceAt(1, 0).piece);

    Move move15 = new Move(new Position(3, 3), new Position(3, 4)); // b
    game.playMove(move15);
    Move move16 = new Move(new Position(1, 0), new Position(3, 2)); // n
    game.playMove(move16);

    assertEquals(Piece.EMPTY, game.getGameState().getBoard().getBoard().getPieceAt(1, 0).piece);
    assertEquals(Piece.QUEEN, game.getGameState().getBoard().getBoard().getPieceAt(3, 2).piece);
  }
}
