package pdp.model.board;

import java.util.List;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

/** Interface containing all needed methods to implement a custom board representation. */
public interface BoardRepresentation {
  List<Position> getPawns(boolean white);

  List<Position> getRooks(boolean white);

  List<Position> getBishops(boolean white);

  List<Position> getKnights(boolean white);

  List<Position> getQueens(boolean white);

  List<Position> getKing(boolean white);

  ColoredPiece getPieceAt(int x, int y);

  int getNbCols();

  int getNbRows();

  void movePiece(Position from, Position to);

  void deletePieceAt(int x, int y);

  boolean queensOffTheBoard();

  boolean areKingsActive(
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle);

  boolean pawnsHaveProgressed(boolean isWhite);

  int nbPiecesRemaining();

  List<Move> retrieveKingMoves(
      boolean white,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle);

  List<Move> retrieveBishopMoves(boolean white);

  List<List<Position>> retrieveWhitePiecesPos();

  List<List<Position>> retrieveBlackPiecesPos();

  List<List<Position>> retrieveInitialWhitePiecesPos();

  List<List<Position>> retrieveInitialBlackPiecesPos();

  BoardRepresentation getCopy();

  List<Move> getAvailableMoves(
      int x,
      int y,
      boolean kingReachable,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle);

  List<Move> getAllAvailableMoves(
      boolean isWhite,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle);

  boolean isAttacked(
      int x,
      int y,
      Color by,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle);

  boolean isCheck(
      Color color,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle);

  boolean isCheckAfterMove(
      Color color,
      Move move,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle);

  boolean isCheckMate(
      Color color,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle);

  boolean isStaleMate(
      Color color,
      Color colorTurnToPlay,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle);

  boolean isDrawByInsufficientMaterial();

  boolean isPawnPromoting(int x, int y, boolean white);

  boolean isPromotionMove(int xSource, int ySource, int xDest, int yDest, boolean isWhite);

  void promotePawn(int x, int y, boolean white, Piece newPiece);

  boolean isDoublePushPossible(Move move, boolean white);

  boolean isEnPassant(int x, int y, Move move, boolean white);

  boolean hasEnoughMaterialToMate(boolean white);

  void applyShortCastle(Color color);

  void applyLongCastle(Color color);

  boolean canCastle(
      Color color,
      boolean shortCastle,
      boolean whiteShortCastle,
      boolean whiteLongCastle,
      boolean blackShortCastle,
      boolean blackLongCastle,
      Position enPassantPos,
      boolean isLastMoveDoublePush);

  boolean isCastleMove(ColoredPiece coloredPiece, Position source, Position dest);

  boolean isEndGamePhase(
      int fullTurn,
      boolean white,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle);

  boolean validatePieceOwnership(boolean white, Position sourcePosition);
}
