package pdp.model;

import static java.util.Map.entry;
import static pdp.utils.Logging.DEBUG;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import pdp.utils.Logging;
import pdp.utils.Position;

public class BitboardRepresentation implements BoardRepresentation {
  private static final Logger LOGGER = Logger.getLogger(BitboardRepresentation.class.getName());
  private Bitboard[] board;
  private int nbCols = 8;
  private int nbRows = 8;
  Map<Integer, ColoredPiece> pieces =
      Map.ofEntries(
          entry(0, new ColoredPiece(Piece.KING, Color.WHITE)),
          entry(1, new ColoredPiece(Piece.QUEEN, Color.WHITE)),
          entry(2, new ColoredPiece(Piece.BISHOP, Color.WHITE)),
          entry(3, new ColoredPiece(Piece.ROOK, Color.WHITE)),
          entry(4, new ColoredPiece(Piece.KNIGHT, Color.WHITE)),
          entry(5, new ColoredPiece(Piece.PAWN, Color.WHITE)),
          entry(6, new ColoredPiece(Piece.KING, Color.BLACK)),
          entry(7, new ColoredPiece(Piece.QUEEN, Color.BLACK)),
          entry(8, new ColoredPiece(Piece.BISHOP, Color.BLACK)),
          entry(9, new ColoredPiece(Piece.ROOK, Color.BLACK)),
          entry(10, new ColoredPiece(Piece.KNIGHT, Color.BLACK)),
          entry(11, new ColoredPiece(Piece.PAWN, Color.BLACK)));

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

  /** Initialize all the bitboards to the default values for a board when the game begin */
  public BitboardRepresentation() {
    Logging.configureLogging(LOGGER);
    board = new Bitboard[12];
    board[0] = new Bitboard(16L); // WKi
    board[1] = new Bitboard(8L); // WQ
    board[2] = new Bitboard(36L); // WB
    board[3] = new Bitboard(129L); // WR
    board[4] = new Bitboard(66L); // WKn
    board[5] = new Bitboard(65280L); // WP
    board[6] = new Bitboard(1152921504606846976L); // BKi
    board[7] = new Bitboard(576460752303423488L); // BQ
    board[8] = new Bitboard(2594073385365405696L); // BB
    board[9] = new Bitboard(); // BR // TODO: Find why overflow ???
    board[9].setBit(56);
    board[9].setBit(63);
    board[10] = new Bitboard(4755801206503243776L); // BKi
    board[11] = new Bitboard(71776119061217280L);
  }

