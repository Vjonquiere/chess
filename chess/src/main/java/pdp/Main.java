package pdp;

import pdp.controller.BagOfCommands;
import pdp.controller.GameController;

import pdp.view.View;
import pdp.view.CLIView;
import pdp.model.Game;
import pdp.model.History;

public class Main {

  public static void main(String[] args) {
    // TODO handle cli arguments
    Game model = Game.initialize(false, false, null, false, null, new History());
    View view = new CLIView();
    BagOfCommands bagOfCommands = BagOfCommands.getInstance();
    GameController controller = new GameController(model, view, bagOfCommands);
  }
}
