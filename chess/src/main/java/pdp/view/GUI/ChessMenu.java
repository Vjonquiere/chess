package pdp.view.GUI;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pdp.GameInitializer;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.SaveGameCommand;
import pdp.controller.commands.StartGameCommand;
import pdp.model.Game;
import pdp.utils.OptionType;
import pdp.view.GUI.menu.HelpPopup;
import pdp.view.GUI.menu.SettingsEditorPopup;

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
    MenuItem newGameItem = new MenuItem("New Game");
    MenuItem loadGameItem = new MenuItem("Load Game");
    MenuItem saveGameItem = new MenuItem("Save Game");
    MenuItem helpItem = new MenuItem("Help");
    MenuItem quitItem = new MenuItem("Quit");
    MenuItem settingItem = new MenuItem("Settings");
    newGameItem.setOnAction(event -> openNewGamePopup());
    quitItem.setOnAction(event -> Runtime.getRuntime().exit(0));
    helpItem.setOnAction(event -> new HelpPopup());
    settingItem.setOnAction(event -> new SettingsEditorPopup());
    saveGameItem.setOnAction(
        event -> {
          String path = fileSaver();
          if (path != null && !path.isEmpty()) {
            BagOfCommands.getInstance().addCommand(new SaveGameCommand("./" + path));
          }
        });
    loadGameItem.setOnAction(
        event -> {
          File file = fileChooser();
          if (file != null) {
            HashMap<OptionType, String> map = Game.getInstance().getOptions();
            map.put(OptionType.LOAD, file.getAbsolutePath());
            GameInitializer.initialize(map);
          }
        });
    fileMenu
        .getItems()
        .addAll(newGameItem, loadGameItem, saveGameItem, helpItem, settingItem, quitItem);
    return fileMenu;
  }

  /**
   * Create a file chooser to pick a file to load
   *
   * @return The file corresponding to the path
   */
  private File fileChooser() {
    Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle("Select a game file to load");

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open a board");
    fileChooser
        .getExtensionFilters()
        .addAll(
            // new FileChooser.ExtensionFilter("Chess Files", "*.chess"),
            new FileChooser.ExtensionFilter("All Files", "*"));
    return fileChooser.showOpenDialog(popupStage);
  }

  /**
   * Create a popup dialog to get the user input for the path of the file
   *
   * @return The path given by the user
   */
  private String fileSaver() {
    TextInputDialog dialog = new TextInputDialog();
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.setHeaderText(null);
    dialog.setTitle("Enter a game file name");
    dialog.setContentText("Give a name:");

    Optional<String> result = dialog.showAndWait();
    return result.orElse(null);
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

  private void openNewGamePopup() {
    NewGamePopup.show(Game.getInstance().getOptions());
  }
}
