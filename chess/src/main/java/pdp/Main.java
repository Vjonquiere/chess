package pdp;

import pdp.controller.BagOfCommands;
import pdp.controller.GameController;
import pdp.model.Game;
import pdp.model.History;
import pdp.view.CLIView;
import pdp.view.View;

public class Main {

  public static void main(String[] args) {
    // TODO handle cli arguments
    Game model = Game.initialize(false, false, null, false, null, new History());
    View view = new CLIView();
    BagOfCommands bagOfCommands = BagOfCommands.getInstance();
    GameController controller = new GameController(model, view, bagOfCommands);
    Thread viewThread = view.start();

    try {
      viewThread.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
