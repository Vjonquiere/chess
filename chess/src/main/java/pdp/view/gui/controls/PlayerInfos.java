package pdp.view.gui.controls;

import java.text.DecimalFormat;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import pdp.model.Game;
import pdp.model.ai.Solver;
import pdp.utils.Timer;
import pdp.view.GuiView;
import pdp.view.gui.popups.AiMonitor;
import pdp.view.gui.popups.InfoPopUp;

/** GUI widget to display player data. */
public class PlayerInfos extends HBox {
  /** Canvas to add a colored circle next to the current Player. */
  private final Canvas currentPlayer;

  /** Label containing the timer. */
  private final Label timerLabel = new Label();

  /** Timeline needed to update the timer label every 0.5 second. */
  private Timeline timeline;

  private Label lastNodeInfo = new Label();

  AiMonitor aiMonitor = new AiMonitor();

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
            aiMonitor.show();
          });
      Tooltip.install(info, new Tooltip(solver.toString()));
      lastNodeInfo = new Label();
      this.getChildren()
          .addAll(
              getPlayerIcon(isAi), new Label(name), timerLabel, currentPlayer, info, lastNodeInfo);
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

  public static String formatNumber(long number) {
    if (number < 1_000) {
      return String.valueOf(number);
    } else if (number < 10_000) {
      return (number / 100) / 10 + "k";
    } else if (number < 1_000_000) {
      return (number / 1_000) + "k";
    } else if (number < 10_000_000) {
      return new DecimalFormat("#.##").format(number / 1_000_000.0) + "M";
    } else {
      return (number / 1_000_000) + "M";
    }
  }

  public void setAiStats(long exploratedNodes, long explorationTime) {
    if (lastNodeInfo != null) {
      if (explorationTime / 1000000000 == 0) return;
      lastNodeInfo.setText(
          formatNumber(exploratedNodes / (explorationTime / 1000000000))
              + " Nodes/s ("
              + formatNumber(exploratedNodes)
              + " explored)");
    }
    aiMonitor.update(true);
  }
}
