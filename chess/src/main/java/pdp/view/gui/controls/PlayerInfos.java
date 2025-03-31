package pdp.view.gui.controls;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import pdp.model.Game;
import pdp.model.ai.Solver;
import pdp.utils.Timer;
import pdp.view.GuiView;
import pdp.view.gui.popups.InfoPopUp;

/** GUI widget to display player data. */
public class PlayerInfos extends HBox {
  /** Canvas to add a colored circle next to the current Player. */
  private final Canvas currentPlayer;

  /** Label containing the timer. */
  private final Label timerLabel = new Label();

  /** Timeline needed to update the timer label every 0.5 second. */
  private Timeline timeline;

  /**
   * Build a player infos widget from given information.
   *
   * @param name The name of the player.
   * @param isAi The player type.
   * @param isWhite The color of the player.
   */
  public PlayerInfos(final String name, final boolean isAi, final boolean isWhite) {
    super();
    this.setAlignment(Pos.CENTER_LEFT);
    currentPlayer = new Canvas(20, 20);
    final Timer timer = Game.getInstance().getTimer(isWhite);
    if (timer != null) {
      timerLabel.setText(timer.getTimeRemainingString());
      updateTimer(isWhite);
    }

    if (isAi) {
      ImageView info = getInfoIcon();
      Solver solver;
      if (isWhite) {
        solver = Game.getInstance().getWhiteSolver();
      } else {
        solver = Game.getInstance().getBlackSolver();
      }
      info.setOnMouseClicked(
          event -> {
            InfoPopUp.show(solver.toString());
          });
      Tooltip.install(info, new Tooltip(solver.toString()));
      this.getChildren()
          .addAll(getPlayerIcon(isAi), new Label(name), timerLabel, currentPlayer, info);
    } else {
      this.getChildren().addAll(getPlayerIcon(isAi), new Label(name), timerLabel, currentPlayer);
    }

    this.setSpacing(10);
  }

  /**
   * Get the icon depending on the player type.
   *
   * @param isAi The player type.
   * @return An image corresponding to the player type
   */
  public ImageView getPlayerIcon(final boolean isAi) {
    final ImageView imageView = new ImageView();
    final String fileName = isAi ? "ai" : "player";
    final String path = "/assets/icons/" + fileName + ".png";
    final Image image = new Image(getClass().getResourceAsStream(path));
    imageView.setImage(image);
    imageView.setFitWidth(50);
    imageView.setFitHeight(50);
    return imageView;
  }

  public ImageView getInfoIcon() {
    final ImageView imageView = new ImageView();
    final String path = "/assets/icons/information.png";
    final Image image = new Image(getClass().getResourceAsStream(path));
    imageView.setImage(image);
    imageView.setFitWidth(25);
    imageView.setFitHeight(25);
    return imageView;
  }

  /**
   * Update the remaining time depending on player color.
   *
   * @param isWhite The player color.
   */
  public void updateTimer(final boolean isWhite) {
    timeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.5),
                event -> {
                  final Timer timer = Game.getInstance().getTimer(isWhite);
                  if (timer != null) {
                    timerLabel.setText(timer.getTimeRemainingString());
                  }
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
  }

  /**
   * Update the current player.
   *
   * @param isCurrent The current player status.
   */
  public void setCurrentPlayer(final boolean isCurrent) {
    final GraphicsContext graphicCtx = currentPlayer.getGraphicsContext2D();
    graphicCtx.clearRect(0, 0, currentPlayer.getWidth(), currentPlayer.getHeight());
    if (timeline != null) {
      timeline.stop();
    }
    if (isCurrent) {
      graphicCtx.setFill(Color.web(GuiView.getTheme().getAccent()));
      graphicCtx.fillOval(0, 0, 20, 20);
      if (timeline != null) {
        timeline.play();
      }
    }
  }
}
