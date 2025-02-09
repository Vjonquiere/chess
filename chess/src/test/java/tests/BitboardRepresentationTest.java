package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import pdp.model.*;
import pdp.utils.Position;

public class BitboardRepresentationTest {
  Bitboard DEFAULT_WHITE_KING = new Bitboard(16L);
  Bitboard DEFAULT_WHITE_QUEEN = new Bitboard(8L);
  Bitboard DEFAULT_WHITE_BISHOPS = new Bitboard(36L);
  Bitboard DEFAULT_WHITE_ROOKS = new Bitboard(129L);
  Bitboard DEFAULT_WHITE_KNIGHTS = new Bitboard(66L);
  Bitboard DEFAULT_WHITE_PAWNS = new Bitboard(65280L);
  Bitboard DEFAULT_BLACK_KING = new Bitboard(1152921504606846976L);
  Bitboard DEFAULT_BLACK_QUEEN = new Bitboard(576460752303423488L);
  Bitboard DEFAULT_BLACK_BISHOPS = new Bitboard(2594073385365405696L);
  // Bitboard DEFAULT_BLACK_ROOKS = new Bitboard(9295429630892703744L);
  Bitboard DEFAULT_BLACK_KNIGHT = new Bitboard(4755801206503243776L);
  Bitboard DEFAULT_BLACK_PAWNS = new Bitboard(71776119061217280L);

  @Test
  public void testGetPawns() {
    // Test with default positions
    BitboardRepresentation board = new BitboardRepresentation();

    int x = 0;
    int y = 6;
    // Black pawns initial positions
    for (Position position : board.getPawns(false)) {
      assertEquals(x++, position.getX());
      assertEquals(y, position.getY());
    }

    x = 0;
    y = 1;
    // White pawns initial positions
    for (Position position : board.getPawns(true)) {
      assertEquals(x++, position.getX());
      assertEquals(y, position.getY());
    }

    board.movePiece(new Position(1, 0), new Position(2, 0)); // move pawn
    List<Position> pawns = board.getPawns(true);
    assertFalse(pawns.contains(new Position(1, 0)));
    assertTrue(pawns.contains(new Position(2, 0)));
  }

  @Test
  public void testGetQueens() {
    BitboardRepresentation board = new BitboardRepresentation();
    // Need move equals
  }

  @Test
  public void testGetBishops() {
    BitboardRepresentation board = new BitboardRepresentation();
    // Need move equals
  }

  @Test
  public void testGetRooks() {
    BitboardRepresentation board = new BitboardRepresentation();
    // Need move equals
  }

  @Test
  public void testGetKnights() {
    BitboardRepresentation board = new BitboardRepresentation();
    // Need move equals
  }

  @Test
  public void testGetKing() {
    BitboardRepresentation board = new BitboardRepresentation();
    // Need move equals
  }

  @Test
  public void testGetNbRows() {
    BitboardRepresentation board = new BitboardRepresentation();
    assertEquals(8, board.getNbRows());
  }

  @Test
  public void testGetNbCols() {
    BitboardRepresentation board = new BitboardRepresentation();
    assertEquals(8, board.getNbCols());
  }

  @Test
  public void testGetPieceAt() {
    BitboardRepresentation board = new BitboardRepresentation();

    for (Position position :
        board.getPawns(true)) { // Test getPieceAt on white pawns at the beginning of the game
      assertEquals(
          new ColoredPiece<Piece, Color>(Piece.PAWN, Color.WHITE),
          board.getPieceAt(position.getX(), position.getY()));
    }
    for (Position position :
        board.getPawns(false)) { // Test getPieceAt on white pawns at the beginning of the game
      assertEquals(
          new ColoredPiece<Piece, Color>(Piece.PAWN, Color.BLACK),
          board.getPieceAt(position.getX(), position.getY()));
    }
    assertNotEquals(
        new ColoredPiece<Piece, Color>(Piece.ROOK, Color.WHITE),
        board.getPieceAt(0, 1)); // Not a rook a pawn place

    assertEquals(
        new ColoredPiece<>(Piece.EMPTY, Color.EMPTY), board.getPieceAt(0, 3)); // Empty square
    board.movePiece(new Position(1, 0), new Position(3, 0)); // moveUp pawn
    assertEquals(
        new ColoredPiece<>(Piece.PAWN, Color.WHITE), board.getPieceAt(0, 3)); // Check if pawn moved
  }

