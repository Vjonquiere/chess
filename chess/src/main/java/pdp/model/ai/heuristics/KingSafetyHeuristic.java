package pdp.model.ai.heuristics;

import java.util.List;
import pdp.model.board.Board;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

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
  public int evaluate(Board board, boolean isWhite) {
    int score = 0;
    score += kingVulnerabilityScore(board, true) - kingVulnerabilityScore(board, false);
    score += kingProtectionScore(board, true) - kingProtectionScore(board, false);
    score += kingSafetyToChecksFromEnemy(board, true) - kingSafetyToChecksFromEnemy(board, false);
    return isWhite ? score : -score;
  }

  /**
   * Penalizes (or not) the king for being in the center (as it makes him more vulnerable)
   *
   * @param board the board of the game
   * @param isWhite true if white, fahislse otherwise
   * @return a penalty score (negative) if the king is in the center, 0 otherwise
   */
  private int kingVulnerabilityScore(Board board, boolean isWhite) {
    int score = 0;

    // Define center area
    Position posTopLeftCenter = new Position(2, 5);
    Position posTopRightCenter = new Position(5, 5);
    Position posDownLeftCenter = new Position(2, 2);
    Position posDownRightCenter = new Position(5, 2);

    Position kingPosition = board.getBoardRep().getKing(isWhite).get(0);

    boolean isKingInCenter =
        kingPosition.getX() >= posDownLeftCenter.getX()
            && kingPosition.getX() <= posTopRightCenter.getX()
            && kingPosition.getY() >= posDownRightCenter.getY()
            && kingPosition.getY() <= posTopLeftCenter.getY();

    if (isKingInCenter) {
      // Penalize king in the center
      score = -20;
    }

    return score;
  }

  /**
   * Assesses how well the king is protected by friendly pieces
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a positive score if the king has friendly pieces nearby, 0 otherwise
   */
  private int kingProtectionScore(Board board, boolean isWhite) {
    int score = 0;
    BoardRepresentation bitboard = board.getBoardRep();

    Position kingPos = bitboard.getKing(isWhite).get(0);

    // Squares around the king
    int[][] directions = {
      {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, /*King pos*/ {0, 1}, {1, -1}, {1, 0}, {1, 1}
    };

    for (int[] dir : directions) {
      int newX = kingPos.getX() + dir[0];
      int newY = kingPos.getY() + dir[1];

      Position newPos = new Position(newX, newY);

      if (newPos.isValid()) {
        ColoredPiece piece = bitboard.getPieceAt(newX, newY);
        if (piece.piece != Piece.EMPTY) {
          Color colorPiece = piece.color;
          boolean white = colorPiece == Color.WHITE;
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
   * Returns a positive or negative score according to the number of possible checks from the enemy
   *
   * @param board the board of the game
   * @param isWhite true if white, false otherwise
   * @return a negative score if the king can get checked, positive otherwise
   */
  private int kingSafetyToChecksFromEnemy(Board board, boolean isWhite) {
    int score = 0;

    BoardRepresentation bitboard = board.getBoardRep();

    if (isWhite) {
      // Test for white king
      List<List<Position>> posBlackPieces = bitboard.retrieveBlackPiecesPos();
      Position whiteKingPosition = bitboard.getKing(true).get(0);

      for (List<Position> posList : posBlackPieces) {
        for (Position posBlackPiece : posList) {
          // Must not be king
          if (bitboard.getPieceAt(posBlackPiece.getX(), posBlackPiece.getY()).piece != Piece.KING) {
            List<Move> movesForPiece =
                bitboard.getAvailableMoves(posBlackPiece.getX(), posBlackPiece.getY(), true);
            for (Move move : movesForPiece) {
              if (move.getDest().getX() == whiteKingPosition.getX()
                  && move.getDest().getY() == whiteKingPosition.getY()) {
                // Check is possible from black
                score -= 30;
              }
            }
          }
        }
      }
    } else {
      // Test for black king
      List<List<Position>> posWhitePieces = bitboard.retrieveWhitePiecesPos();
      Position blackKingPosition = bitboard.getKing(false).get(0);

      for (List<Position> posList : posWhitePieces) {
        for (Position posWhitePiece : posList) {
          // Must not be king
          if (bitboard.getPieceAt(posWhitePiece.getX(), posWhitePiece.getY()).piece != Piece.KING) {
            List<Move> movesForPiece =
                bitboard.getAvailableMoves(posWhitePiece.getX(), posWhitePiece.getY(), true);
            for (Move move : movesForPiece) {
              if (move.getDest().getX() == blackKingPosition.getX()
                  && move.getDest().getY() == blackKingPosition.getY()) {
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

  @Override
  public boolean isThreefoldImpact() {
    return false;
  }
}
