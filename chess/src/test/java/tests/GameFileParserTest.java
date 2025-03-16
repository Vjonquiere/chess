package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.exceptions.IllegalMoveException;
import pdp.model.Game;
import pdp.model.board.BitboardRepresentation;
import pdp.model.board.Move;
import pdp.model.parsers.BoardFileParser;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

public class GameFileParserTest {
  private BoardFileParser parser = new BoardFileParser();
  private ClassLoader classLoader = getClass().getClassLoader();
  private static final Logger LOGGER = Logger.getLogger(GameFileParserTest.class.getName());

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @BeforeEach
  void setUpConsole() {
    System.setOut(new PrintStream(outputStream));
    System.setErr(new PrintStream(outputStream));
  }

  @AfterEach
  void tearDownConsole() {
    System.setOut(originalOut);
    System.setErr(originalErr);
    outputStream.reset();
  }

  @Test
  public void parseDefaultGameFile() {
    URL filePath = classLoader.getResource("gameBoards/defaultGame");
    FileBoard board = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    assertEquals(new BitboardRepresentation(), board.board());
    assertTrue(board.isWhiteTurn());
  }

  @Test
  public void parseDefaultGameWithFirstPlayerIsBlack() {
    URL filePath = classLoader.getResource("gameBoards/defaultGameWithBlackTurn");
    FileBoard board = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    assertEquals(new BitboardRepresentation(), board.board());
    assertFalse(board.isWhiteTurn());
    Game game = Game.initialize(false, false, null, null, null, board, new HashMap<>());
    assertEquals(game.getGameState().isWhiteTurn(), board.isWhiteTurn());
    assertEquals(game.getBoard().getBoard(), board.board());
    assertThrows(
        IllegalMoveException.class,
        () -> {
          game.playMove(new Move(new Position(1, 1), new Position(1, 2)));
        }); // ensure white move in not possible
  }

  @Test
  public void parseEmptyGameFile() {
    /* Loading an empty game is not possible,
       the result should be EXIT_FAILURE
    */
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/emptyGame");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    verify(mockRuntime).exit(1);
  }

