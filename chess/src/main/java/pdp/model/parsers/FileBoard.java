package pdp.model.parsers;

import pdp.model.board.BoardRepresentation;

/**
 * Object that represent a board loaded from a file.
 *
 * @param board The board corresponding to the loaded file.
 * @param isWhiteTurn The current player.
 * @param header The FEN header.
 */
public record FileBoard(BoardRepresentation board, boolean isWhiteTurn, FenHeader header) {}
