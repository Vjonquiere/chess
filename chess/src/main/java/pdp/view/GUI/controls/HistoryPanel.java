package pdp.view.GUI.controls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import pdp.utils.TextGetter;
import pdp.view.GUIView;

public class HistoryPanel extends VBox {
  private static String styleFileName = "" + GUIView.theme;

  public HistoryPanel() {
    ListView<String> list = new ListView<String>();
    list.setStyle("-fx-background-color: " + GUIView.theme.getBackground() + ";");
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
