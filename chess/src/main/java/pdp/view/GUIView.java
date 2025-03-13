package pdp.view;

import static pdp.utils.Logging.DEBUG;
import static pdp.view.GUI.themes.ColorTheme.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pdp.events.EventType;
import pdp.model.Game;
import pdp.utils.Logging;
import pdp.utils.TextGetter;
import pdp.view.GUI.ChessMenu;
import pdp.view.GUI.ControlPanel;
import pdp.view.GUI.GUILauncher;
import pdp.view.GUI.board.Board;
import pdp.view.GUI.popups.EndGamePopUp;
import pdp.view.GUI.themes.ColorTheme;

public class GUIView implements View {
  private static final Logger LOGGER = Logger.getLogger(GUIView.class.getName());
  private BorderPane root;
  private Stage stage;
  private Scene scene;
  private Board board;
  private ControlPanel controlPanel;
  private ChessMenu menu;
  public static ColorTheme theme = SIMPLE;
  boolean init = false;

  static {
    Logging.configureLogging(LOGGER);
  }

  public void applyCSS(String cssContent) {
    try {
      File tempFile = File.createTempFile("theme-", ".css");
      tempFile.deleteOnExit();

      try (FileWriter writer = new FileWriter(tempFile)) {
        writer.write(cssContent);
      }

      scene.getStylesheets().clear();
      scene.getStylesheets().add(tempFile.toURI().toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void updateTheme() {
    applyCSS(theme.getCSSStyle());
    onGameEvent(EventType.GAME_STARTED);
  }

  public GUIView() {
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
    applyCSS(theme.getCSSStyle());
    stage.setScene(scene);
    if (board != null) board.setStage(stage);
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
    Thread guiThread = new Thread(() -> GUILauncher.launchGUI(this));
    guiThread.start();
    return guiThread;
  }

  /**
   * Handle game events to keep view updated (JavaFx version).
   *
   * @param event
   */
  @Override
  public void onGameEvent(EventType event) {
    DEBUG(LOGGER, "View received event " + event);
    if (!Platform.isFxApplicationThread() && !init) {
      DEBUG(LOGGER, "Init GUI thread");
      init = true;
      Platform.startup(() -> Platform.runLater(() -> this.onGameEvent(event)));
    }
    Platform.runLater(
        () -> {
          Game.getInstance().viewLock.lock();
          DEBUG(LOGGER, "View handling event " + event);
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
                DEBUG(LOGGER, "Board view initialized");
                if (controlPanel != null) {
                  root.getChildren().remove(controlPanel);
                }
                controlPanel = new ControlPanel(root);
                controlPanel.update(event);
                root.setCenter(controlPanel);
                if (menu != null) {
                  root.getChildren().remove(menu);
                }
                menu = new ChessMenu(this);
                root.setTop(menu);

                break;
              case MOVE_PLAYED:
                if (board != null) {
                  board.updateBoard();
                }
                if (controlPanel != null) {
                  controlPanel.update(event);
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
                /*
                case DRAW_ACCEPTED:
                  System.out.println(TextGetter.getText("drawAccepted"));
                  break;
                   */
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
                /*
                case OUT_OF_TIME_WHITE:
                  System.out.println(TextGetter.getText("outOfTime", TextGetter.getText("white")));
                  break;
                case OUT_OF_TIME_BLACK:
                  System.out.println(TextGetter.getText("outOfTime", TextGetter.getText("black")));
                  break;
                case THREEFOLD_REPETITION:
                  System.out.println(TextGetter.getText("threeFoldRepetition"));
                  break;
                case INSUFFICIENT_MATERIAL:
                  System.out.println(TextGetter.getText("insufficientMaterial"));
                  break;
                case FIFTY_MOVE_RULE:
                  System.out.println(TextGetter.getText("fiftyMoveRule"));
                  break;
                case WHITE_RESIGNS:
                  System.out.println(TextGetter.getText("resigns", TextGetter.getText("white")));
                  break;
                case BLACK_RESIGNS:
                  System.out.println(TextGetter.getText("resigns", TextGetter.getText("black")));
                  break;
                case CHECKMATE_WHITE:
                  System.out.println(
                      TextGetter.getText(
                          "checkmate", TextGetter.getText("white"), TextGetter.getText("black")));
                  break;
                case CHECKMATE_BLACK:
                  System.out.println(
                      TextGetter.getText(
                          "checkmate", TextGetter.getText("black"), TextGetter.getText("white")));
                  break;
                case STALEMATE:
                  System.out.println(TextGetter.getText("stalemate"));
                  break;
                   */
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
                }
                break;
              case UPDATE_THEME:
                updateTheme();
              default:
                DEBUG(LOGGER, "Received unknown game event: " + event);
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
}
