package pdp.view.gui.popups;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
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
import pdp.view.GuiView;

/** GUI popup to configure a new game with all defined options (AI, blitz, ...). */
public final class NewGamePopup {

  /** Private constructor to avoid instanciating a utility class. */
  private NewGamePopup() {}

  /**
   * GUI widget that represent configurable options for AI.
   *
   * @param isWhite Side of the AI.
   * @param options Current options.
   * @return A javaFx object to configure AI.
   */
  private static VBox makeAiBox(final boolean isWhite, final HashMap<OptionType, String> options) {
    options.remove(OptionType.LOAD);

    final String colorTag = isWhite ? "white" : "black";
    final String colorText = TextGetter.getText(colorTag);

    final VBox aiContainer = new VBox(5);

    aiContainer.getChildren().add(new Label(TextGetter.getText("aiModeLabel", colorText)));
    final ComboBox<String> aiModeDropdown = new ComboBox<>();

    aiModeDropdown.setId(colorTag + "AiModeDropdown");

    for (final AlgorithmType type : AlgorithmType.values()) {
      aiModeDropdown.getItems().add(type.toString());
    }

    final OptionType modeType = isWhite ? OptionType.AI_MODE_W : OptionType.AI_MODE_B;
    if (options.containsKey(modeType)) {
      aiModeDropdown.setValue(options.get(modeType));
    }

    final VBox depthContainer = new VBox(5);
    final VBox simulationContainer = new VBox(5);
    final VBox heuristicContainer = new VBox(5);

    aiModeDropdown
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              options.put(modeType, newVal);
              depthContainer.setVisible(!Objects.equals(newVal, AlgorithmType.MCTS.toString()));
              depthContainer.setManaged(!Objects.equals(newVal, AlgorithmType.MCTS.toString()));
              heuristicContainer.setVisible(!Objects.equals(newVal, AlgorithmType.MCTS.toString()));
              heuristicContainer.setManaged(!Objects.equals(newVal, AlgorithmType.MCTS.toString()));

              simulationContainer.setVisible(Objects.equals(newVal, AlgorithmType.MCTS.toString()));
              simulationContainer.setManaged(Objects.equals(newVal, AlgorithmType.MCTS.toString()));
            });

    aiContainer.getChildren().add(aiModeDropdown);

    heuristicContainer
        .getChildren()
        .add(new Label(TextGetter.getText("aiHeuristicLabel", colorText)));
    final ComboBox<String> heuristicDropdown = new ComboBox<>();

    heuristicDropdown.setId(colorTag + "HeuristicDropdown");

    for (final HeuristicType type : HeuristicType.values()) {
      heuristicDropdown.getItems().add(type.toString());
    }

    final OptionType heuristicType =
        isWhite ? OptionType.AI_HEURISTIC_W : OptionType.AI_HEURISTIC_B;
    if (options.containsKey(heuristicType)) {
      heuristicDropdown.setValue(options.get(heuristicType));
    }

    heuristicDropdown
        .valueProperty()
        .addListener((obs, oldVal, newVal) -> options.put(heuristicType, newVal));

    heuristicContainer.setId(colorTag + "HeuristicContainer");
    heuristicContainer.setVisible(
        options.containsKey(modeType)
            && !Objects.equals(options.get(modeType), AlgorithmType.MCTS.toString()));
    heuristicContainer.setManaged(
        options.containsKey(modeType)
            && !Objects.equals(options.get(modeType), AlgorithmType.MCTS.toString()));

    heuristicContainer.getChildren().add(heuristicDropdown);

    heuristicContainer
        .getChildren()
        .add(new Label(TextGetter.getText("aiEndgameHeuristicLabel", colorText)));
    final ComboBox<String> endgameHeuristicDropdown = new ComboBox<>();

    endgameHeuristicDropdown.setId(colorTag + "EndgameHeuristicDropdown");

    for (final HeuristicType type : HeuristicType.values()) {
      endgameHeuristicDropdown.getItems().add(type.toString());
    }

    final OptionType egHeuristicType = isWhite ? OptionType.AI_ENDGAME_W : OptionType.AI_ENDGAME_B;
    if (options.containsKey(egHeuristicType)) {
      endgameHeuristicDropdown.setValue(options.get(egHeuristicType));
    }

    endgameHeuristicDropdown
        .valueProperty()
        .addListener((obs, oldVal, newVal) -> options.put(egHeuristicType, newVal));

    heuristicContainer.getChildren().add(endgameHeuristicDropdown);

    aiContainer.getChildren().add(heuristicContainer);

    depthContainer.setId(colorTag + "DepthContainer");
    depthContainer.setVisible(
        options.containsKey(modeType)
            && !Objects.equals(options.get(modeType), AlgorithmType.MCTS.toString()));
    depthContainer.setManaged(
        options.containsKey(modeType)
            && !Objects.equals(options.get(modeType), AlgorithmType.MCTS.toString()));

    depthContainer.getChildren().add(new Label(TextGetter.getText("aiDepthLabel", colorText)));
    final Slider depthSlider = new Slider(1, 10, 3);
    depthSlider.setId(colorTag + "DepthSlider");
    depthSlider.setShowTickLabels(true);
    depthSlider.setShowTickMarks(true);
    depthSlider.setMajorTickUnit(1);
    depthSlider.setMinorTickCount(0);
    depthSlider.setSnapToTicks(true);
    final OptionType depthType = isWhite ? OptionType.AI_DEPTH_W : OptionType.AI_DEPTH_B;
    depthSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> options.put(depthType, String.valueOf(newVal.intValue())));

    if (options.containsKey(depthType)) {
      depthSlider.setValue(Integer.parseInt(options.get(depthType)));
    } else {
      options.put(depthType, String.valueOf(Math.round(depthSlider.getValue())));
    }

    depthContainer.getChildren().add(depthSlider);

    simulationContainer.setId(colorTag + "SimulationContainer");
    simulationContainer.setVisible(
        options.containsKey(modeType)
            && Objects.equals(options.get(modeType), AlgorithmType.MCTS.toString()));
    simulationContainer.setManaged(
        options.containsKey(modeType)
            && Objects.equals(options.get(modeType), AlgorithmType.MCTS.toString()));

    simulationContainer
        .getChildren()
        .add(new Label(TextGetter.getText("aiSimulationsLabel", colorText)));
    final Slider simulationSlider = new Slider(100, 1000, 300);
    simulationSlider.setId(colorTag + "SimulationSlider");
    simulationSlider.setShowTickLabels(true);
    simulationSlider.setShowTickMarks(true);
    simulationSlider.setMajorTickUnit(10);
    simulationSlider.setMinorTickCount(0);
    simulationSlider.setSnapToTicks(true);
    final OptionType simulationsType =
        isWhite ? OptionType.AI_SIMULATION_W : OptionType.AI_SIMULATION_B;
    simulationSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) ->
                options.put(simulationsType, String.valueOf(newVal.intValue())));

    if (options.containsKey(simulationsType)) {
      simulationSlider.setValue(Integer.parseInt(options.get(simulationsType)));
    } else {
      options.put(simulationsType, String.valueOf(Math.round(simulationSlider.getValue())));
    }

    simulationContainer.getChildren().add(simulationSlider);

    aiContainer.getChildren().add(depthContainer);
    aiContainer.getChildren().add(simulationContainer);

    aiContainer.getChildren().add(new Separator());

    return aiContainer;
  }

  private static VBox makeAiTimeBox(final HashMap<OptionType, String> options) {

    final VBox aiTimeFull = new VBox();

    final CheckBox aiTimeCheckBox = new CheckBox(TextGetter.getText("aiTimeLimitLabel"));
    aiTimeCheckBox.setId("aiTimeCheckBox");
    aiTimeCheckBox.setSelected(options.containsKey(OptionType.AI_TIME));

    aiTimeFull.getChildren().add(aiTimeCheckBox);

    final VBox aiTimeContainer = new VBox(5);
    aiTimeContainer.setId("aiTimeContainer");
    aiTimeContainer.setVisible(aiTimeCheckBox.isSelected());
    aiTimeContainer.setManaged(aiTimeCheckBox.isSelected());

    aiTimeContainer.getChildren().add(new Label(TextGetter.getText("aiTimeLabel")));
    final Slider aiTimeSlider = new Slider(5, 60, 10);
    aiTimeSlider.setId("aiTimeSlider");
    aiTimeSlider.setShowTickLabels(true);
    aiTimeSlider.setShowTickMarks(true);
    aiTimeSlider.setMajorTickUnit(5);
    aiTimeSlider.setMinorTickCount(0);
    aiTimeSlider.setSnapToTicks(true);
    aiTimeSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) ->
                options.put(OptionType.AI_TIME, String.valueOf(newVal.intValue())));

    if (options.containsKey(OptionType.AI_TIME)) {
      aiTimeSlider.setValue(Integer.parseInt(options.get(OptionType.AI_TIME)));
    } else if (aiTimeCheckBox.isSelected()) {
      options.put(OptionType.AI_TIME, String.valueOf(Math.round(aiTimeSlider.getValue())));
    }

    aiTimeCheckBox.setOnAction(
        event -> {
          final boolean selected = aiTimeCheckBox.isSelected();
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

  /**
   * Build a new game popup configuration.
   *
   * @param options Current options.
   */
  public static void show(final HashMap<OptionType, String> options) {
    final Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle(TextGetter.getText("newGame.options"));

    final VBox layout = new VBox(10);
    layout.setId("newGamePopUp");

    final CheckBox blitzCheckBox = new CheckBox("Blitz");
    blitzCheckBox.setId("blitzCheckBox");
    blitzCheckBox.setSelected(options.containsKey(OptionType.BLITZ));

    final VBox timeContainer = new VBox(5);
    timeContainer.setId("timeContainer");
    timeContainer.setVisible(blitzCheckBox.isSelected());
    timeContainer.setManaged(blitzCheckBox.isSelected());

    blitzCheckBox.setOnAction(
        event -> {
          final boolean selected = blitzCheckBox.isSelected();
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

    final Slider timeSlider = new Slider(1, 30, 30);
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

    final ComboBox<String> aiDropdown = new ComboBox<>();
    aiDropdown.setId("aiDropdown");
    aiDropdown.getItems().add("None");
    aiDropdown.getItems().add("W");
    aiDropdown.getItems().add("B");
    aiDropdown.getItems().add("A");

    aiDropdown.setValue(options.getOrDefault(OptionType.AI, "None"));
    layout.getChildren().add(new Label(TextGetter.getText("newGame.aiPlayers")));
    layout.getChildren().add(aiDropdown);

    final VBox aiWhiteContainer = makeAiBox(true, options);
    layout.getChildren().add(aiWhiteContainer);
    aiWhiteContainer.setVisible(
        "A".equals(aiDropdown.getValue()) || "W".equals(aiDropdown.getValue()));
    aiWhiteContainer.setManaged(
        "A".equals(aiDropdown.getValue()) || "W".equals(aiDropdown.getValue()));
    aiWhiteContainer.setId("aiWhiteContainer");

    final VBox aiBlackContainer = makeAiBox(false, options);
    layout.getChildren().add(aiBlackContainer);
    aiBlackContainer.setVisible(
        "A".equals(aiDropdown.getValue()) || "B".equals(aiDropdown.getValue()));
    aiBlackContainer.setManaged(
        "A".equals(aiDropdown.getValue()) || "B".equals(aiDropdown.getValue()));
    aiBlackContainer.setId("aiBlackContainer");

    final VBox aiTimeContainer = makeAiTimeBox(options);
    layout.getChildren().add(aiTimeContainer);
    aiTimeContainer.setVisible(!"None".equals(aiDropdown.getValue()));
    aiTimeContainer.setManaged(!"None".equals(aiDropdown.getValue()));
    aiTimeContainer.setId("fullAITimeContainer");

    aiDropdown
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              final boolean selectedWhite = "A".equals(newVal) || "W".equals(newVal);
              final boolean selectedBlack = "A".equals(newVal) || "B".equals(newVal);
              aiWhiteContainer.setVisible(selectedWhite);
              aiWhiteContainer.setManaged(selectedWhite);
              aiBlackContainer.setVisible(selectedBlack);
              aiBlackContainer.setManaged(selectedBlack);
              if (!"None".equals(newVal)) {
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

    final TextField loadTextField = new TextField();
    loadTextField.setId("loadTextField");
    loadTextField.setPromptText(TextGetter.getText("newGame.loadPrompt"));
    loadTextField.setEditable(false);

    final Button browseButton = new Button(TextGetter.getText("newGame.browse"));
    browseButton.setId("browseButton");
    browseButton.setOnAction(
        event -> {
          final FileChooser fileChooser = new FileChooser();
          fileChooser.setTitle(TextGetter.getText("newGame.savePrompt"));

          fileChooser
              .getExtensionFilters()
              .add(new FileChooser.ExtensionFilter(TextGetter.getText("newGame.fileSave"), "*"));

          final File selectedFile = fileChooser.showOpenDialog(popupStage);

          if (selectedFile != null) {
            loadTextField.setText(selectedFile.getAbsolutePath());
            options.put(OptionType.LOAD, selectedFile.getAbsolutePath());
          } else {
            options.remove(OptionType.LOAD);
          }
        });

    final VBox loadContainer = new VBox(5);
    final Label loadLabel = new Label(TextGetter.getText("newGame.load"));
    loadContainer.getChildren().add(loadLabel);
    final HBox centerContainer = new HBox(5, loadTextField, browseButton);
    centerContainer.setAlignment(Pos.CENTER_LEFT);
    loadContainer.getChildren().add(centerContainer);

    layout.getChildren().add(loadContainer);
    layout.getChildren().add(new Separator());

    final Button startGameButton = new Button(TextGetter.getText("startGame"));
    startGameButton.setId("startGameButton");
    startGameButton.setOnAction(
        event -> {
          GameInitializer.initialize(options);
          popupStage.close();
        });

    final HBox buttonContainer = new HBox(startGameButton);
    buttonContainer.setAlignment(Pos.CENTER);
    layout.getChildren().add(buttonContainer);

    final ScrollPane scrollPane = new ScrollPane();
    scrollPane.setId("scrollPane");
    scrollPane.setContent(layout);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);

    final Scene scene = new Scene(scrollPane, 400, 600);
    GuiView.applyCss(scene);
    layout.setStyle("; -fx-padding: 10;");
    popupStage.setScene(scene);
    popupStage.showAndWait();
  }
}
