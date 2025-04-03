package tests.GUI;

import static org.junit.jupiter.api.Assertions.*;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testfx.framework.junit5.ApplicationTest;
import pdp.view.gui.popups.ThemePopUp;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ThemePopUpTest extends ApplicationTest {

  @BeforeAll
  public void setup() {}

  @Override
  public void start(Stage stage) {
    Platform.runLater(ThemePopUp::show);
  }

  @Test
  @Tag("gui")
  public void testColorPickerInPlace() {
    ColorPicker cp_primary = lookup("#primary").query();
    assertNotNull(cp_primary);
    ColorPicker cp_secondary = lookup("#secondary").query();
    assertNotNull(cp_secondary);
    ColorPicker cp_third = lookup("#tertiary").query();
    assertNotNull(cp_third);
    ColorPicker cp_accent = lookup("#accent").query();
    assertNotNull(cp_accent);
    ColorPicker cp_background = lookup("#background").query();
    assertNotNull(cp_background);
    ColorPicker cp_background2 = lookup("#background2").query();
    assertNotNull(cp_background2);
    ColorPicker cp_text = lookup("#text").query();
    assertNotNull(cp_text);
    ColorPicker cp_textInverted = lookup("#textInverted").query();
    assertNotNull(cp_textInverted);
  }

  @Test
  @Tag("gui")
  public void testCloseButton() {
    Button closeButton = lookup("#cancelButtonThemes").query();
    assertNotNull(closeButton);

    Window popupWindow = lookup("#vbox").query().getScene().getWindow();
    assertTrue(popupWindow.isShowing());

    clickOn("#cancelButtonThemes");

    assertFalse(popupWindow.isShowing());
  }
}
