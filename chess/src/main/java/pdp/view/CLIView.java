package pdp.view;

import pdp.model.Game;

public class CLIView implements View {
  @Override
  public void onGameEvent() {
    System.out.println(Game.getInstance().getGameRepresentation());
  }
}
