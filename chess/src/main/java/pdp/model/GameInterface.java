package pdp.model;

import java.util.List;
import java.util.Map;
import pdp.events.Subject;
import pdp.model.ai.Solver;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.model.board.ZobristHashing;
import pdp.model.history.History;
import pdp.utils.OptionType;
import pdp.utils.Timer;

public abstract class GameInterface extends Subject {
  public abstract Map<OptionType, String> getOptions();

  public abstract void playMove(Move move);

  public abstract boolean isWhiteTurn();

  public abstract boolean isCurrentPlayerAi();

  public abstract boolean isWhiteAi();

  public abstract boolean isBlackAi();

  public abstract List<Integer> getHintIntegers();

  public abstract String getGameRepresentation();

  public abstract Timer getTimer(final boolean isWhite);

  public abstract Solver getBlackSolver();

  public abstract Solver getWhiteSolver();

  public abstract void lockView();

  public abstract void unlockView();

  public abstract boolean isViewLocked();

  public abstract void signalWorkingViewCondition();

  public abstract void restartGame();

  public abstract void saveGame(String filepath);

  public abstract void startAi();

  public abstract BoardRepresentation getBoard();

  public abstract History getHistory();

  public abstract GameState getGameState();

  public abstract Map<Long, Integer> getStateCount();

  public abstract ZobristHashing getZobristHasher();

  public abstract void previousState();

  public abstract void nextState();

  public abstract boolean isOver();
}
