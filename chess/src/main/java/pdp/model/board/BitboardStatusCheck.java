package pdp.model.board;

import java.util.List;
import java.util.logging.Logger;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Logging;
import pdp.utils.Position;

public class BitboardStatusCheck {
  private static final Logger LOGGER = Logger.getLogger(BitboardStatusCheck.class.getName());
  private BitboardRepresentation bitboardRepresentation;

  public BitboardStatusCheck(BitboardRepresentation bitboardRepresentation) {
    this.bitboardRepresentation = bitboardRepresentation;
  }

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Checks if queens are off the board. Method used to detect endgames
   *
   * @return true if queens are off the board. false otherwise
   */
  public boolean queensOffTheBoard() {
    return this.bitboardRepresentation.getQueens(true).size() == 0
        && this.bitboardRepresentation.getQueens(false).size() == 0;
  }

  /**
   * Checks the progress of pawns in the game for a specific color.
   *
   * @param isWhite true for white pawns, false for black pawns
   * @return true if the majority of pawns for the given color are past the middle of the board.
   */
  public boolean pawnsHaveProgressed(boolean isWhite) {
    List<Position> pawns = this.bitboardRepresentation.getPawns(isWhite);
    if (pawns.isEmpty()) {
      return false;
    }

    final double factorAdvancedPawns = 2.0 / 3.0;
    final int middleRankWhite = 3;
    final int middleRankBlack = 4;

    int advancedPawns = 0;

    for (Position pos : pawns) {
      if (isWhite && pos.getY() >= middleRankWhite) {
        advancedPawns++;
      } else if (!isWhite && pos.getY() <= middleRankBlack) {
        advancedPawns++;
      }
    }

    double ratio = (double) advancedPawns / pawns.size();
    return ratio >= factorAdvancedPawns;
  }

  /**
   * Checks if the kings on the board are active. Method used to detect endgames
   *
   * @return true if kings are somewhat active. false otherwise
   */
  public boolean areKingsActive() {
    int nbMovesConsideringKingActive = 4;

    Position blackKingPos = this.bitboardRepresentation.getKing(false).get(0);
    Position whiteKingPos = this.bitboardRepresentation.getKing(true).get(0);

    ColoredPiece blackKing =
        this.bitboardRepresentation.getPieceAt(blackKingPos.getX(), blackKingPos.getY());
    ColoredPiece whiteKing =
        this.bitboardRepresentation.getPieceAt(whiteKingPos.getX(), whiteKingPos.getY());

    Bitboard unreachableSquaresBlack =
        blackKing.color == Color.WHITE
            ? this.bitboardRepresentation.getWhiteBoard()
            : this.bitboardRepresentation.getBlackBoard();
    unreachableSquaresBlack.clearBit(blackKingPos.getX() % 8 + blackKingPos.getY() * 8);

    Bitboard unreachableSquaresWhite =
        whiteKing.color == Color.WHITE
            ? this.bitboardRepresentation.getWhiteBoard()
            : this.bitboardRepresentation.getBlackBoard();
    unreachableSquaresWhite.clearBit(whiteKingPos.getX() % 8 + whiteKingPos.getY() * 8);

    List<Move> blackKingMoves =
        this.bitboardRepresentation.getKingMoves(
            blackKingPos,
            unreachableSquaresBlack,
            this.bitboardRepresentation.getWhiteBoard(),
            blackKing);
    List<Move> whiteKingMoves =
        this.bitboardRepresentation.getKingMoves(
            whiteKingPos,
            unreachableSquaresWhite,
            this.bitboardRepresentation.getBlackBoard(),
            whiteKing);

    return blackKingMoves.size() >= nbMovesConsideringKingActive
        && whiteKingMoves.size() >= nbMovesConsideringKingActive;
  }

