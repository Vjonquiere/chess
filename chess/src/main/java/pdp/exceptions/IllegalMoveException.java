package pdp.exceptions;

import pdp.utils.TextGetter;

/** Exception thrown when it wasn't possible to play the given move. */
public class IllegalMoveException extends RuntimeException {

  /** Creates the custom exception. */
  public IllegalMoveException(final String move) {
    super(TextGetter.getText("illegalMove", move));
  }
}
