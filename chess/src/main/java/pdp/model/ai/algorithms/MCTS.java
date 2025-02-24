package pdp.model.ai.algorithms;

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

  public MCTS(Solver solver) {
    this.solver = solver;
  }

  /**
   * Determines the best move using the AlphaBeta algorithm
   *
   * @param game The current game state
   * @param depth The number of moves to look ahead
   * @param player The current player (true for white, false for black)
   * @return The best move for the player
   */
  @Override
  public AIMove findBestMove(Game game, int depth, boolean player) {
    return null;
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
   * @return the node that the algorithm select to explore (based on UCT)
   */
  private TreeNodeMCTS select(TreeNodeMCTS node) {
    while (!node.getChildrenNodes().isEmpty()) {
      node = node.getChildToExplore(EXPLORATION_FACTOR);
    }
    return node;
  }

  /**
   * Generate child nodes until we reach a Terminal State
   *
   * @param game the current ongoing game
   * @param node the current node in the algorithm
   * @return
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
        game.playMove(move);
        // Add node to tree
        node.getChildrenNodes().add(new TreeNodeMCTS(nextState, node));
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
   * Simulate a game randomly by playing moves randomly and assessing the sequence of played moves
   *
   * @param game the current ongoing game
   * @param node the current node in the algorithm
   * @return the evaluation of the simulated sequence of moves
   */
  private int simulate(Game game, TreeNodeMCTS node) {
    GameState simulatedState = node.getGameState().getCopy();

    while (!simulatedState.isGameOver()) {
      List<Move> availableMoves =
          simulatedState
              .getBoard()
              .getBoardRep()
              .getAllAvailableMoves(simulatedState.isWhiteTurn());

      if (availableMoves.isEmpty()) {
        // No more legal moves so end
        break;
      }

      Move randomMove = selectRandomMove(availableMoves);
      try {
        randomMove = AlgorithmHelpers.promoteMove(randomMove);
        game.playMove(randomMove);
      } catch (Exception e) {
        // Illegal move was caught
        continue;
      }
      simulatedState.getBoard().makeMove(randomMove);
    }

    return evaluateSimulation(simulatedState);
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
   * Back propagate the obtained result during the algorithm
   *
   * @param node the current tree node in the algorithm
   * @param result the obtained result after simulation
   */
  private void backpropagate(TreeNodeMCTS node, int result) {
    while (node != null) {
      node.incrementNbVisits();
      node.incrementNbWinsBy(result);
      node = node.getParentNode();
    }
  }
}
