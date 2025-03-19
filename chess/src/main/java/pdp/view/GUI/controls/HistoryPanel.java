package pdp.view.GUI.controls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import pdp.model.Game;
import pdp.model.history.History;
import pdp.model.history.HistoryNode;
import pdp.utils.TextGetter;

public class HistoryPanel extends VBox {

  private ListView<String> list = new ListView<String>();

  public HistoryPanel() {

    ObservableList<String> items = FXCollections.observableArrayList();

    ObservableList<String> currentItems = this.list.getItems();
    items.addAll(currentItems);

    // Récupérer l'historique des mouvements à partir de la liste doublement chainée
    History history = Game.getInstance().getHistory();

    // Itérer sur l'historique et ajouter chaque mouvement à la liste observable
    HistoryNode currentNode = history.getCurrentMove().orElse(null);
    while (currentNode != null) {
      if (currentNode.getPrevious().orElse(null) == null) {
        break;
      }
      currentNode = currentNode.getPrevious().orElse(null);
    }

    currentNode = currentNode.getNext().orElse(null);

    while (currentNode != null) {
      items.add(
          currentNode
              .getState()
              .toString()); // Assurez-vous que la méthode toString() est définie dans la classe
      // Move
      currentNode = currentNode.getNext().orElse(null); // Passer au mouvement précédent
    }

    list.setItems(items);
    super.getChildren().add(new Label(TextGetter.getText("gameHistory")));
    super.getChildren().add(list);
  }

  public void updateHistoryPanel() {

    History history = Game.getInstance().getHistory();
    ObservableList<String> items = FXCollections.observableArrayList();

    ObservableList<String> currentItems = this.list.getItems();
    items.addAll(currentItems);

    HistoryNode currentNode = history.getCurrentMove().orElse(null);
    items.add(currentNode.getState().toString());

    this.list.setItems(items);
  }
}
