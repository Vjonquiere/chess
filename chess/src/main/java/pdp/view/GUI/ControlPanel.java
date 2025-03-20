package pdp.view.GUI;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pdp.events.EventType;
import pdp.view.GUI.controls.ButtonsPanel;
import pdp.view.GUI.controls.HistoryPanel;
import pdp.view.GUI.controls.PlayerPanel;
import pdp.view.GUIView;

public class ControlPanel extends VBox {
  private PlayerPanel playerPanel;
  private HistoryPanel historyPanel;
  private ButtonsPanel buttonsPanel;
  String borderStyle =
      "-fx-border-color: "
          + GUIView.theme.getPrimary()
          + ";\n"
          + "-fx-border-width: 2;\n"
          + "-fx-border-radius: 10;\n"
          + "-fx-padding: 5;\n"
          + "-fx-border-style: solid;\n";

  /**
   * Creates the panel on the right of the game with information about the current game, the
   * player's turn and timers, the history and buttons.
   *
   * @param stage BorderPane to resize the panel
   */
  public ControlPanel(BorderPane stage) {
    initPlayerPanel();
    initHistoryPanel();
    initButtonsPanel();
    super.getChildren().addAll(playerPanel, historyPanel, buttonsPanel);
    // TODO: really fix the width to 1/3 (does not work for now)
    super.setWidth(stage.getWidth() / 3);
    super.setSpacing(10);
    super.setPadding(new Insets(10));
  }

  /**
   * Initialises the players panel. Contains the timers if blitz is on, the type of player (AI or
   * human) and the current player.
   */
  private void initPlayerPanel() {
    playerPanel = new PlayerPanel();
    playerPanel.setStyle(borderStyle);
  }

  /**
   * Initializes the buttons panel. Contains buttons to restart, resign, undo and redo moves, draw
   * or undraw.
   */
  private void initButtonsPanel() {
    buttonsPanel = new ButtonsPanel();
    buttonsPanel.setStyle(borderStyle);
  }

  /** Initializes the history panel. Composed of the different moves played during the game. */
  private void initHistoryPanel() {
    historyPanel = new HistoryPanel();
    historyPanel.setStyle(borderStyle);
  }

  public HistoryPanel getHistoryPanel() {
    return this.historyPanel;
  }

  /**
   * Updates the player panel with the current player.
   *
   * @param type
   */
  public void update(EventType type) {
    playerPanel.switchCurrentPlayer();
  }
}
