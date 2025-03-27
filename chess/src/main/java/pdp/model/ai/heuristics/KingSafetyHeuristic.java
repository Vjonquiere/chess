package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.Board;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

/**
 * Heuristic based on the safety of the king (not in center, pieces around to protect him, checks
 * available).
 */
public class KingSafetyHeuristic implements Heuristic {

  /**
   * Assigns a score to a player according to the safety of his king. Checks: if king is in the
   * center (so more vulnerable), if king has pieces around him to protect him, and there are many
   * checks possible from the enemy onto him.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return score according to the safety of the king
   */
  @Override
  public float evaluate(final Board board, final boolean isWhite) {
    float score = 0;
    score += kingVulnerabilityScore(board, true) - kingVulnerabilityScore(board, false);
    score += kingProtectionScore(board, true) - kingProtectionScore(board, false);
    score += kingSafetyToChecksFromEnemy(board, true) - kingSafetyToChecksFromEnemy(board, false);
    return isWhite ? score : -score;
  }

  /**
   * Penalizes (or not) the king for being in the center (as it makes him more vulnerable).
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a penalty score (negative) if the king is in the center, 0 otherwise
   */
  private float kingVulnerabilityScore(final Board board, final boolean isWhite) {
    float score = 0;

    // Define center area
    final Position posTopLeftCenter = new Position(2, 5);
    final Position posTopRightCenter = new Position(5, 5);
    final Position posDownLeftCenter = new Position(2, 2);
    final Position posDownRightCenter = new Position(5, 2);

    final Position kingPosition = board.getBoardRep().getKing(isWhite).get(0);

    final boolean isKingInCenter =
        kingPosition.x() >= posDownLeftCenter.x()
            && kingPosition.x() <= posTopRightCenter.x()
            && kingPosition.y() >= posDownRightCenter.y()
            && kingPosition.y() <= posTopLeftCenter.y();

    if (isKingInCenter) {
      // Penalize king in the center
      score = -20;
    }

    return score;
  }

  /**
   * Assesses how well the king is protected by friendly pieces.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a positive score if the king has friendly pieces nearby, 0 otherwise
   */
  private float kingProtectionScore(final Board board, final boolean isWhite) {
    float score = 0;
    final BoardRepresentation bitboard = board.getBoardRep();

    final Position kingPos = bitboard.getKing(isWhite).get(0);

    // Squares around the king
    final int[][] directions = {
      {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, /*King pos*/ {0, 1}, {1, -1}, {1, 0}, {1, 1}
    };
    Position newPos;
    for (final int[] dir : directions) {
      final int newX = kingPos.x() + dir[0];
      final int newY = kingPos.y() + dir[1];

      newPos = new Position(newX, newY);

      if (newPos.isValid()) {
        final ColoredPiece piece = bitboard.getPieceAt(newX, newY);
        if (piece.getPiece() != Piece.EMPTY) {
          final Color colorPiece = piece.getColor();
          final boolean white = colorPiece == Color.WHITE;
          if (white == isWhite) {
            // Protection from piece of the same color
            score += 5;
          }
        }
      }
    }

    return score;
  }

  /**
   * Returns a positive or negative score according to the number of possible checks from the enemy.
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a negative score if the king can get checked, positive otherwise
   */
  private float kingSafetyToChecksFromEnemy(final Board board, final boolean isWhite) {
    float score = 0;

    final BoardRepresentation bitboard = board.getBoardRep();

    if (isWhite) {
      // Test for white king
      final List<List<Position>> posBlackPieces = bitboard.retrieveBlackPiecesPos();
      final Position whiteKingPosition = bitboard.getKing(true).get(0);

      for (final List<Position> posList : posBlackPieces) {
        for (final Position posBlackPiece : posList) {
          // Must not be king
          if (bitboard.getPieceAt(posBlackPiece.x(), posBlackPiece.y()).getPiece() != Piece.KING) {
            final List<Move> movesForPiece =
                bitboard.getAvailableMoves(posBlackPiece.x(), posBlackPiece.y(), true);
            for (final Move move : movesForPiece) {
              if (move.getDest().x() == whiteKingPosition.x()
                  && move.getDest().y() == whiteKingPosition.y()) {
                // Check is possible from black
                score -= 30;
              }
            }
          }
        }
      }
    } else {
      // Test for black king
      final List<List<Position>> posWhitePieces = bitboard.retrieveWhitePiecesPos();
      final Position blackKingPosition = bitboard.getKing(false).get(0);

      for (final List<Position> posList : posWhitePieces) {
        for (final Position posWhitePiece : posList) {
          // Must not be king
          if (bitboard.getPieceAt(posWhitePiece.x(), posWhitePiece.y()).getPiece() != Piece.KING) {
            final List<Move> movesForPiece =
                bitboard.getAvailableMoves(posWhitePiece.x(), posWhitePiece.y(), true);
            for (final Move move : movesForPiece) {
              if (move.getDest().x() == blackKingPosition.x()
                  && move.getDest().y() == blackKingPosition.y()) {
                // Check is possible from white
                score -= 30;
              }
            }
          }
        }
      }
    }

    // No checks available from enemy so good score
    if (score == 0) {
      score += 20;
    }

    return score;
  }
}
