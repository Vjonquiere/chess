package tests.GUI;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import pdp.model.Game;
import pdp.utils.OptionType;
import pdp.utils.TextGetter;
import pdp.view.gui.menu.SettingsEditorPopup;

class SettingsEditorPopupTest extends ApplicationTest {

  private File tempConfigFile;

  @BeforeAll
  public static void setUpLocale() {
      Locale.setDefault(Locale.ENGLISH);
  }

  @BeforeEach
  void setUp() throws IOException {
    Game.initialize(false, false, null, null, null, new HashMap<>());

    // Create a temporary file to simulate "default.chessrc" if needed
    tempConfigFile = File.createTempFile("default.chessrc", ".tmp");
    tempConfigFile.deleteOnExit(); // Ensures the file is deleted after the test

    // Add some dummy content to simulate the configuration file
    try (FileWriter writer = new FileWriter(tempConfigFile)) {
      writer.write("This is a simulated configuration file.");
    }

    // Set the temporary file path in the Game instance to simulate the configuration
    Game.getInstance().getOptions().put(OptionType.CONFIG, tempConfigFile.getAbsolutePath());
  }

  @Test
  @Tag("gui")
  void testSettingsEditorPopupDisplaysAndHandlesFileNotFound() {

    tempConfigFile.delete(); // Delete the temporary file to simulate "FileNotFoundException"
    openSettingsEditorPopup();

    // Check that the popup is displayed
    Optional<Window> popupWindow = findPopupWindow();
    assertTrue(popupWindow.isPresent(), "The popup should be displayed");

    clickAndCheckClose(TextGetter.getText("close"));
  }

  /** Opens the settings popup non-blocking. */
  private void openSettingsEditorPopup() {
    Platform.runLater(() -> new SettingsEditorPopup()); // Open the popup
    WaitForAsyncUtils.waitForFxEvents(); // Wait for the popup to appear
  }

  /** Clicks a button and checks that the popup closes. */
  private void clickAndCheckClose(String buttonText) {
    clickOn(buttonText);
    WaitForAsyncUtils.waitForFxEvents();
    Optional<Window> popupWindow = findPopupWindow();
    assertFalse(popupWindow.isPresent(), "The popup should be closed after clicking " + buttonText);
  }

  /** Finds the popup window. */
  private Optional<Window> findPopupWindow() {
    return listTargetWindows().stream()
        .filter(window -> window instanceof Stage && window.isShowing())
        .findFirst();
  }
}
