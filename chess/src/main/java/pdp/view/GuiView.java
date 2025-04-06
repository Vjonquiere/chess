package pdp.view;

import static pdp.utils.Logging.debug;
import static pdp.utils.Logging.error;
import static pdp.view.gui.themes.ColorTheme.GREY;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pdp.events.EventType;
import pdp.model.Game;
import pdp.utils.Logging;
import pdp.utils.Position;
import pdp.utils.TextGetter;
import pdp.view.gui.ChessMenu;
import pdp.view.gui.ControlPanel;
import pdp.view.gui.GuiLauncher;
import pdp.view.gui.board.Board;
import pdp.view.gui.popups.AiMonitor;
import pdp.view.gui.popups.EndGamePopUp;
import pdp.view.gui.themes.ColorThemeInterface;

/** Base of our graphical interface. */
public class GuiView implements View {
  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(GuiView.class.getName());

  /** Root of the application, all components are places inside. */
  private final BorderPane root;

  /** Stage of the app. */
  private Stage stage;

  /** Scene of the app. */
  private Scene scene;

  /** Representation of the current chess board. */
  private Board board;

  /**
   * Control panel containing the players and their timers, the history, and buttons to modify thr
   * game.
   */
  private ControlPanel controlPanel;

  /**
   * Menu containing the File, Game Options and About menus and also a place to display informative
   * or error messages during the game.
   */
  private ChessMenu menu;

  /** Color theme of the whole app. Grey by default. */
  private static ColorThemeInterface theme = GREY;

  /** Set if the move piece animation has to be done. */
  public static boolean ANIMATION_ENABLED = true;

  /** Boolean to indicate whether the GUI View is initialized or not. */
  private boolean initialized;

  private static AiMonitor WhiteAiMonitor;
  private static AiMonitor BlackAiMonitor;

  private static boolean monitoring = false;

  static {
    Logging.configureLogging(LOGGER);
  }

  public static void toggleMonitoring() {
    monitoring = !monitoring;
    if (WhiteAiMonitor != null && BlackAiMonitor != null && !monitoring) {
      WhiteAiMonitor.hide();
      BlackAiMonitor.hide();
    }
    if (WhiteAiMonitor != null && BlackAiMonitor != null && monitoring) {
      WhiteAiMonitor.show();
      BlackAiMonitor.show();
    }
  }

  public static boolean getMonitoringStatus() {
    return monitoring;
  }

  /**
   * Retrieves the scene of the view. (Used for tests)
   *
   * @return field scene
   */
  public Scene getScene() {
    return scene;
  }

  /**
   * Retrieves the board of the view. (Used for tests)
   *
   * @return field board
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Retrieves the controlPanel of the view. (Used for tests)
   *
   * @return field controlPanel
   */
  public ControlPanel getControlPanel() {
    return controlPanel;
  }

  /**
   * Retrieves the menu of the view. (Used for tests)
   *
   * @return field menu
   */
  public ChessMenu getMenu() {
    return menu;
  }

  /**
   * Retrieves the root of the app, the border pane containing all elements of the UI. Used for
   * tests.
   *
   * @return fiels root.
   */
  public BorderPane getRoot() {
    return root;
  }

  /**
   * Takes the CSS in resources, transforms it with the color theme of the app and applies it to the
   * scene.
   *
   * @param scene scene to apply the CSS over
   */
  public static void applyCss(final Scene scene) {
    String text;
    try {
      final InputStream inputStream =
          GuiView.class.getClassLoader().getResourceAsStream("styles/sample.css");
      if (inputStream == null) {
        throw new FileNotFoundException("CSS file not found in resources.");
      }

      text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      text = text.replace("#000001", theme.getPrimary());
      text = text.replace("#000002", theme.getSecondary());
      text = text.replace("#000003", theme.getTertiary());
      text = text.replace("#000004", theme.getAccent());
      text = text.replace("#000005", theme.getBackground());
      text = text.replace("#000006", theme.getBackground2());
      text = text.replace("#000007", theme.getText());
      text = text.replace("#000008", theme.getTextInverted());
      final File tempFile = File.createTempFile("theme-", ".css");
      tempFile.deleteOnExit();

      try (FileWriter writer = new FileWriter(tempFile)) {
        writer.write(text);
      }

      scene.getStylesheets().clear();
      scene.getStylesheets().add(tempFile.toURI().toString());
    } catch (Exception ex) {
      error(ex.toString());
    }
  }

  /**
   * Retrieves the current color theme of the app.
   *
   * @return field theme
   */
  public static ColorThemeInterface getTheme() {
    return theme;
  }

  /** Defines the new color theme of the app. */
  public static void setTheme(final ColorThemeInterface newTheme) {
    theme = newTheme;
  }