  @Test
  public void testMovePiece() {
    BitboardRepresentation board = new BitboardRepresentation();

    // Test move on white pawn
    assertEquals(new ColoredPiece<>(Piece.PAWN, Color.WHITE), board.getPieceAt(0, 1));
    board.movePiece(new Position(1, 0), new Position(3, 0));
    assertEquals(new ColoredPiece<>(Piece.EMPTY, Color.EMPTY), board.getPieceAt(0, 1));
    assertEquals(new ColoredPiece<>(Piece.PAWN, Color.WHITE), board.getPieceAt(0, 3));

    // Test move on black knight
    assertEquals(new ColoredPiece<>(Piece.KNIGHT, Color.BLACK), board.getPieceAt(1, 7));
    board.movePiece(new Position(7, 1), new Position(5, 0));
    assertEquals(new ColoredPiece<>(Piece.EMPTY, Color.EMPTY), board.getPieceAt(1, 7));
    assertEquals(new ColoredPiece<>(Piece.KNIGHT, Color.BLACK), board.getPieceAt(0, 5));
  }

  @Test
  public void testGetAvailableMoves() {
    BitboardRepresentation board = new BitboardRepresentation();
    assertEquals(List.of(), board.getAvailableMoves(4, 0, false)); // King is blocked
    assertEquals(List.of(), board.getAvailableMoves(3, 0, false)); // Queen is blocked
    assertEquals(List.of(), board.getAvailableMoves(0, 0, false)); // Rook is blocked
    assertEquals(
        List.of(
            new Move(new Position(0, 6), new Position(2, 5)),
            new Move(new Position(0, 6), new Position(2, 7))),
        board.getAvailableMoves(6, 0, false)); // Knight move

    board.movePiece(new Position(1, 0), new Position(2, 0)); // move pawn
    assertEquals(
        List.of(new Move(new Position(2, 0), new Position(3, 0))),
        board.getAvailableMoves(0, 2, false)); // pawn move
    assertEquals(
        List.of(new Move(new Position(0, 0), new Position(1, 0))),
        board.getAvailableMoves(0, 0, false)); // Rook no more blocked
  }

  @Test
  public void testIsCheck() {
    BitboardRepresentation board = new BitboardRepresentation();
    assertFalse(board.isCheck(Color.BLACK));
    assertFalse(board.isCheck(Color.WHITE));

    board =
        new BitboardRepresentation(
            new Bitboard(1048576L),
            DEFAULT_WHITE_QUEEN,
            DEFAULT_WHITE_BISHOPS,
            DEFAULT_WHITE_ROOKS,
            DEFAULT_WHITE_KNIGHTS,
            DEFAULT_WHITE_PAWNS,
            DEFAULT_BLACK_KING,
            DEFAULT_BLACK_QUEEN,
            DEFAULT_BLACK_BISHOPS,
            new Bitboard(17592186044416L),
            DEFAULT_BLACK_KNIGHT,
            DEFAULT_BLACK_PAWNS);
    assertTrue(board.isCheck(Color.WHITE));
    assertFalse(board.isCheck(Color.BLACK));
    assertFalse(board.isCheckMate(Color.WHITE));
    board.movePiece(new Position(2, 4), new Position(0, 4)); // move king
    board.movePiece(new Position(0, 3), new Position(2, 0)); // move queen
    board.movePiece(new Position(0, 5), new Position(2, 6));
    board.movePiece(new Position(1, 4), new Position(0, 3));
    board.movePiece(new Position(1, 0), new Position(0, 5));
    board.movePiece(new Position(0, 6), new Position(3, 0));
    board.movePiece(new Position(2, 6), new Position(0, 6));
    board.movePiece(new Position(2, 0), new Position(7, 0));
    // System.out.println(board.getPieceAt(4, 2).getPiece());
    assertTrue(board.isCheckMate(Color.WHITE));
  }

  @Test
  public void testIsCheckMate() {
    BitboardRepresentation board = new BitboardRepresentation();
    assertFalse(board.isCheckMate(Color.BLACK));
    assertFalse(board.isCheckMate(Color.WHITE));
  }

