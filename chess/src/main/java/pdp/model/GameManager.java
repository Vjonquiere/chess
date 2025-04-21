package pdp.model;

public class GameManager {
  private static GameInterface instance;

  private GameManager() {
    // Private constructor
  }

  public static GameInterface getInstance() {
    if (instance == null) {
      throw new IllegalStateException("Game instance has not been initialized.");
    }
    return instance;
  }

  public static void setInstance(GameInterface game) {
    instance = game;
  }

  public static boolean isInstanceInitialized() {
    return instance != null;
  }
}
