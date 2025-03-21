package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import java.util.HashMap;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import pdp.utils.OptionType;
import pdp.view.gui.popups.NewGamePopup;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NewGamePopupTest extends ApplicationTest {

  private HashMap<OptionType, String> options;

  private boolean scrollUntilVisible(String id) {

    ScrollPane scrollPane = lookup("#scrollPane").query();

    while (scrollPane.getVvalue() != 0) {
      scroll(-1);
    }

    double previousScrollPosition = -1;
    double currentScrollPosition = 0;

    while (previousScrollPosition != currentScrollPosition) {
      try {
        verifyThat(id, isVisible());
        return true;
      } catch (AssertionError e) {
        scroll(1);
        waitForFxEvents();

        previousScrollPosition = currentScrollPosition;
        currentScrollPosition = scrollPane.getVvalue();
      }
    }

    return false;
  }

  @BeforeAll
  public void setup() {
    options = new HashMap<>();
  }

  @Override
  public void start(Stage stage) {
    Platform.runLater(() -> NewGamePopup.show(options));
  }

  @Test
  @Tag("gui")
  public void testBlitzCheckBox() {

    CheckBox blitzCheckBox = lookup("#blitzCheckBox").query();
    assertTrue(scrollUntilVisible("#blitzCheckBox"));
    blitzCheckBox.fire();

    assertTrue(scrollUntilVisible("#timeContainer"));

    blitzCheckBox.fire();
    assertFalse(scrollUntilVisible("#timeContainer"));
  }

  @Test
  @Tag("gui")
  public void testTimeSlider() {
    Slider timeSlider = lookup("#timeSlider").query();
    assertEquals(30.0, timeSlider.getValue(), 0.1);
    assertTrue(scrollUntilVisible("#timeSlider"));

    timeSlider.setValue(45);
    assertEquals("45", options.get(OptionType.TIME));
  }

  @Test
  @Tag("gui")
  public void testAIDropdown() {
    ComboBox<String> aiDropdown = lookup("#aiDropdown").query();
    assertTrue(scrollUntilVisible("#aiDropdown"));

    clickOn(aiDropdown);
    clickOn("None");
    assertEquals("None", aiDropdown.getValue());
    assertEquals(null, options.get(OptionType.AI));

    clickOn(aiDropdown);
    clickOn("W");

    assertEquals("W", aiDropdown.getValue());
    assertEquals("W", options.get(OptionType.AI));
  }

  @Test
  @Tag("gui")
  public void testHeuristicDropdown() {

    ComboBox<String> aiDropdown = lookup("#aiDropdown").query();
    assertTrue(scrollUntilVisible("#aiDropdown"));

    clickOn(aiDropdown);
    clickOn("W");

    ComboBox<String> whiteAiModeDropdown = lookup("#whiteAiModeDropdown").query();
    assertTrue(scrollUntilVisible("#whiteAiModeDropdown"));
    clickOn(whiteAiModeDropdown);
    clickOn("ALPHA_BETA");

    ComboBox<String> whiteHeuristicDropdown = lookup("#whiteHeuristicDropdown").query();
    assertTrue(scrollUntilVisible("#whiteHeuristicDropdown"));
    clickOn(whiteHeuristicDropdown);
    clickOn("MATERIAL");
    assertEquals("MATERIAL", options.get(OptionType.AI_HEURISTIC_W));
  }

  @Test
  @Tag("gui")
  public void testWhiteDepthSlider() {

    ComboBox<String> aiDropdown = lookup("#aiDropdown").query();
    assertTrue(scrollUntilVisible("#aiDropdown"));

    clickOn(aiDropdown);
    clickOn("W");

    Slider depthSlider = lookup("#whiteDepthSlider").query();
    assertTrue(scrollUntilVisible("#whiteDepthSlider"));
    depthSlider.setValue(5);
    assertEquals("5", options.get(OptionType.AI_DEPTH_W));
  }

  @Test
  @Tag("gui")
  public void testBlackDepthSlider() {

    ComboBox<String> aiDropdown = lookup("#aiDropdown").query();
    assertTrue(scrollUntilVisible("#aiDropdown"));

    clickOn(aiDropdown);
    clickOn("B");

    Slider depthSlider = lookup("#blackDepthSlider").query();
    assertTrue(scrollUntilVisible("#blackDepthSlider"));
    depthSlider.setValue(5);
    assertEquals("5", options.get(OptionType.AI_DEPTH_B));
  }

  @Test
  @Tag("gui")
  public void testAIComponentsVisibility() {

    ComboBox<String> aiDropdown = lookup("#aiDropdown").query();
    assertTrue(scrollUntilVisible("#aiDropdown"));

    clickOn(aiDropdown);
    clickOn("None");

    assertFalse(scrollUntilVisible("#aiWhiteContainer"));
    assertFalse(scrollUntilVisible("#aiBlackContainer"));

    assertTrue(scrollUntilVisible("#aiDropdown"));
    clickOn(aiDropdown);
    clickOn("B");

    assertTrue(scrollUntilVisible("#aiBlackContainer"));
    assertFalse(scrollUntilVisible("#aiWhiteContainer"));

    CheckBox aiTimeCheckBox = lookup("#aiTimeCheckBox").query();

    assertFalse(scrollUntilVisible("#aiTimeContainer"));

    aiTimeCheckBox.fire();

    assertTrue(scrollUntilVisible("#aiTimeContainer"));

    Slider aiTimeSlider = lookup("#aiTimeSlider").query();
    aiTimeSlider.setValue(30);
    assertEquals("30", options.get(OptionType.AI_TIME));

    aiTimeCheckBox.fire();
    assertFalse(scrollUntilVisible("#aiTimeContainer"));

    assertTrue(scrollUntilVisible("#aiDropdown"));
    clickOn(aiDropdown);
    clickOn("W");

    assertFalse(scrollUntilVisible("#aiBlackContainer"));
    assertTrue(scrollUntilVisible("#aiWhiteContainer"));

    assertTrue(scrollUntilVisible("#aiDropdown"));
    clickOn(aiDropdown);
    clickOn("A");

    assertTrue(scrollUntilVisible("#aiBlackContainer"));
    assertTrue(scrollUntilVisible("#aiWhiteContainer"));
  }
}
