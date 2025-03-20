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
import pdp.controller.commands.CancelMoveCommand;
import pdp.controller.commands.ChangeTheme;
import pdp.controller.commands.RestartCommand;
import pdp.controller.commands.RestoreMoveCommand;
import pdp.controller.commands.SaveGameCommand;
import pdp.controller.commands.StartGameCommand;
import pdp.model.Game;
import pdp.utils.OptionType;
import pdp.utils.TextGetter;
import pdp.view.GUI.menu.HelpPopup;
import pdp.view.GUI.menu.SettingsEditorPopup;
import pdp.view.GUI.popups.NewGamePopup;
import pdp.view.GUI.popups.ThemePopUp;
import pdp.view.GUI.popups.YesNoPopUp;
import pdp.view.GUI.themes.ColorTheme;
import pdp.view.GUIView;

public class ChessMenu extends VBox {
  public ChessMenu(GUIView view) {
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().add(createFileMenu());
    menuBar.getMenus().add(createGameMenu());
    menuBar.getMenus().add(createAboutMenu());
    menuBar.getMenus().add(createOptionsMenu(view));
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
    MenuItem undo = new MenuItem(TextGetter.getText("undo"));
    MenuItem redo = new MenuItem(TextGetter.getText("redo"));
    MenuItem restart = new MenuItem(TextGetter.getText("restart"));
    MenuItem hint = new MenuItem(TextGetter.getText("hint"));
    start.setOnAction(
        e -> {
          BagOfCommands.getInstance().addCommand(new StartGameCommand());
        });
    undo.setOnAction(
        e -> {
          BagOfCommands.getInstance().addCommand(new CancelMoveCommand());
          if (!Game.getInstance().isWhiteAI() && !Game.getInstance().isBlackAI())
            new YesNoPopUp(
                "undoInstructionsGui",
                new CancelMoveCommand(),
                () -> Game.getInstance().getGameState().undoRequestReset());
        });
    redo.setOnAction(
        e -> {
          BagOfCommands.getInstance().addCommand(new RestoreMoveCommand());
          if (!Game.getInstance().isWhiteAI() && !Game.getInstance().isBlackAI())
            new YesNoPopUp(
                "redoInstructionsGui",
                new RestoreMoveCommand(),
                () -> Game.getInstance().getGameState().redoRequestReset());
        });
    restart.setOnAction(
        e -> {
          BagOfCommands.getInstance().addCommand(new RestartCommand());
          new YesNoPopUp("restartInstructionsGui", new RestartCommand(), null);
        });

    hint.setOnAction(
        e -> {
          /* Solver hintSolver = new Solver();
          Move hintMove = hintSolver.getAlgorithm().findBestMove(Game.getInstance(), 2, Game.getInstance().getGameState().isWhiteTurn()).move();
          System.out.println("best move hint : " + hintMove.getSource()+ " " + hintMove.getDest()); */
          new YesNoPopUp("hintInstructionsGui", null, null);
        });
    gameMenu.getItems().add(start);
    gameMenu.getItems().add(undo);
    gameMenu.getItems().add(redo);
    gameMenu.getItems().add(restart);
    gameMenu.getItems().add(hint);
    return gameMenu;
  }

  private Menu createAboutMenu() {
    Menu aboutMenu = new Menu("About");
    return aboutMenu;
  }

  private Menu createOptionsMenu(GUIView view) {
    Menu optionsMenu = new Menu("Options");
    // MenuItem theme = new MenuItem(TextGetter.getText("theme.title"));
    // theme.setOnAction(event -> openThemePopup(view));
    optionsMenu.getItems().add(createThemeMenuItem());
    return optionsMenu;
  }

  private void openNewGamePopup() {
    NewGamePopup.show(Game.getInstance().getOptions());
  }

  private void openThemePopup(GUIView view) {
    ThemePopUp.show(view);
  }

  private Menu createThemeMenuItem() {
    Menu themes = new Menu("Theme", null);

    for (ColorTheme c : ColorTheme.values()) {
      MenuItem theme = new MenuItem(c.name());
      theme.setOnAction(
          e -> {
            GUIView.theme = c;
            BagOfCommands.getInstance().addCommand(new ChangeTheme());
          });
      themes.getItems().add(theme);
    }
    return themes;
  }
}
