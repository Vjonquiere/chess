package pdp.view.GUI;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.VBox;

public class ChessMenu extends VBox {
  public ChessMenu() {
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().add(createFileMenu());
    menuBar.getMenus().add(createGameMenu());
    menuBar.getMenus().add(createAboutMenu());

    this.getChildren().add(menuBar);
  }

  private Menu createFileMenu() {
    Menu fileMenu = new Menu("File");
    return fileMenu;
  }

  private Menu createGameMenu() {
    Menu gameMenu = new Menu("Game");
    return gameMenu;
  }

  private Menu createAboutMenu() {
    Menu aboutMenu = new Menu("About");
    return aboutMenu;
  }
}
