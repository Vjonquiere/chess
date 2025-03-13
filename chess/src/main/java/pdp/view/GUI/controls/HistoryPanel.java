package pdp.view.GUI.controls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import pdp.utils.TextGetter;

public class HistoryPanel extends VBox {

  public HistoryPanel() {
    ListView<String> list = new ListView<String>();
    // TODO: add history (just an example for now)
    ObservableList<String> items =
        FXCollections.observableArrayList(
            "Move1", "Move2", "Move3", "Move4", "Move5", "Move6", "Move7", "Move8", "Move9",
            "Move10", "Move11", "Move12", "Move13", "Move14", "Move15", "Move16", "Move17",
            "Move18", "Move19", "Move20");
    list.setItems(items);
    super.getChildren().add(new Label(TextGetter.getText("gameHistory")));
    super.getChildren().add(list);
  }
}
