package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.model.Game;
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;
import pdp.model.ai.heuristics.Heuristic;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.utils.Position;

public class HeuristicsTest {
  Game game;
  Solver solver;
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @BeforeAll
  public static void setUpLocale() {
      Locale.setDefault(Locale.ENGLISH);
  }

  @AfterEach
  void tearDownConsole() {
    System.setOut(originalOut);
    System.setErr(originalErr);
    outputStream.reset();
  }

  @BeforeEach
  public void setup() {
    System.setOut(new PrintStream(outputStream));
    System.setErr(new PrintStream(outputStream));
    solver = new Solver();
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
  }

  @Test
  public void BasicMaterialTest() {
    solver.setHeuristic(HeuristicType.MATERIAL);
    assertEquals(0, solver.evaluateBoard(game.getGameState(), true));
    assertEquals(0, solver.evaluateBoard(game.getGameState(), false));
  }

  @Test
  public void BasicMobilityTest() {
    solver.setHeuristic(HeuristicType.MOBILITY);
    // same number of moves so score = 0
    assertEquals(0, solver.evaluateBoard(game.getGameState(), true));
    assertEquals(0, solver.evaluateBoard(game.getGameState(), false));
  }

  @Test
  public void BadPawnsTest() {
    solver.setHeuristic(HeuristicType.BAD_PAWNS);
    BoardRepresentation board = game.getBoard();
    assertEquals(0, solver.evaluateBoard(game.getGameState(), true));
    board.makeMove(new Move(new Position(0, 1), new Position(0, 4)));
    board.makeMove(new Move(new Position(2, 1), new Position(2, 3)));
    board.makeMove(new Move(new Position(3, 1), new Position(2, 4)));
    board.makeMove(new Move(new Position(4, 1), new Position(4, 3)));
    board.makeMove(new Move(new Position(5, 1), new Position(4, 4)));

    float expected = -8 * (100f / 28);
    assertEquals(expected, solver.evaluateBoard(game.getGameState(), true));
    board.setPlayer(
        false); // to change turn to recalculate (if no change, zobrist takes the previous score)
    assertEquals(-expected, solver.evaluateBoard(game.getGameState(), false));
  }

  @Test
  public void BadPawnsTestBackWardsPawnsOnlyOnePawn() {
    solver.setHeuristic(HeuristicType.BAD_PAWNS);
    BoardRepresentation board = game.getBoard();

    board.movePiece(new Position(1, 1), new Position(1, 2));
    board.movePiece(new Position(1, 6), new Position(1, 5));

    assertEquals(0f, Math.abs(solver.evaluateBoard(game.getGameState(), false)));
  }

  @Test
  public void BadPawnsTestBackWardsPawnsNoDoubledPawns() {
    solver.setHeuristic(HeuristicType.BAD_PAWNS);
    BoardRepresentation board = game.getBoard();

    board.movePiece(new Position(3, 1), new Position(3, 3));
    board.movePiece(new Position(4, 1), new Position(4, 2));
    board.movePiece(new Position(5, 1), new Position(5, 3));

    float expected = 4 * (100f / 28);
    assertEquals(expected, solver.evaluateBoard(game.getGameState(), false));
  }

  @Test
  public void BadPawnsTestBackWardsPawnsTwoForBlack() {
    solver.setHeuristic(HeuristicType.BAD_PAWNS);
    BoardRepresentation board = game.getBoard();

    board.movePiece(new Position(3, 6), new Position(3, 4));
    board.movePiece(new Position(4, 6), new Position(4, 5));
    board.movePiece(new Position(5, 6), new Position(5, 4));
    board.movePiece(new Position(2, 6), new Position(2, 5));
    board.movePiece(new Position(1, 6), new Position(1, 4));

    float expected = -8 * (100f / 28);
    assertEquals(expected, solver.evaluateBoard(game.getGameState(), false));
  }

