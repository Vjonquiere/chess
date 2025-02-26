package pdp.view;

import static pdp.utils.Logging.DEBUG;

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
import pdp.view.GUI.GUILauncher;
import pdp.view.GUI.board.Board;

public class GUIView implements View {
  private static final Logger LOGGER = Logger.getLogger(GUIView.class.getName());
  private BorderPane root;
  private Stage stage;
  private Board board;
  boolean init = false;

  static {
    Logging.configureLogging(LOGGER);
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
    stage.setTitle(TextGetter.getText("title"));
    root.setTop(new ChessMenu());
    // root.setCenter(board);
    Scene scene = new Scene(root, 820, 820);
    stage.setScene(scene);

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
                if (board != null) {
                  root.getChildren().remove(board);
                }
                board = new Board(Game.getInstance());
                root.setCenter(board);
                System.out.println("GUI board displayed"); // TODO: Add in resource bundle
                DEBUG(LOGGER, "Board view initialized");
                break;
              case MOVE_PLAYED:
                if (board != null) {
                  board.updateBoard();
                }
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
              case DRAW_ACCEPTED:
                System.out.println(TextGetter.getText("drawAccepted"));
                break;
              case GAME_SAVED:
                System.out.println(TextGetter.getText("gameSaved"));
                break;
              case MOVE_UNDO:
                System.out.println(TextGetter.getText("moveUndone"));
                if (board != null) {
                  board.updateBoard();
                }
                break;
              case MOVE_REDO:
                System.out.println(TextGetter.getText("moveRedone"));
                if (board != null) {
                  board.updateBoard();
                }
                break;
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
              case AI_PLAYING:
                System.out.println(TextGetter.getText("ai_playing"));
                break;
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