  @Deprecated
  public BitboardRepresentation(
      Bitboard whiteKing,
      Bitboard whiteQueen,
      Bitboard whiteBishops,
      Bitboard whiteRooks,
      Bitboard whiteKnights,
      Bitboard whitePawns,
      Bitboard blackKing,
      Bitboard blackQueen,
      Bitboard blackBishops,
      Bitboard blackRooks,
      Bitboard blackKnights,
      Bitboard blackPawns) {
    Logging.configureLogging(LOGGER);
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
   * Translate a list of squares (0..63) to a list of position (x,y)
   *
   * @param squares The list of squares to change to position
   * @return A new list containing the translations
   */
  private List<Position> squaresToPosition(List<Integer> squares) {
    List<Position> positions = new ArrayList<>();
    for (Integer i : squares) {
      positions.add(new Position(i / 8, i % 8));
    }
    return positions;
  }

  /**
   * Translate a squares (0..63) to a position (x,y)
   *
   * @param square The square to change to position
   * @return A Position containing the translations
   */
  private Position squareToPosition(int square) {
    return new Position(square / 8, square % 8);
  }

  /**
   * Get the bitboard that contains all the white pieces, by or on all white pieces bitboards
   *
   * @return the bitboard containing all white pieces
   */
  private Bitboard getWhiteBoard() {
    return board[0].or(board[1]).or(board[2]).or(board[3]).or(board[4]).or(board[5]);
  }

  /**
   * Get the bitboard that contains all the black pieces, by or on all black pieces bitboards
   *
   * @return the bitboard containing all black pieces
   */
  private Bitboard getBlackBoard() {
    return board[6].or(board[7]).or(board[8]).or(board[9]).or(board[10]).or(board[11]);
  }

  /**
   * Get the positions of all bits set to 1 in the given bitboard
   *
   * @param bitBoardIndex The bitboard to lookUp
   * @return A list of positions
   */
  private List<Position> getOccupiedSquares(int bitBoardIndex) {
    return squaresToPosition(board[bitBoardIndex].getSetBits());
  }

  /**
   * Get the positions of the pawns
   *
   * @param white if true -> white pawns, if false -> black pawns
   * @return A list of the pawns positions for the given color
   */
  @Override
  public List<Position> getPawns(boolean white) {
    int bitmapIndex = white ? 5 : 11;
    return getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the positions of the rooks
   *
   * @param white if true -> white rooks, if false -> black rooks
   * @return A list of the rooks positions for the given color
   */
  @Override
  public List<Position> getRooks(boolean white) {
    int bitmapIndex = white ? 3 : 9;
    return getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the positions of the bishops
   *
   * @param white if true -> white bishops, if false -> black bishops
   * @return A list of the bishops positions for the given color
   */
  @Override
  public List<Position> getBishops(boolean white) {
    int bitmapIndex = white ? 2 : 8;
    return getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the positions of the knights
   *
   * @param white if true -> white knights, if false -> black knights
   * @return A list of the knights positions for the given color
   */
  @Override
  public List<Position> getKnights(boolean white) {
    int bitmapIndex = white ? 4 : 10;
    return getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the positions of the queens
   *
   * @param white if true -> white queens, if false -> black queens
   * @return A list of the queens positions for the given color
   */
  @Override
  public List<Position> getQueens(boolean white) {
    int bitmapIndex = white ? 1 : 7;
    return getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the positions of the king
   *
   * @param white if true -> white king, if false -> black king
   * @return A list containing the king position for the given color
   */
  @Override
  public List<Position> getKing(boolean white) {
    int bitmapIndex = white ? 0 : 6;
    return getOccupiedSquares(bitmapIndex);
  }

  /**
   * Get the piece and its color for the given square
   *
   * @param x column
   * @param y row
   * @return Piece and its Color
   */
  @Override
  public ColoredPiece getPieceAt(int x, int y) {
    int square = x + 8 * y;
    for (int index = 0; index < board.length; index++) {
      if (board[index].getBit(square)) return pieces.get(index);
    }
    return new ColoredPiece(Piece.EMPTY, Color.EMPTY);
  }

  /**
   * @return The horizontal size of the board
   */
  public int getNbCols() {
    return nbCols;
  }

  /**
   * @return The vertical size of the board
   */
  public int getNbRows() {
    return nbRows;
  }

  /**
   * Move a piece from a source position to a destination position Can throw an exception if there
   * isn't a piece at source position
   *
   * @param from The initial position of the piece
   * @param to The position to reach
   */
  @Override
  public void movePiece(Position from, Position to) {
    ColoredPiece piece = getPieceAt(from.getX(), from.getY());
    int fromIndex = from.getX() % 8 + from.getY() * 8;
    int toIndex = to.getX() % 8 + to.getY() * 8;
    int bitboardIndex =
        switch (piece.piece) {
          case KING -> piece.color == Color.WHITE ? 0 : 6;
          case QUEEN -> piece.color == Color.WHITE ? 1 : 7;
          case BISHOP -> piece.color == Color.WHITE ? 2 : 8;
          case ROOK -> piece.color == Color.WHITE ? 3 : 9;
          case KNIGHT -> piece.color == Color.WHITE ? 4 : 10;
          case PAWN -> piece.color == Color.WHITE ? 5 : 11;
          default -> throw new IllegalArgumentException("Invalid piece: " + piece.piece);
        };
    board[bitboardIndex].clearBit(fromIndex);
    board[bitboardIndex].setBit(toIndex);
  }

  /**
   * Iterate on the given direction to generate the possible movement from a given position
   * depending on allies and enemies
   *
   * @param piece Bitboard where only the position of the piece you want to move is set to 1
   * @param unreachableSquares A bitboard containing all the unreachable squares
   * @param enemies A bitboard containing all the enemies pieces
   * @param moveFunction The function that make the direction to follow (ex: right)
   * @return A bitboard containing all the squares reachable for a given direction
   */
  private Bitboard getMultipleMovesFromDirection(
      Bitboard piece,
      Bitboard unreachableSquares,
      Bitboard enemies,
      Function<Bitboard, Bitboard> moveFunction) {
    Bitboard allowedMoves = new Bitboard(piece.bitboard);
    int bitCount = enemies.bitCount();
    do {
      allowedMoves = allowedMoves.or(moveFunction.apply(allowedMoves));
      allowedMoves = allowedMoves.xor(allowedMoves.and(unreachableSquares));
      bitCount++;
    } while (allowedMoves.or(enemies).bitCount() > bitCount);
    return allowedMoves;
  }

  /**
   * Generate the bitboard containing the reachable positions for up, down, left, right directions
   *
   * @param square The position of the piece that want to move
   * @param unreachableSquares A bitboard containing all the unreachable squares
   * @param enemies A bitboard containing all the enemies pieces
   * @return A bitboard containing the possible inline moves
   */
  private Bitboard getInlineMoves(Position square, Bitboard unreachableSquares, Bitboard enemies) {
    Bitboard position = new Bitboard();
    int squareIndex = square.getX() % 8 + square.getY() * 8;
    position.setBit(squareIndex);
    // move left
    Bitboard leftMoves =
        getMultipleMovesFromDirection(position, unreachableSquares, enemies, Bitboard::moveLeft);
    // move right
    Bitboard rightMoves =
        getMultipleMovesFromDirection(position, unreachableSquares, enemies, Bitboard::moveRight);
    // move up
    Bitboard upMoves =
        getMultipleMovesFromDirection(position, unreachableSquares, enemies, Bitboard::moveUp);
    // move down
    Bitboard downMoves =
        getMultipleMovesFromDirection(position, unreachableSquares, enemies, Bitboard::moveDown);

    Bitboard res = new Bitboard().or(leftMoves).or(rightMoves).or(upMoves).or(downMoves);
    res.clearBit(squareIndex);
    return res;
  }

  /**
   * Generate the bitboard containing the reachable positions for up left, down left, up right, down
   * right directions
   *
   * @param square The position of the piece that want to move
   * @param unreachableSquares A bitboard containing all the unreachable squares
   * @param enemies A bitboard containing all the enemies pieces
   * @return A bitboard containing the possible diagonal moves
   */
  private Bitboard getDiagonalMoves(
      Position square, Bitboard unreachableSquares, Bitboard enemies) {
    Bitboard position = new Bitboard();
    int squareIndex = square.getX() % 8 + square.getY() * 8;
    position.setBit(squareIndex);

    Bitboard upLeftMoves =
        getMultipleMovesFromDirection(position, unreachableSquares, enemies, Bitboard::moveUpLeft);
    Bitboard upRightMoves =
        getMultipleMovesFromDirection(position, unreachableSquares, enemies, Bitboard::moveUpRight);
    Bitboard downLeftMoves =
        getMultipleMovesFromDirection(
            position, unreachableSquares, enemies, Bitboard::moveDownLeft);
    Bitboard downRightMoves =
        getMultipleMovesFromDirection(
            position, unreachableSquares, enemies, Bitboard::moveDownRight);
    Bitboard res =
        new Bitboard().or(upLeftMoves).or(upRightMoves).or(downRightMoves).or(downLeftMoves);
    res.clearBit(squareIndex);
    return res;
  }

  /**
   * Convert a move bitboard to a list of possible moves. It also set if a move is a capture or not
   *
   * @param moveBitboard The bitboard to transform
   * @param enemies A bitboard containing the enemies
   * @param source The initial position of the piece
   * @return A list of possible moves from a move bitboard
   */
  private List<Move> bitboardToMoves(
      Bitboard moveBitboard, Bitboard enemies, Position source, Piece piece) {
    List<Move> moves = new ArrayList<>();
    for (Integer i : moveBitboard.getSetBits()) {
      moves.add(
          new Move(
              source,
              squareToPosition(i),
              piece,
              enemies.getBit(i))); // enemies.getBit(i) ? true : false -> capture ?
    }
    return moves;
  }

  /**
   * Generate the list of possible moves from a given position for a king piece
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @param enemies Enemies occupation bitboard
   * @return The list of possible moves
   */
  private List<Move> getKingMoves(Position square, Bitboard unreachableSquares, Bitboard enemies) {
    Bitboard position = new Bitboard();
    int squareIndex = square.getX() % 8 + square.getY() * 8;
    position.setBit(squareIndex);
    Bitboard move =
        position
            .moveLeft()
            .or(position.moveRight())
            .or(position.moveUp())
            .or(position.moveDown())
            .or(position.moveUpLeft())
            .or(position.moveUpRight())
            .or(position.moveDownLeft())
            .or(position.moveDownRight());
    move = move.xor(move.and(unreachableSquares));
    return bitboardToMoves(move, enemies, square, Piece.KING);
  }

  /**
   * Generate the list of possible moves from a given position for a knight piece
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @param enemies Enemies occupation bitboard
   * @return The list of possible moves
   */
  private List<Move> getKnightMoves(
      Position square, Bitboard unreachableSquares, Bitboard enemies) {
    Bitboard position = new Bitboard();
    int squareIndex = square.getX() % 8 + square.getY() * 8;
    position.setBit(squareIndex);
    Bitboard move =
        position
            .moveUp()
            .moveUpRight()
            .or(position.moveUp().moveUpLeft())
            .or(position.moveUp().moveUpRight())
            .or(position.moveDown().moveDownLeft())
            .or(position.moveDown().moveDownRight())
            .or(position.moveLeft().moveUpLeft())
            .or(position.moveLeft().moveDownLeft())
            .or(position.moveRight().moveDownRight())
            .or(position.moveRight().moveUpRight());
    move = move.xor(move.and(unreachableSquares));
    return bitboardToMoves(move, enemies, square, Piece.KNIGHT);
  }

  /**
   * Generate the list of possible moves from a given position for a pawn piece
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @param enemies Enemies occupation bitboard
   * @return The list of possible moves
   */
  private List<Move> getPawnMoves(
      Position square, Bitboard unreachableSquares, Bitboard enemies, boolean white) {
    Bitboard position = new Bitboard();
    Bitboard attackRight, attackLeft;
    int squareIndex = square.getX() % 8 + square.getY() * 8;
    position.setBit(squareIndex);

    if (white) {
      attackRight = position.moveUpRight().and(enemies);
      attackLeft = position.moveUpLeft().and(enemies);
      position = position.moveUp();
    } else {
      attackRight = position.moveDownRight().and(enemies);
      attackLeft = position.moveDownLeft().and(enemies);
      position = position.moveDown();
    }
    position = position.xor(position.and(unreachableSquares));

    return bitboardToMoves(position.or(attackRight).or(attackLeft), enemies, square, Piece.PAWN);
  }

  /**
   * Generate the list of possible moves from a given position for a queen piece
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @param enemies Enemies occupation bitboard
   * @return The list of possible moves
   */
  private List<Move> getQueenMoves(Position square, Bitboard unreachableSquares, Bitboard enemies) {
    return bitboardToMoves(
        getInlineMoves(square, unreachableSquares, enemies)
            .or(getDiagonalMoves(square, unreachableSquares, enemies)),
        enemies,
        square,
        Piece.QUEEN);
  }

  /**
   * Generate the list of possible moves from a given position for a bishop piece
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @param enemies Enemies occupation bitboard
   * @return The list of possible moves
   */
  private List<Move> getBishopMoves(
      Position square, Bitboard unreachableSquares, Bitboard enemies) {
    return bitboardToMoves(
        getDiagonalMoves(square, unreachableSquares, enemies), enemies, square, Piece.BISHOP);
  }

  /**
   * Generate the list of possible moves from a given position for a rook piece
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @param enemies Enemies occupation bitboard
   * @return The list of possible moves
   */
  private List<Move> getRookMoves(Position square, Bitboard unreachableSquares, Bitboard enemies) {
    return bitboardToMoves(
        getInlineMoves(square, unreachableSquares, enemies), enemies, square, Piece.ROOK);
  }

  /**
   * Generate the possible moves from a position depending on the piece type. This function do not
   * apply special rules (castling, pinned, ...)
   *
   * @param x The board column
   * @param y The board row
   * @param kingReachable Can the piece reach opponent king (keep false if not checking
   *     check/checkmate)
   * @return The list of possible moves (without special cases)
   */
  @Override
  public List<Move> getAvailableMoves(int x, int y, boolean kingReachable) {
    ColoredPiece piece = getPieceAt(x, y);
    Position enemyKing = getKing(piece.color != Color.WHITE).get(0);
    Bitboard unreachableSquares = piece.color == Color.WHITE ? getWhiteBoard() : getBlackBoard();
    unreachableSquares.clearBit(x % 8 + y * 8); // remove piece position from reachable positions
    if (!kingReachable)
      unreachableSquares.setBit(
          enemyKing.getX() % 8 + enemyKing.getY() * 8); // Put enemyKing to unreachable positions
    Bitboard enemies = piece.color == Color.WHITE ? getBlackBoard() : getWhiteBoard();
    return switch (piece.piece) {
      case KING -> getKingMoves(new Position(y, x), unreachableSquares, enemies);
      case QUEEN -> getQueenMoves(new Position(y, x), unreachableSquares, enemies);
      case BISHOP -> getBishopMoves(new Position(y, x), unreachableSquares, enemies);
      case ROOK -> getRookMoves(new Position(y, x), unreachableSquares, enemies);
      case KNIGHT -> getKnightMoves(new Position(y, x), unreachableSquares, enemies);
      case PAWN ->
          piece.color == Color.WHITE
              ? getPawnMoves(new Position(y, x), unreachableSquares, enemies, true)
              : getPawnMoves(new Position(y, x), unreachableSquares, enemies, false);
      default -> new ArrayList<>();
    };
  }

  /**
   * Delete the piece contained at the given position
   *
   * @param x The board column
   * @param y The board row
   */
  public void deletePieceAt(int x, int y) {
    ColoredPiece piece = getPieceAt(x, y);
    for (Map.Entry<Integer, ColoredPiece> entry : pieces.entrySet()) {
      if (entry.getValue().equals(piece)) {
        board[entry.getKey()].clearBit(x % 8 + y * 8);
        DEBUG(LOGGER, "Piece at position " + x + " and position " + y + " was removed");
        return;
      }
    }
  }

  /**
   * Add a new piece in the bitboard corresponding to the given piece at the coordinates x,y. ⚠️
   * This method should be only used for undo/redo moves
   *
   * @param x The board column
   * @param y The board row
   * @param piece The type of piece to add
   */
  private void addPieceAt(int x, int y, ColoredPiece piece) {
    for (Map.Entry<Integer, ColoredPiece> entry : pieces.entrySet()) {
      if (entry.getValue().equals(piece)) {
        board[entry.getKey()].setBit(x % 8 + y * 8);
        DEBUG(LOGGER, "A " + piece.color + " " + piece.piece + " was added to the board");
        return;
      }
    }
  }

  /**
   * Get if the given square (x,y format) can be attacked by a piece of the given color
   *
   * @param x X coordinate of the Position
   * @param y Y coordinate of the Position
   * @param by The color of the attacker
   * @return True if the given square is attacked, False else
   */
  @Override
  public boolean isAttacked(int x, int y, Color by) {
    Bitboard pieces = by == Color.WHITE ? getWhiteBoard() : getBlackBoard();
    Position destination = new Position(y, x);
    for (Integer i : pieces.getSetBits()) {
      Position piecePosition = squareToPosition(i);
      List<Move> moves = getAvailableMoves(piecePosition.getX(), piecePosition.getY(), true);
      boolean oneAttack = moves.stream().anyMatch(move -> move.getDest().equals(destination));
      if (oneAttack) return true;
    }
    return false;
  }

  /**
   * Get the check state for the given color
   *
   * @param color The piece color you want to know check status
   * @return True if the given color is in check, False else
   */
  @Override
  public boolean isCheck(Color color) {
    Position kingPosition = getKing(color == Color.WHITE).get(0);
    return isAttacked(
        kingPosition.getX(), kingPosition.getY(), color == Color.WHITE ? Color.BLACK : Color.WHITE);
  }

  /**
   * Get the check state after move for the given color
   *
   * @param color The piece color you want to know check status
   * @param move The move you want to check if it puts the king in check
   * @return True if the given color is in check after the given move, False else
   */
  @Override
  public boolean isCheckAfterMove(Color color, Move move) {
    ColoredPiece removedPiece = null;
    if (move.isTake) {
      removedPiece = getPieceAt(move.getDest().getX(), move.getDest().getY());
      deletePieceAt(move.getDest().getX(), move.getDest().getY());
    }
    this.movePiece(move.source, move.dest); // Play move
    boolean isCheckAfterMove = isCheck(color);
    this.movePiece(move.dest, move.source); // undo move
    if (move.isTake) {
      addPieceAt(move.getDest().getX(), move.getDest().getY(), removedPiece);
    }
    if (isCheckAfterMove) {
      DEBUG(LOGGER, color.toString() + "will be checked after move");
    }
    return isCheckAfterMove;
  }

  /**
   * Get the checkMate state for the given color (⚠️ can be resources/time-consuming if there are
   * many pieces remaining on the board)
   *
   * @param color The piece color you want to know checkMate status
   * @return True if the given color is in checkMate, False else
   */
  @Override
  public boolean isCheckMate(Color color) {
    if (!isCheck(color)) return false;
    Bitboard pieces = color == Color.WHITE ? getWhiteBoard() : getBlackBoard();
    for (Integer i : pieces.getSetBits()) {
      Position piecePosition = squareToPosition(i);
      List<Move> availableMoves =
          getAvailableMoves(
              piecePosition.getX(), piecePosition.getY(), false); // TODO: Check this line
      for (Move move : availableMoves) {
        ColoredPiece removedPiece = null;
        if (move.isTake) {
          removedPiece = getPieceAt(move.getDest().getX(), move.getDest().getY());
          deletePieceAt(move.getDest().getX(), move.getDest().getY());
        }
        movePiece(move.source, move.dest); // Play move
        boolean isStillCheck = isCheck(color);
        movePiece(move.dest, move.source); // Undo move
        if (move.isTake) {
          addPieceAt(move.getDest().getX(), move.getDest().getY(), removedPiece);
        }
        if (!isStillCheck) {
          DEBUG(LOGGER, color.toString() + " is not check mate");
          return false;
        }
      }
    }
    DEBUG(LOGGER, color.toString() + " is check mate ");
    return true;
  }

  /**
   * @Override Checks the StaleMate state for the given color
   *
   * @param color The color you want to check StaleMate for
   * @param colorTurnToPlay Player's turn to know if player who potentially moves in check has to
   *     move
   * @return true if color {color} is stalemated. false otherwise.
   */
  @Override
  public boolean isStaleMate(Color color, Color colorTurnToPlay) {
    if (isCheck(color)) {
      return false;
    }
    Bitboard pieces = color == Color.WHITE ? getWhiteBoard() : getBlackBoard();
    for (Integer i : pieces.getSetBits()) {
      Position piecePosition = squareToPosition(i);
      List<Move> availableMoves =
          getAvailableMoves(piecePosition.getX(), piecePosition.getY(), true);
      for (Move move : availableMoves) {
        movePiece(move.source, move.dest); // Play move
        boolean isStillCheck = isCheck(color);
        movePiece(move.dest, move.source); // Undo move
        if (!isStillCheck) return false;
      }
    }
    // Stalemate only if it is someone's turn to play and that someone has no move
    // If "stalemate" but it is other player's turn to play, then can play a move to prevent
    // stalemate
    return color == colorTurnToPlay;
  }

  /**
   * Checks if draw by insufficient material is observed (both colors each case) Cases: King vs King
   * King and Bishop vs King King and Knight vs King King and Bishop vs King and Bishop (same
   * colored Bishops)
   *
   * @return true if a draw by insufficient material is observed
   */
  @Override
  public boolean isDrawByInsufficientMaterial() {
    List<Position> posWhiteKing = getKing(true);
    List<Position> posBlackKing = getKing(false);
    if (posWhiteKing.isEmpty() || posBlackKing.isEmpty()) {
      return false;
    }

    // If at least a queen or a rook or a pawn is found on the board then no draw by insufficient
    // material
    List<List<Position>> posListFalseInAllCases = new ArrayList<>();
    posListFalseInAllCases.add(getQueens(true));
    posListFalseInAllCases.add(getRooks(true));
    posListFalseInAllCases.add(getPawns(true));
    posListFalseInAllCases.add(getQueens(false));
    posListFalseInAllCases.add(getRooks(false));
    posListFalseInAllCases.add(getPawns(false));

    for (List<Position> pieceList : posListFalseInAllCases) {
      if (!pieceList.isEmpty()) {
        return false;
      }
    }

    // Get all remaining pieces
    List<Position> posWhiteBishops = getBishops(true);
    List<Position> posBlackBishops = getBishops(false);
    List<Position> posWhiteKnights = getKnights(true);
    List<Position> posBlackKnights = getKnights(false);

    // King vs King
    if (posWhiteBishops.isEmpty()
        && posBlackBishops.isEmpty()
        && posWhiteKnights.isEmpty()
        && posBlackKnights.isEmpty()) {
      return true;
    }

    // King and Bishop vs King
    if ((posWhiteBishops.size() == 1
            && posBlackBishops.isEmpty()
            && posWhiteKnights.isEmpty()
            && posBlackKnights.isEmpty())
        || (posBlackBishops.size() == 1
            && posWhiteBishops.isEmpty()
            && posWhiteKnights.isEmpty()
            && posBlackKnights.isEmpty())) {
      return true;
    }

    // King and Knight vs King
    if ((posWhiteKnights.size() == 1
            && posBlackBishops.isEmpty()
            && posWhiteBishops.isEmpty()
            && posBlackKnights.isEmpty())
        || (posBlackKnights.size() == 1
            && posBlackBishops.isEmpty()
            && posWhiteBishops.isEmpty()
            && posWhiteKnights.isEmpty())) {
      return true;
    }

    // King and Bishop vs King and Bishop (same-colored bishops)
    if (posWhiteBishops.size() == 1 && posBlackBishops.size() == 1) {
      Position whiteBishop = posWhiteBishops.get(0);
      Position blackBishop = posBlackBishops.get(0);
      // Check if bishops are on the same color square to know if same color
      if ((whiteBishop.getX() + whiteBishop.getY()) % 2
          == (blackBishop.getX() + blackBishop.getY()) % 2) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks if a pawn at Position(x,y) checks for promotion
   *
   * @param x The x-coordinate (file) of the pawn
   * @param y The y-coordinate (rank) of the pawn
   * @param white {true} if pawn is white, {false} if pawn is black
   * @return true if the pawn is being promoted, otherwise false
   */
  @Override
  public boolean isPawnPromoting(int x, int y, boolean white) {
    if (white && y != 7) {
      return false;
    } else if (!white && y != 0) {
      return false;
    } else {
      // White pawns --> 5 and Black pawns --> 11
      Bitboard pawnBitBoard = white ? this.board[5] : this.board[11];
      int bitIndex = 8 * y + x;

      // If bit is 1 then pawn is located at Position(x,y)
      return pawnBitBoard.getBit(bitIndex);
    }
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
  public void promotePawn(int x, int y, boolean white, Piece newPiece) {
    ColoredPiece pieceAtPosition = getPieceAt(x, y);
    if (pieceAtPosition.piece != Piece.PAWN
        || pieceAtPosition.color != (white ? Color.WHITE : Color.BLACK)) {
      return;
    }

    int boardIndex = white ? 0 : 6;
    Bitboard newPieceBitBoard = null;
    Bitboard pawnBitboard = this.board[5 + boardIndex];
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
        System.err.println("Error: A pawn can only be promoted to Queen, Rook, Knight or Bishop !");
        return;
    }

    int bitIndex = 8 * y + x;
    // Change bits
    pawnBitboard.clearBit(bitIndex);
    newPieceBitBoard.setBit(bitIndex);
  }

  /**
   * Checks if a given move is a double pawn push A double push occurs when a pawn moves forward by
   * two squares from its starting position
   *
   * @param move The move to check
   * @param white {true} if pawn is white, {false} if pawn is black
   * @return True if the move is a valid double pawn push, false else
   */
  @Override
  public boolean isDoublePushPossible(Move move, boolean white) {
    ColoredPiece piece = getPieceAt(move.source.getX(), move.source.getY());
    if (white
        && piece.piece == Piece.PAWN
        && move.source.getY() == 1
        && move.dest.getY() == 3
        && move.source.getX() == move.dest.getX()) {
      return ((getPieceAt(move.dest.getX(), move.dest.getY()).piece == Piece.EMPTY)
          && (getPieceAt(move.dest.getX(), move.dest.getY() - 1).piece == Piece.EMPTY));
    }

    if (!white
        && piece.piece == Piece.PAWN
        && move.source.getY() == 6
        && move.dest.getY() == 4
        && move.source.getX() == move.dest.getX()) {
      return ((getPieceAt(move.dest.getX(), move.dest.getY()).piece == Piece.EMPTY)
          && (getPieceAt(move.dest.getX(), move.dest.getY() + 1).piece == Piece.EMPTY));
    }
    return false;
  }

  /**
   * Checks if a given move is an en passant
   *
   * @param x The x-coordinate of the square where an en passant capture can occur
   * @param y The y-coordinate of the square where an en passant capture can occur
   * @param move The move being checked
   * @param white {true} if pawn is white, {false} if pawn is black
   * @return True if the move is a valid en passant capture, false else
   */
  public boolean isEnPassant(int x, int y, Move move, boolean white) {
    ColoredPiece piece = getPieceAt(move.source.getX(), move.source.getY());
    if (white
        && piece.piece == Piece.PAWN
        && (move.dest.getX() == (x) && move.dest.getY() == (y))
        && ((move.source.getX() == (x - 1) && move.source.getY() == (y - 1))
            || (move.source.getX() == (x + 1) && move.source.getY() == (y - 1)))) {
      return true;
    }
    if (!white
        && piece.piece == Piece.PAWN
        && (move.dest.getX() == (x) && move.dest.getY() == (y))
        && ((move.source.getX() == (x + 1) && move.source.getY() == (y + 1))
            || (move.source.getX() == (x - 1) && move.source.getY() == (y + 1)))) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return getWhiteBoard().or(getBlackBoard()).toString();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof BitboardRepresentation obj) {
      if (board.length != obj.board.length) return false;
      for (int i = 0; i < board.length; i++) {
        if (!board[i].equals(obj.board[i])) return false;
      }
      return true;
    }
    return false;
  }
}
