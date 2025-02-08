package pdp.exceptions;

import pdp.utils.TextGetter;

public class InvalidPositionException extends RuntimeException {
  public InvalidPositionException(String pos) {
    super(TextGetter.getText("invalidPosition", pos));
  }
}
