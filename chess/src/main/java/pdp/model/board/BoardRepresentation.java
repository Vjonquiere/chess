package pdp.model.board;

import java.util.List;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

public interface BoardRepresentation {
  public List<Position> getPawns(boolean white);

  public List<Position> getRooks(boolean white);

  public List<Position> getBishops(boolean white);

  public List<Position> getKnights(boolean white);

  public List<Position> getQueens(boolean white);

  public List<Position> getKing(boolean white);

  public ColoredPiece getPieceAt(int x, int y);

  public int getNbCols();

  public int getNbRows();

  public void movePiece(Position from, Position to);

  public void deletePieceAt(int x, int y);

  public boolean queensOffTheBoard();

  public boolean areKingsActive();

  public boolean pawnsHaveProgressed(boolean isWhite);

  public int nbPiecesRemaining();

  public List<Move> retrieveKingMoves(boolean white);

  public List<Move> retrieveBishopMoves(boolean white);

  public List<List<Position>> retrieveWhitePiecesPos();

  public List<List<Position>> retrieveBlackPiecesPos();

  public List<List<Position>> retrieveInitialWhitePiecesPos();

  public List<List<Position>> retrieveInitialBlackPiecesPos();

  public BoardRepresentation getCopy();

  public List<Move> getAvailableMoves(int x, int y, boolean kingReachable);

  public List<Move> getAllAvailableMoves(boolean isWhite);

  public boolean isAttacked(int x, int y, Color by);

  public boolean isCheck(Color color);

  public boolean isCheckAfterMove(Color color, Move move);

  public boolean isCheckMate(Color color);

  public boolean isStaleMate(Color color, Color colorTurnToPlay);

  public boolean isDrawByInsufficientMaterial();

  public boolean isPawnPromoting(int x, int y, boolean white);

  public void promotePawn(int x, int y, boolean white, Piece newPiece);

  public boolean isDoublePushPossible(Move move, boolean white);

  public boolean isEnPassant(int x, int y, Move move, boolean white);

  public boolean hasEnoughMaterialToMate(boolean white);

  public void applyShortCastle(Color color);

  public void applyLongCastle(Color color);

  public boolean canCastle(
      Color color,
      boolean shortCastle,
      boolean whiteShortCastle,
      boolean whiteLongCastle,
      boolean blackShortCastle,
      boolean blackLongCastle);

  public boolean isCastleMove(ColoredPiece coloredPiece, Position source, Position dest);

  public boolean isEndGamePhase(int fullTurn, boolean white);

  public boolean validatePieceOwnership(boolean white, Position sourcePosition);

  public List<Move> getSpecialMoves(
      boolean white,
      Position enPassantPos,
      boolean isLastMoveDoublePush,
      boolean isWhiteLongCastle,
      boolean isWhiteShortCastle,
      boolean isBlackLongCastle,
      boolean isBlackShortCastle);
}