  /**
   * Calls apply CSS to modify the sample css with the theme colors and apply it to some components.
   * Sends a game started event to reload the components of the scene with the new theme.
   */
  public void updateTheme() {
    applyCss(scene);
    onGameEvent(EventType.GAME_STARTED);
  }

  /** Calls the GameS Started event to reload the Scene to apply the language change. */
  public void updateLanguage() {
    onGameEvent(EventType.GAME_STARTED);
  }

  /** Creates a GUI View by creating the root component. */
  public GuiView() {
    root = new BorderPane();
  }

  /**
   * Initializes the components of the view.
   *
   * @param stage Main stage of the Application.
   */
  public void init(final Stage stage) {
    stage.setTitle(TextGetter.getText("title"));
    // root.setCenter(board);
    scene = new Scene(root, 1200, 820);
    applyCss(scene);
    stage.setScene(scene);
    if (board != null) {
      board.setStage(stage);
    }
    stage
        .widthProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              if (board != null) {
                board.buildBoard();
              }
            });
    stage
        .heightProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              if (board != null) {
                board.buildBoard();
              }
            });
    this.stage = stage;
  }

  /** Display the Stage of the JavaFX Application. */
  public void show() {
    this.stage.show();
  }

  /**
   * Starts the GUI view.
   *
   * @return The thread that was started.
   */
  @Override
  public Thread start() {
    final Thread guiThread = new Thread(() -> GuiLauncher.launchGui(this));
    guiThread.start();
    return guiThread;
  }

  /**
   * Handle game events to keep view updated (JavaFx version).
   *
   * @param event Notification sent by the model
   */
  @Override
  public void onGameEvent(final EventType event) {
    debug(LOGGER, "View received event " + event);
    if (!Platform.isFxApplicationThread() && !isInitialized()) {
      debug(LOGGER, "Init GUI thread");
      setInitialized(true);
      Platform.startup(() -> Platform.runLater(() -> this.onGameEvent(event)));
    }
    Platform.runLater(
        () -> {
          Game.getInstance().getViewLock().lock();
          debug(LOGGER, "View handling event " + event);
          try {
            switch (event) {
              case GAME_STARTED:
                root.setStyle("-fx-background-color: " + theme.getBackground() + ";");
                if (board != null) {
                  root.getChildren().remove(board);
                }
                board = new Board(Game.getInstance(), stage);
                root.setLeft(board);
                BorderPane.setAlignment(board, Pos.CENTER_LEFT);
                debug(LOGGER, "Board view initialized");
                if (controlPanel != null) {
                  root.getChildren().remove(controlPanel);
                }
                controlPanel = new ControlPanel(root);
                controlPanel.update(event);
                root.setCenter(controlPanel);
                if (menu != null) {
                  root.getChildren().remove(menu);
                }
                menu = new ChessMenu();
                root.setTop(menu);
                if (Game.getInstance().isWhiteAi()) {
                  menu.displayMessage(TextGetter.getText("guiStartMessageGameStart"), false, true);
                } else {
                  menu.displayMessage(TextGetter.getText("guiStartMessagePlayAMove"), false, true);
                }

                if (WhiteAiMonitor != null) {
                  WhiteAiMonitor.hide();
                  WhiteAiMonitor = null;
                }
                if (BlackAiMonitor != null) {
                  BlackAiMonitor.hide();
                  WhiteAiMonitor = null;
                }
                if (Game.getInstance().isWhiteAi()) {
                  WhiteAiMonitor = new AiMonitor(true);
                }
                if (Game.getInstance().isWhiteAi() && monitoring) {
                  WhiteAiMonitor.show();
                }
                if (Game.getInstance().isBlackAi()) {
                  BlackAiMonitor = new AiMonitor(false);
                  // BlackAiMonitor.show();
                }
                if (Game.getInstance().isBlackAi() && monitoring) {
                  BlackAiMonitor.show();
                }

                break;
              case MOVE_PLAYED:
                if (board != null) {
                  board.updateBoard(ANIMATION_ENABLED);
                }
                if (controlPanel != null) {
                  controlPanel.update(event);
                  if (controlPanel.getHistoryPanel() != null) {
                    controlPanel.getHistoryPanel().updateHistoryPanel();
                  }
                }
                if (Game.getInstance().isWhiteAi()
                    && WhiteAiMonitor != null
                    && WhiteAiMonitor.isShowing()) {
                  WhiteAiMonitor.update();
                }

                if (Game.getInstance().isBlackAi()
                    && BlackAiMonitor != null
                    && BlackAiMonitor.isShowing()) {
                  BlackAiMonitor.update();
                }
                menu.getMessageDisplay().clearPreviousMessage();

                break;
              case DRAW_ACCEPTED,
                  INSUFFICIENT_MATERIAL,
                  OUT_OF_TIME_BLACK,
                  OUT_OF_TIME_WHITE,
                  AI_NOT_ENOUGH_TIME,
                  THREEFOLD_REPETITION,
                  WHITE_RESIGNS,
                  BLACK_RESIGNS,
                  FIFTY_MOVE_RULE,
                  CHECKMATE_BLACK,
                  STALEMATE,
                  CHECKMATE_WHITE:
                EndGamePopUp.show(event);
                break;
              case WIN_WHITE:
                menu.displayMessage(TextGetter.getText("whiteWin"), false, false);
                break;
              case WIN_BLACK:
                menu.displayMessage(TextGetter.getText("blackWin"), false, false);
                break;
              case DRAW:
                menu.displayMessage(TextGetter.getText("onDraw"), false, false);
                break;
              case WHITE_DRAW_PROPOSAL:
                menu.displayMessage(
                    TextGetter.getText("drawProposal", TextGetter.getText("white")), false, false);
                break;
              case BLACK_DRAW_PROPOSAL:
                menu.displayMessage(
                    TextGetter.getText("drawProposal", TextGetter.getText("black")), false, false);
                break;
              case WHITE_UNDRAW:
                menu.displayMessage(
                    TextGetter.getText("cancelDrawProposal", TextGetter.getText("white")),
                    false,
                    false);
                break;
              case BLACK_UNDRAW:
                menu.displayMessage(
                    TextGetter.getText("cancelDrawProposal", TextGetter.getText("black")),
                    false,
                    false);
                break;
              case GAME_SAVED:
                menu.displayMessage(TextGetter.getText("gameSaved"), false, false);
                break;
              case MOVE_UNDO:
                menu.displayMessage(TextGetter.getText("moveUndone"), false, false);
                if (board != null) {
                  board.updateBoard(false);
                }
                if (controlPanel != null) {
                  controlPanel.update(event);
                  if (controlPanel.getHistoryPanel() != null) {
                    controlPanel.getHistoryPanel().updateHistoryPanel();
                  }
                }
                break;
              case WHITE_UNDO_PROPOSAL:
                if (board != null) {
                  board.updateBoard(false);
                }
                if (controlPanel != null) {
                  controlPanel.update(event);
                }
                break;
              case BLACK_UNDO_PROPOSAL:
                if (board != null) {
                  board.updateBoard(false);
                }
                if (controlPanel != null) {
                  controlPanel.update(event);
                }
                break;
              case MOVE_REDO:
                menu.displayMessage(TextGetter.getText("moveRedone"), false, false);
                if (board != null) {
                  board.updateBoard(ANIMATION_ENABLED);
                }
                if (controlPanel != null) {
                  controlPanel.update(event);
                  if (controlPanel.getHistoryPanel() != null) {
                    controlPanel.getHistoryPanel().updateHistoryPanel();
                  }
                }
                break;
              case WHITE_REDO_PROPOSAL:
                break;
              case BLACK_REDO_PROPOSAL:
                break;
              case AI_PLAYING:
                menu.displayMessage(TextGetter.getText("ai_playing"), false, false);
                break;
              case GAME_RESTART:
                menu.displayMessage(TextGetter.getText("gameRestart"), false, false);
                if (board != null) {
                  board.updateBoard(false);
                }
                if (controlPanel != null) {
                  controlPanel.update(event);
                  controlPanel.updateTimersOnce();
                  if (controlPanel.getHistoryPanel() != null) {
                    controlPanel.getHistoryPanel().updateHistoryPanel();
                  }
                }
                break;
              case UPDATE_THEME:
                updateTheme();
                break;
              case UPDATE_LANG:
                updateLanguage();
                break;
              case MOVE_HINT:
                if (board != null) {
                  final List<Integer> hintIntegers =
                      Game.getInstance().getGameState().getHintIntegers();
                  board.setHintSquares(
                      new Position(hintIntegers.get(0), hintIntegers.get(1)),
                      new Position(hintIntegers.get(2), hintIntegers.get(3)));
                }
                break;
              default:
                debug(LOGGER, "Received unknown game event: " + event);
                break;
            }
          } finally {
            if (Game.getInstance().getViewLock().isLocked()) {
              Game.getInstance().getWorkingViewCondition().signal();
              Game.getInstance().getViewLock().unlock();
            }
          }
        });
  }

  /** When an execution is received, displays the error message in the menu. */
  @Override
  public void onErrorEvent(final Exception exception) {
    Platform.runLater(() -> menu.displayMessage(exception.getMessage(), true, false));
  }

  /**
   * Retrieves a boolean to indicates whether the GUI View is initialized.
   *
   * @return true if the view is initialized, false otherwise.
   */
  public boolean isInitialized() {
    return initialized;
  }

  /**
   * Sets the field initialized.
   *
   * @param init true if the view is initialized, false otherwise.
   */
  public void setInitialized(final boolean init) {
    this.initialized = init;
  }
}
