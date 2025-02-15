package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.model.*;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.Board;
import pdp.model.board.Move;
import pdp.model.board.ZobristHashing;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;
import tests.helpers.MockBoard;

public class ZobristHashingTest {
  Game game;

  @BeforeEach
  public void setUp() {
    game = Game.initialize(false, false, null, null);
  }

  @AfterEach
  public void tearDown() {
    game = Game.initialize(false, false, null, null);
  }

  @Test
  public void testGenerateZobristHashing() {
    ZobristHashing zobristHashing = new ZobristHashing();
    Board board = new Board();
    long hash1 = zobristHashing.generateHashFromBitboards(board);
    long hash2 = zobristHashing.generateHashFromBitboards(board);
    // same hash for the same board
    assertEquals(hash1, hash2);

    board.makeMove(new Move(new Position(0, 1), new Position(0, 2)));
    // different hash for different board
    assertNotEquals(hash1, zobristHashing.generateHashFromBitboards(board));
    board.getBoard().movePiece(new Position(0, 2), new Position(0, 1));

    // player changed but board the same
    assertNotEquals(hash1, zobristHashing.generateHashFromBitboards(board));
  }

  @Test
  public void testGenerateZobristHashingEnPassant() {
    ZobristHashing zobristHashing = new ZobristHashing();
    Board board = game.getBoard();
    // generate en passant
    game.playMove(new Move(new Position(0, 1), new Position(0, 3)));
    long hashEnPassant = zobristHashing.generateHashFromBitboards(board);

    // move a pawn two rows ahead but in two times (same pos as en passantPos but not registered)
    game = Game.initialize(false, false, null, null);
    board = game.getBoard();
    game.playMove(new Move(new Position(0, 1), new Position(0, 2)));
    game.playMove(new Move(new Position(1, 7), new Position(2, 5)));
    game.playMove(new Move(new Position(0, 2), new Position(0, 3)));
    game.playMove(new Move(new Position(2, 5), new Position(1, 7)));
    long hashNotEnPassant = zobristHashing.generateHashFromBitboards(board);
    // Should be different because possible en passant position is taken into account
    assertNotEquals(hashEnPassant, hashNotEnPassant);
  }

  @Test
  public void testGenerateSimplifiedZobristHashingEnPassant() {
    ZobristHashing zobristHashing = new ZobristHashing();
    Board board = game.getBoard();
    // generate en passant
    game.playMove(new Move(new Position(0, 1), new Position(0, 3)));
    long hashEnPassant = zobristHashing.generateSimplifiedHashFromBitboards(board);

    // move a pawn two rows ahead but in two times (same pos as en passantPos but not registered)
    game = Game.initialize(false, false, null, null);
    board = game.getBoard();
    game.playMove(new Move(new Position(0, 1), new Position(0, 2)));
    game.playMove(new Move(new Position(1, 7), new Position(2, 5)));
    game.playMove(new Move(new Position(0, 2), new Position(0, 3)));
    game.playMove(new Move(new Position(2, 5), new Position(1, 7)));
    long hashNotEnPassant = zobristHashing.generateSimplifiedHashFromBitboards(board);
    // Should be equal because simplified only takes the pieces positions into account
    assertEquals(hashEnPassant, hashNotEnPassant);
  }

  @Test
  public void testGenerateSimplifiedZobristHashing() {
    ZobristHashing zobristHashing = new ZobristHashing();
    Board board = new Board();
    long hash1 = zobristHashing.generateSimplifiedHashFromBitboards(board);
    long hash2 = zobristHashing.generateSimplifiedHashFromBitboards(board);
    assertEquals(hash1, hash2);

    board.makeMove(new Move(new Position(0, 1), new Position(0, 3)));
    assertNotEquals(hash1, zobristHashing.generateSimplifiedHashFromBitboards(board));
    board.getBoard().movePiece(new Position(0, 3), new Position(0, 1));
    assertEquals(hash1, zobristHashing.generateSimplifiedHashFromBitboards(board));
  }

