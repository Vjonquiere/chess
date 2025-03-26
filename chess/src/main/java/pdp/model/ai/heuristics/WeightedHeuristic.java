package pdp.model.ai.heuristics;

/**
 * Heuristic with a weight to adjust composite heuristics.
 *
 * @param heuristic heuristic to weight.
 * @param weight weight to add to the heuristic.
 */
public record WeightedHeuristic(Heuristic heuristic, float weight) {}
