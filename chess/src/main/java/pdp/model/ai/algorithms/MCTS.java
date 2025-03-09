package pdp.model.ai.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import pdp.model.Game;
import pdp.model.GameState;
import pdp.model.ai.AIMove;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.model.piece.Color;

public class MCTS implements SearchAlgorithm {
  Solver solver;
  private static final double EXPLORATION_FACTOR = Math.sqrt(2); // c value
  private final Random random = new Random(); // Randomizer for the moves
  private final int SIMULATION_LIMIT; // Number of times to execute MCTS

  public MCTS(Solver solver, int nbIterations) {
    this.solver = solver;
    SIMULATION_LIMIT = nbIterations;
  }

  public MCTS(Solver solver) {
    this.solver = solver;
    SIMULATION_LIMIT = 100; // 100 by default
  }

  /**
   * Determines the "best move" using the MCTS algorithm
   *
   * @param game The current game state
   * @param depth The number of moves to look ahead
   * @param player The current player (true for white, false for black)
   * @return The best move for the player
   */
  @Override
  public AIMove findBestMove(Game game, int depth, boolean player) {
    GameState gameStateCopy = game.getGameState().getCopy();
    // Give the root a copy of the game state to work on new ones
    TreeNodeMCTS root = new TreeNodeMCTS(gameStateCopy, null, null);

    // Run MCTS for a fixed number of simulations
    for (int i = 0; i < SIMULATION_LIMIT; i++) {
      TreeNodeMCTS selectedNode = select(root);
      TreeNodeMCTS expandedNode = expand(game, selectedNode);
      int simulationResult = simulate(game, expandedNode);
      backpropagate(game, expandedNode, simulationResult);
    }

    AIMove move = getBestMove(game, root);
    System.out.println("MOVE MCTS : " + move.toString());
    System.out.println("Number of simulations : " + SIMULATION_LIMIT);

    return move;
  }

  /**
   * Assess the resulting position after a simulated sequence of moves
   *
   * @param state the state of the game
   * @return the result of the simulation. -1 if black wins, 1 if white wins and 0 if draw
   */
  private int evaluateSimulation(GameState state) {
    if (state.isGameOver()) {
      if (state.getBoard().getBoardRep().isCheckMate(Color.WHITE)) {
        // Black wins
        return -1;
      }
      if (state.getBoard().getBoardRep().isCheckMate(Color.BLACK)) {
        // White wins
        return 1;
      }
      // Draw
      return 0;
    }
    return 0;
  }

  /**
   * Select the node to explore
   *
   * @param node the current tree node in the algorithm
   * @return the node that the algorithm selects to explore (based on UCT)
   */
  private TreeNodeMCTS select(TreeNodeMCTS node) {
    while (!node.getChildrenNodes().isEmpty() && node.isFullyExpanded()) {
      node = node.getChildToExplore(EXPLORATION_FACTOR);
    }
    return node;
  }

  /**
   * Generate a child node for every possible move in the gameState of the given node
   *
   * @param game the current ongoing game
   * @param node the current node in the algorithm
   * @return the expanded node
   */
  private TreeNodeMCTS expand(Game game, TreeNodeMCTS node) {
    if (node.getGameState().isGameOver()) {
      // No expansion if game over
      return node;
    }

    List<Move> possibleMoves =
        node.getGameState()
            .getBoard()
            .getBoardRep()
            .getAllAvailableMoves(node.getGameState().isWhiteTurn());
    for (Move move : possibleMoves) {
      try {
        move = AlgorithmHelpers.promoteMove(move);
        GameState nextState = node.getGameState().getCopy();
        game.playMoveOtherGameState(nextState, move);
        // Add node to tree
        node.addChildToTree(new TreeNodeMCTS(nextState, node, move));
      } catch (Exception e) {
        // Illegal movewas caught
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
   * Stop when a Terminal State is reached (win, loss, draw) and return the obtained result
   *
   * @param game the current ongoing game
   * @param node the current node in the algorithm
   * @return the evaluation of the simulated sequence of moves from current node
   */
  private int simulate(Game game, TreeNodeMCTS node) {
    GameState simulationState = node.getGameState().getCopy();

    while (!simulationState.isGameOver()) {
      List<Move> availableMoves =
          simulationState
              .getBoard()
              .getBoardRep()
              .getAllAvailableMoves(simulationState.isWhiteTurn());

      if (availableMoves.isEmpty()) {
        break;
      }

      // Filter only legal moves
      List<Move> legalMoves = new ArrayList<>();
      for (Move move : availableMoves) {
        try {
          Move promotedMove = AlgorithmHelpers.promoteMove(move);
          // Copy GameState and try to play the move to see if move is valid and legal
          GameState testState = simulationState.getCopy();
          game.playMoveOtherGameState(testState, promotedMove);
          legalMoves.add(promotedMove);
        } catch (Exception e) {
          // Caught illegal move, pursue
        }
      }

      if (legalMoves.isEmpty()) {
        break;
      }

      Move randomMove = selectRandomMove(legalMoves);

      try {
        game.playMoveOtherGameState(simulationState, randomMove);
      } catch (Exception e) {
        // Exception occured when trying to play the randomly selected move
      }
    }

    return evaluateSimulation(simulationState);
  }

  /**
   * Selects a random move from the list of available moves
   *
   * @param moves the list of possible moves
   * @return A randomly chosen move
   */
  private Move selectRandomMove(List<Move> moves) {
    return moves.get(this.random.nextInt(moves.size()));
  }

  /**
   * Back propagate the obtained result during the algorithm to the root node
   *
   * @param game the current ongoing game
   * @param node the current tree node in the algorithm
   * @param result the obtained result after simulation
   */
  private void backpropagate(Game game, TreeNodeMCTS node, int result) {
    while (node != null) {
      node.incrementNbVisits();
      node.incrementNbWinsBy(result);
      node = node.getParentNode();
    }
  }

  /**
   * Returns the move that's considered best, namely the one that has the highest winrate
   *
   * @param game the current ongoing game
   * @param root the root node in the tree representing the initial game state
   * @return the best computed move based on winrate of the move
   */
  private AIMove getBestMove(Game game, TreeNodeMCTS root) {
    if (root.getChildrenNodes().isEmpty()) {
      return new AIMove(null, 0);
    }

    TreeNodeMCTS bestNode = null;
    int maxVisits = -1;

    // Find the most visited child node
    List<TreeNodeMCTS> childrenNodes = root.getChildrenNodes();
    for (TreeNodeMCTS child : childrenNodes) {
      int visits = child.getNbVisits();
      if (visits > maxVisits) {
        maxVisits = visits;
        bestNode = child;
      }
    }

    if (bestNode == null) {
      return new AIMove(null, 0);
    }

    Move bestMove = bestNode.getStartingMove();
    double winRate = (double) bestNode.getNbWins() / bestNode.getNbVisits();

    return new AIMove(bestMove, (int) winRate);
  }
}
