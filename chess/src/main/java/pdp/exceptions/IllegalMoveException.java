package pdp.exceptions;

import pdp.utils.TextGetter;

public class IllegalMoveException extends RuntimeException {
  public IllegalMoveException(String move) {
    super(TextGetter.getText("illegalMove", move));
  }
}
