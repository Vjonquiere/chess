package pdp.model.ai;

import pdp.model.board.Move;

/**
 * Represents a move in the game along with its evaluation score. It is used in some AI algorithms
 * to compare different possible moves and determine the best one based on their scores.
 *
 * @param move the move considered in the game
 * @param score evaluation obtained after playing this move in the game
 */
public record AiMove(Move move, int score) {}