  @Test
  public void testSpaceControlHeuristicFourPawnsEachSide() {
    solver.setHeuristic(HeuristicType.SPACE_CONTROL);
    BoardRepresentation board = game.getBoard();

    Position initWhiteKingPos = new Position(4, 0);
    Position initBlackKingPos = new Position(4, 7);

    Position a2 = new Position(0, 1);
    Position b2 = new Position(1, 1);
    Position g2 = new Position(6, 1);
    Position h2 = new Position(7, 1);

    Position a7 = new Position(0, 6);
    Position b7 = new Position(1, 6);
    Position g7 = new Position(6, 6);
    Position h7 = new Position(7, 6);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListWhite.add(a2);
    posListWhite.add(b2);
    posListWhite.add(g2);
    posListWhite.add(h2);

    posListBlack.add(initBlackKingPos);
    posListBlack.add(a7);
    posListBlack.add(b7);
    posListBlack.add(g7);
    posListBlack.add(h7);

    BitboardRepresentationTest.deleteAllPiecesExceptThosePositionsBoard(
        board, posListWhite, posListBlack);

    assertEquals(0f, Math.abs(solver.evaluateBoard(game.getGameState(), false)));
    assertEquals(0f, Math.abs(solver.evaluateBoard(game.getGameState(), true)));
  }

  @Test
  public void testSpaceControlHeuristicBishopsAimCenter() {
    solver.setHeuristic(HeuristicType.SPACE_CONTROL);
    BoardRepresentation board = game.getBoard();

    Position initWhiteKingPos = new Position(4, 0);
    Position initBlackKingPos = new Position(4, 7);

    Position bishopsF1 = new Position(5, 0);
    Position bishopsC1 = new Position(2, 0);
    Position bishopsF8 = new Position(5, 7);
    Position bishopsC8 = new Position(2, 7);
    Position rookA1 = new Position(0, 0);
    Position rookA8 = new Position(0, 7);

    Position b2 = new Position(1, 1);
    Position g2 = new Position(6, 1);
    Position c4 = new Position(2, 3);

    Position b7 = new Position(1, 6);
    Position g7 = new Position(6, 6);
    Position c5 = new Position(2, 4);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListWhite.add(bishopsC1);
    posListWhite.add(bishopsF1);
    posListWhite.add(rookA1);

    posListBlack.add(initBlackKingPos);
    posListBlack.add(bishopsC8);
    posListBlack.add(bishopsF8);
    posListBlack.add(rookA8);

    BitboardRepresentationTest.deleteAllPiecesExceptThosePositionsBoard(
        board, posListWhite, posListBlack);

    board.movePiece(bishopsC1, b2);
    board.movePiece(bishopsF1, g2);
    board.movePiece(rookA1, c4);

    board.movePiece(bishopsC8, b7);
    board.movePiece(bishopsF8, g7);
    board.movePiece(rookA8, c5);

    assertEquals(0, solver.evaluateBoard(game.getGameState(), true));
    assertEquals(0, solver.evaluateBoard(game.getGameState(), false));
  }

  @Test
  public void OpponentCheckTest() {
    solver.setHeuristic(HeuristicType.GAME_STATUS);
    // board at init
    assertEquals(0, solver.evaluateBoard(game.getGameState(), true));
    game.playMove(new Move(new Position(4, 1), new Position(4, 3)));
    game.playMove(new Move(new Position(4, 6), new Position(4, 4)));
    game.playMove(new Move(new Position(3, 0), new Position(7, 4)));
    game.playMove(new Move(new Position(1, 7), new Position(2, 5)));
    game.playMove(new Move(new Position(5, 0), new Position(2, 3)));
    game.playMove(new Move(new Position(6, 7), new Position(5, 5)));
    game.playMove(new Move(new Position(7, 4), new Position(5, 6)));
    // Scholar's Mate (black checkmate)
    assertEquals(-100, solver.evaluateBoard(game.getGameState(), false));
    game.getBoard().setPlayer(true);
    assertEquals(100, solver.evaluateBoard(game.getGameState(), true));
  }

  @Test
  public void testPromotionHeuristic() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver = new Solver();
    solver.setHeuristic(HeuristicType.PROMOTION);
    Heuristic heuristic = solver.getHeuristic();

