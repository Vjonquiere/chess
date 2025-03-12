package pdp.view.GUI;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pdp.view.GUI.controls.ButtonsPanel;
import pdp.view.GUI.controls.HistoryPanel;
import pdp.view.GUI.controls.TimersPanel;
import pdp.view.GUIView;

public class ControlPanel extends VBox {
  private VBox timerPanel;
  private HBox playerPanel;
  private VBox historyPanel;
  private ButtonsPanel buttonsPanel;
  String borderStyle =
      "-fx-border-color: "
          + GUIView.theme.getPrimary()
          + ";\n"
          + "-fx-border-width: 2;\n"
          + "-fx-border-radius: 10;\n"
          + "-fx-padding: 5;\n"
          + "-fx-border-style: solid;\n";

  public ControlPanel(BorderPane stage) {
    initTimerPanel();
    initPlayerPanel();
    initHistoryPanel();
    initButtonsPanel();
    super.getChildren().addAll(timerPanel, playerPanel, historyPanel, buttonsPanel);
    // TODO: really fix the width to 1/3 (does not work for now)
    super.setWidth(stage.getWidth() / 3);
    super.setSpacing(10);
    super.setPadding(new Insets(10));
  }

  private void initTimerPanel() {
    timerPanel = new TimersPanel();
    timerPanel.setStyle(borderStyle);
  }

  private void initPlayerPanel() {
    playerPanel = new HBox(new Label("player panel"));
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
}
