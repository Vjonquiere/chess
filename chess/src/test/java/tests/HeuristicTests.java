package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.model.Game;
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;
import pdp.model.ai.heuristics.BishopEndgameHeuristic;
import pdp.model.ai.heuristics.EndGameHeuristic;
import pdp.model.ai.heuristics.Heuristic;
import pdp.model.ai.heuristics.KingActivityHeuristic;
import pdp.model.ai.heuristics.KingSafetyHeuristic;
import pdp.model.ai.heuristics.PromotionHeuristic;
import pdp.model.board.Board;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.utils.Position;

public class HeuristicTests {
  Game game;
  Solver solver;

  @BeforeEach
  public void setup() {
    solver = new Solver();
    game = Game.initialize(false, false, null, null);
  }

  @Test
  public void BasicMaterialTest() {
    solver.setHeuristic(HeuristicType.MATERIAL);
    assertEquals(0, solver.evaluateBoard(game.getBoard(), true));
    assertEquals(0, solver.evaluateBoard(game.getBoard(), false));
  }

  @Test
  public void BasicMobilityTest() {
    solver.setHeuristic(HeuristicType.MOBILITY);
    // same number of moves so score = 0
    assertEquals(0, solver.evaluateBoard(game.getBoard(), true));
    assertEquals(0, solver.evaluateBoard(game.getBoard(), false));
  }

  @Test
  public void BadPawnsTest() {
    solver.setHeuristic(HeuristicType.BAD_PAWNS);
    Board board = game.getBoard();
    assertEquals(0, solver.evaluateBoard(board, true));
    board.makeMove(new Move(new Position(0, 1), new Position(0, 4)));
    board.makeMove(new Move(new Position(2, 1), new Position(2, 3)));
    board.makeMove(new Move(new Position(3, 1), new Position(2, 4)));
    board.makeMove(new Move(new Position(4, 1), new Position(4, 3)));
    board.makeMove(new Move(new Position(5, 1), new Position(4, 4)));

    // 2 isolated pawns ( e3 and 4)
    // 2 doubled pawns --> ({c3-c4} and {e3-e4})
    // factor -0.5 so (2+2)*-0.5
    assertEquals(-2, solver.evaluateBoard(board, true));
    board.isWhite =
        false; // to change turn to recalculate (if no change, zobrist takes the previous score)
    assertEquals(2, solver.evaluateBoard(board, false));
  }

  @Test
  public void OpponentCheckTest() {
    solver.setHeuristic(HeuristicType.OPPONENT_CHECK);
    // board at init
    assertEquals(0, solver.evaluateBoard(game.getBoard(), true));
    game.playMove(new Move(new Position(4, 1), new Position(4, 3)));
    game.playMove(new Move(new Position(4, 6), new Position(4, 4)));
    game.playMove(new Move(new Position(3, 0), new Position(7, 4)));
    game.playMove(new Move(new Position(1, 7), new Position(2, 5)));
    game.playMove(new Move(new Position(5, 0), new Position(2, 3)));
    game.playMove(new Move(new Position(6, 7), new Position(5, 5)));
    game.playMove(new Move(new Position(7, 4), new Position(5, 6)));
    // Scholar's Mate (black checkmate)
    assertEquals(-150, solver.evaluateBoard(game.getBoard(), false));
    game.getBoard().isWhite = true;
    assertEquals(150, solver.evaluateBoard(game.getBoard(), true));
  }

  @Test
  public void testEndGameHeuristic() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    int score = 10;
    assertEquals(score, solver.evaluateBoard(game.getBoard(), true));
    assertEquals(score, solver.evaluateBoard(game.getBoard(), false));
  }

  @Test
  public void testPromotionHeuristic() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    if (heuristic instanceof EndGameHeuristic) {
      List<Heuristic> heuristics = ((EndGameHeuristic) heuristic).getHeuristics();
      for (Heuristic h : heuristics) {
        if (h instanceof PromotionHeuristic) {
          int scoreWhenGameStarts = 0;
          assertEquals(scoreWhenGameStarts, h.evaluate(game.getBoard(), true));
          assertEquals(scoreWhenGameStarts, h.evaluate(game.getBoard(), false));
        }
      }
    }
  }

  @Test
  public void testKingActivityHeuristic() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    if (heuristic instanceof EndGameHeuristic) {
      List<Heuristic> heuristics = ((EndGameHeuristic) heuristic).getHeuristics();
      for (Heuristic h : heuristics) {
        if (h instanceof KingActivityHeuristic) {
          int scoreWhenGameStarts = 0;
          assertEquals(scoreWhenGameStarts, h.evaluate(game.getBoard(), false));
        }
      }
    }
  }

  @Test
  public void testKingSafetyHeuristic() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    if (heuristic instanceof EndGameHeuristic) {
      List<Heuristic> heuristics = ((EndGameHeuristic) heuristic).getHeuristics();
      for (Heuristic h : heuristics) {
        if (h instanceof KingSafetyHeuristic) {
          int scoreWhenGameStarts = 45;
          assertEquals(scoreWhenGameStarts, h.evaluate(game.getBoard(), false));
        }
      }
    }
  }

  @Test
  public void testBishopEndgameHeuristic() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard().getBoardRep();

    Position initWhiteKingPos = new Position(4, 0);
    Position initWhiteBishopPos = new Position(5, 0);
    Position initBlackKingPos = new Position(4, 7);
    Position initBlackBishopPos = new Position(5, 7);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListWhite.add(initWhiteBishopPos);
    posListBlack.add(initBlackKingPos);
    posListBlack.add(initBlackBishopPos);

    BitboardRepresentationTest.deleteAllPiecesExceptThosePositionsBoard(
        board, posListWhite, posListBlack);

    Position e4 = new Position(4, 3);
    Position e5 = new Position(4, 4);

    board.movePiece(initWhiteBishopPos, e4);
    board.movePiece(initBlackBishopPos, e5);

    if (heuristic instanceof EndGameHeuristic) {
      List<Heuristic> heuristics = ((EndGameHeuristic) heuristic).getHeuristics();
      for (Heuristic h : heuristics) {
        if (h instanceof BishopEndgameHeuristic) {
          // Expected score in this position
          int expectedScore = 14;
          assertEquals(expectedScore, h.evaluate(game.getBoard(), false));
          assertEquals(expectedScore, h.evaluate(game.getBoard(), false));
        }
      }
    }
  }
}
