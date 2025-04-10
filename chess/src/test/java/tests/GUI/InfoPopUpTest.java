package tests.GUI;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import java.util.Optional;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import pdp.view.gui.popups.InfoPopUp;

class InfoPopUpTest extends ApplicationTest {

  @BeforeAll
  public static void setUpLocale() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @Test
  @Tag("gui")
  void testInfoPopUpDisplaysAndFades() {

    interact(() -> InfoPopUp.show("Test message"));

    WaitForAsyncUtils.waitForFxEvents();
    Optional<Window> popupWindow = findPopupWindow();
    assertTrue(popupWindow.isPresent(), "The popup should be displayed");

    // Wait for the popup to disappear after 3.5 seconds (safety margin)
    sleep(3500);
    popupWindow = findPopupWindow();
    assertFalse(popupWindow.isPresent(), "The popup should be closed after the animation");
  }

  /** Searches for a Stage window corresponding to the popup. */
  private Optional<Window> findPopupWindow() {
    return listTargetWindows().stream()
        .filter(window -> window instanceof Stage && window.isShowing())
        .findFirst();
  }
}
