package pdp.view.gui.menu;

import java.io.IOException;
import java.util.Properties;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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

    // Create bold text elements
    final Text versionTitle = new Text(TextGetter.getText("version") + " ");
    versionTitle.setStyle("-fx-font-weight: bold;");

    final Text authorsTitle = new Text("\n" + TextGetter.getText("authors") + " ");
    authorsTitle.setStyle("-fx-font-weight: bold;");

    final Text projectTitle = new Text("\n" + TextGetter.getText("project") + " ");
    projectTitle.setStyle("-fx-font-weight: bold;");

    // Create normal text elements
    final Text versionValue = new Text(properties.getProperty("version"));
    final Text authorsValue =
        new Text(
            "CHOLLON Mathilde, DEMIRCI Denis, JOMAA Iwen, JONQUIERE Valentin, LANDRY Jonathan");
    final Text projectValue = new Text(TextGetter.getText("projectDescription"));

    // Add texts to TextFlow for proper formatting
    final TextFlow textFlow =
        new TextFlow(
            versionTitle,
            versionValue,
            authorsTitle,
            authorsValue,
            new Text("\n"),
            projectTitle,
            projectValue);

    // Set the TextFlow as the content of the Label
    final Label aboutLabel = new Label();
    aboutLabel.setGraphic(textFlow);
    aboutLabel.setWrapText(true);

    return aboutLabel;
  }
}
