package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import pdp.controller.GameController;
import pdp.controller.commands.*;
import pdp.exceptions.CommandNotAvailableNowException;
import pdp.exceptions.FailedRedoException;
import pdp.exceptions.FailedSaveException;
import pdp.exceptions.FailedUndoException;
import pdp.model.Game;
import pdp.model.GameState;
import pdp.model.board.Move;

public class CommandsTest {
  private Game model;
  private GameController controller;
  private GameState gameState;
  private MockedStatic<Game> gameMock;

  @BeforeEach
  public void setUp() {
    model = mock(Game.class);
    controller = mock(GameController.class);
    gameState = mock(GameState.class);
    when(model.getGameState()).thenReturn(gameState);

    gameMock = mockStatic(Game.class);
    gameMock.when(Game::getInstance).thenReturn(model);
  }

  @AfterEach
  public void tearDown() {
    gameMock.close();
  }

  // PlayMoveCommand

  @Test
  public void testPlayMoveCommandSuccess() {

    PlayMoveCommand command = new PlayMoveCommand("e2-e4");

    Optional<Exception> result = command.execute(model, controller);

    verify(model).playMove(any(Move.class));
    assertTrue(result.isEmpty());
  }

  @Test
  public void testPlayMoveCommandGameOver() {
    when(model.getGameState().isGameOver()).thenReturn(true);
    PlayMoveCommand command = new PlayMoveCommand("e2-e4");

    Optional<Exception> result = command.execute(model, controller);

    assertTrue(result.isPresent());
    assertTrue(result.get() instanceof CommandNotAvailableNowException);
  }

  @Test
  public void testPlayMoveCommandInvalidFormat() {
    doThrow(new IllegalArgumentException("Invalid move")).when(model).playMove(any(Move.class));
    PlayMoveCommand command = new PlayMoveCommand("e2-e5");

    Optional<Exception> result = command.execute(model, controller);

    assertTrue(result.isPresent());
    assertEquals("Invalid move", result.get().getMessage());
  }

  // ProposeDrawCommand

  @Test
  void testProposeDrawCommandWhiteSucces() {
    ProposeDrawCommand command = new ProposeDrawCommand(true);

    Optional<Exception> result = command.execute(model, controller);

    verify(gameState).doesWhiteWantsToDraw();
    assertTrue(result.isEmpty());
  }

  @Test
  void testProposeDrawCommandBlackSucces() {
    ProposeDrawCommand command = new ProposeDrawCommand(false);

    Optional<Exception> result = command.execute(model, controller);

    verify(gameState).doesBlackWantsToDraw();
    assertTrue(result.isEmpty());
  }

  @Test
  void testProposeDrawCommandGameOver() {
    when(model.getGameState().isGameOver()).thenReturn(true);
    ProposeDrawCommand command = new ProposeDrawCommand(true);

    Optional<Exception> result = command.execute(model, controller);

    assertTrue(result.isPresent());
    assertTrue(result.get() instanceof CommandNotAvailableNowException);
  }

  // CancelDrawCommand

  @Test
  void testCancelDrawCommandWhiteSucces() {
    CancelDrawCommand command = new CancelDrawCommand(true);

    Optional<Exception> result = command.execute(model, controller);

    verify(gameState).whiteCancelsDrawRequest();
    assertTrue(result.isEmpty());
  }

  @Test
  void testCancelDrawCommandBlackSucces() {
    CancelDrawCommand command = new CancelDrawCommand(false);

    Optional<Exception> result = command.execute(model, controller);

    verify(gameState).blackCancelsDrawRequest();
    assertTrue(result.isEmpty());
  }

  @Test
  void testCancelDrawCommandGameOver() {
    when(model.getGameState().isGameOver()).thenReturn(true);
    CancelDrawCommand command = new CancelDrawCommand(true);

    Optional<Exception> result = command.execute(model, controller);

    assertTrue(result.isPresent());
    assertTrue(result.get() instanceof CommandNotAvailableNowException);
  }

  // SaveGameCommand

  @Test
  public void testSaveGameCommandSuccess() {
    SaveGameCommand command = new SaveGameCommand("test.txt");

    Optional<Exception> result = command.execute(model, controller);

    verify(model).saveGame("test.txt");
  }

  @Test
  public void testSaveGameCommandSuccessNoPath() {
    SaveGameCommand command = new SaveGameCommand("");

    Optional<Exception> result = command.execute(model, controller);

    verify(model).saveGame("save.txt");
  }

  @Test
  public void testSaveGameCommandFailure() {
    doThrow(new FailedSaveException("")).when(model).saveGame(anyString());
    SaveGameCommand command = new SaveGameCommand("error.txt");
    Optional<Exception> result = command.execute(model, controller);
    assertTrue(result.isPresent());
    assertTrue(result.get() instanceof FailedSaveException);
  }

  // CancelMoveCommand

  @Test
  public void testCancelMoveCommandSuccess() {
    CancelMoveCommand command = new CancelMoveCommand();

    Optional<Exception> result = command.execute(model, controller);

    verify(model).previousState();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testCancelMoveCommandFailure() {
    doThrow(new FailedUndoException()).when(model).previousState();
    CancelMoveCommand command = new CancelMoveCommand();

    Optional<Exception> result = command.execute(model, controller);

    verify(model).previousState();
    assertTrue(result.isPresent());
    assertTrue(result.get() instanceof FailedUndoException);
  }

  // RestoreMoveCommand

  @Test
  public void testRestoreMoveCommandSuccess() {
    RestoreMoveCommand command = new RestoreMoveCommand();

    Optional<Exception> result = command.execute(model, controller);

    verify(model).nextState();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testRestoreMoveCommandFailure() {
    doThrow(new FailedRedoException()).when(model).nextState();
    RestoreMoveCommand command = new RestoreMoveCommand();

    Optional<Exception> result = command.execute(model, controller);

    verify(model).nextState();
    assertTrue(result.isPresent());
    assertTrue(result.get() instanceof FailedRedoException);
  }

  // SurrenderCommand

  @Test
  void testSurrenderCommandWhiteSuccess() {
    SurrenderCommand command = new SurrenderCommand(true);

    Optional<Exception> result = command.execute(model, controller);

    verify(gameState).whiteResigns();
    assertTrue(result.isEmpty());
  }

  @Test
  void testSurrenderCommandBlackSuccess() {
    SurrenderCommand command = new SurrenderCommand(false);

    Optional<Exception> result = command.execute(model, controller);

    verify(gameState).blackResigns();
    assertTrue(result.isEmpty());
  }

  @Test
  void testSurrenderCommandGameOver() {
    when(model.getGameState().isGameOver()).thenReturn(true);
    SurrenderCommand command = new SurrenderCommand(true);

    Optional<Exception> result = command.execute(model, controller);

    assertTrue(result.isPresent());
    assertTrue(result.get() instanceof CommandNotAvailableNowException);
  }
}
