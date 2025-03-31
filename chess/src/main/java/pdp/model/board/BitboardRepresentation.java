package pdp.model.board;

import static pdp.utils.Logging.debug;
import static pdp.utils.Logging.error;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import pdp.exceptions.IllegalMoveException;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.BiDirectionalMap;
import pdp.utils.Logging;
import pdp.utils.Position;

/** Implementation of BoardRepresentation using bitboards. */
public class BitboardRepresentation implements BoardRepresentation {
  /** Size of the cache. */
  private static final int CACHE_SIZE = 10_000;

  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(BitboardRepresentation.class.getName());

  /**
   * Array of bitboard representing the placement of each piece type for white and black players.
   */
  private final Bitboard[] board;

  /** Number of columns on a chess board. */
  private static final int NB_COLS = 8;

  /** Number of rows on a chess board. */
  private static final int NB_ROWS = 8;

  /** Bidirectional map associating each bitmap to the type of piece stored inside. */
  private static final BiDirectionalMap<Integer, ColoredPiece> pieces = new BiDirectionalMap<>();

  /** Cache instance. */
  private static final BitboardCache cache;

  /** Zobrist hashing instance used to generate the hash. */
  private static final ZobristHashing zobristHashing;

  /** Zobrist simple hash generated for the board at a given moment. */
  private long simpleHash;

  /** Position where an en passant could happen in the next turn. */
  private Position enPassantPos;

  /** Boolean to indicate whether the last move was a double push or not. */
  private boolean isLastMoveDoublePush;

  /**
   * Boolean to indicate whether a white long castle is still possible to make (the tower and king
   * didn't move).
   */
  private boolean whiteLongCastle;

  /**
   * Boolean to indicate whether a white short castle is still possible to make (the tower and king
   * didn't move).
   */
  private boolean whiteShortCastle;

  /**
   * Boolean to indicate whether a black long castle is still possible to make (the tower and king
   * didn't move).
   */
  private boolean blackLongCastle;

  /**
   * Boolean to indicate whether a black short castle is still possible to make (the tower and king
   * didn't move).
   */
  private boolean blackShortCastle;

  /** Boolean to indicate whether the current player is white or black. */
  private boolean isWhite;

  /** Boolean to indicate if the capture was an en passant take. */
  private boolean isEnPassantTake;

  /** Counter of move made without a pawn moved or a piece captured. */
  private int nbMovesWithNoCaptureOrPawn;

  static {
    Logging.configureLogging(LOGGER);
    pieces.put(0, new ColoredPiece(Piece.KING, Color.WHITE));
    pieces.put(1, new ColoredPiece(Piece.QUEEN, Color.WHITE));
    pieces.put(2, new ColoredPiece(Piece.BISHOP, Color.WHITE));
    pieces.put(3, new ColoredPiece(Piece.ROOK, Color.WHITE));
    pieces.put(4, new ColoredPiece(Piece.KNIGHT, Color.WHITE));
    pieces.put(5, new ColoredPiece(Piece.PAWN, Color.WHITE));
    pieces.put(6, new ColoredPiece(Piece.KING, Color.BLACK));
    pieces.put(7, new ColoredPiece(Piece.QUEEN, Color.BLACK));
    pieces.put(8, new ColoredPiece(Piece.BISHOP, Color.BLACK));
    pieces.put(9, new ColoredPiece(Piece.ROOK, Color.BLACK));
    pieces.put(10, new ColoredPiece(Piece.KNIGHT, Color.BLACK));
    pieces.put(11, new ColoredPiece(Piece.PAWN, Color.BLACK));

    zobristHashing = new ZobristHashing();

    cache = new BitboardCache(CACHE_SIZE);
  }

  /*
  BitBoards order:
     0. White king
     1. White queen
     2. White bishops
     3. White rooks
     4. White knights
     5. White pawns
     6. Black king
     7. Black queen
     8. Black bishops
     9. Black rooks
     10. Black knights
     11. Black pawns
   */
  /** Initialize all the bitboards to the default values for a board when the game begin. */
  public BitboardRepresentation() {
    this.isWhite = true;
    this.enPassantPos = null;
    this.whiteShortCastle = true;
    this.blackShortCastle = true;
    this.whiteLongCastle = true;
    this.blackLongCastle = true;
    this.isLastMoveDoublePush = false;
    this.isEnPassantTake = false;
    this.nbMovesWithNoCaptureOrPawn = 0;

    board = new Bitboard[12];
    board[0] = new Bitboard(16L); // WKi
    board[1] = new Bitboard(8L); // WQ
    board[2] = new Bitboard(36L); // WB
    board[3] = new Bitboard(129L); // WR
    board[4] = new Bitboard(66L); // WKn
    board[5] = new Bitboard(65_280L); // WP
    board[6] = new Bitboard(1_152_921_504_606_846_976L); // BKi
    board[7] = new Bitboard(576_460_752_303_423_488L); // BQ
    board[8] = new Bitboard(2_594_073_385_365_405_696L); // BB
    board[9] = new Bitboard(); // BR // TODO: Find why overflow ???
    board[9].setBit(56);
    board[9].setBit(63);
    board[10] = new Bitboard(4_755_801_206_503_243_776L); // BKi
    board[11] = new Bitboard(71_776_119_061_217_280L);

    this.simpleHash = zobristHashing.generateSimplifiedHashFromBitboards(this);
  }

