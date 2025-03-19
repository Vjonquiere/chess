package pdp.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import pdp.GameInitializer;
import pdp.model.Game;
import pdp.model.ai.Solver;

public class Contest {
  private String filePath;
  private InputStream inputStream;
  private HashMap<OptionType, String> options;

  public Contest(String path) {
    this.filePath = path;
    this.inputStream = new ByteArrayInputStream(path.getBytes());
  }

  /**
   * @return the input stream for the file used to load game and play AI move.
   */
  public InputStream getInputStream() {
    return this.inputStream;
  }

  /**
   * @return the path of the file used to load game and play AI move.
   */
  public String getFilePath() {
    return this.filePath;
  }

  /**
   * Loads and initializes the game from the given file.
   *
   * @return
   */
  private Game loadGame() {
    Game game = GameInitializer.initialize(options);
    return game;
  }

  /**
   * Tries to play AI move with Solver in the game.
   *
   * @param game The loaded and initialized game.
   */
  private void playAIMoveOnGame(Game game) {
    Solver solver =
        game.getGameState().isWhiteTurn() ? game.getWhiteSolver() : game.getBlackSolver();
    solver.playAIMove(game);
  }

  /** Saves the game (file) after AI move was played. */
  private void saveContestedGame(Game game) {
    game.saveGame(getFilePath());
  }

  /** Loads, initializes game from file, plays AI move on it and saves the files. */
  public void contestGame() {
    Game game = loadGame();
    playAIMoveOnGame(game);
    saveContestedGame(game);
  }
}
