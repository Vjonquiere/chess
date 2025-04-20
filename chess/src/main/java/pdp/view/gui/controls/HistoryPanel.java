package pdp.view.gui.controls;

import java.util.Stack;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.UndoMultipleMoveCommand;
import pdp.model.GameAbstract;
import pdp.model.history.History;
import pdp.model.history.HistoryNode;
import pdp.utils.TextGetter;
import pdp.view.gui.popups.InfoPopUp;
import pdp.view.gui.popups.YesNoPopUp;

/** GUI representation for history display. */
public class HistoryPanel extends VBox {

  /** List containing the moves written as strings. */
  private final ListView<String> list = new ListView<>();

  /** Construct a new panel. */
  public HistoryPanel() {
    super();
    final ObservableList<String> items = FXCollections.observableArrayList();

    final ObservableList<String> currentItems = this.list.getItems();
    items.addAll(currentItems);

    final History history = GameAbstract.getInstance().getHistory();

    HistoryNode currentNode = history.getCurrentMove().orElse(null);
    while (currentNode != null) {
      if (currentNode.getPrevious().orElse(null) == null) {
        break;
      }
      currentNode = currentNode.getPrevious().orElse(null);
    }

    currentNode = currentNode.getNext().orElse(null);

    while (currentNode != null) {
      items.add(currentNode.getState().toString());
      currentNode = currentNode.getNext().orElse(null);
    }

    list.setItems(items);
    super.getChildren().add(new Label(TextGetter.getText("gameHistory")));
    super.getChildren().add(list);
  }

  /** Update displayed information of the panel. */
  public void updateHistoryPanel() {

    final ObservableList<String> items = FXCollections.observableArrayList();

    final History history = GameAbstract.getInstance().getHistory();

    HistoryNode currentNode = history.getCurrentMove().orElse(null);

    final Stack<HistoryNode> stack = new Stack<>();

    while (currentNode != null) {
      stack.push(currentNode);
      currentNode = currentNode.getPrevious().orElse(null); // Utilisation du getter
    }

    HistoryNode node = stack.pop();
    while (!stack.isEmpty()) {
      node = stack.pop();
      items.add(node.getState().toString());
    }

    list.setOnMouseClicked(
        event -> {
          if (event.getClickCount() == 2) {
            final int selectedItem = list.getSelectionModel().getSelectedIndex();
            if (selectedItem != -1) {
              if (GameAbstract.getInstance().getGameState().getFullTurn() > 0) {
                BagOfCommands.getInstance()
                    .addCommand(new UndoMultipleMoveCommand(items.size() - selectedItem - 1));
                if (!GameAbstract.getInstance().isWhiteAi()
                    && !GameAbstract.getInstance().isBlackAi()) {
                  new YesNoPopUp(
                      "undoInstructionsGui",
                      new UndoMultipleMoveCommand(items.size() - selectedItem - 1),
                      () -> GameAbstract.getInstance().getGameState().undoRequestReset());
                }
              } else {
                InfoPopUp.show(TextGetter.getText("notAllowed"));
              }
              if (GameAbstract.getInstance().isWhiteAi()
                  && GameAbstract.getInstance().isBlackAi()) {
                InfoPopUp.show(TextGetter.getText("notAllowed"));
              }
            }
          }
        });
    list.setItems(items);
    list.scrollTo(items.size() - 1);
    list.getSelectionModel().select(items.size() - 1);
  }
}
