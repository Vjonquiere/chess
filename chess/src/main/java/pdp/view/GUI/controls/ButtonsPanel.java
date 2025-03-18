package pdp.view.GUI.controls;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.CancelMoveCommand;
import pdp.controller.commands.RestartCommand;
import pdp.controller.commands.RestoreMoveCommand;
import pdp.controller.commands.SurrenderCommand;
import pdp.model.Game;
import pdp.utils.TextGetter;
import pdp.view.GUI.CustomButton;
import pdp.view.GUI.popups.YesNoPopUp;
import pdp.view.GUIView;

public class ButtonsPanel extends GridPane {
  private Button drawButton;
  private Button undoButton;
  private Button redoButton;
  private Button resignButton;
  private Button undrawButton;
  private Button restartButton;
  String buttonStyle =
      "-fx-background-color: "
          + GUIView.theme.getSecondary()
          + ";"
          + "-fx-text-fill: "
          + GUIView.theme.getPrimary()
          + ";"
          + "-fx-border-color: "
          + GUIView.theme.getPrimary()
          + ";"
          + "-fx-font-size: 18px;"
          + "-fx-font-weight: bold;"
          + "-fx-padding: 15;"
          + "-fx-background-radius: 20;"
          + "-fx-border-radius: 20;";

  public ButtonsPanel() {
    setPadding(new Insets(10));
    setHgap(10);
    setVgap(10);
    setAlignment(Pos.CENTER);
    initButtons();

    add(drawButton, 0, 0);
    add(undoButton, 1, 0);
    add(resignButton, 2, 0);

    add(undrawButton, 0, 1);
    add(redoButton, 1, 1);
    add(restartButton, 2, 1);
  }

  private void initButtons() {
    initResignButton();
    initDrawButton();
    initUndrawButton();
    initRedoButton();
    initUndoButton();
    initRestartButton();
  }

  private void initUndoButton() {
    undoButton = new CustomButton(TextGetter.getText("undo"));
    undoButton.setOnAction(
        event -> {
          BagOfCommands.getInstance().addCommand(new CancelMoveCommand());
          if (!Game.getInstance().isWhiteAI() && !Game.getInstance().isBlackAI())
            new YesNoPopUp(
                "undoInstructionsGui",
                new CancelMoveCommand(),
                () -> Game.getInstance().getGameState().undoRequestReset());
        });
  }

  private void initRedoButton() {
    redoButton = new CustomButton(TextGetter.getText("redo"));
    redoButton.setOnAction(
        event -> {
          BagOfCommands.getInstance().addCommand(new RestoreMoveCommand());
          if (!Game.getInstance().isWhiteAI() && !Game.getInstance().isBlackAI())
            new YesNoPopUp(
                "redoInstructionsGui",
                new RestoreMoveCommand(),
                () -> Game.getInstance().getGameState().redoRequestReset());
        });
  }

  private void initDrawButton() {
    drawButton = new CustomButton(TextGetter.getText("draw"));
    // TODO: add action to button
  }

  private void initUndrawButton() {
    undrawButton = new CustomButton(TextGetter.getText("undraw"));
    // TODO: add action to button
  }

  private void initResignButton() {
    resignButton = new CustomButton(TextGetter.getText("resign"));
    resignButton.setOnAction(
        event -> {
          new YesNoPopUp(
              "resignInstructionsGui",
              new SurrenderCommand(Game.getInstance().getGameState().isWhiteTurn()),
              null);
        });
  }

  private void initRestartButton() {
    restartButton = new CustomButton(TextGetter.getText("restart"));
    restartButton.setOnAction(
        event -> {
          // BagOfCommands.getInstance().addCommand(new RestartCommand());
          new YesNoPopUp("restartInstructionsGui", new RestartCommand(), null);
        });
  }
}