  @Test
  public void parseWrongBoardFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/wrongBoard");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    verify(mockRuntime).exit(1);
  }

  @Test
  public void parseUnknownFile() {
    Runtime mockRuntime = mock(Runtime.class);
    parser.parseGameFile("Unknow/file/path", mockRuntime);
    verify(mockRuntime).exit(1);
  }

  @Test
  public void parseWrongPlayerFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/unknownPlayerGame");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    verify(mockRuntime).exit(1);
  }

  @Test
  public void parseWrongFormattedFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/wrongFormattedGame");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    verify(mockRuntime).exit(1);
  }

  @Test
  public void parseUnknownPieceFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/unknownPieceGame");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    verify(mockRuntime).exit(1);
  }

  @Test
  public void parseNoWhiteKingFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/noWhiteKing");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    verify(mockRuntime).exit(1);
  }

  @Test
  public void parseNoBlackKingFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/noBlackKing");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    verify(mockRuntime).exit(1);
  }

  @Test
  public void parseBlackCheckMateFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/scholarMate");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    verify(mockRuntime).exit(1);
  }

  @Test
  public void parseCommentFile() {
    URL filePath = classLoader.getResource("gameBoards/commentsBoard");
    parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
  }

  @Test
  public void parseTest1() {
    URL filePath = classLoader.getResource("gameBoards/gameExample1");
    FileBoard board = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    game.playMove(
        new Move(
            new Position(0, 1),
            new Position(0, 3),
            new ColoredPiece(Piece.PAWN, Color.WHITE),
            false));
    game.playMove(
        new Move(
            new Position(6, 7),
            new Position(7, 5),
            new ColoredPiece(Piece.KNIGHT, Color.BLACK),
            false));
    game.playMove(
        new Move(
            new Position(6, 1),
            new Position(6, 3),
            new ColoredPiece(Piece.PAWN, Color.WHITE),
            false));
    game.playMove(
        new Move(
            new Position(7, 5),
            new Position(6, 3),
            new ColoredPiece(Piece.KNIGHT, Color.BLACK),
            true,
            new ColoredPiece(Piece.PAWN, Color.WHITE)));
    game.playMove(
        new Move(
            new Position(5, 0),
            new Position(7, 2),
            new ColoredPiece(Piece.BISHOP, Color.WHITE),
            false));
    assertEquals(game.getBoard().getBoard(), board.board());
    assertEquals(game.getGameState().isWhiteTurn(), board.isWhiteTurn());
    assertFalse(board.isWhiteTurn());
  }

  @Test
  public void parseTestWithHistory() {
    /*Verify that parsing a game with history or do not change output*/
    URL filePath = classLoader.getResource("gameBoards/gameExample1WithHistory");
    FileBoard board = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    Game game = Game.initialize(false, false, null, null, null, new HashMap<>());
    game.playMove(
        new Move(
            new Position(0, 1),
            new Position(0, 3),
            new ColoredPiece(Piece.PAWN, Color.WHITE),
            false));
    game.playMove(
        new Move(
            new Position(6, 7),
            new Position(7, 5),
            new ColoredPiece(Piece.KNIGHT, Color.BLACK),
            false));
    game.playMove(
        new Move(
            new Position(6, 1),
            new Position(6, 3),
            new ColoredPiece(Piece.PAWN, Color.WHITE),
            false));
    game.playMove(
        new Move(
            new Position(7, 5),
            new Position(6, 3),
            new ColoredPiece(Piece.KNIGHT, Color.BLACK),
            true,
            new ColoredPiece(Piece.PAWN, Color.WHITE)));
    game.playMove(
        new Move(
            new Position(5, 0),
            new Position(7, 2),
            new ColoredPiece(Piece.BISHOP, Color.WHITE),
            false));
    assertEquals(game.getBoard().getBoard(), board.board());
    assertEquals(game.getGameState().isWhiteTurn(), board.isWhiteTurn());
    assertFalse(board.isWhiteTurn());
  }

  @Test
  public void parseFenDefaultFile() {
    URL filePath = classLoader.getResource("gameBoards/fenVersions/defaultGame");
    FileBoard fb = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    assertEquals(new BitboardRepresentation(), fb.board());
    assertTrue(fb.header().whiteKingCastling());
    assertTrue(fb.header().whiteQueenCastling());
    assertTrue(fb.header().blackKingCastling());
    assertTrue(fb.header().blackQueenCastling());
    assertNull(fb.header().enPassant());
    assertEquals(0, fb.header().fiftyMoveRule());
    assertEquals(0, fb.header().playedMoves());
  }

  @Test
  public void parseFenDefaultFileWithoutCastling() {
    URL filePath = classLoader.getResource("gameBoards/fenVersions/defaultGameCastlingUnable");
    FileBoard fb = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    assertEquals(new BitboardRepresentation(), fb.board());
    assertFalse(fb.header().whiteKingCastling());
    assertFalse(fb.header().whiteQueenCastling());
    assertFalse(fb.header().blackKingCastling());
    assertFalse(fb.header().blackQueenCastling());
    assertNull(fb.header().enPassant());
    assertEquals(20, fb.header().fiftyMoveRule());
    assertEquals(10, fb.header().playedMoves());
  }

  @Test
  public void parseFenDefaultFileWithEnPassant() {
    URL filePath = classLoader.getResource("gameBoards/fenVersions/defaultGameWithEnPassant");
    FileBoard fb = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    System.out.println(fb.header());
    assertEquals(new BitboardRepresentation(), fb.board());
    assertTrue(fb.header().whiteKingCastling());
    assertTrue(fb.header().whiteQueenCastling());
    assertTrue(fb.header().blackKingCastling());
    assertTrue(fb.header().blackQueenCastling());
    assertEquals(new Position(5, 3), fb.header().enPassant());
    assertEquals(0, fb.header().fiftyMoveRule());
    assertEquals(30, fb.header().playedMoves());
  }

  @Test
  public void parseWrongFENCastlingHeader() {
    URL filePath = classLoader.getResource("gameBoards/fenVersions/emptyFENCastling");
    FileBoard fb = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    assertFalse(fb.header().whiteKingCastling());
    assertFalse(fb.header().whiteQueenCastling());
    assertFalse(fb.header().blackKingCastling());
    assertFalse(fb.header().blackQueenCastling());
    assertEquals(new Position(5, 2), fb.header().enPassant());
    assertEquals(43, fb.header().fiftyMoveRule());
    assertEquals(70, fb.header().playedMoves());
  }

  @Test
  public void parseWrongFENEnPassantHeader() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/fenVersions/wrongFENEnPassant");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    verify(mockRuntime).exit(1);
  }

  @Test
  public void parseWrongFENEnPassantOutOfSquaresHeader() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/fenVersions/wrongFENEnPassantOutOfSquares");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    verify(mockRuntime).exit(1);
  }

  @Test
  public void parseOneMoveFromFiftyMoveRule() {
    URL filePath = classLoader.getResource("gameBoards/fenVersions/oneMoveFromFifty");
    FileBoard fb = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    assertTrue(fb.header().whiteKingCastling());
    assertTrue(fb.header().whiteQueenCastling());
    assertTrue(fb.header().blackKingCastling());
    assertTrue(fb.header().blackQueenCastling());
    assertNull(fb.header().enPassant());
    assertEquals(99, fb.header().fiftyMoveRule());
    assertEquals(140, fb.header().playedMoves());

    Game game = Game.initialize(false, false, null, null, null, fb, new HashMap<>());

    // Checking params are given to the game
    assertNull(game.getBoard().getEnPassantPos());
    assertEquals(49, game.getBoard().getNbMovesWithNoCaptureOrPawn());
    assertEquals(140, game.getGameState().getFullTurn());

    assertFalse(Game.getInstance().isOver());
    game.playMove(
        new Move(
            new Position(3, 0),
            new Position(5, 2),
            new ColoredPiece(Piece.QUEEN, Color.WHITE),
            false)); // Play a move to force 50 move rule
    assertTrue(Game.getInstance().isOver());
  }

  @Test
  public void parseNotLinearHistory() {
    URL filePath = classLoader.getResource("gameBoards/fenVersions/notLinearHistory");
    FileBoard fb = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    assertTrue(fb.header().whiteKingCastling());
    assertTrue(fb.header().whiteQueenCastling());
    assertTrue(fb.header().blackKingCastling());
    assertTrue(fb.header().blackQueenCastling());
    assertEquals(new Position(0, 2), fb.header().enPassant());
    assertEquals(0, fb.header().fiftyMoveRule());
    assertEquals(141, fb.header().playedMoves());

    Game game = Game.initialize(false, false, null, null, null, fb, new HashMap<>());

    // Checking params are given to the game
    assertEquals(0, game.getBoard().getNbMovesWithNoCaptureOrPawn());
    assertEquals(141, game.getGameState().getFullTurn());
    assertEquals(new Position(0, 2), game.getBoard().getEnPassantPos());
    assertFalse(Game.getInstance().isOver());
  }
}
