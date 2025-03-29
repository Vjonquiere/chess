package pdp.view.gui.menu;

import java.io.IOException;
import java.util.Properties;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pdp.utils.CommandLineOptions;
import pdp.utils.TextGetter;
import pdp.view.GuiView;

/** GUI widget displaying basic "About" information. */
public class AboutPopUp extends VBox {
  /** Build a new popup. */
  public AboutPopUp() {
    super();
    final Stage popupStage = new Stage();
    popupStage.setTitle(TextGetter.getText("about"));
    popupStage.initModality(Modality.APPLICATION_MODAL);
    Label infoLabel = null;
    try {
      infoLabel = getLabel();
    } catch (IOException ignored) {

    }

    final Button closeButton = new Button(TextGetter.getText("close"));
    closeButton.setOnAction(e -> popupStage.close());

    final VBox layout = new VBox(10, infoLabel, closeButton);
    layout.setAlignment(Pos.TOP_CENTER);
    layout.setStyle("-fx-padding: 15;");

    final Scene scene = new Scene(layout);
    GuiView.applyCss(scene);
    popupStage.setScene(scene);
    popupStage.showAndWait();
  }

  /**
   * Get the application information from TextGetter and build a Label from it.
   *
   * @return A label containing the about message.
   */
  private Label getLabel() throws IOException {
    final Properties properties = new Properties();
    properties.load(CommandLineOptions.class.getClassLoader().getResourceAsStream(".properties"));

    final String aboutText =
        TextGetter.getText("version")
            + " "
            + properties.getProperty("version")
            + "\n"
            + TextGetter.getText("authors")
            + " "
            + "CHOLLON Mathilde, DEMIRCI Denis, JOMAA Iwen, JONQUIERE Valentin, LANDRY Jonathan"
            + "\n"
            + TextGetter.getText("project")
            + " "
            + TextGetter.getText("projectDescription");

    final Label aboutLabel = new Label(aboutText);
    aboutLabel.setWrapText(true);
    return aboutLabel;
  }
}
