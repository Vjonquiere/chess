package pdp.model.parsers;

import pdp.model.board.BoardRepresentation;

public record FileBoard(BoardRepresentation board, boolean isWhiteTurn, FenHeader header) {}