  @Test
  public void testUpdateZobristHashing() {
    ZobristHashing zobristHashing = new ZobristHashing();
    Board board = new Board(); // Initialize standard board

    long hash1 = zobristHashing.generateHashFromBitboards(board);

    Move move1 =
        new Move(
            new Position(0, 1),
            new Position(0, 3),
            new ColoredPiece(Piece.PAWN, Color.WHITE),
            false); // Move white pawn
    board.makeMove(move1);
    hash1 = zobristHashing.updateHashFromBitboards(hash1, board, move1);

    Move move2 =
        new Move(
            new Position(1, 6),
            new Position(1, 4),
            new ColoredPiece(Piece.PAWN, Color.BLACK),
            false); // Move black pawn
    board.makeMove(move2);
    hash1 = zobristHashing.updateHashFromBitboards(hash1, board, move2);

    long hash2 = zobristHashing.generateHashFromBitboards(board);

    assertEquals(hash1, hash2, "Incrementally updated hash should match recomputed hash.");
  }

  @Test
  void testUpdateSimplifiedZobristHashing() {
    Board board = new Board();
    ZobristHashing zobrist = new ZobristHashing();

    long initialHash = zobrist.generateSimplifiedHashFromBitboards(board);

    Move move =
        new Move(
            new Position(0, 1),
            new Position(0, 2),
            new ColoredPiece(Piece.PAWN, Color.WHITE),
            false);
    board.makeMove(move);

    long updatedHash = zobrist.updateSimplifiedHashFromBitboards(initialHash, board, move);
    long recalculatedHash = zobrist.generateSimplifiedHashFromBitboards(board);

    assertEquals(recalculatedHash, updatedHash, "Updated hash should match recalculated hash.");
  }

  @Test
  public void testUpdateZobristHashingEnPassant() {
    ZobristHashing zobristHashing = new ZobristHashing();
    Board board = game.getBoard();
    // generate en passant
    long hashUpdate = zobristHashing.generateHashFromBitboards(board);
    game.playMove(new Move(new Position(0, 1), new Position(0, 3)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(0, 1),
                new Position(0, 3),
                new ColoredPiece(Piece.PAWN, Color.WHITE),
                false));
    long hashGenerate = zobristHashing.generateHashFromBitboards(board);

    assertEquals(hashUpdate, hashGenerate, "Updated hash should match recomputed hash.");

    game.playMove(new Move(new Position(0, 6), new Position(0, 4)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(0, 6),
                new Position(0, 4),
                new ColoredPiece(Piece.PAWN, Color.BLACK),
                false));
    hashGenerate = zobristHashing.generateHashFromBitboards(board);

    assertEquals(hashUpdate, hashGenerate, "Updated hash should match recomputed hash.");
  }

