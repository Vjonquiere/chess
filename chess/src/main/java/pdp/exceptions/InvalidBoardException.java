package pdp.exceptions;

import pdp.utils.TextGetter;

/** Exception thrown when the board representation used is not bitboard. */
public class InvalidBoardException extends RuntimeException {
  public InvalidBoardException() {
    super(TextGetter.getText("onlyBitboards"));
  }
}
