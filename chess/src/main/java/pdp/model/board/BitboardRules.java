package pdp.model.board;

import static pdp.utils.Logging.DEBUG;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import pdp.exceptions.IllegalMoveException;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Logging;
import pdp.utils.Position;

public class BitboardRules {
  private static final Logger LOGGER = Logger.getLogger(BitboardRules.class.getName());
  private BitboardRepresentation bitboardRepresentation;

  public BitboardRules(BitboardRepresentation bitboardRepresentation) {
    this.bitboardRepresentation = bitboardRepresentation;
  }

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Get if the given square (x,y format) can be attacked by a piece of the given color
   *
   * @param x X coordinate of the Position
   * @param y Y coordinate of the Position
   * @param by The color of the attacker
   * @return True if the given square is attacked, False else
   */
  public boolean isAttacked(int x, int y, Color by) {
    Bitboard square = new Bitboard();
    square.setBit((x % 8) + (y * 8));
    return (square.bitboard
            & this.bitboardRepresentation.getColorMoveBitboard(by == Color.WHITE).bitboard)
        != 0;
  }

  /**
   * Get the check state for the given color
   *
   * @param color The piece color you want to know check status
   * @return True if the given color is in check, False else
   */
  public boolean isCheck(Color color) {
    int kingPosition = this.bitboardRepresentation.getKingOpti(color == Color.WHITE);
    Color attacker = color == Color.WHITE ? Color.BLACK : Color.WHITE;
    return isAttacked(kingPosition % 8, kingPosition / 8, attacker);
  }

