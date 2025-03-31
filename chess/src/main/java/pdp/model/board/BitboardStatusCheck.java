package pdp.model.board;

import java.util.List;
import java.util.logging.Logger;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Logging;
import pdp.utils.Position;

/** Utility class to remove complexity for BitboardRepresentation. */
public final class BitboardStatusCheck {
  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(BitboardStatusCheck.class.getName());

  /** Private constructor to avoid instantiation. */
  private BitboardStatusCheck() {
    throw new UnsupportedOperationException("Cannot instantiate utility class");
  }

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Checks if queens are off the board. Method used to detect endgames.
   *
   * @return true if queens are off the board. false otherwise
   */
  public static boolean queensOffTheBoard(final BitboardRepresentation bitboardRepresentation) {
    return bitboardRepresentation.getQueens(true).isEmpty()
        && bitboardRepresentation.getQueens(false).isEmpty();
  }

  /**
   * Checks the progress of pawns in the game for a specific color.
   *
   * @param isWhite true for white pawns, false for black pawns
   * @return true if the majority of pawns for the given color are past the middle of the board.
   */
  public static boolean pawnsHaveProgressed(
      final boolean isWhite, final BitboardRepresentation bitboardRepresentation) {
    final List<Position> pawns = bitboardRepresentation.getPawns(isWhite);
    if (pawns.isEmpty()) {
      return false;
    }

    final double factorAdvancedPawns = 2.0 / 3.0;
    final int middleRankWhite = 3;
    final int middleRankBlack = 4;

    int advancedPawns = 0;

    for (final Position pos : pawns) {
      if (isWhite && pos.y() >= middleRankWhite) {
        advancedPawns++;
      } else if (!isWhite && pos.y() <= middleRankBlack) {
        advancedPawns++;
      }
    }

    final double ratio = (double) advancedPawns / pawns.size();
    return ratio >= factorAdvancedPawns;
  }