  /**
   * Create a board from a given board state (support FileBoard header).
   *
   * @param board The board state to use
   */
  public BitboardRepresentation(FileBoard board) {

    this();
    this.isWhite = board.isWhiteTurn();

    if (board.header() != null) {
      this.enPassantPos = board.header().enPassant();
      if (this.enPassantPos != null) {
        this.isLastMoveDoublePush = true;
      }

      this.whiteShortCastle = board.header().whiteKingCastling();
      this.blackShortCastle = board.header().blackKingCastling();
      this.whiteLongCastle = board.header().whiteQueenCastling();
      this.blackLongCastle = board.header().blackQueenCastling();

      this.nbMovesWithNoCaptureOrPawn = board.header().fiftyMoveRule();
    } else {
      this.enPassantPos = null;
      this.isLastMoveDoublePush = false;

      this.whiteShortCastle =
          board.board().getPieceAt(7, 0).equals(new ColoredPiece(Piece.ROOK, Color.WHITE))
              && board.board().getPieceAt(4, 0).equals(new ColoredPiece(Piece.KING, Color.WHITE));
      this.blackShortCastle =
          board.board().getPieceAt(7, 7).equals(new ColoredPiece(Piece.ROOK, Color.BLACK))
              && board.board().getPieceAt(4, 7).equals(new ColoredPiece(Piece.KING, Color.BLACK));
      this.whiteLongCastle =
          board.board().getPieceAt(0, 0).equals(new ColoredPiece(Piece.ROOK, Color.WHITE))
              && board.board().getPieceAt(4, 0).equals(new ColoredPiece(Piece.KING, Color.WHITE));
      this.blackLongCastle =
          board.board().getPieceAt(0, 7).equals(new ColoredPiece(Piece.ROOK, Color.BLACK))
              && board.board().getPieceAt(4, 7).equals(new ColoredPiece(Piece.KING, Color.BLACK));

      this.nbMovesWithNoCaptureOrPawn = 0;
    }

    for (int y = 0; y < NB_ROWS; y++) {
      for (int x = 0; x < NB_COLS; x++) {
        ColoredPiece piece = board.board().getPieceAt(x, y);
        ColoredPiece current = this.getPieceAt(x, y);
        if (current.getPiece() != Piece.EMPTY) {
          this.deletePieceAt(x, y);
        }
        if (piece.getPiece() != Piece.EMPTY) {
          this.setSquare(piece, x + y * this.getNbCols());
        }
      }
    }
  }

  /**
   * Initialize a board with all wanted values.
   *
   * @param whiteKing The white king bitboard
   * @param whiteQueen The white queen bitboard
   * @param whiteBishops The white bishops bitboard
   * @param whiteRooks The white rooks bitboard
   * @param whiteKnights The white knights bitboard
   * @param whitePawns The white pawns bitboard
   * @param blackKing The black king bitboard
   * @param blackQueen The black queen bitboard
   * @param blackBishops The black bishops bitboard
   * @param blackRooks The black rooks bitboard
   * @param blackKnights The black knights bitboard
   * @param blackPawns The black pawns bitboard
   */
  @Deprecated
  public BitboardRepresentation(
      final Bitboard whiteKing,
      final Bitboard whiteQueen,
      final Bitboard whiteBishops,
      final Bitboard whiteRooks,
      final Bitboard whiteKnights,
      final Bitboard whitePawns,
      final Bitboard blackKing,
      final Bitboard blackQueen,
      final Bitboard blackBishops,
      final Bitboard blackRooks,
      final Bitboard blackKnights,
      final Bitboard blackPawns) {

    this.isWhite = true;
    this.enPassantPos = null;
    this.whiteShortCastle = true;
    this.blackShortCastle = true;
    this.whiteLongCastle = true;
    this.blackLongCastle = true;
    this.isLastMoveDoublePush = false;
    this.isEnPassantTake = false;
    this.nbMovesWithNoCaptureOrPawn = 0;

    board = new Bitboard[12];
    board[0] = whiteKing;
    board[1] = whiteQueen;
    board[2] = whiteBishops;
    board[3] = whiteRooks;
    board[4] = whiteKnights;
    board[5] = whitePawns;
    board[6] = blackKing;
    board[7] = blackQueen;
    board[8] = blackBishops;
    board[9] = blackRooks;
    board[10] = blackKnights;
    board[11] = blackPawns;
  }

  /**
   * Retrieves the bidirectional map mapping a bitboard to the type of pieces it represents.
   *
   * @return field pieces.
   */
  public static BiDirectionalMap<Integer, ColoredPiece> getPiecesMap() {
    return pieces;
  }

  @Override
  public String toString() {
    return getWhiteBoard().or(getBlackBoard()).toString();
  }

  @Override
  public boolean equals(final Object o) {
    if (o instanceof BitboardRepresentation obj) {
      if (board.length != obj.board.length) {
        return false;
      }
      for (int i = 0; i < board.length; i++) {
        if (!board[i].equals(obj.board[i])) {
          return false;
        }
      }
      /*
      if (this.getPlayer() != obj.getPlayer() || this.isLastMoveDoublePush() != obj.isLastMoveDoublePush() || this.getEnPassantPos().equals(obj.getEnPassantPos())
      || this.isWhiteShortCastle() != obj.isWhiteShortCastle() || this.isBlackShortCastle() != obj.isBlackShortCastle() || this.isWhiteLongCastle() != obj.isWhiteLongCastle()
      || this.isBlackLongCastle() != obj.isBlackLongCastle() || this.getNbMovesWithNoCaptureOrPawn() != obj.getNbMovesWithNoCaptureOrPawn()) {
        return false;
      }
        */
      return true;
    }
    return false;
  }

