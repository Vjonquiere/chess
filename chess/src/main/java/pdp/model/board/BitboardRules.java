package pdp.model.board;

import static pdp.utils.Logging.debug;
import static pdp.utils.Logging.error;

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

  /** Logger of the class. */
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
      final int x, final int y, final Color by, final BitboardRepresentation bitboardRep) {
    final Bitboard square = new Bitboard();
    square.setBit((x % 8) + (y * 8));
    return (square.getBits() & bitboardRep.getColorAttackBitboard(by == Color.WHITE).getBits())
        != 0;
  }

  /**
   * Get the check state for the given color.
   *
   * @param color The piece color you want to know check status
   * @return True if the given color is in check, False else
   */
  public static boolean isCheck(final Color color, final BitboardRepresentation bitboardRep) {
    final int kingPosition = bitboardRep.getKingOpti(color == Color.WHITE);
    final Color attacker = color == Color.WHITE ? Color.BLACK : Color.WHITE;
    return isAttacked(kingPosition % 8, kingPosition / 8, attacker, bitboardRep);
  }

  /**
   * Get the check state after move for the given color.
   *
   * @param color The piece color you want to know check status
   * @param move The move you want to check if it puts the king in check
   * @return True if the given color is in check after the given move, False else
   */
  public static boolean isCheckAfterMove(
      Color color, Move move, BitboardRepresentation bitboardRep) {
    debug(LOGGER, "Checking if " + color + " is check after move (" + move + ")");
    ColoredPiece removedPiece = null;
    if (move.getTakeDest() == null) {
      move.setTakeDest(move.getDest());
    }
    if (move.isTake()) {
      removedPiece = bitboardRep.getPieceAt(move.getTakeDest().x(), move.getTakeDest().y());
      bitboardRep.deletePieceAt(move.getTakeDest().x(), move.getTakeDest().y());
    }
    bitboardRep.movePiece(move.getSource(), move.getDest()); // Play move
    boolean isCheckAfterMove = isCheck(color, bitboardRep);
    bitboardRep.movePiece(move.getDest(), move.getSource()); // undo move
    if (move.isTake()) {
      bitboardRep.addPieceAt(move.getTakeDest().x(), move.getTakeDest().y(), removedPiece);
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
  public static boolean isCheckMate(Color color, BitboardRepresentation bitboardRep) {
    debug(LOGGER, "Checking if " + color + " is check mate");
    if (!bitboardRep.isCheck(color)) {
      return false;
    }
    Bitboard pieces =
        color == Color.WHITE ? bitboardRep.getWhiteBoard() : bitboardRep.getBlackBoard();
    for (Integer i : pieces.getSetBits()) {
      Position piecePosition = bitboardRep.squareToPosition(i);
      List<Move> availableMoves =
          bitboardRep.getAvailableMoves(piecePosition.x(), piecePosition.y(), false);
      for (Move move : availableMoves) {
        if (move.getTakeDest() == null) {
          move.setTakeDest(move.getDest());
        }
        ColoredPiece removedPiece = null;
        if (move.isTake()) {
          removedPiece = bitboardRep.getPieceAt(move.getTakeDest().x(), move.getTakeDest().y());
          bitboardRep.deletePieceAt(move.getTakeDest().x(), move.getTakeDest().y());
        }
        bitboardRep.movePiece(move.getSource(), move.getDest()); // Play move
        boolean isStillCheck = bitboardRep.isCheck(color);
        bitboardRep.movePiece(move.getDest(), move.getSource()); // Undo move
        if (move.isTake()) {
          bitboardRep.addPieceAt(move.getTakeDest().x(), move.getTakeDest().y(), removedPiece);
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
      Color color, Color colorTurnToPlay, BitboardRepresentation bitboardRep) {
    if (bitboardRep.isCheck(color)) {
      return false;
    }
    Bitboard pieces =
        color == Color.WHITE ? bitboardRep.getWhiteBoard() : bitboardRep.getBlackBoard();
    for (Integer i : pieces.getSetBits()) {
      Position piecePosition = bitboardRep.squareToPosition(i);
      List<Move> availableMoves =
          bitboardRep.getAvailableMoves(piecePosition.x(), piecePosition.y(), true);
      for (Move move : availableMoves) {
        ColoredPiece removedPiece = null;
        if (move.isTake()) {
          removedPiece = bitboardRep.getPieceAt(move.getTakeDest().x(), move.getTakeDest().y());
          bitboardRep.deletePieceAt(move.getTakeDest().x(), move.getTakeDest().y());
        }
        bitboardRep.movePiece(move.getSource(), move.getDest()); // Play move
        boolean isStillCheck = isCheck(color, bitboardRep);
        bitboardRep.movePiece(move.getDest(), move.getSource()); // Undo move
        if (move.isTake()) {
          bitboardRep.addPieceAt(move.getTakeDest().x(), move.getTakeDest().y(), removedPiece);
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
   * Checks if draw by insufficient material is observed (both colors each case) Cases: King vs
   * King, King and Bishop vs King, King and Knight vs King, King and Bishop vs King and Bishop
   * (same colored Bishops).
   *
   * @return true if a draw by insufficient material is observed
   */
  public static boolean isDrawByInsufficientMaterial(BitboardRepresentation bitboardRep) {
    debug(LOGGER, "Checking is draw by insufficient material");
    List<Position> posWhiteKing = bitboardRep.getKing(true);
    List<Position> posBlackKing = bitboardRep.getKing(false);
    if (posWhiteKing.isEmpty() || posBlackKing.isEmpty()) {
      return false;
    }

    // If at least a queen or a rook or a pawn is found on the board then no draw by insufficient
    // material
    List<List<Position>> posListFalseInAllCases = new ArrayList<>();
    posListFalseInAllCases.add(bitboardRep.getQueens(true));
    posListFalseInAllCases.add(bitboardRep.getRooks(true));
    posListFalseInAllCases.add(bitboardRep.getPawns(true));
    posListFalseInAllCases.add(bitboardRep.getQueens(false));
    posListFalseInAllCases.add(bitboardRep.getRooks(false));
    posListFalseInAllCases.add(bitboardRep.getPawns(false));

    for (List<Position> pieceList : posListFalseInAllCases) {
      if (!pieceList.isEmpty()) {
        return false;
      }
    }

    // Get all remaining pieces
    List<Position> posWhiteBishops = bitboardRep.getBishops(true);
    List<Position> posBlackBishops = bitboardRep.getBishops(false);
    List<Position> posWhiteKnights = bitboardRep.getKnights(true);
    List<Position> posBlackKnights = bitboardRep.getKnights(false);

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
      return (whiteBishop.x() + whiteBishop.y()) % 2 == (blackBishop.x() + blackBishop.y()) % 2;
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
      int x, int y, boolean white, BitboardRepresentation bitboardRep) {
    debug(LOGGER, "Checking is pawn promoting");
    if (white && y != 7) {
      return false;
    } else if (!white && y != 0) {
      return false;
    } else {
      // White pawns --> 5 and Black pawns --> 11
      Bitboard pawnBitBoard =
          white ? bitboardRep.getBitboards()[5] : bitboardRep.getBitboards()[11];
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
      BitboardRepresentation bitboardRep) {
    debug(LOGGER, "Checking is promotion move");
    if (white && yDest != 7) {
      return false;
    } else if (!white && yDest != 0) {
      return false;
    } else {
      // White pawns --> 5 and Black pawns --> 11
      Bitboard pawnBitBoard =
          white ? bitboardRep.getBitboards()[5] : bitboardRep.getBitboards()[11];
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
      int x, int y, boolean white, Piece newPiece, BitboardRepresentation bitboardRep) {
    debug(LOGGER, "Promoting pawn at [" + x + ", " + y + "] to " + newPiece);
    ColoredPiece pieceAtPosition = bitboardRep.getPieceAt(x, y);
    if (pieceAtPosition.getPiece() != Piece.PAWN
        || pieceAtPosition.getColor() != (white ? Color.WHITE : Color.BLACK)) {
      return;
    }

    int boardIndex = white ? 0 : 6;
    Bitboard newPieceBitBoard;
    Bitboard pawnBitboard = bitboardRep.getBitboards()[5 + boardIndex];
    switch (newPiece) {
      case KNIGHT:
        newPieceBitBoard = bitboardRep.getBitboards()[4 + boardIndex];
        break;
      case BISHOP:
        newPieceBitBoard = bitboardRep.getBitboards()[2 + boardIndex];
        break;
      case ROOK:
        newPieceBitBoard = bitboardRep.getBitboards()[3 + boardIndex];
        break;
      case QUEEN:
        newPieceBitBoard = bitboardRep.getBitboards()[1 + boardIndex];
        break;
      default:
        error("Error: A pawn can only be promoted to Queen, Rook, Knight or Bishop !");
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
      Move move, boolean white, BitboardRepresentation bitboardRep) {
    debug(LOGGER, "Checking is double push possible");
    ColoredPiece piece = bitboardRep.getPieceAt(move.getSource().x(), move.getSource().y());
    if (white
        && piece.getPiece() == Piece.PAWN
        && move.getSource().y() == 1
        && move.getDest().y() == 3
        && move.getSource().x() == move.getDest().x()) {
      return (bitboardRep.getPieceAt(move.getDest().x(), move.getDest().y()).getPiece()
              == Piece.EMPTY)
          && (bitboardRep.getPieceAt(move.getDest().x(), move.getDest().y() - 1).getPiece()
              == Piece.EMPTY);
    }

    if (!white
        && piece.getPiece() == Piece.PAWN
        && move.getSource().y() == 6
        && move.getDest().y() == 4
        && move.getSource().x() == move.getDest().x()) {
      return (bitboardRep.getPieceAt(move.getDest().x(), move.getDest().y()).getPiece()
              == Piece.EMPTY)
          && (bitboardRep.getPieceAt(move.getDest().x(), move.getDest().y() + 1).getPiece()
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
      int x, int y, Move move, boolean white, BitboardRepresentation bitboardRep) {
    debug(LOGGER, "Checking is en passant");
    ColoredPiece piece = bitboardRep.getPieceAt(move.getSource().x(), move.getSource().y());
    if (white
        && piece.getPiece() == Piece.PAWN
        && move.getDest().x() == x
        && move.getDest().y() == y
        && ((move.getSource().x() == (x - 1) && move.getSource().y() == (y - 1))
            || (move.getSource().x() == (x + 1) && move.getSource().y() == (y - 1)))) {
      return true;
    }
    return !white
        && piece.getPiece() == Piece.PAWN
        && move.getDest().x() == x
        && move.getDest().y() == y
        && ((move.getSource().x() == (x + 1) && move.getSource().y() == (y + 1))
            || (move.getSource().x() == (x - 1) && move.getSource().y() == (y + 1)));
  }

  /**
   * Method that verifies of a player has enough material to mate. Used for rule loss on time but
   * enemy does not have enough material to mate.
   *
   * @param white color of the player we check the material for
   * @return true if {white} has enough material to mate. false otherwise
   */
  public static boolean hasEnoughMaterialToMate(boolean white, BitboardRepresentation bitboardRep) {
    // Pawn can promote
    List<Position> posPawns = bitboardRep.getPawns(white);
    if (!posPawns.isEmpty()) {
      return true;
    }
    // Mate with queen(s)
    List<Position> queenPos = bitboardRep.getQueens(white);
    if (!queenPos.isEmpty()) {
      return true;
    }
    // Mate with rook(s)
    List<Position> rooksPos = bitboardRep.getRooks(white);
    if (!rooksPos.isEmpty()) {
      return true;
    }
    // Mate with bishops
    List<Position> bishopsPos = bitboardRep.getBishops(white);
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
    List<Position> knightsPos = bitboardRep.getKnights(white);
    if (knightsPos.size() >= 2) {
      return true;
    }
    // Mate with bishop and knight
    return bishopsPos.size() == 1 && knightsPos.size() == 1;
  }

  /**
   * Retrieves the list of current positions of white pieces.
   *
   * @return the list containing the list of current positions for the white pieces
   */
  public static List<List<Position>> retrieveWhitePiecesPos(BitboardRepresentation bitboardRep) {
    List<List<Position>> whitePositions = new ArrayList<>();

    List<Position> kingPos = bitboardRep.getKing(true);
    List<Position> queenPos = bitboardRep.getQueens(true);
    List<Position> rookPos = bitboardRep.getRooks(true);
    List<Position> bishopPos = bitboardRep.getBishops(true);
    List<Position> knightPos = bitboardRep.getKnights(true);
    List<Position> pawnsPos = bitboardRep.getPawns(true);

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
  public static List<List<Position>> retrieveBlackPiecesPos(BitboardRepresentation bitboardRep) {
    List<List<Position>> blackPositions = new ArrayList<>();

    List<Position> kingPos = bitboardRep.getKing(false);
    List<Position> queenPos = bitboardRep.getQueens(false);
    List<Position> rookPos = bitboardRep.getRooks(false);
    List<Position> bishopPos = bitboardRep.getBishops(false);
    List<Position> knightPos = bitboardRep.getKnights(false);
    List<Position> pawnsPos = bitboardRep.getPawns(false);

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
      boolean white, Position sourcePosition, BitboardRepresentation bitboardRep)
      throws IllegalMoveException {
    ColoredPiece pieceAtSource = bitboardRep.getPieceAt(sourcePosition.x(), sourcePosition.y());

    if ((pieceAtSource.getColor() == Color.WHITE && !white)
        || (pieceAtSource.getColor() == Color.BLACK && white)) {
      debug(LOGGER, "Not a " + pieceAtSource.getColor() + " piece at " + sourcePosition);
      return false;
    }
    return true;
  }
}
