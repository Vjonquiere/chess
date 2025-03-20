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
import pdp.controller.commands.*;
import pdp.model.Game;
import pdp.utils.OptionType;
import pdp.utils.TextGetter;
import pdp.view.GUI.menu.HelpPopup;
import pdp.view.GUI.menu.SettingsEditorPopup;
import pdp.view.GUI.popups.NewGamePopup;
import pdp.view.GUI.popups.RedoPopUp;
import pdp.view.GUI.popups.ThemePopUp;
import pdp.view.GUI.popups.UndoPopUp;
import pdp.view.GUI.themes.ColorTheme;
import pdp.view.GUIView;

public class ChessMenu extends VBox {
  /**
   * Creates the menu of out application. Composed of different menus : File, Game, About and
   * Options.
   */
  public ChessMenu() {
    MenuBar menuBar = new MenuBar();
    menuBar.setId("menuBar");
    menuBar.getMenus().add(createFileMenu());
    menuBar.getMenus().add(createGameMenu());
    menuBar.getMenus().add(createAboutMenu());
    menuBar.getMenus().add(createOptionsMenu());
    this.getChildren().add(menuBar);
  }

  /**
   * Creates the File menu. Composed of the following items : New game, Save Game, Load Game, Help,
   * Settings and Quit.
   *
   * @return Menu File
   */
  private Menu createFileMenu() {
    Menu fileMenu = new Menu(TextGetter.getText("file"));
    fileMenu.setId("filemenu");
    MenuItem newGameItem = new MenuItem(TextGetter.getText("newGame"));
    newGameItem.setId("newGameItem");
    MenuItem loadGameItem = new MenuItem(TextGetter.getText("loadGame"));
    loadGameItem.setId("loadGameItem");
    MenuItem saveGameItem = new MenuItem(TextGetter.getText("saveGame"));
    saveGameItem.setId("saveGameItem");
    MenuItem helpItem = new MenuItem(TextGetter.getText("help"));
    helpItem.setId("helpItem");
    MenuItem quitItem = new MenuItem(TextGetter.getText("quit"));
    quitItem.setId("quitItem");
    MenuItem settingItem = new MenuItem(TextGetter.getText("settings"));
    settingItem.setId("settingsItem");
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
    popupStage.setTitle(TextGetter.getText("fileChooser.title"));

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(TextGetter.getText("fileChooser.open"));
    fileChooser
        .getExtensionFilters()
        .addAll(
            // new FileChooser.ExtensionFilter("Chess Files", "*.chess"),
            new FileChooser.ExtensionFilter(TextGetter.getText("fileChooser.allFiles"), "*"));
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
    dialog.setTitle(TextGetter.getText("fileSaver.title"));
    dialog.setContentText(TextGetter.getText("fileSaver.name"));
    GUIView.applyCSS(dialog.getDialogPane().getScene());

    Optional<String> result = dialog.showAndWait();
    return result.orElse(null);
  }

  /**
   * Creates the Game menu. Composed of the following items : Start, Undo, Redo, Restart.
   *
   * @return Menu Game
   */
  private Menu createGameMenu() {
    Menu gameMenu = new Menu(TextGetter.getText("game"));
    MenuItem start = new MenuItem(TextGetter.getText("start"));
    MenuItem undo = new MenuItem(TextGetter.getText("undo"));
    MenuItem redo = new MenuItem(TextGetter.getText("redo"));
    MenuItem restart = new MenuItem(TextGetter.getText("restart"));
    start.setOnAction(
        e -> {
          BagOfCommands.getInstance().addCommand(new StartGameCommand());
        });
    undo.setOnAction(
        e -> {
          BagOfCommands.getInstance().addCommand(new CancelMoveCommand());
          if (!Game.getInstance().isWhiteAI() && !Game.getInstance().isBlackAI()) new UndoPopUp();
        });
    redo.setOnAction(
        e -> {
          BagOfCommands.getInstance().addCommand(new RestoreMoveCommand());
          if (!Game.getInstance().isWhiteAI() && !Game.getInstance().isBlackAI()) new RedoPopUp();
        });
    restart.setOnAction(
        e -> {
          BagOfCommands.getInstance().addCommand(new RestartCommand());
        });
    gameMenu.getItems().add(start);
    gameMenu.getItems().add(undo);
    gameMenu.getItems().add(redo);
    gameMenu.getItems().add(restart);
    return gameMenu;
  }

  private Menu createAboutMenu() {
    Menu aboutMenu = new Menu(TextGetter.getText("about"));
    return aboutMenu;
  }

  /**
   * Creates the Options menu. Composed of the following items : Themes and Language.
   *
   * @return Menu Options
   */
  private Menu createOptionsMenu() {
    Menu optionsMenu = new Menu(TextGetter.getText("options"));
    optionsMenu.getItems().addAll(createThemeMenuItem(), createLangMenu());
    return optionsMenu;
  }

  /** Launches a popup to customize a new game. */
  private void openNewGamePopup() {
    NewGamePopup.show(Game.getInstance().getOptions());
  }

  /** Launches a popup to customize the application's theme. */
  private void openThemePopup() {
    ThemePopUp.show();
  }

  /**
   * Creates the Theme menu. Composed of the different themes present in the ColorTheme Enum and of
   * a Customize item that launches a popup to choose your own theme.
   *
   * @return Menu Game
   */
  private Menu createThemeMenuItem() {
    Menu themes = new Menu(TextGetter.getText("theme"), null);

    for (ColorTheme c : ColorTheme.values()) {
      MenuItem theme = new MenuItem(c.name());
      theme.setOnAction(
          e -> {
            GUIView.theme = c;
            BagOfCommands.getInstance().addCommand(new ChangeTheme());
          });
      themes.getItems().add(theme);
    }
    MenuItem customize = new MenuItem("Customize");
    customize.setOnAction(
        e -> {
          openThemePopup();
        });
    themes.getItems().add(customize);
    return themes;
  }

  /**
   * Creates the Language Menu. Composed of the following languages : English and French.
   * Automatically updates the whole application.
   *
   * @return Menu Game
   */
  private Menu createLangMenu() {
    Menu lang = new Menu(TextGetter.getText("language"), null);
    lang.setId("language");
    MenuItem english = new MenuItem(TextGetter.getText("english"));
    english.setOnAction(
        e -> {
          TextGetter.setLocale("en");
          BagOfCommands.getInstance().addCommand(new ChangeLang());
        });
    MenuItem french = new MenuItem(TextGetter.getText("french"));
    french.setId("french");
    french.setOnAction(
        e -> {
          TextGetter.setLocale("fr");
          BagOfCommands.getInstance().addCommand(new ChangeLang());
        });
    lang.getItems().addAll(english, french);

    return lang;
  }
}
