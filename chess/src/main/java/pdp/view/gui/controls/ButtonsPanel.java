package pdp.view.gui.controls;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.CancelDrawCommand;
import pdp.controller.commands.CancelMoveCommand;
import pdp.controller.commands.ProposeDrawCommand;
import pdp.controller.commands.RestartCommand;
import pdp.controller.commands.RestoreMoveCommand;
import pdp.controller.commands.SurrenderCommand;
import pdp.model.Game;
import pdp.utils.TextGetter;
import pdp.view.GuiView;
import pdp.view.gui.CustomButton;
import pdp.view.gui.popups.YesNoPopUp;

public class ButtonsPanel extends GridPane {
  private Button drawButton;
  private Button undoButton;
  private Button redoButton;
  private Button resignButton;
  private Button undrawButton;
  private Button restartButton;
  private int buttonMinWidth = 100;
  String buttonStyle =
      "-fx-background-color: "
          + GuiView.theme.getSecondary()
          + ";"
          + "-fx-text-fill: "
          + GuiView.theme.getPrimary()
          + ";"
          + "-fx-border-color: "
          + GuiView.theme.getPrimary()
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

  /**
   * Initializes the undo button and sets its action. When clicked, the button allows the player to
   * undo the last move. If neither player is controlled by AI, a confirmation popup appears, and
   * the move is undone if the player accepts.
   */
  private void initUndoButton() {
    undoButton = new CustomButton(TextGetter.getText("undo"), buttonMinWidth);
    undoButton.setOnAction(
        event -> {
          BagOfCommands.getInstance().addCommand(new CancelMoveCommand());
          if (!Game.getInstance().isWhiteAi() && !Game.getInstance().isBlackAi())
            new YesNoPopUp(
                "undoInstructionsGui",
                new CancelMoveCommand(),
                () -> Game.getInstance().getGameState().undoRequestReset());
        });
  }

  /**
   * Initializes the redo button and sets its action. When clicked, the button allows the player to
   * redo the last undone move. If neither player is controlled by AI, a confirmation popup appears,
   * and the move is redone if the player accepts.
   */
  private void initRedoButton() {
    redoButton = new CustomButton(TextGetter.getText("redo"), buttonMinWidth);
    redoButton.setOnAction(
        event -> {
          BagOfCommands.getInstance().addCommand(new RestoreMoveCommand());
          if (!Game.getInstance().isWhiteAi() && !Game.getInstance().isBlackAi())
            new YesNoPopUp(
                "redoInstructionsGui",
                new RestoreMoveCommand(),
                () -> Game.getInstance().getGameState().redoRequestReset());
        });
  }

  /**
   * Initializes the draw button and sets its action. When clicked, the button allows the player to
   * propose a draw. A confirmation popup appears for the player to confirm their proposal before
   * sending it.
   */
  private void initDrawButton() {
    drawButton = new CustomButton(TextGetter.getText("draw"), buttonMinWidth);
    drawButton.setOnAction(
        event -> {
          new YesNoPopUp(
              "drawInstructionsGui",
              new ProposeDrawCommand(Game.getInstance().getGameState().isWhiteTurn()),
              null);
        });
  }

  /**
   * Initializes the undraw button and sets its action. When clicked, the button allows the player
   * to cancel their draw proposal. A confirmation popup appears for the player to confirm the
   * cancellation.
   */
  private void initUndrawButton() {
    undrawButton = new CustomButton(TextGetter.getText("undraw"), buttonMinWidth);
    undrawButton.setOnAction(
        event -> {
          new YesNoPopUp(
              "undrawInstructionsGui",
              new CancelDrawCommand(Game.getInstance().getGameState().isWhiteTurn()),
              null);
        });
  }

  /**
   * Initializes the resign button and sets its action. When clicked, the button allows the player
   * to resign from the game. A confirmation popup appears for the player to confirm their
   * resignation before ending the game.
   */
  private void initResignButton() {
    resignButton = new CustomButton(TextGetter.getText("resign"), buttonMinWidth);
    resignButton.setOnAction(
        event -> {
          new YesNoPopUp(
              "resignInstructionsGui",
              new SurrenderCommand(Game.getInstance().getGameState().isWhiteTurn()),
              null);
        });
  }

  /**
   * Initializes the restart button and sets its action. When clicked, the button allows the player
   * to restart the game. A confirmation popup appears for the player to confirm the restart before
   * the game is reset.
   */
  private void initRestartButton() {
    restartButton = new CustomButton(TextGetter.getText("restart"), buttonMinWidth);
    restartButton.setOnAction(
        event -> {
          // BagOfCommands.getInstance().addCommand(new RestartCommand());
          new YesNoPopUp("restartInstructionsGui", new RestartCommand(), null);
        });
  }
}