  /**
   * Creates a deep copy of this BitboardRepresentation object. Each bitboard is copied
   * independently to avoid shared references.
   *
   * @return A new instance of BitboardRepresentation with the same state as the current object.
   */
  @Override
  public BoardRepresentation getCopy() {
    final BitboardRepresentation copy = new BitboardRepresentation();
    for (int i = 0; i < this.board.length; i++) {
      copy.board[i] = this.board[i].getCopy();
    }

    copy.setPlayer(this.getPlayer());
    copy.setWhiteShortCastle(this.isWhiteShortCastle());
    copy.setBlackShortCastle(this.isBlackShortCastle());
    copy.setWhiteLongCastle(this.isWhiteLongCastle());
    copy.setBlackLongCastle(this.isBlackLongCastle());
    copy.setEnPassantPos(
        (this.getEnPassantPos() != null) ? this.getEnPassantPos().getCopy() : null);
    copy.setLastMoveDoublePush(this.isLastMoveDoublePush());
    copy.setEnPassantTake(this.isEnPassantTake());
    copy.setNbMovesWithNoCaptureOrPawn(this.getNbMovesWithNoCaptureOrPawn());

    copy.simpleHash = this.simpleHash;

    return copy;
  }

  /**
   * Get the piece and its color for the given square.
   *
   * @param x column
   * @param y row
   * @return Piece and its Color
   */
  @Override
  public ColoredPiece getPieceAt(final int x, final int y) {
    final ColoredPiece piece;
    final int square = x + 8 * y;
    for (int index = 0; index < board.length; index++) {
      if (board[index].getBit(square)) {
        piece = pieces.getFromKey(index);
        return piece;
      }
    }
    piece = new ColoredPiece(Piece.EMPTY, Color.EMPTY);
    return piece;
  }

  /**
   * Move a piece from a source position to a destination position Can throw an exception if there
   * isn't a piece at source position.
   *
   * @param from The initial position of the piece
   * @param to The position to reach
   */
  @Override
  public void movePiece(final Position from, final Position to) {
    final ColoredPiece piece = getPieceAt(from.x(), from.y());
    final int fromIndex = from.x() % 8 + from.y() * 8;
    final int toIndex = to.x() % 8 + to.y() * 8;
    final int bitboardIndex =
        switch (piece.getPiece()) {
          case KING -> piece.getColor() == Color.WHITE ? 0 : 6;
          case QUEEN -> piece.getColor() == Color.WHITE ? 1 : 7;
          case BISHOP -> piece.getColor() == Color.WHITE ? 2 : 8;
          case ROOK -> piece.getColor() == Color.WHITE ? 3 : 9;
          case KNIGHT -> piece.getColor() == Color.WHITE ? 4 : 10;
          case PAWN -> piece.getColor() == Color.WHITE ? 5 : 11;
          default -> throw new IllegalArgumentException("Invalid piece: " + piece.getColor());
        };
    board[bitboardIndex].clearBit(fromIndex);
    board[bitboardIndex].setBit(toIndex);

    this.simpleHash = zobristHashing.generateSimplifiedHashFromBitboards(this);
  }

  /**
   * Replaces pawnToPromote with newPiece. Bitboards get changed. Assumes pawn can be promoted.
   *
   * @param x The x-coordinate (file) of the pawn
   * @param y The y-coordinate (rank) of the pawn
   * @param white {true} if pawn is white, {false} if pawn is black
   * @param newPiece The piece asked by the player that is replacing the promoting pawn
   */
  @Override
  public void promotePawn(final int x, final int y, final boolean white, final Piece newPiece) {
    debug(LOGGER, "Promoting pawn at [" + x + ", " + y + "] to " + newPiece);
    final ColoredPiece pieceAtPosition = getPieceAt(x, y);
    if (pieceAtPosition.getPiece() != Piece.PAWN
        || pieceAtPosition.getColor() != (white ? Color.WHITE : Color.BLACK)) {
      return;
    }

    final int boardIndex = white ? 0 : 6;
    final Bitboard newPieceBitBoard;
    final Bitboard pawnBitboard = this.board[5 + boardIndex];
    switch (newPiece) {
      case KNIGHT:
        newPieceBitBoard = this.board[4 + boardIndex];
        break;
      case BISHOP:
        newPieceBitBoard = this.board[2 + boardIndex];
        break;
      case ROOK:
        newPieceBitBoard = this.board[3 + boardIndex];
        break;
      case QUEEN:
        newPieceBitBoard = this.board[1 + boardIndex];
        break;
      default:
        error("Error: A pawn can only be promoted to Queen, Rook, Knight or Bishop !");
        return;
    }

    final int bitIndex = 8 * y + x;
    // Change bits
    pawnBitboard.clearBit(bitIndex);
    newPieceBitBoard.setBit(bitIndex);

    this.simpleHash = zobristHashing.generateSimplifiedHashFromBitboards(this);
  }

  /**
   * Sets the given squareIndex in the bitboard corresponding to piece.
   *
   * @param piece type of piece to update in the bitboards.
   * @param squareIndex position to set in the bitboard.
   */
  public void setSquare(final ColoredPiece piece, final int squareIndex) {
    board[pieces.getFromValue(piece)].setBit(squareIndex);
    this.simpleHash = zobristHashing.generateSimplifiedHashFromBitboards(this);
  }

  /**
   * Retrieves the bitboards representing the current chess board.
   *
   * @return field board.
   */
  protected Bitboard[] getBitboards() {
    return this.board;
  }

  /**
   * Delete the piece contained at the given position.
   *
   * @param x The board column
   * @param y The board row
   */
  @Override
  public void deletePieceAt(final int x, final int y) {
    final ColoredPiece piece = getPieceAt(x, y);
    board[pieces.getFromValue(piece)].clearBit(x % 8 + y * 8);
    this.simpleHash = zobristHashing.generateSimplifiedHashFromBitboards(this);
    debug(LOGGER, "Piece at position " + x + " and position " + y + " was removed");
  }

