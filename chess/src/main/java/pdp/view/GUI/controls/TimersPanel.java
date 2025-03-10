package pdp.view.GUI.controls;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pdp.utils.TextGetter;

public class TimersPanel extends VBox {

  public TimersPanel() {
    Label WhiteLabel = new Label(TextGetter.getText("whiteTimer"));
    Label timeWLabel = new Label("Time");
    HBox whiteBox = new HBox();
    whiteBox.getChildren().addAll(WhiteLabel, timeWLabel);
    Label BlackLabel = new Label(TextGetter.getText("blackTimer"));
    Label timeBLabel = new Label("Time");
    HBox blackBox = new HBox();
    blackBox.getChildren().addAll(BlackLabel, timeBLabel);

    super.getChildren().addAll(whiteBox, blackBox);
  }
}
