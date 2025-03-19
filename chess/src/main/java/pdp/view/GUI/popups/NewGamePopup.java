package pdp.view.GUI.popups;

import java.io.File;
import java.util.HashMap;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pdp.GameInitializer;
import pdp.model.ai.AlgorithmType;
import pdp.model.ai.HeuristicType;
import pdp.utils.OptionType;
import pdp.utils.TextGetter;
import pdp.view.GUIView;

public class NewGamePopup {

  private static VBox makeAIBox(boolean isWhite, HashMap<OptionType, String> options) {

    String colorTag = isWhite ? "white" : "black";
    String colorText = TextGetter.getText(colorTag);

    OptionType modeType = isWhite ? OptionType.AI_MODE_W : OptionType.AI_MODE_B;
    OptionType heuristicType = isWhite ? OptionType.AI_HEURISTIC_W : OptionType.AI_HEURISTIC_B;
    OptionType depthType = isWhite ? OptionType.AI_DEPTH_W : OptionType.AI_DEPTH_B;
    OptionType simulationsType = isWhite ? OptionType.AI_SIMULATION_W : OptionType.AI_SIMULATION_B;

    VBox aiContainer = new VBox(5);

    aiContainer.getChildren().add(new Label(TextGetter.getText("aiModeLabel", colorText)));
    ComboBox<String> aiModeDropdown = new ComboBox<>();

    aiModeDropdown.setId(colorTag + "AiModeDropdown");

    for (AlgorithmType type : AlgorithmType.values()) {
      aiModeDropdown.getItems().add(type.toString());
    }

    if (options.containsKey(modeType)) {
      aiModeDropdown.setValue(options.get(modeType));
    }

    VBox depthContainer = new VBox(5);
    VBox simulationContainer = new VBox(5);
    VBox heuristicContainer = new VBox(5);

    aiModeDropdown
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              options.put(modeType, newVal);
              depthContainer.setVisible(newVal != AlgorithmType.MCTS.toString());
              depthContainer.setManaged(newVal != AlgorithmType.MCTS.toString());
              heuristicContainer.setVisible(newVal != AlgorithmType.MCTS.toString());
              heuristicContainer.setManaged(newVal != AlgorithmType.MCTS.toString());

              simulationContainer.setVisible(newVal == AlgorithmType.MCTS.toString());
              simulationContainer.setManaged(newVal == AlgorithmType.MCTS.toString());
            });

    aiContainer.getChildren().add(aiModeDropdown);

    heuristicContainer
        .getChildren()
        .add(new Label(TextGetter.getText("aiHeuristicLabel", colorText)));
    ComboBox<String> heuristicDropdown = new ComboBox<>();

    heuristicDropdown.setId(colorTag + "HeuristicDropdown");

    for (HeuristicType type : HeuristicType.values()) {
      heuristicDropdown.getItems().add(type.toString());
    }

    if (options.containsKey(heuristicType)) {
      heuristicDropdown.setValue(options.get(heuristicType));
    }

    heuristicDropdown
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              options.put(heuristicType, newVal);
            });

    heuristicContainer.setId(colorTag + "HeuristicContainer");
    heuristicContainer.setVisible(
        options.containsKey(modeType) && options.get(modeType) != AlgorithmType.MCTS.toString());
    heuristicContainer.setManaged(
        options.containsKey(modeType) && options.get(modeType) != AlgorithmType.MCTS.toString());

    heuristicContainer.getChildren().add(heuristicDropdown);
    aiContainer.getChildren().add(heuristicContainer);

    depthContainer.setId(colorTag + "DepthContainer");
    depthContainer.setVisible(
        options.containsKey(modeType) && options.get(modeType) != AlgorithmType.MCTS.toString());
    depthContainer.setManaged(
        options.containsKey(modeType) && options.get(modeType) != AlgorithmType.MCTS.toString());

    depthContainer.getChildren().add(new Label(TextGetter.getText("aiDepthLabel", colorText)));
    Slider depthSlider = new Slider(1, 10, 3);
    depthSlider.setId(colorTag + "DepthSlider");
    depthSlider.setShowTickLabels(true);
    depthSlider.setShowTickMarks(true);
    depthSlider.setMajorTickUnit(1);
    depthSlider.setMinorTickCount(0);
    depthSlider.setSnapToTicks(true);
    depthSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              options.put(depthType, String.valueOf(newVal.intValue()));
            });

    if (options.containsKey(depthType)) {
      depthSlider.setValue(Integer.parseInt(options.get(depthType)));
    }

    depthContainer.getChildren().add(depthSlider);

    simulationContainer.setId(colorTag + "SimulationContainer");
    simulationContainer.setVisible(
        options.containsKey(modeType) && options.get(modeType) == AlgorithmType.MCTS.toString());
    simulationContainer.setManaged(
        options.containsKey(modeType) && options.get(modeType) == AlgorithmType.MCTS.toString());

    simulationContainer
        .getChildren()
        .add(new Label(TextGetter.getText("aiSimulationsLabel", colorText)));
    Slider simulationSlider = new Slider(100, 1000, 300);
    simulationSlider.setId(colorTag + "SimulationSlider");
    simulationSlider.setShowTickLabels(true);
    simulationSlider.setShowTickMarks(true);
    simulationSlider.setMajorTickUnit(10);
    simulationSlider.setMinorTickCount(0);
    simulationSlider.setSnapToTicks(true);

    simulationSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              options.put(simulationsType, String.valueOf(newVal.intValue()));
            });

    if (options.containsKey(simulationsType)) {
      depthSlider.setValue(Integer.parseInt(options.get(simulationsType)));
    }

    simulationContainer.getChildren().add(simulationSlider);

    aiContainer.getChildren().add(depthContainer);
    aiContainer.getChildren().add(simulationContainer);

    aiContainer.getChildren().add(new Separator());

    return aiContainer;
  }

  private static VBox makeAITimeBox(HashMap<OptionType, String> options) {

    VBox aiTimeFull = new VBox();

    CheckBox aiTimeCheckBox = new CheckBox(TextGetter.getText("aiTimeLimitLabel"));
    aiTimeCheckBox.setId("aiTimeCheckBox");
    aiTimeCheckBox.setSelected(options.containsKey(OptionType.AI_TIME));

    aiTimeFull.getChildren().add(aiTimeCheckBox);

    VBox aiTimeContainer = new VBox(5);
    aiTimeContainer.setId("aiTimeContainer");
    aiTimeContainer.setVisible(aiTimeCheckBox.isSelected());
    aiTimeContainer.setManaged(aiTimeCheckBox.isSelected());

    aiTimeContainer.getChildren().add(new Label(TextGetter.getText("aiTimeLabel")));
    Slider aiTimeSlider = new Slider(5, 60, 10);
    aiTimeSlider.setId("aiTimeSlider");
    aiTimeSlider.setShowTickLabels(true);
    aiTimeSlider.setShowTickMarks(true);
    aiTimeSlider.setMajorTickUnit(5);
    aiTimeSlider.setMinorTickCount(0);
    aiTimeSlider.setSnapToTicks(true);
    aiTimeSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              options.put(OptionType.AI_TIME, String.valueOf(newVal.intValue()));
            });

    if (options.containsKey(OptionType.AI_TIME)) {
      aiTimeSlider.setValue(Integer.parseInt(options.get(OptionType.AI_TIME)));
    }

    aiTimeCheckBox.setOnAction(
        event -> {
          boolean selected = aiTimeCheckBox.isSelected();
          aiTimeContainer.setVisible(selected);
          aiTimeContainer.setManaged(selected);
          if (selected) {
            options.put(OptionType.AI_TIME, String.valueOf(Math.round(aiTimeSlider.getValue())));
          } else {
            options.remove(OptionType.AI_TIME);
          }
        });

    aiTimeContainer.getChildren().add(aiTimeSlider);

    aiTimeFull.getChildren().add(aiTimeContainer);

    return aiTimeFull;
  }

  public static void show(HashMap<OptionType, String> options) {
    Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle(TextGetter.getText("newGame.options"));

    VBox layout = new VBox(10);
    layout.setId("newGamePopUp");
    /*
    layout.setStyle(
        "-fx-background-color: "
            + GUIView.theme.getBackground()
            + "; -fx-padding: 10; -fx-alignment: center-left; -fx-text-fill: black;");

     */

    CheckBox blitzCheckBox = new CheckBox("Blitz");
    blitzCheckBox.setId("blitzCheckBox");
    blitzCheckBox.setSelected(options.containsKey(OptionType.BLITZ));

    VBox timeContainer = new VBox(5);
    timeContainer.setId("timeContainer");
    timeContainer.setVisible(blitzCheckBox.isSelected());
    timeContainer.setManaged(blitzCheckBox.isSelected());

    blitzCheckBox.setOnAction(
        event -> {
          boolean selected = blitzCheckBox.isSelected();
          timeContainer.setVisible(selected);
          timeContainer.setManaged(selected);
          if (selected) {
            options.put(OptionType.BLITZ, "");
          } else {
            options.remove(OptionType.BLITZ);
          }
        });

    layout.getChildren().add(blitzCheckBox);

    timeContainer.getChildren().add(new Label(TextGetter.getText("newGame.time")));

    Slider timeSlider = new Slider(1, 60, 30);
    timeSlider.setId("timeSlider");
    timeSlider.setShowTickLabels(true);
    timeSlider.setShowTickMarks(true);
    timeSlider.setMajorTickUnit(1);
    timeSlider.setMinorTickCount(0);
    timeSlider.setSnapToTicks(true);
    timeSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) ->
                options.put(OptionType.TIME, String.valueOf(newVal.intValue())));

    if (options.containsKey(OptionType.TIME)) {
      timeSlider.setValue(Integer.parseInt(options.get(OptionType.TIME)));
    } else {
      options.put(OptionType.TIME, String.valueOf(Math.round(timeSlider.getValue())));
    }

    timeContainer.getChildren().add(timeSlider);
    layout.getChildren().add(timeContainer);

    layout.getChildren().add(new Separator());

    ComboBox<String> aiDropdown = new ComboBox<>();
    aiDropdown.setId("aiDropdown");
    aiDropdown.getItems().add("None");
    aiDropdown.getItems().add("W");
    aiDropdown.getItems().add("B");
    aiDropdown.getItems().add("A");

    if (options.containsKey(OptionType.AI)) {
      aiDropdown.setValue(options.get(OptionType.AI));
    } else {
      aiDropdown.setValue("None");
    }
    layout.getChildren().add(new Label(TextGetter.getText("newGame.aiPlayers")));
    layout.getChildren().add(aiDropdown);

    VBox aiWhiteContainer = makeAIBox(true, options);
    layout.getChildren().add(aiWhiteContainer);
    aiWhiteContainer.setVisible(
        aiDropdown.getValue().equals("A") || aiDropdown.getValue().equals("W"));
    aiWhiteContainer.setManaged(
        aiDropdown.getValue().equals("A") || aiDropdown.getValue().equals("W"));
    aiWhiteContainer.setId("aiWhiteContainer");

    VBox aiBlackContainer = makeAIBox(false, options);
    layout.getChildren().add(aiBlackContainer);
    aiBlackContainer.setVisible(
        aiDropdown.getValue().equals("A") || aiDropdown.getValue().equals("B"));
    aiBlackContainer.setManaged(
        aiDropdown.getValue().equals("A") || aiDropdown.getValue().equals("B"));
    aiBlackContainer.setId("aiBlackContainer");

    VBox aiTimeContainer = makeAITimeBox(options);
    layout.getChildren().add(aiTimeContainer);
    aiTimeContainer.setVisible(!aiDropdown.getValue().equals("None"));
    aiTimeContainer.setManaged(!aiDropdown.getValue().equals("None"));
    aiTimeContainer.setId("fullAITimeContainer");

    aiDropdown
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              boolean selectedWhite = newVal.equals("A") || newVal.equals("W");
              boolean selectedBlack = newVal.equals("A") || newVal.equals("B");
              aiWhiteContainer.setVisible(selectedWhite);
              aiWhiteContainer.setManaged(selectedWhite);
              aiBlackContainer.setVisible(selectedBlack);
              aiBlackContainer.setManaged(selectedBlack);
              if (newVal != "None") {
                aiTimeContainer.setVisible(true);
                aiTimeContainer.setManaged(true);
                options.put(OptionType.AI, newVal);
              } else {
                aiTimeContainer.setVisible(false);
                aiTimeContainer.setManaged(false);
                options.remove(OptionType.AI);
              }
            });

    layout.getChildren().add(new Separator());

    Label loadLabel = new Label(TextGetter.getText("newGame.load"));
    TextField loadTextField = new TextField();
    loadTextField.setId("loadTextField");
    loadTextField.setPromptText(TextGetter.getText("newGame.loadPrompt"));
    loadTextField.setEditable(false);

    Button browseButton = new Button(TextGetter.getText("newGame.browse"));
    browseButton.setId("browseButton");
    browseButton.setOnAction(
        event -> {
          FileChooser fileChooser = new FileChooser();
          fileChooser.setTitle(TextGetter.getText("newGame.savePrompt"));

          fileChooser
              .getExtensionFilters()
              .add(
                  new FileChooser.ExtensionFilter(TextGetter.getText("newGame.fileSave", "*.txt")));

          File selectedFile = fileChooser.showOpenDialog(popupStage);

          if (selectedFile != null) {
            loadTextField.setText(selectedFile.getAbsolutePath());
            options.put(OptionType.LOAD, selectedFile.getAbsolutePath());
          } else {
            options.remove(OptionType.LOAD);
          }
        });

    VBox loadContainer = new VBox(5);
    loadContainer.getChildren().add(loadLabel);
    loadContainer.getChildren().add(new HBox(5, loadTextField, browseButton));

    layout.getChildren().add(loadContainer);
    layout.getChildren().add(new Separator());

    Button startGameButton = new Button(TextGetter.getText("startGame"));
    startGameButton.setId("startGameButton");
    startGameButton.setOnAction(
        event -> {
          GameInitializer.initialize(options);
          popupStage.close();
        });

    HBox buttonContainer = new HBox(startGameButton);
    buttonContainer.setAlignment(Pos.CENTER);
    layout.getChildren().add(buttonContainer);

    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setId("scrollPane");
    scrollPane.setContent(layout);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);
    scrollPane
        .getContent()
        .setStyle("-fx-background-color: " + GUIView.theme.getBackground() + ";");

    Scene scene = new Scene(scrollPane, 400, 600);
    GUIView.applyCSS(scene);
    popupStage.setScene(scene);
    popupStage.showAndWait();
  }
}