  /**
   * Add a new piece in the bitboard corresponding to the given piece at the coordinates x,y. This
   * method should be only used for undo/redo moves.
   *
   * @param x The board column
   * @param y The board row
   * @param piece The type of piece to add
   */
  protected void addPieceAt(final int x, final int y, final ColoredPiece piece) {
    board[pieces.getFromValue(piece)].setBit(x % 8 + y * 8);
    this.simpleHash = zobristHashing.generateSimplifiedHashFromBitboards(this);
    debug(LOGGER, "A " + piece.getColor() + " " + piece.getPiece() + " was added to the board");
  }

  /**
   * Applies short castle for color {color}. Changes bitboards. Assumes castle is possible.
   *
   * @param color color for which castling move is applied
   */
  @Override
  public void applyShortCastle(final Color color) {
    if (color == Color.WHITE) {
      final Position e1Square = new Position(4, 0);
      final Position f1Square = new Position(5, 0);
      final Position g1Square = new Position(6, 0);
      final Position h1Square = new Position(7, 0);
      // Move king
      this.movePiece(e1Square, g1Square);
      // Move rook
      this.movePiece(h1Square, f1Square);

      this.whiteShortCastle = false;
      this.whiteLongCastle = false;
    } else {
      final Position e8Square = new Position(4, 7);
      final Position f8Square = new Position(5, 7);
      final Position g8Square = new Position(6, 7);
      final Position h8Square = new Position(7, 7);
      // Move king
      this.movePiece(e8Square, g8Square);
      // Move rook
      this.movePiece(h8Square, f8Square);

      this.blackShortCastle = false;
      this.blackLongCastle = false;
    }
  }

  /**
   * Applies long castle for color {color}. Changes bitboards. Assumes castle is possible.
   *
   * @param color color for which castling move is applied
   */
  @Override
  public void applyLongCastle(final Color color) {
    if (color == Color.WHITE) {
      final Position e1Square = new Position(4, 0);
      final Position d1Square = new Position(3, 0);
      final Position c1Square = new Position(2, 0);
      final Position a1Square = new Position(0, 0);
      // Move king
      this.movePiece(e1Square, c1Square);
      // Move rook
      this.movePiece(a1Square, d1Square);

      this.whiteShortCastle = false;
      this.whiteLongCastle = false;
    } else {
      final Position e8Square = new Position(4, 7);
      final Position d8Square = new Position(3, 7);
      final Position c8Square = new Position(2, 7);
      final Position a8Square = new Position(0, 7);
      // Move king
      this.movePiece(e8Square, c8Square);
      // Move rook
      this.movePiece(a8Square, d8Square);

      this.blackShortCastle = false;
      this.blackLongCastle = false;
    }
  }

  /**
   * Generate the list of possible moves from a given position for a king piece.
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @param enemies Enemies occupation bitboard
   * @return The list of possible moves
   */
  protected List<Move> getKingMoves(
      final Position square,
      final Bitboard unreachableSquares,
      final Bitboard enemies,
      final ColoredPiece piece) {
    return BitboardMovesGen.getKingMoves(
        square,
        unreachableSquares,
        enemies,
        piece,
        this,
        whiteLongCastle,
        whiteShortCastle,
        blackLongCastle,
        blackShortCastle);
  }

  /**
   * Generate the possible moves from a position depending on the piece type. This function do not
   * apply special rules (castling, pinned, ...).
   *
   * @param x The board column
   * @param y The board row
   * @param kingReachable Can the piece reach opponent king (keep false if not checking
   *     check/checkmate)
   * @return The list of possible moves (without special cases)
   */
  @Override
  public List<Move> getAvailableMoves(final int x, final int y, final boolean kingReachable) {
    return BitboardMovesGen.getAvailableMoves(
        x,
        y,
        kingReachable,
        this,
        enPassantPos,
        isLastMoveDoublePush,
        whiteLongCastle,
        whiteShortCastle,
        blackLongCastle,
        blackShortCastle);
  }

  /**
   * Generate the possible moves for a player. This function do not apply special rules (castling,
   * pinned, ...).
   *
   * @param isWhite {true} if pawn is white, {false} if pawn is black
   * @return The list of possible moves (without special cases)
   */
  @Override
  public List<Move> getAllAvailableMoves(final boolean isWhite) {
    return BitboardMovesGen.getAllAvailableMoves(
        isWhite,
        this,
        enPassantPos,
        isLastMoveDoublePush,
        whiteLongCastle,
        whiteShortCastle,
        blackLongCastle,
        blackShortCastle);
  }

  /**
   * Generate the possible moves for a player. This function do not apply check after guard.
   * Optimised for AI.
   *
   * @param isWhite {true} if pawn is white, {false} if pawn is black
   * @return The bitboard containing all possible moves (without special cases)
   */
  public Bitboard getColorMoveBitboard(final boolean isWhite) {
    // TODO: can delete this function ?
    // Bitboard moves =
    // BitboardMovesGen.getColorAttackBitboard(isWhite, this, enPassantPos, isLastMoveDoublePush);

    return getColorAttackBitboard(isWhite);
  }

  /**
   * Generate the possible moves for a player. This function do not apply check after guard.
   * Optimised for AI.
   *
   * @param isWhite {true} if pawn is white, {false} if pawn is black
   * @return The bitboard containing all possible moves (without special cases)
   */
  public Bitboard getColorAttackBitboard(final boolean isWhite) {
    final CachedResult cached = cache.getOrCreate(simpleHash);
    final Long attackLong = cached.getAttackBitboard(isWhite);
    if (attackLong != null) {
      return new Bitboard(attackLong);
    }

    final Bitboard attackBitboard =
        BitboardMovesGen.getColorAttackBitboard(isWhite, this, enPassantPos, isLastMoveDoublePush);
    cached.setAttackBitboard(isWhite, attackBitboard);
    return attackBitboard;
  }

