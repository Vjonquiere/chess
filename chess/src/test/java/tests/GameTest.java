package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pdp.utils.Logging.configureGlobalLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.exceptions.FailedSaveException;
import pdp.exceptions.IllegalMoveException;
import pdp.model.Game;
import pdp.model.GameAbstract;
import pdp.model.GameState;
import pdp.model.ai.AlgorithmType;
import pdp.model.ai.HeuristicType;
import pdp.model.ai.Solver;
import pdp.model.ai.heuristics.EndGameHeuristic;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.model.parsers.BoardFileParser;
import pdp.model.parsers.FenHeader;
import pdp.model.parsers.FileBoard;
import pdp.model.savers.BoardSaver;
import pdp.utils.Position;
import pdp.utils.Timer;

public class GameTest {
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @BeforeAll
  public static void setUpLocale() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @BeforeEach
  void setUpConsole() {
    System.setOut(new PrintStream(outputStream));
    System.setErr(new PrintStream(outputStream));
    configureGlobalLogger();
  }

  @AfterEach
  void tearDownConsole() {
    System.setOut(originalOut);
    System.setErr(originalErr);
    outputStream.reset();
    configureGlobalLogger();
  }

  @Test
  public void playMoveTest() {

    // Correctly play move
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    BitboardRepresentation bitboards = new BitboardRepresentation();
    game.playMove(new Move(new Position(0, 1), new Position(0, 2)));
    bitboards.movePiece(new Position(0, 1), new Position(0, 2));
    assertEquals(game.getBoard(), bitboards);

    // Play move only in game
    game.playMove(new Move(new Position(1, 6), new Position(1, 5)));
    assertNotEquals(game.getBoard(), bitboards);
    bitboards.movePiece(new Position(1, 6), new Position(1, 5)); // Play move in bitboards
    assertEquals(game.getBoard(), bitboards);

    // Tests to add
    // 6. try a move that is not a classical or special move
  }

