package pdp.view.GUI;

import java.io.File;
import java.util.HashMap;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    blitzCheckBox.setSelected(options.containsKey(OptionType.BLITZ));

    VBox timeContainer = new VBox(5);
    timeContainer.setVisible(false);
    timeContainer.setManaged(false);

    blitzCheckBox.setOnAction(
        event -> {
          boolean selected = blitzCheckBox.isSelected();
          timeContainer.setVisible(selected);
          timeContainer.setManaged(selected);
        });

    layout.getChildren().add(blitzCheckBox);

    timeContainer.getChildren().add(new Label("Time (in minutes):"));

    Slider timeSlider = new Slider(1, 60, 30);
    timeSlider.setShowTickLabels(true);
    timeSlider.setShowTickMarks(true);
    timeSlider.setMajorTickUnit(1);
    timeSlider.setMinorTickCount(0);
    timeSlider.setSnapToTicks(true);
    timeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {});

    if (options.containsKey(OptionType.TIME)) {
      timeSlider.setValue(Integer.parseInt(options.get(OptionType.TIME)));
    }

    timeContainer.getChildren().add(timeSlider);
    layout.getChildren().add(timeContainer);

    CheckBox aiCheckBox = new CheckBox("AI");
    aiCheckBox.setSelected(options.containsKey(OptionType.AI));
    layout.getChildren().add(aiCheckBox);
    VBox aiContainer = new VBox(5);
    aiContainer.setVisible(aiCheckBox.isSelected());
    aiContainer.setManaged(aiCheckBox.isSelected());

    aiContainer.getChildren().add(new Label("AI Mode"));
    ComboBox<String> aiModeDropdown = new ComboBox<>();

    for (AlgorithmType type : AlgorithmType.values()) {
      aiModeDropdown.getItems().add(type.toString());
    }

    if (options.containsKey(OptionType.AI_MODE)) {
      aiModeDropdown.setValue(options.get(OptionType.AI_MODE));
    }

    aiContainer.getChildren().add(aiModeDropdown);

    aiContainer.getChildren().add(new Label("AI Heuristic"));
    ComboBox<String> heuristicDropdown = new ComboBox<>();

    for (HeuristicType type : HeuristicType.values()) {
      heuristicDropdown.getItems().add(type.toString());
    }

    if (options.containsKey(OptionType.AI_HEURISTIC)) {
      aiModeDropdown.setValue(options.get(OptionType.AI_HEURISTIC));
    }

    aiContainer.getChildren().add(heuristicDropdown);

    aiContainer.getChildren().add(new Label("AI Depth"));
    Slider depthSlider = new Slider(1, 10, 3);
    depthSlider.setShowTickLabels(true);
    depthSlider.setShowTickMarks(true);
    depthSlider.setMajorTickUnit(1);
    depthSlider.setMinorTickCount(0);
    depthSlider.setSnapToTicks(true);
    depthSlider.valueProperty().addListener((obs, oldVal, newVal) -> {});

    if (options.containsKey(OptionType.AI_DEPTH)) {
      depthSlider.setValue(Integer.parseInt(options.get(OptionType.AI_DEPTH)));
    }

    aiContainer.getChildren().add(depthSlider);

    aiContainer.getChildren().add(new Label("AI Time (in seconds)"));
    Slider aiTimeSlider = new Slider(5, 60, 10);
    aiTimeSlider.setShowTickLabels(true);
    aiTimeSlider.setShowTickMarks(true);
    aiTimeSlider.setMajorTickUnit(5);
    aiTimeSlider.setMinorTickCount(0);
    aiTimeSlider.setSnapToTicks(true);
    aiTimeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {});

    if (options.containsKey(OptionType.AI_TIME)) {
      aiTimeSlider.setValue(Integer.parseInt(options.get(OptionType.AI_TIME)));
    }

    aiContainer.getChildren().add(aiTimeSlider);

    layout.getChildren().add(aiContainer);

    aiCheckBox.setOnAction(
        event -> {
          boolean selected = aiCheckBox.isSelected();
          aiContainer.setVisible(selected);
          aiContainer.setManaged(selected);
        });

    Label loadLabel = new Label("Load game from:");
    TextField loadTextField = new TextField();
    loadTextField.setPromptText("Select a file...");
    loadTextField.setEditable(false);

    Button browseButton = new Button("Browse");
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
          }
        });

    VBox loadContainer = new VBox(5);
    loadContainer.getChildren().add(loadLabel);
    loadContainer.getChildren().add(new HBox(5, loadTextField, browseButton));

    layout.getChildren().add(loadContainer);

    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setContent(layout);
    scrollPane.setFitToWidth(true);

    Scene scene = new Scene(scrollPane, 400, 600);
    popupStage.setScene(scene);
    popupStage.showAndWait();
  }
}
