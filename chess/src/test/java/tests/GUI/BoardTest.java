package tests.GUI;

import java.util.HashMap;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testfx.framework.junit5.ApplicationTest;
import pdp.GameControllerInit;
import pdp.model.Game;
import pdp.utils.OptionType;
import pdp.view.GUI.board.Board;
import pdp.view.GUI.board.Square;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BoardTest extends ApplicationTest {

  private HashMap<OptionType, String> options;

  @BeforeAll
  public void setup() {
    options = new HashMap<>();
    options.put(OptionType.GUI, "");
    GameControllerInit.initialize(options);
  }

  @Override
  public void start(Stage stage) {
    Platform.runLater(() -> new Board(Game.getInstance(), stage));
  }

  @Test
  @Tag("gui")
  public void testClickSquare() {

    Square blitzCheckBox = lookup("#square00").query();
  }
}
