package pdp.view.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import pdp.view.GuiView;

/** Launcher for GUI view. */
public class GuiLauncher extends Application {
  private static GuiView guiView;

  /**
   * Launches the JavaFx application.
   *
   * @param view the GuiView managing the graphical interface
   */
  public static void launchGui(GuiView view) {
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
