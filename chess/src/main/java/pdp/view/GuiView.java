package pdp.view;

import static pdp.utils.Logging.debug;
import static pdp.view.gui.themes.ColorTheme.SIMPLE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pdp.events.EventType;
import pdp.model.Game;
import pdp.model.parsers.BoardFileParser;
import pdp.utils.Logging;
import pdp.utils.Position;
import pdp.utils.TextGetter;
import pdp.view.gui.ChessMenu;
import pdp.view.gui.ControlPanel;
import pdp.view.gui.GuiLauncher;
import pdp.view.gui.board.Board;
import pdp.view.gui.popups.EndGamePopUp;
import pdp.view.gui.themes.ColorTheme;

/** Base of our graphical interface. */
public class GuiView implements View {
  private static final Logger LOGGER = Logger.getLogger(GuiView.class.getName());
  private BorderPane root;
  private Stage stage;
  private Scene scene;
  private Board board;
  private ControlPanel controlPanel;
  private ChessMenu menu;
  private static ColorTheme theme = SIMPLE;
  private boolean init = false;

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Takes the CSS in resources, transforms it with the color theme of the app and applies it to the
   * scene.
   *
   * @param scene scene to apply the CSS over
   */
  public static void applyCss(Scene scene) {
    String text;
    String path = "";
    try {
      // TODO: allow user to give his css file
      text = new BoardFileParser().readFile(path);
    } catch (FileNotFoundException e) {
      try {
        URL filePath = GuiView.class.getClassLoader().getResource("styles/sample.css");
        text = new BoardFileParser().readFile(filePath.getPath());
        text = text.replace("#000001", theme.getPrimary());
        text = text.replace("#000002", theme.getSecondary());
        text = text.replace("#000003", theme.getTertiary());
        text = text.replace("#000004", theme.getAccent());
        text = text.replace("#000005", theme.getBackground());
        text = text.replace("#000006", theme.getBackground2());
        text = text.replace("#000007", theme.getText());
        text = text.replace("#000008", theme.getTextInverted());
        File tempFile = File.createTempFile("theme-", ".css");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
          writer.write(text);
        }

        scene.getStylesheets().clear();
        scene.getStylesheets().add(tempFile.toURI().toString());
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  public static ColorTheme getTheme() {
    return theme;
  }

  public static void setTheme(ColorTheme newTheme) {
    theme = newTheme;
  }

  public void updateTheme() {
    applyCss(scene);
    onGameEvent(EventType.GAME_STARTED);
  }

  public void updateLanguage() {
    onGameEvent(EventType.GAME_STARTED);
  }

  public GuiView() {
    root = new BorderPane();
  }

  /**
   * Initializes the components of the view.
   *
   * @param stage Main stage of the Application.
   */
  public void init(Stage stage) {
    System.out.println(stage);
    stage.setTitle(TextGetter.getText("title"));
    // root.setCenter(board);
    scene = new Scene(root, 1200, 820);
    applyCss(scene);
    stage.setScene(scene);
    if (board != null) {
      board.setStage(stage);
    }
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
    Thread guiThread = new Thread(() -> GuiLauncher.launchGui(this));
    guiThread.start();
    return guiThread;
  }

  /**
   * Handle game events to keep view updated (JavaFx version).
   *
   * @param event Notification sent by the model
   */
  @Override
  public void onGameEvent(EventType event) {
    debug(LOGGER, "View received event " + event);
    if (!Platform.isFxApplicationThread() && !isInit()) {
      debug(LOGGER, "Init GUI thread");
      setInit(true);
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
                  menu.displayMessage(
                      "Start game by clicking Game -> start",
                      false,
                      true); // TODO: Add in resource bundle
                } else {
                  menu.displayMessage("Play a move to begin the game", false, true);
                }

                break;
              case MOVE_PLAYED:
                if (board != null) {
                  board.updateBoard();
                }
                if (controlPanel != null) {
                  controlPanel.update(event);
                  if (controlPanel.getHistoryPanel() != null) {
                    controlPanel.getHistoryPanel().updateHistoryPanel();
                  }
                }
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
                  board.updateBoard();
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
                  board.updateBoard();
                }
                if (controlPanel != null) {
                  controlPanel.update(event);
                }
                break;
              case BLACK_UNDO_PROPOSAL:
                if (board != null) {
                  board.updateBoard();
                }
                if (controlPanel != null) {
                  controlPanel.update(event);
                }
                break;
              case MOVE_REDO:
                menu.displayMessage(TextGetter.getText("moveRedone"), false, false);
                if (board != null) {
                  board.updateBoard();
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
                  board.updateBoard();
                }
                if (controlPanel != null) {
                  controlPanel.update(event);
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
                  List<Integer> hintIntegers = Game.getInstance().getGameState().getHintIntegers();
                  board.setHintSquares(
                      new Position(hintIntegers.get(0), hintIntegers.get(1)),
                      new Position(hintIntegers.get(2), hintIntegers.get(3)));
                }
                break;
              default:
                debug(LOGGER, "Received unknown game event: " + event);
                break;
            }
            Game.getInstance().getWorkingViewCondition().signal();
          } finally {
            Game.getInstance().getViewLock().unlock();
          }
        });
  }

  @Override
  public void onErrorEvent(Exception exception) {
    Platform.runLater(() -> menu.displayMessage(exception.getMessage(), true, false));
  }

  public boolean isInit() {
    return init;
  }

  public void setInit(boolean init) {
    this.init = init;
  }
}