  @Test
  public void testSimplifiedZobristHashingCapture() {
    ZobristHashing zobristHashing = new ZobristHashing();
    Board board = game.getBoard();
    // capture
    long hashUpdate = zobristHashing.generateSimplifiedHashFromBitboards(board);
    game.playMove(new Move(new Position(1, 1), new Position(1, 3)));
    hashUpdate =
        zobristHashing.updateSimplifiedHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(1, 1),
                new Position(1, 3),
                new ColoredPiece(Piece.PAWN, Color.WHITE),
                false));
    game.playMove(new Move(new Position(0, 6), new Position(0, 4)));
    hashUpdate =
        zobristHashing.updateSimplifiedHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(0, 6),
                new Position(0, 4),
                new ColoredPiece(Piece.PAWN, Color.BLACK),
                false));
    game.playMove(new Move(new Position(1, 3), new Position(0, 4)));
    hashUpdate =
        zobristHashing.updateSimplifiedHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(1, 3),
                new Position(0, 4),
                new ColoredPiece(Piece.PAWN, Color.WHITE),
                true,
                new ColoredPiece(Piece.PAWN, Color.BLACK)));

    Board board2 = new Board();
    if (board2.getBoard() instanceof BitboardRepresentation bitboardRepresentation) {
      bitboardRepresentation.deletePieceAt(0, 6);
      bitboardRepresentation.movePiece(new Position(1, 1), new Position(0, 4));
    }
    long hashGenerate = zobristHashing.generateSimplifiedHashFromBitboards(board2);
    // TODO: find out why bitboard representations are not equals
    // assertEquals(board.getBoard(),board2.getBoard());
    assertEquals(hashUpdate, hashGenerate);
  }

  @Test
  public void testZobristHashingWhiteLongCastle() {
    ZobristHashing zobristHashing = new ZobristHashing();
    Board board = game.getBoard();

    game.playMove(new Move(new Position(0, 1), new Position(0, 3)));
    long hashOnlyPawn = zobristHashing.generateHashFromBitboards(board);

    long hashUpdate = zobristHashing.generateHashFromBitboards(board);
    game.playMove(new Move(new Position(1, 7), new Position(2, 5)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(1, 7),
                new Position(2, 5),
                new ColoredPiece(Piece.KNIGHT, Color.BLACK),
                false));
    game.playMove(new Move(new Position(0, 0), new Position(0, 1)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(0, 0),
                new Position(0, 1),
                new ColoredPiece(Piece.ROOK, Color.WHITE),
                false));
    game.playMove(new Move(new Position(2, 5), new Position(1, 7)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(2, 5),
                new Position(1, 7),
                new ColoredPiece(Piece.KNIGHT, Color.BLACK),
                false));
    game.playMove(new Move(new Position(0, 1), new Position(0, 0)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(0, 1),
                new Position(0, 0),
                new ColoredPiece(Piece.ROOK, Color.WHITE),
                false));

    // Same positions on board but Castling rights changed
    assertNotEquals(hashOnlyPawn, hashUpdate);
  }

  @Test
  public void testZobristHashingBlackShortCastle() {
    ZobristHashing zobristHashing = new ZobristHashing();
    Board board = game.getBoard();

    game.playMove(new Move(new Position(1, 0), new Position(2, 2)));
    long hashWithCastling = zobristHashing.generateHashFromBitboards(board);

    long hashUpdate = zobristHashing.generateHashFromBitboards(board);
    Move firstBlackMove =
        new Move(
            new Position(0, 6),
            new Position(0, 5),
            new ColoredPiece(Piece.PAWN, Color.BLACK),
            false);
    game.playMove(new Move(new Position(7, 6), new Position(7, 5)));
    hashUpdate = zobristHashing.updateHashFromBitboards(hashUpdate, board, firstBlackMove);
    hashWithCastling =
        zobristHashing.updateHashFromBitboards(hashWithCastling, board, firstBlackMove);
    game.playMove(new Move(new Position(2, 2), new Position(1, 0)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(2, 2),
                new Position(1, 0),
                new ColoredPiece(Piece.KNIGHT, Color.WHITE),
                false));
    game.playMove(new Move(new Position(7, 7), new Position(7, 6)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(0, 7),
                new Position(0, 6),
                new ColoredPiece(Piece.ROOK, Color.BLACK),
                false));
    game.playMove(new Move(new Position(1, 0), new Position(2, 2)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(1, 0),
                new Position(2, 2),
                new ColoredPiece(Piece.KNIGHT, Color.WHITE),
                false));
    game.playMove(new Move(new Position(7, 6), new Position(7, 7)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(0, 6),
                new Position(0, 7),
                new ColoredPiece(Piece.ROOK, Color.BLACK),
                false));

    // Same positions on board but Castling rights changed
    assertNotEquals(hashWithCastling, hashUpdate);
  }

  @Test
  public void testZobristHashingBlackLongCastle() {
    ZobristHashing zobristHashing = new ZobristHashing();
    Board board = game.getBoard();

    game.playMove(new Move(new Position(1, 0), new Position(2, 2)));
    long hashWithCastling = zobristHashing.generateHashFromBitboards(board);

    long hashUpdate = zobristHashing.generateHashFromBitboards(board);
    Move firstBlackMove =
        new Move(
            new Position(0, 6),
            new Position(0, 5),
            new ColoredPiece(Piece.PAWN, Color.BLACK),
            false);
    game.playMove(new Move(new Position(0, 6), new Position(0, 5)));
    hashUpdate = zobristHashing.updateHashFromBitboards(hashUpdate, board, firstBlackMove);
    hashWithCastling =
        zobristHashing.updateHashFromBitboards(hashWithCastling, board, firstBlackMove);
    game.playMove(new Move(new Position(2, 2), new Position(1, 0)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(2, 2),
                new Position(1, 0),
                new ColoredPiece(Piece.KNIGHT, Color.WHITE),
                false));
    game.playMove(new Move(new Position(0, 7), new Position(0, 6)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(0, 7),
                new Position(0, 6),
                new ColoredPiece(Piece.ROOK, Color.BLACK),
                false));
    game.playMove(new Move(new Position(1, 0), new Position(2, 2)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(1, 0),
                new Position(2, 2),
                new ColoredPiece(Piece.KNIGHT, Color.WHITE),
                false));
    game.playMove(new Move(new Position(0, 6), new Position(0, 7)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(0, 6),
                new Position(0, 7),
                new ColoredPiece(Piece.ROOK, Color.BLACK),
                false));

    // Same positions on board but Castling rights changed
    assertNotEquals(hashWithCastling, hashUpdate);
  }

  @Test
  public void testZobristHashingNoCastle() {
    ZobristHashing zobristHashing = new ZobristHashing();
    Board board = game.getBoard();

    game.playMove(new Move(new Position(4, 1), new Position(4, 2)));
    long hashWithCastling = zobristHashing.generateHashFromBitboards(board);

    long hashUpdate = zobristHashing.generateHashFromBitboards(board);
    Move firstBlackMove =
        new Move(
            new Position(0, 6),
            new Position(0, 5),
            new ColoredPiece(Piece.PAWN, Color.BLACK),
            false);
    game.playMove(new Move(new Position(4, 6), new Position(4, 5)));
    hashUpdate = zobristHashing.updateHashFromBitboards(hashUpdate, board, firstBlackMove);
    hashWithCastling =
        zobristHashing.updateHashFromBitboards(hashWithCastling, board, firstBlackMove);
    game.playMove(new Move(new Position(4, 0), new Position(4, 1)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(4, 0),
                new Position(4, 1),
                new ColoredPiece(Piece.KING, Color.WHITE),
                false));
    game.playMove(new Move(new Position(4, 7), new Position(4, 6)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(4, 7),
                new Position(4, 6),
                new ColoredPiece(Piece.KING, Color.BLACK),
                false));
    game.playMove(new Move(new Position(4, 1), new Position(4, 0)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(4, 1),
                new Position(4, 0),
                new ColoredPiece(Piece.KING, Color.WHITE),
                false));
    game.playMove(new Move(new Position(4, 6), new Position(4, 7)));
    hashUpdate =
        zobristHashing.updateHashFromBitboards(
            hashUpdate,
            board,
            new Move(
                new Position(4, 6),
                new Position(4, 7),
                new ColoredPiece(Piece.KING, Color.BLACK),
                false));

    // Same positions on board but Castling rights changed
    assertNotEquals(hashWithCastling, hashUpdate);
  }

  @Test
  void testGenerateHashThrowsExceptionForNonBitboard() {
    MockBoard board = new MockBoard();

    ZobristHashing zobrist = new ZobristHashing();

    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              zobrist.generateHashFromBitboards(board);
            });

    assertEquals("Only available for bitboards", exception.getMessage());
  }

  @Test
  void testUpdateHashThrowsExceptionForNonBitboard() {
    MockBoard board = new MockBoard();

    ZobristHashing zobrist = new ZobristHashing();

    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              zobrist.generateHashFromBitboards(board);
            });
    Exception exception2 =
        assertThrows(
            RuntimeException.class,
            () -> {
              zobrist.updateHashFromBitboards(
                  1234532, board, new Move(new Position(4, 1), new Position(4, 2)));
            });

    assertEquals("Only available for bitboards", exception.getMessage());
    assertEquals("Only available for bitboards", exception2.getMessage());
  }
}
