package pdp.model.parsers;

import pdp.utils.Position;

public record FenHeader(
    boolean whiteKingCastling,
    boolean whiteQueenCastling,
    boolean blackKingCastling,
    boolean blackQueenCastling,
    Position enPassant,
    int fiftyMoveRule,
    int playedMoves) {}
