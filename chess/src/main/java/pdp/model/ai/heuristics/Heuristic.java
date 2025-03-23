package pdp.model.ai.heuristics;

import pdp.model.board.Board;

/** Common interface for heuristics,to be able to change the solver's heuristic with ease. */
public interface Heuristic {
  float evaluate(Board board, boolean isWhite);
}
