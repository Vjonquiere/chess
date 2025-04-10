package tests.GUI;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Locale;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import pdp.model.Game;
import pdp.model.board.Move;
import pdp.utils.Position;
import pdp.utils.TextGetter;
import pdp.view.gui.ControlPanel;
import pdp.view.gui.controls.ButtonsPanel;
import pdp.view.gui.controls.HistoryPanel;

class ControlPanelTest extends ApplicationTest {

  private ControlPanel controlPanel;
  private ButtonsPanel buttonsPanel;
  private HistoryPanel historyPanel;
  private BorderPane root;
  private Game game;
  private Move move;
  private Move move2;

  @BeforeAll
  public static void setUpLocale() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @Override
  public void start(Stage stage) throws Exception {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());

    move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);

    // Initialization
    root = new BorderPane();
    controlPanel = new ControlPanel(root);
    buttonsPanel = new ButtonsPanel();

    Scene scene = new Scene(controlPanel);
    stage.setScene(scene);
    stage.show();
  }

  @Test
  @Tag("gui")
  void testHistoryPanelDisplaysCorrectMoves() {
    ListView<String> listView = lookup(".list-view").query();

    assertNotNull(listView, "The history list should be initialized.");
    assertEquals(2, listView.getItems().size(), "The history list should contain 2 moves.");

    // Expected moves (assuming the history stores moves in string format correctly)
    String expectedMove1 =
        game.getHistory().getCurrentMove().get().getPrevious().get().getState().toString();
    String expectedMove2 = game.getHistory().getCurrentMove().get().getState().toString();

    assertEquals(expectedMove1, listView.getItems().get(0), "First move should match.");
    assertEquals(expectedMove2, listView.getItems().get(1), "Second move should match.");
  }

  @Test
  @Tag("gui")
  void testUpdateHistoryPanel() {
    ListView<String> listView = lookup(".list-view").query();
    assertNotNull(listView, "The history list should be initialized.");

    assertEquals(
        2, listView.getItems().size(), "The history list should initially contain 2 moves.");

    Move move3 = new Move(new Position(3, 1), new Position(3, 3));
    game.playMove(move3);

    interact(() -> controlPanel.getHistoryPanel().updateHistoryPanel());

    assertEquals(
        game.getHistory().getCurrentMove().get().getState().toString(),
        listView.getItems().get(2),
        "The third move should match.");
  }

  @Test
  @Tag("gui")
  void testButtonsInButtonsPanel() {
    assertNotNull(buttonsPanel, "The ButtonsPanel should be initialized.");

    // Simulate a click on "Undo" button
    clickOn(TextGetter.getText("undo"));
    WaitForAsyncUtils.waitForFxEvents();
    sleep(500);
    clickOn(TextGetter.getText("accept"));

    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getSource(),
        move.getSource());
    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getDest(), move.getDest());

    // Simulate a click on "Redo" button
    clickOn(TextGetter.getText("redo"));
    WaitForAsyncUtils.waitForFxEvents();
    sleep(500);
    clickOn(TextGetter.getText("accept"));
    sleep(500);

    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getSource(),
        move2.getSource());
    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getDest(), move2.getDest());

    // Simulate a click on "Draw" button
    clickOn(TextGetter.getText("draw"));
    WaitForAsyncUtils.waitForFxEvents();
    sleep(500);
    clickOn(TextGetter.getText("accept"));
    sleep(500);
    assertTrue(game.getGameState().hasWhiteRequestedDraw());

    // Simulate a click on "Undraw" button
    clickOn(TextGetter.getText("undraw"));
    WaitForAsyncUtils.waitForFxEvents();
    sleep(500);
    clickOn(TextGetter.getText("accept"));
    sleep(500);
    assertTrue(!game.getGameState().hasWhiteRequestedDraw());

    // Simulate a click on "Resign" button
    clickOn(TextGetter.getText("resign"));
    WaitForAsyncUtils.waitForFxEvents();
    sleep(500);
    clickOn(TextGetter.getText("accept"));
    sleep(500);
    assertTrue(game.getGameState().hasWhiteResigned());

    // Simulate a click on "Restart" button
    clickOn(TextGetter.getText("restart"));
    WaitForAsyncUtils.waitForFxEvents();
    sleep(500);
    clickOn(TextGetter.getText("accept"));
    sleep(500);

    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getSource(),
        new Position(-1, -1));
    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getDest(),
        new Position(-1, -1));

    // Not allowed clicks
    clickOn(TextGetter.getText("undo"));
    WaitForAsyncUtils.waitForFxEvents();
    clickOn(TextGetter.getText("redo"));
    WaitForAsyncUtils.waitForFxEvents();
    clickOn(TextGetter.getText("resign"));
    WaitForAsyncUtils.waitForFxEvents();
    clickOn(TextGetter.getText("draw"));
    WaitForAsyncUtils.waitForFxEvents();
    clickOn(TextGetter.getText("undraw"));
    WaitForAsyncUtils.waitForFxEvents();
  }
}
