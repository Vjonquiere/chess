package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.api.FxRobotInterface.*;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import java.util.HashMap;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import pdp.utils.OptionType;
import pdp.view.GUI.NewGamePopup;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NewGamePopupTest extends ApplicationTest {

  private HashMap<OptionType, String> options;

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

    lookup("#timeContainer").query().setVisible(false);

    clickOn("#blitzCheckBox");
    verifyThat("#timeContainer", isVisible());

    clickOn("#blitzCheckBox");
    verifyThat("#timeContainer", isInvisible());
  }

  @Test
  @Tag("gui")
  public void testTimeSlider() {

    Slider timeSlider = lookup("#timeSlider").query();
    assertEquals(30.0, timeSlider.getValue(), 0.1);

    timeSlider.setValue(45);
    assertEquals("45", options.get(OptionType.TIME));
  }

  @Test
  @Tag("gui")
  public void testAIDropdown() {

    ComboBox<String> aiDropdown = lookup("#aiDropdown").query();
    assertEquals("None", aiDropdown.getValue());

    clickOn(aiDropdown);
    clickOn("W");

    assertEquals("W", options.get(OptionType.AI));
  }

  @Test
  @Tag("gui")
  public void testAIModeDropdown() {

    ComboBox<String> aiModeDropdown = lookup("#aiModeDropdown").query();

    clickOn(aiModeDropdown);
    clickOn("MCTS");

    assertEquals("MCTS", options.get(OptionType.AI_MODE));
  }

  @Test
  @Tag("gui")
  public void testHeuristicDropdown() {
    ComboBox<String> heuristicDropdown = lookup("#heuristicDropdown").query();

    clickOn(heuristicDropdown);
    clickOn("SHANNON");
    assertEquals("SHANNON", options.get(OptionType.AI_HEURISTIC));
  }

  @Test
  @Tag("gui")
  public void testDepthSlider() {

    Slider depthSlider = lookup("#depthSlider").query();
    depthSlider.setValue(5);
    assertEquals("5", options.get(OptionType.AI_DEPTH));
  }

  @Test
  @Tag("gui")
  public void testAIComponentsVisibility() {

    ComboBox<String> aiDropdown = lookup("#aiDropdown").query();

    clickOn(aiDropdown);
    clickOn("None");

    VBox aiContainer = lookup("#aiContainer").query();
    assertFalse(aiContainer.isVisible());

    clickOn(aiDropdown);
    clickOn("B");

    verifyThat("#aiContainer", isVisible());

    CheckBox aiTimeCheckBox = lookup("#aiTimeCheckBox").query();

    VBox aiTimeContainer = lookup("#aiTimeContainer").query();
    assertFalse(aiTimeContainer.isVisible());

    clickOn("#aiTimeCheckBox");

    assertTrue(aiTimeContainer.isVisible());

    Slider aiTimeSlider = lookup("#aiTimeSlider").query();
    aiTimeSlider.setValue(30);
    assertEquals("30", options.get(OptionType.AI_TIME));

    clickOn("#aiTimeCheckBox");
    assertFalse(aiTimeContainer.isVisible());
  }
}
