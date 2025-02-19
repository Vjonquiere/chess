package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import pdp.exceptions.FailedRedoException;
import pdp.exceptions.FailedUndoException;
import pdp.model.*;
import pdp.model.board.Move;
import pdp.model.piece.Piece;
import pdp.utils.Position;

public class HistoryTest {

  /*   @Test
  void testHistoryToString() {
    // Arrange: Create a new history and add moves
    History history = new History();
    history.addMove(new HistoryState(Move.fromString("e3-e5"), 1, true)); // White move
    history.addMove(new HistoryState(Move.fromString("h4-h5"), 1, false)); // Black move
    history.addMove(new HistoryState(Move.fromString("g1-f3"), 2, true)); // White move
    history.addMove(new HistoryState(Move.fromString("e7-e6"), 2, false)); // Black move

    // Act: Convert history to string
    String historyOutput = history.toString();

    // Assert: Check expected output
    String expectedOutput = "1. W e3-e5 B h4-h5\n2. W g1-f3 B e7-e6";
    assertEquals(expectedOutput, historyOutput);
  } */

  @Test
  public void HistoryTestInGame() {
    Game game = Game.initialize(false, false, null, null);
    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);
    Move move3 = new Move(new Position(3, 1), new Position(3, 2));
    game.playMove(move3);
    Move move4 = new Move(new Position(3, 6), new Position(3, 5));
    game.playMove(move4);

    String historyOutput = game.getHistory().toString();

    // Assert: Check expected output
    String expectedOutput = "1. W e2-e4 B e7-e5\n2. W d2-d3 B d7-d6";
    assertEquals(expectedOutput, historyOutput);
  }

  @Test
  public void undoHistoryInGame() {
    Game game = Game.initialize(false, false, null, null);

    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);

    game.previousState();
    assertEquals(game.getBoard().getPlayer(), false);
    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getSource(),
        move.getSource());
    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getDest(), move.getDest());
  }

  @Test
  public void redoHistoryInGame() {
    Game game = Game.initialize(false, false, null, null);

    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);
    game.previousState();
    assertEquals(game.getBoard().getPlayer(), false);
    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getSource(),
        move.getSource());
    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getDest(), move.getDest());
    game.nextState();
    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getSource(),
        move2.getSource());
    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getMove().getDest(), move2.getDest());
  }

  @Test
  public void redoWithEmptyHistoryHistoryInGame() {
    Game game = Game.initialize(false, false, null, null);
    assertThrows(
        FailedRedoException.class,
        () -> {
          game.nextState();
        });
  }

  @Test
  public void undoWithEmptyHistoryHistoryInGame() {
    Game game = Game.initialize(false, false, null, null);
    assertThrows(
        FailedUndoException.class,
        () -> {
          game.previousState();
        });
  }

  @Test
  public void getCurrentMoveTest() {
    Game game = Game.initialize(false, false, null, null);

    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);

    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);

    Move move3 = new Move(new Position(3, 1), new Position(3, 2));
    game.playMove(move3);

    Move move4 = new Move(new Position(3, 6), new Position(3, 5));
    game.playMove(move4);

    assertEquals(game.getHistory().getCurrentMove().get().getState().getMove(), move4);
    assertEquals(
        game.getHistory().getCurrentMove().get().getState().getGameState().getBoard().getPlayer(),
        game.getGameState().getBoard().getPlayer());
    assertEquals(
        game.getHistory().getCurrentMove().get().getPrevious().get().getState().getMove(), move3);
    assertEquals(
        game.getHistory()
            .getCurrentMove()
            .get()
            .getPrevious()
            .get()
            .getState()
            .getGameState()
            .getBoard()
            .getPlayer(),
        !game.getGameState().getBoard().getPlayer());
  }

  @Test
  public void compareGameStatePositionAndHistory() {
    Game game = Game.initialize(false, false, null, null);

    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);

    assertEquals(
        Piece.PAWN,
        game.getHistory()
            .getCurrentMove()
            .get()
            .getState()
            .getGameState()
            .getBoard()
            .getBoardRep()
            .getPieceAt(4, 3)
            .piece);
  }

  @Test
  public void compareGameStatePositionAndHistoryAfterUndo() {
    Game game = Game.initialize(false, false, null, null);

    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);

    assertEquals(
        Piece.PAWN,
        game.getHistory()
            .getCurrentMove()
            .get()
            .getState()
            .getGameState()
            .getBoard()
            .getBoardRep()
            .getPieceAt(4, 4)
            .piece);
    assertEquals(
        Piece.EMPTY,
        game.getHistory()
            .getCurrentMove()
            .get()
            .getState()
            .getGameState()
            .getBoard()
            .getBoardRep()
            .getPieceAt(4, 6)
            .piece);

    game.previousState();

    assertEquals(
        Piece.EMPTY,
        game.getHistory()
            .getCurrentMove()
            .get()
            .getState()
            .getGameState()
            .getBoard()
            .getBoardRep()
            .getPieceAt(4, 4)
            .piece);
    assertEquals(
        Piece.PAWN,
        game.getHistory()
            .getCurrentMove()
            .get()
            .getState()
            .getGameState()
            .getBoard()
            .getBoardRep()
            .getPieceAt(4, 6)
            .piece);
  }

  @Test
  public void getNextNodeEmptyTest() {
    Game game = Game.initialize(false, false, null, null);

    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);

    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);

    Move move3 = new Move(new Position(3, 1), new Position(3, 2));
    game.playMove(move3);

    Move move4 = new Move(new Position(3, 6), new Position(3, 5));
    game.playMove(move4);

    assertEquals(game.getHistory().getCurrentMove().get().getNext(), Optional.empty());
  }

  @Test
  public void toAlgebraicinHistoryStateBlackTest() {
    Game game = Game.initialize(false, false, null, null);
    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);
    Move move3 = new Move(new Position(3, 1), new Position(3, 2));
    game.playMove(move3);
    Move move4 = new Move(new Position(3, 6), new Position(3, 5));
    game.playMove(move4);

    String historyOutput = game.getHistory().getCurrentMove().get().getState().toAlgebraicString();

    // Assert: Check expected output
    String expectedOutput = "B d7-d6";
    assertEquals(expectedOutput, historyOutput);
  }

  public void toAlgebraicinHistoryStateWhiteTest() {
    Game game = Game.initialize(false, false, null, null);
    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);
    Move move2 = new Move(new Position(4, 6), new Position(4, 4));
    game.playMove(move2);
    Move move3 = new Move(new Position(3, 1), new Position(3, 2));
    game.playMove(move3);

    String historyOutput = game.getHistory().toString();

    // Assert: Check expected output
    String expectedOutput = "W d2-d3";
    assertEquals(expectedOutput, historyOutput);
  }
}
