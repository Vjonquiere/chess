package pdp.model.ai.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import pdp.model.Game;
import pdp.model.GameAi;
import pdp.model.GameState;
import pdp.model.ai.AiMove;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.model.piece.Color;

/** Algorithm of artificial intelligence Monte Carlo Tree search. */
public class MonteCarloTreeSearch implements SearchAlgorithm {
  /**
   * Solver used for calling the evaluation of the board once the number of iterations is reached or
   * time is up.
   */
  private final Solver solver;

  /** c value. */
  private static final double EXPLORATION_FACTOR = Math.sqrt(2);

  /** Randomizer for the moves. */
  private final Random random = new Random();

  /** Number of times to execute MonteCarloTreeSearch. */
  private final int simulationLimit;

  /**
   * Creates an instance of the MonteCarloTreeSearch algorithm with a given solver and a set number
   * of simulations (number of times to execute the MTCS).
   *
   * @param solver Solver to save into the field.
   * @param nbIterations max number of simulations
   */
  public MonteCarloTreeSearch(final Solver solver, final int nbIterations) {
    this.solver = solver;
    simulationLimit = nbIterations;
  }

  /**
   * Creates an instance of the MonteCarloTreeSearch algorithm with a given solver.
   *
   * @param solver Solver to save into the field.
   */
  public MonteCarloTreeSearch(final Solver solver) {
    this.solver = solver;
    simulationLimit = 150; // 150 by default
  }

  /**
   * Returns the number of iterations to execute MonteCarloTreeSearch.
   *
   * @return The number of iterations
   */
  public int getSimulationLimit() {
    return simulationLimit;
  }

  /**
   * Determines the "best move" using the MonteCarloTreeSearch algorithm.
   *
   * @param game The current game state
   * @param depth Not needed in MCTS.
   * @param player The current player (true for white, false for black)
   * @return The best move for the player
   */
  @Override
  public AiMove findBestMove(final Game game, int depth, boolean player) {
    final GameAi aiGame = GameAi.fromGame(game);
    final GameState gameStateCopy = aiGame.getGameState().getCopy();
    // Give the root a copy of the game state to work on new ones
    final TreeNodeMonteCarlo root = new TreeNodeMonteCarlo(gameStateCopy, null, null);

    // Run MonteCarloTreeSearch for a fixed number of simulations
    for (int i = 0; i < simulationLimit; i++) {
      final TreeNodeMonteCarlo selectedNode = select(root);
      final TreeNodeMonteCarlo expandedNode = expand(aiGame, selectedNode);
      final int simulationResult = simulate(aiGame, expandedNode);
      backpropagate(expandedNode, simulationResult);
    }

    return getBestMove(root);
  }

  /**
   * Assess the resulting position after a simulated sequence of moves.
   *
   * @param state the state of the game
   * @return the result of the simulation. -1 if black wins, 1 if white wins and 0 if draw
   */
  private int evaluateSimulation(final GameState state) {
    // Draw or game not over.
    int res = 0;
    if (state.isGameOver()) {
      if (state.getBoard().getBoardRep().isCheckMate(Color.WHITE)) {
        // Black wins
        res = -1;
      } else if (state.getBoard().getBoardRep().isCheckMate(Color.BLACK)) {
        // White wins
        res = 1;
      }
    }
    return res;
  }

  /**
   * Select the node to explore.
   *
   * @param node the current tree node in the algorithm
   * @return the node that the algorithm selects to explore (based on UCT)
   */
  private TreeNodeMonteCarlo select(TreeNodeMonteCarlo node) {
    while (!node.getChildrenNodes().isEmpty() && node.isFullyExpanded()) {
      if (solver.isSearchStopped()) {
        return node;
      }
      node = node.getChildToExplore(EXPLORATION_FACTOR);
    }
    return node;
  }

  /**
   * Generate a child node for every possible move in the gameState of the given node.
   *
   * @param game the current ongoing game
   * @param node the current node in the algorithm
   * @return the expanded node
   */
  private TreeNodeMonteCarlo expand(final GameAi game, final TreeNodeMonteCarlo node) {
    if (solver.isSearchStopped() || node.getGameState().isGameOver()) {
      // No expansion if game over
      return node;
    }

    final List<Move> possibleMoves =
        node.getGameState()
            .getBoard()
            .getBoardRep()
            .getAllAvailableMoves(node.getGameState().isWhiteTurn());
    for (final Move move : possibleMoves) {
      if (solver.isSearchStopped()) {
        return node;
      }
      try {
        final Move promoteMovemove = AlgorithmHelpers.promoteMove(move);
        final GameState nextState = node.getGameState().getCopy();
        game.playMoveOtherGameState(nextState, promoteMovemove);
        // Add node to tree
        node.addChildToTree(new TreeNodeMonteCarlo(nextState, node, promoteMovemove));
      } catch (Exception e) {
        // Illegal move was caught
        continue;
      }
    }

    if (node.getChildrenNodes().isEmpty()) {
      return node;
    } else {
      return node.getChildrenNodes().get(random.nextInt(node.getChildrenNodes().size()));
    }
  }

