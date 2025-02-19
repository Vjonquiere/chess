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
    return null;
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
  public BoardRepresentation getCopy() {
    return new DummyBoardRepresentation();
  }
}
