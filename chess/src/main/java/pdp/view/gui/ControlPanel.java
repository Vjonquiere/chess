package pdp.view.gui;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pdp.events.EventType;
import pdp.view.GuiView;
import pdp.view.gui.controls.ButtonsPanel;
import pdp.view.gui.controls.HistoryPanel;
import pdp.view.gui.controls.PlayerPanel;

/** Control panel for GUI view. Contains player infos, history and buttons. */
public class ControlPanel extends VBox {

  /** Panel containing the players' information. */
  private PlayerPanel playerPanel;

  /** Panel containing the history. */
  private HistoryPanel historyPanel;

  /** Panel containing the different buttons. */
  private ButtonsPanel buttonsPanel;

  /** Style of the coder of each component of the panel. */
  private final String borderStyle =
      "-fx-border-color: "
          + GuiView.getTheme().getPrimary()
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
  public ControlPanel(final BorderPane stage) {
    super();
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

  /**
   * Retrieves the history panel.
   *
   * @return History panel
   */
  public HistoryPanel getHistoryPanel() {
    return this.historyPanel;
  }

  /**
   * Updates the player panel with the current player.
   *
   * @param type The type of the event sent.
   */
  public void update(EventType type) {
    playerPanel.switchCurrentPlayer();
  }
}
