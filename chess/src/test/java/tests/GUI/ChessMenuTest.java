package tests.GUI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testfx.framework.junit5.ApplicationTest;
import pdp.view.GUI.ChessMenu;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChessMenuTest extends ApplicationTest {

  ChessMenu chessMenu;

  @BeforeAll
  public void setup() {}

  @Override
  public void start(Stage stage) {
    Platform.runLater(
        () -> {
          chessMenu = new ChessMenu();
          Scene scene = new Scene(chessMenu);
          stage.setScene(scene);
          stage.show();
        });
  }

  @Test
  @Tag("gui")
  public void testFileMenu() {
    MenuBar menuBar = lookup("#menuBar").queryAs(MenuBar.class);
    assertNotNull(menuBar);
    Menu fileMenu =
        menuBar.getMenus().stream()
            .filter(menu -> "File".equals(menu.getText()))
            .findFirst()
            .orElse(null);

    assertNotNull(fileMenu);
    assertEquals("File", fileMenu.getText());
  }

  @Test
  @Tag("gui")
  public void testGameMenu() {}
}
