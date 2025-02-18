package pdp.view;

import java.util.logging.Logger;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pdp.events.EventType;
import pdp.utils.Logging;
import pdp.utils.TextGetter;
import pdp.view.GUI.ChessMenu;
import pdp.view.GUI.GUILauncher;

public class GUIView implements View {
  private static final Logger LOGGER = Logger.getLogger(GUIView.class.getName());
  private BorderPane root;
  private Stage stage;

  public GUIView() {
    Logging.configureLogging(LOGGER);
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
    Scene scene = new Scene(root, 1280, 720);
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

  @Override
  public void onGameEvent(EventType event) {
    // TODO: manage game events
    /*throw new UnsupportedOperationException(
    "Method not implemented in " + this.getClass().getName());*/
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
