package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.GameInitializer;
import pdp.controller.GameController;
import pdp.utils.OptionType;
import pdp.view.CLIView;

class GameInitializerTest {
  private HashMap<OptionType, String> options;

  @BeforeEach
  void setUp() {
    options = new HashMap<>();
  }

  @Test
  void testGameInitializationCLI() {
    GameController controller = GameInitializer.initialize(options);
    assertNotNull(controller);
    assertTrue(controller.getView() instanceof CLIView);
  }

  /*
  @Test
  void testGameInitializationGUI() {
    options.put(OptionType.GUI, "");
    GameController controller = GameInitializer.initialize(options);
    assertNotNull(controller);
    assertTrue(controller.getView() instanceof GameView);
  }
    */

  /*
  @Test
  void testGameInitializationBlitzMode300() {
    options.put(OptionType.BLITZ, "");
    GameController controller = GameInitializer.initialize(options);
    assertNotNull(controller);
    assertNotNull(controller.getModel().getGameState().getMoveTimer());
  }
    */

  /*
  @Test
  void testGameInitializationBlitzMode300() {
    options.put(OptionType.BLITZ, "");
    options.put(OptionType.TIME, "300");
    GameController controller = GameInitializer.initialize(options);
    assertNotNull(controller);
    assertNotNull(controller.getModel().getGameState().getMoveTimer());
    assertEquals(controller.getModel().getGameState().getMoveTimer().getTimeRemaining(), 300);
  }
    */

  /*
    @Test
    void testGameInitializationAIWhite() {
      options.put(OptionType.AI, "W");
      GameController controller = GameInitializer.initialize(options);
      assertNotNull(controller);
    }

    @Test
    void testGameInitializationAIBlack() {
      options.put(OptionType.AI, "B");
      GameController controller = GameInitializer.initialize(options);
      assertNotNull(controller);
    }

    @Test
    void testGameInitializationAIAll() {
      options.put(OptionType.AI, "A");
      GameController controller = GameInitializer.initialize(options);
      assertNotNull(controller);
    }
  */
}
