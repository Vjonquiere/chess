package pdp.model.ai;

import static pdp.utils.Logging.DEBUG;

import java.util.HashMap;
import java.util.logging.Logger;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.Board;
import pdp.model.board.ZobristHashing;
import pdp.utils.Logging;

public class Solver {
  private final Logger LOGGER = Logger.getLogger(Solver.class.getName());
  // Zobrist hashing to avoid recomputing the position evaluation for the same boards
  private ZobristHashing zobristHashing = new ZobristHashing();
  private HashMap<Long, Integer> evaluatedBoards;

  AlgorithmType algorithm;
  HeuristicType heuristic = HeuristicType.MATERIAL;
  int depth = 3;
  int time = 500;

  public Solver() {
    Logging.configureLogging(LOGGER);
    evaluatedBoards = new HashMap<>();
  }

  /**
   * Set the algorithm to be used.
   *
   * @param algorithm The algorithm to use.
   */
  public void setAlgorithm(AlgorithmType algorithm) {
    this.algorithm = algorithm;
  }

  /**
   * Set the heuristic to be used.
   *
   * @param heuristic The heuristic to use.
   */
  public void setHeuristic(HeuristicType heuristic) {
    this.heuristic = heuristic;
  }

  /**
   * Set the maximum depth the solver should explore.
   *
   * @param depth The depth to use.
   */
  public void setDepth(int depth) {
    this.depth = depth;
  }

  /**
   * Set the maximum time (in milliseconds) the solver should spend computing a move.
   *
   * @param time The time to use.
   */
  public void setTime(int time) {
    this.time = time;
  }

  /**
   * Evaluates the board based on the chosen heuristic.
   *
   * @param board Current board to evaluate
   * @param isWhite Current player
   * @return score corresponding to the position evaluation of the board.
   */
  public int evaluateBoard(Board board, boolean isWhite) {
    if (board == null) {
      throw new IllegalArgumentException("Board is null");
    }
    int score = 0;
    long hash = zobristHashing.generateHashFromBitboards(board);
    if (evaluatedBoards.containsKey(hash)) {
      return evaluatedBoards.get(hash);
    }
    switch (heuristic) {
      case MATERIAL:
        DEBUG(LOGGER, "Evaluate board position with heuristic type DUMB");
        score = evaluationMaterial(board, isWhite);
        break;
      default:
        throw new IllegalArgumentException("No heuristic is set");
    }
    evaluatedBoards.put(hash, score);
    return score;
  }

  /**
   * Evaluates the board based on the number of pieces still on the board.
   *
   * @param board Current board to evaluate
   * @param isWhite color of the current player
   * @return score of the board
   */
  private int evaluationMaterial(Board board, boolean isWhite) {
    int score = 0;
    if (!(board.getBoardRep() instanceof BitboardRepresentation bitboardRepresentation))
      throw new RuntimeException("Only available for bitboards");
    score +=
        bitboardRepresentation.getPawns(isWhite).size()
            - bitboardRepresentation.getPawns(!isWhite).size();
    score +=
        (bitboardRepresentation.getQueens(isWhite).size()
                - bitboardRepresentation.getQueens(!isWhite).size())
            * 9;
    score +=
        (bitboardRepresentation.getBishops(isWhite).size()
                - bitboardRepresentation.getBishops(!isWhite).size())
            * 3;
    score +=
        (bitboardRepresentation.getKnights(isWhite).size()
                - bitboardRepresentation.getKnights(!isWhite).size())
            * 3;
    score +=
        (bitboardRepresentation.getRooks(isWhite).size()
                - bitboardRepresentation.getRooks(!isWhite).size())
            * 5;
    return score;
  }
}