  public void deleteAllPiecesExceptThosePositions(
      BitboardRepresentation board, List<Position> posListWhite, List<Position> posListBlack) {
    for (int yWhite = 0; yWhite <= 1; yWhite++) {
      for (int xWhite = 0; xWhite <= 7; xWhite++) {
        Position pos = new Position(yWhite, xWhite);
        if (!posListWhite.contains(pos)) {
          board.deletePieceAt(xWhite, yWhite);
        }
      }
    }

    for (int yBlack = 6; yBlack <= 7; yBlack++) {
      for (int xBlack = 0; xBlack <= 7; xBlack++) {
        Position pos = new Position(yBlack, xBlack);
        if (!posListBlack.contains(pos)) {
          board.deletePieceAt(xBlack, yBlack);
        }
      }
    }
  }

  @Test
  public void testIsStaleMateShouldBeFalse() {
    BitboardRepresentation board = new BitboardRepresentation();
    assertFalse(board.isStaleMate(Color.BLACK, Color.WHITE));
    assertFalse(board.isStaleMate(Color.WHITE, Color.WHITE));
  }

  @Test
  public void testIsStaleMateShouldBeTrue() {
    BitboardRepresentation board = new BitboardRepresentation();

    // Simulated position:
    // white king on h1 --- black king on f2 and black queen on g3
    // white to move

    Position initWhiteKingPos = new Position(0, 4);
    Position initBlackKingPos = new Position(7, 4);
    Position initBlackQueenPos = new Position(7, 3);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListBlack.add(initBlackKingPos);
    posListBlack.add(initBlackQueenPos);

    Position finalWhiteKingPos = new Position(0, 7);
    Position finalBlackKingPos = new Position(1, 5);
    Position finalBlackQueenPos = new Position(2, 6);

    // Delete pieces except white king, black king and black queen
    deleteAllPiecesExceptThosePositions(board, posListWhite, posListBlack);

    // Moves pieces to wanted positions
    board.movePiece(initWhiteKingPos, finalWhiteKingPos);
    board.movePiece(initBlackKingPos, finalBlackKingPos);
    board.movePiece(initBlackQueenPos, finalBlackQueenPos);

    // White to move
    assertTrue(board.isStaleMate(Color.WHITE, Color.WHITE));
  }

  @Test
  public void testDrawByInsufficientMaterialKingVsKing() {
    BitboardRepresentation board = new BitboardRepresentation();

    Position initWhiteKingPos = new Position(0, 4);
    Position initBlackKingPos = new Position(7, 4);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListBlack.add(initBlackKingPos);

    deleteAllPiecesExceptThosePositions(board, posListWhite, posListBlack);

    assertTrue(board.isDrawByInsufficientMaterial());
  }

  @Test
  public void testDrawByInsufficientMaterialKingAndBishopVsKing() {
    BitboardRepresentation board = new BitboardRepresentation();

    Position initWhiteKingPos = new Position(0, 4);
    Position posWhiteBishop = new Position(0, 2);
    Position initBlackKingPos = new Position(7, 4);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListWhite.add(posWhiteBishop);
    posListBlack.add(initBlackKingPos);

    deleteAllPiecesExceptThosePositions(board, posListWhite, posListBlack);

    assertTrue(board.isDrawByInsufficientMaterial());
  }

  @Test
  public void testDrawByInsufficientMaterialKingAndKnightVsKing() {
    BitboardRepresentation board = new BitboardRepresentation();

    Position initWhiteKingPos = new Position(0, 4);
    Position posWhiteKnight = new Position(0, 1);
    Position initBlackKingPos = new Position(7, 4);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListWhite.add(posWhiteKnight);
    posListBlack.add(initBlackKingPos);

    deleteAllPiecesExceptThosePositions(board, posListWhite, posListBlack);

    assertTrue(board.isDrawByInsufficientMaterial());
  }