  // ________________________ BitboardPieces

  /**
   * Get the positions of the pawns.
   *
   * @param white if true -> white pawns, if false -> black pawns
   * @return A list of the pawns positions for the given color
   */
  @Override
  public List<Position> getPawns(final boolean white) {
    return BitboardPieces.getPawns(white, this);
  }

  /**
   * Get the positions of the rooks.
   *
   * @param white if true -> white rooks, if false -> black rooks
   * @return A list of the rooks positions for the given color
   */
  @Override
  public List<Position> getRooks(final boolean white) {
    return BitboardPieces.getRooks(white, this);
  }

  /**
   * Get the positions of the bishops.
   *
   * @param white if true -> white bishops, if false -> black bishops
   * @return A list of the bishops positions for the given color
   */
  @Override
  public List<Position> getBishops(final boolean white) {
    return BitboardPieces.getBishops(white, this);
  }

  /**
   * Get the positions of the knights.
   *
   * @param white if true -> white knights, if false -> black knights
   * @return A list of the knights positions for the given color
   */
  @Override
  public List<Position> getKnights(final boolean white) {
    return BitboardPieces.getKnights(white, this);
  }

  /**
   * Get the positions of the queens.
   *
   * @param white if true -> white queens, if false -> black queens
   * @return A list of the queens positions for the given color
   */
  @Override
  public List<Position> getQueens(final boolean white) {
    return BitboardPieces.getQueens(white, this);
  }

  /**
   * Get the positions of the king.
   *
   * @param white if true -> white king, if false -> black king
   * @return A list containing the king position for the given color
   */
  @Override
  public List<Position> getKing(final boolean white) {
    return BitboardPieces.getKing(white, this);
  }

  /**
   * Retrieves index in the bitmap of the king of the given color. Optimized with bitboards so more
   * efficient.
   *
   * @param white if true -> white king, if false -> black king
   * @return An int corresponding to the king position for the given color
   */
  public int getKingOpti(final boolean white) {
    return BitboardPieces.getKingOpti(white, this);
  }

  /**
   * Checks and returns the number of remaining pieces on the board.
   *
   * @return the number of remaining pieces on the board
   */
  @Override
  public int nbPiecesRemaining() {
    return BitboardPieces.nbPiecesRemaining(this);
  }

  /**
   * Get the bitboard that contains all the white pieces, by or on all white pieces bitboards.
   *
   * @return the bitboard containing all white pieces
   */
  protected Bitboard getWhiteBoard() {
    return BitboardPieces.getWhiteBoard(this);
  }

  /**
   * Get the bitboard that contains all the black pieces, by or on all black pieces bitboards.
   *
   * @return the bitboard containing all black pieces
   */
  protected Bitboard getBlackBoard() {
    return BitboardPieces.getBlackBoard(this);
  }

  // ________________________ BitboardRules

  /**
   * Get if the given square (x,y format) can be attacked by a piece of the given color.
   *
   * @param x X coordinate of the Position
   * @param y Y coordinate of the Position
   * @param by The color of the attacker
   * @return True if the given square is attacked, False else
   */
  @Override
  public boolean isAttacked(final int x, final int y, final Color by) {

    return BitboardRules.isAttacked(x, y, by, this);
  }

  /**
   * Get the check state for the given color.
   *
   * @param color The piece color you want to know check status
   * @return True if the given color is in check, False else
   */
  @Override
  public boolean isCheck(final Color color) {
    final CachedResult cached = cache.getOrCreate(simpleHash);
    Boolean isCheck = cached.isCheck(color);
    if (isCheck != null) {
      return isCheck;
    }

    isCheck = BitboardRules.isCheck(color, this);
    cached.setCheck(isCheck, color);

    return isCheck;
  }

  /**
   * Get the check state after move for the given color.
   *
   * @param color The piece color you want to know check status
   * @param move The move you want to check if it puts the king in check
   * @return True if the given color is in check after the given move, False else
   */
  @Override
  public boolean isCheckAfterMove(final Color color, final Move move) {
    return BitboardRules.isCheckAfterMove(color, move, this);
  }

  /**
   * Get the checkMate state for the given color (can be resources/time-consuming if there are many
   * pieces remaining on the board).
   *
   * @param color The piece color you want to know checkMate status
   * @return True if the given color is in checkMate, False else
   */
  @Override
  public boolean isCheckMate(final Color color) {
    final CachedResult cached = cache.getOrCreate(simpleHash);
    Boolean isCheckMate = cached.isCheckMate(color);
    if (isCheckMate != null) {
      return isCheckMate;
    }

    isCheckMate = BitboardRules.isCheckMate(color, this);

    cached.setCheckMate(isCheckMate, color);
    return isCheckMate;
  }

  /**
   * Checks the StaleMate state for the given color.
   *
   * @param color The color you want to check StaleMate for
   * @param colorTurnToPlay Player's turn to know if player who potentially moves in check has to
   *     move
   * @return true if color {color} is stalemated. false otherwise.
   */
  @Override
  public boolean isStaleMate(final Color color, final Color colorTurnToPlay) {
    final Boolean res = cache.getOrCreate(simpleHash).isStaleMate(color);
    if (res != null) {
      return res;
    }
    final boolean cacheElt = BitboardRules.isStaleMate(color, colorTurnToPlay, this);
    cache.getOrCreate(simpleHash).setStaleMate(cacheElt, color);
    return cacheElt;
  }

