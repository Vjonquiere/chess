package pdp.exceptions;

import java.io.Serial;
import pdp.utils.TextGetter;

/** Exception thrown when it wasn't possible to get the given position. */
public class InvalidPositionException extends RuntimeException {
  /** Define serialization id to avoid serialization related bugs. */
  @Serial private static final long serialVersionUID = -8425619722573869807L;

  /** Creates the custom exception. */
  public InvalidPositionException(final String pos) {
    super(TextGetter.getText("invalidPosition", pos));
  }
}
