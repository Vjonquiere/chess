package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.controller.GameController;
import pdp.controller.commands.*;
import pdp.exceptions.CommandNotAvailableNowException;
import pdp.model.Game;
import pdp.model.GameState;
import pdp.model.Move;

class CommandTest {
  private Game model;
  private GameController controller;
  private GameState gameState;

  @BeforeEach
  public void setUp() {
    model = mock(Game.class);
    controller = mock(GameController.class);
    gameState = mock(GameState.class);
    when(model.getGameState()).thenReturn(gameState);
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

    verify(gameState).whiteWantsToDraw();
    assertTrue(result.isEmpty());
  }

  @Test
  void testProposeDrawCommandBlackSucces() {
    ProposeDrawCommand command = new ProposeDrawCommand(false);

    Optional<Exception> result = command.execute(model, controller);

    verify(gameState).blackWantsToDraw();
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
}
