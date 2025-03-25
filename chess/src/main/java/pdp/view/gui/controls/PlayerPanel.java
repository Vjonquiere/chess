package pdp.view.gui.controls;

import javafx.scene.layout.VBox;
import pdp.model.Game;
import pdp.utils.TextGetter;

/** GUI widget to display all the player information. */
public class PlayerPanel extends VBox {
  private PlayerInfos whitePlayer;
  private PlayerInfos blackPlayer;

  /** Build a new player panel from current game instance. */
  public PlayerPanel() {
    setSpacing(5);
    whitePlayer =
        new PlayerInfos(TextGetter.getText("whitePlayer"), Game.getInstance().isWhiteAi(), true);
    blackPlayer =
        new PlayerInfos(TextGetter.getText("blackPlayer"), Game.getInstance().isBlackAi(), false);
    this.getChildren().addAll(whitePlayer, blackPlayer);
  }

  /** Switch the current player relying on game status. */
  public void switchCurrentPlayer() {
    boolean isWhiteTurn = Game.getInstance().getGameState().isWhiteTurn();
    whitePlayer.setCurrentPlayer(isWhiteTurn);
    blackPlayer.setCurrentPlayer(!isWhiteTurn);
  }
}
