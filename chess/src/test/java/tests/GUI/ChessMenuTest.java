package tests.GUI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testfx.framework.junit5.ApplicationTest;
import pdp.view.gui.ChessMenu;

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
    MenuItem newGame =
        fileMenu.getItems().stream()
            .filter(menuItem -> "New Game".equals(menuItem.getText()))
            .findFirst()
            .orElse(null);
    assertNotNull(newGame);
    MenuItem loadGame =
        fileMenu.getItems().stream()
            .filter(menuItem -> "Load Game".equals(menuItem.getText()))
            .findFirst()
            .orElse(null);
    assertNotNull(loadGame);
    MenuItem saveGame =
        fileMenu.getItems().stream()
            .filter(menuItem -> "Save Game".equals(menuItem.getText()))
            .findFirst()
            .orElse(null);
    assertNotNull(saveGame);
    MenuItem help =
        fileMenu.getItems().stream()
            .filter(menuItem -> "Help".equals(menuItem.getText()))
            .findFirst()
            .orElse(null);
    assertNotNull(help);
    MenuItem settings =
        fileMenu.getItems().stream()
            .filter(menuItem -> "Settings".equals(menuItem.getText()))
            .findFirst()
            .orElse(null);
    assertNotNull(settings);
    MenuItem quit =
        fileMenu.getItems().stream()
            .filter(menuItem -> "Quit".equals(menuItem.getText()))
            .findFirst()
            .orElse(null);
    assertNotNull(quit);
  }

  @Test
  @Tag("gui")
  public void testGameMenu() {
    MenuBar menuBar = lookup("#menuBar").queryAs(MenuBar.class);
    assertNotNull(menuBar);
    Menu gameMenu =
        menuBar.getMenus().stream()
            .filter(menu -> "Game".equals(menu.getText()))
            .findFirst()
            .orElse(null);

    assertNotNull(gameMenu);
    assertEquals("Game", gameMenu.getText());
    MenuItem start =
        gameMenu.getItems().stream()
            .filter(menuItem -> "Start".equals(menuItem.getText()))
            .findFirst()
            .orElse(null);
    assertNotNull(start);
    MenuItem undo =
        gameMenu.getItems().stream()
            .filter(menuItem -> "Undo".equals(menuItem.getText()))
            .findFirst()
            .orElse(null);
    assertNotNull(undo);
    MenuItem redo =
        gameMenu.getItems().stream()
            .filter(menuItem -> "Redo".equals(menuItem.getText()))
            .findFirst()
            .orElse(null);
    assertNotNull(redo);
    MenuItem restart =
        gameMenu.getItems().stream()
            .filter(menuItem -> "Redo".equals(menuItem.getText()))
            .findFirst()
            .orElse(null);
    assertNotNull(restart);
  }

  @Test
  @Tag("gui")
  public void testOptionsMenu() {
    MenuBar menuBar = lookup("#menuBar").queryAs(MenuBar.class);
    assertNotNull(menuBar);
    Menu optionsMenu =
        menuBar.getMenus().stream()
            .filter(menu -> "Options".equals(menu.getText()))
            .findFirst()
            .orElse(null);

    assertNotNull(optionsMenu);
    assertEquals("Options", optionsMenu.getText());
    MenuItem theme =
        optionsMenu.getItems().stream()
            .filter(menuItem -> "Theme".equals(menuItem.getText()))
            .findFirst()
            .orElse(null);
    assertNotNull(theme);
    MenuItem lang =
        optionsMenu.getItems().stream()
            .filter(menuItem -> "Language".equals(menuItem.getText()))
            .findFirst()
            .orElse(null);
    assertNotNull(lang);
  }
}
