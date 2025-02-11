package pdp.model;

import java.util.Map;
import java.util.Random;

public class ZobristHashing {

  private static final int PIECES_TYPES = 12;
  private static final int BOARD_SQUARES = 64;
  private static final int CASTLINGS = 16;
  private static final int EN_PASSANT = 8;

  private final long[][] pieces = new long[PIECES_TYPES][BOARD_SQUARES];
  private final long[] castlings = new long[CASTLINGS];
  private final long[] enPassant = new long[EN_PASSANT];
  private long sideToMove;

  private int prevCastlingIndex;
  private int prevEnPassantFile = -1;
  private final Random random = new Random();

  public ZobristHashing() {
    initializeHash();
  }

  private void initializeHash() {

    for (int i = 0; i < PIECES_TYPES; i++) {
      for (int j = 0; j < BOARD_SQUARES; j++) {
        pieces[i][j] = random.nextLong();
      }
    }

    for (int i = 0; i < CASTLINGS; i++) {
      castlings[i] = random.nextLong();
    }

    for (int i = 0; i < EN_PASSANT; i++) {
      enPassant[i] = random.nextLong();
    }

    sideToMove = random.nextLong();
  }

  private int translateCastling(Board board) {
    if (!board.whiteShortCastle
        && !board.whiteLongCastle
        && !board.blackShortCastle
        && !board.blackLongCastle) {
      return -1;
    }
    int castlingRights = 0;
    if (board.whiteShortCastle) {
      castlingRights |= 1;
    }
    if (board.whiteLongCastle) {
      castlingRights |= 2;
    }
    if (board.blackShortCastle) {
      castlingRights |= 4;
    }
    if (board.blackLongCastle) {
      castlingRights |= 8;
    }
    return castlingRights;
  }

  public long generateHashFromBitboards(Board board) {
    long hash = 0;
    if (!(board.getBoard() instanceof BitboardRepresentation bitboardsRepresentation))
      throw new RuntimeException("Only available for bitboards");
    Bitboard[] bitboards = bitboardsRepresentation.getBitboards();
    for (int i = 0; i < PIECES_TYPES; i++) {
      long bitboardValue = bitboards[i].bitboard;
      while (bitboardValue != 0) {
        int square = Long.numberOfTrailingZeros(bitboardValue);
        hash ^= pieces[i][square];
        bitboardValue &= bitboardValue - 1;
      }
    }

    prevCastlingIndex = translateCastling(board);
    hash ^= castlings[prevCastlingIndex];

    if (board.enPassantPos != null) {
      prevEnPassantFile = board.enPassantPos.getX();
      hash ^= enPassant[prevEnPassantFile];
    }
    if (board.isWhite) {
      hash ^= sideToMove;
    }
    return hash;
  }

  public long updateHashFromBitboards(long currHash, Board board, Move move) {
    int from = move.source.getX() + move.source.getY() * board.getBoard().getNbRows();
    int to = move.dest.getX() + move.dest.getY() * board.getBoard().getNbCols();
    ColoredPiece capturedPiece = null;

    if (!(board.getBoard() instanceof BitboardRepresentation bitboardsRepresentation))
      throw new RuntimeException("Only available for bitboards");

    for (Map.Entry<Integer, ColoredPiece> entry : bitboardsRepresentation.pieces.entrySet()) {
      if (entry.getValue().equals(move.piece)) {
        currHash ^= pieces[entry.getKey()][to];
        currHash ^= pieces[entry.getKey()][from];
      }
    }
    // if en passant is not possible in the column saved before
    if (prevEnPassantFile != -1) {
      currHash ^= enPassant[prevEnPassantFile];
      prevEnPassantFile = -1;
    }
    // if en passant is possible
    if (board.enPassantPos != null) {
      prevEnPassantFile = board.enPassantPos.getX();
      currHash ^= enPassant[prevEnPassantFile];
    }

    // update castlings rights if needed
    int newCastlingIndex = translateCastling(board);
    if (prevCastlingIndex != newCastlingIndex) {
      currHash ^= castlings[prevCastlingIndex];
      currHash ^= castlings[newCastlingIndex];
      prevCastlingIndex = newCastlingIndex;
    }

    if (capturedPiece != null) {
      for (Map.Entry<Integer, ColoredPiece> entry : bitboardsRepresentation.pieces.entrySet()) {
        if (entry.getValue().equals(capturedPiece)) {
          currHash ^= pieces[entry.getKey()][to];
        }
      }
    }

    currHash ^= sideToMove;

    return currHash;
  }
}
