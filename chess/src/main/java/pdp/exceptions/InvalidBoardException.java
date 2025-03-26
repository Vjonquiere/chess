package pdp.exceptions;

import pdp.utils.TextGetter;

/** Exception thrown when the board representation used is not bitboard. */
public class InvalidBoardException extends RuntimeException {

  /** Creates the custom exception. */
  public InvalidBoardException() {
    super(TextGetter.getText("onlyBitboards"));
  }
}