  @Test
  public void playEnPassantTest() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    game.playMove(new Move(new Position(0, 1), new Position(0, 3)));
    game.playMove(new Move(new Position(0, 6), new Position(0, 5)));
    game.playMove(new Move(new Position(0, 3), new Position(0, 4)));
    game.playMove(new Move(new Position(1, 6), new Position(1, 4)));
    game.playMove(new Move(new Position(0, 4), new Position(1, 5)));
    BitboardRepresentation bitboards = new BitboardRepresentation();
    bitboards.deletePieceAt(1, 6);
    bitboards.movePiece(new Position(0, 1), new Position(1, 5));
    bitboards.movePiece(new Position(0, 6), new Position(0, 5));
    assertEquals(bitboards, game.getBoard());
  }

  @Test
  public void playEnPassantIsCheckTest() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    game.playMove(new Move(new Position(4, 1), new Position(4, 2)));
    game.playMove(new Move(new Position(7, 6), new Position(7, 4)));
    game.playMove(new Move(new Position(4, 2), new Position(4, 3)));
    game.playMove(new Move(new Position(7, 7), new Position(7, 5)));
    game.playMove(new Move(new Position(4, 3), new Position(4, 4)));
    game.playMove(new Move(new Position(7, 5), new Position(4, 5)));
    game.playMove(new Move(new Position(7, 1), new Position(7, 2)));
    game.playMove(new Move(new Position(3, 6), new Position(3, 4)));

    assertThrows(
        IllegalMoveException.class,
        () -> {
          game.playMove(new Move(new Position(4, 4), new Position(3, 5)));
        });
  }

  @Test
  public void playDoublePushTest() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    game.playMove(new Move(new Position(1, 1), new Position(1, 3)));
    BitboardRepresentation bitboards = new BitboardRepresentation();
    bitboards.movePiece(new Position(1, 1), new Position(1, 3));
    assertEquals(bitboards, game.getBoard());
  }

  @Test
  public void playDoublePushIsCheckTest() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    game.playMove(new Move(new Position(0, 1), new Position(0, 2)));
    game.playMove(new Move(new Position(3, 6), new Position(3, 4)));
    game.playMove(new Move(new Position(0, 2), new Position(0, 3)));
    game.playMove(new Move(new Position(3, 7), new Position(3, 6)));
    game.playMove(new Move(new Position(0, 3), new Position(0, 4)));
    game.playMove(new Move(new Position(3, 6), new Position(3, 5)));
    game.playMove(new Move(new Position(1, 1), new Position(1, 2)));
    game.playMove(new Move(new Position(3, 5), new Position(1, 3)));
    assertThrows(
        IllegalMoveException.class,
        () -> {
          game.playMove(new Move(new Position(3, 1), new Position(3, 3)));
        });
  }

  @Test
  public void wrongTurnForWhitePlayerTest() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    game.playMove(new Move(new Position(0, 1), new Position(0, 2)));
    assertThrows(
        IllegalMoveException.class,
        () -> {
          game.playMove(new Move(new Position(0, 2), new Position(0, 3)));
        });
  }

  @Test
  public void wrongTurnForBlackPlayerTest() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    game.playMove(new Move(new Position(0, 1), new Position(0, 2)));
    game.playMove(new Move(new Position(0, 6), new Position(0, 5)));
    assertThrows(
        IllegalMoveException.class,
        () -> {
          game.playMove(new Move(new Position(0, 5), new Position(0, 4)));
        });
  }

  @Test
  public void pinnedPieceTest() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    game.playMove(new Move(new Position(0, 1), new Position(0, 2)));
    game.playMove(new Move(new Position(2, 6), new Position(2, 4)));
    game.playMove(new Move(new Position(0, 2), new Position(0, 3)));
    game.playMove(new Move(new Position(3, 7), new Position(0, 4)));
    assertThrows(
        IllegalMoveException.class,
        () -> {
          game.playMove(new Move(new Position(3, 1), new Position(3, 2)));
        });
  }

  @Test
  public void threefoldRepetitionTest() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    game.playMove(Move.fromString("b1-c3"));
    game.playMove(Move.fromString("g8-f6"));
    game.playMove(Move.fromString("c3-b1"));
    game.playMove(Move.fromString("f6-g8"));
    game.playMove(Move.fromString("b1-c3"));
    game.playMove(Move.fromString("g8-f6"));
    game.playMove(Move.fromString("c3-b1"));
    game.playMove(Move.fromString("f6-g8"));
    game.playMove(Move.fromString("b1-c3"));
    game.playMove(Move.fromString("g8-f6"));
    game.playMove(Move.fromString("c3-b1"));
    game.playMove(Move.fromString("f6-g8"));
    assertTrue(game.getGameState().isThreefoldRepetition());
    assertTrue(game.isOver());
  }

  @Test
  public void noThreefoldRepetitionOnIllegalClassicalTest() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    try {
      game.playMove(Move.fromString("h1-h3"));
    } catch (IllegalMoveException e) {

    }
    try {
      game.playMove(Move.fromString("h1-h3"));
    } catch (IllegalMoveException e) {

    }
    assertFalse(game.getGameState().isThreefoldRepetition());
    assertFalse(game.isOver());
  }

  @Test
  public void noThreefoldRepetitionOnIllegalSpecialTest() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    try {
      game.playMove(Move.fromString("e1-c1"));
    } catch (IllegalMoveException e) {

    }
    try {
      game.playMove(Move.fromString("e1-g1"));
    } catch (IllegalMoveException e) {

    }
    assertFalse(game.getGameState().isThreefoldRepetition());
    assertFalse(game.isOver());
  }

  @Test
  public void testIsEndGamePhaseShouldBeFalse() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    assertFalse(game.isEndGamePhase());
  }

  @Test
  public void testIsEndGamePhaseShouldBeTrue() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    // Move pawns
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

    // Move kings
    game.playMove(Move.fromString("e1-e2"));
    game.playMove(Move.fromString("e8-e7"));

    // Get queens off the board
    game.playMove(Move.fromString("d4-e5"));
    game.playMove(Move.fromString("d5-e4"));
    game.playMove(Move.fromString("d1-d8"));
    game.playMove(Move.fromString("e7-d8"));

    // Play a few more moves to reach conditions
    game.playMove(Move.fromString("a1-a3"));
    game.playMove(Move.fromString("a8-a6"));
    game.playMove(Move.fromString("h1-h3"));
    game.playMove(Move.fromString("h8-h6"));
    game.playMove(Move.fromString("h3-f3"));
    game.playMove(Move.fromString("h6-f6"));
    game.playMove(Move.fromString("a3-c3"));
    game.playMove(Move.fromString("a6-c6"));
    game.playMove(Move.fromString("h4-g5"));
    game.playMove(Move.fromString("h5-g4"));
    game.playMove(Move.fromString("c4-b5"));
    game.playMove(Move.fromString("a5-b4"));
    game.playMove(Move.fromString("b5-c6"));
    game.playMove(Move.fromString("b4-c3"));
    game.playMove(Move.fromString("g5-f6"));
    game.playMove(Move.fromString("g4-f3"));
    game.playMove(Move.fromString("g1-f3"));
    game.playMove(Move.fromString("g8-f6"));
    game.playMove(Move.fromString("b1-c3"));
    game.playMove(Move.fromString("b8-c6"));
    game.playMove(Move.fromString("c1-a3"));
    game.playMove(Move.fromString("c8-a6"));
    game.playMove(Move.fromString("e2-e1"));
    game.playMove(Move.fromString("a6-f1"));
    game.playMove(Move.fromString("e1-f1"));
    game.playMove(Move.fromString("c6-e5"));
    game.playMove(Move.fromString("a3-c5"));
    game.playMove(Move.fromString("f8-c5"));
    game.playMove(Move.fromString("f3-e5"));

    assertTrue(game.isEndGamePhase());
  }

  @Test
  public void testCheckGameStatusMateFromBlack() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    game.playMove(Move.fromString("f2-f4"));
    game.playMove(Move.fromString("e7-e6"));
    game.playMove(Move.fromString("g2-g4"));
    game.playMove(Move.fromString("d8-h4"));

    assertTrue(game.isOver());
  }

  @Test
  public void testCheckGameStatusCheckDrawByAgreement() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    game.playMove(Move.fromString("f2-f4"));
    game.playMove(Move.fromString("e7-e6"));

    game.getGameState().doesBlackWantsToDraw();
    game.getGameState().doesWhiteWantsToDraw();

    game.playMove(Move.fromString("a2-a3"));

    assertTrue(game.isOver());
  }

  @Test
  public void testCheckGameStatusHasWhiteResigned() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    game.playMove(Move.fromString("f2-f4"));
    game.playMove(Move.fromString("e7-e6"));

    game.getGameState().whiteResigns();

    game.playMove(Move.fromString("a2-a3"));

    assertTrue(game.isOver());
  }

  @Test
  public void testCheckGameStatusHasBlackResigned() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    game.playMove(Move.fromString("f2-f4"));
    game.playMove(Move.fromString("e7-e6"));

    game.getGameState().blackResigns();

    game.playMove(Move.fromString("a2-a3"));

    assertTrue(game.isOver());
  }

  @Test
  public void testCheckGameStatusDrawByInsufficientMaterial() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    BoardRepresentation board = game.getBoard();

    Position initWhiteKingPos = new Position(4, 0);
    Position initBlackKingPos = new Position(4, 7);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListBlack.add(initBlackKingPos);

    BitboardRepresentationTest.deleteAllPiecesExceptThosePositionsBoard(
        board, posListWhite, posListBlack);

    game.playMove(Move.fromString("e1-e2"));

    assertTrue(game.isOver());
  }

  @Test
  public void testCheckGameStatusStaleMate() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    BoardRepresentation board = game.getBoard();

    Position initWhiteKingPos = new Position(4, 0);
    Position initBlackKingPos = new Position(4, 7);
    Position initBlackRookPos = new Position(0, 7);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListBlack.add(initBlackKingPos);
    posListBlack.add(initBlackRookPos);

    BitboardRepresentationTest.deleteAllPiecesExceptThosePositionsBoard(
        board, posListWhite, posListBlack);

    assertFalse(game.isOver());
    game.playMove(Move.fromString("e1-f1"));
    assertFalse(game.isOver());
    game.playMove(Move.fromString("e8-f7"));
    game.playMove(Move.fromString("f1-g2"));
    game.playMove(Move.fromString("f7-f6"));
    game.playMove(Move.fromString("g2-f2"));
    game.playMove(Move.fromString("f6-f5"));
    game.playMove(Move.fromString("f2-g1"));
    game.playMove(Move.fromString("f5-f4"));
    game.playMove(Move.fromString("g1-h1"));
    game.playMove(Move.fromString("f4-f3"));
    game.playMove(Move.fromString("h1-h2"));
    game.playMove(Move.fromString("f3-f2"));
    game.playMove(Move.fromString("h2-h1"));
    game.playMove(Move.fromString("a8-g8"));
    game.playMove(Move.fromString("h1-h2"));
    game.playMove(Move.fromString("g8-g6"));
    game.playMove(Move.fromString("h2-h1"));
    assertFalse(game.isOver());
    game.playMove(Move.fromString("g6-g2"));

    assertTrue(game.isOver());
  }

  @Test
  public void testCheckGameStatusFiftyMoveRule() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    BoardRepresentation board = game.getBoard();

    Position initWhiteKingPos = new Position(4, 0);
    Position initWhiteRookPos = new Position(7, 0);
    Position initBlackKingPos = new Position(4, 7);
    Position initBlackRookPos = new Position(0, 7);

    List<Position> posListWhite = new ArrayList<>();
    List<Position> posListBlack = new ArrayList<>();

    posListWhite.add(initWhiteKingPos);
    posListWhite.add(initWhiteRookPos);
    posListBlack.add(initBlackKingPos);
    posListBlack.add(initBlackRookPos);

    BitboardRepresentationTest.deleteAllPiecesExceptThosePositionsBoard(
        board, posListWhite, posListBlack);

    game.playMove(Move.fromString("h1-h2"));
    game.playMove(Move.fromString("a8-a7"));
    game.playMove(Move.fromString("h2-g2"));
    game.playMove(Move.fromString("a7-b7"));
    game.playMove(Move.fromString("g2-f2"));
    game.playMove(Move.fromString("b7-c7"));
    game.playMove(Move.fromString("f2-e2"));
    // Black king moves away from check
    game.playMove(Move.fromString("e8-f8"));
    game.playMove(Move.fromString("e2-d2"));
    game.playMove(Move.fromString("c7-d7"));
    game.playMove(Move.fromString("d2-c2"));
    game.playMove(Move.fromString("d7-e7"));
    // White king moves away from check
    game.playMove(Move.fromString("e1-d1"));
    game.playMove(Move.fromString("e7-f7"));
    game.playMove(Move.fromString("c2-b2"));
    game.playMove(Move.fromString("f7-g7"));
    game.playMove(Move.fromString("b2-a2"));
    game.playMove(Move.fromString("g7-h7"));
    game.playMove(Move.fromString("a2-a3"));
    game.playMove(Move.fromString("h7-h6"));
    // 10 moves mark reached
    game.playMove(Move.fromString("a3-b3"));
    game.playMove(Move.fromString("h6-g6"));
    game.playMove(Move.fromString("b3-c3"));
    game.playMove(Move.fromString("g6-f6"));
    game.playMove(Move.fromString("c3-d3"));
    game.playMove(Move.fromString("f6-e6"));
    game.playMove(Move.fromString("d3-e3"));
    game.playMove(Move.fromString("e6-d6"));
    // White king moves away from check
    game.playMove(Move.fromString("d1-e1"));
    game.playMove(Move.fromString("d6-c6"));
    game.playMove(Move.fromString("e3-f3"));
    // Black king moves away from check
    game.playMove(Move.fromString("f8-e8"));
    game.playMove(Move.fromString("f3-g3"));
    game.playMove(Move.fromString("c6-b6"));
    game.playMove(Move.fromString("g3-h3"));
    game.playMove(Move.fromString("b6-a6"));
    game.playMove(Move.fromString("h3-h4"));
    game.playMove(Move.fromString("a6-a5"));
    game.playMove(Move.fromString("h4-g4"));
    game.playMove(Move.fromString("a5-b5"));
    // 20 moves mark reached
    game.playMove(Move.fromString("g4-f4"));
    game.playMove(Move.fromString("b5-c5"));
    game.playMove(Move.fromString("f4-e4"));
    // Black king moves away from check
    game.playMove(Move.fromString("e8-f8"));
    game.playMove(Move.fromString("e4-d4"));
    game.playMove(Move.fromString("c5-d5"));
    game.playMove(Move.fromString("d4-c4"));
    game.playMove(Move.fromString("d5-e5"));
    // White king moves away from check
    game.playMove(Move.fromString("e1-d1"));
    game.playMove(Move.fromString("e5-f5"));
    game.playMove(Move.fromString("c4-b4"));
    game.playMove(Move.fromString("f5-g5"));
    game.playMove(Move.fromString("b4-a4"));
    game.playMove(Move.fromString("g5-h5"));
    // Rooks change side
    game.playMove(Move.fromString("a4-a5"));
    game.playMove(Move.fromString("h5-h4"));
    game.playMove(Move.fromString("a5-b5"));
    game.playMove(Move.fromString("h4-g4"));
    game.playMove(Move.fromString("b5-c5"));
    game.playMove(Move.fromString("g4-f4"));
    // 30 moves mark reached
    game.playMove(Move.fromString("c5-d5"));
    game.playMove(Move.fromString("f4-e4"));
    game.playMove(Move.fromString("d5-e5"));
    game.playMove(Move.fromString("e4-d4"));
    // White king moves away from check
    game.playMove(Move.fromString("d1-e1"));
    game.playMove(Move.fromString("d4-c4"));
    game.playMove(Move.fromString("e5-f5"));
    // Black king moves away from check
    game.playMove(Move.fromString("f8-e8"));
    game.playMove(Move.fromString("f5-g5"));
    game.playMove(Move.fromString("c4-b4"));
    game.playMove(Move.fromString("g5-h5"));
    game.playMove(Move.fromString("b4-a4"));
    game.playMove(Move.fromString("h5-h6"));
    game.playMove(Move.fromString("a4-a3"));
    game.playMove(Move.fromString("h6-g6"));
    game.playMove(Move.fromString("a3-b3"));
    game.playMove(Move.fromString("g6-f6"));
    game.playMove(Move.fromString("b3-c3"));
    game.playMove(Move.fromString("f6-e6"));
    // Black king moves away from check
    game.playMove(Move.fromString("e8-f8"));
    // 40 moves mark reached
    game.playMove(Move.fromString("e6-d6"));
    game.playMove(Move.fromString("c3-d3"));
    game.playMove(Move.fromString("d6-c6"));
    game.playMove(Move.fromString("d3-e3"));
    // White king moves away from check
    game.playMove(Move.fromString("e1-d1"));
    game.playMove(Move.fromString("e3-f3"));
    game.playMove(Move.fromString("c6-b6"));
    game.playMove(Move.fromString("f3-g3"));
    game.playMove(Move.fromString("b6-a6"));
    game.playMove(Move.fromString("g3-h3"));
    game.playMove(Move.fromString("a6-a7"));
    game.playMove(Move.fromString("h3-h2"));
    game.playMove(Move.fromString("a7-b7"));
    game.playMove(Move.fromString("h2-g2"));
    game.playMove(Move.fromString("b7-c7"));
    game.playMove(Move.fromString("g2-f2"));
    game.playMove(Move.fromString("c7-d7"));
    game.playMove(Move.fromString("f2-e2"));
    game.playMove(Move.fromString("d7-e7"));
    game.playMove(Move.fromString("f8-g8"));
    // 50 moves mark reached

    // 50 moves rules should be applied

    assertTrue(game.isOver());
  }

  @Test
  public void testSaveGame() throws IOException {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    Path tempFile = Files.createTempFile("game-save-test", ".txt");
    String tempFilePath = tempFile.toAbsolutePath().toString();

    game.saveGame(tempFilePath);

    String content = Files.readString(tempFile);

    assertNotNull(content);
    assertFalse(content.isEmpty());

    String board =
        BoardSaver.saveBoard(
            new FileBoard(
                game.getBoard(),
                game.getBoard().getPlayer(),
                new FenHeader(true, true, true, true, null, 0, 0)));

    assertTrue(content.contains(board));

    Files.deleteIfExists(tempFile);
  }

  @Test
  public void testSaveGameWithHistory() throws IOException, IllegalMoveException {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    Move move = new Move(new Position(4, 1), new Position(4, 3));

    game.playMove(move);

    Path tempFile = Files.createTempFile("game-save-test", ".txt");
    String tempFilePath = tempFile.toAbsolutePath().toString();
    game.saveGame(tempFilePath);

    String content = Files.readString(tempFile);

    String board =
        BoardSaver.saveBoard(
            new FileBoard(
                game.getBoard(),
                game.getBoard().getPlayer(),
                new FenHeader(true, true, true, true, new Position(4, 2), 0, 1)));

    assertTrue(content.contains(board));

    assertTrue(content.contains(game.getHistory().toAlgebraicString()));

    Files.deleteIfExists(tempFile);
  }

  @Test
  public void testSaveGameToUnwritableLocation() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    // Use an invalid directory
    String invalidPath = "/this/path/does/not/exist-" + UUID.randomUUID().toString();

    assertThrows(FailedSaveException.class, () -> game.saveGame(invalidPath));
  }

  @Test
  public void startAiTest() {
    Solver s = new Solver();
    Game game = Game.initialize(true, false, s, s, null, new HashMap<>());
    game.startAi();
    // AI white and white's turn
    assertNotEquals(
        game.getGameState().isWhiteTurn(),
        Game.initialize(true, false, s, s, null, new HashMap<>()).getGameState().isWhiteTurn());
    assertNotEquals(
        game.getGameState().getBoard(),
        Game.initialize(true, false, s, s, null, new HashMap<>()).getGameState().getBoard());
    BitboardRepresentation bitboard = (BitboardRepresentation) game.getBoard();
    // Ai white and Black's turn
    game.startAi();
    assertEquals(game.getGameState().getBoard(), bitboard);

    game = Game.initialize(false, true, s, s, null, new HashMap<>());
    game.startAi();
    // AI black and white's turn
    assertEquals(
        game.getGameState().isWhiteTurn(),
        Game.initialize(false, true, s, s, null, new HashMap<>()).getGameState().isWhiteTurn());
    assertEquals(
        game.getGameState().getBoard(),
        Game.initialize(false, true, s, s, null, new HashMap<>()).getGameState().getBoard());
  }

  @Test
  public void AIPlayOnItsOwn() {
    Solver s = new Solver();
    s.setDepth(3);
    s.setAlgorithm(AlgorithmType.ALPHA_BETA);
    Game game = Game.initialize(true, true, s, s, null, new HashMap<>());

    game.startAi();

    assertTrue(game.isOver());
  }

  @Test
  public void changeHeuristicEndGame() {
    BoardFileParser parser = new BoardFileParser();
    ClassLoader classLoader = getClass().getClassLoader();
    URL filePath = classLoader.getResource("gameBoards/fenVersions/endGame");
    FileBoard board = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    Solver s = new Solver();
    s.setHeuristic(HeuristicType.STANDARD);
    s.setEndgameHeuristic(HeuristicType.ENDGAME);
    Game game = Game.initialize(true, false, s, s, null, board, new HashMap<>());
    game.startAi();
    assertTrue(s.getHeuristic() instanceof EndGameHeuristic);
  }

  @Test
  public void testOutOfTimeCallbackWhite() throws Exception {
    Game game = spy(Game.initialize(false, false, null, null, new Timer(5000), new HashMap<>()));

    GameState mockGameState = mock(GameState.class);

    when(mockGameState.isWhiteTurn()).thenReturn(true);

    Field field = GameAbstract.class.getDeclaredField("gameState");
    field.setAccessible(true);
    field.set(game, mockGameState);

    game.outOfTimeCallback();

    verify(game, times(1)).outOfTimeCallback();
    verify(mockGameState).playerOutOfTime(true);
  }

  @Test
  public void testOutOfTimeCallbackBlack() throws Exception {
    Game game = spy(Game.initialize(false, false, null, null, new Timer(5000), new HashMap<>()));

    GameState mockGameState = mock(GameState.class);

    when(mockGameState.isWhiteTurn()).thenReturn(false);

    Field field = GameAbstract.class.getDeclaredField("gameState");
    field.setAccessible(true);
    field.set(game, mockGameState);

    game.outOfTimeCallback();

    verify(game, times(1)).outOfTimeCallback();
    verify(game.getGameState()).playerOutOfTime(false);
  }

  @Test
  public void testWhiteLosesOnTime() throws InterruptedException {
    Runnable callback = mock(Runnable.class);

    Timer timer = new Timer(100);

    Game game = spy(Game.initialize(false, false, null, null, timer, new HashMap<>()));

    timer.setCallback(callback);

    Thread.sleep(150);

    assertEquals(0, game.getGameState().getMoveTimer().getTimeRemaining());
    verify(callback, times(1)).run();
  }

  @Test
  public void testBlackLosesOnTime() throws InterruptedException {

    Runnable callback = mock(Runnable.class);

    Timer timer = new Timer(200);

    Game game = spy(Game.initialize(false, false, null, null, timer, new HashMap<>()));

    timer.setCallback(callback);

    Thread.sleep(20);
    game.playMove(Move.fromString("e2-e4"));

    Thread.sleep(250);

    assertEquals(0, game.getGameState().getMoveTimer().getTimeRemaining());
    verify(callback, times(1)).run();
  }

  @Test
  public void testNotCalledBeforeTimeout() throws InterruptedException {

    Runnable callback = mock(Runnable.class);

    Timer timer = new Timer(200);

    Game game = spy(Game.initialize(false, false, null, null, timer, new HashMap<>()));

    timer.setCallback(callback);

    Thread.sleep(20);

    game.playMove(Move.fromString("e2-e4"));

    Thread.sleep(20);
    game.getGameState().getMoveTimer().stop();

    assertTrue(game.getGameState().getMoveTimer().getTimeRemaining() < 200);
    assertTrue(game.getGameState().getMoveTimer().getTimeRemaining() > 0);
    verify(callback, never()).run();
  }

  @Test
  public void testTimeResetAfterMove() throws InterruptedException {

    Runnable callback = mock(Runnable.class);

    Timer timer = new Timer(100);

    Game game = spy(Game.initialize(false, false, null, null, timer, new HashMap<>()));

    timer.setCallback(callback);

    Thread.sleep(60);
    game.playMove(Move.fromString("e2-e4"));
    Thread.sleep(70);

    game.getGameState().getMoveTimer().stop();

    assert (game.getGameState().getMoveTimer().getTimeRemaining() < 100);
    assertTrue(game.getGameState().getMoveTimer().getTimeRemaining() > 0);
    verify(callback, never()).run();
  }

  @Test
  public void testSetBlackAi() {
    Game game = spy(Game.initialize(false, false, null, null, null, new HashMap<>()));

    assertFalse(game.isBlackAi());
    game.setBlackAi(true);
    assertTrue(game.isBlackAi());
  }

  @Test
  public void testSetWhiteAi() {
    Game game = spy(Game.initialize(false, false, null, null, null, new HashMap<>()));

    assertFalse(game.isWhiteAi());
    game.setWhiteAi(true);
    assertTrue(game.isWhiteAi());
  }

  @Test
  public void testSetInit() {

    assertFalse(Game.getInstance().isInitialized());
    Game.getInstance().setInitializing(true);
    assertTrue(Game.getInstance().isInitialized());
  }

  @Test
  public void testGetStringHistory() {
    Game game = spy(Game.initialize(false, false, null, null, null, new HashMap<>()));
    Move move = new Move(new Position(4, 1), new Position(4, 3));
    game.playMove(move);

    assertEquals("1. W e2-e4", game.getStringHistory());
  }

  @Test
  public void testSetBlackSolver() {

    Game game = spy(Game.initialize(false, false, null, null, null, new HashMap<>()));

    Solver newSolverBlack = new Solver();
    newSolverBlack.setDepth(2);
    game.setBlackSolver(newSolverBlack);
    assertEquals(2, game.getBlackSolver().getDepth());
    newSolverBlack.setDepth(6);
    game.setBlackSolver(newSolverBlack);
    assertEquals(6, game.getBlackSolver().getDepth());
  }

  @Test
  public void testSetWhiteSolver() {
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());

    Solver newSolverWhite = new Solver();
    newSolverWhite.setDepth(2);
    game.setWhiteSolver(newSolverWhite);
    assertEquals(2, game.getWhiteSolver().getDepth());
    newSolverWhite.setDepth(6);
    game.setWhiteSolver(newSolverWhite);
    assertEquals(6, game.getWhiteSolver().getDepth());
  }
}
