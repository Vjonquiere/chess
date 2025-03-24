package pdp.view.gui.controls;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import pdp.model.Game;
import pdp.utils.Timer;
import pdp.view.GuiView;

/** GUI widget to display player data. */
public class PlayerInfos extends HBox {
  Canvas currentPlayer;
  Label timerLabel = new Label();
  Timeline timeline;

  /**
   * Build a player infos widget from given information.
   *
   * @param name The name of the player.
   * @param isAi The player type.
   * @param isWhite The color of the player.
   */
  public PlayerInfos(String name, boolean isAi, boolean isWhite) {
    this.setAlignment(Pos.CENTER_LEFT);
    currentPlayer = new Canvas(20, 20);
    Timer timer = Game.getInstance().getTimer(isWhite);
    if (timer != null) {
      timerLabel.setText(timer.getTimeRemainingString());
      updateTimer(isWhite);
    }
    this.getChildren().addAll(getPlayerIcon(isAi), new Label(name), timerLabel, currentPlayer);
    this.setSpacing(10);
  }

  /**
   * Get the icon depending on the player type.
   *
   * @param isAi The player type.
   * @return An image corresponding to the player type
   */
  public ImageView getPlayerIcon(boolean isAi) {
    ImageView imageView = new ImageView();
    String fileName = isAi ? "ai" : "player";
    String path = "/assets/icons/" + fileName + ".png";
    Image image = new Image(getClass().getResourceAsStream(path));
    imageView.setImage(image);
    imageView.setFitWidth(50);
    imageView.setFitHeight(50);
    return imageView;
  }

  public void updateTimer(boolean isWhite) {
    timeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.5),
                event -> {
                  Timer timer = Game.getInstance().getTimer(isWhite);
                  if (timer != null) {
                    timerLabel.setText(timer.getTimeRemainingString());
                  }
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
  }

  public void setCurrentPlayer(boolean isCurrent) {
    GraphicsContext gc = currentPlayer.getGraphicsContext2D();
    gc.clearRect(0, 0, currentPlayer.getWidth(), currentPlayer.getHeight());
    if (timeline != null) {
      timeline.stop();
    }
    if (isCurrent) {
      gc.setFill(Color.web(GuiView.theme.getAccent()));
      gc.fillOval(0, 0, 20, 20);
      if (timeline != null) {
        timeline.play();
      }
    }
  }
}
