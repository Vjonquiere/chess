package pdp.view.GUI;

import javafx.application.Application;
import javafx.stage.Stage;
import pdp.view.GUIView;

public class GUILauncher extends Application {
  private static GUIView guiView;

  /**
   * Launches the JavaFx application.
   *
   * @param view the GUIView managing the graphical interface
   */
  public static void launchGUI(GUIView view) {
    guiView = view;
    Application.launch();
  }

  /**
   * Initializes and starts the JavaFX application. Called automatically by Application.launch()
   *
   * @param primaryStage the primary stage for the JavaFX application
   */
  @Override
  public void start(Stage primaryStage) {
    guiView.init(primaryStage);
    guiView.show();
  }
}
