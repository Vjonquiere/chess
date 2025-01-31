package pdp.controller;

import pdp.model.Game;
import pdp.view.View;

public class GameController {
  Game model;
  View view;
  BagOfCommands bagOfCommands;

  public GameController(Game model, View view, BagOfCommands bagOfCommands) {
    this.model = model;
    this.view = view;
    this.bagOfCommands = bagOfCommands;
  }
}