  /**
   * Get the check state after move for the given color
   *
   * @param color The piece color you want to know check status
   * @param move The move you want to check if it puts the king in check
   * @return True if the given color is in check after the given move, False else
   */
  public boolean isCheckAfterMove(Color color, Move move) {
    DEBUG(LOGGER, "Checking if " + color + " is check after move (" + move + ")");
    ColoredPiece removedPiece = null;
    if (move.isTake) {
      removedPiece =
          this.bitboardRepresentation.getPieceAt(move.getDest().getX(), move.getDest().getY());
      this.bitboardRepresentation.deletePieceAt(move.getDest().getX(), move.getDest().getY());
    }
    this.bitboardRepresentation.movePiece(move.source, move.dest); // Play move
    boolean isCheckAfterMove = isCheck(color);
    this.bitboardRepresentation.movePiece(move.dest, move.source); // undo move
    if (move.isTake) {
      this.bitboardRepresentation.addPieceAt(
          move.getDest().getX(), move.getDest().getY(), removedPiece);
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
  public boolean isCheckMate(Color color) {
    DEBUG(LOGGER, "Checking if " + color + " is check mate");
    if (!isCheck(color)) {
      return false;
    }
    Bitboard pieces =
        color == Color.WHITE
            ? this.bitboardRepresentation.getWhiteBoard()
            : this.bitboardRepresentation.getBlackBoard();
    for (Integer i : pieces.getSetBits()) {
      Position piecePosition = this.bitboardRepresentation.squareToPosition(i);
      List<Move> availableMoves =
          this.bitboardRepresentation.getAvailableMoves(
              piecePosition.getX(), piecePosition.getY(), false); // TODO: Check this line
      for (Move move : availableMoves) {
        ColoredPiece removedPiece = null;
        if (move.isTake) {
          removedPiece =
              this.bitboardRepresentation.getPieceAt(move.getDest().getX(), move.getDest().getY());
          this.bitboardRepresentation.deletePieceAt(move.getDest().getX(), move.getDest().getY());
        }
        this.bitboardRepresentation.movePiece(move.source, move.dest); // Play move
        boolean isStillCheck = isCheck(color);
        this.bitboardRepresentation.movePiece(move.dest, move.source); // Undo move
        if (move.isTake) {
          this.bitboardRepresentation.addPieceAt(
              move.getDest().getX(), move.getDest().getY(), removedPiece);
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
   * Checks the StaleMate state for the given color
   *
   * @param color The color you want to check StaleMate for
   * @param colorTurnToPlay Player's turn to know if player who potentially moves in check has to
   *     move
   * @return true if color {color} is stalemated. false otherwise.
   */
  public boolean isStaleMate(Color color, Color colorTurnToPlay) {
    if (isCheck(color)) {
      return false;
    }
    Bitboard pieces =
        color == Color.WHITE
            ? this.bitboardRepresentation.getWhiteBoard()
            : this.bitboardRepresentation.getBlackBoard();
    for (Integer i : pieces.getSetBits()) {
      Position piecePosition = this.bitboardRepresentation.squareToPosition(i);
      List<Move> availableMoves =
          this.bitboardRepresentation.getAvailableMoves(
              piecePosition.getX(), piecePosition.getY(), true);
      for (Move move : availableMoves) {
        ColoredPiece removedPiece = null;
        if (move.isTake) {
          removedPiece =
              this.bitboardRepresentation.getPieceAt(move.getDest().getX(), move.getDest().getY());
          this.bitboardRepresentation.deletePieceAt(move.getDest().getX(), move.getDest().getY());
        }
        this.bitboardRepresentation.movePiece(move.source, move.dest); // Play move
        boolean isStillCheck = isCheck(color);
        this.bitboardRepresentation.movePiece(move.dest, move.source); // Undo move
        if (move.isTake) {
          this.bitboardRepresentation.addPieceAt(
              move.getDest().getX(), move.getDest().getY(), removedPiece);
        }
        if (!isStillCheck) {
          DEBUG(LOGGER, color.toString() + " is not stalemate");
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
   * colored Bishops)
   *
   * @return true if a draw by insufficient material is observed
   */
  public boolean isDrawByInsufficientMaterial() {
    DEBUG(LOGGER, "Checking is draw by insufficient material");
    List<Position> posWhiteKing = this.bitboardRepresentation.getKing(true);
    List<Position> posBlackKing = this.bitboardRepresentation.getKing(false);
    if (posWhiteKing.isEmpty() || posBlackKing.isEmpty()) {
      return false;
    }

    // If at least a queen or a rook or a pawn is found on the board then no draw by insufficient
    // material
    List<List<Position>> posListFalseInAllCases = new ArrayList<>();
    posListFalseInAllCases.add(this.bitboardRepresentation.getQueens(true));
    posListFalseInAllCases.add(this.bitboardRepresentation.getRooks(true));
    posListFalseInAllCases.add(this.bitboardRepresentation.getPawns(true));
    posListFalseInAllCases.add(this.bitboardRepresentation.getQueens(false));
    posListFalseInAllCases.add(this.bitboardRepresentation.getRooks(false));
    posListFalseInAllCases.add(this.bitboardRepresentation.getPawns(false));

    for (List<Position> pieceList : posListFalseInAllCases) {
      if (!pieceList.isEmpty()) {
        return false;
      }
    }

    // Get all remaining pieces
    List<Position> posWhiteBishops = this.bitboardRepresentation.getBishops(true);
    List<Position> posBlackBishops = this.bitboardRepresentation.getBishops(false);
    List<Position> posWhiteKnights = this.bitboardRepresentation.getKnights(true);
    List<Position> posBlackKnights = this.bitboardRepresentation.getKnights(false);

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
  public boolean isPawnPromoting(int x, int y, boolean white) {
    DEBUG(LOGGER, "Checking is pawn promoting");
    if (white && y != 7) {
      return false;
    } else if (!white && y != 0) {
      return false;
    } else {
      // White pawns --> 5 and Black pawns --> 11
      Bitboard pawnBitBoard =
          white
              ? this.bitboardRepresentation.getBitboards()[5]
              : this.bitboardRepresentation.getBitboards()[11];
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
  public void promotePawn(int x, int y, boolean white, Piece newPiece) {
    DEBUG(LOGGER, "Promoting pawn at [" + x + ", " + y + "] to " + newPiece);
    ColoredPiece pieceAtPosition = this.bitboardRepresentation.getPieceAt(x, y);
    if (pieceAtPosition.piece != Piece.PAWN
        || pieceAtPosition.color != (white ? Color.WHITE : Color.BLACK)) {
      return;
    }

    int boardIndex = white ? 0 : 6;
    Bitboard newPieceBitBoard = null;
    Bitboard pawnBitboard = this.bitboardRepresentation.getBitboards()[5 + boardIndex];
    switch (newPiece) {
      case KNIGHT:
        newPieceBitBoard = this.bitboardRepresentation.getBitboards()[4 + boardIndex];
        break;
      case BISHOP:
        newPieceBitBoard = this.bitboardRepresentation.getBitboards()[2 + boardIndex];
        break;
      case ROOK:
        newPieceBitBoard = this.bitboardRepresentation.getBitboards()[3 + boardIndex];
        break;
      case QUEEN:
        newPieceBitBoard = this.bitboardRepresentation.getBitboards()[1 + boardIndex];
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
  public boolean isDoublePushPossible(Move move, boolean white) {
    DEBUG(LOGGER, "Checking is double push possible");
    ColoredPiece piece =
        this.bitboardRepresentation.getPieceAt(move.source.getX(), move.source.getY());
    if (white
        && piece.piece == Piece.PAWN
        && move.source.getY() == 1
        && move.dest.getY() == 3
        && move.source.getX() == move.dest.getX()) {
      return ((this.bitboardRepresentation.getPieceAt(move.dest.getX(), move.dest.getY()).piece
              == Piece.EMPTY)
          && (this.bitboardRepresentation.getPieceAt(move.dest.getX(), move.dest.getY() - 1).piece
              == Piece.EMPTY));
    }

    if (!white
        && piece.piece == Piece.PAWN
        && move.source.getY() == 6
        && move.dest.getY() == 4
        && move.source.getX() == move.dest.getX()) {
      return ((this.bitboardRepresentation.getPieceAt(move.dest.getX(), move.dest.getY()).piece
              == Piece.EMPTY)
          && (this.bitboardRepresentation.getPieceAt(move.dest.getX(), move.dest.getY() + 1).piece
              == Piece.EMPTY));
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
    DEBUG(LOGGER, "Checking is en passant");
    ColoredPiece piece =
        this.bitboardRepresentation.getPieceAt(move.source.getX(), move.source.getY());
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

  public void setSquare(ColoredPiece piece, int squareIndex) {
    this.bitboardRepresentation.getBitboards()[BitboardRepresentation.pieces.getFromValue(piece)]
        .setBit(squareIndex);
  }

  protected Bitboard[] getBitboards() {
    return this.bitboardRepresentation.getBitboards();
  }

  /**
   * Method that verifies of a player has enough material to mate. Used for rule loss on time but
   * enemy does not have enough material to mate
   *
   * @param white color of the player we check the material for
   * @return true if {white} has enouhg material to mate. false otherwise
   */
  public boolean hasEnoughMaterialToMate(boolean white) {
    // Pawn can promote
    List<Position> posPawns = this.bitboardRepresentation.getPawns(white);
    if (!posPawns.isEmpty()) {
      return true;
    }
    // Mate with queen(s)
    List<Position> queenPos = this.bitboardRepresentation.getQueens(white);
    if (!queenPos.isEmpty()) {
      return true;
    }
    // Mate with rook(s)
    List<Position> rooksPos = this.bitboardRepresentation.getRooks(white);
    if (!rooksPos.isEmpty()) {
      return true;
    }
    // Mate with bishops
    List<Position> bishopsPos = this.bitboardRepresentation.getBishops(white);
    // Check if at least two bishops are of opposite colors
    if (bishopsPos.size() >= 2) {
      int nbBishopsLightSquares = 0;
      int nbBishopsDarkSquares = 0;
      for (Position posBishop : bishopsPos) {
        if ((posBishop.getX() + posBishop.getY()) % 2 == 0) {
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
    List<Position> knightsPos = this.bitboardRepresentation.getKnights(white);
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
   * @return the list containing the list of current positions for the white pieces
   */
  public List<List<Position>> retrieveWhitePiecesPos() {
    List<List<Position>> whitePositions = new ArrayList<>();

    List<Position> kingPos = this.bitboardRepresentation.getKing(true);
    List<Position> queenPos = this.bitboardRepresentation.getQueens(true);
    List<Position> rookPos = this.bitboardRepresentation.getRooks(true);
    List<Position> bishopPos = this.bitboardRepresentation.getBishops(true);
    List<Position> knightPos = this.bitboardRepresentation.getKnights(true);
    List<Position> pawnsPos = this.bitboardRepresentation.getPawns(true);

    whitePositions.add(kingPos);
    whitePositions.add(queenPos);
    whitePositions.add(rookPos);
    whitePositions.add(bishopPos);
    whitePositions.add(knightPos);
    whitePositions.add(pawnsPos);

    return whitePositions;
  }

  /**
   * @return the list containing the list of current positions for the black pieces
   */
  public List<List<Position>> retrieveBlackPiecesPos() {
    List<List<Position>> blackPositions = new ArrayList<>();

    List<Position> kingPos = this.bitboardRepresentation.getKing(false);
    List<Position> queenPos = this.bitboardRepresentation.getQueens(false);
    List<Position> rookPos = this.bitboardRepresentation.getRooks(false);
    List<Position> bishopPos = this.bitboardRepresentation.getBishops(false);
    List<Position> knightPos = this.bitboardRepresentation.getKnights(false);
    List<Position> pawnsPos = this.bitboardRepresentation.getPawns(false);

    blackPositions.add(kingPos);
    blackPositions.add(queenPos);
    blackPositions.add(rookPos);
    blackPositions.add(bishopPos);
    blackPositions.add(knightPos);
    blackPositions.add(pawnsPos);

    return blackPositions;
  }

  /**
   * @return the list containing the list of initial positions for the white pieces
   */
  public List<List<Position>> retrieveInitialWhitePiecesPos() {
    List<List<Position>> whiteInitPos = new ArrayList<>();

    List<Position> kingPos = List.of(new Position(4, 0));
    List<Position> queenPos = List.of(new Position(3, 0));
    List<Position> rooksPos = List.of(new Position(0, 0), new Position(7, 0));
    List<Position> bishopsPos = List.of(new Position(2, 0), new Position(5, 0));
    List<Position> knightsPos = List.of(new Position(1, 0), new Position(6, 0));
    List<Position> pawnsPos = new ArrayList<>();

    for (int i = 0; i < 8; i++) {
      pawnsPos.add(new Position(i, 1));
    }

    whiteInitPos.add(kingPos);
    whiteInitPos.add(queenPos);
    whiteInitPos.add(rooksPos);
    whiteInitPos.add(bishopsPos);
    whiteInitPos.add(knightsPos);
    whiteInitPos.add(pawnsPos);

    return whiteInitPos;
  }

  /**
   * @return the list containing the list of initial positions for the black pieces
   */
  public List<List<Position>> retrieveInitialBlackPiecesPos() {
    List<List<Position>> blackInitPos = new ArrayList<>();

    List<Position> kingPos = List.of(new Position(4, 7));
    List<Position> queenPos = List.of(new Position(3, 7));
    List<Position> rooksPos = List.of(new Position(0, 7), new Position(7, 7));
    List<Position> bishopsPos = List.of(new Position(2, 7), new Position(5, 7));
    List<Position> knightsPos = List.of(new Position(1, 7), new Position(6, 7));
    List<Position> pawnsPos = new ArrayList<>();

    for (int i = 0; i < 8; i++) {
      pawnsPos.add(new Position(i, 6));
    }

    blackInitPos.add(kingPos);
    blackInitPos.add(queenPos);
    blackInitPos.add(rooksPos);
    blackInitPos.add(bishopsPos);
    blackInitPos.add(knightsPos);
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
  public boolean isCastleMove(ColoredPiece coloredPiece, Position source, Position dest) {
    if (coloredPiece.piece != Piece.KING) {
      return false;
    }
    int deltaX = Math.abs(dest.getX() - source.getX());
    return deltaX == 2
        && ((source.getY() == 0 && dest.getY() == 0) || (source.getY() == 7 && dest.getY() == 7));
  }

  /**
   * Return true if the piece located at sourcePosition is of the same color as the player that has
   * to play a move, false is not, and exception otherwise.
   *
   * @param white the game state for which we want to verify piece ownership
   * @param sourcePosition the position
   * @throws IllegalMoveException If the move is illegal in the current configuration.
   */
  public boolean validatePieceOwnership(boolean white, Position sourcePosition)
      throws IllegalMoveException {
    ColoredPiece pieceAtSource =
        this.bitboardRepresentation.getPieceAt(sourcePosition.getX(), sourcePosition.getY());

    if ((pieceAtSource.color == Color.WHITE && !white)
        || (pieceAtSource.color == Color.BLACK && white)) {
      DEBUG(LOGGER, "Not a " + pieceAtSource.color + " piece at " + sourcePosition);
      return false;
    }
    return true;
  }
}