  @Test
  public void testDrawByInsufficientMaterialKingAndBishopVsKingAndSameColorBishop() {
    BitboardRepresentation board = new BitboardRepresentation();

    Position initWhiteKingPos = new Position(0, 4);
    Position posWhiteBishop = new Position(0, 2);
    Position initBlackKingPos = new Position(7, 4);
    Position posBlackBishop = new Position(7, 5);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListWhite.add(posWhiteBishop);
    posListBlack.add(initBlackKingPos);
    posListBlack.add(posBlackBishop);

    deleteAllPiecesExceptThosePositions(board, posListWhite, posListBlack);

    assertTrue(board.isDrawByInsufficientMaterial());
  }

  @Test
  public void testDrawByInsufficientMaterialNotDrawDueToPawn() {
    BitboardRepresentation board = new BitboardRepresentation();

    Position initWhiteKingPos = new Position(0, 4);
    Position posWhitePawn = new Position(1, 0);
    Position initBlackKingPos = new Position(7, 4);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListWhite.add(posWhitePawn);
    posListBlack.add(initBlackKingPos);

    deleteAllPiecesExceptThosePositions(board, posListWhite, posListBlack);

    assertFalse(board.isDrawByInsufficientMaterial());
  }

  @Test
  public void testIsPawnPromotingShouldReturnFalse() {
    BitboardRepresentation board = new BitboardRepresentation();
    boolean resultWhite;
    boolean resultBlack;
    boolean white = true;
    int xForWhite = 0, yForWhite = 0;
    int xForBlack = 0, yForBlack = 7;
    int nbLoops = 7;

    for (int i = 0; i < nbLoops; i++) {
      resultWhite = board.isPawnPromoting(xForWhite, yForWhite, white);
      resultBlack = board.isPawnPromoting(xForBlack, yForBlack, !white);
      assertFalse(resultWhite, "Pawn should not be able to promote !");
      assertFalse(resultBlack, "Pawn should not be able to promote !");
      yForWhite++;
      yForBlack--;
    }
  }

  @Test
  public void testIsPawnPromotingShouldReturnTrue() {
    BitboardRepresentation board = new BitboardRepresentation();
    boolean white = true;

    // Move piece blocking the last rank position before moving the white pawn
    Position whiteBlockerCurrPos = new Position(7, 0);
    Position whiteBlockerNextPos = new Position(4, 0);
    board.movePiece(whiteBlockerCurrPos, whiteBlockerNextPos);

    // Move pawn now
    Position whitePawnSrcPos = new Position(1, 0);
    Position whitePawnDstPos = new Position(7, 0);
    board.movePiece(whitePawnSrcPos, whitePawnDstPos);

    boolean resultWhite = board.isPawnPromoting(0, 7, white);
    assertTrue(resultWhite, "White pawn should be able to promote !");

    // Same thing for black
    Position blackBlockerCurrPos = new Position(0, 7);
    Position blackBlockerNextPos = new Position(3, 7);
    board.movePiece(blackBlockerCurrPos, blackBlockerNextPos);

    Position blackPawnSrcPos = new Position(6, 7);
    Position blackPawnDstPos = new Position(0, 7);
    board.movePiece(blackPawnSrcPos, blackPawnDstPos);

    boolean resultBlack = board.isPawnPromoting(7, 0, !white);
    assertTrue(resultBlack, "Black pawn should be able to promote !");
  }

  @Test
  public void testPromotePawnShouldBeSuccess() {
    BitboardRepresentation board = new BitboardRepresentation();
    boolean white = true;

    // Move piece blocking the last rank position before moving the white pawn
    Position whiteBlockerCurrPos = new Position(7, 0);
    Position whiteBlockerNextPos = new Position(4, 0);
    board.movePiece(whiteBlockerCurrPos, whiteBlockerNextPos);

    // Move pawn now
    Position whitePawnSrcPos = new Position(1, 0);
    Position whitePawnDstPos = new Position(7, 0);
    board.movePiece(whitePawnSrcPos, whitePawnDstPos);
    board.promotePawn(0, 7, white, Piece.QUEEN);

    assertNotNull(board.getPieceAt(0, 7));
    assertEquals(Piece.QUEEN, board.getPieceAt(0, 7).getPiece());

    // Same thing for black
    Position blackBlockerCurrPos = new Position(0, 7);
    Position blackBlockerNextPos = new Position(3, 7);
    board.movePiece(blackBlockerCurrPos, blackBlockerNextPos);

    Position blackPawnSrcPos = new Position(6, 7);
    Position blackPawnDstPos = new Position(0, 7);
    board.movePiece(blackPawnSrcPos, blackPawnDstPos);
    board.promotePawn(7, 0, !white, Piece.QUEEN);

    assertNotNull(board.getPieceAt(7, 0));
    assertEquals(Piece.QUEEN, board.getPieceAt(7, 0).getPiece());
  }

