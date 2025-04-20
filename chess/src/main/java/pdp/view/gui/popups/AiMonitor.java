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
import pdp.model.GameAbstract;
import pdp.model.ai.Solver;
import pdp.model.ai.algorithms.MonteCarloTreeSearch;
import pdp.utils.TextGetter;
import pdp.view.GuiView;

/** A Stage to monitor the performances of AI. */
public class AiMonitor extends Stage {

  /** Content of the list to display. */
  private final ObservableList<MonitorEntry> data = FXCollections.observableArrayList();

  /** Table to display Ai data. */
  private final TableView<MonitorEntry> table;

  /** The solver to lookup. */
  private final Solver solver;

  /** Label to display average values. */
  private final Label average;

  /**
   * Constructor to create a monitor from the Ai color.
   *
   * @param isWhite The Ai color to monitor.
   */
  public AiMonitor(final boolean isWhite) {
    super();
    final String title =
        isWhite
            ? TextGetter.getText("monitoringWhiteWindowTitle")
            : TextGetter.getText("monitoringBlackWindowTitle");
    this.setTitle(title);

    solver =
        isWhite
            ? GameAbstract.getInstance().getWhiteSolver()
            : GameAbstract.getInstance().getBlackSolver();

    table = new TableView<>();

    average = new Label();

    if (solver.getAlgorithm() instanceof MonteCarloTreeSearch) {
      average.setText("Monitoring couldn't be activated with MCTS");
    }

    final TableColumn<MonitorEntry, Integer> turnColumn =
        new TableColumn<>(TextGetter.getText("turn"));
    turnColumn.setCellValueFactory(new PropertyValueFactory<>("turn"));

    final TableColumn<MonitorEntry, String> nodesColumn =
        new TableColumn<>(TextGetter.getText("visitedNodes"));
    nodesColumn.setCellValueFactory(new PropertyValueFactory<>("visitedNodes"));
    nodesColumn.setMinWidth(125);

    final TableColumn<MonitorEntry, String> timeColumn =
        new TableColumn<>(TextGetter.getText("searchTime"));
    timeColumn.setCellValueFactory(new PropertyValueFactory<>("searchTime"));
    timeColumn.setMinWidth(150);

    final TableColumn<MonitorEntry, String> rateColumn =
        new TableColumn<>(TextGetter.getText("nodesSecond"));
    rateColumn.setCellValueFactory(new PropertyValueFactory<>("nodesPerSecond"));
    rateColumn.setMinWidth(150);

    table.setItems(data);

    table.getColumns().addAll(turnColumn, nodesColumn, timeColumn, rateColumn);

    final Label config = new Label(solver.toString());
    final VBox layout = new VBox(config, average, table);
    layout.setAlignment(Pos.TOP_CENTER);

    final Scene secondaryScene = new Scene(layout, 600, 200);
    GuiView.applyCss(secondaryScene);
    this.setScene(secondaryScene);
  }

  /**
   * Format numbers in order to be more readable.
   *
   * @param number The number to transform.
   * @return A String corresponding to the number.
   */
  public static String formatNumber(final long number) {
    if (number < 1_000) {
      return String.valueOf(number);
    } else if (number < 10_000) {
      return number / 100 / 10 + "k";
    } else if (number < 1_000_000) {
      return number / 1_000 + "k";
    } else if (number < 10_000_000) {
      return new DecimalFormat("#.##").format(number / 1_000_000.0) + "M";
    } else {
      return number / 1_000_000 + "M";
    }
  }

  /** Update the displayed data. */
  public void update() {
    data.clear();
    final List<Long> times = solver.getMoveTimes();
    final List<Long> nodes = solver.getAlgorithm().getVisitedNodeList();
    if (nodes.isEmpty() || nodes.size() != times.size()) {
      return;
    }
    long totalNodes = 0;
    long totalTime = 0;
    for (int i = 0; i < nodes.size(); i++) {
      data.add(new MonitorEntry(i, nodes.get(i), times.get(i)));
      totalNodes += nodes.get(i);
      totalTime += times.get(i);
    }
    average.setText(
        "Avg visited nodes:"
            + formatNumber(totalNodes / nodes.size())
            + " time:"
            + (totalTime / times.size() / 1_000_000)
            + " Nd/s:"
            + formatNumber((totalNodes * 1_000_000_000) / totalTime));
    table.scrollTo(data.size() - 1);
    table.getSelectionModel().select(data.size() - 1);
  }

  /** A class to serialize the data for display./ */
  public static class MonitorEntry {
    /** Number of visited nodes. */
    private final long visitedNodes;

    /** The time that was necessary to complete the search. */
    private final long searchTime;

    /** The turn number. */
    private final long turn;

    /** The number of nodes that can be explored each second. */
    private final long nodesPerSecond;

    /**
     * Build a new entry with several parameters.
     *
     * @param turn The turn number.
     * @param visitedNodes The number of visited nodes.
     * @param searchTime The search time.
     */
    public MonitorEntry(final int turn, final long visitedNodes, final long searchTime) {
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
      return searchTime / 1_000_000;
    }

    public String getNodesPerSecond() {
      return formatNumber(nodesPerSecond);
    }
  }
}
