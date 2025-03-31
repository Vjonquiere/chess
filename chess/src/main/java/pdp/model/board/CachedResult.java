package pdp.model.board;

import pdp.model.piece.Color;

/** Structure of the elements present in bitboard cache. Avoid recalculating expensive methods. */
public class CachedResult {

  /** Boolean to indicate whether the white player is in check. */
  private Boolean isCheckWhite = null;

  /** Boolean to indicate whether the white player is checkmate. */
  private Boolean isCheckMateWhite = null;

  /** Boolean to indicate whether the black player is in check. */
  private Boolean isCheckBlack = null;

  /** Boolean to indicate whether the black player is checkmate. */
  private Boolean isCheckMateBlack = null;

  /** Boolean to indicate whether the white player is stalemate. */
  private Boolean isStalemateWhite = null;

  /** Boolean to indicate whether the black player is stalemate. */
  private Boolean isStalemateBlack = null;

  /** Attack bitboard of the white player. */
  private Long whiteAttackBitboard = null;

  /** Attack bitboard of the black player. */
  private Long blackAttackBitboard = null;

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
   * Saves the given bitboard into the attack bitboard field corresponding to the colo given in
   * parameters.
   *
   * @param white true if the player is white, false otherwise.
   * @param bitboard bitboard to save in whiteAttackBitboards/blackAttackBitboards
   */
  public void setAttackBitboard(boolean white, Bitboard bitboard) {
    if (white) {
      this.whiteAttackBitboard = bitboard.getBits();
    } else {
      this.blackAttackBitboard = bitboard.getBits();
    }
  }

  /**
   * Retrieves the attack bitboard of the side corresponding to the parameter white.
   *
   * @param white true if the player is white, false otherwise.
   * @return attack bitboard of the side given in arguments
   */
  public Long getAttackBitboard(boolean white) {
    if (white) {
      return this.whiteAttackBitboard;
    } else {
      return this.blackAttackBitboard;
    }
  }
}