  @Test
  public void testPromotePawnShouldBeFailure() {
    BitboardRepresentation board = new BitboardRepresentation();
    boolean white = true;

    // Move piece blocking the last rank position before moving the white pawn
    Position whiteBlockerCurrPos = new Position(7, 0);
    Position whiteBlockerNextPos = new Position(4, 0);
    board.movePiece(whiteBlockerCurrPos, whiteBlockerNextPos);

    // Move pawn now
    Position whitePawnSrcPos = new Position(1, 0);
    Position whitePawnDstPos = new Position(7, 0);
    board.movePiece(whitePawnSrcPos, whitePawnDstPos);

    // Ensure pawn is remaining at the promotion position before trying invalid promotion
    assertEquals(
        Piece.PAWN,
        board.getPieceAt(0, 7).getPiece(),
        "White pawn should still be at promotion square before invalid promotion !");

    // Attempt invalid promotions
    board.promotePawn(0, 7, white, Piece.KING);
    board.promotePawn(0, 7, white, Piece.PAWN);

    assertEquals(
        Piece.PAWN,
        board.getPieceAt(0, 7).getPiece(),
        "White pawn should remain unchanged after invalid promotion !");

    // Same process for black
    Position blackBlockerCurrPos = new Position(0, 7);
    Position blackBlockerNextPos = new Position(3, 7);
    board.movePiece(blackBlockerCurrPos, blackBlockerNextPos);

    Position blackPawnSrcPos = new Position(6, 7);
    Position blackPawnDstPos = new Position(0, 7);
    board.movePiece(blackPawnSrcPos, blackPawnDstPos);

    assertEquals(
        Piece.PAWN,
        board.getPieceAt(7, 0).getPiece(),
        "Black pawn should still be at promotion square before invalid promotion!");

    board.promotePawn(7, 0, !white, Piece.KING);
    board.promotePawn(7, 0, !white, Piece.PAWN);

    assertEquals(
        Piece.PAWN,
        board.getPieceAt(7, 0).getPiece(),
        "Black pawn should remain unchanged after invalid promotion!");
  }

  @Test
  public void testPromotePawnShouldNotWorkForOtherPieces() {
    BitboardRepresentation board = new BitboardRepresentation();
    boolean white = true;

    Position whiteBlockerCurrPos = new Position(7, 0);
    Position whiteBlockerNextPos = new Position(4, 0);
    board.movePiece(whiteBlockerCurrPos, whiteBlockerNextPos);

    // Move a white knight to the last rank
    Position whiteKnightSrcPos = new Position(0, 1);
    Position whiteKnightDstPos = new Position(7, 0);
    board.movePiece(whiteKnightSrcPos, whiteKnightDstPos);

    // Try to promote the knight
    board.promotePawn(0, 7, white, Piece.QUEEN);

    assertEquals(
        Piece.KNIGHT, board.getPieceAt(0, 7).getPiece(), "White knight should not be promotable !");

    // Same for black but with a bishop for instance
    Position blackBlockerCurrPos = new Position(0, 7);
    Position blackBlockerNextPos = new Position(3, 7);
    board.movePiece(blackBlockerCurrPos, blackBlockerNextPos);

    // Move a black bishop to the first rank
    Position blackBishopSrcPos = new Position(7, 2);
    Position blackBishopDstPos = new Position(0, 7);
    board.movePiece(blackBishopSrcPos, blackBishopDstPos);

    // Try to promote the bishop
    board.promotePawn(7, 0, !white, Piece.QUEEN);

    assertEquals(
        Piece.BISHOP, board.getPieceAt(7, 0).getPiece(), "Black bishop should not be promotable !");
  }

  @Test
  public void testDeletePieceAt() {
    BitboardRepresentation board = new BitboardRepresentation();
    board.deletePieceAt(0, 0);
  }
}
