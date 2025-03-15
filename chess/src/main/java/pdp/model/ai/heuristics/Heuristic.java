package pdp.model.ai.heuristics;

import pdp.model.board.Board;

public interface Heuristic {
  float evaluate(Board board, boolean isWhite);
}
