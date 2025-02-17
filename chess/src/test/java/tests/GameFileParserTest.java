package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.net.URL;
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
    Game game = Game.initialize(false, false, null, null, board);
    assertEquals(game.getGameState().isWhiteTurn(), board.isWhiteTurn());
    assertEquals(game.getBoard().board, board.board());
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
    mockRuntime.exit(1);
  }

  @Test
  public void parseWrongBoardFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/wrongBoard");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    mockRuntime.exit(1);
  }

  @Test
  public void parseUnknownFile() {
    Runtime mockRuntime = mock(Runtime.class);
    parser.parseGameFile("Unknow/file/path", mockRuntime);
    mockRuntime.exit(1);
  }

  @Test
  public void parseWrongPlayerFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/unknownPlayerGame");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    mockRuntime.exit(1);
  }

  @Test
  public void parseWrongFormattedFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/wrongFormattedGame");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    mockRuntime.exit(1);
  }

  @Test
  public void parseUnknownPieceFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/unknownPieceGame");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    mockRuntime.exit(1);
  }

  @Test
  public void parseNoWhiteKingFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/noWhiteKing");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    mockRuntime.exit(1);
  }

  @Test
  public void parseNoBlackKingFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/noBlackKing");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    mockRuntime.exit(1);
  }

  @Test
  public void parseBlackCheckMateFile() {
    Runtime mockRuntime = mock(Runtime.class);
    URL filePath = classLoader.getResource("gameBoards/scholarMate");
    parser.parseGameFile(filePath.getPath(), mockRuntime);
    mockRuntime.exit(1);
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
    Game game = Game.initialize(false, false, null, null);
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
    assertEquals(game.getBoard().board, board.board());
    assertEquals(game.getGameState().isWhiteTurn(), board.isWhiteTurn());
    assertFalse(board.isWhiteTurn());
  }

  @Test
  public void parseTestWithHistory() {
    /*Verify that parsing a game with history or do not change output*/
    URL filePath = classLoader.getResource("gameBoards/gameExample1WithHistory");
    FileBoard board = parser.parseGameFile(filePath.getPath(), Runtime.getRuntime());
    Game game = Game.initialize(false, false, null, null);
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
    assertEquals(game.getBoard().board, board.board());
    assertEquals(game.getGameState().isWhiteTurn(), board.isWhiteTurn());
    assertFalse(board.isWhiteTurn());
  }
}
