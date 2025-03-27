package pdp.exceptions;

import java.io.Serial;
import pdp.utils.TextGetter;

/** Exception thrown when a given move had the wrong format when parsing a file with moves. */
public class MoveParsingException extends RuntimeException {
  /** Define serialization id to avoid serialization related bugs */
  @Serial private static final long serialVersionUID = -875772480112506781L;

  /** Creates the custom exception. */
  public MoveParsingException(final String move) {
    super(TextGetter.getText("invalidFormat", move));
  }
}
