package pdp.view.gui.menu;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/** A GUI widget to display messages in the menu bar. */
public class MessageDisplay extends HBox {
  /** Icon for the error messages. */
  private final ImageView errorIcon;

  /** Icon for the information messages. */
  private final ImageView infoIcon;

  /** Duration of the display of a finite message. */
  private static final int MESSAGE_TIMEOUT = 3;

  /** Build a new message display dans load needed assets. */
  public MessageDisplay() {
    super();
    errorIcon = loadIcon("/assets/icons/error.png");
    infoIcon = loadIcon("/assets/icons/information.png");
    super.setAlignment(Pos.CENTER);
    super.setSpacing(5);
  }

  /** Remove the last displayed message. */
  public void clearPreviousMessage() {
    this.getChildren().clear();
  }

  /**
   * Display a message with the error formatting.
   *
   * @param error The error message.
   */
  public void displayError(final String error, final boolean infinite) {
    clearPreviousMessage();
    Label label = new Label(error);
    label.setId("labelError");
    super.getChildren().addAll(errorIcon, label);
    if (!infinite) {
      autoClearMessage();
    }
  }

  /**
   * Display a message with the information formatting.
   *
   * @param information The information message.
   */
  public void displayInfo(final String information, final boolean infinite) {
    clearPreviousMessage();
    Label label = new Label(information);
    label.setId("labelInfo");
    super.getChildren().addAll(infoIcon, label);
    if (!infinite) {
      autoClearMessage();
    }
  }

  /**
   * Load icon from resources.
   *
   * @param path The path of the icon to load.
   * @return An image view corresponding to the icon.
   */
  public ImageView loadIcon(final String path) {
    final Image image = new Image(getClass().getResourceAsStream(path));
    final ImageView img = new ImageView(image);
    img.setFitWidth(25);
    img.setFitHeight(25);
    return img;
  }

  /** Automatically remove the message after the defined time. */
  private void autoClearMessage() {
    final PauseTransition pause = new PauseTransition(Duration.seconds(MESSAGE_TIMEOUT));
    pause.setOnFinished(event -> clearPreviousMessage());
    pause.play();
  }
}
