package pdp.model.board;

import static pdp.utils.Logging.DEBUG;
import static pdp.utils.Logging.VERBOSE;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Logging;
import pdp.utils.Position;

public class BitboardMovesGen {
  private static final Logger LOGGER = Logger.getLogger(BitboardMovesGen.class.getName());

  private BitboardMovesGen() {
    throw new UnsupportedOperationException("Cannot instantiate utility class");
  }

  static {
    Logging.configureLogging(LOGGER);
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
   * Generate the bitboard containing the reachable positions for up, down, left, right directions
   *
   * @param square The position of the piece that want to move
   * @param unreachableSquares A bitboard containing all the unreachable squares
   * @param enemies A bitboard containing all the enemies pieces
   * @return A bitboard containing the possible inline moves
   */
  public static Bitboard getInlineMoves(
      Position square, Bitboard unreachableSquares, Bitboard enemies) {
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
  public static Bitboard getDiagonalMoves(
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
  public static List<Move> bitboardToMoves(
      Bitboard moveBitboard,
      Bitboard enemies,
      Position source,
      ColoredPiece piece,
      BitboardRepresentation bitboardRepresentation) {
    List<Move> moves = new ArrayList<>();
    for (Integer i : moveBitboard.getSetBits()) {
      if (enemies.getBit(i)) { // move is capture
        for (int j = 0; j < bitboardRepresentation.getBitboards().length; j++) {
          if (enemies.getBit(i)) {
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

      } else {
        moves.add(new Move(source, bitboardRepresentation.squareToPosition(i), piece, false));
      }
      // TODO: save the captured piece
      // enemies.getBit(i) ? true : false -> capture ?
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
  public static List<Move> getKingMoves(
      Position square,
      Bitboard unreachableSquares,
      Bitboard enemies,
      ColoredPiece piece,
      BitboardRepresentation bitboardRepresentation) {
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
    return bitboardToMoves(move, enemies, square, piece, bitboardRepresentation);
  }

  public static Bitboard getKingMoveBitboard(
      Position square, Bitboard unreachableSquares, Bitboard enemies, ColoredPiece piece) {
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
    return move;
  }

  /**
   * Generate the list of possible moves from a given position for a knight piece
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
    return bitboardToMoves(move, enemies, square, piece, bitboardRepresentation);
  }

  public static Bitboard getKnightMoveBitboard(
      Position square, Bitboard unreachableSquares, Bitboard enemies, ColoredPiece piece) {
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
    return move;
  }

  /**
   * Generate the list of possible moves from a given position for a pawn piece
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
      BitboardRepresentation bitboardRepresentation) {
    Bitboard position = new Bitboard();
    Bitboard attackRight;
    Bitboard attackLeft;
    int squareIndex = square.getX() % 8 + square.getY() * 8;
    position.setBit(squareIndex);

    if (white) {
      attackRight = position.moveUpRight().and(enemies);
      attackLeft = position.moveUpLeft().and(enemies);
      position = position.moveUp().and(enemies.not());
    } else {
      attackRight = position.moveDownRight().and(enemies);
      attackLeft = position.moveDownLeft().and(enemies);
      position = position.moveDown().and(enemies.not());
    }
    position = position.xor(position.and(unreachableSquares));

    return bitboardToMoves(
        position.or(attackRight).or(attackLeft),
        enemies,
        square,
        new ColoredPiece(Piece.PAWN, white ? Color.WHITE : Color.BLACK),
        bitboardRepresentation);
  }

  public static Bitboard getPawnMoveBitboard(
      Position square, Bitboard unreachableSquares, Bitboard enemies, boolean white) {
    Bitboard position = new Bitboard();
    Bitboard attackRight;
    Bitboard attackLeft;
    int squareIndex = square.getX() % 8 + square.getY() * 8;
    position.setBit(squareIndex);

    /*
    if (white && square.getY() == 6 || !white && square.getY() == 1) {
      return new Bitboard(0L);
    }
    */

    if (white) {
      attackRight = position.moveUpRight().and(enemies);
      attackLeft = position.moveUpLeft().and(enemies);
      position = position.moveUp().and(enemies.not());
    } else {
      attackRight = position.moveDownRight().and(enemies);
      attackLeft = position.moveDownLeft().and(enemies);
      position = position.moveDown().and(enemies.not());
    }
    position = position.xor(position.and(unreachableSquares));

    return position.or(attackRight).or(attackLeft);
  }

  /**
   * Generate the list of possible moves from a given position for a queen piece
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
   * Generate the list of possible moves from a given position for a bishop piece
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
   * Generate the list of possible moves from a given position for a rook piece
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
   * apply special rules (castling, pinned, ...)
   *
   * @param x The board column
   * @param y The board row
   * @param kingReachable Can the piece reach opponent king (keep false if not checking
   *     check/checkmate)
   * @return The list of possible moves (without special cases)
   */
  public static List<Move> getAvailableMoves(
      int x, int y, boolean kingReachable, BitboardRepresentation bitboardRepresentation) {
    ColoredPiece piece = bitboardRepresentation.getPieceAt(x, y);
    Position enemyKing = bitboardRepresentation.getKing(piece.getColor() != Color.WHITE).get(0);
    Bitboard unreachableSquares =
        piece.getColor() == Color.WHITE
            ? bitboardRepresentation.getWhiteBoard()
            : bitboardRepresentation.getBlackBoard();
    unreachableSquares.clearBit(x % 8 + y * 8); // remove piece position from reachable positions
    if (!kingReachable) {
      unreachableSquares.setBit(
          enemyKing.getX() % 8 + enemyKing.getY() * 8); // Put enemyKing to unreachable positions
    }
    Bitboard enemies =
        piece.getColor() == Color.WHITE
            ? bitboardRepresentation.getBlackBoard()
            : bitboardRepresentation.getWhiteBoard();
    VERBOSE(
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
              new Position(x, y), unreachableSquares, enemies, piece, bitboardRepresentation);
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
                  new Position(x, y), unreachableSquares, enemies, true, bitboardRepresentation)
              : getPawnMoves(
                  new Position(x, y), unreachableSquares, enemies, false, bitboardRepresentation);
      default -> new ArrayList<>();
    };
  }

  /**
   * Equivalent to getAvailableMoves but do not generate the move list from the bitboard. Used in
   * check verification optimisation
   *
   * @param x The board column
   * @param y The board row
   * @param kingReachable Can the piece reach opponent king (keep false if not checking
   *     check/checkmate)
   * @return The bitboard containing all the reachable positions
   */
  public static Bitboard getMoveBitboard(
      int x, int y, boolean kingReachable, BitboardRepresentation bitboardRepresentation) {
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
    VERBOSE(
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
      case KING -> getKingMoveBitboard(new Position(x, y), unreachableSquares, enemies, piece);
      case QUEEN ->
          getInlineMoves(new Position(x, y), unreachableSquares, enemies)
              .or(getDiagonalMoves(new Position(x, y), unreachableSquares, enemies));
      case BISHOP -> getDiagonalMoves(new Position(x, y), unreachableSquares, enemies);
      case ROOK -> getInlineMoves(new Position(x, y), unreachableSquares, enemies);
      case KNIGHT -> getKnightMoveBitboard(new Position(x, y), unreachableSquares, enemies, piece);
      case PAWN ->
          piece.getColor() == Color.WHITE
              ? getPawnMoveBitboard(new Position(x, y), unreachableSquares, enemies, true)
              : getPawnMoveBitboard(new Position(x, y), unreachableSquares, enemies, false);
      default -> new Bitboard();
    };
  }

  /**
   * Generate the possible moves for a player. This function do not apply special rules (castling,
   * pinned, ...)
   *
   * @param isWhite {true} if pawn is white, {false} if pawn is black
   * @return The list of possible moves (without special cases)
   */
  public static List<Move> getAllAvailableMoves(
      boolean isWhite, BitboardRepresentation bitboardRepresentation) {
    DEBUG(LOGGER, "Getting all available moves for a player");
    Bitboard pieces =
        isWhite ? bitboardRepresentation.getWhiteBoard() : bitboardRepresentation.getBlackBoard();
    List<Move> moves = new ArrayList<>();
    for (Integer i : pieces.getSetBits()) {
      Position piecePosition = bitboardRepresentation.squareToPosition(i);
      moves.addAll(
          getAvailableMoves(
              piecePosition.getX(), piecePosition.getY(), false, bitboardRepresentation));
    }
    return moves;
  }

  /**
   * Generate the possible moves for a player. This function do not apply special rules (castling,
   * pinned, ...). Optimised for AI
   *
   * @param isWhite {true} if pawn is white, {false} if pawn is black
   * @return The bitboard containing all possible moves (without special cases)
   */
  public static Bitboard getColorMoveBitboard(
      boolean isWhite, BitboardRepresentation bitboardRepresentation) {
    Bitboard pieces =
        isWhite ? bitboardRepresentation.getWhiteBoard() : bitboardRepresentation.getBlackBoard();
    Bitboard attacked = new Bitboard();
    for (Integer i : pieces.getSetBits()) {
      attacked = attacked.or(getMoveBitboard(i % 8, i / 8, true, bitboardRepresentation));
    }
    return attacked;
  }

  /**
   * Returns the list of available moves for the king of either white or black
   *
   * @param white true if we want the moves of the white king, false otherwise
   * @return the list of moves for the corresponding king
   */
  public static List<Move> retrieveKingMoves(
      boolean white, BitboardRepresentation bitboardRepresentation) {
    if (white) {
      Position whiteKingPos = bitboardRepresentation.getKing(true).get(0);
      ColoredPiece whiteKing =
          bitboardRepresentation.getPieceAt(whiteKingPos.getX(), whiteKingPos.getY());
      Bitboard unreachableSquaresWhite =
          whiteKing.getColor() == Color.WHITE
              ? bitboardRepresentation.getWhiteBoard()
              : bitboardRepresentation.getBlackBoard();
      unreachableSquaresWhite.clearBit(whiteKingPos.getX() % 8 + whiteKingPos.getY() * 8);
      List<Move> whiteKingMoves =
          getKingMoves(
              whiteKingPos,
              unreachableSquaresWhite,
              bitboardRepresentation.getBlackBoard(),
              whiteKing,
              bitboardRepresentation);

      return whiteKingMoves;
    } else {
      Position blackKingPos = bitboardRepresentation.getKing(false).get(0);

      ColoredPiece blackKing =
          bitboardRepresentation.getPieceAt(blackKingPos.getX(), blackKingPos.getY());

      Bitboard unreachableSquaresBlack =
          blackKing.getColor() == Color.WHITE
              ? bitboardRepresentation.getWhiteBoard()
              : bitboardRepresentation.getBlackBoard();
      unreachableSquaresBlack.clearBit(blackKingPos.getX() % 8 + blackKingPos.getY() * 8);

      List<Move> blackKingMoves =
          getKingMoves(
              blackKingPos,
              unreachableSquaresBlack,
              bitboardRepresentation.getWhiteBoard(),
              blackKing,
              bitboardRepresentation);

      return blackKingMoves;
    }
  }

  /**
   * Returns the list of available for the bishops of either white or black
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
      ColoredPiece bishop = bitboardRepresentation.getPieceAt(bishopPos.getX(), bishopPos.getY());
      bishopMoves.addAll(
          getBishopMoves(bishopPos, friendlyPieces, enemyPieces, bishop, bitboardRepresentation));
    }

    return bishopMoves;
  }

  public static List<Move> getSpecialMoves(
      boolean white,
      BitboardRepresentation bitboardRepresentation,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle) {
    Color player = white ? Color.WHITE : Color.BLACK;
    Color opponent = !white ? Color.WHITE : Color.BLACK;
    List<Move> specialMoves = new ArrayList<>();
    if (enPassantPos != null && isLastMoveDoublePush) {
      Position pos = enPassantPos;
      int pawnY = pos.getY() + (white ? -1 : 1);
      Position capturedPos = new Position(pos.getX(), pawnY);

      // left
      if (pos.getX() > 0) {
        if (bitboardRepresentation
            .getPieceAt(pos.getX() - 1, pawnY)
            .equals(new ColoredPiece(Piece.PAWN, player))) {
          specialMoves.add(
              new Move(
                  new Position(pos.getX() - 1, pawnY),
                  pos,
                  new ColoredPiece(Piece.PAWN, player),
                  true,
                  new ColoredPiece(Piece.PAWN, opponent),
                  capturedPos));
        }
      }

      // right
      if (pos.getX() < bitboardRepresentation.getNbCols() - 1) {
        if (bitboardRepresentation
            .getPieceAt(pos.getX() + 1, pawnY)
            .equals(new ColoredPiece(Piece.PAWN, player))) {
          specialMoves.add(
              new Move(
                  new Position(pos.getX() + 1, pawnY),
                  pos,
                  new ColoredPiece(Piece.PAWN, player),
                  true,
                  new ColoredPiece(Piece.PAWN, opponent),
                  capturedPos));
        }
      }
    }

    if (isWhiteLongCastle && white) {
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
        specialMoves.add(
            new Move(
                new Position(4, 0),
                new Position(2, 0),
                new ColoredPiece(Piece.KING, Color.WHITE),
                false));
      }
    }

    if (isWhiteShortCastle && white) {
      if (!bitboardRepresentation.isAttacked(5, 0, Color.BLACK)
          && !bitboardRepresentation.isAttacked(6, 0, Color.BLACK)
          && !bitboardRepresentation.isAttacked(4, 0, Color.BLACK)
          && bitboardRepresentation
              .getPieceAt(5, 0)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))
          && bitboardRepresentation
              .getPieceAt(6, 0)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))) {
        specialMoves.add(
            new Move(
                new Position(4, 0),
                new Position(6, 0),
                new ColoredPiece(Piece.KING, Color.WHITE),
                false));
      }
    }

    if (isBlackLongCastle && !white) {
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
        specialMoves.add(
            new Move(
                new Position(4, 7),
                new Position(2, 7),
                new ColoredPiece(Piece.KING, Color.BLACK),
                false));
      }
    }

    if (isBlackShortCastle && !white) {
      if (!bitboardRepresentation.isAttacked(5, 7, Color.WHITE)
          && !bitboardRepresentation.isAttacked(6, 7, Color.WHITE)
          && !bitboardRepresentation.isAttacked(4, 7, Color.WHITE)
          && bitboardRepresentation
              .getPieceAt(5, 7)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))
          && bitboardRepresentation
              .getPieceAt(6, 7)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))) {
        specialMoves.add(
            new Move(
                new Position(4, 7),
                new Position(6, 7),
                new ColoredPiece(Piece.KING, Color.BLACK),
                false));
      }
    }

    for (Position pos : bitboardRepresentation.getPawns(white)) {
      if (pos.getY() == 1
          && bitboardRepresentation
              .getPieceAt(pos.getX(), pos.getY() + 2)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))
          && bitboardRepresentation
              .getPieceAt(pos.getX(), pos.getY() + 1)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))
          && white) {
        specialMoves.add(
            new Move(
                pos,
                new Position(pos.getX(), pos.getY() + 2),
                new ColoredPiece(Piece.PAWN, player),
                false));
      }
      if (pos.getY() == 6 && white) {
        if (bitboardRepresentation
            .getPieceAt(pos.getX(), pos.getY() + 1)
            .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))) {
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX(), pos.getY() + 1), Piece.QUEEN));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX(), pos.getY() + 1), Piece.KNIGHT));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX(), pos.getY() + 1), Piece.ROOK));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX(), pos.getY() + 1), Piece.BISHOP));
        }
        if (pos.getX() < bitboardRepresentation.getNbCols() - 1
            && bitboardRepresentation.getPieceAt(pos.getX() + 1, pos.getY() + 1).getColor()
                == opponent) {
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX() + 1, pos.getY() + 1), Piece.QUEEN));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX() + 1, pos.getY() + 1), Piece.KNIGHT));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX() + 1, pos.getY() + 1), Piece.ROOK));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX() + 1, pos.getY() + 1), Piece.BISHOP));
        }
        if (pos.getX() > 0
            && bitboardRepresentation.getPieceAt(pos.getX() - 1, pos.getY() + 1).getColor()
                == opponent) {
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX() - 1, pos.getY() + 1), Piece.QUEEN));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX() - 1, pos.getY() + 1), Piece.KNIGHT));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX() - 1, pos.getY() + 1), Piece.ROOK));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX() - 1, pos.getY() + 1), Piece.BISHOP));
        }
      }
      if (pos.getY() == 6
          && bitboardRepresentation
              .getPieceAt(pos.getX(), pos.getY() - 2)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))
          && bitboardRepresentation
              .getPieceAt(pos.getX(), pos.getY() - 1)
              .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))
          && !white) {
        specialMoves.add(
            new Move(
                pos,
                new Position(pos.getX(), pos.getY() - 2),
                new ColoredPiece(Piece.PAWN, player),
                false));
      }

      if (pos.getY() == 1 && !white) {
        if (bitboardRepresentation
            .getPieceAt(pos.getX(), pos.getY() - 1)
            .equals(new ColoredPiece(Piece.EMPTY, Color.EMPTY))) {
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX(), pos.getY() - 1), Piece.QUEEN));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX(), pos.getY() - 1), Piece.KNIGHT));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX(), pos.getY() - 1), Piece.ROOK));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX(), pos.getY() - 1), Piece.BISHOP));
        }
        if (pos.getX() < bitboardRepresentation.getNbCols() - 1
            && bitboardRepresentation.getPieceAt(pos.getX() + 1, pos.getY() + 1).getColor()
                == opponent) {
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX() + 1, pos.getY() - 1), Piece.QUEEN));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX() + 1, pos.getY() - 1), Piece.KNIGHT));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX() + 1, pos.getY() - 1), Piece.ROOK));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX() + 1, pos.getY() - 1), Piece.BISHOP));
        }
        if (pos.getX() > 0
            && bitboardRepresentation.getPieceAt(pos.getX() - 1, pos.getY() + 1).getColor()
                == opponent) {
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX() - 1, pos.getY() - 1), Piece.QUEEN));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX() - 1, pos.getY() - 1), Piece.KNIGHT));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX() - 1, pos.getY() - 1), Piece.ROOK));
          specialMoves.add(
              new PromoteMove(pos, new Position(pos.getX() - 1, pos.getY() - 1), Piece.BISHOP));
        }
      }
    }

    return specialMoves;
  }
}
