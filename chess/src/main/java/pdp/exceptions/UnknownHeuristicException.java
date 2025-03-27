package pdp.exceptions;

import java.io.Serial;
import pdp.utils.TextGetter;

/** Exception thrown when a given move had the wrong format when parsing a file with moves. */
public class UnknownHeuristicException extends RuntimeException {

  /** Define serialization id to avoid serialization related bugs. */
  @Serial private static final long serialVersionUID = 1906611762336666339L;

  /** Creates the custom exception. */
  public UnknownHeuristicException(final String message) {
    super(TextGetter.getText("unknownHeuristic") + ": " + message);
  }
}
