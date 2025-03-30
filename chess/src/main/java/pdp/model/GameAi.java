package pdp.model;

import static pdp.utils.Logging.debug;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import pdp.exceptions.IllegalMoveException;
import pdp.model.board.CastlingMove;
import pdp.model.board.Move;
import pdp.model.board.PromoteMove;
import pdp.model.board.ZobristHashing;
import pdp.model.history.History;
import pdp.model.history.HistoryState;
import pdp.utils.Logging;
import pdp.utils.Position;

/** Specific implementation of game for AI players. */
public final class GameAi extends GameAbstract {
  private static final Logger LOGGER = Logger.getLogger(GameAi.class.getName());

  static {
    Logging.configureLogging(LOGGER);
  }

  private GameAi(
      final GameState gameState,
      final History history,
      final HashMap<Long, Integer> stateCount,
      final ZobristHashing zobristHashing) {
    super(gameState, history, stateCount, zobristHashing);
  }

  /**
   * Tries to play the given move on the game.
   *
   * @param move The move to be executed.
   * @throws IllegalMoveException If the move is not legal.
   */
  @Override
  public void playMove(final Move move) {
    final Position sourcePosition = new Position(move.getSource().x(), move.getSource().y());
    final Position destPosition = new Position(move.getDest().x(), move.getDest().y());
    debug(LOGGER, "Trying to play move [" + sourcePosition + ", " + destPosition + "]");

    if (!super.validatePieceOwnership(super.getGameState(), sourcePosition)) {
      throw new IllegalMoveException(move.toString());
    }
    super.validatePromotionMove(move);

    final List<Move> availableMoves =
        super.getGameState().getBoard().getAvailableMoves(sourcePosition);
    final Optional<Move> classicalMove = move.isMoveClassical(availableMoves);

    final Move moveToProcess;
    if (classicalMove.isPresent()) {
      moveToProcess = classicalMove.get();
      super.processMove(super.getGameState(), moveToProcess);
    } else {
      throw new IllegalMoveException(move.toString());
    }

    this.updateGameStateAfterMove(moveToProcess);
  }

  /**
   * Updates the game state after a move is played.
   *
   * <p>The game state is updated by:
   *
   * <ul>
   *   <li>Incrementing the full turn number if the move was made by white.
   *   <li>Adding the move to the history.
   *   <li>Switching the current player turn.
   *   <li>Updating the board player.
   *   <li>Updating the simplified zobrist hashing.
   *   <li>Checking for threefold repetition.
   *   <li>Checking the game status, which may end the game.
   *   <li>Notifying observers that a move has been played.
   * </ul>
   */
  private void updateGameStateAfterMove(final Move move) {
    if (super.getGameState().isWhiteTurn()) {
      super.getGameState().incrementsFullTurn();
    }

    super.getGameState().switchPlayerTurn();
    if (move instanceof CastlingMove || move instanceof PromoteMove) {
      super.getGameState()
          .setSimplifiedZobristHashing(
              super.getZobristHasher()
                  .updateSimplifiedHashFromBitboards(
                      super.getGameState().getSimplifiedZobristHashing(), getBoard(), move));
    } else {
      super.getGameState()
          .setSimplifiedZobristHashing(
              super.getZobristHasher().generateSimplifiedHashFromBitboards(getBoard()));
    }

    debug(LOGGER, "Checking threefold repetition...");
    final boolean threefoldRep =
        super.addStateToCount(super.getGameState().getSimplifiedZobristHashing());

    if (threefoldRep) {
      super.getGameState().activateThreefold();
    }

    debug(LOGGER, "Checking game status...");
    super.getGameState().checkGameStatus();

    super.getHistory().addMove(new HistoryState(move, super.getGameState().getCopy()));
  }

  /**
   * Tries to play the given move on the game for the game state in parameter.
   *
   * @param gameState the game state for which we want the move to occur
   * @param move The move to be executed
   * @throws IllegalMoveException If the move is not legal
   */
  public void playMoveOtherGameState(final GameState gameState, final Move move) {

    final Position sourcePosition = new Position(move.getSource().x(), move.getSource().y());
    final Position destPosition = new Position(move.getDest().x(), move.getDest().y());
    debug(LOGGER, "Trying to play move [" + sourcePosition + ", " + destPosition + "]");

    if (!validatePieceOwnership(gameState, sourcePosition)) {
      throw new IllegalMoveException(move.toString());
    }
    validatePromotionMove(move);

    final List<Move> availableMoves = gameState.getBoard().getAvailableMoves(sourcePosition);
    final Optional<Move> classicalMove = move.isMoveClassical(availableMoves);

    if (classicalMove.isPresent()) {
      processMove(gameState, classicalMove.get());
    } else {
      throw new IllegalMoveException(move.toString());
    }
    updateOtherGameStateAfterMove(gameState);
  }

  /**
   * Method used for MonteCarloTreeSearch simulation that processes gameState copies. Updates the
   * game state in parameter (supposed to be copy) after a move is played.
   *
   * <p>The provided game state is updated by:
   *
   * <ul>
   *   <li>Incrementing the full turn number if the move was made by white.
   *   <li>Switching the current player turn.
   *   <li>Updating the board player.
   *   <li>Checking the game status, which may end the game.
   * </ul>
   */
  private void updateOtherGameStateAfterMove(final GameState gameState) {
    if (gameState.isWhiteTurn()) {
      gameState.incrementsFullTurn();
    }

    gameState.switchPlayerTurn();

    debug(LOGGER, "Checking game status...");
    gameState.checkGameStatus();
  }

  /**
   * Retrieves a copy of the current GameAI.
   *
   * @return Instance of game AI
   */
  public GameAi copy() {
    final History history = new History();
    history.addMove(
        new HistoryState(
            new Move(new Position(-1, -1), new Position(-1, -1)), this.getGameState().getCopy()));

    final ZobristHashing zobristHashing = new ZobristHashing(this.getZobristHasher());

    return new GameAi(
        super.getGameState().getCopy(),
        history,
        new HashMap<>(super.getStateCount()),
        zobristHashing);
  }

  /**
   * Creates a GameAI from a given Game.
   *
   * @param game game to transform into a GameAI
   * @return a gameAI from the given game
   */
  public static GameAi fromGame(final Game game) {
    final History history = new History();
    history.addMove(
        new HistoryState(
            new Move(new Position(-1, -1), new Position(-1, -1)), game.getGameState().getCopy()));

    final GameState gameState = game.getGameState().getCopy();

    final HashMap<Long, Integer> stateCount = new HashMap<>(game.getStateCount());

    final ZobristHashing zobristHashing = new ZobristHashing(game.getZobristHasher());

    return new GameAi(gameState, history, stateCount, zobristHashing);
  }
}
