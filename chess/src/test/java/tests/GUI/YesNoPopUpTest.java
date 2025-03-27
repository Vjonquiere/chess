package tests.GUI;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testfx.framework.junit5.ApplicationTest;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.Game;
import pdp.view.gui.popups.YesNoPopUp;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class YesNoPopUpTest extends ApplicationTest {

  @BeforeAll
  public void setup() {
    Platform.startup(() -> {}); // Initialiser JavaFX
  }

  @Override
  public void start(Stage stage) {
    // On crée la popup ici avec des IDs définis sur les boutons
    Platform.runLater(
        () -> {
          YesNoPopUp popup =
              new YesNoPopUp("hint", null, null); // Pass null for the command and action
          Button acceptButton = (Button) popup.lookup("#acceptButton");
          Button refuseButton = (Button) popup.lookup("#refuseButton");

          assertNotNull(acceptButton);
          assertNotNull(refuseButton);
        });
  }

  // Classe mock pour tester si la commande est exécutée
  static class MockCommand implements Command {
    private boolean executed = false;

    public Optional execute(Game game, GameController gameController) {
      executed = true; // Simuler l'exécution de la commande
      return null;
    }

    public boolean isExecuted() {
      return executed;
    }
  }

  @Test
  @Tag("gui")
  public void testPopupOpens() {
    Platform.runLater(
        () -> {
          YesNoPopUp popup = new YesNoPopUp("hint", null, null);
          Window popupWindow = popup.getScene().getWindow();
          assertTrue(popupWindow.isShowing());
        });
  }

  @Test
  @Tag("gui")
  public void testCommandExecutedOnAccept() {
    MockCommand mockCommand = new MockCommand();

    Platform.runLater(() -> new YesNoPopUp("hint", mockCommand, null));

    // Vérifier que la commande n'a pas encore été exécutée
    assertFalse(mockCommand.isExecuted());

    // Simuler un clic sur "Accept"
    clickOn("#acceptButton");

    // Vérifier que la commande a bien été exécutée
    assertTrue(mockCommand.isExecuted());
  }

  @Test
  @Tag("gui")
  public void testActionExecutedOnRefuse() {
    final boolean[] actionExecuted = {false};

    Platform.runLater(() -> new YesNoPopUp("hint", null, () -> actionExecuted[0] = true));

    // Vérifier que l'action n'a pas encore été exécutée
    assertFalse(actionExecuted[0]);

    // Simuler un clic sur "Refuse"
    clickOn("#refuseButton");

    // Vérifier que l'action a bien été exécutée
    assertTrue(actionExecuted[0]);
  }

  @Test
  @Tag("gui")
  public void testPopupClosesOnAction() {
    Platform.runLater(
        () -> {
          YesNoPopUp popup = new YesNoPopUp("hint", null, null);
          Window popupWindow = popup.getScene().getWindow();

          assertTrue(popupWindow.isShowing());

          // Simuler un clic sur "Accept"
          clickOn("#acceptButton");

          // Vérifier que la fenêtre est fermée
          assertFalse(popupWindow.isShowing());
        });
  }
}
