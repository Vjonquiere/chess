package pdp.view.gui;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pdp.GameInitializer;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.AskHintCommand;
import pdp.controller.commands.CancelMoveCommand;
import pdp.controller.commands.ChangeLang;
import pdp.controller.commands.ChangeTheme;
import pdp.controller.commands.RestartCommand;
import pdp.controller.commands.RestoreMoveCommand;
import pdp.controller.commands.SaveGameCommand;
import pdp.controller.commands.StartGameCommand;
import pdp.model.Game;
import pdp.utils.OptionType;
import pdp.utils.TextGetter;
import pdp.view.GuiView;
import pdp.view.gui.menu.AboutPopUp;
import pdp.view.gui.menu.HelpPopup;
import pdp.view.gui.menu.MessageDisplay;
import pdp.view.gui.menu.SettingsEditorPopup;
import pdp.view.gui.popups.InfoPopUp;
import pdp.view.gui.popups.NewGamePopup;
import pdp.view.gui.popups.ThemePopUp;
import pdp.view.gui.popups.YesNoPopUp;
import pdp.view.gui.themes.ColorTheme;

/** Menu of the application. */
public class ChessMenu extends HBox {
  /** Place to display messages for the user in the menu. */
  private final MessageDisplay messageDisplay;

  /**
   * Creates the menu of out application. Composed of different menus : File, Game, About and
   * Options.
   */
  public ChessMenu() {
    messageDisplay = new MessageDisplay();
    final MenuBar menuBar = new MenuBar();
    menuBar.setId("menuBar");
    menuBar.getMenus().add(createFileMenu());
    menuBar.getMenus().add(createGameMenu());
    menuBar.getMenus().add(createAboutMenu());
    menuBar.getMenus().add(createOptionsMenu());
    this.getChildren().addAll(menuBar, messageDisplay);
  }

