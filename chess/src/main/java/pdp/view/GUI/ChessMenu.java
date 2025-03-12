package pdp.view.GUI;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.StartGameCommand;
import pdp.model.Game;
import pdp.utils.TextGetter;
import pdp.view.GUI.popups.NewGamePopup;
import pdp.view.GUI.popups.ThemePopUp;
import pdp.view.GUIView;

public class ChessMenu extends VBox {
  public ChessMenu(GUIView view) {
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().add(createFileMenu());
    menuBar.getMenus().add(createGameMenu());
    menuBar.getMenus().add(createAboutMenu());
    menuBar.getMenus().add(createOptionsMenu(view));
    menuBar
        .getStylesheets()
        .add(getClass().getResource("/styles/" + GUIView.theme + ".css").toExternalForm());
    this.getChildren().add(menuBar);
  }

  private Menu createFileMenu() {
    Menu fileMenu = new Menu("File");
    MenuItem newGameItem = new MenuItem("New Game");
    newGameItem.setOnAction(event -> openNewGamePopup());
    fileMenu.getItems().add(newGameItem);
    return fileMenu;
  }

  private Menu createGameMenu() {
    Menu gameMenu = new Menu("Game");
    MenuItem start = new MenuItem("Start");
    start.setOnAction(
        e -> {
          BagOfCommands.getInstance().addCommand(new StartGameCommand());
        });
    gameMenu.getItems().add(start);
    return gameMenu;
  }

  private Menu createAboutMenu() {
    Menu aboutMenu = new Menu("About");
    return aboutMenu;
  }

  private Menu createOptionsMenu(GUIView view) {
    Menu optionsMenu = new Menu("Options");
    MenuItem theme = new MenuItem(TextGetter.getText("theme.title"));
    theme.setOnAction(event -> openThemePopup(view));
    optionsMenu.getItems().add(theme);
    return optionsMenu;
  }

  private void openNewGamePopup() {
    NewGamePopup.show(Game.getInstance().getOptions());
  }

  private void openThemePopup(GUIView view) {
    ThemePopUp.show(view);
  }
}
