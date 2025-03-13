package pdp.view.GUI.popups;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.RestartCommand;
import pdp.events.EventType;
import pdp.utils.TextGetter;
import pdp.view.GUIView;

public class EndGamePopUp {
  public static void show(EventType event) {
    Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle(TextGetter.getText("endgame.title"));

    VBox layout = new VBox(10);
    layout.setStyle(
        "-fx-background-color: "
            + GUIView.theme.getBackground()
            + "; -fx-padding: 10; -fx-text-fill: black;");

    Label gameOverLabel = new Label(TextGetter.getText("gameOver"));
    Label endgameLabel = new Label();
    switch (event) {
      case DRAW_ACCEPTED ->
          endgameLabel =
              new Label(TextGetter.getText("drawAccepted") + " " + TextGetter.getText("onDraw"));
      case OUT_OF_TIME_BLACK ->
          endgameLabel = new Label(TextGetter.getText("outOfTime", TextGetter.getText("black")));
      case OUT_OF_TIME_WHITE ->
          endgameLabel = new Label(TextGetter.getText("outOfTime", TextGetter.getText("white")));
      case THREEFOLD_REPETITION ->
          endgameLabel =
              new Label(
                  TextGetter.getText("threeFoldRepetition") + "\n" + TextGetter.getText("onDraw"));
      case STALEMATE ->
          endgameLabel =
              new Label(TextGetter.getText("stalemate") + "\n" + TextGetter.getText("onDraw"));
      case BLACK_RESIGNS ->
          endgameLabel = new Label(TextGetter.getText("resigns", TextGetter.getText("black")));
      case WHITE_RESIGNS ->
          endgameLabel = new Label(TextGetter.getText("resigns", TextGetter.getText("white")));
      case CHECKMATE_BLACK ->
          endgameLabel =
              new Label(
                  TextGetter.getText(
                      "checkmate", TextGetter.getText("black"), TextGetter.getText("white")));
      case CHECKMATE_WHITE ->
          endgameLabel =
              new Label(
                  TextGetter.getText(
                      "checkmate", TextGetter.getText("white"), TextGetter.getText("black")));
      case FIFTY_MOVE_RULE ->
          endgameLabel =
              new Label(TextGetter.getText("fiftyMoveRule") + "\n" + TextGetter.getText("onDraw"));
      case AI_NOT_ENOUGH_TIME -> endgameLabel = new Label(TextGetter.getText("ai_not_enough_time"));
      case INSUFFICIENT_MATERIAL ->
          endgameLabel =
              new Label(
                  TextGetter.getText("insufficientMaterial") + "\n" + TextGetter.getText("onDraw"));
      default -> new Label(TextGetter.getText("error"));
    }
    layout.getChildren().addAll(gameOverLabel, endgameLabel);

    HBox buttonBox = new HBox();
    Button analyzeButton = new Button(TextGetter.getText("analyze"));
    analyzeButton.setId("analyzeButton");
    analyzeButton.setOnAction(
        e -> {
          System.out.println("No game analysis for now");
        });

    Button restartButton = new Button(TextGetter.getText("restart"));
    restartButton.setId("restartButton");
    restartButton.setOnAction(
        e -> {
          popupStage.close();
          BagOfCommands.getInstance().addCommand(new RestartCommand());
        });
    Button quitButton = new Button(TextGetter.getText("quit"));
    quitButton.setId("quitButton");
    quitButton.setOnAction(
        e -> {
          popupStage.close();
          Runtime.getRuntime().exit(0);
        });
    buttonBox.getChildren().addAll(analyzeButton, restartButton, quitButton);
    buttonBox.setAlignment(Pos.CENTER);
    layout.getChildren().add(buttonBox);
    layout.setAlignment(Pos.CENTER);

    Scene scene = new Scene(layout, 400, 200);
    popupStage.setScene(scene);
    popupStage.showAndWait();
  }
}
