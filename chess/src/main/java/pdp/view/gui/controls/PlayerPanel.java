package pdp.view.gui.controls;

import javafx.scene.layout.VBox;
import pdp.model.GameManager;
import pdp.utils.TextGetter;

/** GUI widget to display all the player information. */
public class PlayerPanel extends VBox {
  /** Information concerning the white player. */
  private final PlayerInfos whitePlayer;

  /** Information concerning the white player. */
  private final PlayerInfos blackPlayer;

  /** Build a new player panel from current game instance. */
  public PlayerPanel() {
    super();
    setSpacing(5);
    whitePlayer =
        new PlayerInfos(
            TextGetter.getText("whitePlayer"), GameManager.getInstance().isWhiteAi(), true);
    blackPlayer =
        new PlayerInfos(
            TextGetter.getText("blackPlayer"), GameManager.getInstance().isBlackAi(), false);
    this.getChildren().addAll(whitePlayer, blackPlayer);
  }

  /** Switch the current player relying on game status. */
  public void switchCurrentPlayer() {
    final boolean isWhiteTurn = GameManager.getInstance().isWhiteTurn();
    whitePlayer.setCurrentPlayer(isWhiteTurn);
    blackPlayer.setCurrentPlayer(!isWhiteTurn);
  }

  /** Updates the timers of both players. */
  public void updateTimersOnce() {
    whitePlayer.updateTimerOnce(true);
    blackPlayer.updateTimerOnce(false);
  }
}
