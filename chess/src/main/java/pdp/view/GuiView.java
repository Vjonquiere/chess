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
  public static ColorTheme theme = SIMPLE;
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
          Game.getInstance().viewLock.lock();
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
                System.out.println("GUI board displayed"); // TODO: Add in resource bundle
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
                System.out.println(TextGetter.getText("whiteWin"));
                break;
              case WIN_BLACK:
                System.out.println(TextGetter.getText("blackWin"));
                break;
              case DRAW:
                System.out.println(TextGetter.getText("onDraw"));
                break;
              case WHITE_DRAW_PROPOSAL:
                System.out.println(TextGetter.getText("drawProposal", TextGetter.getText("white")));
                break;
              case BLACK_DRAW_PROPOSAL:
                System.out.println(TextGetter.getText("drawProposal", TextGetter.getText("black")));
                break;
              case WHITE_UNDRAW:
                System.out.println(
                    TextGetter.getText("cancelDrawProposal", TextGetter.getText("white")));
                break;
              case BLACK_UNDRAW:
                System.out.println(
                    TextGetter.getText("cancelDrawProposal", TextGetter.getText("black")));
                break;
              case GAME_SAVED:
                System.out.println(TextGetter.getText("gameSaved"));
                break;
              case MOVE_UNDO:
                System.out.println(TextGetter.getText("moveUndone"));
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
                System.out.println(TextGetter.getText("undoProposal", TextGetter.getText("white")));
                System.out.println(
                    TextGetter.getText("undoInstructions", TextGetter.getText("black")));
                if (board != null) {
                  board.updateBoard();
                }
                if (controlPanel != null) {
                  controlPanel.update(event);
                }
                break;
              case BLACK_UNDO_PROPOSAL:
                System.out.println(TextGetter.getText("undoProposal", TextGetter.getText("black")));
                System.out.println(
                    TextGetter.getText("undoInstructions", TextGetter.getText("white")));
                if (board != null) {
                  board.updateBoard();
                }
                if (controlPanel != null) {
                  controlPanel.update(event);
                }
                break;
              case MOVE_REDO:
                System.out.println(TextGetter.getText("moveRedone"));
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
                System.out.println(TextGetter.getText("redoProposal", TextGetter.getText("white")));
                System.out.println(
                    TextGetter.getText("redoInstructions", TextGetter.getText("black")));
                break;
              case BLACK_REDO_PROPOSAL:
                System.out.println(TextGetter.getText("redoProposal", TextGetter.getText("black")));
                System.out.println(
                    TextGetter.getText("redoInstructions", TextGetter.getText("white")));
                break;
              case AI_PLAYING:
                System.out.println(TextGetter.getText("ai_playing"));
                break;
              case GAME_RESTART:
                System.out.println(TextGetter.getText("gameRestart"));
                System.out.println(TextGetter.getText("welcomeCLI"));
                System.out.println(TextGetter.getText("welcomeInstructions"));
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
            Game.getInstance().workingView.signal();
          } finally {
            Game.getInstance().viewLock.unlock();
          }
        });
  }

  @Override
  public void onErrorEvent(Exception e) {
    // TODO: manage errors
    /*
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());

     */
  }

  public boolean isInit() {
    return init;
  }

  public void setInit(boolean init) {
    this.init = init;
  }
}
