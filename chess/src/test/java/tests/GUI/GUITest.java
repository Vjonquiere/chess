package tests.GUI;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Locale;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import pdp.events.EventType;
import pdp.exceptions.FailedRedoException;
import pdp.model.Game;
import pdp.model.ai.Solver;
import pdp.model.board.Move;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;
import pdp.utils.TextGetter;
import pdp.view.GuiView;
import pdp.view.gui.board.Square;
import pdp.view.gui.themes.ColorTheme;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GUITest extends ApplicationTest {

  private GuiView guiView;

  @Override
  public void start(Stage stage) {

    Game.initialize(false, false, null, null, null, new HashMap<>());
    Platform.runLater(
        () -> {
          guiView = new GuiView();
          guiView.init(stage);
          guiView.setInitialized(true);
        });
  }

  @Test
  @Tag("gui")
  public void testAppLaunches() {
    Scene scene = guiView.getScene();
    assertNotNull(scene, "Scene should be initialized");
  }

  @Test
  @Tag("gui")
  public void testGameEventProcessing() {
    guiView.onGameEvent(EventType.GAME_STARTED);

    WaitForAsyncUtils.waitForFxEvents();
    assertNotNull(guiView.getBoard());
    assertNotNull(guiView.getControlPanel());
    assertNotNull(guiView.getMenu());
  }

  @Test
  @Tag("gui")
  public void testMovePlayed() {
    guiView.onGameEvent(EventType.GAME_STARTED);

    WaitForAsyncUtils.waitForFxEvents();
    Square square1 = from(guiView.getBoard()).lookup("#square41").query();
    assertEquals(new ColoredPiece(Piece.PAWN, Color.WHITE), square1.getCurrentPiece());
    Game.getInstance().playMove(new Move(new Position(4, 1), new Position(4, 2)));
    guiView.onGameEvent(EventType.MOVE_PLAYED);
    WaitForAsyncUtils.waitForFxEvents();
    square1 = from(guiView.getBoard()).lookup("#square41").query();
    Square square2 = from(guiView.getBoard()).lookup("#square42").query();
    assertEquals(new ColoredPiece(Piece.EMPTY, Color.EMPTY), square1.getCurrentPiece());
    assertEquals(new ColoredPiece(Piece.PAWN, Color.WHITE), square2.getCurrentPiece());
    assertNotNull(guiView.getBoard());
    assertNotNull(guiView.getControlPanel());
    assertNotNull(guiView.getMenu());
  }

  @Test
  @Tag("gui")
  public void testChangeStyle() {
    guiView.onGameEvent(EventType.GAME_STARTED);
    WaitForAsyncUtils.waitForFxEvents();
    assertEquals(ColorTheme.GREY, GuiView.getTheme());
    assertTrue(guiView.getRoot().getStyle().contains("-fx-background-color: #e0e1dd;"));
    GuiView.setTheme(ColorTheme.PURPLE);
    assertEquals(ColorTheme.PURPLE, GuiView.getTheme());

    guiView.onGameEvent(EventType.UPDATE_THEME);
    WaitForAsyncUtils.waitForFxEvents();

    assertTrue(guiView.getRoot().getStyle().contains("-fx-background-color: #F8E6F0;"));
  }

  @Test
  @Tag("gui")
  public void testChangeLang() {
    guiView.onGameEvent(EventType.GAME_STARTED);

    WaitForAsyncUtils.waitForFxEvents();
    Label label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(label.getText(), TextGetter.getText("guiStartMessagePlayAMove"));
    TextGetter.setLocale("fr");
    assertEquals(Locale.FRENCH, TextGetter.getLocale());

    guiView.onGameEvent(EventType.UPDATE_LANG);
    WaitForAsyncUtils.waitForFxEvents();

    label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(label.getText(), TextGetter.getText("guiStartMessagePlayAMove"));
  }

  @Test
  @Tag("gui")
  public void testSimpleMessages() {
    guiView.onGameEvent(EventType.GAME_STARTED);

    WaitForAsyncUtils.waitForFxEvents();
    Label label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(label.getText(), TextGetter.getText("guiStartMessagePlayAMove"));

    // message displayed when receiving WHITE_WIN
    guiView.onGameEvent(EventType.WIN_WHITE);
    WaitForAsyncUtils.waitForFxEvents();
    label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(label.getText(), TextGetter.getText("whiteWin"));

    // message displayed when receiving BLACK_WIN
    guiView.onGameEvent(EventType.WIN_BLACK);
    WaitForAsyncUtils.waitForFxEvents();
    label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(label.getText(), TextGetter.getText("blackWin"));

    // message displayed when receiving DRAW
    guiView.onGameEvent(EventType.DRAW);
    WaitForAsyncUtils.waitForFxEvents();
    label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(label.getText(), TextGetter.getText("onDraw"));

    // message displayed when receiving WHITE_DRAW_PROPOSAL
    guiView.onGameEvent(EventType.WHITE_DRAW_PROPOSAL);
    WaitForAsyncUtils.waitForFxEvents();
    label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(label.getText(), TextGetter.getText("drawProposal", TextGetter.getText("white")));

    // message displayed when receiving BLACK_DRAW_PROPOSAL
    guiView.onGameEvent(EventType.BLACK_DRAW_PROPOSAL);
    WaitForAsyncUtils.waitForFxEvents();
    label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(label.getText(), TextGetter.getText("drawProposal", TextGetter.getText("black")));

    // message displayed when receiving WHITE_UNDRAW
    guiView.onGameEvent(EventType.WHITE_UNDRAW);
    WaitForAsyncUtils.waitForFxEvents();
    label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(
        label.getText(), TextGetter.getText("cancelDrawProposal", TextGetter.getText("white")));

    // message displayed when receiving BLACK_UNDRAW
    guiView.onGameEvent(EventType.BLACK_UNDRAW);
    WaitForAsyncUtils.waitForFxEvents();
    label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(
        label.getText(), TextGetter.getText("cancelDrawProposal", TextGetter.getText("black")));

    // message displayed when receiving GAME_SAVED
    guiView.onGameEvent(EventType.GAME_SAVED);
    WaitForAsyncUtils.waitForFxEvents();
    label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(label.getText(), TextGetter.getText("gameSaved"));

    // message displayed when receiving AI_PLAYING
    guiView.onGameEvent(EventType.AI_PLAYING);
    WaitForAsyncUtils.waitForFxEvents();
    label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(label.getText(), TextGetter.getText("ai_playing"));

    // message displayed when receiving GAME_RESTART
    guiView.onGameEvent(EventType.GAME_RESTART);
    WaitForAsyncUtils.waitForFxEvents();
    label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(label.getText(), TextGetter.getText("gameRestart"));

    // message displayed when receiving MOVE_REDO
    guiView.onGameEvent(EventType.MOVE_REDO);
    WaitForAsyncUtils.waitForFxEvents();
    label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(label.getText(), TextGetter.getText("moveRedone"));

    // message displayed when receiving MOVE_UNDO
    guiView.onGameEvent(EventType.MOVE_UNDO);
    WaitForAsyncUtils.waitForFxEvents();
    label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(label.getText(), TextGetter.getText("moveUndone"));
  }

  @Test
  @Tag("gui")
  public void testMessageError() {
    guiView.onGameEvent(EventType.GAME_STARTED);

    WaitForAsyncUtils.waitForFxEvents();
    Label label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(label.getText(), TextGetter.getText("guiStartMessagePlayAMove"));

    // message displayed when receiving a Failed redo exception
    guiView.onErrorEvent(new FailedRedoException());
    WaitForAsyncUtils.waitForFxEvents();

    label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelError");
    assertEquals(label.getText(), TextGetter.getText("failedRedo"));
  }

  @Test
  @Tag("gui")
  public void testLaunchGameAI() {
    Game.initialize(true, false, new Solver(), null, null, new HashMap<>());
    guiView.onGameEvent(EventType.GAME_STARTED);

    WaitForAsyncUtils.waitForFxEvents();
    Label label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelInfo");
    assertEquals(label.getText(), TextGetter.getText("guiStartMessageGameStart"));

    // message displayed when receiving a Failed redo exception
    guiView.onErrorEvent(new FailedRedoException());
    WaitForAsyncUtils.waitForFxEvents();

    label = (Label) guiView.getMenu().getMessageDisplay().lookup("#labelError");
    assertEquals(label.getText(), TextGetter.getText("failedRedo"));
  }
}