  /**
   * Creates the File menu. Composed of the following items : New game, Save Game, Load Game, Help,
   * Settings and Quit.
   *
   * @return Menu File
   */
  private Menu createFileMenu() {
    final Menu fileMenu = new Menu(TextGetter.getText("file"));
    fileMenu.setId("filemenu");
    final MenuItem newGameItem = new MenuItem(TextGetter.getText("newGame"));
    newGameItem.setId("newGameItem");
    final MenuItem loadGameItem = new MenuItem(TextGetter.getText("loadGame"));
    loadGameItem.setId("loadGameItem");
    final MenuItem saveGameItem = new MenuItem(TextGetter.getText("saveGame"));
    saveGameItem.setId("saveGameItem");
    final MenuItem helpItem = new MenuItem(TextGetter.getText("help"));
    helpItem.setId("helpItem");
    final MenuItem quitItem = new MenuItem(TextGetter.getText("quit"));
    quitItem.setId("quitItem");
    final MenuItem settingItem = new MenuItem(TextGetter.getText("settings"));
    settingItem.setId("settingsItem");
    newGameItem.setOnAction(event -> openNewGamePopup());
    quitItem.setOnAction(event -> Runtime.getRuntime().exit(0));
    helpItem.setOnAction(event -> new HelpPopup());
    settingItem.setOnAction(event -> new SettingsEditorPopup());
    saveGameItem.setOnAction(
        event -> {
          final String path = fileSaver();
          if (path != null && !path.isEmpty()) {
            BagOfCommands.getInstance().addCommand(new SaveGameCommand("./" + path));
          }
        });
    loadGameItem.setOnAction(
        event -> {
          final File file = fileChooser();
          if (file != null) {
            final HashMap<OptionType, String> map = Game.getInstance().getOptions();
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
   * Create a file chooser to pick a file to load.
   *
   * @return The file corresponding to the path
   */
  private File fileChooser() {
    final Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle(TextGetter.getText("fileChooser.title"));

    final FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(TextGetter.getText("fileChooser.open"));
    fileChooser
        .getExtensionFilters()
        .addAll(
            // new FileChooser.ExtensionFilter("Chess Files", "*.chess"),
            new FileChooser.ExtensionFilter(TextGetter.getText("fileChooser.allFiles"), "*"));
    return fileChooser.showOpenDialog(popupStage);
  }

  /**
   * Create a popup dialog to get the user input for the path of the file.
   *
   * @return The path given by the user
   */
  public static String fileSaver() {
    final TextInputDialog dialog = new TextInputDialog();
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.setHeaderText(null);
    dialog.setTitle(TextGetter.getText("fileSaver.title"));
    dialog.setContentText(TextGetter.getText("fileSaver.name"));
    GuiView.applyCss(dialog.getDialogPane().getScene());

    final Optional<String> result = dialog.showAndWait();
    return result.orElse(null);
  }

  /**
   * Creates the Game menu. Composed of the following items : Start, Undo, Redo, Restart, Hint.
   *
   * @return Menu Game
   */
  private Menu createGameMenu() {
    final MenuItem start = new MenuItem(TextGetter.getText("start"));
    start.setOnAction(
        e -> {
          BagOfCommands.getInstance().addCommand(new StartGameCommand());
        });
    final MenuItem undo = new MenuItem(TextGetter.getText("undo"));
    undo.setOnAction(
        e -> {
          if (Game.getInstance().getGameState().getFullTurn() > 0) {
            BagOfCommands.getInstance().addCommand(new CancelMoveCommand());
            if (!Game.getInstance().isWhiteAi() && !Game.getInstance().isBlackAi()) {
              new YesNoPopUp(
                  "undoInstructionsGui",
                  new CancelMoveCommand(),
                  () -> Game.getInstance().getGameState().undoRequestReset());
            }
          } else {
            InfoPopUp.show(TextGetter.getText("notAllowed"));
          }

          if (Game.getInstance().isWhiteAi() && Game.getInstance().isBlackAi()) {
            InfoPopUp.show(TextGetter.getText("notAllowed"));
          }
        });
    final MenuItem redo = new MenuItem(TextGetter.getText("redo"));
    redo.setOnAction(
        e -> {
          if (Game.getInstance().getHistory().getCurrentMove().orElse(null) != null
              && Game.getInstance().getHistory().getCurrentMove().get().getNext().orElse(null)
                  != null) {
            BagOfCommands.getInstance().addCommand(new RestoreMoveCommand());
            if (!Game.getInstance().isWhiteAi() && !Game.getInstance().isBlackAi()) {
              new YesNoPopUp(
                  "redoInstructionsGui",
                  new RestoreMoveCommand(),
                  () -> Game.getInstance().getGameState().redoRequestReset());
            }
          } else {
            InfoPopUp.show(TextGetter.getText("notAllowed"));
          }
          if (Game.getInstance().isWhiteAi() && Game.getInstance().isBlackAi()) {
            InfoPopUp.show(TextGetter.getText("notAllowed"));
          }
        });
    final MenuItem restart = new MenuItem(TextGetter.getText("restart"));
    restart.setOnAction(
        e -> {
          BagOfCommands.getInstance().addCommand(new RestartCommand());
          new YesNoPopUp("restartInstructionsGui", new RestartCommand(), null);
        });
    final MenuItem hint = new MenuItem(TextGetter.getText("hint"));
    hint.setOnAction(
        e -> {
          new YesNoPopUp("hintInstructionsGui", new AskHintCommand(), null);
        });
    final Menu gameMenu = new Menu(TextGetter.getText("game"));
    gameMenu.getItems().add(start);
    gameMenu.getItems().add(undo);
    gameMenu.getItems().add(redo);
    gameMenu.getItems().add(restart);
    gameMenu.getItems().add(hint);
    return gameMenu;
  }

  /**
   * Creates the About menu. Composed of the following items : Themes and Language.
   *
   * @return Menu About
   */
  private Menu createAboutMenu() {
    final Menu aboutMenu = new Menu(TextGetter.getText("about"));
    aboutMenu.setId("aboutMenu");
    final MenuItem about = new MenuItem(TextGetter.getText("about"));
    about.setOnAction(event -> new AboutPopUp());
    aboutMenu.getItems().add(about);
    return aboutMenu;
  }

  /**
   * Creates the Options menu. Composed of the following items : Themes and Language.
   *
   * @return Menu Options
   */
  private Menu createOptionsMenu() {
    final Menu optionsMenu = new Menu(TextGetter.getText("options"));
    RadioMenuItem monitor = new RadioMenuItem("monitor");
    monitor.setSelected(GuiView.getMonitoringStatus());
    monitor.setOnAction(
        event -> {
          System.out.println("monitor toggled");
          GuiView.toggleMonitoring();
        });
    optionsMenu.getItems().addAll(createThemeMenuItem(), createLangMenu(), monitor);
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
    final Menu themes = new Menu(TextGetter.getText("theme"), null);

    for (final ColorTheme c : ColorTheme.values()) {
      final MenuItem theme = new MenuItem(c.name());
      theme.setOnAction(
          e -> {
            GuiView.setTheme(c);
            BagOfCommands.getInstance().addCommand(new ChangeTheme());
          });
      themes.getItems().add(theme);
    }
    final MenuItem customize = new MenuItem("Customize");
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
    final Menu lang = new Menu(TextGetter.getText("language"), null);
    lang.setId("language");
    final MenuItem english = new MenuItem(TextGetter.getText("english"));
    english.setOnAction(
        e -> {
          TextGetter.setLocale("en");
          BagOfCommands.getInstance().addCommand(new ChangeLang());
        });
    final MenuItem french = new MenuItem(TextGetter.getText("french"));
    french.setId("french");
    french.setOnAction(
        e -> {
          TextGetter.setLocale("fr");
          BagOfCommands.getInstance().addCommand(new ChangeLang());
        });
    lang.getItems().addAll(english, french);

    return lang;
  }

  /**
   * Display a message in the menu bar.
   *
   * @param message The message to display.
   * @param error The type of message.
   * @param infinite true if the message needs to be kept while no new message arrived, false
   *     otherwise.
   */
  public void displayMessage(final String message, final boolean error, final boolean infinite) {
    if (error) {
      messageDisplay.displayError(message, infinite);
    } else {
      messageDisplay.displayInfo(message, infinite);
    }
  }
}
