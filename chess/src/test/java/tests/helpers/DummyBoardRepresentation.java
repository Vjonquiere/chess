package tests.helpers;

import java.util.ArrayList;
import java.util.List;
import pdp.model.board.BoardRepresentation;
import pdp.model.board.Move;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

public class DummyBoardRepresentation implements BoardRepresentation {

  @Override
  public List<Position> getPawns(boolean white) {
    return List.of();
  }

  @Override
  public List<Position> getRooks(boolean white) {
    return List.of();
  }

  @Override
  public List<Position> getBishops(boolean white) {
    return List.of();
  }

  @Override
  public List<Position> getKnights(boolean white) {
    return List.of();
  }

  @Override
  public List<Position> getQueens(boolean white) {
    return List.of();
  }

  @Override
  public List<Position> getKing(boolean white) {
    return List.of();
  }

  @Override
  public ColoredPiece getPieceAt(int x, int y) {
    return new ColoredPiece(Piece.EMPTY, Color.EMPTY);
  }

  @Override
  public int getNbCols() {
    return 0;
  }

  @Override
  public int getNbRows() {
    return 0;
  }

  @Override
  public void movePiece(Position from, Position to) {}

  @Override
  public void deletePieceAt(int x, int y) {}

  @Override
  public List<Move> getAvailableMoves(int x, int y, boolean kingReachable) {
    return List.of();
  }

  @Override
  public List<Move> getAllAvailableMoves(boolean isWhite) {
    return List.of();
  }

  @Override
  public boolean isAttacked(int x, int y, Color by) {
    return false;
  }

  @Override
  public boolean isCheck(Color color) {
    return false;
  }

  @Override
  public boolean isCheckAfterMove(Color color, Move move) {
    return false;
  }

  @Override
  public boolean isCheckMate(Color color) {
    return false;
  }

  @Override
  public boolean isStaleMate(Color color, Color colorTurnToPlay) {
    return false;
  }

  @Override
  public boolean isDrawByInsufficientMaterial() {
    return false;
  }

  @Override
  public boolean isPawnPromoting(int x, int y, boolean white) {
    return false;
  }

  @Override
  public void promotePawn(int x, int y, boolean white, Piece newPiece) {}

  @Override
  public boolean isDoublePushPossible(Move move, boolean white) {
    return false;
  }

  @Override
  public boolean isEnPassant(int x, int y, Move move, boolean white) {
    return false;
  }

  @Override
  public boolean hasEnoughMaterialToMate(boolean white) {
    return false;
  }

  @Override
  public void applyShortCastle(Color color) {}

  @Override
  public void applyLongCastle(Color color) {}

  @Override
  public boolean canCastle(Color color, boolean shortCastle) {
    return false;
  }

  @Override
  public boolean isCastleMove(ColoredPiece coloredPiece, Position source, Position dest) {
    return false;
  }

  @Override
  public boolean isEndGamePhase(int fullTurn, boolean white) {
    return false;
  }

  @Override
  public boolean validatePieceOwnership(boolean white, Position sourcePosition) {
    return false;
  }

  @Override
  public boolean areKingsActive() {
    return false;
  }

  @Override
  public boolean pawnsHaveProgressed(boolean isWhite) {
    return false;
  }

  @Override
  public int nbPiecesRemaining() {
    return 32;
  }

  @Override
  public boolean queensOffTheBoard() {
    return false;
  }

  @Override
  public List<Move> retrieveKingMoves(boolean white) {
    return new ArrayList<>();
  }

  @Override
  public List<Move> retrieveBishopMoves(boolean white) {
    return new ArrayList<>();
  }

  @Override
  public List<List<Position>> retrieveWhitePiecesPos() {
    return new ArrayList<>();
  }

  @Override
  public List<List<Position>> retrieveBlackPiecesPos() {
    return new ArrayList<>();
  }

  @Override
  public List<List<Position>> retrieveInitialWhitePiecesPos() {
    return new ArrayList<>();
  }

  @Override
  public List<List<Position>> retrieveInitialBlackPiecesPos() {
    return new ArrayList<>();
  }

  @Override
  public BoardRepresentation getCopy() {
    return new DummyBoardRepresentation();
  }

  @Override
  public boolean isPromotionMove(int xSource, int ySource, int xDest, int yDest, boolean isWhite) {
    return false;
  }

  @Override
  public boolean getPlayer() {
    return true;
  }

  @Override
  public void setPlayer(boolean isWhite) {}

  @Override
  public Position getEnPassantPos() {
    return null;
  }

  @Override
  public void setEnPassantPos(Position enPassantPos) {}

  @Override
  public boolean isLastMoveDoublePush() {
    return false;
  }

  @Override
  public void setLastMoveDoublePush(boolean lastMoveDoublePush) {}

  @Override
  public boolean isWhiteShortCastle() {
    return false;
  }

  @Override
  public void setWhiteShortCastle(boolean whiteShortCastle) {}

  @Override
  public boolean isBlackShortCastle() {
    return false;
  }

  @Override
  public void setBlackShortCastle(boolean blackShortCastle) {}

  @Override
  public boolean isWhiteLongCastle() {
    return false;
  }

  @Override
  public void setWhiteLongCastle(boolean whiteLongCastle) {}

  @Override
  public boolean isBlackLongCastle() {
    return false;
  }

  @Override
  public void setBlackLongCastle(boolean blackLongCastle) {}

  @Override
  public boolean isEnPassantTake() {
    return false;
  }

  @Override
  public void setEnPassantTake(boolean enPassantTake) {}

  @Override
  public int getNbMovesWithNoCaptureOrPawn() {
    return 0;
  }

  @Override
  public void setNbMovesWithNoCaptureOrPawn(int newVal) {}

  @Override
  public List<Move> getAvailableMoves(Position pos) {
    return new ArrayList<>();
  }

  @Override
  public int getNbFullMovesWithNoCaptureOrPawn() {
    return 0;
  }

  @Override
  public void makeMove(Move move) {}

  @Override
  public char[][] getAsciiRepresentation() {
    return new char[2][2];
  }

  @Override
  public void applyCastle(Color color, boolean shortCastle) {}

  @Override
  public boolean[] getCastlingRights() {
    return new boolean[4];
  }
}
