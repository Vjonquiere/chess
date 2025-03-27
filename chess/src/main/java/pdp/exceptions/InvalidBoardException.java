package pdp.exceptions;

import java.io.Serial;
import pdp.utils.TextGetter;

/** Exception thrown when the board representation used is not bitboard. */
public class InvalidBoardException extends RuntimeException {
  /** Define serialization id to avoid serialization related bugs */
  @Serial private static final long serialVersionUID = -4739727503913384901L;

  /** Creates the custom exception. */
  public InvalidBoardException() {
    super(TextGetter.getText("onlyBitboards"));
  }
}
