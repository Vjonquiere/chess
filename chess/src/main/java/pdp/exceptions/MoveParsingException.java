package pdp.exceptions;

import pdp.utils.TextGetter;

/** Exception thrown when a given move had the wrong format when parsing a file with moves. */
public class MoveParsingException extends RuntimeException {

  /** Creates the custom exception. */
  public MoveParsingException(final String move) {
    super(TextGetter.getText("invalidFormat", move));
  }
}
