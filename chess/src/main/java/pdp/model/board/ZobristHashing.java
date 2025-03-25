package pdp.model.board;

import java.util.concurrent.ThreadLocalRandom;
import pdp.exceptions.InvalidBoardException;
import pdp.model.piece.ColoredPiece;

/** Implementation of zobrist hashing to store hashes from boards. */
public class ZobristHashing {

  /** Number of types of pieces in chess. */
  private static final int PIECES_TYPES = 12;

  /** Number of squares on a chess board. */
  private static final int BOARD_SQUARES = 64;

  /** Bits necessary to compute castling rights. */
  private static final int CASTLING_RIGHTS = 16;

  /** Number of files (columns) where the en passant can happen. */
  private static final int EN_PASSANT_INDEX = 8;

  /** Hash for each piece. */
  private static final long[][] PIECES = new long[PIECES_TYPES][BOARD_SQUARES];

  /** Hash for each castling rights. */
  private static final long[] CASTLING = new long[CASTLING_RIGHTS];

  /** Hash for each en passant files. */
  private static final long[] EN_PASSANT = new long[EN_PASSANT_INDEX];

  /** Hash corresponding to a player move. */
  private static final long SIDE_TO_MOVE;

  /** Index of the previous castling rights. */
  private int prevCastlingIndex;

  /** Index of the previous en passant file. */
  private int prevEnPassantFile = -1;

  static {
    // Initialize static tables once using ThreadLocalRandom
    for (int i = 0; i < PIECES_TYPES; i++) {
      for (int j = 0; j < BOARD_SQUARES; j++) {
        PIECES[i][j] = ThreadLocalRandom.current().nextLong();
      }
    }
    for (int i = 0; i < CASTLING_RIGHTS; i++) {
      CASTLING[i] = ThreadLocalRandom.current().nextLong();
    }
    for (int i = 0; i < EN_PASSANT_INDEX; i++) {
      EN_PASSANT[i] = ThreadLocalRandom.current().nextLong();
    }
    SIDE_TO_MOVE = ThreadLocalRandom.current().nextLong();
  }

  /** Constructor to initialize the components to the future hash. */
  public ZobristHashing() {
    // Nothing to initialise.
  }

  /**
   * Initializes the previous en passant file and castling index according to the parent.
   *
   * @param parent Zobrist hashing to inherit from.
   */
  public ZobristHashing(final ZobristHashing parent) {
    this.prevCastlingIndex = parent.prevCastlingIndex;
    this.prevEnPassantFile = parent.prevEnPassantFile;
  }

  /**
   * Translates the castling rights of the given board into an integer representation. The castling
   * rights are encoded as a 4-bit integer: Bit 0 (1) White can castle kingside, Bit 1 (2) White can
   * castle queenside, Bit 2 (4) Black can castle kingside, Bit 3 (8) Black can castle queenside
   *
   * @param board Current board game to get the castling rights
   * @return An integer (0 to 15) representing castling rights, or -1 if no castling is possible.
   */
  private int translateCastling(final Board board) {
    int castlingRights = 0;
    if (!board.isWhiteShortCastle()
        && !board.isWhiteLongCastle()
        && !board.isBlackShortCastle()
        && !board.isBlackLongCastle()) {
      castlingRights = -1;
    } else {
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
    }
    return castlingRights;
  }

  /**
   * Generate the hash corresponding to the pieces of the current board.
   *
   * @param boardRep Current board representation
   * @return hash corresponding to the board given in parameters
   */
  private long generatePieceHash(final BoardRepresentation boardRep) {
    if (!(boardRep instanceof BitboardRepresentation bitboardRep)) {
      throw new InvalidBoardException();
    }
    final Bitboard[] bitboards = bitboardRep.getBitboards();
    long hash = 0;
    for (int i = 0; i < PIECES_TYPES; i++) {
      long bitboardValue = bitboards[i].getBits();
      while (bitboardValue != 0) {
        final int square = Long.numberOfTrailingZeros(bitboardValue);
        hash ^= PIECES[i][square];
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
  private long updatePieceHash(final long currHash, final Board board, final Move move) {
    if (!(board.getBoardRep() instanceof BitboardRepresentation)) {
      throw new InvalidBoardException();
    }
    long hash = currHash;

    final int from = move.getSource().x() + move.getSource().y() * board.getBoardRep().getNbRows();
    final int to = move.getDest().x() + move.getDest().y() * board.getBoardRep().getNbCols();

    // Remove piece from its source and add it to the destination
    hash ^= PIECES[BitboardRepresentation.getPiecesMap().getFromValue(move.getPiece())][to];
    hash ^= PIECES[BitboardRepresentation.getPiecesMap().getFromValue(move.getPiece())][from];

    final ColoredPiece capturedPiece = move.getPieceTaken();
    // delete the captured piece
    if (capturedPiece != null) {
      hash ^= PIECES[BitboardRepresentation.getPiecesMap().getFromValue(capturedPiece)][to];
    }
    return hash;
  }

  /**
   * Generate the hash corresponding to the current board state.
   *
   * @param board Current board
   * @return hash corresponding to the board given in parameters
   */
  public long generateHashFromBitboards(final Board board) {
    long hash = generatePieceHash(board.getBoardRep());
    prevCastlingIndex = translateCastling(board);
    if (prevCastlingIndex != -1) {
      hash ^= CASTLING[prevCastlingIndex];
    }

    if (board.getEnPassantPos() != null) {
      prevEnPassantFile = board.getEnPassantPos().x();
      hash ^= EN_PASSANT[prevEnPassantFile];
    }
    if (board.getPlayer()) {
      hash ^= SIDE_TO_MOVE;
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
  public long updateHashFromBitboards(final long currHash, final Board board, final Move move) {
    long hash = updatePieceHash(currHash, board, move);
    // if en passant is not possible in the column saved before
    if (prevEnPassantFile != -1) {
      hash ^= EN_PASSANT[prevEnPassantFile];
      prevEnPassantFile = -1;
    }
    // if en passant is possible
    if (board.getEnPassantPos() != null) {
      prevEnPassantFile = board.getEnPassantPos().x();
      hash ^= EN_PASSANT[prevEnPassantFile];
    }

    // update castling rights if needed
    final int newCastlingIndex = translateCastling(board);
    if (prevCastlingIndex != newCastlingIndex
        && newCastlingIndex != -1
        && prevCastlingIndex != -1) {
      hash ^= CASTLING[prevCastlingIndex];
      hash ^= CASTLING[newCastlingIndex];
      prevCastlingIndex = newCastlingIndex;
    }

    hash ^= SIDE_TO_MOVE;

    return hash;
  }

  /**
   * Generate the simplified hash corresponding to the pieces of the current board.
   *
   * @param board Current board
   * @return hash corresponding to the board given in parameters
   */
  public long generateSimplifiedHashFromBitboards(final Board board) {
    return generatePieceHash(board.getBoardRep());
  }

  /**
   * Generate the simplified hash corresponding to the pieces of the current board representation.
   *
   * @param boardRep current board representation
   * @return hash corresponding to the given board
   */
  public long generateSimplifiedHashFromBitboards(final BoardRepresentation boardRep) {
    return generatePieceHash(boardRep);
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
  public long updateSimplifiedHashFromBitboards(
      final long currHash, final Board board, final Move move) {
    return updatePieceHash(currHash, board, move);
  }
}