  /**
   * Checks if draw by insufficient material is observed (both colors each case) Cases: King vs King
   * and Bishop vs King and Knight vs King and Bishop vs King and Bishop (same colored Bishops).
   *
   * @return true if a draw by insufficient material is observed
   */
  @Override
  public boolean isDrawByInsufficientMaterial() {
    return BitboardRules.isDrawByInsufficientMaterial(this);
  }

  /**
   * Checks if a pawn at Position(x,y) checks for promotion.
   *
   * @param x The x-coordinate (file) of the pawn
   * @param y The y-coordinate (rank) of the pawn
   * @param white {true} if pawn is white, {false} if pawn is black
   * @return true if the pawn is being promoted, otherwise false
   */
  @Override
  public boolean isPawnPromoting(final int x, final int y, final boolean white) {
    return BitboardRules.isPawnPromoting(x, y, white, this);
  }

  /**
   * Checks if a pawn at Position(x,y) checks for promotion.
   *
   * @param xSource The x-coordinate (file) of the source position
   * @param ySource The y-coordinate (rank) of the source position
   * @param xDest The x-coordinate (file) of the destination position
   * @param yDest The y-coordinate (rank) of the destination position
   * @param isWhite {true} if pawn is white, {false} if pawn is black
   * @return true if the pawn is being promoted with the move, otherwise false
   */
  @Override
  public boolean isPromotionMove(
      final int xSource,
      final int ySource,
      final int xDest,
      final int yDest,
      final boolean isWhite) {
    return BitboardRules.isPromotionMove(xSource, ySource, xDest, yDest, isWhite, this);
  }

  /**
   * Checks if a given move is a double pawn push A double push occurs when a pawn moves forward by
   * two squares from its starting position.
   *
   * @param move The move to check
   * @param white {true} if pawn is white, {false} if pawn is black
   * @return True if the move is a valid double pawn push, false else
   */
  @Override
  public boolean isDoublePushPossible(final Move move, final boolean white) {
    return BitboardRules.isDoublePushPossible(move, white, this);
  }

  /**
   * Checks if a given move is an en passant.
   *
   * @param x The x-coordinate of the square where an en passant capture can occur
   * @param y The y-coordinate of the square where an en passant capture can occur
   * @param move The move being checked
   * @param white {true} if pawn is white, {false} if pawn is black
   * @return True if the move is a valid en passant capture, false else
   */
  @Override
  public boolean isEnPassant(final int x, final int y, final Move move, final boolean white) {
    return BitboardRules.isEnPassant(x, y, move, white, this);
  }

  /**
   * Method that verifies of a player has enough material to mate. Used for rule loss on time but
   * enemy does not have enough material to mate.
   *
   * @param white color of the player we check the material for
   * @return true if {white} has enough material to mate. false otherwise
   */
  @Override
  public boolean hasEnoughMaterialToMate(final boolean white) {
    return BitboardRules.hasEnoughMaterialToMate(white, this);
  }

  /**
   * Returns the list of available moves for the king of either white or black.
   *
   * @param white true if we want the moves of the white king, false otherwise
   * @return the list of moves for the corresponding king
   */
  @Override
  public List<Move> retrieveKingMoves(final boolean white) {
    return BitboardMovesGen.retrieveKingMoves(
        white, this, whiteLongCastle, whiteShortCastle, blackLongCastle, blackShortCastle);
  }

  /**
   * Returns the list of available for the bishops of either white or black.
   *
   * @param white true if we want the moves of the white bishops, false otherwise
   * @return the list of moves for the corresponding bishops
   */
  @Override
  public List<Move> retrieveBishopMoves(final boolean white) {
    return BitboardMovesGen.retrieveBishopMoves(white, this);
  }

  /**
   * Retrieves the current positions of white pieces.
   *
   * @return the list containing the list of current positions for the white pieces
   */
  @Override
  public List<List<Position>> retrieveWhitePiecesPos() {
    return BitboardRules.retrieveWhitePiecesPos(this);
  }

  /**
   * Retrieves the current positions of black pieces.
   *
   * @return the list containing the list of current positions for the black pieces
   */
  @Override
  public List<List<Position>> retrieveBlackPiecesPos() {
    return BitboardRules.retrieveBlackPiecesPos(this);
  }

  /**
   * Retrieves the positions of white pieces at the start of the game.
   *
   * @return the list containing the list of initial positions for the white pieces
   */
  @Override
  public List<List<Position>> retrieveInitialWhitePiecesPos() {
    return BitboardRules.retrieveInitialWhitePiecesPos();
  }

  /**
   * Retrieves the positions of black pieces at the start of the game.
   *
   * @return the list containing the list of initial positions for the black pieces
   */
  @Override
  public List<List<Position>> retrieveInitialBlackPiecesPos() {
    return BitboardRules.retrieveInitialBlackPiecesPos();
  }

  /**
   * Determines if the given move is a castle move.
   *
   * @param coloredPiece The piece being moved, expected to be a king for castling.
   * @param source The source position of the move.
   * @param dest The destination position of the move.
   * @return true if the move is a castle move, false otherwise.
   */
  @Override
  public boolean isCastleMove(
      final ColoredPiece coloredPiece, final Position source, final Position dest) {
    return BitboardRules.isCastleMove(coloredPiece, source, dest);
  }

  /**
   * Return true if the piece located at sourcePosition is of the same color as the player that has
   * to play a move, false is not, and exception otherwise.
   *
   * @param white the game state for which we want to verify piece ownership
   * @param sourcePosition the position
   * @throws IllegalMoveException If the move is illegal in the current configuration.
   */
  @Override
  public boolean validatePieceOwnership(final boolean white, final Position sourcePosition) {
    return BitboardRules.validatePieceOwnership(white, sourcePosition, this);
  }

