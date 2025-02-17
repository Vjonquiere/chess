package pdp.model.ai.heuristics;

import java.util.ArrayList;
import java.util.List;
import pdp.model.board.Board;

public class EndGameHeuristic implements Heuristic {
  private List<Heuristic> heuristics;

  public EndGameHeuristic() {
    heuristics = new ArrayList<>();
    heuristics.add(new KingSafetyHeuristic());
    heuristics.add(new MaterialHeuristic());
    heuristics.add(new KingActivityHeuristic());
    heuristics.add(new BadPawnsHeuristic());
    heuristics.add(new PromotionHeuristic());
    // Piece activity --> include legal moves
    // Number of checks
    // Pawn structure
    // King opposition ??
    // Same colored bishops
  }

  @Override
  public int evaluate(Board board, boolean isWhite) {
    int score = 0;
    for (Heuristic heuristic : heuristics) {
      score += heuristic.evaluate(board, isWhite);
    }
    return score;
  }

  public void addHeuristic(Heuristic heuristic) {
    heuristics.add(heuristic);
  }
}
