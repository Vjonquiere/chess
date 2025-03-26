package pdp.model.board;

import static pdp.utils.Logging.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import pdp.exceptions.IllegalMoveException;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Logging;
import pdp.utils.Position;

/** Specification of the BitboardRepresentation. All methods are static because it is a util. */
public final class BitboardRules {
  private static final Logger LOGGER = Logger.getLogger(BitboardRules.class.getName());

  /** Private constructor to avoid instantiation. */
  private BitboardRules() {
    throw new UnsupportedOperationException("Cannot instantiate utility class");
  }

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Get if the given square (x,y format) can be attacked by a piece of the given color.
   *
   * @param x X coordinate of the Position
   * @param y Y coordinate of the Position
   * @param by The color of the attacker
   * @return True if the given square is attacked, False else
   */
  public static boolean isAttacked(
      int x, int y, Color by, BitboardRepresentation bitboardRepresentation) {
    Bitboard square = new Bitboard();
    square.setBit((x % 8) + (y * 8));
    return (square.getBits()
            & bitboardRepresentation.getColorMoveBitboard(by == Color.WHITE).getBits())
        != 0;
  }

  /**
   * Get the check state for the given color.
   *
   * @param color The piece color you want to know check status
   * @return True if the given color is in check, False else
   */
  public static boolean isCheck(Color color, BitboardRepresentation bitboardRepresentation) {
    int kingPosition = bitboardRepresentation.getKingOpti(color == Color.WHITE);
    Color attacker = color == Color.WHITE ? Color.BLACK : Color.WHITE;
    return isAttacked(kingPosition % 8, kingPosition / 8, attacker, bitboardRepresentation);
  }

  /**
   * Get the check state after move for the given color.
   *
   * @param color The piece color you want to know check status
   * @param move The move you want to check if it puts the king in check
   * @return True if the given color is in check after the given move, False else
   */
  public static boolean isCheckAfterMove(
      Color color, Move move, BitboardRepresentation bitboardRepresentation) {
    debug(LOGGER, "Checking if " + color + " is check after move (" + move + ")");
    ColoredPiece removedPiece = null;
    if (move.getTakeDest() == null) {
      move.setTakeDest(move.getDest());
    }
    if (move.isTake()) {
      removedPiece =
          bitboardRepresentation.getPieceAt(move.getTakeDest().x(), move.getTakeDest().y());
      bitboardRepresentation.deletePieceAt(move.getTakeDest().x(), move.getTakeDest().y());
    }
    bitboardRepresentation.movePiece(move.getSource(), move.getDest()); // Play move
    boolean isCheckAfterMove = isCheck(color, bitboardRepresentation);
    bitboardRepresentation.movePiece(move.getDest(), move.getSource()); // undo move
    if (move.isTake()) {
      bitboardRepresentation.addPieceAt(
          move.getTakeDest().x(), move.getTakeDest().y(), removedPiece);
    }
    if (isCheckAfterMove) {
      debug(LOGGER, color.toString() + "will be checked after move");
    }
    return isCheckAfterMove;
  }

  /**
   * Get the checkMate state for the given color (can be resources/time-consuming if there are many
   * pieces remaining on the board).
   *
   * @param color The piece color you want to know checkMate status
   * @return True if the given color is in checkMate, False else
   */
  public static boolean isCheckMate(Color color, BitboardRepresentation bitboardRepresentation) {
    debug(LOGGER, "Checking if " + color + " is check mate");
    if (!isCheck(color, bitboardRepresentation)) {
      return false;
    }
    Bitboard pieces =
        color == Color.WHITE
            ? bitboardRepresentation.getWhiteBoard()
            : bitboardRepresentation.getBlackBoard();
    for (Integer i : pieces.getSetBits()) {
      Position piecePosition = bitboardRepresentation.squareToPosition(i);
      List<Move> availableMoves =
          bitboardRepresentation.getAvailableMoves(
              piecePosition.x(), piecePosition.y(), false); // TODO: Check this line
      for (Move move : availableMoves) {
        if (move.getTakeDest() == null) {
          move.setTakeDest(move.getDest());
        }
        ColoredPiece removedPiece = null;
        if (move.isTake()) {
          removedPiece =
              bitboardRepresentation.getPieceAt(move.getTakeDest().x(), move.getTakeDest().y());
          bitboardRepresentation.deletePieceAt(move.getTakeDest().x(), move.getTakeDest().y());
        }
        bitboardRepresentation.movePiece(move.getSource(), move.getDest()); // Play move
        boolean isStillCheck = isCheck(color, bitboardRepresentation);
        bitboardRepresentation.movePiece(move.getDest(), move.getSource()); // Undo move
        if (move.isTake()) {
          bitboardRepresentation.addPieceAt(
              move.getTakeDest().x(), move.getTakeDest().y(), removedPiece);
        }
        if (!isStillCheck) {
          debug(LOGGER, color.toString() + " is not check mate");
          return false;
        }
      }
    }
    debug(LOGGER, color.toString() + " is check mate ");
    return true;
  }

