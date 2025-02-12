package pdp.exceptions;

import pdp.utils.TextGetter;

public class MoveParsingException extends RuntimeException {
  public MoveParsingException(String move) {
    super(TextGetter.getText("invalidFormat", move));
  }
}
