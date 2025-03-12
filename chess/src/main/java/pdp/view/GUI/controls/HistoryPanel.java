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
            "Single",
            "Double",
            "Suite",
            "Family App",
            "Single",
            "Double",
            "Suite",
            "Family App",
            "Single",
            "Double",
            "Suite",
            "Family App",
            "Single",
            "Double",
            "Suite",
            "Family App",
            "Single",
            "Double",
            "Suite",
            "Family App",
            "Single",
            "Double",
            "Suite",
            "Family App");
    list.setItems(items);
    super.getChildren().add(new Label(TextGetter.getText("gameHistory")));
    super.getChildren().add(list);
  }
}
