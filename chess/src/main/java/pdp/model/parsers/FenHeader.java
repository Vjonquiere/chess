package pdp.model.parsers;

import pdp.utils.Position;

/**
 * Record to represent game attributes in FEN like format.
 *
 * @param whiteKingCastling The white king castling status.
 * @param whiteQueenCastling The white queen castling status.
 * @param blackKingCastling The black king castling status.
 * @param blackQueenCastling The black queen castling status.
 * @param enPassant The en passant position (if available).
 * @param fiftyMoveRule The current position in fifty move rule.
 * @param playedMoves The number of played move in the game.
 */
public record FenHeader(
    boolean whiteKingCastling,
    boolean whiteQueenCastling,
    boolean blackKingCastling,
    boolean blackQueenCastling,
    Position enPassant,
    int fiftyMoveRule,
    int playedMoves) {}
