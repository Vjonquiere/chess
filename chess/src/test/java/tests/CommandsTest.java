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
import pdp.events.EventType;
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
  public void testProposeDrawCommandWhiteSucces() {
    ProposeDrawCommand command = new ProposeDrawCommand(true);

    Optional<Exception> result = command.execute(model, controller);

    verify(gameState).doesWhiteWantsToDraw();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testProposeDrawCommandBlackSucces() {
    ProposeDrawCommand command = new ProposeDrawCommand(false);

    Optional<Exception> result = command.execute(model, controller);

    verify(gameState).doesBlackWantsToDraw();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testProposeDrawCommandGameOver() {
    when(model.getGameState().isGameOver()).thenReturn(true);
    ProposeDrawCommand command = new ProposeDrawCommand(true);

    Optional<Exception> result = command.execute(model, controller);

    assertTrue(result.isPresent());
    assertTrue(result.get() instanceof CommandNotAvailableNowException);
  }

  // CancelDrawCommand

  @Test
  public void testCancelDrawCommandWhiteSucces() {
    CancelDrawCommand command = new CancelDrawCommand(true);

    Optional<Exception> result = command.execute(model, controller);

    verify(gameState).whiteCancelsDrawRequest();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testCancelDrawCommandBlackSucces() {
    CancelDrawCommand command = new CancelDrawCommand(false);

    Optional<Exception> result = command.execute(model, controller);

    verify(gameState).blackCancelsDrawRequest();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testCancelDrawCommandGameOver() {
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

  @Test
  public void testCancelMoveCommandAiBlackSuccess() {
    when(model.isBlackAi()).thenReturn(true);
    when(model.isWhiteAi()).thenReturn(false);
    when(gameState.isWhiteTurn()).thenReturn(false);

    doNothing().doThrow(new pdp.exceptions.FailedUndoException()).when(model).previousState();

    pdp.model.ai.Solver blackSolver = mock(pdp.model.ai.Solver.class);
    when(model.getBlackSolver()).thenReturn(blackSolver);
    doNothing().when(blackSolver).playAiMove(model);

    CancelMoveCommand command = new CancelMoveCommand();
    Optional<Exception> result = command.execute(model, controller);

    verify(model, times(2)).previousState();
    verify(blackSolver).playAiMove(model);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testCancelMoveCommandAiWhiteSuccess() {
    when(model.isBlackAi()).thenReturn(false);
    when(model.isWhiteAi()).thenReturn(true);
    when(gameState.isWhiteTurn()).thenReturn(true);

    doNothing().doThrow(new pdp.exceptions.FailedUndoException()).when(model).previousState();

    pdp.model.ai.Solver whiteSolver = mock(pdp.model.ai.Solver.class);
    when(model.getWhiteSolver()).thenReturn(whiteSolver);
    doNothing().when(whiteSolver).playAiMove(model);

    CancelMoveCommand command = new CancelMoveCommand();
    Optional<Exception> result = command.execute(model, controller);

    verify(model, times(2)).previousState();
    verify(whiteSolver).playAiMove(model);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testCancelMoveCommandNoAiUndoSameTurn() {
    when(model.isBlackAi()).thenReturn(false);
    when(model.isWhiteAi()).thenReturn(false);
    when(gameState.getUndoRequestTurnNumber()).thenReturn(5);
    when(gameState.getFullTurn()).thenReturn(5);

    doNothing().when(model).previousState();

    CancelMoveCommand command = new CancelMoveCommand();
    Optional<Exception> result = command.execute(model, controller);

    verify(model).previousState();
    verify(gameState, never()).undoRequest();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testCancelMoveCommandNoAiUndoDifferentTurn() {
    when(model.isBlackAi()).thenReturn(false);
    when(model.isWhiteAi()).thenReturn(false);
    when(gameState.getUndoRequestTurnNumber()).thenReturn(3);
    when(gameState.getFullTurn()).thenReturn(5);

    doNothing().when(gameState).undoRequest();

    CancelMoveCommand command = new CancelMoveCommand();
    Optional<Exception> result = command.execute(model, controller);

    verify(gameState).undoRequest();
    verify(model, never()).previousState();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testCancelMoveCommandException() {
    when(model.isBlackAi()).thenReturn(false);
    when(model.isWhiteAi()).thenReturn(false);
    when(gameState.getUndoRequestTurnNumber()).thenReturn(5);
    when(gameState.getFullTurn()).thenReturn(5);

    doThrow(new RuntimeException("Previous state failed")).when(model).previousState();

    CancelMoveCommand command = new CancelMoveCommand();
    Optional<Exception> result = command.execute(model, controller);

    assertTrue(result.isPresent());
    assertEquals("Previous state failed", result.get().getMessage());
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

  @Test
  public void testRestoreMoveCommandMatchingRedoTurn() {
    when(gameState.getRedoRequestTurnNumber()).thenReturn(5);
    when(gameState.getFullTurn()).thenReturn(5);
    when(model.getGameState()).thenReturn(gameState);
    doNothing().when(model).nextState();

    when(model.isBlackAi()).thenReturn(true);
    when(model.isWhiteAi()).thenReturn(false);
    when(gameState.isWhiteTurn()).thenReturn(false);

    pdp.model.ai.Solver blackSolver = mock(pdp.model.ai.Solver.class);
    when(model.getBlackSolver()).thenReturn(blackSolver);
    doNothing().when(blackSolver).playAiMove(model);

    RestoreMoveCommand command = new RestoreMoveCommand();
    Optional<Exception> result = command.execute(model, controller);

    verify(model).nextState();
    verify(blackSolver).playAiMove(model);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testRestoreMoveCommandMatchingRedoTurnWhiteAI() {
    when(gameState.getRedoRequestTurnNumber()).thenReturn(5);
    when(gameState.getFullTurn()).thenReturn(5);
    when(model.getGameState()).thenReturn(gameState);
    doNothing().when(model).nextState();

    when(model.isBlackAi()).thenReturn(false);
    when(model.isWhiteAi()).thenReturn(true);
    when(gameState.isWhiteTurn()).thenReturn(true);

    pdp.model.ai.Solver whiteSolver = mock(pdp.model.ai.Solver.class);
    when(model.getWhiteSolver()).thenReturn(whiteSolver);
    doNothing().when(whiteSolver).playAiMove(model);

    RestoreMoveCommand command = new RestoreMoveCommand();
    Optional<Exception> result = command.execute(model, controller);

    verify(model).nextState();
    verify(whiteSolver).playAiMove(model);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testRestoreMoveCommandNonMatchingRedoTurnNoAI() {
    when(gameState.getRedoRequestTurnNumber()).thenReturn(3);
    when(gameState.getFullTurn()).thenReturn(5);
    when(model.getGameState()).thenReturn(gameState);

    when(model.isBlackAi()).thenReturn(false);
    when(model.isWhiteAi()).thenReturn(false);

    doNothing().when(gameState).redoRequest();

    RestoreMoveCommand command = new RestoreMoveCommand();
    Optional<Exception> result = command.execute(model, controller);

    verify(gameState).redoRequest();
    verify(model, never()).nextState();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testRestoreMoveCommandNonMatchingRedoTurnWithAI() {
    when(gameState.getRedoRequestTurnNumber()).thenReturn(3);
    when(gameState.getFullTurn()).thenReturn(5);
    when(model.getGameState()).thenReturn(gameState);
    doNothing().when(model).nextState();

    when(model.isBlackAi()).thenReturn(true);
    when(model.isWhiteAi()).thenReturn(false);
    when(gameState.isWhiteTurn()).thenReturn(false);

    pdp.model.ai.Solver blackSolver = mock(pdp.model.ai.Solver.class);
    when(model.getBlackSolver()).thenReturn(blackSolver);
    doNothing().when(blackSolver).playAiMove(model);

    RestoreMoveCommand command = new RestoreMoveCommand();
    Optional<Exception> result = command.execute(model, controller);

    verify(model).nextState();
    verify(blackSolver).playAiMove(model);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testRestoreMoveCommand_MatchingRedoTurn_NoAi() {
    when(gameState.getRedoRequestTurnNumber()).thenReturn(5);
    when(gameState.getFullTurn()).thenReturn(5);
    when(model.getGameState()).thenReturn(gameState);
    doNothing().when(model).nextState();

    when(model.isBlackAi()).thenReturn(false);
    when(model.isWhiteAi()).thenReturn(false);

    RestoreMoveCommand command = new RestoreMoveCommand();
    Optional<Exception> result = command.execute(model, controller);

    verify(model).nextState();
    verify(model, never()).getBlackSolver();
    verify(model, never()).getWhiteSolver();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testRestoreMoveCommandNonMatchingRedoTurnWhiteAI() {
    when(gameState.getRedoRequestTurnNumber()).thenReturn(3);
    when(gameState.getFullTurn()).thenReturn(5);
    when(model.getGameState()).thenReturn(gameState);
    doNothing().when(model).nextState();

    when(model.isBlackAi()).thenReturn(false);
    when(model.isWhiteAi()).thenReturn(true);
    when(gameState.isWhiteTurn()).thenReturn(true);

    pdp.model.ai.Solver whiteSolver = mock(pdp.model.ai.Solver.class);
    when(model.getWhiteSolver()).thenReturn(whiteSolver);
    doNothing().when(whiteSolver).playAiMove(model);

    RestoreMoveCommand command = new RestoreMoveCommand();
    Optional<Exception> result = command.execute(model, controller);

    verify(model).nextState();
    verify(whiteSolver).playAiMove(model);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testRestoreMoveCommandNonMatchingRedoTurnBothAiWhiteTurn() {
    when(gameState.getRedoRequestTurnNumber()).thenReturn(2);
    when(gameState.getFullTurn()).thenReturn(6);
    when(model.getGameState()).thenReturn(gameState);
    doNothing().when(model).nextState();
    when(model.isBlackAi()).thenReturn(true);
    when(model.isWhiteAi()).thenReturn(true);
    when(gameState.isWhiteTurn()).thenReturn(true);

    pdp.model.ai.Solver whiteSolver = mock(pdp.model.ai.Solver.class);
    when(model.getWhiteSolver()).thenReturn(whiteSolver);
    doNothing().when(whiteSolver).playAiMove(model);

    RestoreMoveCommand command = new RestoreMoveCommand();
    Optional<Exception> result = command.execute(model, controller);

    verify(model).nextState();
    verify(whiteSolver).playAiMove(model);
    verify(model, never()).getBlackSolver();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testRestoreMoveCommandNonMatchingRedoTurnBothAiBlackTurn() {
    when(gameState.getRedoRequestTurnNumber()).thenReturn(2);
    when(gameState.getFullTurn()).thenReturn(6);
    when(model.getGameState()).thenReturn(gameState);
    doNothing().when(model).nextState();

    when(model.isBlackAi()).thenReturn(true);
    when(model.isWhiteAi()).thenReturn(true);
    when(gameState.isWhiteTurn()).thenReturn(false);

    pdp.model.ai.Solver blackSolver = mock(pdp.model.ai.Solver.class);
    when(model.getBlackSolver()).thenReturn(blackSolver);
    doNothing().when(blackSolver).playAiMove(model);

    RestoreMoveCommand command = new RestoreMoveCommand();
    Optional<Exception> result = command.execute(model, controller);

    verify(model).nextState();
    verify(blackSolver).playAiMove(model);
    verify(model, never()).getWhiteSolver();
    assertTrue(result.isEmpty());
  }

  // UndoMultipleMoveCommand tests

  @Test
  public void testUndoMultipleMoveCommandAiBlackSuccess() {
    int nbMoveToUndo = 2;
    UndoMultipleMoveCommand command = new UndoMultipleMoveCommand(nbMoveToUndo);

    when(model.isBlackAi()).thenReturn(true);
    when(model.isWhiteAi()).thenReturn(false);
    when(gameState.isWhiteTurn()).thenReturn(false);

    doNothing() // first previousState() in loop
        .doNothing() // second previousState() in loop
        .doThrow(new FailedUndoException()) // extra call in try block
        .when(model)
        .previousState();

    pdp.model.ai.Solver blackSolver = mock(pdp.model.ai.Solver.class);
    when(model.getBlackSolver()).thenReturn(blackSolver);
    doNothing().when(blackSolver).playAiMove(model);

    Optional<Exception> result = command.execute(model, controller);
    verify(model, times(nbMoveToUndo + 1)).previousState();
    verify(blackSolver).playAiMove(model);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testUndoMultipleMoveCommandAiWhiteSuccess() {
    int nbMoveToUndo = 3;
    UndoMultipleMoveCommand command = new UndoMultipleMoveCommand(nbMoveToUndo);

    when(model.isBlackAi()).thenReturn(false);
    when(model.isWhiteAi()).thenReturn(true);
    when(gameState.isWhiteTurn()).thenReturn(true);

    doNothing() // first call
        .doNothing() // second call
        .doNothing() // third call
        .doThrow(new FailedUndoException()) // extra call
        .when(model)
        .previousState();

    pdp.model.ai.Solver whiteSolver = mock(pdp.model.ai.Solver.class);
    when(model.getWhiteSolver()).thenReturn(whiteSolver);
    doNothing().when(whiteSolver).playAiMove(model);

    Optional<Exception> result = command.execute(model, controller);

    verify(model, times(nbMoveToUndo + 1)).previousState();
    verify(whiteSolver).playAiMove(model);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testUndoMultipleMoveCommandNoAiUndoSameTurn() {
    int nbMoveToUndo = 2;
    UndoMultipleMoveCommand command = new UndoMultipleMoveCommand(nbMoveToUndo);

    when(model.isBlackAi()).thenReturn(false);
    when(model.isWhiteAi()).thenReturn(false);

    when(gameState.getUndoRequestTurnNumber()).thenReturn(5);
    when(gameState.getFullTurn()).thenReturn(5);
    when(model.getGameState()).thenReturn(gameState);

    doNothing().when(model).previousState();

    Optional<Exception> result = command.execute(model, controller);

    verify(model, times(nbMoveToUndo)).previousState();
    verify(gameState, never()).undoRequest();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testUndoMultipleMoveCommandNoAiUndoDifferentTurn() {
    int nbMoveToUndo = 3;
    UndoMultipleMoveCommand command = new UndoMultipleMoveCommand(nbMoveToUndo);

    when(model.isBlackAi()).thenReturn(false);
    when(model.isWhiteAi()).thenReturn(false);

    when(gameState.getUndoRequestTurnNumber()).thenReturn(2);
    when(gameState.getFullTurn()).thenReturn(5);
    when(model.getGameState()).thenReturn(gameState);

    doNothing().when(gameState).undoRequest();

    Optional<Exception> result = command.execute(model, controller);

    verify(gameState).undoRequest();
    verify(model, never()).previousState();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testUndoMultipleMoveCommandFailure() {
    int nbMoveToUndo = 2;
    UndoMultipleMoveCommand command = new UndoMultipleMoveCommand(nbMoveToUndo);

    when(model.isBlackAi()).thenReturn(false);
    when(model.isWhiteAi()).thenReturn(false);

    when(gameState.getUndoRequestTurnNumber()).thenReturn(5);
    when(gameState.getFullTurn()).thenReturn(5);
    when(model.getGameState()).thenReturn(gameState);

    doThrow(new RuntimeException("Undo failure")).when(model).previousState();

    Optional<Exception> result = command.execute(model, controller);

    assertTrue(result.isPresent());
    assertEquals("Undo failure", result.get().getMessage());
  }

  // SurrenderCommand

  @Test
  public void testSurrenderCommandWhiteSuccess() {
    SurrenderCommand command = new SurrenderCommand(true);

    Optional<Exception> result = command.execute(model, controller);

    verify(gameState).whiteResigns();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testSurrenderCommandBlackSuccess() {
    SurrenderCommand command = new SurrenderCommand(false);

    Optional<Exception> result = command.execute(model, controller);

    verify(gameState).blackResigns();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testSurrenderCommandGameOver() {
    when(model.getGameState().isGameOver()).thenReturn(true);
    SurrenderCommand command = new SurrenderCommand(true);

    Optional<Exception> result = command.execute(model, controller);

    assertTrue(result.isPresent());
    assertTrue(result.get() instanceof CommandNotAvailableNowException);
  }

  // ChangeLangCommand tests

  @Test
  public void testChangeLangCommandSuccess() {
    ChangeLangCommand command = new ChangeLangCommand();

    Optional<Exception> result = command.execute(model, controller);

    verify(model).notifyObservers(EventType.UPDATE_LANG);

    assertTrue(result.isEmpty());
  }

  @Test
  public void testChangeLangCommandFailure() {

    doThrow(new RuntimeException("Notification failed"))
        .when(model)
        .notifyObservers(EventType.UPDATE_LANG);

    ChangeLangCommand command = new ChangeLangCommand();

    Optional<Exception> result = command.execute(model, controller);

    assertTrue(result.isPresent());
    assertEquals("Notification failed", result.get().getMessage());
  }

  // ChangeThemeCommand tests

  @Test
  public void testChangeThemeCommandSuccess() {
    ChangeThemeCommand command = new ChangeThemeCommand();

    Optional<Exception> result = command.execute(model, controller);

    verify(model).notifyObservers(EventType.UPDATE_THEME);

    assertTrue(result.isEmpty());
  }

  @Test
  public void testChangeThemeCommandFailure() {

    doThrow(new RuntimeException("Notification failed"))
        .when(model)
        .notifyObservers(EventType.UPDATE_THEME);

    ChangeThemeCommand command = new ChangeThemeCommand();

    Optional<Exception> result = command.execute(model, controller);

    assertTrue(result.isPresent());
    assertEquals("Notification failed", result.get().getMessage());
  }

  // StartGameCommand tests

  @Test
  public void testStartGameCommandSuccess() {
    StartGameCommand command = new StartGameCommand();

    Optional<Exception> result = command.execute(model, controller);

    verify(model).startAi();

    assertTrue(result.isEmpty());
  }

  @Test
  public void testStartGameCommandFailure() {
    doThrow(new RuntimeException("AI start failure")).when(model).startAi();

    StartGameCommand command = new StartGameCommand();

    Optional<Exception> result = command.execute(model, controller);

    verify(model).startAi();

    assertTrue(result.isPresent());
    assertEquals("AI start failure", result.get().getMessage());
  }

  // AskHintCommand tests

  @Test
  public void testAskHintCommandGameOver() {
    when(model.getGameState().isGameOver()).thenReturn(true);
    AskHintCommand command = new AskHintCommand();

    Optional<Exception> result = command.execute(model, controller);

    assertTrue(result.isPresent());
    assertTrue(result.get() instanceof CommandNotAvailableNowException);
  }
}
