package pdp.model.board;

import static pdp.utils.Logging.debug;
import static pdp.utils.Logging.verbose;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Logging;
import pdp.utils.Position;

/** Utility class to remove complexity for BitboardRepresentation. */
public final class BitboardMovesGen {
  private static final Logger LOGGER = Logger.getLogger(BitboardMovesGen.class.getName());

  /** Private constructor to avoid instantiation. */
  private BitboardMovesGen() {
    throw new UnsupportedOperationException("Cannot instantiate utility class");
  }

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Iterate on the given direction to generate the possible movement from a given position
   * depending on allies and enemies.
   *
   * @param piece Bitboard where only the position of the piece you want to move is set to 1
   * @param unreachableSquares A bitboard containing all the unreachable squares
   * @param enemies A bitboard containing all the enemies pieces
   * @param moveFunction The function that make the direction to follow (ex: right)
   * @return A bitboard containing all the squares reachable for a given direction
   */
  public static Bitboard getMultipleMovesFromDirection(
      Bitboard piece,
      Bitboard unreachableSquares,
      Bitboard enemies,
      Function<Bitboard, Bitboard> moveFunction) {
    Bitboard allowedMoves = new Bitboard(piece.getBits());
    int bitCount = enemies.bitCount();
    do {
      allowedMoves = allowedMoves.or(moveFunction.apply(allowedMoves));
      allowedMoves = allowedMoves.xor(allowedMoves.and(unreachableSquares));
      bitCount++;
    } while (allowedMoves.or(enemies).bitCount() > bitCount);
    return allowedMoves;
  }