  /**
   * Checks if the kings on the board are active. Method used to detect endgames.
   *
   * @return true if kings are somewhat active. false otherwise
   */
  public static boolean areKingsActive(final BitboardRepresentation bitboardRepresentation) {
    final int nbMovesConsideringKingActive = 4;

    final Position blackKingPos = bitboardRepresentation.getKing(false).get(0);
    final Position whiteKingPos = bitboardRepresentation.getKing(true).get(0);

    final ColoredPiece blackKing =
        bitboardRepresentation.getPieceAt(blackKingPos.x(), blackKingPos.y());
    final ColoredPiece whiteKing =
        bitboardRepresentation.getPieceAt(whiteKingPos.x(), whiteKingPos.y());

    final Bitboard unreachableSquaresBlack =
        blackKing.getColor() == Color.WHITE
            ? bitboardRepresentation.getWhiteBoard()
            : bitboardRepresentation.getBlackBoard();
    unreachableSquaresBlack.clearBit(blackKingPos.x() % 8 + blackKingPos.y() * 8);

    final Bitboard unreachableSquaresWhite =
        whiteKing.getColor() == Color.WHITE
            ? bitboardRepresentation.getWhiteBoard()
            : bitboardRepresentation.getBlackBoard();
    unreachableSquaresWhite.clearBit(whiteKingPos.x() % 8 + whiteKingPos.y() * 8);

    final List<Move> blackKingMoves =
        bitboardRepresentation.getKingMoves(
            blackKingPos,
            unreachableSquaresBlack,
            bitboardRepresentation.getWhiteBoard(),
            blackKing);
    final List<Move> whiteKingMoves =
        bitboardRepresentation.getKingMoves(
            whiteKingPos,
            unreachableSquaresWhite,
            bitboardRepresentation.getBlackBoard(),
            whiteKing);

    return blackKingMoves.size() >= nbMovesConsideringKingActive
        && whiteKingMoves.size() >= nbMovesConsideringKingActive;
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
  public static boolean canCastle(
      final Color color,
      final boolean shortCastle,
      final boolean whiteShortCastle,
      final boolean whiteLongCastle,
      final boolean blackShortCastle,
      final boolean blackLongCastle,
      final BitboardRepresentation bitboardRepresentation) {
    if (color == Color.WHITE) {
      if (shortCastle && !whiteShortCastle) {
        return false;
      }
      if (!shortCastle && !whiteLongCastle) {
        return false;
      }

      final Position f1Square = new Position(5, 0);
      final Position g1Square = new Position(6, 0);

      final Position d1Square = new Position(3, 0);
      final Position c1Square = new Position(2, 0);
      final Position b1Square = new Position(1, 0);

      if (shortCastle) {
        if ((bitboardRepresentation.getPieceAt(f1Square.x(), f1Square.y()).getPiece()
                != Piece.EMPTY)
            || (bitboardRepresentation.getPieceAt(g1Square.x(), g1Square.y()).getPiece()
                != Piece.EMPTY)) {
          return false;
        }
        // Squares are empty so now ensure king is not in check and does not move through check
        return !bitboardRepresentation.isCheck(Color.WHITE)
            && !bitboardRepresentation.isAttacked(5, 0, Color.BLACK)
            && !bitboardRepresentation.isAttacked(6, 0, Color.BLACK);
      } else {
        if ((bitboardRepresentation.getPieceAt(d1Square.x(), d1Square.y()).getPiece()
                != Piece.EMPTY)
            || (bitboardRepresentation.getPieceAt(c1Square.x(), c1Square.y()).getPiece()
                != Piece.EMPTY)
            || (bitboardRepresentation.getPieceAt(b1Square.x(), b1Square.y()).getPiece()
                != Piece.EMPTY)) {
          return false;
        }
        // Squares are empty so now ensure king is not in check and does not move through check
        return !bitboardRepresentation.isCheck(Color.WHITE)
            && !bitboardRepresentation.isAttacked(3, 0, Color.BLACK)
            && !bitboardRepresentation.isAttacked(2, 0, Color.BLACK);
      }
    } else {
      if (shortCastle && !blackShortCastle) {
        return false;
      }
      if (!shortCastle && !blackLongCastle) {
        return false;
      }

      final Position f8Square = new Position(5, 7);
      final Position g8Square = new Position(6, 7);

      final Position d8Square = new Position(3, 7);
      final Position c8Square = new Position(2, 7);
      final Position b8Square = new Position(1, 7);

      if (shortCastle) {
        if ((bitboardRepresentation.getPieceAt(f8Square.x(), f8Square.y()).getPiece()
                != Piece.EMPTY)
            || (bitboardRepresentation.getPieceAt(g8Square.x(), g8Square.y()).getPiece()
                != Piece.EMPTY)) {
          return false;
        }
        // Squares are empty so now ensure king is not in check and does not move through check
        return !bitboardRepresentation.isCheck(Color.BLACK)
            && !bitboardRepresentation.isAttacked(5, 7, Color.WHITE)
            && !bitboardRepresentation.isAttacked(6, 7, Color.WHITE);
      } else {
        if ((bitboardRepresentation.getPieceAt(d8Square.x(), d8Square.y()).getPiece()
                != Piece.EMPTY)
            || (bitboardRepresentation.getPieceAt(c8Square.x(), c8Square.y()).getPiece()
                != Piece.EMPTY)
            || (bitboardRepresentation.getPieceAt(b8Square.x(), b8Square.y()).getPiece()
                != Piece.EMPTY)) {
          return false;
        }
        // Squares are empty so now ensure king is not in check and does not move through check
        return !bitboardRepresentation.isCheck(Color.BLACK)
            && !bitboardRepresentation.isAttacked(3, 7, Color.WHITE)
            && !bitboardRepresentation.isAttacked(2, 7, Color.WHITE);
      }
    }
  }

  /**
   * Checks if the Game is in an end game phase. Used to know when to switch heuristics.
   *
   * @return true if we're in an endgame (according to the chosen criteria)
   */
  public static boolean isEndGamePhase(
      final int fullTurn,
      final boolean white,
      final BitboardRepresentation bitboardRepresentation) {
    final int nbRequiredConditions = 4;
    int nbFilledConditions = 0;

    final int halfNbPieces = 16;
    final int nbPlayedMovesBeforeEndGame = 25;
    final int nbPossibleMoveInEndGame = 25;

    // Queens are off the board
    if (bitboardRepresentation.queensOffTheBoard()) {
      nbFilledConditions++;
    }
    // Number of pieces remaining
    if (bitboardRepresentation.nbPiecesRemaining() <= halfNbPieces) {
      nbFilledConditions++;
    }
    // King activity
    if (bitboardRepresentation.areKingsActive()) {
      nbFilledConditions++;
    }
    // Number of played moves
    if (fullTurn >= nbPlayedMovesBeforeEndGame) {
      nbFilledConditions++;
    }
    // Number of possible Moves

    final int nbMovesWhite = bitboardRepresentation.getColorMoveBitboard(true).bitCount();
    final int nbMovesBlack = bitboardRepresentation.getColorMoveBitboard(false).bitCount();

    if (nbMovesWhite + nbMovesBlack <= nbPossibleMoveInEndGame) {
      nbFilledConditions++;
    }
    // Pawns progresses on the board
    if (bitboardRepresentation.pawnsHaveProgressed(white)) {
      nbFilledConditions++;
    }

    return nbFilledConditions >= nbRequiredConditions;
  }
}
