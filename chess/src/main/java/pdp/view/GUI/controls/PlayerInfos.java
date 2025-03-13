package pdp.view.GUI.controls;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import pdp.view.GUIView;

public class PlayerInfos extends HBox {
  Canvas currentPlayer;

  public PlayerInfos(String name, boolean isAI) {
    this.setAlignment(Pos.CENTER_LEFT);
    currentPlayer = new Canvas(20, 20);
    this.getChildren().addAll(getPlayerIcon(isAI), new Label(name), currentPlayer);
    this.setSpacing(10);
  }

  public ImageView getPlayerIcon(boolean isAI) {
    ImageView imageView = new ImageView();
    String fileName = isAI ? "ai" : "player";
    String path = "/assets/icons/" + fileName + ".png";
    Image image = new Image(getClass().getResourceAsStream(path));
    imageView.setImage(image);
    imageView.setFitWidth(50);
    imageView.setFitHeight(50);
    return imageView;
  }

  public void setCurrentPlayer(boolean isCurrent) {
    GraphicsContext gc = currentPlayer.getGraphicsContext2D();
    gc.clearRect(0, 0, currentPlayer.getWidth(), currentPlayer.getHeight());
    if (isCurrent) {
      gc.setFill(Color.web(GUIView.theme.getAccent()));
      gc.fillOval(0, 0, 20, 20);
    }
  }
}
