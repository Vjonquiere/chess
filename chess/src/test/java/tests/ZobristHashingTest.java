package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import pdp.model.*;
import pdp.utils.Position;

public class ZobristHashingTest {

  @Test
  public void testInitZobristHashing() {
    ZobristHashing zobristHashing = new ZobristHashing();
    Board board = new Board();
    long hash1 = zobristHashing.generateHashFromBitboards(board);
    long hash2 = zobristHashing.generateHashFromBitboards(board);
    // same hash for the same board
    assertEquals(hash1, hash2);

    board.makeMove(new Move(new Position(1, 0), new Position(3, 0)));
    // different hash for different board
    assertNotEquals(hash1, zobristHashing.generateHashFromBitboards(board));
    board.getBoard().movePiece(new Position(3, 0), new Position(1, 0));

    // player changed but board the same
    assertNotEquals(hash1, zobristHashing.generateHashFromBitboards(board));
  }

  @Test
  public void testInitSimplifiedZobristHashing() {
    ZobristHashing zobristHashing = new ZobristHashing();
    Board board = new Board();
    long hash1 = zobristHashing.generateSimplifiedHashFromBitboards(board);
    long hash2 = zobristHashing.generateSimplifiedHashFromBitboards(board);
    assertEquals(hash1, hash2);

    board.makeMove(new Move(new Position(1, 0), new Position(3, 0)));
    assertNotEquals(hash1, zobristHashing.generateSimplifiedHashFromBitboards(board));
    board.getBoard().movePiece(new Position(3, 0), new Position(1, 0));
    assertEquals(hash1, zobristHashing.generateSimplifiedHashFromBitboards(board));
  }

  @Test
  public void testUpdateZobristHashing() {
    ZobristHashing zobristHashing = new ZobristHashing();
    Board board = new Board(); // Initialize standard board

    long hash1 = zobristHashing.generateHashFromBitboards(board);

    Move move1 =
        new Move(
            new Position(1, 0),
            new Position(3, 0),
            new ColoredPiece(Piece.PAWN, Color.WHITE),
            false); // Move white pawn
    board.makeMove(move1);
    hash1 = zobristHashing.updateHashFromBitboards(hash1, board, move1);

    Move move2 =
        new Move(
            new Position(6, 1),
            new Position(4, 1),
            new ColoredPiece(Piece.PAWN, Color.BLACK),
            false); // Move black pawn
    board.makeMove(move2);
    hash1 = zobristHashing.updateHashFromBitboards(hash1, board, move2);

    long hash2 = zobristHashing.generateHashFromBitboards(board);

    assertEquals(hash1, hash2, "Incrementally updated hash should match recomputed hash.");
  }

  @Test
  void testUpdateHashFromBitboards() {
    Board board = new Board(); // Set up an initial board
    ZobristHashing zobrist = new ZobristHashing();

    long initialHash = zobrist.generateSimplifiedHashFromBitboards(board);

    Move move =
        new Move(
            new Position(1, 0),
            new Position(2, 0),
            new ColoredPiece(Piece.PAWN, Color.WHITE),
            false);
    board.makeMove(move);

    long updatedHash = zobrist.updateSimplifiedHashFromBitboards(initialHash, board, move);
    long recalculatedHash = zobrist.generateSimplifiedHashFromBitboards(board);

    assertEquals(recalculatedHash, updatedHash, "Updated hash should match recalculated hash.");
  }
}
