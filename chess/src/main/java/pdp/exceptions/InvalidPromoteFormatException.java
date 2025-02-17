package pdp.exceptions;

import pdp.utils.TextGetter;

public class InvalidPromoteFormatException extends RuntimeException {
  public InvalidPromoteFormatException() {
    super(TextGetter.getText("invalidPromoteFormat", "e7-e8=Q"));
  }
}
