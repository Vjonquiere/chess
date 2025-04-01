package tests.GUI;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testfx.framework.junit5.ApplicationTest;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.CancelMoveCommand;
import pdp.model.Game;
import pdp.model.board.Move;
import pdp.utils.Position;
import pdp.utils.TextGetter;
import pdp.view.gui.popups.YesNoPopUp;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class YesNoPopUpTest extends ApplicationTest {

  private Game game;
  private Move move;
  private Move move2;

  @BeforeEach
  void setUp() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());

    move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);

    BagOfCommands.getInstance().addCommand(new CancelMoveCommand());
  }

  @Override
  public void start(Stage stage) {
    // On ne démarre pas l'UI complète, chaque test affichera sa propre popup

  }

  @Test
  @Tag("gui")
  void testUndoAccept() {

    runFx(
        () -> {
          Platform.runLater(
              () -> {
                new YesNoPopUp(
                    "undoInstructionsGui",
                    new CancelMoveCommand(),
                    () -> game.getGameState().undoRequestReset());
              });
        });

    sleep(500);
    clickOn(TextGetter.getText("accept"));

    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getSource(),
        move.getSource());
    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getDest(), move.getDest());
  }

  @Test
  @Tag("gui")
  void testUndoRefuse() {

    runFx(
        () -> {
          Platform.runLater(
              () -> {
                new YesNoPopUp(
                    "undoInstructionsGui",
                    new CancelMoveCommand(),
                    () -> game.getGameState().undoRequestReset());
              });
        });

    sleep(500);
    clickOn(TextGetter.getText("refuse"));

    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getSource(),
        move2.getSource());
    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getDest(), move2.getDest());

    assertEquals(-1, game.getGameState().getUndoRequestTurnNumber());
  }

  private void runFx(Runnable action) {
    interact(action);
  }

  @Test
  @Tag("gui")
  void testWithoutCommandRefuse() {

    runFx(
        () -> {
          Platform.runLater(
              () -> {
                new YesNoPopUp("undoInstructionsGui", null, null);
              });
        });

    sleep(500);
    clickOn(TextGetter.getText("refuse"));
  }

  @Test
  @Tag("gui")
  void testWithoutCommandAccept() {

    runFx(
        () -> {
          Platform.runLater(
              () -> {
                new YesNoPopUp("undoInstructionsGui", null, null);
              });
        });

    sleep(500);
    clickOn(TextGetter.getText("accept"));
  }
}
