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
import pdp.model.ai.heuristics.KingOppositionHeuristic;
import pdp.model.ai.heuristics.KingSafetyHeuristic;
import pdp.model.ai.heuristics.PawnChainHeuristic;
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
    // 1 backward pawn
    // factor -0.5 so (2+2+4)*-0.5
    assertEquals(-4, solver.evaluateBoard(board, true));
    board.isWhite =
        false; // to change turn to recalculate (if no change, zobrist takes the previous score)
    assertEquals(4, solver.evaluateBoard(board, false));
  } // Should be equal

  @Test
  public void BadPawnsTestBackWardsPawnsOnlyOnePawn() {
    solver.setHeuristic(HeuristicType.BAD_PAWNS);
    BoardRepresentation board = game.getBoard().getBoardRep();

    board.movePiece(new Position(1, 1), new Position(1, 2));
    board.movePiece(new Position(1, 6), new Position(1, 5));

    // Should be equal since 1 backwards pawn for each player (the a pawn)
    assertEquals(0, solver.evaluateBoard(game.getBoard(), false));
  }

  @Test
  public void BadPawnsTestBackWardsPawnsNoDoubledPawns() {
    solver.setHeuristic(HeuristicType.BAD_PAWNS);
    BoardRepresentation board = game.getBoard().getBoardRep();

    board.movePiece(new Position(3, 1), new Position(3, 3));
    board.movePiece(new Position(4, 1), new Position(4, 2));
    board.movePiece(new Position(5, 1), new Position(5, 3));

    // Should be equal since 1 backwards pawn for each player (the a pawn)
    assertEquals(2, solver.evaluateBoard(game.getBoard(), false));
  }

  @Test
  public void BadPawnsTestBackWardsPawnsTwoForBlack() {
    solver.setHeuristic(HeuristicType.BAD_PAWNS);
    BoardRepresentation board = game.getBoard().getBoardRep();

    board.movePiece(new Position(3, 6), new Position(3, 4));
    board.movePiece(new Position(4, 6), new Position(4, 5));
    board.movePiece(new Position(5, 6), new Position(5, 4));
    board.movePiece(new Position(2, 6), new Position(2, 5));
    board.movePiece(new Position(1, 6), new Position(1, 4));

    // Should be equal since 1 backwards pawn for each player (the a pawn)
    assertEquals(-4, solver.evaluateBoard(game.getBoard(), false));
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
          int scoreWhenGameStartsBlack = -3;
          int scoreWhenGameStartsWhite = 3;
          assertEquals(scoreWhenGameStartsBlack, h.evaluate(game.getBoard(), false));
          assertEquals(scoreWhenGameStartsWhite, h.evaluate(game.getBoard(), true));
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
          int scoreWhenGameStartsIsBalanced = 0;
          assertEquals(scoreWhenGameStartsIsBalanced, h.evaluate(game.getBoard(), false));
        }
      }
    }
  }

  @Test
  public void testKingSafetyHeuristicToChecks() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    if (heuristic instanceof EndGameHeuristic) {
      List<Heuristic> heuristics = ((EndGameHeuristic) heuristic).getHeuristics();
      for (Heuristic h : heuristics) {
        if (h instanceof KingSafetyHeuristic) {
          int scoreWhenGameStartsIsBalanced = 0;
          assertEquals(scoreWhenGameStartsIsBalanced, h.evaluate(game.getBoard(), true));
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
          int expectedScoreBlack = -2;
          assertEquals(expectedScoreBlack, h.evaluate(game.getBoard(), false));
        }
      }
    }
  }

  @Test
  public void testBishopEndgameHeuristicTwoBishops() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    if (heuristic instanceof EndGameHeuristic) {
      List<Heuristic> heuristics = ((EndGameHeuristic) heuristic).getHeuristics();
      for (Heuristic h : heuristics) {
        if (h instanceof BishopEndgameHeuristic) {
          // Expected score in this position
          int expectedScoreWhenGameStartsWhite = 2;
          assertEquals(expectedScoreWhenGameStartsWhite, h.evaluate(game.getBoard(), true));
        }
      }
    }
  }

  @Test
  public void testPawnChainsHeuristicWhenGameStarts() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    if (heuristic instanceof EndGameHeuristic) {
      List<Heuristic> heuristics = ((EndGameHeuristic) heuristic).getHeuristics();
      for (Heuristic h : heuristics) {
        if (h instanceof PawnChainHeuristic) {
          // Expected score
          int expectedScoreWhenGameStarts = 0;
          assertEquals(expectedScoreWhenGameStarts, h.evaluate(game.getBoard(), true));
        }
      }
    }
  }

  @Test
  public void testKingOppositionHeuristic() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    if (heuristic instanceof EndGameHeuristic) {
      List<Heuristic> heuristics = ((EndGameHeuristic) heuristic).getHeuristics();
      for (Heuristic h : heuristics) {
        if (h instanceof KingOppositionHeuristic) {
          // Expected score
          int expectedScore = 0;
          assertEquals(expectedScore, h.evaluate(game.getBoard(), true));
        }
      }
    }
  }

  @Test
  public void testKingOppositionHeuristicStrongOpposition() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard().getBoardRep();

    Position initWhiteKingPos = new Position(4, 0);
    Position initBlackKingPos = new Position(4, 7);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListBlack.add(initBlackKingPos);

    BitboardRepresentationTest.deleteAllPiecesExceptThosePositionsBoard(
        board, posListWhite, posListBlack);

    Position e4 = new Position(4, 3);
    Position e6 = new Position(4, 5);

    board.movePiece(initWhiteKingPos, e4);
    board.movePiece(initBlackKingPos, e6);

    if (heuristic instanceof EndGameHeuristic) {
      List<Heuristic> heuristics = ((EndGameHeuristic) heuristic).getHeuristics();
      for (Heuristic h : heuristics) {
        if (h instanceof KingOppositionHeuristic) {
          // Expected score
          int expectedScore = -10;
          assertEquals(expectedScore, h.evaluate(game.getBoard(), true));
        }
      }
    }
  }

  @Test
  public void testBishopEndgameHeuristicSameColorBishopOpponent() {
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
    posListWhite.add(initBlackKingPos);
    posListBlack.add(initBlackBishopPos);

    BitboardRepresentationTest.deleteAllPiecesExceptThosePositionsBoard(
        board, posListWhite, posListBlack);

    Position e4 = new Position(4, 3);
    Position e2 = new Position(4, 1);

    board.movePiece(initWhiteBishopPos, e4);
    board.movePiece(initBlackBishopPos, e2);

    if (heuristic instanceof EndGameHeuristic) {
      List<Heuristic> heuristics = ((EndGameHeuristic) heuristic).getHeuristics();
      for (Heuristic h : heuristics) {
        if (h instanceof BishopEndgameHeuristic) {
          // Expected score in this position
          int expectedScore = 4;
          assertEquals(expectedScore, h.evaluate(game.getBoard(), true));
        }
      }
    }
  }

  @Test
  public void testBishopEndgameHeuristicSameColorBishopSamePlayer() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard().getBoardRep();

    Position initWhiteKingPos = new Position(4, 0);
    Position initWhiteBishopPos1 = new Position(5, 0);
    Position initWhiteBishopPos2 = new Position(2, 0);
    Position initBlackKingPos = new Position(4, 7);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListWhite.add(initWhiteBishopPos1);
    posListWhite.add(initWhiteBishopPos2);
    posListBlack.add(initBlackKingPos);

    BitboardRepresentationTest.deleteAllPiecesExceptThosePositionsBoard(
        board, posListWhite, posListBlack);

    Position e4 = new Position(4, 3);
    Position e2 = new Position(4, 1);

    board.movePiece(initWhiteBishopPos1, e4);
    board.movePiece(initWhiteBishopPos2, e2);

    if (heuristic instanceof EndGameHeuristic) {
      List<Heuristic> heuristics = ((EndGameHeuristic) heuristic).getHeuristics();
      for (Heuristic h : heuristics) {
        if (h instanceof BishopEndgameHeuristic) {
          // Expected score in this position
          int expectedScore = 18;
          assertEquals(expectedScore, h.evaluate(game.getBoard(), true));
        }
      }
    }
  }

  @Test
  public void testKingOppositionHeuristicDiagonal() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard().getBoardRep();

    Position initWhiteKingPos = new Position(4, 0);
    Position initBlackKingPos = new Position(4, 7);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListBlack.add(initBlackKingPos);

    BitboardRepresentationTest.deleteAllPiecesExceptThosePositionsBoard(
        board, posListWhite, posListBlack);

    Position e4 = new Position(4, 3);
    Position c6 = new Position(2, 5);

    board.movePiece(initWhiteKingPos, e4);
    board.movePiece(initBlackKingPos, c6);

    if (heuristic instanceof EndGameHeuristic) {
      List<Heuristic> heuristics = ((EndGameHeuristic) heuristic).getHeuristics();
      for (Heuristic h : heuristics) {
        if (h instanceof KingOppositionHeuristic) {
          // Expected score in this position
          int expectedScore = -5;
          assertEquals(expectedScore, h.evaluate(game.getBoard(), true));
        }
      }
    }
  }

  @Test
  public void testKingSafetyHeuristicInCenter() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard().getBoardRep();

    Position initWhiteKingPos = new Position(4, 0);
    Position initBlackKingPos = new Position(4, 7);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListBlack.add(initBlackKingPos);

    BitboardRepresentationTest.deleteAllPiecesExceptThosePositionsBoard(
        board, posListWhite, posListBlack);

    Position e4 = new Position(4, 3);
    Position e6 = new Position(4, 5);

    board.movePiece(initWhiteKingPos, e4);
    board.movePiece(initBlackKingPos, e6);

    if (heuristic instanceof EndGameHeuristic) {
      List<Heuristic> heuristics = ((EndGameHeuristic) heuristic).getHeuristics();
      for (Heuristic h : heuristics) {
        if (h instanceof KingSafetyHeuristic) {
          // Expected score in this position
          int expectedScore = 0;
          assertEquals(expectedScore, h.evaluate(game.getBoard(), true));
        }
      }
    }
  }

  @Test
  public void testKingActivityHeuristicKingHasManyMoves() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard().getBoardRep();

    Position initWhiteKingPos = new Position(4, 0);
    Position initBlackKingPos = new Position(4, 7);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListBlack.add(initBlackKingPos);

    BitboardRepresentationTest.deleteAllPiecesExceptThosePositionsBoard(
        board, posListWhite, posListBlack);

    Position b2 = new Position(1, 1);
    Position g7 = new Position(6, 6);

    board.movePiece(initWhiteKingPos, b2);
    board.movePiece(initBlackKingPos, g7);

    if (heuristic instanceof EndGameHeuristic) {
      List<Heuristic> heuristics = ((EndGameHeuristic) heuristic).getHeuristics();
      for (Heuristic h : heuristics) {
        if (h instanceof KingActivityHeuristic) {
          // Expected score in this position
          int expectedScore = 3;
          assertEquals(expectedScore, h.evaluate(game.getBoard(), true));
        }
      }
    }
  }

  @Test
  public void testPawnChainsHeuristic() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard().getBoardRep();

    // white pawns
    Position a2 = new Position(0, 1);
    Position c2 = new Position(2, 1);
    Position e2 = new Position(4, 1);
    Position g2 = new Position(6, 1);

    Position a3 = new Position(0, 2);
    Position c3 = new Position(2, 2);
    Position e3 = new Position(4, 2);
    Position g3 = new Position(6, 2);

    // black pawns
    Position a7 = new Position(0, 7);
    Position c7 = new Position(2, 7);
    Position e7 = new Position(4, 7);
    Position g7 = new Position(6, 7);

    Position a6 = new Position(0, 6);
    Position c6 = new Position(2, 6);
    Position e6 = new Position(4, 6);
    Position g6 = new Position(6, 6);

    board.movePiece(a2, a3);
    board.movePiece(c2, c3);
    board.movePiece(e2, e3);
    board.movePiece(g2, g3);

    board.movePiece(a7, a6);
    board.movePiece(c7, c6);
    board.movePiece(e7, e6);
    board.movePiece(g7, g6);

    if (heuristic instanceof EndGameHeuristic) {
      List<Heuristic> heuristics = ((EndGameHeuristic) heuristic).getHeuristics();
      for (Heuristic h : heuristics) {
        if (h instanceof PawnChainHeuristic) {
          // Expected score
          int expectedScore = 0;
          assertEquals(expectedScore, h.evaluate(game.getBoard(), true));
        }
      }
    }
  }

  @Test
  public void testPawnPromotionHeuristicCloseToPromotion() {
    game = Game.initialize(false, false, null, null);
    solver = new Solver();
    solver.setHeuristic(HeuristicType.ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard().getBoardRep();

    // white pawns
    Position a2 = new Position(0, 1);
    Position c2 = new Position(2, 1);
    Position e2 = new Position(4, 1);
    Position g2 = new Position(6, 1);

    Position a6 = new Position(0, 5);
    Position c6 = new Position(2, 5);
    Position e6 = new Position(4, 5);
    Position g6 = new Position(6, 5);

    // black pawns
    Position b7 = new Position(1, 6);
    Position d7 = new Position(3, 6);
    Position f7 = new Position(5, 6);
    Position h7 = new Position(7, 6);

    Position b3 = new Position(1, 2);
    Position d3 = new Position(3, 2);
    Position f3 = new Position(5, 2);
    Position h3 = new Position(7, 2);

    board.movePiece(a2, a6);
    board.movePiece(c2, c6);
    board.movePiece(e2, e6);
    board.movePiece(g2, g6);

    board.movePiece(b7, b3);
    board.movePiece(d7, d3);
    board.movePiece(f7, f3);
    board.movePiece(h7, h3);

    if (heuristic instanceof EndGameHeuristic) {
      List<Heuristic> heuristics = ((EndGameHeuristic) heuristic).getHeuristics();
      for (Heuristic h : heuristics) {
        if (h instanceof PromotionHeuristic) {
          // Expected score
          int expectedScore = 0;
          assertEquals(expectedScore, h.evaluate(game.getBoard(), true));
        }
      }
    }
  }
}