    float scoreWhenGameStarts = 0f;
    assertEquals(scoreWhenGameStarts, Math.abs(heuristic.evaluate(game.getBoard(), true)));
    assertEquals(scoreWhenGameStarts, Math.abs(heuristic.evaluate(game.getBoard(), false)));
  }

  @Test
  public void testKingActivityHeuristic() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver = new Solver();
    solver.setHeuristic(HeuristicType.KING_ACTIVITY);
    Heuristic heuristic = solver.getHeuristic();

    float scoreWhenGameStartsBlack = -10;
    float scoreWhenGameStartsWhite = 10;
    assertEquals(scoreWhenGameStartsBlack, heuristic.evaluate(game.getBoard(), false));
    assertEquals(scoreWhenGameStartsWhite, heuristic.evaluate(game.getBoard(), true));
  }

  @Test
  public void testKingSafetyHeuristic() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver = new Solver();
    solver.setHeuristic(HeuristicType.KING_SAFETY);
    Heuristic heuristic = solver.getHeuristic();

    float scoreWhenGameStartsIsBalanced = 0f;
    assertEquals(
        scoreWhenGameStartsIsBalanced, Math.abs(heuristic.evaluate(game.getBoard(), false)));
  }

  @Test
  public void testKingSafetyHeuristicToChecks() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver = new Solver();
    solver.setHeuristic(HeuristicType.KING_SAFETY);
    Heuristic heuristic = solver.getHeuristic();

    int scoreWhenGameStartsIsBalanced = 0;
    assertEquals(scoreWhenGameStartsIsBalanced, heuristic.evaluate(game.getBoard(), true));
  }

  @Test
  public void testBishopEndgameHeuristic() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver = new Solver();
    solver.setHeuristic(HeuristicType.BISHOP_ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard();

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

    // Expected score in this position
    float expectedScoreBlack = -2f * (100f / 96);
    assertEquals(expectedScoreBlack, heuristic.evaluate(game.getBoard(), false));
  }

  @Test
  public void testBishopEndgameHeuristicTwoBishops() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver = new Solver();
    solver.setHeuristic(HeuristicType.BISHOP_ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    // Expected score in this position
    float expectedScoreWhenGameStartsWhite = 2f * (100f / 96);
    assertEquals(expectedScoreWhenGameStartsWhite, heuristic.evaluate(game.getBoard(), true));
  }

  @Test
  public void testPawnChainsHeuristicWhenGameStarts() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver = new Solver();
    solver.setHeuristic(HeuristicType.PAWN_CHAIN);
    Heuristic heuristic = solver.getHeuristic();

    // Expected score
    int expectedScoreWhenGameStarts = 0;
    assertEquals(expectedScoreWhenGameStarts, heuristic.evaluate(game.getBoard(), true));
  }

  @Test
  public void testKingOppositionHeuristic() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver = new Solver();
    solver.setHeuristic(HeuristicType.KING_OPPOSITION);
    Heuristic heuristic = solver.getHeuristic();

    // Expected score
    int expectedScore = 0;
    assertEquals(expectedScore, heuristic.evaluate(game.getBoard(), true));
  }

  @Test
  public void testKingOppositionHeuristicStrongOpposition() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver = new Solver();
    solver.setHeuristic(HeuristicType.KING_OPPOSITION);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard();

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

    // Expected score
    float expectedScore = -100;
    assertEquals(expectedScore, heuristic.evaluate(game.getBoard(), true));
  }

  @Test
  public void testBishopEndgameHeuristicSameColorBishopOpponent() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver = new Solver();
    solver.setHeuristic(HeuristicType.BISHOP_ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard();

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

    float expectedScore = 4f * (100f / 96);
    assertEquals(expectedScore, heuristic.evaluate(game.getBoard(), true));
  }

  @Test
  public void testBishopEndgameHeuristicSameColorBishopSamePlayer() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver = new Solver();
    solver.setHeuristic(HeuristicType.BISHOP_ENDGAME);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard();

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

    // Expected score in this position
    float expectedScore = 18f * (100f / 96);
    assertEquals(expectedScore, heuristic.evaluate(game.getBoard(), true));
  }

  @Test
  public void testKingOppositionHeuristicDiagonal() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver = new Solver();
    solver.setHeuristic(HeuristicType.KING_OPPOSITION);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard();

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
    // Expected score in this position
    float expectedScore = -50f;
    assertEquals(expectedScore, heuristic.evaluate(game.getBoard(), true));
  }

  @Test
  public void testKingSafetyHeuristicInCenter() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver = new Solver();
    solver.setHeuristic(HeuristicType.KING_SAFETY);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard();

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

    float expectedScore = 31.25f;
    assertEquals(expectedScore, heuristic.evaluate(game.getBoard(), true));
  }

  @Test
  public void testKingActivityHeuristicKingHasManyMoves() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver = new Solver();
    solver.setHeuristic(HeuristicType.KING_ACTIVITY);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard();

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

    // Expected score in this position
    float expectedScore = 10;
    assertEquals(expectedScore, heuristic.evaluate(game.getBoard(), true));
  }

  @Test
  public void testPawnChainsHeuristic() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver = new Solver();
    solver.setHeuristic(HeuristicType.PAWN_CHAIN);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard();

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

    // Expected score
    int expectedScore = 0;
    assertEquals(expectedScore, heuristic.evaluate(game.getBoard(), true));
  }

  @Test
  public void testPawnPromotionHeuristicCloseToPromotion() {
    game = Game.initialize(false, false, null, null, null, new HashMap<>());
    solver = new Solver();
    solver.setHeuristic(HeuristicType.PROMOTION);
    Heuristic heuristic = solver.getHeuristic();

    BoardRepresentation board = game.getBoard();

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

    // Expected score
    int expectedScore = 0;
    assertEquals(expectedScore, heuristic.evaluate(game.getBoard(), true));
  }

  @Test
  public void testDevelopmentHeuristicWhenGameStartsWhite() {
    solver.setHeuristic(HeuristicType.DEVELOPMENT);

    assertEquals(0f, Math.abs(solver.evaluateBoard(game.getGameState(), false)));
  }

  @Test
  public void testDevelopmentHeuristicWhenGameStartsBlack() {
    solver.setHeuristic(HeuristicType.DEVELOPMENT);

    assertEquals(0f, Math.abs(solver.evaluateBoard(game.getGameState(), true)));
  }

  @Test
  public void testDevelopmentHeuristicWhenEveryPawnForwardByTwo() {
    solver.setHeuristic(HeuristicType.DEVELOPMENT);

    game.playMove(Move.fromString("a2-a4"));
    game.playMove(Move.fromString("a7-a5"));
    game.playMove(Move.fromString("b2-b4"));
    game.playMove(Move.fromString("b7-b5"));
    game.playMove(Move.fromString("c2-c4"));
    game.playMove(Move.fromString("c7-c5"));
    game.playMove(Move.fromString("d2-d4"));
    game.playMove(Move.fromString("d7-d5"));
    game.playMove(Move.fromString("e2-e4"));
    game.playMove(Move.fromString("e7-e5"));
    game.playMove(Move.fromString("f2-f4"));
    game.playMove(Move.fromString("f7-f5"));
    game.playMove(Move.fromString("g2-g4"));
    game.playMove(Move.fromString("g7-g5"));
    game.playMove(Move.fromString("h2-h4"));
    game.playMove(Move.fromString("h7-h5"));

    assertEquals(0f, Math.abs(solver.evaluateBoard(game.getGameState(), false)));
  }

  @Test
  public void testDevelopmentHeuristicKnightsBishopsRookMoved() {
    solver.setHeuristic(HeuristicType.DEVELOPMENT);

    game.playMove(Move.fromString("g1-f3"));
    game.playMove(Move.fromString("g8-f6"));
    game.playMove(Move.fromString("b1-c3"));
    game.playMove(Move.fromString("b8-c6"));
    game.playMove(Move.fromString("g2-g3"));
    game.playMove(Move.fromString("g7-g6"));
    game.playMove(Move.fromString("f1-g2"));
    game.playMove(Move.fromString("f8-g7"));
    game.playMove(Move.fromString("e1-f1"));
    game.playMove(Move.fromString("e8-f8"));
    game.playMove(Move.fromString("h1-g1"));
    game.playMove(Move.fromString("h8-g8"));
    game.playMove(Move.fromString("b2-b3"));
    game.playMove(Move.fromString("b7-b6"));
    game.playMove(Move.fromString("c1-b2"));
    game.playMove(Move.fromString("c8-b7"));
    game.playMove(Move.fromString("a1-b1"));
    game.playMove(Move.fromString("a8-b8"));
    game.playMove(Move.fromString("d1-e1"));
    game.playMove(Move.fromString("d8-e8"));

    assertEquals(0f, Math.abs(solver.evaluateBoard(game.getGameState(), true)));
  }
}
