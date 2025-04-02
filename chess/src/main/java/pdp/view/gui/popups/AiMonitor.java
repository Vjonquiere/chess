package pdp.view.gui.popups;

import java.text.DecimalFormat;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pdp.model.Game;
import pdp.model.ai.Solver;
import pdp.view.GuiView;

/** A Stage to monitor the performances of AI. */
public class AiMonitor extends Stage {

  ObservableList<MonitorEntry> data = FXCollections.observableArrayList();
  TableView<MonitorEntry> table;
  Solver solver;
  Label average;

  /**
   * Constructor to create a monitor from the Ai color.
   *
   * @param isWhite The Ai color to monitor.
   */
  public AiMonitor(boolean isWhite) {
    String title = isWhite ? "White" : "Black";
    this.setTitle(title + " AI Monitor");

    solver = isWhite ? Game.getInstance().getWhiteSolver() : Game.getInstance().getBlackSolver();

    average = new Label();

    table = new TableView<>();

    TableColumn<MonitorEntry, Integer> turnColumn = new TableColumn<>("Turn");
    turnColumn.setCellValueFactory(new PropertyValueFactory<>("turn"));

    TableColumn<MonitorEntry, String> nodesColumn = new TableColumn<>("Visited Nodes");
    nodesColumn.setCellValueFactory(new PropertyValueFactory<>("visitedNodes"));
    nodesColumn.setMinWidth(125);

    TableColumn<MonitorEntry, String> timeColumn = new TableColumn<>("Search Time (ms)");
    timeColumn.setCellValueFactory(new PropertyValueFactory<>("searchTime"));
    timeColumn.setMinWidth(150);

    TableColumn<MonitorEntry, String> rateColumn = new TableColumn<>("Nodes/Second");
    rateColumn.setCellValueFactory(new PropertyValueFactory<>("nodesPerSecond"));
    rateColumn.setMinWidth(150);

    table.setItems(data);

    table.getColumns().addAll(turnColumn, nodesColumn, timeColumn, rateColumn);

    Label config = new Label(solver.toString());
    VBox layout = new VBox(config, average, table);
    layout.setAlignment(Pos.TOP_CENTER);

    Scene secondaryScene = new Scene(layout, 600, 200);
    GuiView.applyCss(secondaryScene);
    this.setScene(secondaryScene);
  }

  /**
   * Format numbers in order to be more readable.
   *
   * @param number The number to transform.
   * @return A String corresponding to the number.
   */
  public static String formatNumber(long number) {
    if (number < 1_000) {
      return String.valueOf(number);
    } else if (number < 10_000) {
      return (number / 100) / 10 + "k";
    } else if (number < 1_000_000) {
      return (number / 1_000) + "k";
    } else if (number < 10_000_000) {
      return new DecimalFormat("#.##").format(number / 1_000_000.0) + "M";
    } else {
      return (number / 1_000_000) + "M";
    }
  }

  /** Update the displayed data. */
  public void update() {
    data.clear();
    List<Long> times = solver.getMoveTimes();
    List<Long> nodes = solver.getAlgorithm().getVisitedNodeList();
    long totalNodes = 0;
    long totalTime = 0;
    if (nodes.isEmpty() || nodes.size() != times.size()) {
      return;
    }
    for (int i = 0; i < nodes.size(); i++) {
      data.add(new MonitorEntry(i, nodes.get(i), times.get(i)));
      totalNodes += nodes.get(i);
      totalTime += times.get(i);
    }
    average.setText(
        "Avg visited nodes:"
            + formatNumber(totalNodes / nodes.size())
            + " time:"
            + (totalTime / times.size() / 1000000)
            + " Nd/s:"
            + formatNumber((totalNodes * 1_000_000_000) / totalTime));
    table.scrollTo(data.size() - 1);
    table.getSelectionModel().select(data.size() - 1);
  }

  /** A class to serialize the data for display./ */
  public static class MonitorEntry {
    private final long visitedNodes;
    private final long searchTime;
    private final long turn;
    private final long nodesPerSecond;

    /**
     * Build a new entry with several parameters.
     *
     * @param turn The turn number.
     * @param visitedNodes The number of visited nodes.
     * @param searchTime The search time.
     */
    public MonitorEntry(int turn, long visitedNodes, long searchTime) {
      this.turn = turn;
      this.visitedNodes = visitedNodes;
      this.searchTime = searchTime;
      this.nodesPerSecond = (visitedNodes * 1_000_000_000) / searchTime;
    }

    public long getTurn() {
      return turn;
    }

    public String getVisitedNodes() {
      return formatNumber(visitedNodes);
    }

    public long getSearchTime() {
      return searchTime / 1000000;
    }

    public String getNodesPerSecond() {
      return formatNumber(nodesPerSecond);
    }
  }
}
