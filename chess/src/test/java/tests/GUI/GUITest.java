package tests.GUI;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import pdp.events.EventType;
import pdp.model.Game;
import pdp.model.board.Move;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;
import pdp.view.GuiView;
import pdp.view.gui.board.Square;

public class GUITest extends ApplicationTest {

  private GuiView guiView;

  @Override
  public void start(Stage stage) {
    Game.initialize(false, false, null, null, null, new HashMap<>());
    guiView = new GuiView();

    guiView.init(stage);
    guiView.show();
  }

  @Test
  public void testAppLaunches() {
    Scene scene = guiView.getScene();
    assertNotNull(scene, "Scene should be initialized");
  }

  @Test
  public void testGameEventProcessing() {
    Platform.runLater(() -> guiView.onGameEvent(EventType.GAME_STARTED));
    WaitForAsyncUtils.waitForFxEvents();
    assertNotNull(guiView.getBoard());
    assertNotNull(guiView.getControlPanel());
    assertNotNull(guiView.getMenu());
  }

  @Test
  public void testMovePlayed() {
    Platform.runLater(() -> guiView.onGameEvent(EventType.GAME_STARTED));
    WaitForAsyncUtils.waitForFxEvents();
    Square square1 = lookup("#square41").query();
    assertEquals(new ColoredPiece(Piece.PAWN, Color.WHITE), square1.getCurrentPiece());
    Game.getInstance().playMove(new Move(new Position(4, 1), new Position(4, 2)));
    Platform.runLater(() -> guiView.onGameEvent(EventType.MOVE_PLAYED));
    WaitForAsyncUtils.waitForFxEvents();
    square1 = lookup("#square41").query();
    Square square2 = lookup("#square42").query();
    assertEquals(new ColoredPiece(Piece.EMPTY, Color.EMPTY), square1.getCurrentPiece());
    assertEquals(new ColoredPiece(Piece.PAWN, Color.WHITE), square2.getCurrentPiece());
    assertNotNull(guiView.getBoard());
    assertNotNull(guiView.getControlPanel());
    assertNotNull(guiView.getMenu());
  }
}