  /**
   * Simulate a game randomly by playing moves randomly and assessing the sequence of played moves.
   * Stop when a Terminal State is reached (win, loss, draw) and return the obtained result.
   *
   * @param game the current ongoing game
   * @param node the current node in the algorithm
   * @return the evaluation of the simulated sequence of moves from current node
   */
  private int simulate(final GameAi game, final TreeNodeMonteCarlo node) {
    if (solver.isSearchStopped()) {
      final TreeNodeMonteCarlo parentNode = node.getParentNode();
      if (parentNode == null) {
        return 0;
      }
      return parentNode.getGameState().isWhiteTurn() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    }

    final GameState simulationState = node.getGameState().getCopy();

    while (!simulationState.isGameOver()) {
      if (solver.isSearchStopped()) {
        final TreeNodeMonteCarlo parentNode = node.getParentNode();
        if (parentNode == null) {
          return 0;
        }
        return parentNode.getGameState().isWhiteTurn() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
      }
      final List<Move> availableMoves =
          simulationState
              .getBoard()
              .getBoardRep()
              .getAllAvailableMoves(simulationState.isWhiteTurn());

      if (availableMoves.isEmpty()) {
        break;
      }

      // Filter only legal moves
      final List<Move> legalMoves = new ArrayList<>();
      for (final Move move : availableMoves) {
        if (solver.isSearchStopped()) {
          final TreeNodeMonteCarlo parentNode = node.getParentNode();
          if (parentNode == null) {
            return 0;
          }
          return parentNode.getGameState().isWhiteTurn() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }
        try {
          final Move promotedMove = AlgorithmHelpers.promoteMove(move);
          // Copy GameState and try to play the move to see if move is valid and legal
          final GameState testState = simulationState.getCopy();
          game.playMoveOtherGameState(testState, promotedMove);
          legalMoves.add(promotedMove);
        } catch (Exception ignored) {
          // Caught illegal move, pursue
        }
      }

      if (legalMoves.isEmpty()) {
        break;
      }

      final Move randomMove = selectRandomMove(legalMoves);

      try {
        game.playMoveOtherGameState(simulationState, randomMove);
      } catch (Exception ignored) {
        // Exception occured when trying to play the randomly selected move
      }
    }

    return evaluateSimulation(simulationState);
  }

  /**
   * Selects a random move from the list of available moves.
   *
   * @param moves the list of possible moves
   * @return A randomly chosen move
   */
  private Move selectRandomMove(final List<Move> moves) {
    return moves.get(this.random.nextInt(moves.size()));
  }

  /**
   * Back propagate the obtained result during the algorithm to the root node.
   *
   * @param node the current tree node in the algorithm
   * @param result the obtained result after simulation
   */
  private void backpropagate(TreeNodeMonteCarlo node, final int result) {
    while (node != null) {
      node.incrementNbVisits();
      node.incrementNbWinsBy(result);
      node = node.getParentNode();
      if (solver.isSearchStopped()) {
        break;
      }
    }
  }

  /**
   * Returns the move that's considered best, namely the one that has the highest winrate.
   *
   * @param root the root node in the tree representing the initial game state
   * @return the best computed move based on winrate of the move
   */
  private AiMove getBestMove(final TreeNodeMonteCarlo root) {
    if (root.getChildrenNodes().isEmpty()) {
      return new AiMove(null, 0);
    }

    TreeNodeMonteCarlo bestNode = null;
    int maxVisits = -1;

    // Find the most visited child node
    final List<TreeNodeMonteCarlo> childrenNodes = root.getChildrenNodes();
    for (final TreeNodeMonteCarlo child : childrenNodes) {
      final int visits = child.getNbVisits();
      if (visits > maxVisits) {
        maxVisits = visits;
        bestNode = child;
      }
    }

    if (bestNode == null) {
      return new AiMove(null, 0);
    }

    final Move bestMove = bestNode.getStartingMove();
    final double winRate = (double) bestNode.getNbWins() / bestNode.getNbVisits();

    return new AiMove(bestMove, (int) winRate);
  }
}
