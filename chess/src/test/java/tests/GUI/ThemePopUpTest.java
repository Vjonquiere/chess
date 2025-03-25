package tests.GUI;

import static org.junit.jupiter.api.Assertions.*;
import static pdp.view.gui.popups.ThemePopUp.toHexString;

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
import pdp.view.gui.themes.ColorTheme;

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
  public void testColorPickerDefaultColors() {
    ColorPicker cp_primary = lookup("#primary").query();
    assertEquals(toHexString(cp_primary.getValue()), ColorTheme.CUSTOM.getPrimary());
    ColorPicker cp_secondary = lookup("#secondary").query();
    assertEquals(toHexString(cp_secondary.getValue()), ColorTheme.CUSTOM.getSecondary());
    ColorPicker cp_third = lookup("#tertiary").query();
    assertEquals(toHexString(cp_third.getValue()), ColorTheme.CUSTOM.getTertiary());
    ColorPicker cp_accent = lookup("#accent").query();
    assertEquals(toHexString(cp_accent.getValue()), ColorTheme.CUSTOM.getAccent());
    ColorPicker cp_background = lookup("#background").query();
    assertEquals(toHexString(cp_background.getValue()), ColorTheme.CUSTOM.getBackground());
    ColorPicker cp_background2 = lookup("#background2").query();
    assertEquals(toHexString(cp_background2.getValue()), ColorTheme.CUSTOM.getBackground2());
    ColorPicker cp_text = lookup("#text").query();
    assertEquals(toHexString(cp_text.getValue()), ColorTheme.CUSTOM.getText());
    ColorPicker cp_textInverted = lookup("#textInverted").query();
    assertEquals(toHexString(cp_textInverted.getValue()), ColorTheme.CUSTOM.getTextInverted());
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

  @Test
  @Tag("gui")
  public void testSaveButton() {
    Button saveButton = lookup("#saveButtonThemes").query();
    assertNotNull(saveButton);

    ColorPicker cp_primary = lookup("#primary").query();
    clickOn("#primary");
    moveBy(50, 50);
    clickOn();
    assertNotEquals(toHexString(cp_primary.getValue()), ColorTheme.CUSTOM.getPrimary());

    clickOn("#saveButtonThemes");
    assertEquals(ColorTheme.CUSTOM.getPrimary(), toHexString(cp_primary.getValue()));
  }
}