  // ________________________ BitboardStatusCheck

  /**
   * Checks if queens are off the board. Method used to detect endgames.
   *
   * @return true if queens are off the board. false otherwise
   */
  @Override
  public boolean queensOffTheBoard() {
    return BitboardStatusCheck.queensOffTheBoard(this);
  }

  /**
   * Checks the progress of pawns in the game for a specific color.
   *
   * @param isWhite true for white pawns, false for black pawns
   * @return true if the majority of pawns for the given color are past the middle of the board.
   */
  @Override
  public boolean pawnsHaveProgressed(final boolean isWhite) {
    return BitboardStatusCheck.pawnsHaveProgressed(isWhite, this);
  }

  /**
   * Checks if the kings on the board are active. Method used to detect endgames.
   *
   * @return true if kings are somewhat active. false otherwise
   */
  @Override
  public boolean areKingsActive() {
    return BitboardStatusCheck.areKingsActive(this);
  }

  /**
   * Checks if castle (long or short in parameter) for one side is possible or not. No need to fetch
   * king position because if king has moved, then boolean attributes for castling rights are false.
   *
   * @param color the color of the player we want to test castle for
   * @param shortCastle boolean value to indicate if we're looking for the short castle right or
   *     long castle right
   * @return true if castle {shortCastle} is possible for player of Color {color}. false otherwise
   */
  @Override
  public boolean canCastle(final Color color, final boolean shortCastle) {
    return BitboardStatusCheck.canCastle(
        color,
        shortCastle,
        whiteShortCastle,
        whiteLongCastle,
        blackShortCastle,
        blackLongCastle,
        this);
  }

  /**
   * Checks if the Game is in an end game phase. Used to know when to switch heuristics.
   *
   * @return true if we're in an endgame (according to the chosen criteria)
   */
  @Override
  public boolean isEndGamePhase(final int fullTurn, final boolean white) {
    return BitboardStatusCheck.isEndGamePhase(fullTurn, white, this);
  }

  // ________________________ BitboardUtils

  /**
   * Translate a list of squares (0..63) to a list of position (x,y).
   *
   * @param squares The list of squares to change to position
   * @return A new list containing the translations
   */
  protected List<Position> squaresToPosition(final List<Integer> squares) {
    return BitboardUtils.squaresToPosition(squares);
  }

  /**
   * Translate a squares (0..63) to a position (x,y).
   *
   * @param square The square to change to position
   * @return A Position containing the translations
   */
  protected Position squareToPosition(final int square) {
    return BitboardUtils.squareToPosition(square);
  }

  /**
   * Get the positions of all bits set to 1 in the given bitboard.
   *
   * @param bitBoardIndex The bitboard to lookUp
   * @return A list of positions
   */
  protected List<Position> getOccupiedSquares(final int bitBoardIndex) {
    return BitboardUtils.getOccupiedSquares(bitBoardIndex, this);
  }

  /**
   * Retrieves the number of columns of a board.
   *
   * @return The horizontal size of the board
   */
  @Override
  public int getNbCols() {
    return NB_COLS;
  }

  /**
   * Retrieves the number of rows of a board.
   *
   * @return The vertical size of the board
   */
  @Override
  public int getNbRows() {
    return NB_ROWS;
  }

  @Override
  public boolean getPlayer() {
    return this.isWhite;
  }

  @Override
  public void setPlayer(final boolean isWhite) {
    this.isWhite = isWhite;
  }

  @Override
  public Position getEnPassantPos() {
    return enPassantPos;
  }

  @Override
  public void setEnPassantPos(final Position enPassantPos) {
    this.enPassantPos = enPassantPos;
  }

  @Override
  public boolean isLastMoveDoublePush() {
    return isLastMoveDoublePush;
  }

  @Override
  public void setLastMoveDoublePush(final boolean lastMoveDoublePush) {
    isLastMoveDoublePush = lastMoveDoublePush;
  }

  @Override
  public boolean isWhiteShortCastle() {
    return whiteShortCastle;
  }

  @Override
  public void setWhiteShortCastle(final boolean whiteShortCastle) {
    this.whiteShortCastle = whiteShortCastle;
  }

  @Override
  public boolean isBlackShortCastle() {
    return blackShortCastle;
  }

  @Override
  public void setBlackShortCastle(final boolean blackShortCastle) {
    this.blackShortCastle = blackShortCastle;
  }

  @Override
  public boolean isWhiteLongCastle() {
    return whiteLongCastle;
  }

  @Override
  public void setWhiteLongCastle(final boolean whiteLongCastle) {
    this.whiteLongCastle = whiteLongCastle;
  }

  @Override
  public boolean isBlackLongCastle() {
    return blackLongCastle;
  }

  @Override
  public void setBlackLongCastle(final boolean blackLongCastle) {
    this.blackLongCastle = blackLongCastle;
  }

  @Override
  public boolean isEnPassantTake() {
    return isEnPassantTake;
  }

  @Override
  public void setEnPassantTake(final boolean enPassantTake) {
    isEnPassantTake = enPassantTake;
  }

  @Override
  public int getNbMovesWithNoCaptureOrPawn() {
    return nbMovesWithNoCaptureOrPawn;
  }

  @Override
  public void setNbMovesWithNoCaptureOrPawn(final int newVal) {
    this.nbMovesWithNoCaptureOrPawn = newVal;
  }

  @Override
  public List<Move> getAvailableMoves(Position pos) {
    return this.getAvailableMoves(pos.x(), pos.y(), false);
  }

