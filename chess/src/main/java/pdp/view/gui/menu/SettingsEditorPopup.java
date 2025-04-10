package pdp.view.gui.menu;

import java.io.FileNotFoundException;
import java.net.URL;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pdp.model.Game;
import pdp.model.parsers.BoardFileParser;
import pdp.model.savers.ConfigFileSaver;
import pdp.utils.OptionType;
import pdp.utils.TextGetter;
import pdp.view.GuiView;

/** GUI widget to edit settings. */
public class SettingsEditorPopup extends VBox {
  /** Build a new popup to edit settings. */
  public SettingsEditorPopup() {
    super();
    final Stage popupStage = new Stage();
    popupStage.setTitle(TextGetter.getText("settings.edit"));
    popupStage.initModality(Modality.APPLICATION_MODAL);
    final VBox layout = new VBox(10);

    final Button saveButton = new Button(TextGetter.getText("save"));

    final String path = Game.getInstance().getOptions().get(OptionType.CONFIG);
    String text = "";

    try {
      text = new BoardFileParser().readFile(path);
    } catch (FileNotFoundException e) {
      try {
        final URL filePath = getClass().getClassLoader().getResource("default.chessrc");
        text = new BoardFileParser().readFile(filePath.getPath());
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    final TextArea textArea = new TextArea(text);
    saveButton.setOnAction(
        e -> {
          ConfigFileSaver.save(path, textArea.getText());
          popupStage.close();
        });
    layout.getChildren().add(textArea);

    final Button closeButton = new Button(TextGetter.getText("close"));
    closeButton.setOnAction(e -> popupStage.close());
    layout.getChildren().add(closeButton);

    layout.getChildren().add(saveButton);

    layout.setAlignment(Pos.TOP_CENTER);
    layout.setStyle("-fx-padding: 15;");
    final Scene scene = new Scene(layout);
    GuiView.applyCss(scene);
    popupStage.setScene(scene);
    popupStage.showAndWait();
  }
}
