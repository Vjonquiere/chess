package pdp.view.gui.popups;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pdp.model.Game;
import pdp.model.ai.Solver;

public class AiMonitor extends Stage {

    private GridPane grid = new GridPane();

    public AiMonitor() {
        this.setTitle("Ai monitor");

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> this.close());

        StackPane secondaryLayout = new StackPane();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(grid);
        scrollPane.setFitToWidth(true);
        secondaryLayout.getChildren().addAll(closeBtn, scrollPane);

        grid.setHgap(10);
        grid.setAlignment(Pos.TOP_CENTER);
        Scene secondaryScene = new Scene(secondaryLayout, 200, 150);
        this.setScene(secondaryScene);
    }

    private void generateHeader(){
        grid.add(new Label("Turn"), 0, 0);
        grid.add(new Label("Explored nodes"), 1, 0);
        grid.add(new Label("Time (ms)"), 2, 0);
        grid.add(new Label("Nodes/s"), 3, 0);
    }

    public void update(boolean isWhite){
        grid.getChildren().clear();
        generateHeader();
        Solver solver = isWhite ? Game.getInstance().getWhiteSolver() : Game.getInstance().getBlackSolver();
        int index = 1;
        for (Long nodes : solver.getAlgorithm().getVisitedNodeList()){
            grid.add(new Label(String.valueOf(index)), 0, index);
            grid.add(new Label(String.valueOf(nodes)), 1, index);
            grid.add(new Label(String.valueOf(solver.getLastMoveTime()/1000000)), 2, index);
            index++;
        }

    }

}
