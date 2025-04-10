package tests.GUI;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import pdp.view.gui.menu.HelpPopup;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HelpPopupTest extends ApplicationTest {

  @BeforeAll
  public static void setUpLocale() {
      Locale.setDefault(Locale.ENGLISH);
  }

  @Test
  @Tag("gui")
  void testHelpPopupDisplaysAndCloses() {
    Platform.runLater(() -> new HelpPopup());
    WaitForAsyncUtils.waitForFxEvents();

    // Verify the popup is displayed
    Optional<Window> popupWindow = findPopupWindow();
    assertTrue(popupWindow.isPresent(), "The HelpPopup should be displayed");

    // Check if the Label with help message is present
    Label helpLabel = lookup(".label").query();
    assertNotNull(helpLabel, "Help message should be visible");

    // Click the Close button
    Button closeButton = lookup(".button").query();
    interact(closeButton::fire);

    // Wait for the popup to close
    sleep(500);
    popupWindow = findPopupWindow();
    assertFalse(popupWindow.isPresent(), "The HelpPopup should close after clicking Close");
  }

  /** Finds an open popup window (Stage). */
  private Optional<Window> findPopupWindow() {
    return listTargetWindows().stream()
        .filter(window -> window instanceof Stage && window.isShowing())
        .findFirst();
  }
}