  /**
   * Generate the bitboard containing the reachable positions for up, down, left, right directions.
   *
   * @param square The position of the piece that want to move
   * @param unreachableSquares A bitboard containing all the unreachable squares
   * @param enemies A bitboard containing all the enemies pieces
   * @return A bitboard containing the possible inline moves
   */
  public static Bitboard getInlineMoves(
      Position square, Bitboard unreachableSquares, Bitboard enemies) {
    Bitboard position = new Bitboard();
    int squareIndex = square.x() % 8 + square.y() * 8;
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
   * right directions.
   *
   * @param square The position of the piece that want to move
   * @param unreachableSquares A bitboard containing all the unreachable squares
   * @param enemies A bitboard containing all the enemies pieces
   * @return A bitboard containing the possible diagonal moves
   */
  public static Bitboard getDiagonalMoves(
      Position square, Bitboard unreachableSquares, Bitboard enemies) {
    Bitboard position = new Bitboard();
    int squareIndex = square.x() % 8 + square.y() * 8;
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
   * Convert a move bitboard to a list of possible moves. It also set if a move is a capture or not.
   *
   * @param moveBitboard The bitboard to transform
   * @param enemies A bitboard containing the enemies
   * @param source The initial position of the piece
   * @return A list of possible moves from a move bitboard
   */
  public static List<Move> bitboardToMoves(
      Bitboard moveBitboard,
      Bitboard enemies,
      Position source,
      ColoredPiece piece,
      BitboardRepresentation bitboardRepresentation) {
    List<Move> moves = new ArrayList<>();
    for (Integer i : moveBitboard.getSetBits()) {

      boolean isTake = false;
      ColoredPiece capturedPiece = null;
      boolean isPromotion = false;

      if (enemies.getBit(i)) { // move is capture
        for (int j = 0; j < bitboardRepresentation.getBitboards().length; j++) {
          if (bitboardRepresentation.getBitboards()[j].getBit(i)) {
            isTake = true;
            capturedPiece = BitboardRepresentation.getPiecesMap().getFromKey(j);
            moves.add(
                new Move(
                    source,
                    bitboardRepresentation.squareToPosition(i),
                    piece,
                    true,
                    BitboardRepresentation.getPiecesMap().getFromKey(j)));
            break;
          }
        }

        if (piece.getPiece() == Piece.PAWN) {
          if (piece.getColor() == Color.WHITE && i >= 7 * 8) {
            isPromotion = true;
          }
          if (piece.getColor() == Color.BLACK && i <= 7) {
            isPromotion = true;
          }
        }

        if (isPromotion) {
          moves.add(
              new PromoteMove(
                  source,
                  bitboardRepresentation.squareToPosition(i),
                  Piece.QUEEN,
                  piece,
                  isTake,
                  capturedPiece));
          moves.add(
              new PromoteMove(
                  source,
                  bitboardRepresentation.squareToPosition(i),
                  Piece.KNIGHT,
                  piece,
                  isTake,
                  capturedPiece));
          moves.add(
              new PromoteMove(
                  source,
                  bitboardRepresentation.squareToPosition(i),
                  Piece.ROOK,
                  piece,
                  isTake,
                  capturedPiece));
          moves.add(
              new PromoteMove(
                  source,
                  bitboardRepresentation.squareToPosition(i),
                  Piece.BISHOP,
                  piece,
                  isTake,
                  capturedPiece));
        }
      } else {
        moves.add(
            new Move(
                source, bitboardRepresentation.squareToPosition(i), piece, isTake, capturedPiece));
      }

      // TODO: save the captured piece
      // enemies.getBit(i) ? true : false -> capture ?
    }
    return moves;
  }

  /**
   * Generate the list of possible moves from a given position for a king piece.
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @param enemies Enemies occupation bitboard
   * @return The list of possible moves
   */
  public static List<Move> getKingMoves(
      Position square,
      Bitboard unreachableSquares,
      Bitboard enemies,
      ColoredPiece piece,
      BitboardRepresentation bitboardRepresentation,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle) {
    List<Move> moves =
        bitboardToMoves(
            getKingMoveBitboard(
                square,
                unreachableSquares,
                enemies,
                piece,
                bitboardRepresentation,
                enPassantPos,
                isLastMoveDoublePush,
                isWhiteLongCastle,
                isWhiteShortCastle,
                isBlackLongCastle,
                isBlackShortCastle),
            enemies,
            square,
            piece,
            bitboardRepresentation);
    return moves;
  }

  /**
   * Generate a bitboard with possible "attack" moves from a given position for a king piece (not
   * using castling).
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @param enemies Enemies occupation bitboard
   * @return The list of possible moves
   */
  public static Bitboard getKingAttackBitboard(
      Position square,
      Bitboard unreachableSquares,
      Bitboard enemies,
      ColoredPiece piece,
      BitboardRepresentation bitboardRepresentation) {
    Bitboard position = new Bitboard();
    int squareIndex = square.x() % 8 + square.y() * 8;
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

    return move;
  }

  /**
   * Generate a bitboard with possible moves from a given position for a king piece.
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @param enemies Enemies occupation bitboard
   * @return The list of possible moves
   */
  public static Bitboard getKingMoveBitboard(
      Position square,
      Bitboard unreachableSquares,
      Bitboard enemies,
      ColoredPiece piece,
      BitboardRepresentation bitboardRepresentation,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle) {

    Bitboard move =
        getKingAttackBitboard(square, unreachableSquares, enemies, piece, bitboardRepresentation);

    if (isWhiteLongCastle && piece.getColor() == Color.WHITE) {
      if (!bitboardRepresentation.isAttacked(2, 0, Color.BLACK)
          && !bitboardRepresentation.isAttacked(3, 0, Color.BLACK)
          && !bitboardRepresentation.isAttacked(4, 0, Color.BLACK)
          && bitboardRepresentation
              .getPieceAt(1, 0)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))
          && bitboardRepresentation
              .getPieceAt(2, 0)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))
          && bitboardRepresentation
              .getPieceAt(3, 0)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))) {
        move.setBit(2 + 0 * 8);
      }
    }

    if (isWhiteShortCastle && piece.getColor() == Color.WHITE) {
      if (!bitboardRepresentation.isAttacked(5, 0, Color.BLACK)
          && !bitboardRepresentation.isAttacked(6, 0, Color.BLACK)
          && !bitboardRepresentation.isAttacked(4, 0, Color.BLACK)
          && bitboardRepresentation
              .getPieceAt(5, 0)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))
          && bitboardRepresentation
              .getPieceAt(6, 0)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))) {
        move.setBit(6 + 0 * 8);
      }
    }

    if (isBlackLongCastle && piece.getColor() == Color.BLACK) {
      if (!bitboardRepresentation.isAttacked(2, 7, Color.WHITE)
          && !bitboardRepresentation.isAttacked(3, 7, Color.WHITE)
          && !bitboardRepresentation.isAttacked(4, 7, Color.WHITE)
          && bitboardRepresentation
              .getPieceAt(1, 7)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))
          && bitboardRepresentation
              .getPieceAt(2, 7)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))
          && bitboardRepresentation
              .getPieceAt(3, 7)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))) {
        move.setBit(2 + 7 * 8);
      }
    }

    if (isBlackShortCastle && piece.getColor() == Color.BLACK) {
      if (!bitboardRepresentation.isAttacked(5, 7, Color.WHITE)
          && !bitboardRepresentation.isAttacked(6, 7, Color.WHITE)
          && !bitboardRepresentation.isAttacked(4, 7, Color.WHITE)
          && bitboardRepresentation
              .getPieceAt(5, 7)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))
          && bitboardRepresentation
              .getPieceAt(6, 7)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))) {
        move.setBit(6 + 7 * 8);
      }
    }

    return move;
  }

  /**
   * Generate the list of possible moves from a given position for a knight piece.
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @param enemies Enemies occupation bitboard
   * @return The list of possible moves
   */
  public static List<Move> getKnightMoves(
      Position square,
      Bitboard unreachableSquares,
      Bitboard enemies,
      ColoredPiece piece,
      BitboardRepresentation bitboardRepresentation) {
    return bitboardToMoves(
        getKnightMoveBitboard(square, unreachableSquares),
        enemies,
        square,
        piece,
        bitboardRepresentation);
  }

  /**
   * Generate the move bitboard from a given position for a knight piece.
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @return The list of possible moves
   */
  public static Bitboard getKnightMoveBitboard(Position square, Bitboard unreachableSquares) {
    Bitboard position = new Bitboard();
    int squareIndex = square.x() % 8 + square.y() * 8;
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
    return move;
  }

  /**
   * Generate the list of possible moves from a given position for a pawn piece.
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @param enemies Enemies occupation bitboard
   * @return The list of possible moves
   */
  public static List<Move> getPawnMoves(
      Position square,
      Bitboard unreachableSquares,
      Bitboard enemies,
      boolean white,
      BitboardRepresentation bitboardRepresentation,
      Position enPassantPos,
      boolean isLastMoveDoublePush) {
    return bitboardToMoves(
        getPawnMoveBitboard(
            square, unreachableSquares, enemies, white, enPassantPos, isLastMoveDoublePush),
        enemies,
        square,
        new ColoredPiece(Piece.PAWN, white ? Color.WHITE : Color.BLACK),
        bitboardRepresentation);
  }

  /**
   * Generate the move bitboard from a given position for a pawn piece.
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @param white The piece color
   * @return The list of possible moves
   */
  public static Bitboard getPawnMoveBitboard(
      Position square,
      Bitboard unreachableSquares,
      Bitboard enemies,
      boolean white,
      Position enPassantPos,
      boolean isLastMoveDoublePush) {

    Bitboard position = new Bitboard();
    Bitboard attackRight;
    Bitboard attackLeft;
    Bitboard enPassantCapture = new Bitboard();
    int squareIndex = square.x() % 8 + square.y() * 8;
    position.setBit(squareIndex);

    if (white) {
      attackRight = position.moveUpRight().and(enemies);
      attackLeft = position.moveUpLeft().and(enemies);
      position = position.moveUp().and(enemies.not());

      if (isLastMoveDoublePush && enPassantPos != null && square.y() == 4) {
        if (square.x() == enPassantPos.x() - 1 || square.x() == enPassantPos.x() + 1) {
          enPassantCapture.setBit(enPassantPos.x() % 8 + (square.y() + 1) * 8);
        }
      }
    } else {
      attackRight = position.moveDownRight().and(enemies);
      attackLeft = position.moveDownLeft().and(enemies);
      position = position.moveDown().and(enemies.not());

      if (isLastMoveDoublePush && enPassantPos != null && square.y() == 3) {
        if (square.x() == enPassantPos.x() - 1 || square.x() == enPassantPos.x() + 1) {
          enPassantCapture.setBit(enPassantPos.x() % 8 + (square.y() - 1) * 8);
        }
      }
    }

    position = position.xor(position.and(unreachableSquares));
    return position.or(attackRight).or(attackLeft).or(enPassantCapture);
  }

  /**
   * Generate the list of possible moves from a given position for a queen piece.
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @param enemies Enemies occupation bitboard
   * @return The list of possible moves
   */
  public static List<Move> getQueenMoves(
      Position square,
      Bitboard unreachableSquares,
      Bitboard enemies,
      ColoredPiece piece,
      BitboardRepresentation bitboardRepresentation) {
    return bitboardToMoves(
        getInlineMoves(square, unreachableSquares, enemies)
            .or(getDiagonalMoves(square, unreachableSquares, enemies)),
        enemies,
        square,
        piece,
        bitboardRepresentation);
  }

  /**
   * Generate the list of possible moves from a given position for a bishop piece.
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @param enemies Enemies occupation bitboard
   * @return The list of possible moves
   */
  public static List<Move> getBishopMoves(
      Position square,
      Bitboard unreachableSquares,
      Bitboard enemies,
      ColoredPiece piece,
      BitboardRepresentation bitboardRepresentation) {
    return bitboardToMoves(
        getDiagonalMoves(square, unreachableSquares, enemies),
        enemies,
        square,
        piece,
        bitboardRepresentation);
  }

  /**
   * Generate the list of possible moves from a given position for a rook piece.
   *
   * @param square Position of the piece
   * @param unreachableSquares unreachable squares bitboard
   * @param enemies Enemies occupation bitboard
   * @return The list of possible moves
   */
  public static List<Move> getRookMoves(
      Position square,
      Bitboard unreachableSquares,
      Bitboard enemies,
      ColoredPiece piece,
      BitboardRepresentation bitboardRepresentation) {
    return bitboardToMoves(
        getInlineMoves(square, unreachableSquares, enemies),
        enemies,
        square,
        piece,
        bitboardRepresentation);
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
  public static List<Move> getAvailableMoves(
      int x,
      int y,
      boolean kingReachable,
      BitboardRepresentation bitboardRepresentation,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle) {
    ColoredPiece piece = bitboardRepresentation.getPieceAt(x, y);
    Position enemyKing = bitboardRepresentation.getKing(piece.getColor() != Color.WHITE).get(0);
    Bitboard unreachableSquares =
        piece.getColor() == Color.WHITE
            ? bitboardRepresentation.getWhiteBoard()
            : bitboardRepresentation.getBlackBoard();
    unreachableSquares.clearBit(x % 8 + y * 8); // remove piece position from reachable positions
    if (!kingReachable) {
      unreachableSquares.setBit(
          enemyKing.x() % 8 + enemyKing.y() * 8); // Put enemyKing to unreachable positions
    }
    Bitboard enemies =
        piece.getColor() == Color.WHITE
            ? bitboardRepresentation.getBlackBoard()
            : bitboardRepresentation.getWhiteBoard();
    verbose(
        LOGGER,
        "Generating moves for "
            + piece.getColor()
            + " "
            + piece.getPiece()
            + " at ["
            + x
            + ", "
            + y
            + "] (king reachable="
            + kingReachable
            + ")");
    return switch (piece.getPiece()) {
      case KING ->
          getKingMoves(
              new Position(x, y),
              unreachableSquares,
              enemies,
              piece,
              bitboardRepresentation,
              enPassantPos,
              isLastMoveDoublePush,
              isWhiteLongCastle,
              isWhiteShortCastle,
              isBlackLongCastle,
              isBlackShortCastle);
      case QUEEN ->
          getQueenMoves(
              new Position(x, y), unreachableSquares, enemies, piece, bitboardRepresentation);
      case BISHOP ->
          getBishopMoves(
              new Position(x, y), unreachableSquares, enemies, piece, bitboardRepresentation);
      case ROOK ->
          getRookMoves(
              new Position(x, y), unreachableSquares, enemies, piece, bitboardRepresentation);
      case KNIGHT ->
          getKnightMoves(
              new Position(x, y), unreachableSquares, enemies, piece, bitboardRepresentation);
      case PAWN ->
          piece.getColor() == Color.WHITE
              ? getPawnMoves(
                  new Position(x, y),
                  unreachableSquares,
                  enemies,
                  true,
                  bitboardRepresentation,
                  enPassantPos,
                  isLastMoveDoublePush)
              : getPawnMoves(
                  new Position(x, y),
                  unreachableSquares,
                  enemies,
                  false,
                  bitboardRepresentation,
                  enPassantPos,
                  isLastMoveDoublePush);
      default -> new ArrayList<>();
    };
  }

  /**
   * Equivalent to getMoveBitboard but does not generate castling.
   *
   * @param x The board column
   * @param y The board row
   * @param kingReachable Can the piece reach opponent king (keep false if not checking
   *     check/checkmate)
   * @return The bitboard containing all the reachable positions
   */
  public static Bitboard getAttackBitboard(
      int x,
      int y,
      boolean kingReachable,
      BitboardRepresentation bitboardRepresentation,
      Position enPassantPos,
      boolean isLastMoveDoublePush) {
    ColoredPiece piece = bitboardRepresentation.getPieceAt(x, y);
    int enemyKing = bitboardRepresentation.getKingOpti(piece.getColor() != Color.WHITE);
    Bitboard unreachableSquares =
        piece.getColor() == Color.WHITE
            ? bitboardRepresentation.getWhiteBoard()
            : bitboardRepresentation.getBlackBoard();
    unreachableSquares.clearBit(x % 8 + y * 8); // remove piece position from reachable positions
    if (!kingReachable) {
      unreachableSquares.setBit(enemyKing); // Put enemyKing to unreachable positions
    }
    Bitboard enemies =
        piece.getColor() == Color.WHITE
            ? bitboardRepresentation.getBlackBoard()
            : bitboardRepresentation.getWhiteBoard();
    verbose(
        LOGGER,
        "Generating attack moves for "
            + piece.getColor()
            + " "
            + piece.getPiece()
            + " at ["
            + x
            + ", "
            + y
            + "] (king reachable="
            + kingReachable
            + ")");
    return switch (piece.getPiece()) {
      case KING ->
          getKingAttackBitboard(
              new Position(x, y), unreachableSquares, enemies, piece, bitboardRepresentation);
      case QUEEN ->
          getInlineMoves(new Position(x, y), unreachableSquares, enemies)
              .or(getDiagonalMoves(new Position(x, y), unreachableSquares, enemies));
      case BISHOP -> getDiagonalMoves(new Position(x, y), unreachableSquares, enemies);
      case ROOK -> getInlineMoves(new Position(x, y), unreachableSquares, enemies);
      case KNIGHT -> getKnightMoveBitboard(new Position(x, y), unreachableSquares);
      case PAWN ->
          piece.getColor() == Color.WHITE
              ? getPawnMoveBitboard(
                  new Position(x, y),
                  unreachableSquares,
                  enemies,
                  true,
                  enPassantPos,
                  isLastMoveDoublePush)
              : getPawnMoveBitboard(
                  new Position(x, y),
                  unreachableSquares,
                  enemies,
                  false,
                  enPassantPos,
                  isLastMoveDoublePush);
      default -> new Bitboard();
    };
  }

  /**
   * Equivalent to getAvailableMoves but do not generate the move list from the bitboard. Used in
   * check verification optimisation.
   *
   * @param x The board column
   * @param y The board row
   * @param kingReachable Can the piece reach opponent king (keep false if not checking
   *     check/checkmate)
   * @return The bitboard containing all the reachable positions
   */
  public static Bitboard getMoveBitboard(
      int x,
      int y,
      boolean kingReachable,
      BitboardRepresentation bitboardRepresentation,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle) {
    ColoredPiece piece = bitboardRepresentation.getPieceAt(x, y);
    int enemyKing = bitboardRepresentation.getKingOpti(piece.getColor() != Color.WHITE);
    Bitboard unreachableSquares =
        piece.getColor() == Color.WHITE
            ? bitboardRepresentation.getWhiteBoard()
            : bitboardRepresentation.getBlackBoard();
    unreachableSquares.clearBit(x % 8 + y * 8); // remove piece position from reachable positions
    if (!kingReachable) {
      unreachableSquares.setBit(enemyKing); // Put enemyKing to unreachable positions
    }
    Bitboard enemies =
        piece.getColor() == Color.WHITE
            ? bitboardRepresentation.getBlackBoard()
            : bitboardRepresentation.getWhiteBoard();
    verbose(
        LOGGER,
        "Generating moves for "
            + piece.getColor()
            + " "
            + piece.getPiece()
            + " at ["
            + x
            + ", "
            + y
            + "] (king reachable="
            + kingReachable
            + ")");
    return switch (piece.getPiece()) {
      case KING ->
          getKingMoveBitboard(
              new Position(x, y),
              unreachableSquares,
              enemies,
              piece,
              bitboardRepresentation,
              enPassantPos,
              isLastMoveDoublePush,
              isWhiteLongCastle,
              isWhiteShortCastle,
              isBlackLongCastle,
              isBlackShortCastle);
      case QUEEN ->
          getInlineMoves(new Position(x, y), unreachableSquares, enemies)
              .or(getDiagonalMoves(new Position(x, y), unreachableSquares, enemies));
      case BISHOP -> getDiagonalMoves(new Position(x, y), unreachableSquares, enemies);
      case ROOK -> getInlineMoves(new Position(x, y), unreachableSquares, enemies);
      case KNIGHT -> getKnightMoveBitboard(new Position(x, y), unreachableSquares);
      case PAWN ->
          piece.getColor() == Color.WHITE
              ? getPawnMoveBitboard(
                  new Position(x, y),
                  unreachableSquares,
                  enemies,
                  true,
                  enPassantPos,
                  isLastMoveDoublePush)
              : getPawnMoveBitboard(
                  new Position(x, y),
                  unreachableSquares,
                  enemies,
                  false,
                  enPassantPos,
                  isLastMoveDoublePush);
      default -> new Bitboard();
    };
  }

  /**
   * Generate the possible moves for a player. This function do not apply special rules (castling,
   * pinned, ...).
   *
   * @param isWhite {true} if pawn is white, {false} if pawn is black
   * @return The list of possible moves (without special cases)
   */
  public static List<Move> getAllAvailableMoves(
      boolean isWhite,
      BitboardRepresentation bitboardRepresentation,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle) {
    debug(LOGGER, "Getting all available moves for a player");
    Bitboard pieces =
        isWhite ? bitboardRepresentation.getWhiteBoard() : bitboardRepresentation.getBlackBoard();
    List<Move> moves = new ArrayList<>();
    for (Integer i : pieces.getSetBits()) {
      Position piecePosition = bitboardRepresentation.squareToPosition(i);
      moves.addAll(
          getAvailableMoves(
              piecePosition.x(),
              piecePosition.y(),
              false,
              bitboardRepresentation,
              enPassantPos,
              isLastMoveDoublePush,
              isWhiteLongCastle,
              isWhiteShortCastle,
              isBlackLongCastle,
              isBlackShortCastle));
    }
    return moves;
  }

  /**
   * Generate the possible moves for a player. This function do not apply check after guard.
   * Optimised for AI.
   *
   * @param isWhite {true} if pawn is white, {false} if pawn is black
   * @return The bitboard containing all possible moves (without special cases)
   */
  public static Bitboard getColorMoveBitboard(
      boolean isWhite,
      BitboardRepresentation bitboardRepresentation,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle) {
    Bitboard pieces =
        isWhite ? bitboardRepresentation.getWhiteBoard() : bitboardRepresentation.getBlackBoard();
    Bitboard attacked = new Bitboard();
    for (Integer i : pieces.getSetBits()) {
      attacked =
          attacked.or(
              getMoveBitboard(
                  i % 8,
                  i / 8,
                  true,
                  bitboardRepresentation,
                  enPassantPos,
                  isLastMoveDoublePush,
                  isWhiteLongCastle,
                  isWhiteShortCastle,
                  isBlackLongCastle,
                  isBlackShortCastle));
    }
    return attacked;
  }

  /**
   * Generate the attacked squares for a player. This function does not check castling
   *
   * @param isWhite {true} if pawn is white, {false} if pawn is black
   * @return The bitboard containing all possible moves (without special cases)
   */
  public static Bitboard getColorAttackBitboard(
      boolean isWhite,
      BitboardRepresentation bitboardRepresentation,
      Position enPassantPos,
      boolean isLastMoveDoublePush) {
    Bitboard pieces =
        isWhite ? bitboardRepresentation.getWhiteBoard() : bitboardRepresentation.getBlackBoard();
    Bitboard attacked = new Bitboard();
    for (Integer i : pieces.getSetBits()) {
      attacked =
          attacked.or(
              getAttackBitboard(
                  i % 8, i / 8, true, bitboardRepresentation, enPassantPos, isLastMoveDoublePush));
    }
    return attacked;
  }

  /**
   * Returns the list of available moves for the king of either white or black.
   *
   * @param white true if we want the moves of the white king, false otherwise
   * @return the list of moves for the corresponding king
   */
  public static List<Move> retrieveKingMoves(
      boolean white,
      BitboardRepresentation bitboardRepresentation,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle) {
    if (white) {
      Position whiteKingPos = bitboardRepresentation.getKing(true).get(0);
      ColoredPiece whiteKing =
          bitboardRepresentation.getPieceAt(whiteKingPos.x(), whiteKingPos.y());
      Bitboard unreachableSquaresWhite =
          whiteKing.getColor() == Color.WHITE
              ? bitboardRepresentation.getWhiteBoard()
              : bitboardRepresentation.getBlackBoard();
      unreachableSquaresWhite.clearBit(whiteKingPos.x() % 8 + whiteKingPos.y() * 8);

      return getKingMoves(
          whiteKingPos,
          unreachableSquaresWhite,
          bitboardRepresentation.getBlackBoard(),
          whiteKing,
          bitboardRepresentation,
          enPassantPos,
          isLastMoveDoublePush,
          isWhiteLongCastle,
          isWhiteShortCastle,
          isBlackLongCastle,
          isBlackShortCastle);
    } else {
      Position blackKingPos = bitboardRepresentation.getKing(false).get(0);

      ColoredPiece blackKing =
          bitboardRepresentation.getPieceAt(blackKingPos.x(), blackKingPos.y());

      Bitboard unreachableSquaresBlack =
          blackKing.getColor() == Color.WHITE
              ? bitboardRepresentation.getWhiteBoard()
              : bitboardRepresentation.getBlackBoard();
      unreachableSquaresBlack.clearBit(blackKingPos.x() % 8 + blackKingPos.y() * 8);

      return getKingMoves(
          blackKingPos,
          unreachableSquaresBlack,
          bitboardRepresentation.getWhiteBoard(),
          blackKing,
          bitboardRepresentation,
          enPassantPos,
          isLastMoveDoublePush,
          isWhiteLongCastle,
          isWhiteShortCastle,
          isBlackLongCastle,
          isBlackShortCastle);
    }
  }

  /**
   * Returns the list of available for the bishops of either white or black.
   *
   * @param white true if we want the moves of the white bishops, false otherwise
   * @return the list of moves for the corresponding bishops
   */
  public static List<Move> retrieveBishopMoves(
      boolean white, BitboardRepresentation bitboardRepresentation) {
    List<Position> bishops = bitboardRepresentation.getBishops(white);
    Bitboard friendlyPieces =
        white ? bitboardRepresentation.getWhiteBoard() : bitboardRepresentation.getBlackBoard();
    Bitboard enemyPieces =
        white ? bitboardRepresentation.getBlackBoard() : bitboardRepresentation.getWhiteBoard();
    List<Move> bishopMoves = new ArrayList<>();

    for (Position bishopPos : bishops) {
      ColoredPiece bishop = bitboardRepresentation.getPieceAt(bishopPos.x(), bishopPos.y());
      bishopMoves.addAll(
          getBishopMoves(bishopPos, friendlyPieces, enemyPieces, bishop, bitboardRepresentation));
    }

    return bishopMoves;
  }
}
