package pdp.model.ai.heuristics;

import java.util.ArrayList;
import java.util.List;
import pdp.model.board.Board;

public class ShannonBasic implements Heuristic {
  List<Heuristic> heuristics;

  public ShannonBasic() {
    heuristics = new ArrayList<>();
    heuristics.add(new MobilityHeuristic());
    heuristics.add(new MaterialHeuristic());
    // heuristics.add(new badPawnsHeuristics());
  }

  @Override
  public int evaluate(Board board, boolean isWhite) {
    int score = 0;
    for (Heuristic heuristic : heuristics) {
      score += heuristic.evaluate(board, isWhite);
    }
    return score;
  }
}
