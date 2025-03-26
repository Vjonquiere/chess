package pdp.exceptions;

import pdp.utils.TextGetter;

/** Exception thrown when it wasn't possible to play the given move. */
public class IllegalMoveException extends RuntimeException {
  public IllegalMoveException(String move) {
    super(TextGetter.getText("illegalMove", move));
  }
}
