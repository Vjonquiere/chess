package pdp.model.ai.heuristics;

import pdp.model.board.Board;

public interface Heuristic {
  int evaluate(Board board, boolean isWhite);
}
