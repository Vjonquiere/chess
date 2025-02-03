package pdp.controller;

import pdp.model.Game;

public interface Command {
  public void execute(Game model, GameController controller);
}
