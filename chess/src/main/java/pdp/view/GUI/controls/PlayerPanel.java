package pdp.view.GUI.controls;

import javafx.scene.layout.VBox;
import pdp.model.Game;
import pdp.utils.TextGetter;

public class PlayerPanel extends VBox {
  private PlayerInfos whitePlayer;
  private PlayerInfos blackPlayer;

  public PlayerPanel() {
    setSpacing(5);
    whitePlayer =
        new PlayerInfos(TextGetter.getText("whitePlayer"), Game.getInstance().isWhiteAI(), true);
    blackPlayer =
        new PlayerInfos(TextGetter.getText("blackPlayer"), Game.getInstance().isBlackAI(), false);
    this.getChildren().addAll(whitePlayer, blackPlayer);
  }

  public void switchCurrentPlayer() {
    boolean isWhiteTurn = Game.getInstance().getGameState().isWhiteTurn();
    whitePlayer.setCurrentPlayer(isWhiteTurn);
    blackPlayer.setCurrentPlayer(!isWhiteTurn);
  }
}
