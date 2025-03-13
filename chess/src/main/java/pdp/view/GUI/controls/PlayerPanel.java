package pdp.view.GUI.controls;

import javafx.scene.layout.VBox;
import pdp.model.Game;

public class PlayerPanel extends VBox {
  PlayerInfos whitePlayer;
  PlayerInfos blackPlayer;

  public PlayerPanel() {
    setSpacing(5);
    whitePlayer = new PlayerInfos("white player", Game.getInstance().isWhiteAI());
    blackPlayer = new PlayerInfos("black player", Game.getInstance().isBlackAI());
    this.getChildren().addAll(whitePlayer, blackPlayer);
  }

  public void switchCurrentPlayer() {
    boolean isWhiteTurn = Game.getInstance().getGameState().isWhiteTurn();
    whitePlayer.setCurrentPlayer(isWhiteTurn);
    blackPlayer.setCurrentPlayer(!isWhiteTurn);
  }
}
