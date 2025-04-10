package pdp.view.gui.popups;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import pdp.view.GuiView;

/** Creates a pop-up that fades away in 2 seconds. */
public final class InfoPopUp {

  /** Private constructor to avoid instanciating a utility class. */
  private InfoPopUp() {}

  /**
   * Displays a pop-up during 2 seconds, that fades into the background. It displays the given
   * message.
   *
   * @param message String to display in a popup
   */
  public static void show(final String message) {
    final Stage popupStage = new Stage();

    popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

    final Label messageLabel = new Label(message);
    messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black; -fx-wrap-text: true;");

    final StackPane layout = new StackPane(messageLabel);
    layout.setAlignment(Pos.CENTER);
    layout.setStyle(
        "-fx-background-color:"
            + GuiView.getTheme().getBackground()
            + "; -fx-background-radius: 20px; -fx-padding: 20px;");

    final Scene scene = new Scene(layout, 300, 150);
    scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
    GuiView.applyCss(scene);
    popupStage.setScene(scene);
    popupStage.setAlwaysOnTop(true);

    final FadeTransition fadeOut =
        new FadeTransition(Duration.seconds(2), popupStage.getScene().getRoot());
    fadeOut.setFromValue(1.0);
    fadeOut.setToValue(0.0);
    fadeOut.setCycleCount(1);
    fadeOut.setOnFinished(event -> popupStage.close());

    popupStage.show();

    final PauseTransition delay = new PauseTransition(Duration.seconds(1));
    delay.setOnFinished(event -> fadeOut.play());
    delay.play();
  }
}