  /**
   * Checks if castle (long or short in parameter) for one side is possible or not. No need to fetch
   * king position because if king has moved, then boolean attributes for castling rights are false
   *
   * @param color the color of the player we want to test castle for
   * @param shortCastle boolean value to indicate if we're looking for the short castle right or
   *     long castle right
   * @return true if castle {shortCastle} is possible for player of Color {color}. false otherwise
   */
  public boolean canCastle(
      Color color,
      boolean shortCastle,
      boolean whiteShortCastle,
      boolean whiteLongCastle,
      boolean blackShortCastle,
      boolean blackLongCastle) {
    if (color == Color.WHITE) {
      if (shortCastle && !whiteShortCastle) {
        return false;
      }
      if (!shortCastle && !whiteLongCastle) {
        return false;
      }

      Position f1Square = new Position(5, 0);
      Position g1Square = new Position(6, 0);

      Position d1Square = new Position(3, 0);
      Position c1Square = new Position(2, 0);
      Position b1Square = new Position(1, 0);

      if (shortCastle) {
        if ((this.bitboardRepresentation.getPieceAt(f1Square.getX(), f1Square.getY()).piece
                != Piece.EMPTY)
            || (this.bitboardRepresentation.getPieceAt(g1Square.getX(), g1Square.getY()).piece
                != Piece.EMPTY)) {
          return false;
        }
        // Squares are empty so now ensure king is not in check and does not move through check
        if (this.bitboardRepresentation.isCheck(Color.WHITE)
            || this.bitboardRepresentation.isAttacked(5, 0, Color.BLACK)
            || this.bitboardRepresentation.isAttacked(6, 0, Color.BLACK)) {
          return false;
        }
      } else {
        if ((this.bitboardRepresentation.getPieceAt(d1Square.getX(), d1Square.getY()).piece
                != Piece.EMPTY)
            || (this.bitboardRepresentation.getPieceAt(c1Square.getX(), c1Square.getY()).piece
                != Piece.EMPTY)
            || (this.bitboardRepresentation.getPieceAt(b1Square.getX(), b1Square.getY()).piece
                != Piece.EMPTY)) {
          return false;
        }
        // Squares are empty so now ensure king is not in check and does not move through check
        if (this.bitboardRepresentation.isCheck(Color.WHITE)
            || this.bitboardRepresentation.isAttacked(3, 0, Color.BLACK)
            || this.bitboardRepresentation.isAttacked(2, 0, Color.BLACK)) {
          return false;
        }
      }
      return true;
    } else {
      if (shortCastle && !blackShortCastle) {
        return false;
      }
      if (!shortCastle && !blackLongCastle) {
        return false;
      }

      Position f8Square = new Position(5, 7);
      Position g8Square = new Position(6, 7);

      Position d8Square = new Position(3, 7);
      Position c8Square = new Position(2, 7);
      Position b8Square = new Position(1, 7);

      if (shortCastle) {
        if ((this.bitboardRepresentation.getPieceAt(f8Square.getX(), f8Square.getY()).piece
                != Piece.EMPTY)
            || (this.bitboardRepresentation.getPieceAt(g8Square.getX(), g8Square.getY()).piece
                != Piece.EMPTY)) {
          return false;
        }
        // Squares are empty so now ensure king is not in check and does not move through check
        if (this.bitboardRepresentation.isCheck(Color.BLACK)
            || this.bitboardRepresentation.isAttacked(5, 7, Color.WHITE)
            || this.bitboardRepresentation.isAttacked(6, 7, Color.WHITE)) {
          return false;
        }
      } else {
        if ((this.bitboardRepresentation.getPieceAt(d8Square.getX(), d8Square.getY()).piece
                != Piece.EMPTY)
            || (this.bitboardRepresentation.getPieceAt(c8Square.getX(), c8Square.getY()).piece
                != Piece.EMPTY)
            || (this.bitboardRepresentation.getPieceAt(b8Square.getX(), b8Square.getY()).piece
                != Piece.EMPTY)) {
          return false;
        }
        // Squares are empty so now ensure king is not in check and does not move through check
        if (this.bitboardRepresentation.isCheck(Color.BLACK)
            || this.bitboardRepresentation.isAttacked(3, 7, Color.WHITE)
            || this.bitboardRepresentation.isAttacked(2, 7, Color.WHITE)) {
          return false;
        }
      }
      return true;
    }
  }

  /**
   * Checks if the Game is in an end game phase. Used to know when to switch heuristics.
   *
   * @return true if we're in an endgame (according to the chosen criterias)
   */
  public boolean isEndGamePhase(int fullTurn, boolean white) {
    int nbRequiredConditions = 4;
    int nbFilledConditions = 0;

    int halfNbPieces = 16;
    int nbPlayedMovesBeforeEndGame = 25;
    int nbPossibleMoveInEndGame = 25;

    // Queens are off the board
    if (this.bitboardRepresentation.queensOffTheBoard()) {
      nbFilledConditions++;
    }
    // Number of pieces remaining
    if (this.bitboardRepresentation.nbPiecesRemaining() <= halfNbPieces) {
      nbFilledConditions++;
    }
    // King activity
    if (this.bitboardRepresentation.areKingsActive()) {
      nbFilledConditions++;
    }
    // Number of played moves
    if (fullTurn >= nbPlayedMovesBeforeEndGame) {
      nbFilledConditions++;
    }
    // Number of possible Moves

    int nbMovesWhite;
    int nbMovesBlack;

    if (this.bitboardRepresentation instanceof BitboardRepresentation) {
      nbMovesWhite =
          ((BitboardRepresentation) this.bitboardRepresentation)
              .getColorMoveBitboard(true)
              .bitCount();
      nbMovesBlack =
          ((BitboardRepresentation) this.bitboardRepresentation)
              .getColorMoveBitboard(false)
              .bitCount();
    } else {
      nbMovesWhite = this.bitboardRepresentation.getAllAvailableMoves(true).size();
      nbMovesBlack = this.bitboardRepresentation.getAllAvailableMoves(false).size();
    }
    if (nbMovesWhite + nbMovesBlack <= nbPossibleMoveInEndGame) {
      nbFilledConditions++;
    }
    // Pawns progresses on the board
    if (this.bitboardRepresentation.pawnsHaveProgressed(white)) {
      nbFilledConditions++;
    }

    return nbFilledConditions >= nbRequiredConditions;
  }
}
