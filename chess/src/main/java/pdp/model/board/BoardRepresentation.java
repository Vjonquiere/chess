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

  boolean areKingsActive();

  boolean pawnsHaveProgressed(boolean isWhite);

  int nbPiecesRemaining();

  List<Move> retrieveKingMoves(boolean white);

  List<Move> retrieveBishopMoves(boolean white);

  List<List<Position>> retrieveWhitePiecesPos();

  List<List<Position>> retrieveBlackPiecesPos();

  List<List<Position>> retrieveInitialWhitePiecesPos();

  List<List<Position>> retrieveInitialBlackPiecesPos();

  BoardRepresentation getCopy();

  List<Move> getAvailableMoves(Position pos);

  List<Move> getAvailableMoves(int x, int y, boolean kingReachable);

  List<Move> getAllAvailableMoves(boolean isWhite);

  boolean isAttacked(int x, int y, Color by);

  boolean isCheck(Color color);

  boolean isCheckAfterMove(Color color, Move move);

  boolean isCheckMate(Color color);

  boolean isStaleMate(Color color, Color colorTurnToPlay);

  boolean isDrawByInsufficientMaterial();

  boolean isPawnPromoting(int x, int y, boolean white);

  boolean isPromotionMove(int xSource, int ySource, int xDest, int yDest, boolean isWhite);

  void promotePawn(int x, int y, boolean white, Piece newPiece);

  boolean isDoublePushPossible(Move move, boolean white);

  boolean isEnPassant(int x, int y, Move move, boolean white);

  boolean hasEnoughMaterialToMate(boolean white);

  void applyShortCastle(Color color);

  void applyLongCastle(Color color);

  boolean canCastle(Color color, boolean shortCastle);

  boolean isCastleMove(ColoredPiece coloredPiece, Position source, Position dest);

  boolean isEndGamePhase(int fullTurn, boolean white);

  boolean validatePieceOwnership(boolean white, Position sourcePosition);

  boolean getPlayer();

  void setPlayer(boolean isWhite);

  Position getEnPassantPos();

  void setEnPassantPos(Position enPassantPos);

  boolean isLastMoveDoublePush();

  void setLastMoveDoublePush(boolean lastMoveDoublePush);

  boolean isWhiteShortCastle();

  void setWhiteShortCastle(boolean whiteShortCastle);

  boolean isBlackShortCastle();

  void setBlackShortCastle(boolean blackShortCastle);

  boolean isWhiteLongCastle();

  void setWhiteLongCastle(boolean whiteLongCastle);

  boolean isBlackLongCastle();

  void setBlackLongCastle(boolean blackLongCastle);

  boolean isEnPassantTake();

  void setEnPassantTake(boolean enPassantTake);

  int getNbMovesWithNoCaptureOrPawn();

  int getNbFullMovesWithNoCaptureOrPawn();

  void setNbMovesWithNoCaptureOrPawn(int newVal);

  void makeMove(Move move);

  char[][] getAsciiRepresentation();

  void applyCastle(Color color, boolean shortCastle);

  boolean[] getCastlingRights();
}
