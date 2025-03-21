package pdp.exceptions;

import pdp.utils.TextGetter;

/** Exception thrown when the format of the pawn promotion is incorrect. */
public class InvalidPromoteFormatException extends RuntimeException {
  public InvalidPromoteFormatException() {
    super(TextGetter.getText("invalidPromoteFormat", "e7-e8=Q"));
  }
}