  @Override
  public int getNbFullMovesWithNoCaptureOrPawn() {
    // Divide by 2 because fifty move rule is for full moves
    return this.getNbMovesWithNoCaptureOrPawn() / 2;
  }

  /**
   * Executes a given move on the board, handling captures, en passant, castling, pawn promotion.
   *
   * @param move The move to be executed
   */
  @Override
  public void makeMove(Move move) {

    this.setNbMovesWithNoCaptureOrPawn(this.getNbMovesWithNoCaptureOrPawn() + 1);
    if (this.getPieceAt(move.getSource().x(), move.getSource().y()).getPiece() == Piece.PAWN) {
      // Reset the number of moves with no pawn move
      this.setNbMovesWithNoCaptureOrPawn(0);
    }

    // TODO REFACTOR EN PASSANT DELETE
    if (move.isTake()) {
      // SAVE DELETED PIECE FOR HASHING
      if (!this.isEnPassantTake()) {
        this.deletePieceAt(move.getTakeDest().x(), move.getTakeDest().y());
      }
      // Reset the number of moves with no capture
      this.setNbMovesWithNoCaptureOrPawn(0);
    }

    if (this.isEnPassantTake()) {
      this.setLastMoveDoublePush(false);
      this.setEnPassantTake(false);
      if (this.getPlayer()) {
        this.deletePieceAt(move.getTakeDest().x(), move.getTakeDest().y());
      } else {
        this.deletePieceAt(move.getTakeDest().x(), move.getTakeDest().y());
      }
    }

    this.movePiece(move.getSource(), move.getDest());

    if (this.isWhiteLongCastle()
        && (move.getSource().equals(new Position(4, 0))
            || move.getSource().equals(new Position(0, 0)))) { // rook on a1 and king on e1
      this.setWhiteLongCastle(false);
    }
    if (this.isWhiteShortCastle()
        && (move.getSource().equals(new Position(4, 0))
            || move.getSource().equals(new Position(7, 0)))) { // rook on h1 and king on e1
      this.setWhiteShortCastle(false);
    }

    if (this.isBlackShortCastle()
        && (move.getSource().equals(new Position(4, 7))
            || move.getSource().equals(new Position(7, 7)))) { // rook on h8 and king on e8
      this.setBlackShortCastle(false);
    }
    if (this.isBlackLongCastle()
        && (move.getSource().equals(new Position(4, 7))
            || move.getSource().equals(new Position(0, 7)))) { // rook on a8 and king on e8
      this.setBlackLongCastle(false);
    }
    if (this.isPawnPromoting(move.getDest().x(), move.getDest().y(), this.getPlayer())) {
      Piece newPiece = ((PromoteMove) move).getPromPiece();
      this.promotePawn(
          move.getDest().x(),
          move.getDest().y(),
          this.getPlayer(),
          newPiece); // replace Piece.QUEEN by newPiece
    }

    if (isLastMoveDoublePush()) {
      this.setLastMoveDoublePush(false);
    }

    if (this.isEnPassantTake()) {
      this.setLastMoveDoublePush(false);
    }
  }

  /**
   * Generates an ASCII representation of the chess board.
   *
   * <p>White pieces are represented by uppercase characters and black pieces by lowercase
   * characters. A1 is the bottom-left corner of the board ([7][0]).
   *
   * @return a 2D array of characters representing the chess board.
   */
  @Override
  public char[][] getAsciiRepresentation() {
    int rows = this.getNbRows();
    int cols = this.getNbCols();
    char[][] charBoard = new char[rows][cols];

    for (int i = 0; i < rows; i++) {
      Arrays.fill(charBoard[i], Piece.EMPTY.getCharRepresentation(true));
    }

    for (int i = 0; i < 2; i++) {
      boolean color = i == 0;

      placePiecesOnBoard(charBoard, this.getPawns(color), Piece.PAWN.getCharRepresentation(color));
      placePiecesOnBoard(charBoard, this.getRooks(color), Piece.ROOK.getCharRepresentation(color));
      placePiecesOnBoard(
          charBoard, this.getKnights(color), Piece.KNIGHT.getCharRepresentation(color));
      placePiecesOnBoard(
          charBoard, this.getBishops(color), Piece.BISHOP.getCharRepresentation(color));
      placePiecesOnBoard(
          charBoard, this.getQueens(color), Piece.QUEEN.getCharRepresentation(color));
      placePiecesOnBoard(charBoard, this.getKing(color), Piece.KING.getCharRepresentation(color));
    }

    return charBoard;
  }

  /**
   * Places the pieces on the given board at the given positions. The y-coordinate of the position
   * is inverted to match the 0-indexed array representation of the board (bottom to top).
   *
   * @param board the current ASCII representation of the board
   * @param positions the positions where the pieces should be placed
   * @param rep the character to use to represent the pieces
   */
  private void placePiecesOnBoard(char[][] board, List<Position> positions, char rep) {
    for (Position pos : positions) {
      board[this.getNbRows() - 1 - pos.y()][pos.x()] = rep;
    }
  }

  /**
   * Applies long or short castle to {color} according to the boolean value given in parameter.
   * Assumes castle is possible
   *
   * @param color color for which castling move is applied
   * @param isShortCastle If the castle to apply is short (true) or long (false)
   */
  @Override
  public void applyCastle(Color color, boolean isShortCastle) {
    if (isShortCastle) {
      this.applyShortCastle(color);
    } else {
      this.applyLongCastle(color);
    }
  }

  /**
   * Get the castling rights of the board.
   *
   * @return An array that contains castling rights
   */
  @Override
  public boolean[] getCastlingRights() {
    return new boolean[] {
      isWhiteShortCastle(), isWhiteLongCastle(), isBlackShortCastle(), isBlackLongCastle()
    };
  }
}
