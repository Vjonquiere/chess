package pdp.model.board;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;

/** Structure of the elements present in bitboard cache. Avoid recalculating expensive methods. */
public class CachedResult {
  private Boolean isCheckWhite = null;
  private Boolean isCheckMateWhite = null;
  private Boolean isCheckBlack = null;
  private Boolean isCheckMateBlack = null;
  private Boolean isStalemateWhite = null;
  private Boolean isStalemateBlack = null;
  private Long whiteMoveBitboard = null;
  private Long blackMoveBitboard = null;
  private Bitboard whiteAttackBitboards = null;
  private Bitboard blackAttackBitboards = null;
  private final AbstractMap<Integer, Long> blackAttackBitboard = new ConcurrentHashMap<>();
  private final AbstractMap<Integer, ColoredPiece> pieces = new ConcurrentHashMap<>();
  private final AbstractMap<Integer, Boolean> isAttackedByWhite = new ConcurrentHashMap<>();
  private final AbstractMap<Integer, Boolean> isAttackedByBlack = new ConcurrentHashMap<>();

  /**
   * Checks whether the king is in check in this cache instance.
   *
   * @param color Color of the king to check
   * @return true if the king is check, false otherwise
   */
  public Boolean isCheck(Color color) {
    return color == Color.WHITE ? isCheckWhite : isCheckBlack;
  }

  /**
   * Sets the field representing the check status of the king given in parameters.
   *
   * @param check true if the king is check, false otherwise
   * @param color Color of the king to set
   */
  public void setCheck(boolean check, Color color) {
    if (color == Color.WHITE) {
      isCheckWhite = check;
    } else {
      isCheckBlack = check;
    }
  }

  /**
   * Checks whether the king is checkmate in this cache instance.
   *
   * @param color Color of the king
   * @return true if the king is checkmate, false otherwise
   */
  public Boolean isCheckMate(Color color) {
    return color == Color.WHITE ? isCheckMateWhite : isCheckMateBlack;
  }

  /**
   * Checks whether the king is stalemate in this cache instance.
   *
   * @param color Color of the king
   * @return true if the king is checkmate, false otherwise
   */
  public Boolean isStaleMate(Color color) {
    return color == Color.WHITE ? isStalemateWhite : isStalemateBlack;
  }

  /**
   * Sets the field representing the staleMate status of the king given in parameters.
   *
   * @param staleMate true if the king is stalemate, false otherwise
   * @param color Color of the king to set
   */
  public void setStaleMate(boolean staleMate, Color color) {
    if (color == Color.WHITE) {
      isStalemateWhite = staleMate;
    } else {
      isStalemateBlack = staleMate;
    }
  }

  /**
   * Sets the field representing the checkmate status of the king given in parameters.
   *
   * @param checkMate true if the king is checkmate, false otherwise
   * @param color Color of the king to set
   */
  public void setCheckMate(boolean checkMate, Color color) {
    if (color == Color.WHITE) {
      isCheckMateWhite = checkMate;
    } else {
      isCheckMateBlack = checkMate;
    }
  }

  /**
   * Retrieves the move bitboard of the given color.
   *
   * @param isWhite true if we want the withe bitboard, false otherwise.
   * @return move bitboard of the given color
   */
  public Long getColorMoveBitboard(boolean isWhite) {
    return isWhite ? this.whiteMoveBitboard : this.blackMoveBitboard;
  }

  /**
   * Sets the move bitboard of the given color to the one given in parameters.
   *
   * @param bitboard bitboard to set in the cache
   * @param isWhite true if we want the withe bitboard, false otherwise.
   */
  public void setColorMoveBitboard(long bitboard, boolean isWhite) {
    if (isWhite) {
      this.whiteMoveBitboard = bitboard;
    } else {
      this.blackMoveBitboard = bitboard;
    }
  }

  /**
   * Retrieves the piece at the given position.
   *
   * @param x x coordinates of the square
   * @param y y coordinates of the square
   * @return piece at x,y
   */
  public ColoredPiece getPieceAt(int x, int y) {
    return pieces.get(x * 8 + y);
  }

  /**
   * Sets in the cache the given piece at the given position.
   *
   * @param x x coordinates of the square
   * @param y y coordinates of the square
   * @param piece piece at the given position
   */
  public void setPieceAt(int x, int y, ColoredPiece piece) {
    pieces.put(x * 8 + y, piece);
  }

  /**
   * Checks whether the given position is attacked in this cache instance.
   *
   * @param x x coordinates of the square to check
   * @param y y coordinates of the square to check
   * @param by Color of the attacking side
   * @return true if the square is attacked, false otherwise
   */
  public Boolean isAttacked(int x, int y, Color by) {
    return by == Color.WHITE ? isAttackedByWhite.get(x * 8 + y) : isAttackedByBlack.get(x * 8 + y);
  }

  /**
   * Sets the attack status of the square of the given position to isAttacked.
   *
   * @param x x coordinates of the square to check
   * @param y y coordinates of the square to check
   * @param by Color of the attacking side
   * @param isAttacked true if the square is attacked, false otherwise
   */
  public void setAttacked(int x, int y, Color by, boolean isAttacked) {
    if (by == Color.WHITE) {
      isAttackedByWhite.put(x * 8 + y, isAttacked);
    } else {
      isAttackedByBlack.put(x * 8 + y, isAttacked);
    }
  }

  public void setAttackBitboard(boolean white, Bitboard bitboard) {
    if (white) {
      whiteAttackBitboards = bitboard;
    } else {
      blackAttackBitboards = bitboard;
    }
  }

  public Bitboard getAttackBitboard(boolean white) {
    if (white) {
      return whiteAttackBitboards;
    } else {
      return blackAttackBitboards;
    }
  }
}
