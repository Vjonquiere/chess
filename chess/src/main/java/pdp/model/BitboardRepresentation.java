package pdp.model;

import static java.util.Map.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import pdp.utils.Logging;
import pdp.utils.Position;

public class BitboardRepresentation implements BoardRepresentation {
  private static final Logger LOGGER = Logger.getLogger(BitboardRepresentation.class.getName());
  private Bitboard[] board;
  private int nbCols = 8;
  private int nbRows = 8;
  Map<Integer, ColoredPiece<Piece, Color>> pieces =
      Map.ofEntries(
          entry(0, new ColoredPiece<Piece, Color>(Piece.KING, Color.WHITE)),
          entry(1, new ColoredPiece<Piece, Color>(Piece.QUEEN, Color.WHITE)),
          entry(2, new ColoredPiece<Piece, Color>(Piece.BISHOP, Color.WHITE)),
          entry(3, new ColoredPiece<Piece, Color>(Piece.ROOK, Color.WHITE)),
          entry(4, new ColoredPiece<Piece, Color>(Piece.KNIGHT, Color.WHITE)),
          entry(5, new ColoredPiece<Piece, Color>(Piece.PAWN, Color.WHITE)),
          entry(6, new ColoredPiece<Piece, Color>(Piece.KING, Color.BLACK)),
          entry(7, new ColoredPiece<Piece, Color>(Piece.QUEEN, Color.BLACK)),
          entry(8, new ColoredPiece<Piece, Color>(Piece.BISHOP, Color.BLACK)),
          entry(9, new ColoredPiece<Piece, Color>(Piece.ROOK, Color.BLACK)),
          entry(10, new ColoredPiece<Piece, Color>(Piece.KNIGHT, Color.BLACK)),
          entry(11, new ColoredPiece<Piece, Color>(Piece.PAWN, Color.BLACK)));

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
    Logging.configureLogging(LOGGER);
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
      positions.add(new Position(i / 8, i % 8));
    }
    return positions;
  }

  private Bitboard getWhiteBoard() {
    return board[0].or(board[1]).or(board[2]).or(board[3]).or(board[4]).or(board[5]);
  }

  private Bitboard getBlackBoard() {
    return board[6].or(board[7]).or(board[8]).or(board[9]).or(board[10]).or(board[11]);
  }

  private List<Position> getOccupiedSquares(int bitBoardIndex) {
    return squaresToPosition(board[bitBoardIndex].getSetBits());
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
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
  public List<Position> getKing(boolean white) {
    int bitmapIndex = white ? 0 : 6;
    return getOccupiedSquares(bitmapIndex);
  }

  @Override
  public ColoredPiece<Piece, Color> getPieceAt(int x, int y) {
    int square = x + 8 * y;
    for (int index = 0; index < board.length; index++) {
      if (board[index].getBit(square)) return pieces.get(index);
    }
    return new ColoredPiece<>(Piece.EMPTY, Color.EMPTY);
  }

  public int getNbCols() {
    return nbCols;
  }

  public int getNbRows() {
    return nbRows;
  }

  private Bitboard getMultipleMovesFromDirection(
      Bitboard piece,
      Bitboard allies,
      Bitboard enemies,
      Function<Bitboard, Bitboard> moveFunction) {
    Bitboard allowedMoves = new Bitboard(piece.bitboard);
    int bitCount = enemies.bitCount();
    do {
      allowedMoves = allowedMoves.or(moveFunction.apply(allowedMoves));
      allowedMoves = allowedMoves.xor(allowedMoves.and(allies));
      bitCount++;
    } while (allowedMoves.or(enemies).bitCount() > bitCount);
    return allowedMoves;
  }

  private Bitboard getInlineMoves(Position square, Bitboard allies, Bitboard enemies) {
    Bitboard position = new Bitboard();
    int squareIndex = square.getX() % 8 + square.getY() * 8;
    position.setBit(squareIndex);
    // move left
    Bitboard leftMoves =
        getMultipleMovesFromDirection(position, allies, enemies, Bitboard::moveLeft);
    // move right
    Bitboard rightMoves =
        getMultipleMovesFromDirection(position, allies, enemies, Bitboard::moveRight);
    // move up
    Bitboard upMoves = getMultipleMovesFromDirection(position, allies, enemies, Bitboard::moveUp);
    // move down
    Bitboard downMoves =
        getMultipleMovesFromDirection(position, allies, enemies, Bitboard::moveDown);

    Bitboard res = new Bitboard().or(leftMoves).or(rightMoves).or(upMoves).or(downMoves);
    res.clearBit(squareIndex);
    return res;
  }

  private Bitboard getDiagonalMoves(Position square, Bitboard allies, Bitboard enemies) {
    Bitboard position = new Bitboard();
    int squareIndex = square.getX() % 8 + square.getY() * 8;
    position.setBit(squareIndex);

    Bitboard upLeftMoves =
        getMultipleMovesFromDirection(position, allies, enemies, Bitboard::moveUpLeft);
    Bitboard upRightMoves =
        getMultipleMovesFromDirection(position, allies, enemies, Bitboard::moveUpRight);
    Bitboard downLeftMoves =
        getMultipleMovesFromDirection(position, allies, enemies, Bitboard::moveDownLeft);
    Bitboard downRightMoves =
        getMultipleMovesFromDirection(position, allies, enemies, Bitboard::moveDownRight);
    Bitboard res =
        new Bitboard().or(upLeftMoves).or(upRightMoves).or(downRightMoves).or(downLeftMoves);
    res.clearBit(squareIndex);
    return res;
  }

  private List<Move> bitboardToMoves(Bitboard moveBitboard, Bitboard enemies) {
    List<Move> moves = new ArrayList<>();
    for (Integer i : moveBitboard.getSetBits()) {
      // moves.add(new Move()); // enemies.getBit(i) ? true : false -> capture ?
    }
    return moves;
  }

  private List<Move> getKingMoves(Position square, Bitboard allies, Bitboard enemies) {
    Bitboard position = new Bitboard();
    int squareIndex = square.getX() % 8 + square.getY() * 8;
    position.setBit(squareIndex);
    Bitboard move =
        position
            .moveLeft()
            .or(position.moveRight())
            .or(position.moveUp())
            .or(position.moveDown())
            .or(position.moveUpLeft())
            .or(position.moveUpRight())
            .or(position.moveDownLeft())
            .or(position.moveDownRight());
    move = move.xor(move.and(allies));
    return bitboardToMoves(move, enemies);
  }

  private List<Move> getKnightMoves(Position square, Bitboard allies, Bitboard enemies) {
    Bitboard position = new Bitboard();
    int squareIndex = square.getX() % 8 + square.getY() * 8;
    position.setBit(squareIndex);
    Bitboard move =
        position
            .moveUp()
            .moveUpRight()
            .or(position.moveUp().moveUpLeft())
            .or(position.moveUp().moveUpRight())
            .or(position.moveDown().moveDownLeft())
            .or(position.moveDown().moveDownRight())
            .or(position.moveLeft().moveUpLeft())
            .or(position.moveLeft().moveDownLeft())
            .or(position.moveRight().moveDownRight())
            .or(position.moveRight().moveUpRight());
    move = move.xor(move.and(allies));
    return bitboardToMoves(move, enemies);
  }

  private List<Move> getPawnMoves(
      Position square, Bitboard allies, Bitboard enemies, boolean white) {
    Bitboard position = new Bitboard();
    Bitboard attackRight, attackLeft;
    int squareIndex = square.getX() % 8 + square.getY() * 8;
    position.setBit(squareIndex);

    if (white) {
      attackRight = position.moveUpRight().and(enemies);
      attackLeft = position.moveUpLeft().and(enemies);
      position = position.moveUp();
    } else {
      attackRight = position.moveDownRight().and(enemies);
      attackLeft = position.moveDownLeft().and(enemies);
      position = position.moveDown();
    }
    position = position.xor(position.and(allies));

    return bitboardToMoves(position.or(attackRight).or(attackLeft), enemies);
  }

  private List<Move> getQueenMoves(Position square, Bitboard allies, Bitboard enemies) {
    return bitboardToMoves(
        getInlineMoves(square, allies, enemies).or(getDiagonalMoves(square, allies, enemies)),
        enemies);
  }

  private List<Move> getBishopMoves(Position square, Bitboard allies, Bitboard enemies) {
    return bitboardToMoves(getDiagonalMoves(square, allies, enemies), enemies);
  }

  private List<Move> getRookMoves(Position square, Bitboard allies, Bitboard enemies) {
    return bitboardToMoves(getInlineMoves(square, allies, enemies), enemies);
  }

  @Override
  public List<Move> getAvailableMoves(int x, int y, Board board) {
    ColoredPiece<Piece, Color> piece = getPieceAt(x, y);
    Bitboard allies = piece.getColor() == Color.WHITE ? getWhiteBoard() : getBlackBoard();
    Bitboard enemies = piece.getColor() == Color.WHITE ? getBlackBoard() : getWhiteBoard();
    return switch (piece.getPiece()) {
      case KING -> getKingMoves(new Position(y, x), allies, enemies);
      case QUEEN -> getQueenMoves(new Position(y, x), allies, enemies);
      case BISHOP -> getBishopMoves(new Position(y, x), allies, enemies);
      case ROOK -> getRookMoves(new Position(y, x), allies, enemies);
      case KNIGHT -> getKnightMoves(new Position(y, x), allies, enemies);
      case PAWN ->
          piece.getColor() == Color.WHITE
              ? getPawnMoves(new Position(y, x), allies, enemies, true)
              : getPawnMoves(new Position(y, x), allies, enemies, false);
      default -> new ArrayList<>();
    };
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
