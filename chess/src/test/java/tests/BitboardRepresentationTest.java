package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.model.board.Bitboard;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
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

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @BeforeEach
  void setUpConsole() {
    System.setOut(new PrintStream(outputStream));
    System.setErr(new PrintStream(outputStream));
  }

  @AfterEach
  void tearDownConsole() {
    System.setOut(originalOut);
    System.setErr(originalErr);
    outputStream.reset();
  }

  @Test
  public void testGetPawns() {
    // Test with default positions
    BitboardRepresentation board = new BitboardRepresentation();

    int x = 0;
    int y = 6;
    // Black pawns initial positions
    for (Position position : board.getPawns(false)) {
      assertEquals(x++, position.x());
      assertEquals(y, position.y());
    }

    x = 0;
    y = 1;
    // White pawns initial positions
    for (Position position : board.getPawns(true)) {
      assertEquals(x++, position.x());
      assertEquals(y, position.y());
    }

    board.movePiece(new Position(0, 1), new Position(0, 2)); // move pawn
    List<Position> pawns = board.getPawns(true);
    assertFalse(pawns.contains(new Position(0, 1)));
    assertTrue(pawns.contains(new Position(0, 2)));
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
          new ColoredPiece(Piece.PAWN, Color.WHITE), board.getPieceAt(position.x(), position.y()));
    }
    for (Position position :
        board.getPawns(false)) { // Test getPieceAt on white pawns at the beginning of the game
      assertEquals(
          new ColoredPiece(Piece.PAWN, Color.BLACK), board.getPieceAt(position.x(), position.y()));
    }
    assertNotEquals(
        new ColoredPiece(Piece.ROOK, Color.WHITE),
        board.getPieceAt(0, 1)); // Not a rook a pawn place

    assertEquals(
        new ColoredPiece(Piece.EMPTY, Color.EMPTY), board.getPieceAt(0, 3)); // Empty square
    board.movePiece(new Position(0, 1), new Position(0, 3)); // moveUp pawn
    assertEquals(
        new ColoredPiece(Piece.PAWN, Color.WHITE), board.getPieceAt(0, 3)); // Check if pawn moved
  }

  @Test
  public void testMovePiece() {
    BitboardRepresentation board = new BitboardRepresentation();

    // Test move on white pawn
    assertEquals(new ColoredPiece(Piece.PAWN, Color.WHITE), board.getPieceAt(0, 1));
    board.movePiece(new Position(0, 1), new Position(0, 3));
    assertEquals(new ColoredPiece(Piece.EMPTY, Color.EMPTY), board.getPieceAt(0, 1));
    assertEquals(new ColoredPiece(Piece.PAWN, Color.WHITE), board.getPieceAt(0, 3));

    // Test move on black knight
    assertEquals(new ColoredPiece(Piece.KNIGHT, Color.BLACK), board.getPieceAt(1, 7));
    board.movePiece(new Position(1, 7), new Position(0, 5));
    assertEquals(new ColoredPiece(Piece.EMPTY, Color.EMPTY), board.getPieceAt(1, 7));
    assertEquals(new ColoredPiece(Piece.KNIGHT, Color.BLACK), board.getPieceAt(0, 5));

    // Test move rook
    assertEquals(new ColoredPiece(Piece.ROOK, Color.WHITE), board.getPieceAt(0, 0));
    board.movePiece(new Position(0, 0), new Position(0, 2));
    assertEquals(new ColoredPiece(Piece.EMPTY, Color.EMPTY), board.getPieceAt(0, 0));
    assertEquals(new ColoredPiece(Piece.ROOK, Color.WHITE), board.getPieceAt(0, 2));

    // Test move bishop
    assertEquals(new ColoredPiece(Piece.BISHOP, Color.BLACK), board.getPieceAt(2, 7));
    board.movePiece(new Position(2, 7), new Position(7, 2));
    assertEquals(new ColoredPiece(Piece.EMPTY, Color.EMPTY), board.getPieceAt(2, 7));
    assertEquals(new ColoredPiece(Piece.BISHOP, Color.BLACK), board.getPieceAt(7, 2));

    // Test move king
    assertEquals(new ColoredPiece(Piece.KING, Color.WHITE), board.getPieceAt(4, 0));
    board.movePiece(new Position(4, 0), new Position(4, 2));
    assertEquals(new ColoredPiece(Piece.EMPTY, Color.EMPTY), board.getPieceAt(4, 0));
    assertEquals(new ColoredPiece(Piece.KING, Color.WHITE), board.getPieceAt(4, 2));

    // Test move queen
    assertEquals(new ColoredPiece(Piece.QUEEN, Color.BLACK), board.getPieceAt(3, 7));
    board.movePiece(new Position(3, 7), new Position(3, 4));
    assertEquals(new ColoredPiece(Piece.EMPTY, Color.EMPTY), board.getPieceAt(3, 7));
    assertEquals(new ColoredPiece(Piece.QUEEN, Color.BLACK), board.getPieceAt(3, 4));

    // Test move king
    assertEquals(new ColoredPiece(Piece.KING, Color.BLACK), board.getPieceAt(4, 7));
    board.movePiece(new Position(4, 7), new Position(3, 7));
    assertEquals(new ColoredPiece(Piece.EMPTY, Color.EMPTY), board.getPieceAt(4, 7));
    assertEquals(new ColoredPiece(Piece.KING, Color.BLACK), board.getPieceAt(3, 7));

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          board.movePiece(new Position(5, 5), new Position(0, 0));
        }); // Try to move an empty square
  }

  @Test
  public void testGetAvailableMoves() {
    BitboardRepresentation board = new BitboardRepresentation();
    assertEquals(List.of(), board.getAvailableMoves(4, 0, false)); // King is blocked
    assertEquals(List.of(), board.getAvailableMoves(3, 0, false)); // Queen is blocked
    assertEquals(List.of(), board.getAvailableMoves(0, 0, false)); // Rook is blocked
    assertEquals(
        List.of(
            new Move(new Position(6, 0), new Position(5, 2)),
            new Move(new Position(6, 0), new Position(7, 2))),
        board.getAvailableMoves(6, 0, false)); // Knight move

    board.movePiece(new Position(0, 1), new Position(0, 2)); // move pawn
    assertEquals(
        List.of(new Move(new Position(0, 2), new Position(0, 3))),
        board.getAvailableMoves(0, 2, false)); // pawn move
    assertEquals(
        List.of(new Move(new Position(0, 0), new Position(0, 1))),
        board.getAvailableMoves(0, 0, false)); // Rook no more blocked
    assertEquals(List.of(), board.getAvailableMoves(5, 5, false)); // Empty square has no moves
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
    board.movePiece(new Position(4, 2), new Position(4, 0)); // move king
    board.movePiece(new Position(3, 0), new Position(0, 2)); // move queen
    board.movePiece(new Position(5, 0), new Position(6, 2));
    board.movePiece(new Position(4, 1), new Position(3, 0));
    board.movePiece(new Position(0, 1), new Position(5, 0));
    board.movePiece(new Position(6, 0), new Position(0, 3));
    board.movePiece(new Position(6, 2), new Position(6, 0));
    board.movePiece(new Position(0, 2), new Position(0, 7));
    // System.out.println(board.getPieceAt(4, 2).getPiece());
    assertTrue(board.isCheckMate(Color.WHITE));
  }

  @Test
  public void testIsCheckMate() {
    BitboardRepresentation board = new BitboardRepresentation();
    assertFalse(board.isCheckMate(Color.BLACK));
    assertFalse(board.isCheckMate(Color.WHITE));

    // scholar's mate -> white checkmate
    board.movePiece(new Position(6, 1), new Position(6, 3));
    assertFalse(board.isCheckMate(Color.WHITE));
    board.movePiece(new Position(4, 6), new Position(4, 4));
    assertFalse(board.isCheckMate(Color.WHITE));
    board.movePiece(new Position(5, 1), new Position(5, 2));
    assertFalse(board.isCheckMate(Color.WHITE));
    board.movePiece(new Position(3, 7), new Position(7, 3));
    assertTrue(board.isCheckMate(Color.WHITE));

    // scholar's mate -> black checkmate
    board = new BitboardRepresentation();
    assertFalse(board.isCheckMate(Color.BLACK));
    assertFalse(board.isCheckMate(Color.WHITE));
    board.movePiece(new Position(4, 1), new Position(4, 3));
    assertFalse(board.isCheckMate(Color.BLACK));
    board.movePiece(new Position(4, 6), new Position(4, 4));
    assertFalse(board.isCheckMate(Color.BLACK));
    board.movePiece(new Position(5, 0), new Position(2, 3));
    assertFalse(board.isCheckMate(Color.BLACK));
    board.movePiece(new Position(5, 7), new Position(2, 4));
    assertFalse(board.isCheckMate(Color.BLACK));
    board.movePiece(new Position(3, 0), new Position(5, 2));
    assertFalse(board.isCheckMate(Color.BLACK));
    board.movePiece(new Position(1, 7), new Position(2, 5));
    assertFalse(board.isCheckMate(Color.BLACK));
    board.deletePieceAt(5, 6);
    board.movePiece(new Position(5, 2), new Position(5, 6));
    assertTrue(board.isCheckMate(Color.BLACK));
  }

  public static void deleteAllPiecesExceptThosePositions(
      BitboardRepresentation board, List<Position> posListWhite, List<Position> posListBlack) {
    for (int yWhite = 0; yWhite <= 1; yWhite++) {
      for (int xWhite = 0; xWhite <= 7; xWhite++) {
        Position pos = new Position(xWhite, yWhite);
        if (!posListWhite.contains(pos)) {
          board.deletePieceAt(xWhite, yWhite);
        }
      }
    }

    for (int yBlack = 6; yBlack <= 7; yBlack++) {
      for (int xBlack = 0; xBlack <= 7; xBlack++) {
        Position pos = new Position(xBlack, yBlack);
        if (!posListBlack.contains(pos)) {
          board.deletePieceAt(xBlack, yBlack);
        }
      }
    }
  }

  public static void deleteAllPiecesExceptThosePositionsBoard(
      BoardRepresentation board, List<Position> posListWhite, List<Position> posListBlack) {
    for (int yWhite = 0; yWhite <= 1; yWhite++) {
      for (int xWhite = 0; xWhite <= 7; xWhite++) {
        Position pos = new Position(xWhite, yWhite);
        if (!posListWhite.contains(pos)) {
          board.deletePieceAt(xWhite, yWhite);
        }
      }
    }

    for (int yBlack = 6; yBlack <= 7; yBlack++) {
      for (int xBlack = 0; xBlack <= 7; xBlack++) {
        Position pos = new Position(xBlack, yBlack);
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

    Position initWhiteKingPos = new Position(4, 0);
    Position initBlackKingPos = new Position(4, 7);
    Position initBlackQueenPos = new Position(3, 7);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListBlack.add(initBlackKingPos);
    posListBlack.add(initBlackQueenPos);

    Position finalWhiteKingPos = new Position(7, 0);
    Position finalBlackKingPos = new Position(5, 1);
    Position finalBlackQueenPos = new Position(6, 2);

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

    Position initWhiteKingPos = new Position(4, 0);
    Position initBlackKingPos = new Position(4, 7);

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

    Position initWhiteKingPos = new Position(4, 0);
    Position posWhiteBishop = new Position(2, 0);
    Position initBlackKingPos = new Position(4, 7);

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

    Position initWhiteKingPos = new Position(4, 0);
    Position posWhiteKnight = new Position(1, 0);
    Position initBlackKingPos = new Position(4, 7);

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

    Position initWhiteKingPos = new Position(4, 0);
    Position posWhiteBishop = new Position(2, 0);
    Position initBlackKingPos = new Position(4, 7);
    Position posBlackBishop = new Position(5, 7);

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

    Position initWhiteKingPos = new Position(4, 0);
    Position posWhitePawn = new Position(0, 1);
    Position initBlackKingPos = new Position(4, 7);

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
    Position whiteBlockerCurrPos = new Position(0, 7);
    Position whiteBlockerNextPos = new Position(0, 4);
    board.movePiece(whiteBlockerCurrPos, whiteBlockerNextPos);

    // Move pawn now
    Position whitePawnSrcPos = new Position(0, 1);
    Position whitePawnDstPos = new Position(0, 7);
    board.movePiece(whitePawnSrcPos, whitePawnDstPos);

    boolean resultWhite = board.isPawnPromoting(0, 7, white);
    assertTrue(resultWhite, "White pawn should be able to promote !");

    // Same thing for black
    Position blackBlockerCurrPos = new Position(7, 0);
    Position blackBlockerNextPos = new Position(7, 3);
    board.movePiece(blackBlockerCurrPos, blackBlockerNextPos);

    Position blackPawnSrcPos = new Position(7, 6);
    Position blackPawnDstPos = new Position(7, 0);
    board.movePiece(blackPawnSrcPos, blackPawnDstPos);

    boolean resultBlack = board.isPawnPromoting(7, 0, !white);
    assertTrue(resultBlack, "Black pawn should be able to promote !");
  }

  @Test
  public void testPromotePawnShouldBeSuccessForKnightAndQueen() {
    BitboardRepresentation board = new BitboardRepresentation();
    boolean white = true;

    // Move piece blocking the last rank position before moving the white pawn
    Position whiteBlockerCurrPos = new Position(0, 7);
    Position whiteBlockerNextPos = new Position(0, 4);
    board.movePiece(whiteBlockerCurrPos, whiteBlockerNextPos);

    // Move pawn now
    Position whitePawnSrcPos = new Position(0, 1);
    Position whitePawnDstPos = new Position(0, 7);
    board.movePiece(whitePawnSrcPos, whitePawnDstPos);
    board.promotePawn(0, 7, white, Piece.KNIGHT);

    assertNotNull(board.getPieceAt(0, 7));
    assertEquals(Piece.KNIGHT, board.getPieceAt(0, 7).piece);

    // Same thing for black
    Position blackBlockerCurrPos = new Position(7, 0);
    Position blackBlockerNextPos = new Position(7, 3);
    board.movePiece(blackBlockerCurrPos, blackBlockerNextPos);

    Position blackPawnSrcPos = new Position(7, 6);
    Position blackPawnDstPos = new Position(7, 0);
    board.movePiece(blackPawnSrcPos, blackPawnDstPos);
    board.promotePawn(7, 0, !white, Piece.QUEEN);

    assertNotNull(board.getPieceAt(7, 0));
    assertEquals(Piece.QUEEN, board.getPieceAt(7, 0).piece);
  }

  @Test
  public void testPromotePawnShouldBeSuccessForBishopAndRook() {
    BitboardRepresentation board = new BitboardRepresentation();
    boolean white = true;

    // Move piece blocking the last rank position before moving the white pawn
    Position whiteBlockerCurrPos = new Position(0, 7);
    Position whiteBlockerNextPos = new Position(0, 4);
    board.movePiece(whiteBlockerCurrPos, whiteBlockerNextPos);

    // Move pawn now
    Position whitePawnSrcPos = new Position(0, 1);
    Position whitePawnDstPos = new Position(0, 7);
    board.movePiece(whitePawnSrcPos, whitePawnDstPos);
    board.promotePawn(0, 7, white, Piece.ROOK);

    assertNotNull(board.getPieceAt(0, 7));
    assertEquals(Piece.ROOK, board.getPieceAt(0, 7).piece);

    // Same thing for black
    Position blackBlockerCurrPos = new Position(7, 0);
    Position blackBlockerNextPos = new Position(7, 3);
    board.movePiece(blackBlockerCurrPos, blackBlockerNextPos);

    Position blackPawnSrcPos = new Position(7, 6);
    Position blackPawnDstPos = new Position(7, 0);
    board.movePiece(blackPawnSrcPos, blackPawnDstPos);
    board.promotePawn(7, 0, !white, Piece.BISHOP);

    assertNotNull(board.getPieceAt(7, 0));
    assertEquals(Piece.BISHOP, board.getPieceAt(7, 0).piece);
  }

  @Test
  public void testPromotePawnShouldBeFailure() {
    BitboardRepresentation board = new BitboardRepresentation();
    boolean white = true;

    // Move piece blocking the last rank position before moving the white pawn
    Position whiteBlockerCurrPos = new Position(0, 7);
    Position whiteBlockerNextPos = new Position(0, 4);
    board.movePiece(whiteBlockerCurrPos, whiteBlockerNextPos);

    // Move pawn now
    Position whitePawnSrcPos = new Position(0, 1);
    Position whitePawnDstPos = new Position(0, 7);
    board.movePiece(whitePawnSrcPos, whitePawnDstPos);

    // Ensure pawn is remaining at the promotion position before trying invalid promotion
    assertEquals(
        Piece.PAWN,
        board.getPieceAt(0, 7).piece,
        "White pawn should still be at promotion square before invalid promotion !");

    // Attempt invalid promotions
    board.promotePawn(0, 7, white, Piece.KING);
    board.promotePawn(0, 7, white, Piece.PAWN);

    assertEquals(
        Piece.PAWN,
        board.getPieceAt(0, 7).piece,
        "White pawn should remain unchanged after invalid promotion !");

    // Same process for black
    Position blackBlockerCurrPos = new Position(7, 0);
    Position blackBlockerNextPos = new Position(7, 3);
    board.movePiece(blackBlockerCurrPos, blackBlockerNextPos);

    Position blackPawnSrcPos = new Position(7, 6);
    Position blackPawnDstPos = new Position(7, 0);
    board.movePiece(blackPawnSrcPos, blackPawnDstPos);

    assertEquals(
        Piece.PAWN,
        board.getPieceAt(7, 0).piece,
        "Black pawn should still be at promotion square before invalid promotion!");

    board.promotePawn(7, 0, !white, Piece.KING);
    board.promotePawn(7, 0, !white, Piece.PAWN);

    assertEquals(
        Piece.PAWN,
        board.getPieceAt(7, 0).piece,
        "Black pawn should remain unchanged after invalid promotion!");
  }

  @Test
  public void testPromotePawnShouldNotWorkForOtherPieces() {
    BitboardRepresentation board = new BitboardRepresentation();
    boolean white = true;

    Position whiteBlockerCurrPos = new Position(0, 7);
    Position whiteBlockerNextPos = new Position(0, 4);
    board.movePiece(whiteBlockerCurrPos, whiteBlockerNextPos);

    // Move a white knight to the last rank
    Position whiteKnightSrcPos = new Position(1, 0);
    Position whiteKnightDstPos = new Position(0, 7);
    board.movePiece(whiteKnightSrcPos, whiteKnightDstPos);

    // Try to promote the knight
    board.promotePawn(0, 7, white, Piece.QUEEN);

    assertEquals(
        Piece.KNIGHT, board.getPieceAt(0, 7).piece, "White knight should not be promotable !");

    // Same for black but with a bishop for instance
    Position blackBlockerCurrPos = new Position(7, 0);
    Position blackBlockerNextPos = new Position(7, 3);
    board.movePiece(blackBlockerCurrPos, blackBlockerNextPos);

    // Move a black bishop to the first rank
    Position blackBishopSrcPos = new Position(2, 7);
    Position blackBishopDstPos = new Position(7, 0);
    board.movePiece(blackBishopSrcPos, blackBishopDstPos);

    // Try to promote the bishop
    board.promotePawn(7, 0, !white, Piece.QUEEN);

    assertEquals(
        Piece.BISHOP, board.getPieceAt(7, 0).piece, "Black bishop should not be promotable !");
  }

  @Test
  public void testPromotePawnOnEmptySquare() {
    BitboardRepresentation board =
        new BitboardRepresentation(
            DEFAULT_WHITE_KING,
            new Bitboard(0L),
            new Bitboard(65536L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            DEFAULT_BLACK_KING,
            new Bitboard(0L),
            new Bitboard(17592186044416L),
            new Bitboard(0L),
            new Bitboard(4294967296L),
            new Bitboard(0L));
    assertEquals(new ColoredPiece(Piece.EMPTY, Color.EMPTY), board.getPieceAt(7, 0));
    board.promotePawn(7, 0, true, Piece.QUEEN);
    assertEquals(
        new ColoredPiece(Piece.EMPTY, Color.EMPTY),
        board.getPieceAt(7, 0)); // check nothing changed
  }

  @Test
  public void testHasEnoughMaterialToMateWhiteQueen() {
    BitboardRepresentation board = new BitboardRepresentation();

    // Should be true when game starts
    assertTrue(board.hasEnoughMaterialToMate(true));

    List<Position> whitePos = new ArrayList<>();
    List<Position> blackPos = new ArrayList<>();

    Position posWhiteKing = new Position(4, 0);
    whitePos.add(posWhiteKing);
    Position posWhiteQueen = new Position(3, 0);
    whitePos.add(posWhiteQueen);

    deleteAllPiecesExceptThosePositions(board, whitePos, blackPos);

    assertTrue(board.hasEnoughMaterialToMate(true));
  }

  @Test
  public void testHasEnoughMaterialToMateWhiteRook() {
    BitboardRepresentation board = new BitboardRepresentation();

    List<Position> whitePos = new ArrayList<>();
    List<Position> blackPos = new ArrayList<>();

    Position posWhiteKing = new Position(4, 0);
    whitePos.add(posWhiteKing);
    Position posWhiteRook = new Position(0, 0);
    whitePos.add(posWhiteRook);

    deleteAllPiecesExceptThosePositions(board, whitePos, blackPos);

    assertTrue(board.hasEnoughMaterialToMate(true));
  }

  @Test
  public void testHasEnoughMaterialToMateWhitePawn() {
    BitboardRepresentation board = new BitboardRepresentation();

    List<Position> whitePos = new ArrayList<>();
    List<Position> blackPos = new ArrayList<>();

    Position posWhiteKing = new Position(4, 0);
    whitePos.add(posWhiteKing);
    Position posWhitePawn = new Position(0, 1);
    whitePos.add(posWhitePawn);

    deleteAllPiecesExceptThosePositions(board, whitePos, blackPos);

    assertTrue(board.hasEnoughMaterialToMate(true));
  }

  @Test
  public void testHasEnoughMaterialToMateWhiteKnightAndBishop() {
    BitboardRepresentation board = new BitboardRepresentation();

    List<Position> whitePos = new ArrayList<>();
    List<Position> blackPos = new ArrayList<>();

    Position posWhiteKing = new Position(4, 0);
    whitePos.add(posWhiteKing);
    Position posWhiteBishop = new Position(2, 0);
    whitePos.add(posWhiteBishop);
    Position posWhiteKnight = new Position(1, 0);
    whitePos.add(posWhiteKnight);

    deleteAllPiecesExceptThosePositions(board, whitePos, blackPos);

    assertTrue(board.hasEnoughMaterialToMate(true));
  }

  @Test
  public void testHasEnoughMaterialToMateWhiteTwoKnights() {
    BitboardRepresentation board = new BitboardRepresentation();

    List<Position> whitePos = new ArrayList<>();
    List<Position> blackPos = new ArrayList<>();

    Position posWhiteKing = new Position(4, 0);
    whitePos.add(posWhiteKing);
    Position posWhiteKnight1 = new Position(6, 0);
    whitePos.add(posWhiteKnight1);
    Position posWhiteKnight2 = new Position(1, 0);
    whitePos.add(posWhiteKnight2);

    deleteAllPiecesExceptThosePositions(board, whitePos, blackPos);

    assertTrue(board.hasEnoughMaterialToMate(true));
  }

  @Test
  public void testHasEnoughMaterialToMateWhiteTwoBishops() {
    BitboardRepresentation board = new BitboardRepresentation();

    List<Position> whitePos = new ArrayList<>();
    List<Position> blackPos = new ArrayList<>();

    Position posWhiteKing = new Position(4, 0);
    whitePos.add(posWhiteKing);
    Position posWhiteBishop1 = new Position(5, 0);
    whitePos.add(posWhiteBishop1);
    Position posWhiteBishop2 = new Position(2, 0);
    whitePos.add(posWhiteBishop2);

    deleteAllPiecesExceptThosePositions(board, whitePos, blackPos);

    assertTrue(board.hasEnoughMaterialToMate(true));
  }

  @Test
  public void testHasEnoughMaterialToMateWhiteShouldBeFalse() {
    BitboardRepresentation board = new BitboardRepresentation();

    List<Position> whitePos = new ArrayList<>();
    List<Position> blackPos = new ArrayList<>();

    Position posWhiteKing = new Position(4, 0);
    whitePos.add(posWhiteKing);
    Position posWhiteBishop = new Position(2, 0);
    whitePos.add(posWhiteBishop);

    deleteAllPiecesExceptThosePositions(board, whitePos, blackPos);

    assertFalse(board.hasEnoughMaterialToMate(true));
  }

  @Test
  public void testDrawMaterialKingVSKing() {
    BitboardRepresentation board =
        new BitboardRepresentation(
            DEFAULT_WHITE_KING,
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            DEFAULT_BLACK_KING,
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L));
    assertTrue(board.isDrawByInsufficientMaterial());
  }

  @Test
  public void testDrawMaterialKingVSKingBishop() {
    BitboardRepresentation board =
        new BitboardRepresentation(
            DEFAULT_WHITE_KING,
            new Bitboard(0L),
            new Bitboard(4294967296L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            DEFAULT_BLACK_KING,
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L));
    assertTrue(board.isDrawByInsufficientMaterial());
  }

  @Test
  public void testDrawMaterialKingBishopVSKing() {
    BitboardRepresentation board =
        new BitboardRepresentation(
            DEFAULT_WHITE_KING,
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            DEFAULT_BLACK_KING,
            new Bitboard(0L),
            new Bitboard(4294967296L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L));
    assertTrue(board.isDrawByInsufficientMaterial());
  }

  @Test
  public void testDrawMaterialKingKnightVSKing() {
    BitboardRepresentation board =
        new BitboardRepresentation(
            DEFAULT_WHITE_KING,
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(4294967296L),
            new Bitboard(0L),
            DEFAULT_BLACK_KING,
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L));
    assertTrue(board.isDrawByInsufficientMaterial());
  }

  @Test
  public void testDrawMaterialKingVSKingKnight() {
    BitboardRepresentation board =
        new BitboardRepresentation(
            DEFAULT_WHITE_KING,
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            DEFAULT_BLACK_KING,
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(4294967296L),
            new Bitboard(0L));
    assertTrue(board.isDrawByInsufficientMaterial());
  }

  @Test
  public void testDrawMaterialAtBeginning() {
    BitboardRepresentation board = new BitboardRepresentation();
    assertFalse(board.isDrawByInsufficientMaterial());
  }

  @Test
  public void testDrawMaterialNoWhiteKing() {
    BitboardRepresentation board =
        new BitboardRepresentation(
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            DEFAULT_BLACK_KING,
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(4294967296L),
            new Bitboard(0L));
    assertFalse(board.isDrawByInsufficientMaterial());
  }

  @Test
  public void testDrawMaterialNoBlackKing() {
    BitboardRepresentation board =
        new BitboardRepresentation(
            DEFAULT_WHITE_KING,
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(4294967296L),
            new Bitboard(0L));
    assertFalse(board.isDrawByInsufficientMaterial());
  }

  @Test
  public void testDrawMaterialTwoDifferentColorBishops() {
    BitboardRepresentation board =
        new BitboardRepresentation(
            DEFAULT_WHITE_KING,
            new Bitboard(0L),
            new Bitboard(65536L),
            new Bitboard(0L),
            new Bitboard(0L),
            new Bitboard(0L),
            DEFAULT_BLACK_KING,
            new Bitboard(0L),
            new Bitboard(17592186044416L),
            new Bitboard(0L),
            new Bitboard(4294967296L),
            new Bitboard(0L));
    assertFalse(board.isDrawByInsufficientMaterial());
  }

  @Test
  public void testIsDoublePushPossibleOnDefaultBoard() {
    BitboardRepresentation board = new BitboardRepresentation();
    assertTrue(board.isDoublePushPossible(new Move(new Position(0, 1), new Position(0, 3)), true));
    assertTrue(board.isDoublePushPossible(new Move(new Position(0, 6), new Position(0, 4)), false));
  }

  @Test
  public void testIsDoublePushPossibleObstructedForWhite() {
    BitboardRepresentation board = new BitboardRepresentation();
    board.setSquare(new ColoredPiece(Piece.PAWN, Color.WHITE), 16);
    assertFalse(board.isDoublePushPossible(new Move(new Position(0, 1), new Position(0, 3)), true));
    assertTrue(board.isDoublePushPossible(new Move(new Position(0, 6), new Position(0, 4)), false));
    board.movePiece(new Position(0, 2), new Position(0, 3));
    assertFalse(board.isDoublePushPossible(new Move(new Position(0, 1), new Position(0, 3)), true));
    assertTrue(board.isDoublePushPossible(new Move(new Position(0, 6), new Position(0, 4)), false));
  }

  @Test
  public void testIsDoublePushPossibleObstructedForBlack() {
    BitboardRepresentation board = new BitboardRepresentation();
    board.setSquare(new ColoredPiece(Piece.PAWN, Color.BLACK), 40);
    assertTrue(board.isDoublePushPossible(new Move(new Position(0, 1), new Position(0, 3)), true));
    assertFalse(
        board.isDoublePushPossible(new Move(new Position(0, 6), new Position(0, 4)), false));
    board.movePiece(new Position(0, 5), new Position(0, 4));
    assertTrue(board.isDoublePushPossible(new Move(new Position(0, 1), new Position(0, 3)), true));
    assertFalse(
        board.isDoublePushPossible(new Move(new Position(0, 6), new Position(0, 4)), false));
  }

  @Test
  public void testIsDoublePushPossibleNotTwoSquaresUp() {
    BitboardRepresentation board = new BitboardRepresentation();
    // More or less than 2 squares up
    assertFalse(board.isDoublePushPossible(new Move(new Position(0, 1), new Position(0, 5)), true));
    assertFalse(
        board.isDoublePushPossible(new Move(new Position(0, 6), new Position(0, 3)), false));
    // X coordinate is modified
    assertFalse(board.isDoublePushPossible(new Move(new Position(0, 1), new Position(1, 3)), true));
    assertFalse(
        board.isDoublePushPossible(new Move(new Position(0, 6), new Position(4, 4)), false));
  }

  @Test
  public void testIsDoublePushPossibleNotAtInitialSquare() {
    BitboardRepresentation board = new BitboardRepresentation();
    assertFalse(board.isDoublePushPossible(new Move(new Position(0, 3), new Position(0, 5)), true));
    assertFalse(
        board.isDoublePushPossible(new Move(new Position(0, 4), new Position(0, 6)), false));
  }

  @Test
  public void testEnPassantPossible() {
    BitboardRepresentation board = new BitboardRepresentation();
    board.movePiece(new Position(0, 1), new Position(1, 4));
    board.movePiece(new Position(1, 1), new Position(3, 4));
    board.movePiece(new Position(2, 6), new Position(2, 4));
    assertTrue(board.isEnPassant(2, 5, new Move(new Position(1, 4), new Position(2, 5)), true));
    assertTrue(board.isEnPassant(2, 5, new Move(new Position(3, 4), new Position(2, 5)), true));
  }

  @Test
  public void testEnPassantPossibleNotAWhitePawn() {
    BitboardRepresentation board = new BitboardRepresentation();
    board.movePiece(new Position(0, 0), new Position(1, 4)); // move rook
    board.movePiece(new Position(2, 6), new Position(2, 4));
    assertFalse(board.isEnPassant(2, 5, new Move(new Position(1, 4), new Position(2, 5)), true));
  }

  @Test
  public void testEnPassantPossibleNotRightDestinationWhitePawn() {
    BitboardRepresentation board = new BitboardRepresentation();
    board.movePiece(new Position(0, 1), new Position(1, 4));
    board.movePiece(new Position(2, 6), new Position(2, 4));
    assertFalse(
        board.isEnPassant(
            2, 5, new Move(new Position(1, 4), new Position(1, 5)), true)); // not right x
    assertFalse(
        board.isEnPassant(
            2, 5, new Move(new Position(1, 4), new Position(2, 4)), true)); // not right y
  }

  @Test
  public void testEnPassantPossibleNotRightSourceWhitePawn() {
    BitboardRepresentation board = new BitboardRepresentation();
    board.movePiece(new Position(0, 1), new Position(1, 4));
    board.movePiece(new Position(2, 6), new Position(2, 4));
    assertFalse(
        board.isEnPassant(
            2, 2, new Move(new Position(2, 4), new Position(2, 5)), true)); // not right x
    assertFalse(
        board.isEnPassant(
            2, 2, new Move(new Position(1, 3), new Position(2, 5)), true)); // not right yù
    assertFalse(
        board.isEnPassant(
            2, 2, new Move(new Position(2, 3), new Position(2, 5)), true)); // not right x and y
  }

  @Test
  public void testEnPassantPossibleBlackPawn() {
    BitboardRepresentation board = new BitboardRepresentation();
    board.movePiece(new Position(0, 1), new Position(1, 3));
    board.movePiece(new Position(1, 1), new Position(3, 3));
    board.movePiece(new Position(2, 1), new Position(2, 3));
    assertTrue(board.isEnPassant(2, 2, new Move(new Position(1, 3), new Position(2, 2)), false));
    assertTrue(board.isEnPassant(2, 2, new Move(new Position(3, 3), new Position(2, 2)), false));
  }

  @Test
  public void testEnPassantPossibleNotABlackPawn() {
    BitboardRepresentation board = new BitboardRepresentation();
    board.movePiece(new Position(7, 7), new Position(1, 3)); // move rook
    board.movePiece(new Position(2, 1), new Position(2, 3));
    assertFalse(board.isEnPassant(2, 2, new Move(new Position(1, 3), new Position(2, 2)), false));
  }

  @Test
  public void testEnPassantPossibleNotRightDestinationBlackPawn() {
    BitboardRepresentation board = new BitboardRepresentation();
    board.movePiece(new Position(0, 7), new Position(1, 3));
    board.movePiece(new Position(2, 1), new Position(2, 3));
    assertFalse(
        board.isEnPassant(
            2, 2, new Move(new Position(1, 3), new Position(1, 2)), false)); // not right x
    assertFalse(
        board.isEnPassant(
            2, 2, new Move(new Position(1, 3), new Position(2, 3)), false)); // not right y
  }

  @Test
  public void testEnPassantPossibleNotRightSourceBlackPawn() {
    BitboardRepresentation board = new BitboardRepresentation();
    System.out.println(board);
    board.movePiece(new Position(0, 7), new Position(1, 3));
    board.movePiece(new Position(2, 1), new Position(2, 3));
    assertFalse(
        board.isEnPassant(
            2, 2, new Move(new Position(2, 3), new Position(2, 2)), false)); // not right x
    assertFalse(
        board.isEnPassant(
            2, 2, new Move(new Position(1, 4), new Position(2, 2)), false)); // not right y
    assertFalse(
        board.isEnPassant(
            2, 2, new Move(new Position(2, 5), new Position(2, 2)), false)); // not right x and y
  }

  @Test
  public void testBitboardRepresentationString() {
    BitboardRepresentation board = new BitboardRepresentation();
    String[] boardString = {
      "Bitboard = 0xffff00000000ffff",
      "1|1|1|1|1|1|1|1",
      "1|1|1|1|1|1|1|1",
      "0|0|0|0|0|0|0|0",
      "0|0|0|0|0|0|0|0",
      "0|0|0|0|0|0|0|0",
      "0|0|0|0|0|0|0|0",
      "1|1|1|1|1|1|1|1",
      "1|1|1|1|1|1|1|1",
    };
    for (String s : boardString) {
      assertTrue(board.toString().contains(s));
    }
  }

  @Test
  public void testBitboardRepresentationEquals() {
    BitboardRepresentation board = new BitboardRepresentation();
    BitboardRepresentation board2 = new BitboardRepresentation();
    assertEquals(board, board2);
  }

  @Test
  public void testBitboardRepresentationNotEquals() {
    BitboardRepresentation board = new BitboardRepresentation();
    BitboardRepresentation board2 = new BitboardRepresentation();
    assertEquals(board, board2);
    board.movePiece(new Position(0, 1), new Position(1, 3));
    assertNotEquals(board, board2);
    board2.movePiece(new Position(0, 1), new Position(1, 3));
    assertEquals(board, board2);
  }

  @Test
  public void testBitboardRepresentationEqualsWithNotSameType() {
    BitboardRepresentation board = new BitboardRepresentation();
    String boardString = board.toString();
    assertNotEquals(board, boardString);
  }

  @Test
  public void testDeletePieceAt() {
    BitboardRepresentation board = new BitboardRepresentation();
    board.deletePieceAt(0, 0);
  }

  @Test
  public void testQueensOffTheBoardWhenGameStarts() {
    BitboardRepresentation board = new BitboardRepresentation();

    assertFalse(board.queensOffTheBoard());
  }

  @Test
  public void testQueensOffTheBoardShouldBeTrue() {
    BitboardRepresentation board = new BitboardRepresentation();

    Position whiteQueenPos = new Position(3, 0);
    Position blackQueenPos = new Position(3, 7);

    board.deletePieceAt(whiteQueenPos.x(), whiteQueenPos.y());
    board.deletePieceAt(blackQueenPos.x(), blackQueenPos.y());

    assertTrue(board.queensOffTheBoard());
  }

  @Test
  public void testAreKingsActiveWhenGameStartsShouldBeFalse() {
    BitboardRepresentation board = new BitboardRepresentation();

    assertFalse(board.areKingsActive());
  }

  @Test
  public void testAreKingsActiveShouldBeTrue() {
    BitboardRepresentation board = new BitboardRepresentation();

    Position whiteQueenPos = new Position(3, 0);
    Position blackQueenPos = new Position(3, 7);

    Position d2Pawn = new Position(3, 1);
    Position d7Pawn = new Position(3, 6);

    Position e2Pawn = new Position(4, 1);
    Position e7Pawn = new Position(4, 6);

    Position f2Pawn = new Position(5, 1);
    Position f7Pawn = new Position(5, 6);

    Position whiteKingsBishop = new Position(5, 0);
    Position blackKingsBishop = new Position(5, 7);

    board.deletePieceAt(whiteQueenPos.x(), whiteQueenPos.y());
    board.deletePieceAt(blackQueenPos.x(), blackQueenPos.y());

    board.deletePieceAt(d2Pawn.x(), d2Pawn.y());
    board.deletePieceAt(d7Pawn.x(), d7Pawn.y());

    board.deletePieceAt(e2Pawn.x(), e2Pawn.y());
    board.deletePieceAt(e7Pawn.x(), e7Pawn.y());

    board.deletePieceAt(f2Pawn.x(), f2Pawn.y());
    board.deletePieceAt(f7Pawn.x(), f7Pawn.y());

    board.deletePieceAt(whiteKingsBishop.x(), whiteKingsBishop.y());
    board.deletePieceAt(blackKingsBishop.x(), blackKingsBishop.y());

    assertTrue(board.areKingsActive());
  }

  @Test
  public void testNbPiecesRemaining() {
    BitboardRepresentation board = new BitboardRepresentation();

    int nbPiecesWhenGameStarts = 32;
    assertEquals(nbPiecesWhenGameStarts, board.nbPiecesRemaining());
  }

  @Test
  public void testPawnsHaveProgressedWhenGameStartsShouldBeFalse() {
    BitboardRepresentation board = new BitboardRepresentation();

    assertFalse(board.pawnsHaveProgressed(true));
    assertFalse(board.pawnsHaveProgressed(false));
  }

  @Test
  public void testPawnshaveProgressedWhenNoPawnsWhiteShouldBeFalse() {
    BitboardRepresentation board = new BitboardRepresentation();

    int yWhite = 1;
    // Delete all pawns
    for (int x = 0; x <= 7; x++) {
      board.deletePieceAt(x, yWhite);
    }

    assertFalse(board.pawnsHaveProgressed(true));
  }

  @Test
  public void testPawnsHaveProgressedWhenNoPawnsBlackShouldBeFalse() {
    BitboardRepresentation board = new BitboardRepresentation();

    int yBlack = 6;
    // Delete all pawns
    for (int x = 0; x <= 7; x++) {
      board.deletePieceAt(x, yBlack);
    }

    assertFalse(board.pawnsHaveProgressed(false));
  }

  @Test
  public void TestPawnsHaveProgressedShouldBeTrue() {
    BitboardRepresentation board = new BitboardRepresentation();

    // from
    Position a2 = new Position(0, 1);
    Position b2 = new Position(1, 1);
    Position c2 = new Position(2, 1);
    Position d2 = new Position(3, 1);
    Position e2 = new Position(4, 1);
    Position f2 = new Position(5, 1);

    Position a7 = new Position(0, 6);
    Position b7 = new Position(1, 6);
    Position c7 = new Position(2, 6);
    Position d7 = new Position(3, 6);
    Position e7 = new Position(4, 6);
    Position f7 = new Position(5, 6);

    // to
    Position a4 = new Position(0, 3);
    Position b4 = new Position(1, 3);
    Position c4 = new Position(2, 3);
    Position d4 = new Position(3, 3);
    Position e4 = new Position(4, 3);
    Position f4 = new Position(5, 3);

    Position a5 = new Position(0, 4);
    Position b5 = new Position(1, 4);
    Position c5 = new Position(2, 4);
    Position d5 = new Position(3, 4);
    Position e5 = new Position(4, 4);
    Position f5 = new Position(5, 4);

    board.movePiece(a2, a4);
    board.movePiece(b2, b4);
    board.movePiece(c2, c4);
    board.movePiece(d2, d4);
    board.movePiece(e2, e4);
    board.movePiece(f2, f4);

    board.movePiece(a7, a5);
    board.movePiece(b7, b5);
    board.movePiece(c7, c5);
    board.movePiece(d7, d5);
    board.movePiece(e7, e5);
    board.movePiece(f7, f5);

    assertTrue(board.pawnsHaveProgressed(true));
    assertTrue(board.pawnsHaveProgressed(false));
  }

  // TODO pawn can't eat front
}
