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
  private static final int SIMULATION_LIMIT = 45; // Number of simulations

  public MCTS(Solver solver) {
    this.solver = solver;
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

    int nbAlgoExec = 0;

    // Run MCTS for a fixed number of simulations
    for (int i = 0; i < SIMULATION_LIMIT; i++) {
      TreeNodeMCTS selectedNode = select(root);
      TreeNodeMCTS expandedNode = expand(game, selectedNode);
      int simulationResult = simulate(game, expandedNode);
      System.out.println("Here is the result after simulation : " + simulationResult);
      System.out.println("Here is the result after simulation : " + simulationResult);
      System.out.println("Here is the result after simulation : " + simulationResult);
      System.out.println("Here is the result after simulation : " + simulationResult);
      System.out.println("Here is the result after simulation : " + simulationResult);
      System.out.println("Here is the result after simulation : " + simulationResult);
      System.out.println("Here is the result after simulation : " + simulationResult);
      System.out.println("Here is the result after simulation : " + simulationResult);
      System.out.println("Here is the result after simulation : " + simulationResult);
      System.out.println("Here is the result after simulation : " + simulationResult);
      System.out.println("Here is the result after simulation : " + simulationResult);
      System.out.println("Here is the result after simulation : " + simulationResult);
      backpropagate(game, expandedNode, simulationResult);
      nbAlgoExec++;
    }

    AIMove move = getBestMove(game, root);
    System.out.println("MOVE : " + move.toString());
    System.out.println("MOVE : " + move.toString());
    System.out.println("MOVE : " + move.toString());
    System.out.println("MOVE : " + move.toString());
    System.out.println("MOVE : " + move.toString());
    System.out.println("MOVE : " + move.toString());
    System.out.println("MOVE : " + move.toString());

    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
    System.out.println(nbAlgoExec);
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
      System.out.println(
          "Here is the possible move : " + move.getSource().toString() + move.getDest().toString());
      try {
        move = AlgorithmHelpers.promoteMove(move);
        GameState nextState = node.getGameState().getCopy();
        game.playMove(nextState, move);
        // Add node to tree
        node.addChildToTree(new TreeNodeMCTS(nextState, node, move));
        System.out.println("SUCCESS !!!!!!!!!!!!!!!!!!!!!!!");
      } catch (Exception e) {
        // Illegal movewas caught
        System.out.println("-------------------------------");
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

    /*
    while (!simulationState.isGameOver()) {
      System.out.println("====================");
      List<Move> availableMoves =
          simulationState
              .getBoard()
              .getBoardRep()
              .getAllAvailableMoves(simulationState.isWhiteTurn());

      System.out.println("Available moves: " + availableMoves);

      if (availableMoves.isEmpty()) {
        // No more legal moves so end
        break;
      }
      System.out.println("111111111111111111111111111111");

      Move randomMove = selectRandomMove(availableMoves);
      try {
        randomMove = AlgorithmHelpers.promoteMove(randomMove);
        System.out.println("PLAYER TURN BEFORE MOVE IS WHITE : " + simulationState.isWhiteTurn());
        System.out.println(
            "PLAYER TURN BEFORE MOVE IS WHITE : " + simulationState.getBoard().isWhite);
        game.playMove(simulationState, randomMove);
        System.out.println("PLAYER TURN BEFORE MOVE IS WHITE: " + simulationState.isWhiteTurn());
        System.out.println(
            "PLAYER TURN BEFORE MOVE IS WHITE : " + simulationState.getBoard().isWhite);
      } catch (Exception e) {
        System.out.println("Illegal move detected: " + randomMove.toString());
        availableMoves.remove(randomMove); // Remove bad move
        if (availableMoves.isEmpty()) {
          break; // Stop if no legal moves left
        }
        continue;
      }

      System.out.println("WHILE LOOPING !");
    }*/

    while (!simulationState.isGameOver()) {
      System.out.println("====================");

      List<Move> availableMoves =
          simulationState
              .getBoard()
              .getBoardRep()
              .getAllAvailableMoves(simulationState.isWhiteTurn());

      if (availableMoves.isEmpty()) {
        System.out.println("No available moves, stopping simulation.");
        break;
      }

      // Filter only legal moves
      List<Move> legalMoves = new ArrayList<>();
      for (Move move : availableMoves) {
        try {
          Move promotedMove = AlgorithmHelpers.promoteMove(move);
          GameState testState = simulationState.getCopy();
          game.playMove(testState, promotedMove);
          legalMoves.add(promotedMove);
        } catch (Exception e) {
          // Move was illegal, do nothing
        }
      }

      if (legalMoves.isEmpty()) {
        System.out.println("No legal moves left, stopping simulation.");
        break;
      }

      Move randomMove = selectRandomMove(legalMoves);

      try {
        game.playMove(simulationState, randomMove);
      } catch (Exception e) {
        System.out.println("Unexpected error while playing move: " + randomMove);
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
    System.out.println("BACK");
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

      System.out.println(
          "Move: "
              + child.getStartingMove()
              + " | Visits: "
              + visits
              + " | Wins: "
              + child.getNbWins());
      System.out.println(
          "Move: "
              + child.getStartingMove()
              + " | Visits: "
              + visits
              + " | Wins: "
              + child.getNbWins());
      System.out.println(
          "Move: "
              + child.getStartingMove()
              + " | Visits: "
              + visits
              + " | Wins: "
              + child.getNbWins());
      System.out.println(
          "Move: "
              + child.getStartingMove()
              + " | Visits: "
              + visits
              + " | Wins: "
              + child.getNbWins());
      System.out.println(
          "Move: "
              + child.getStartingMove()
              + " | Visits: "
              + visits
              + " | Wins: "
              + child.getNbWins());
      System.out.println(
          "Move: "
              + child.getStartingMove()
              + " | Visits: "
              + visits
              + " | Wins: "
              + child.getNbWins());
      System.out.println(
          "Move: "
              + child.getStartingMove()
              + " | Visits: "
              + visits
              + " | Wins: "
              + child.getNbWins());
      System.out.println(
          "Move: "
              + child.getStartingMove()
              + " | Visits: "
              + visits
              + " | Wins: "
              + child.getNbWins());
      System.out.println(
          "Move: "
              + child.getStartingMove()
              + " | Visits: "
              + visits
              + " | Wins: "
              + child.getNbWins());
      System.out.println(
          "Move: "
              + child.getStartingMove()
              + " | Visits: "
              + visits
              + " | Wins: "
              + child.getNbWins());
      System.out.println(
          "Move: "
              + child.getStartingMove()
              + " | Visits: "
              + visits
              + " | Wins: "
              + child.getNbWins());

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

    System.out.println("Best move selected: " + bestMove + " | Win rate: " + winRate);
    System.out.println("Best move selected: " + bestMove + " | Win rate: " + winRate);
    System.out.println("Best move selected: " + bestMove + " | Win rate: " + winRate);
    System.out.println("Best move selected: " + bestMove + " | Win rate: " + winRate);
    System.out.println("Best move selected: " + bestMove + " | Win rate: " + winRate);
    System.out.println("Best move selected: " + bestMove + " | Win rate: " + winRate);
    System.out.println("Best move selected: " + bestMove + " | Win rate: " + winRate);
    System.out.println("Best move selected: " + bestMove + " | Win rate: " + winRate);
    System.out.println("Best move selected: " + bestMove + " | Win rate: " + winRate);
    System.out.println("Best move selected: " + bestMove + " | Win rate: " + winRate);
    System.out.println("Best move selected: " + bestMove + " | Win rate: " + winRate);
    System.out.println("Best move selected: " + bestMove + " | Win rate: " + winRate);
    System.out.println("Best move selected: " + bestMove + " | Win rate: " + winRate);

    return new AIMove(bestMove, (int) winRate);
  }
}
