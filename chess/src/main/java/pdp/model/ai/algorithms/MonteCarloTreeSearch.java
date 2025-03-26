package pdp.model.ai.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import pdp.model.Game;
import pdp.model.GameAi;
import pdp.model.GameState;
import pdp.model.ai.AiMove;
import pdp.model.ai.Solver;
import pdp.model.board.Board;
import pdp.model.board.Move;
import pdp.model.piece.Color;

/** Algorithm of artificial intelligence Monte Carlo Tree search. */
public class MonteCarloTreeSearch implements SearchAlgorithm {
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
  public MonteCarloTreeSearch(Solver solver, int nbIterations) {
    this.solver = solver;
    simulationLimit = nbIterations;
  }

  /**
   * Creates an instance of the MonteCarloTreeSearch algorithm with a given solver.
   *
   * @param solver Solver to save into the field.
   */
  public MonteCarloTreeSearch(Solver solver) {
    this.solver = solver;
    simulationLimit = 100; // 100 by default
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
   * @param depth The number of moves to look ahead
   * @param player The current player (true for white, false for black)
   * @return The best move for the player
   */
  @Override
  public AiMove findBestMove(Game game, int depth, boolean player) {
    GameAi aiGame = GameAi.fromGame(game);
    GameState gameStateCopy = aiGame.getGameState().getCopy();
    // Give the root a copy of the game state to work on new ones
    TreeNodeMonteCarlo root = new TreeNodeMonteCarlo(gameStateCopy, null, null);

    // Run MonteCarloTreeSearch for a fixed number of simulations
    for (int i = 0; i < simulationLimit; i++) {
      TreeNodeMonteCarlo selectedNode = select(root);
      TreeNodeMonteCarlo expandedNode = expand(aiGame, selectedNode);
      int simulationResult = simulate(aiGame, expandedNode);
      backpropagate(aiGame, expandedNode, simulationResult);
    }

    AiMove move = getBestMove(aiGame, root);

    return move;
  }

  /**
   * Assess the resulting position after a simulated sequence of moves.
   *
   * @param state the state of the game
   * @return the result of the simulation. -1 if black wins, 1 if white wins and 0 if draw
   */
  private int evaluateSimulation(GameState state) {
    if (state.isGameOver()) {
      if (state
          .getBoard()
          .getBoardRep()
          .isCheckMate(
              Color.WHITE,
              state.getBoard().getEnPassantPos(),
              state.getBoard().isLastMoveDoublePush(),
              state.getBoard().isWhiteLongCastle(),
              state.getBoard().isWhiteShortCastle(),
              state.getBoard().isBlackLongCastle(),
              state.getBoard().isBlackShortCastle())) {
        // Black wins
        return -1;
      }
      if (state
          .getBoard()
          .getBoardRep()
          .isCheckMate(
              Color.BLACK,
              state.getBoard().getEnPassantPos(),
              state.getBoard().isLastMoveDoublePush(),
              state.getBoard().isWhiteLongCastle(),
              state.getBoard().isWhiteShortCastle(),
              state.getBoard().isBlackLongCastle(),
              state.getBoard().isBlackShortCastle())) {
        // White wins
        return 1;
      }
      // Draw
      return 0;
    }
    return 0;
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
  private TreeNodeMonteCarlo expand(GameAi game, TreeNodeMonteCarlo node) {
    if (solver.isSearchStopped()) {
      return node;
    }
    if (node.getGameState().isGameOver()) {
      // No expansion if game over
      return node;
    }

    Board board = node.getGameState().getBoard();
    List<Move> possibleMoves =
        node.getGameState()
            .getBoard()
            .getBoardRep()
            .getAllAvailableMoves(
                node.getGameState().isWhiteTurn(),
                board.getEnPassantPos(),
                board.isLastMoveDoublePush(),
                board.isWhiteLongCastle(),
                board.isWhiteShortCastle(),
                board.isBlackLongCastle(),
                board.isBlackShortCastle());
    for (Move move : possibleMoves) {
      if (solver.isSearchStopped()) {
        return node;
      }
      try {
        move = AlgorithmHelpers.promoteMove(move);
        GameState nextState = node.getGameState().getCopy();
        game.playMoveOtherGameState(nextState, move);
        // Add node to tree
        node.addChildToTree(new TreeNodeMonteCarlo(nextState, node, move));
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
  private int simulate(GameAi game, TreeNodeMonteCarlo node) {
    if (solver.isSearchStopped()) {
      TreeNodeMonteCarlo parentNode = node.getParentNode();
      if (parentNode == null) {
        return 0;
      }
      return parentNode.getGameState().isWhiteTurn() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    }

    GameState simulationState = node.getGameState().getCopy();

    while (!simulationState.isGameOver()) {
      if (solver.isSearchStopped()) {
        TreeNodeMonteCarlo parentNode = node.getParentNode();
        if (parentNode == null) {
          return 0;
        }
        return parentNode.getGameState().isWhiteTurn() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
      }

      Board board = simulationState.getBoard();
      List<Move> availableMoves =
          simulationState
              .getBoard()
              .getBoardRep()
              .getAllAvailableMoves(
                  simulationState.isWhiteTurn(),
                  board.getEnPassantPos(),
                  board.isLastMoveDoublePush(),
                  board.isWhiteLongCastle(),
                  board.isWhiteShortCastle(),
                  board.isBlackLongCastle(),
                  board.isBlackShortCastle());

      if (availableMoves.isEmpty()) {
        break;
      }

      // Filter only legal moves
      List<Move> legalMoves = new ArrayList<>();
      for (Move move : availableMoves) {
        if (solver.isSearchStopped()) {
          TreeNodeMonteCarlo parentNode = node.getParentNode();
          if (parentNode == null) {
            return 0;
          }
          return parentNode.getGameState().isWhiteTurn() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }
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
   * Selects a random move from the list of available moves.
   *
   * @param moves the list of possible moves
   * @return A randomly chosen move
   */
  private Move selectRandomMove(List<Move> moves) {
    return moves.get(this.random.nextInt(moves.size()));
  }

  /**
   * Back propagate the obtained result during the algorithm to the root node.
   *
   * @param game the current ongoing game
   * @param node the current tree node in the algorithm
   * @param result the obtained result after simulation
   */
  private void backpropagate(GameAi game, TreeNodeMonteCarlo node, int result) {
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
   * @param game the current ongoing game
   * @param root the root node in the tree representing the initial game state
   * @return the best computed move based on winrate of the move
   */
  private AiMove getBestMove(GameAi game, TreeNodeMonteCarlo root) {
    if (root.getChildrenNodes().isEmpty()) {
      return new AiMove(null, 0);
    }

    TreeNodeMonteCarlo bestNode = null;
    int maxVisits = -1;

    // Find the most visited child node
    List<TreeNodeMonteCarlo> childrenNodes = root.getChildrenNodes();
    for (TreeNodeMonteCarlo child : childrenNodes) {
      int visits = child.getNbVisits();
      if (visits > maxVisits) {
        maxVisits = visits;
        bestNode = child;
      }
    }

    if (bestNode == null) {
      return new AiMove(null, 0);
    }

    Move bestMove = bestNode.getStartingMove();
    double winRate = (double) bestNode.getNbWins() / bestNode.getNbVisits();

    return new AiMove(bestMove, (int) winRate);
  }
}
