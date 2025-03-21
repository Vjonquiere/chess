package pdp.view.gui;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pdp.events.EventType;
import pdp.view.GuiView;
import pdp.view.gui.controls.ButtonsPanel;
import pdp.view.gui.controls.HistoryPanel;
import pdp.view.gui.controls.PlayerPanel;

public class ControlPanel extends VBox {
  private PlayerPanel playerPanel;
  private HistoryPanel historyPanel;
  private ButtonsPanel buttonsPanel;
  String borderStyle =
      "-fx-border-color: "
          + GuiView.theme.getPrimary()
          + ";\n"
          + "-fx-border-width: 2;\n"
          + "-fx-border-radius: 10;\n"
          + "-fx-padding: 5;\n"
          + "-fx-border-style: solid;\n";

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

  private void initPlayerPanel() {
    playerPanel = new PlayerPanel();
    playerPanel.setStyle(borderStyle);
  }

  private void initButtonsPanel() {
    buttonsPanel = new ButtonsPanel();
    buttonsPanel.setStyle(borderStyle);
  }

  private void initHistoryPanel() {
    historyPanel = new HistoryPanel();
    historyPanel.setStyle(borderStyle);
  }

  public HistoryPanel getHistoryPanel() {
    return this.historyPanel;
  }

  public void update(EventType type) {
    playerPanel.switchCurrentPlayer();
  }
}
