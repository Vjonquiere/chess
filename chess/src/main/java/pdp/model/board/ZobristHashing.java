package pdp.model.board;

import java.util.Random;
import pdp.exceptions.InvalidBoardException;
import pdp.model.piece.ColoredPiece;

/** Implementation of zobrist hashing to store hashes from boards. */
public class ZobristHashing {

  private static final int PIECES_TYPES = 12;
  private static final int BOARD_SQUARES = 64;
  private static final int CASTLING = 16;
  private static final int EN_PASSANT = 8;

  private final long[][] pieces = new long[PIECES_TYPES][BOARD_SQUARES];
  // 16 combinations of castling rights
  private final long[] castling = new long[CASTLING];
  // 8 en passant possible (1 per column)
  private final long[] enPassant = new long[EN_PASSANT];
  private long sideToMove;

  private int prevCastlingIndex;
  private int prevEnPassantFile = -1;
  private final Random random = new Random();

  /** Constructor to initialize the components to the future hash. */
  public ZobristHashing() {
    initializeHash();
  }

  /**
   * Initialize all arrays with random long corresponding to the pieces, castling rights, en passant
   * moves and size to move.
   */
  private void initializeHash() {

    for (int i = 0; i < PIECES_TYPES; i++) {
      for (int j = 0; j < BOARD_SQUARES; j++) {
        pieces[i][j] = random.nextLong();
      }
    }

    for (int i = 0; i < CASTLING; i++) {
      castling[i] = random.nextLong();
    }

    for (int i = 0; i < EN_PASSANT; i++) {
      enPassant[i] = random.nextLong();
    }

    sideToMove = random.nextLong();
  }

  /**
   * Translates the castling rights of the given board into an integer representation. The castling
   * rights are encoded as a 4-bit integer: Bit 0 (1) White can castle kingside, Bit 1 (2) White can
   * castle queenside, Bit 2 (4) Black can castle kingside, Bit 3 (8) Black can castle queenside
   *
   * @param board Current board game to get the castling rights
   * @return An integer (0 to 15) representing castling rights, or -1 if no castling is possible.
   */
  private int translateCastling(Board board) {
    if (!board.isWhiteShortCastle()
        && !board.isWhiteLongCastle()
        && !board.isBlackShortCastle()
        && !board.isBlackLongCastle()) {
      return -1;
    }
    int castlingRights = 0;
    if (board.isWhiteShortCastle()) {
      castlingRights |= 1;
    }
    if (board.isWhiteLongCastle()) {
      castlingRights |= 2;
    }
    if (board.isBlackShortCastle()) {
      castlingRights |= 4;
    }
    if (board.isBlackLongCastle()) {
      castlingRights |= 8;
    }
    return castlingRights;
  }

  /**
   * Generate the hash corresponding to the pieces of the current board.
   *
   * @param board Current board
   * @return hash corresponding to the board given in parameters
   */
  private long generatePieceHash(Board board) {
    long hash = 0;
    if (!(board.getBoardRep() instanceof BitboardRepresentation bitboardsRepresentation)) {
      throw new InvalidBoardException();
    }
    Bitboard[] bitboards = bitboardsRepresentation.getBitboards();
    for (int i = 0; i < PIECES_TYPES; i++) {
      long bitboardValue = bitboards[i].bitboard;
      while (bitboardValue != 0) {
        int square = Long.numberOfTrailingZeros(bitboardValue);
        hash ^= pieces[i][square];
        bitboardValue &= bitboardValue - 1;
      }
    }
    return hash;
  }

  /**
   * Compute the simplified hash from a board -> update the change of position of piece and captured
   * piece.
   *
   * @param currHash previously computer hash
   * @param board current board
   * @param move last played move
   * @return updated hash
   */
  private long updatePieceHash(long currHash, Board board, Move move) {
    if (!(board.getBoardRep() instanceof BitboardRepresentation)) {
      throw new InvalidBoardException();
    }

    int from = move.source.x() + move.source.y() * board.getBoardRep().getNbRows();
    int to = move.dest.x() + move.dest.y() * board.getBoardRep().getNbCols();

    // Remove piece from its source and add it to the destination
    currHash ^= pieces[BitboardRepresentation.pieces.getFromValue(move.piece)][to];
    currHash ^= pieces[BitboardRepresentation.pieces.getFromValue(move.piece)][from];

    ColoredPiece capturedPiece = move.takenPiece;
    // delete the captured piece
    if (capturedPiece != null) {
      currHash ^= pieces[BitboardRepresentation.pieces.getFromValue(capturedPiece)][to];
    }
    return currHash;
  }

  /**
   * Generate the hash corresponding to the current board state.
   *
   * @param board Current board
   * @return hash corresponding to the board given in parameters
   */
  public long generateHashFromBitboards(Board board) {
    long hash = generatePieceHash(board);
    prevCastlingIndex = translateCastling(board);
    if (prevCastlingIndex != -1) {
      hash ^= castling[prevCastlingIndex];
    }

    if (board.getEnPassantPos() != null) {
      prevEnPassantFile = board.getEnPassantPos().x();
      hash ^= enPassant[prevEnPassantFile];
    }
    if (board.isWhite) {
      hash ^= sideToMove;
    }
    return hash;
  }

  /**
   * Update the hash with only the changed that occurred on the last move. Will update the position
   * of the piece that moved, the piece captured, the updated castling rights, en passant files and
   * player.
   *
   * @param currHash previously computer hash
   * @param board current board
   * @param move last played move
   * @return updated hash
   */
  public long updateHashFromBitboards(long currHash, Board board, Move move) {
    currHash = updatePieceHash(currHash, board, move);
    // if en passant is not possible in the column saved before
    if (prevEnPassantFile != -1) {
      currHash ^= enPassant[prevEnPassantFile];
      prevEnPassantFile = -1;
    }
    // if en passant is possible
    if (board.getEnPassantPos() != null) {
      prevEnPassantFile = board.getEnPassantPos().x();
      currHash ^= enPassant[prevEnPassantFile];
    }

    // update castlings rights if needed
    int newCastlingIndex = translateCastling(board);
    if (prevCastlingIndex != newCastlingIndex
        && newCastlingIndex != -1
        && prevCastlingIndex != -1) {
      currHash ^= castling[prevCastlingIndex];
      currHash ^= castling[newCastlingIndex];
      prevCastlingIndex = newCastlingIndex;
    }

    currHash ^= sideToMove;

    return currHash;
  }

  /**
   * Generate the simplified hash corresponding to the pieces of the current board.
   *
   * @param board Current board
   * @return hash corresponding to the board given in parameters
   */
  public long generateSimplifiedHashFromBitboards(Board board) {
    return generatePieceHash(board);
  }

  /**
   * Compute the simplified hash from a board -> update the change of position of piece and captured
   * piece.
   *
   * @param currHash previously computer hash
   * @param board current board
   * @param move last played move
   * @return updated hash
   */
  public long updateSimplifiedHashFromBitboards(long currHash, Board board, Move move) {
    return updatePieceHash(currHash, board, move);
  }
}