  /**
   * Checks the StaleMate state for the given color.
   *
   * @param color The color you want to check StaleMate for
   * @param colorTurnToPlay Player's turn to know if player who potentially moves in check has to
   *     move
   * @return true if color {color} is stalemated. false otherwise.
   */
  public static boolean isStaleMate(
      Color color, Color colorTurnToPlay, BitboardRepresentation bitboardRepresentation) {
    if (isCheck(color, bitboardRepresentation)) {
      return false;
    }
    Bitboard pieces =
        color == Color.WHITE
            ? bitboardRepresentation.getWhiteBoard()
            : bitboardRepresentation.getBlackBoard();
    for (Integer i : pieces.getSetBits()) {
      Position piecePosition = bitboardRepresentation.squareToPosition(i);
      List<Move> availableMoves =
          bitboardRepresentation.getAvailableMoves(piecePosition.x(), piecePosition.y(), true);
      for (Move move : availableMoves) {
        ColoredPiece removedPiece = null;
        if (move.isTake()) {
          removedPiece = bitboardRepresentation.getPieceAt(move.getDest().x(), move.getDest().y());
          bitboardRepresentation.deletePieceAt(move.getDest().x(), move.getDest().y());
        }
        bitboardRepresentation.movePiece(move.getSource(), move.getDest()); // Play move
        boolean isStillCheck = isCheck(color, bitboardRepresentation);
        bitboardRepresentation.movePiece(move.getDest(), move.getSource()); // Undo move
        if (move.isTake()) {
          bitboardRepresentation.addPieceAt(move.getDest().x(), move.getDest().y(), removedPiece);
        }
        if (!isStillCheck) {
          debug(LOGGER, color.toString() + " is not stalemate");
          return false;
        }
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
   * colored Bishops).
   *
   * @return true if a draw by insufficient material is observed
   */
  public static boolean isDrawByInsufficientMaterial(
      BitboardRepresentation bitboardRepresentation) {
    debug(LOGGER, "Checking is draw by insufficient material");
    List<Position> posWhiteKing = bitboardRepresentation.getKing(true);
    List<Position> posBlackKing = bitboardRepresentation.getKing(false);
    if (posWhiteKing.isEmpty() || posBlackKing.isEmpty()) {
      return false;
    }

    // If at least a queen or a rook or a pawn is found on the board then no draw by insufficient
    // material
    List<List<Position>> posListFalseInAllCases = new ArrayList<>();
    posListFalseInAllCases.add(bitboardRepresentation.getQueens(true));
    posListFalseInAllCases.add(bitboardRepresentation.getRooks(true));
    posListFalseInAllCases.add(bitboardRepresentation.getPawns(true));
    posListFalseInAllCases.add(bitboardRepresentation.getQueens(false));
    posListFalseInAllCases.add(bitboardRepresentation.getRooks(false));
    posListFalseInAllCases.add(bitboardRepresentation.getPawns(false));

    for (List<Position> pieceList : posListFalseInAllCases) {
      if (!pieceList.isEmpty()) {
        return false;
      }
    }

    // Get all remaining pieces
    List<Position> posWhiteBishops = bitboardRepresentation.getBishops(true);
    List<Position> posBlackBishops = bitboardRepresentation.getBishops(false);
    List<Position> posWhiteKnights = bitboardRepresentation.getKnights(true);
    List<Position> posBlackKnights = bitboardRepresentation.getKnights(false);

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
      if ((whiteBishop.x() + whiteBishop.y()) % 2 == (blackBishop.x() + blackBishop.y()) % 2) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks if a pawn at Position(x,y) checks for promotion.
   *
   * @param x The x-coordinate (file) of the pawn
   * @param y The y-coordinate (rank) of the pawn
   * @param white {true} if pawn is white, {false} if pawn is black
   * @return true if the pawn is being promoted, otherwise false
   */
  public static boolean isPawnPromoting(
      int x, int y, boolean white, BitboardRepresentation bitboardRepresentation) {
    debug(LOGGER, "Checking is pawn promoting");
    if (white && y != 7) {
      return false;
    } else if (!white && y != 0) {
      return false;
    } else {
      // White pawns --> 5 and Black pawns --> 11
      Bitboard pawnBitBoard =
          white
              ? bitboardRepresentation.getBitboards()[5]
              : bitboardRepresentation.getBitboards()[11];
      int bitIndex = 8 * y + x;

      // If bit is 1 then pawn is located at Position(x,y)
      return pawnBitBoard.getBit(bitIndex);
    }
  }

  /**
   * Checks if a pawn at Position(x,y) checks for promotion.
   *
   * @param xSource The x-coordinate (file) of the source position
   * @param ySource The y-coordinate (rank) of the source position
   * @param xDest The x-coordinate (file) of the destination position
   * @param yDest The y-coordinate (rank) of the destination position
   * @param white {true} if pawn is white, {false} if pawn is black
   * @return true if the pawn is being promoted, otherwise false
   */
  public static boolean isPromotionMove(
      int xSource,
      int ySource,
      int xDest,
      int yDest,
      boolean white,
      BitboardRepresentation bitboardRepresentation) {
    debug(LOGGER, "Checking is promotion move");
    if (white && yDest != 7) {
      return false;
    } else if (!white && yDest != 0) {
      return false;
    } else {
      // White pawns --> 5 and Black pawns --> 11
      Bitboard pawnBitBoard =
          white
              ? bitboardRepresentation.getBitboards()[5]
              : bitboardRepresentation.getBitboards()[11];
      int bitIndex = 8 * ySource + xSource;

      // If bit is 1 then pawn is located at Position(xSource,ySource)
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
  public static void promotePawn(
      int x, int y, boolean white, Piece newPiece, BitboardRepresentation bitboardRepresentation) {
    debug(LOGGER, "Promoting pawn at [" + x + ", " + y + "] to " + newPiece);
    ColoredPiece pieceAtPosition = bitboardRepresentation.getPieceAt(x, y);
    if (pieceAtPosition.getPiece() != Piece.PAWN
        || pieceAtPosition.getColor() != (white ? Color.WHITE : Color.BLACK)) {
      return;
    }

    int boardIndex = white ? 0 : 6;
    Bitboard newPieceBitBoard = null;
    Bitboard pawnBitboard = bitboardRepresentation.getBitboards()[5 + boardIndex];
    switch (newPiece) {
      case KNIGHT:
        newPieceBitBoard = bitboardRepresentation.getBitboards()[4 + boardIndex];
        break;
      case BISHOP:
        newPieceBitBoard = bitboardRepresentation.getBitboards()[2 + boardIndex];
        break;
      case ROOK:
        newPieceBitBoard = bitboardRepresentation.getBitboards()[3 + boardIndex];
        break;
      case QUEEN:
        newPieceBitBoard = bitboardRepresentation.getBitboards()[1 + boardIndex];
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
   * two squares from its starting position.
   *
   * @param move The move to check
   * @param white {true} if pawn is white, {false} if pawn is black
   * @return True if the move is a valid double pawn push, false else
   */
  public static boolean isDoublePushPossible(
      Move move, boolean white, BitboardRepresentation bitboardRepresentation) {
    debug(LOGGER, "Checking is double push possible");
    ColoredPiece piece =
        bitboardRepresentation.getPieceAt(move.getSource().x(), move.getSource().y());
    if (white
        && piece.getPiece() == Piece.PAWN
        && move.getSource().y() == 1
        && move.getDest().y() == 3
        && move.getSource().x() == move.getDest().x()) {
      return (bitboardRepresentation.getPieceAt(move.getDest().x(), move.getDest().y()).getPiece()
              == Piece.EMPTY)
          && (bitboardRepresentation
                  .getPieceAt(move.getDest().x(), move.getDest().y() - 1)
                  .getPiece()
              == Piece.EMPTY);
    }

    if (!white
        && piece.getPiece() == Piece.PAWN
        && move.getSource().y() == 6
        && move.getDest().y() == 4
        && move.getSource().x() == move.getDest().x()) {
      return (bitboardRepresentation.getPieceAt(move.getDest().x(), move.getDest().y()).getPiece()
              == Piece.EMPTY)
          && (bitboardRepresentation
                  .getPieceAt(move.getDest().x(), move.getDest().y() + 1)
                  .getPiece()
              == Piece.EMPTY);
    }
    return false;
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
  public static boolean isEnPassant(
      int x, int y, Move move, boolean white, BitboardRepresentation bitboardRepresentation) {
    debug(LOGGER, "Checking is en passant");
    ColoredPiece piece =
        bitboardRepresentation.getPieceAt(move.getSource().x(), move.getSource().y());
    if (white
        && piece.getPiece() == Piece.PAWN
        && move.getDest().x() == x
        && move.getDest().y() == y
        && ((move.getSource().x() == (x - 1) && move.getSource().y() == (y - 1))
            || (move.getSource().x() == (x + 1) && move.getSource().y() == (y - 1)))) {
      return true;
    }
    if (!white
        && piece.getPiece() == Piece.PAWN
        && move.getDest().x() == x
        && move.getDest().y() == y
        && ((move.getSource().x() == (x + 1) && move.getSource().y() == (y + 1))
            || (move.getSource().x() == (x - 1) && move.getSource().y() == (y + 1)))) {
      return true;
    }
    return false;
  }

  public static void setSquare(
      ColoredPiece piece, int squareIndex, BitboardRepresentation bitboardRepresentation) {
    bitboardRepresentation.getBitboards()[BitboardRepresentation.getPiecesMap().getFromValue(piece)]
        .setBit(squareIndex);
  }

  public static Bitboard[] getBitboards(BitboardRepresentation bitboardRepresentation) {
    return bitboardRepresentation.getBitboards();
  }

  /**
   * Method that verifies of a player has enough material to mate. Used for rule loss on time but
   * enemy does not have enough material to mate.
   *
   * @param white color of the player we check the material for
   * @return true if {white} has enouhg material to mate. false otherwise
   */
  public static boolean hasEnoughMaterialToMate(
      boolean white, BitboardRepresentation bitboardRepresentation) {
    // Pawn can promote
    List<Position> posPawns = bitboardRepresentation.getPawns(white);
    if (!posPawns.isEmpty()) {
      return true;
    }
    // Mate with queen(s)
    List<Position> queenPos = bitboardRepresentation.getQueens(white);
    if (!queenPos.isEmpty()) {
      return true;
    }
    // Mate with rook(s)
    List<Position> rooksPos = bitboardRepresentation.getRooks(white);
    if (!rooksPos.isEmpty()) {
      return true;
    }
    // Mate with bishops
    List<Position> bishopsPos = bitboardRepresentation.getBishops(white);
    // Check if at least two bishops are of opposite colors
    if (bishopsPos.size() >= 2) {
      int nbBishopsLightSquares = 0;
      int nbBishopsDarkSquares = 0;
      for (Position posBishop : bishopsPos) {
        if ((posBishop.x() + posBishop.y()) % 2 == 0) {
          // Dark squared bishop
          nbBishopsDarkSquares++;
        } else {
          // Light squared bishop
          nbBishopsLightSquares++;
        }
      }
      // Can mate with bishops
      if (nbBishopsDarkSquares >= 1 && nbBishopsLightSquares >= 1) {
        return true;
      }
    }
    // Mate with knights
    List<Position> knightsPos = bitboardRepresentation.getKnights(white);
    if (knightsPos.size() >= 2) {
      return true;
    }
    // Mate with bishop and knight
    if (bishopsPos.size() == 1 && knightsPos.size() == 1) {
      return true;
    }
    return false;
  }

  /**
   * Retrieves the list of current positions of white pieces.
   *
   * @return the list containing the list of current positions for the white pieces
   */
  public static List<List<Position>> retrieveWhitePiecesPos(
      BitboardRepresentation bitboardRepresentation) {
    List<List<Position>> whitePositions = new ArrayList<>();

    List<Position> kingPos = bitboardRepresentation.getKing(true);
    List<Position> queenPos = bitboardRepresentation.getQueens(true);
    List<Position> rookPos = bitboardRepresentation.getRooks(true);
    List<Position> bishopPos = bitboardRepresentation.getBishops(true);
    List<Position> knightPos = bitboardRepresentation.getKnights(true);
    List<Position> pawnsPos = bitboardRepresentation.getPawns(true);

    whitePositions.add(kingPos);
    whitePositions.add(queenPos);
    whitePositions.add(rookPos);
    whitePositions.add(bishopPos);
    whitePositions.add(knightPos);
    whitePositions.add(pawnsPos);

    return whitePositions;
  }

  /**
   * Retrieves the current positions of black pieces.
   *
   * @return the list containing the list of current positions for the black pieces
   */
  public static List<List<Position>> retrieveBlackPiecesPos(
      BitboardRepresentation bitboardRepresentation) {
    List<List<Position>> blackPositions = new ArrayList<>();

    List<Position> kingPos = bitboardRepresentation.getKing(false);
    List<Position> queenPos = bitboardRepresentation.getQueens(false);
    List<Position> rookPos = bitboardRepresentation.getRooks(false);
    List<Position> bishopPos = bitboardRepresentation.getBishops(false);
    List<Position> knightPos = bitboardRepresentation.getKnights(false);
    List<Position> pawnsPos = bitboardRepresentation.getPawns(false);

    blackPositions.add(kingPos);
    blackPositions.add(queenPos);
    blackPositions.add(rookPos);
    blackPositions.add(bishopPos);
    blackPositions.add(knightPos);
    blackPositions.add(pawnsPos);

    return blackPositions;
  }

  /**
   * Retrieves the positions of white pieces at game start.
   *
   * @return the list containing the list of initial positions for the white pieces
   */
  public static List<List<Position>> retrieveInitialWhitePiecesPos() {
    List<List<Position>> whiteInitPos = new ArrayList<>();

    whiteInitPos.add(List.of(new Position(4, 0)));
    whiteInitPos.add(List.of(new Position(3, 0)));
    whiteInitPos.add(List.of(new Position(0, 0), new Position(7, 0)));
    whiteInitPos.add(List.of(new Position(2, 0), new Position(5, 0)));
    whiteInitPos.add(List.of(new Position(1, 0), new Position(6, 0)));
    List<Position> pawnsPos = new ArrayList<>();

    for (int i = 0; i < 8; i++) {
      pawnsPos.add(new Position(i, 1));
    }

    whiteInitPos.add(pawnsPos);

    return whiteInitPos;
  }

  /**
   * Retrieves the positions of black pieces at game start.
   *
   * @return the list containing the list of initial positions for the black pieces
   */
  public static List<List<Position>> retrieveInitialBlackPiecesPos() {
    List<List<Position>> blackInitPos = new ArrayList<>();

    blackInitPos.add(List.of(new Position(4, 7)));
    blackInitPos.add(List.of(new Position(3, 7)));
    blackInitPos.add(List.of(new Position(0, 7), new Position(7, 7)));
    blackInitPos.add(List.of(new Position(2, 7), new Position(5, 7)));
    blackInitPos.add(List.of(new Position(1, 7), new Position(6, 7)));
    List<Position> pawnsPos = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      pawnsPos.add(new Position(i, 6));
    }
    blackInitPos.add(pawnsPos);

    return blackInitPos;
  }

  /**
   * Determines if the given move is a castle move.
   *
   * @param coloredPiece The piece being moved, expected to be a king for castling.
   * @param source The source position of the move.
   * @param dest The destination position of the move.
   * @return true if the move is a castle move, false otherwise.
   */
  public static boolean isCastleMove(ColoredPiece coloredPiece, Position source, Position dest) {
    if (coloredPiece.getPiece() != Piece.KING) {
      return false;
    }
    int deltaX = Math.abs(dest.x() - source.x());
    return deltaX == 2
        && ((source.y() == 0 && dest.y() == 0) || (source.y() == 7 && dest.y() == 7));
  }

  /**
   * Return true if the piece located at sourcePosition is of the same color as the player that has
   * to play a move, false is not, and exception otherwise.
   *
   * @param white the game state for which we want to verify piece ownership
   * @param sourcePosition the position
   * @throws IllegalMoveException If the move is illegal in the current configuration.
   */
  public static boolean validatePieceOwnership(
      boolean white, Position sourcePosition, BitboardRepresentation bitboardRepresentation)
      throws IllegalMoveException {
    ColoredPiece pieceAtSource =
        bitboardRepresentation.getPieceAt(sourcePosition.x(), sourcePosition.y());

    if ((pieceAtSource.getColor() == Color.WHITE && !white)
        || (pieceAtSource.getColor() == Color.BLACK && white)) {
      debug(LOGGER, "Not a " + pieceAtSource.getColor() + " piece at " + sourcePosition);
      return false;
    }
    return true;
  }
}
