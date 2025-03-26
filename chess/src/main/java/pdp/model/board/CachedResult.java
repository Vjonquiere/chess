package pdp.model.board;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;

public class CachedResult {
  private Boolean isCheckWhite = null;
  private Boolean isCheckMateWhite = null;
  private Boolean isCheckBlack = null;
  private Boolean isCheckMateBlack = null;
  private Boolean isStalemateWhite = null;
  private Boolean isStalemateBlack = null;
  private Long whiteMoveBitboard = null;
  private Long blackMoveBitboard = null;
  private final AbstractMap<Integer, ColoredPiece> pieces = new ConcurrentHashMap<>();
  private final AbstractMap<Integer, Boolean> isAttackedByWhite = new ConcurrentHashMap<>();
  private final AbstractMap<Integer, Boolean> isAttackedByBlack = new ConcurrentHashMap<>();

  public Boolean isCheck(Color color) {
    return color == Color.WHITE ? isCheckWhite : isCheckBlack;
  }

  public void setCheck(boolean check, Color color) {
    if (color == Color.WHITE) {
      isCheckWhite = check;
    } else {
      isCheckBlack = check;
    }
  }

  public Boolean isCheckMate(Color color) {
    return color == Color.WHITE ? isCheckMateWhite : isCheckMateBlack;
  }

  public Boolean isStaleMate(Color color) {
    return color == Color.WHITE ? isStalemateWhite : isStalemateBlack;
  }

  public void setStaleMate(boolean staleMate, Color color) {
    if (color == Color.WHITE) {
      isStalemateWhite = staleMate;
    } else {
      isStalemateBlack = staleMate;
    }
  }

  public void setCheckMate(boolean checkMate, Color color) {
    if (color == Color.WHITE) {
      isCheckMateWhite = checkMate;
    } else {
      isCheckMateBlack = checkMate;
    }
  }

  public Long getColorMoveBitboard(boolean isWhite) {
    return isWhite ? this.whiteMoveBitboard : this.blackMoveBitboard;
  }

  public void setColorMoveBitboard(long bitboard, boolean isWhite) {
    if (isWhite) {
      this.whiteMoveBitboard = bitboard;
    } else {
      this.blackMoveBitboard = bitboard;
    }
  }

  public ColoredPiece getPieceAt(int x, int y) {
    return pieces.get(x * 8 + y);
  }

  public void setPieceAt(int x, int y, ColoredPiece piece) {
    pieces.put(x * 8 + y, piece);
  }

  public Boolean isAttacked(int x, int y, Color by) {
    return by == Color.WHITE ? isAttackedByWhite.get(x * 8 + y) : isAttackedByBlack.get(x * 8 + y);
  }

  public void setAttacked(int x, int y, Color by, boolean isAttacked) {
    if (by == Color.WHITE) {
      isAttackedByWhite.put(x * 8 + y, isAttacked);
    } else {
      isAttackedByBlack.put(x * 8 + y, isAttacked);
    }
  }
}
