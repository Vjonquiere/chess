package tests.GUI;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import pdp.events.EventType;
import pdp.utils.TextGetter;
import pdp.view.gui.popups.EndGamePopUp;

class EndGamePopUpTest extends ApplicationTest {

  @Test
  @Tag("gui")
  void testEndGamePopupDisplaysAndButtonsClosePopup() {
    openEndGamePopup();

    // Verify that the popup is displayed
    Optional<Window> popupWindow = findPopupWindow();
    assertTrue(popupWindow.isPresent(), "The popup should be displayed");

    // Click the restart button and check if the popup closes
    clickAndCheckClose(TextGetter.getText("restart"));

    // Verify Save button: open a nzw endgaem popup ? and the save popup
    // Verify Quit button ( error because : we use Runtime.getRuntime().exit(0);)
    // verify New Game button : close the actual popup and open a new one )
  }

  /** Opens the endgame popup in a non-blocking manner. */
  private void openEndGamePopup() {
    Platform.runLater(() -> EndGamePopUp.show(EventType.CHECKMATE_WHITE));
    WaitForAsyncUtils.waitForFxEvents(); // Wait for the popup to appear
  }

  /**
   * Clicks a button and verifies that the popup closes.
   *
   * @param buttonText The text of the button to click.
   */
  private void clickAndCheckClose(String buttonText) {
    clickOn(buttonText);
    WaitForAsyncUtils.waitForFxEvents();

    // Verify that the popup is closed after clicking the button
    Optional<Window> popupWindow = findPopupWindow();
    assertFalse(popupWindow.isPresent(), "The popup should be closed after clicking " + buttonText);
  }

  /**
   * Finds the popup window.
   *
   * @return An Optional containing the popup window if found.
   */
  private Optional<Window> findPopupWindow() {
    return listTargetWindows().stream()
        .filter(window -> window instanceof Stage && window.isShowing())
        .findFirst();
  }
}
