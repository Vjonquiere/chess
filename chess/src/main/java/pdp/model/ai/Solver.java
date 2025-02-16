package pdp.model.ai;

import static pdp.utils.Logging.DEBUG;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import pdp.model.Game;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.Board;
import pdp.model.board.Move;
import pdp.model.board.ZobristHashing;
import pdp.utils.Logging;

public class Solver {
  private final Logger LOGGER = Logger.getLogger(Solver.class.getName());
  // Zobrist hashing to avoid recomputing the position evaluation for the same boards
  private ZobristHashing zobristHashing = new ZobristHashing();
  private HashMap<Long, Integer> evaluatedBoards;

  AlgorithmType algorithm = AlgorithmType.MINIMAX;
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

  public void playAIMove(Game game) {
    AIMove bestMove = null;
    switch (algorithm) {
      case MINIMAX:
        DEBUG(LOGGER, "Using Minimax algorithm");
        bestMove = maxMin(game, depth, game.getBoard().isWhite);
        break;
      case ALPHA_BETA:
        DEBUG(LOGGER, "Using Alpha Beta algorithm");
        break;
      case MCTS:
        DEBUG(LOGGER, "Using Monte Carlo Tree Search algorithm");
        break;
      default:
        throw new IllegalArgumentException("No algorithm is set");
    }
    game.playMove(bestMove.move());
  }

  /**
   * Assigns to best-move the move that maximizes the player's score. Part of Minimax algorithm
   *
   * @param game current game
   * @param depth number of moves to be played
   * @param player current player
   * @return score of the best move for the player
   */
  public AIMove maxMin(Game game, int depth, boolean player) {
    if (depth == 0 || game.isOver()) {
      return new AIMove(null, evaluateBoard(game.getBoard(), player));
    }
    AIMove bestMove = new AIMove(null, Integer.MIN_VALUE);
    List<Move> moves = game.getBoard().getBoardRep().getAllAvailableMoves(player);
    for (Move move : moves) {
      game.playMove(move);
      AIMove currMove = minMax(game, depth - 1, !player);
      if (currMove.score() > bestMove.score()) {
        bestMove = new AIMove(move, currMove.score());
      }
      game.previousState();
    }
    return bestMove;
  }

  /**
   * Assigns to best-move the move that minimizes the player's score. Part of Minimax algorithm
   *
   * @param game current game
   * @param depth number of moves to be played
   * @param player current player
   * @return score of the best move for the player
   */
  public AIMove minMax(Game game, int depth, boolean player) {
    if (depth == 0 || game.isOver()) {
      return new AIMove(null, evaluateBoard(game.getBoard(), player));
    }
    AIMove bestMove = new AIMove(null, Integer.MAX_VALUE);
    List<Move> moves = game.getBoard().getBoardRep().getAllAvailableMoves(player);
    for (Move move : moves) {
      game.playMove(move);
      AIMove currMove = maxMin(game, depth - 1, !player);
      if (currMove.score() < bestMove.score()) {
        bestMove = new AIMove(move, currMove.score());
      }
      game.previousState();
    }
    return bestMove;
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
        DEBUG(LOGGER, "Evaluate board position with heuristic type MATERIAL");
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
