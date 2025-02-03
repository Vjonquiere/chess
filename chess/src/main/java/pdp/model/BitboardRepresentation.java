package pdp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pdp.utils.Position;

import static java.util.Map.entry;

public class BitboardRepresentation implements BoardRepresentation {
  private Bitboard[] board;
  private int nbCols = 8;
  private int nbRows = 8;
  Map<Integer, Piece> pieces = Map.ofEntries(
          entry(0, Piece.KING),
          entry(1, Piece.QUEEN),
          entry(2, Piece.BISHOP),
          entry(3, Piece.ROOK),
          entry(4, Piece.KNIGHT),
          entry(5, Piece.PAWN),
          entry(6, Piece.KING),
          entry(7, Piece.QUEEN),
          entry(8, Piece.BISHOP),
          entry(9, Piece.ROOK),
          entry(10, Piece.KNIGHT),
          entry(11, Piece.PAWN)
  );

  /*
  BitBoards order:
     0. White king
     1. White queen
     2. White bishops
     3. White rooks
     4. White knights
     5. White pawns
     6. Black king
     7. Black queen
     8. Black bishops
     9. Black rooks
     10. Black knights
     11. Black pawns
   */

  public BitboardRepresentation() {
    board = new Bitboard[12];
    board[0] = new Bitboard(16L); // WKi
    board[1] = new Bitboard(8L); // WQ
    board[2] = new Bitboard(36L); // WB
    board[3] = new Bitboard(129L); // WR
    board[4] = new Bitboard(66L); // WKn
    board[5] = new Bitboard(65280L); // WP
    board[6] = new Bitboard(1152921504606846976L); // BKi
    board[7] = new Bitboard(576460752303423488L); // BQ
    board[8] = new Bitboard(2594073385365405696L); // BB
    board[9] = new Bitboard(); // BR // TODO: Find why overflow ???
    board[9].setBit(56);
    board[9].setBit(63);
    board[10] = new Bitboard(4755801206503243776L); // BKi
    board[11] = new Bitboard(71776119061217280L);
  }

  private List<Position> squaresToPosition(List<Integer> squares) {
    List<Position> positions = new ArrayList<>();
    for (Integer i : squares) {
      System.out.println("x = " + i%8 + " y = " + i/8);
      positions.add(new Position( i/8, i%8));
    }
    return positions;
  }

  private List<Position> getOccupiedSquares(int bitBoardIndex) {
    return squaresToPosition(board[bitBoardIndex].getSetBits());
  }

  @Override
  public List<Position> getPawns(boolean white) {
    int bitmapIndex = white ? 5 : 11;
    return getOccupiedSquares(bitmapIndex);
  }

  @Override
  public List<Position> getRooks(boolean white) {
    int bitmapIndex = white ? 3 : 9;
    return getOccupiedSquares(bitmapIndex);
  }

  @Override
  public List<Position> getBishops(boolean white) {
    int bitmapIndex = white ? 2 : 8;
    return getOccupiedSquares(bitmapIndex);
  }

  @Override
  public List<Position> getKnights(boolean white) {
    int bitmapIndex = white ? 4 : 10;
    return getOccupiedSquares(bitmapIndex);
  }

  @Override
  public List<Position> getQueens(boolean white) {
    int bitmapIndex = white ? 1 : 7;
    return getOccupiedSquares(bitmapIndex);
  }

  @Override
  public Position getKing(boolean white) {
    int bitmapIndex = white ? 0 : 6;
    return getOccupiedSquares(bitmapIndex).getFirst();
  }

  @Override
  public Piece getPieceAt(int x, int y) {
    int square = x + 8 * y;
    for (int index = 0; index < board.length; index++) {
      if (board[index].getBit(square)) return pieces.get(index);
    }
    return Piece.EMPTY;
  }

  public int getNbCols() {
    return nbCols;
  }

  public int getNbRows() {
    return nbRows;
  }

  @Override
  public List<Move> getAvailableMoves(int x, int y, Board board) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  @Override
  public boolean isAttacked(int x, int y, Board board) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  @Override
  public boolean isCheck(Board board) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  @Override
  public boolean isCheckMate(Board board) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }
}
