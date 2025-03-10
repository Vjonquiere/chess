package pdp.view.GUI;

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

public class NewGamePopup {
  public static void show(HashMap<OptionType, String> options) {
    Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle("New Game Options");

    VBox layout = new VBox(10);
    layout.setStyle("-fx-padding: 10; -fx-alignment: center-left;");

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

    timeContainer.getChildren().add(new Label("Time (in minutes):"));

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
    layout.getChildren().add(new Label("AI Player(s)"));
    layout.getChildren().add(aiDropdown);
    VBox aiContainer = new VBox(5);
    aiContainer.setId("aiContainer");
    aiContainer.setVisible(!aiDropdown.getValue().equals("None"));
    aiContainer.setManaged(!aiDropdown.getValue().equals("None"));

    aiContainer.getChildren().add(new Label("AI Mode"));
    ComboBox<String> aiModeDropdown = new ComboBox<>();

    aiModeDropdown.setId("aiModeDropdown");

    for (AlgorithmType type : AlgorithmType.values()) {
      aiModeDropdown.getItems().add(type.toString());
    }

    if (options.containsKey(OptionType.AI_MODE)) {
      aiModeDropdown.setValue(options.get(OptionType.AI_MODE));
    }

    aiModeDropdown
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              options.put(OptionType.AI_MODE, newVal);
            });

    aiContainer.getChildren().add(aiModeDropdown);

    aiContainer.getChildren().add(new Label("AI Heuristic"));
    ComboBox<String> heuristicDropdown = new ComboBox<>();

    heuristicDropdown.setId("heuristicDropdown");

    for (HeuristicType type : HeuristicType.values()) {
      heuristicDropdown.getItems().add(type.toString());
    }

    if (options.containsKey(OptionType.AI_HEURISTIC)) {
      heuristicDropdown.setValue(options.get(OptionType.AI_HEURISTIC));
    }

    heuristicDropdown
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              options.put(OptionType.AI_HEURISTIC, newVal);
            });

    aiContainer.getChildren().add(heuristicDropdown);

    aiContainer.getChildren().add(new Label("AI Depth"));
    Slider depthSlider = new Slider(1, 10, 3);
    depthSlider.setId("depthSlider");
    depthSlider.setShowTickLabels(true);
    depthSlider.setShowTickMarks(true);
    depthSlider.setMajorTickUnit(1);
    depthSlider.setMinorTickCount(0);
    depthSlider.setSnapToTicks(true);
    depthSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              options.put(OptionType.AI_DEPTH, String.valueOf(newVal.intValue()));
            });

    if (options.containsKey(OptionType.AI_DEPTH)) {
      depthSlider.setValue(Integer.parseInt(options.get(OptionType.AI_DEPTH)));
    }

    aiContainer.getChildren().add(depthSlider);

    CheckBox aiTimeCheckBox = new CheckBox("AI Time limit");
    aiTimeCheckBox.setId("aiTimeCheckBox");
    aiTimeCheckBox.setSelected(options.containsKey(OptionType.AI_TIME));

    aiContainer.getChildren().add(aiTimeCheckBox);

    VBox aiTimeContainer = new VBox(5);
    aiTimeContainer.setId("aiTimeContainer");
    aiTimeContainer.setVisible(aiTimeCheckBox.isSelected());
    aiTimeContainer.setManaged(aiTimeCheckBox.isSelected());

    aiTimeContainer.getChildren().add(new Label("AI Time (in seconds)"));
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

    aiContainer.getChildren().add(aiTimeContainer);

    aiTimeContainer.getChildren().add(aiTimeSlider);

    layout.getChildren().add(aiContainer);

    aiDropdown
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              boolean selected = !newVal.equals("None");
              aiContainer.setVisible(selected);
              aiContainer.setManaged(selected);
              if (selected) {
                options.put(OptionType.AI, newVal);
              } else {
                options.remove(OptionType.AI);
              }
            });

    layout.getChildren().add(new Separator());

    Label loadLabel = new Label("Load game from:");
    TextField loadTextField = new TextField();
    loadTextField.setId("loadTextField");
    loadTextField.setPromptText("Select a file...");
    loadTextField.setEditable(false);

    Button browseButton = new Button("Browse");
    browseButton.setId("browseButton");
    browseButton.setOnAction(
        event -> {
          FileChooser fileChooser = new FileChooser();
          fileChooser.setTitle("Select a save file");

          fileChooser
              .getExtensionFilters()
              .add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

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

    Button startGameButton = new Button("Start Game");
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

    Scene scene = new Scene(scrollPane, 400, 600);
    popupStage.setScene(scene);
    popupStage.showAndWait();
  }
}
