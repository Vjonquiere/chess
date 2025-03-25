package pdp.exceptions;

import pdp.utils.TextGetter;

/** Exception thrown when it wasn't possible to get the given position. */
public class InvalidPositionException extends RuntimeException {
  public InvalidPositionException(String pos) {
    super(TextGetter.getText("invalidPosition", pos));
  }
}
