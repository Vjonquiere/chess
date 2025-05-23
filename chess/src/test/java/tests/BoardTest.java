package tests;

import static org.junit.jupiter.api.Assertions.*;
import static pdp.utils.Logging.configureGlobalLogger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.exceptions.InvalidPromoteFormatException;
import pdp.model.Game;
import pdp.model.board.Move;
import pdp.model.board.PromoteMove;
import pdp.model.piece.Color;
import pdp.model.piece.Piece;
import pdp.utils.Position;

public class BoardTest {
  public Game game;

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @BeforeAll
  public static void setUpLocale() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @BeforeEach
  void setUpConsole() {
    System.setOut(new PrintStream(outputStream));
    System.setErr(new PrintStream(outputStream));
    configureGlobalLogger();
  }

  @AfterEach
  void tearDownConsole() {
    System.setOut(originalOut);
    System.setErr(originalErr);
    outputStream.reset();
    configureGlobalLogger();
  }

  @Test
  public void testClassicMove() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    Move move = new Move(new Position(4, 1), new Position(4, 2));
    game.playMove(move);
    assertEquals(Piece.PAWN, game.getBoard().getPieceAt(4, 2).getPiece());
    assertEquals(game.getBoard().getPieceAt(1, 4).getPiece(), Piece.EMPTY);
  }

  @Test
  public void testDoublePushMove() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    assertEquals(Piece.PAWN, game.getBoard().getPieceAt(4, 3).getPiece());
    assertEquals(game.getBoard().getPieceAt(1, 4).getPiece(), Piece.EMPTY);
  }

  @Test
  public void testCaptureMove() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move1 = new Move(new Position(3, 6), new Position(3, 4));
    game.playMove(move1);

    Move move2 = new Move(new Position(4, 3), new Position(3, 4));
    game.playMove(move2);

    assertEquals(Piece.PAWN, game.getBoard().getPieceAt(3, 4).getPiece());
  }

  @Test
  public void testCanCastleWhenGameStartsShouldBeFalse() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
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
    game = Game.initialize(false, false, null, null, null, new HashMap<>());

    // e2-e4  white
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.getBoard().makeMove(move1);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // d7-d5  black
    Move move2 = new Move(new Position(3, 6), new Position(3, 4));
    game.getBoard().makeMove(move2);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Ng1-Nf3  white
    Move move3 = new Move(new Position(6, 0), new Position(5, 2));
    game.getBoard().makeMove(move3);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // b7-b6  black
    Move move4 = new Move(new Position(1, 6), new Position(1, 5));
    game.getBoard().makeMove(move4);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bf1-Bb5  white
    Move move5 = new Move(new Position(5, 0), new Position(1, 4));
    game.getBoard().makeMove(move5);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());

    assertFalse(
        game.getBoard().canCastle(Color.BLACK, true),
        "Short castle should not be possible for black when king is in check !");
    assertFalse(
        game.getBoard().canCastle(Color.BLACK, false),
        "Long castle should not be possible for black when king is in check !");
  }

  @Test
  public void testCanCastleWhenWhiteInCheckShouldBeFalse() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());

    // e2-e4  white
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.getBoard().makeMove(move1);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // e7-e5  black
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.getBoard().makeMove(move2);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // f2-f4  white
    Move move3 = new Move(new Position(5, 1), new Position(5, 3));
    game.getBoard().makeMove(move3);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Qd8-Qh4  black
    Move move4 = new Move(new Position(3, 7), new Position(7, 3));
    game.getBoard().makeMove(move4);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());

    assertFalse(
        game.getBoard().canCastle(Color.WHITE, true),
        "Short castle should not be possible for white when king is in check !");
    assertFalse(
        game.getBoard().canCastle(Color.WHITE, false),
        "Long castle should not be possible for white when king is in check !");
  }

  @Test
  public void testCanCastleShortWhenPieceInBetweenShouldBeFalse() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());

    // e2-e4  white
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.getBoard().makeMove(move1);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // e7-e5  black
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.getBoard().makeMove(move2);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bf1-Bc4  white
    Move move3 = new Move(new Position(5, 0), new Position(2, 3));
    game.getBoard().makeMove(move3);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bf8-Bc5  black
    Move move4 = new Move(new Position(5, 7), new Position(2, 4));
    game.getBoard().makeMove(move4);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());

    assertFalse(
        game.getBoard().canCastle(Color.BLACK, true),
        "Short castle should not be possible for black when a piece is blocking !");
    assertFalse(
        game.getBoard().canCastle(Color.WHITE, true),
        "Short castle should not be possible for white when a piece is blocking !");
  }

  @Test
  public void testCanCastleLongWhenPieceInBetweenShouldBeFalse() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());

    // d2-d4  white
    Move move1 = new Move(new Position(3, 1), new Position(3, 3));
    game.getBoard().makeMove(move1);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // d7-d5  black
    Move move2 = new Move(new Position(3, 6), new Position(3, 4));
    game.getBoard().makeMove(move2);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bc1-Bf4  white
    Move move3 = new Move(new Position(2, 0), new Position(5, 3));
    game.getBoard().makeMove(move3);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bc8-Bf5  black
    Move move4 = new Move(new Position(2, 7), new Position(5, 4));
    game.getBoard().makeMove(move4);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Qd1-Qd2 white
    Move move5 = new Move(new Position(3, 0), new Position(3, 1));
    game.getBoard().makeMove(move5);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Qd8-Qd7 black
    Move move6 = new Move(new Position(3, 7), new Position(3, 7));
    game.getBoard().makeMove(move6);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());

    assertFalse(
        game.getBoard().canCastle(Color.BLACK, false),
        "Long castle should not be possible for black when a piece is blocking !");
    assertFalse(
        game.getBoard().canCastle(Color.WHITE, false),
        "Long castle should not be possible for white when a piece is blocking !");
  }

  @Test
  public void testCanCastleShortWhenSquareAttackedShouldBeFalse() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());

    // e2-e4  white
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.getBoard().makeMove(move1);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // e7-e5  black
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.getBoard().makeMove(move2);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // f2-f4  white
    Move move3 = new Move(new Position(5, 1), new Position(5, 3));
    game.getBoard().makeMove(move3);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // f7-f5  black
    Move move4 = new Move(new Position(5, 6), new Position(5, 4));
    game.getBoard().makeMove(move4);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Ng1-Nf3 white
    Move move5 = new Move(new Position(6, 0), new Position(5, 2));
    game.getBoard().makeMove(move5);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Ng8-Nf6 black
    Move move6 = new Move(new Position(6, 7), new Position(5, 5));
    game.getBoard().makeMove(move6);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bf1-Bc4  white
    Move move7 = new Move(new Position(5, 0), new Position(2, 3));
    game.getBoard().makeMove(move7);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bf8-Bc5  black
    Move move8 = new Move(new Position(5, 7), new Position(2, 4));
    game.getBoard().makeMove(move8);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());

    assertFalse(
        game.getBoard().canCastle(Color.BLACK, true),
        "Short castle should not be possible for black when a square is attacked !");
    assertFalse(
        game.getBoard().canCastle(Color.WHITE, true),
        "Short castle should not be possible for white when a square is attacked !");
  }

  @Test
  public void testCanCastleWhenKingHasMovedShouldBeFalse() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());

    // e2-e4  white
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.getBoard().makeMove(move1);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // e7-e5  black
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.getBoard().makeMove(move2);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bf1-Bb5  white
    Move move3 = new Move(new Position(5, 0), new Position(1, 4));
    game.getBoard().makeMove(move3);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bf8-Bb4  black
    Move move4 = new Move(new Position(5, 7), new Position(1, 3));
    game.getBoard().makeMove(move4);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Ng1-Nf3 white
    Move move5 = new Move(new Position(6, 0), new Position(5, 2));
    game.getBoard().makeMove(move5);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Ng8-Nf6 black
    Move move6 = new Move(new Position(6, 7), new Position(5, 5));
    game.getBoard().makeMove(move6);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Ke1-Ke2 white
    Move move7 = new Move(new Position(4, 0), new Position(4, 1));
    game.getBoard().makeMove(move7);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Ke8-Ke7 black
    Move move8 = new Move(new Position(4, 7), new Position(4, 6));
    game.getBoard().makeMove(move8);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Ke2-Ke1 white
    Move move9 = new Move(new Position(4, 1), new Position(4, 0));
    game.getBoard().makeMove(move9);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Ke7-Ke8 black
    Move move10 = new Move(new Position(4, 6), new Position(4, 7));
    game.getBoard().makeMove(move10);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());

    assertFalse(
        game.getBoard().canCastle(Color.WHITE, true),
        "Castling should not be possible for white if the king has moved !");
    assertFalse(
        game.getBoard().canCastle(Color.BLACK, true),
        "Castling should not be possible for black if the king has moved !");
  }

  @Test
  public void testCanCastleWhenRookHasMovedShouldBeFalse() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());

    // d2-d4  white
    Move move1 = new Move(new Position(3, 1), new Position(3, 3));
    game.getBoard().makeMove(move1);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // d7-d5  black
    Move move2 = new Move(new Position(3, 6), new Position(3, 4));
    game.getBoard().makeMove(move2);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bc1-Bf4  white
    Move move3 = new Move(new Position(2, 0), new Position(5, 3));
    game.getBoard().makeMove(move3);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bc8-Bf5  black
    Move move4 = new Move(new Position(2, 7), new Position(5, 4));
    game.getBoard().makeMove(move4);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Nb1-Nc3 white
    Move move5 = new Move(new Position(1, 0), new Position(2, 2));
    game.getBoard().makeMove(move5);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Nb8-Nc6 black
    Move move6 = new Move(new Position(1, 7), new Position(2, 5));
    game.getBoard().makeMove(move6);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Qd1-Qd2 white
    Move move7 = new Move(new Position(3, 0), new Position(3, 1));
    game.getBoard().makeMove(move7);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Qd8-Qd7 black
    Move move8 = new Move(new Position(3, 7), new Position(3, 6));
    game.getBoard().makeMove(move8);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Ra1-Rb1 white
    Move move9 = new Move(new Position(0, 0), new Position(1, 0));
    game.getBoard().makeMove(move9);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Ra8-Bb8 black
    Move move10 = new Move(new Position(0, 7), new Position(1, 7));
    game.getBoard().makeMove(move10);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Rb1-Ra1 white
    Move move11 = new Move(new Position(1, 0), new Position(0, 0));
    game.getBoard().makeMove(move11);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Rb8-Ra8 black
    Move move12 = new Move(new Position(1, 7), new Position(0, 7));
    game.getBoard().makeMove(move12);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());

    assertFalse(
        game.getBoard().canCastle(Color.WHITE, true),
        "Castling should not be possible for white if the rook has moved !");
    assertFalse(
        game.getBoard().canCastle(Color.BLACK, true),
        "Castling should not be possible for black if the rook has moved !");
  }

  @Test
  public void testCanCastleShortShouldBeTrue() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());

    // e2-e4  white
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.getBoard().makeMove(move1);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // e7-e5  black
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.getBoard().makeMove(move2);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bf1-Bb5  white
    Move move3 = new Move(new Position(5, 0), new Position(1, 4));
    game.getBoard().makeMove(move3);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bf8-Bb4  black
    Move move4 = new Move(new Position(5, 7), new Position(1, 3));
    game.getBoard().makeMove(move4);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Ng1-Nf3 white
    Move move5 = new Move(new Position(6, 0), new Position(5, 2));
    game.getBoard().makeMove(move5);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Ng8-Nf6 black
    Move move6 = new Move(new Position(6, 7), new Position(5, 5));
    game.getBoard().makeMove(move6);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());

    assertTrue(
        game.getBoard().canCastle(Color.WHITE, true),
        "Short castle should be possible for white in this position !");
    assertTrue(
        game.getBoard().canCastle(Color.BLACK, true),
        "Short castle should be possible for black in this position !");
  }

  @Test
  public void testCanCastleLongShouldBeTrue() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());

    // d2-d4  white
    Move move1 = new Move(new Position(3, 1), new Position(3, 3));
    game.getBoard().makeMove(move1);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // d7-d5  black
    Move move2 = new Move(new Position(3, 6), new Position(3, 4));
    game.getBoard().makeMove(move2);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bc1-Bf4  white
    Move move3 = new Move(new Position(2, 0), new Position(5, 3));
    game.getBoard().makeMove(move3);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bc8-Bf5  black
    Move move4 = new Move(new Position(2, 7), new Position(5, 4));
    game.getBoard().makeMove(move4);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Nb1-Nc3 white
    Move move5 = new Move(new Position(1, 0), new Position(2, 2));
    game.getBoard().makeMove(move5);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Nb8-Nc6 black
    Move move6 = new Move(new Position(1, 7), new Position(2, 5));
    game.getBoard().makeMove(move6);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Qd1-Qd2 white
    Move move7 = new Move(new Position(3, 0), new Position(3, 1));
    game.getBoard().makeMove(move7);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Qd8-Qd7 black
    Move move8 = new Move(new Position(3, 7), new Position(3, 6));
    game.getBoard().makeMove(move8);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());

    assertTrue(
        game.getBoard().canCastle(Color.WHITE, false),
        "Long castle should be possible for white in this position !");
    assertTrue(
        game.getBoard().canCastle(Color.BLACK, false),
        "Long castle should be possible for black in this position !");
  }

  @Test
  public void testApplyShortCastleShouldBeSuccess() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());

    // e2-e4  white
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.getBoard().makeMove(move1);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // e7-e5  black
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.getBoard().makeMove(move2);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bf1-Bb5  white
    Move move3 = new Move(new Position(5, 0), new Position(1, 4));
    game.getBoard().makeMove(move3);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bf8-Bb4  black
    Move move4 = new Move(new Position(5, 7), new Position(1, 3));
    game.getBoard().makeMove(move4);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Ng1-Nf3 white
    Move move5 = new Move(new Position(6, 0), new Position(5, 2));
    game.getBoard().makeMove(move5);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Ng8-Nf6 black
    Move move6 = new Move(new Position(6, 7), new Position(5, 5));
    game.getBoard().makeMove(move6);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // apply short castle white
    game.getBoard().applyCastle(Color.WHITE, true);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // apply short castle black
    game.getBoard().applyCastle(Color.BLACK, true);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());

    // ensure white king ended up on g1
    assertTrue(
        game.getBoard().getPieceAt(6, 0).getPiece() == Piece.KING,
        "White king should end up on g1 square after short castle !");
    // ensure black king ended up on g8
    assertTrue(
        game.getBoard().getPieceAt(6, 7).getPiece() == Piece.KING,
        "Black king should end up on g8 square after short castle !");
    // ensure white rook ended up on f1
    assertTrue(
        game.getBoard().getPieceAt(5, 0).getPiece() == Piece.ROOK,
        "White rook should end up on f1 square after short castle !");
    // ensure black rook ended up on f8
    assertTrue(
        game.getBoard().getPieceAt(5, 7).getPiece() == Piece.ROOK,
        "Black rook should end up on f8 square after short castle !");

    // ensure h1 square is empty
    assertTrue(
        game.getBoard().getPieceAt(7, 0).getPiece() == Piece.EMPTY,
        "H1 square should be empty after short castle for white !");
    // ensure e1 square is empty
    assertTrue(
        game.getBoard().getPieceAt(4, 0).getPiece() == Piece.EMPTY,
        "E1 square should be empty after short castle for white !");
    // ensure h8 square is empty
    assertTrue(
        game.getBoard().getPieceAt(7, 7).getPiece() == Piece.EMPTY,
        "H8 square should be empty after short castle for black !");
    // ensure e8 square is empty
    assertTrue(
        game.getBoard().getPieceAt(4, 7).getPiece() == Piece.EMPTY,
        "E8 square should be empty after short castle for black !");
  }

  @Test
  public void testApplyLongCastleShouldBeSuccess() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());

    // d2-d4  white
    Move move1 = new Move(new Position(3, 1), new Position(3, 3));
    game.getBoard().makeMove(move1);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // d7-d5  black
    Move move2 = new Move(new Position(3, 6), new Position(3, 4));
    game.getBoard().makeMove(move2);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bc1-Bf4  white
    Move move3 = new Move(new Position(2, 0), new Position(5, 3));
    game.getBoard().makeMove(move3);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bc8-Bf5  black
    Move move4 = new Move(new Position(5, 7), new Position(5, 4));
    game.getBoard().makeMove(move4);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Nb1-Nc3 white
    Move move5 = new Move(new Position(1, 0), new Position(2, 2));
    game.getBoard().makeMove(move5);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Nb8-Nc6 black
    Move move6 = new Move(new Position(1, 7), new Position(2, 5));
    game.getBoard().makeMove(move6);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Qd1-Qd2 white
    Move move7 = new Move(new Position(3, 0), new Position(3, 1));
    game.getBoard().makeMove(move7);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Qd8-Qd7 black
    Move move8 = new Move(new Position(3, 7), new Position(3, 6));
    game.getBoard().makeMove(move8);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // apply long castle white
    game.getBoard().applyCastle(Color.WHITE, false);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // apply long castle black
    game.getBoard().applyCastle(Color.BLACK, false);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());

    // ensure white king ended up on c1
    assertTrue(
        game.getBoard().getPieceAt(2, 0).getPiece() == Piece.KING,
        "White king should end up on c1 square after long castle !");
    // ensure black king ended up on c8
    assertTrue(
        game.getBoard().getPieceAt(2, 7).getPiece() == Piece.KING,
        "Black king should end up on c8 square after long castle !");
    // ensure white rook ended up on d1
    assertTrue(
        game.getBoard().getPieceAt(3, 0).getPiece() == Piece.ROOK,
        "White rook should end up on d1 square after long castle !");
    // ensure black rook ended up on d8
    assertTrue(
        game.getBoard().getPieceAt(3, 7).getPiece() == Piece.ROOK,
        "White rook should end up on d1 square after long castle !");

    // ensure a1 square is empty
    assertTrue(
        game.getBoard().getPieceAt(0, 0).getPiece() == Piece.EMPTY,
        "A1 square should be empty after long castle for white !");
    // ensure e1 square is empty
    assertTrue(
        game.getBoard().getPieceAt(4, 0).getPiece() == Piece.EMPTY,
        "E1 square should be empty after long castle for white !");
    // ensure b1 square is empty
    assertTrue(
        game.getBoard().getPieceAt(1, 0).getPiece() == Piece.EMPTY,
        "B1 square should be empty after long castle for white !");
    // ensure a8 square is empty
    assertTrue(
        game.getBoard().getPieceAt(0, 7).getPiece() == Piece.EMPTY,
        "A8 square should be empty after long castle for black !");
    // ensure e8 square is empty
    assertTrue(
        game.getBoard().getPieceAt(4, 7).getPiece() == Piece.EMPTY,
        "E8 square should be empty after long castle for black !");
    // ensure b8 square is empty
    assertTrue(
        game.getBoard().getPieceAt(1, 7).getPiece() == Piece.EMPTY,
        "B8 square should be empty after long castle for black !");
  }

  @Test
  public void testCanCastleLongWhenSquareAttackedShouldBeFalse() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());

    // e2-e4  white
    Move move1 = new Move(new Position(4, 1), new Position(4, 3));
    game.getBoard().makeMove(move1);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // e7-e5  black
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.getBoard().makeMove(move2);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // d2-d4  white
    Move move3 = new Move(new Position(3, 1), new Position(3, 3));
    game.getBoard().makeMove(move3);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // d7-d5  black
    Move move4 = new Move(new Position(3, 6), new Position(3, 4));
    game.getBoard().makeMove(move4);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bc1-Bg5  white
    Move move5 = new Move(new Position(2, 0), new Position(6, 4));
    game.getBoard().makeMove(move5);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Bc8-Bg4  black
    Move move6 = new Move(new Position(2, 7), new Position(6, 3));
    game.getBoard().makeMove(move6);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Qd1-Qg4  white
    Move move7 = new Move(new Position(3, 0), new Position(6, 3));
    game.playMove(move7);
    // Qd8-Qg5  black
    Move move8 = new Move(new Position(3, 7), new Position(6, 4));
    game.playMove(move8);
    // Nb1-Nc3  white
    Move move9 = new Move(new Position(1, 0), new Position(2, 2));
    game.getBoard().makeMove(move9);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());
    // Nb8-Nc6  black
    Move move10 = new Move(new Position(1, 7), new Position(2, 5));
    game.getBoard().makeMove(move10);
    game.getBoard().setPlayer(!game.getBoard().getPlayer());

    // c8 is attacked
    assertTrue(game.getBoard().isAttacked(2, 7, Color.WHITE));
    // c1 is attacked
    assertTrue(game.getBoard().isAttacked(2, 0, Color.BLACK));

    assertFalse(
        game.getBoard().canCastle(Color.BLACK, false),
        "Long castle should not be possible for black when a square is attacked!");
    assertFalse(
        game.getBoard().canCastle(Color.WHITE, false),
        "Long castle should not be possible for white when a square is attacked !");
  }

  @Test
  public void testEnPassant() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move1 = new Move(new Position(1, 6), new Position(1, 5));
    game.playMove(move1);
    Move move2 = new Move(new Position(4, 3), new Position(4, 4));
    game.playMove(move2);
    Move move3 = new Move(new Position(3, 6), new Position(3, 4));
    game.playMove(move3);

    assertEquals(Piece.EMPTY, game.getBoard().getPieceAt(3, 5).getPiece());
    Move move4 = new Move(new Position(4, 4), new Position(3, 5));
    game.playMove(move4);
    assertEquals(Piece.EMPTY, game.getBoard().getPieceAt(3, 4).getPiece());
    assertEquals(Piece.PAWN, game.getBoard().getPieceAt(3, 5).getPiece());
  }

  @Test // must promote a pawn to Queen
  public void boardPromotionWhiteQueenTest() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
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

    PromoteMove move9 = new PromoteMove(new Position(2, 6), new Position(1, 7), Piece.QUEEN);
    game.playMove(move9);

    assertEquals(Piece.QUEEN, game.getBoard().getPieceAt(1, 7).getPiece());
    Move move10 = new Move(new Position(5, 4), new Position(5, 3));
    game.playMove(move10);
    Move move11 = new Move(new Position(1, 7), new Position(4, 4));
    game.playMove(move11);
    assertEquals(Piece.EMPTY, game.getBoard().getPieceAt(1, 7).getPiece());
    assertEquals(Piece.QUEEN, game.getBoard().getPieceAt(4, 4).getPiece());
  }

  @Test // must promote a pawn to Queen
  public void boardPromotionQueenBlackTest() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());

    Move move1 = new Move(new Position(4, 1), new Position(4, 3)); // w
    game.playMove(move1);
    Move move2 = new Move(new Position(3, 6), new Position(3, 4)); // b
    game.playMove(move2);

    Move move3 = new Move(new Position(4, 3), new Position(4, 4)); // w
    game.playMove(move3);
    Move move4 = new Move(new Position(3, 4), new Position(3, 3)); // b
    game.playMove(move4);

    Move move5 = new Move(new Position(2, 1), new Position(2, 3)); // w
    game.playMove(move5);
    Move move6 = new Move(new Position(3, 3), new Position(2, 2)); // b
    game.playMove(move6);

    Move move7 = new Move(new Position(3, 1), new Position(3, 2)); // w
    game.playMove(move7);
    Move move8 = new Move(new Position(2, 2), new Position(2, 1)); // b
    game.playMove(move8);

    Move move9 = new Move(new Position(3, 2), new Position(3, 3)); // w
    game.playMove(move9);
    PromoteMove move10 = new PromoteMove(new Position(2, 1), new Position(1, 0), Piece.QUEEN); // b
    game.playMove(move10);

    assertEquals(Piece.QUEEN, game.getBoard().getPieceAt(1, 0).getPiece());

    Move move15 = new Move(new Position(3, 3), new Position(3, 4)); // w
    game.playMove(move15);
    Move move16 = new Move(new Position(1, 0), new Position(3, 2)); // b
    game.playMove(move16);

    assertEquals(Piece.EMPTY, game.getBoard().getPieceAt(1, 0).getPiece());
    assertEquals(Piece.QUEEN, game.getBoard().getPieceAt(3, 2).getPiece());
  }

  @Test
  public void exceptionOnPromotionWithoutTargetPiece() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());

    Move move1 = new Move(new Position(4, 1), new Position(4, 3)); // w
    game.playMove(move1);
    Move move2 = new Move(new Position(3, 6), new Position(3, 4)); // b
    game.playMove(move2);

    Move move3 = new Move(new Position(4, 3), new Position(4, 4)); // w
    game.playMove(move3);
    Move move4 = new Move(new Position(3, 4), new Position(3, 3)); // b
    game.playMove(move4);

    Move move5 = new Move(new Position(2, 1), new Position(2, 3)); // w
    game.playMove(move5);
    Move move6 = new Move(new Position(3, 3), new Position(2, 2)); // b
    game.playMove(move6);

    Move move7 = new Move(new Position(3, 1), new Position(3, 2)); // w
    game.playMove(move7);
    Move move8 = new Move(new Position(2, 2), new Position(2, 1)); // b
    game.playMove(move8);

    Move move9 = new Move(new Position(3, 2), new Position(3, 3)); // w
    game.playMove(move9);
    Move move10 = new Move(new Position(2, 1), new Position(1, 0)); // b
    assertThrows(
        InvalidPromoteFormatException.class,
        () -> {
          game.playMove(move10);
        });
  }
}
