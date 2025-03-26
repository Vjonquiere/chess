package pdp.exceptions;

import java.io.Serial;
import pdp.utils.TextGetter;

/** Exception thrown when it wasn't possible to play the given move. */
public class IllegalMoveException extends RuntimeException {
  /** Define serialization id to avoid serialization related bugs */
  @Serial private static final long serialVersionUID = -3995941852910827015L;

  /** Creates the custom exception. */
  public IllegalMoveException(final String move) {
    super(TextGetter.getText("illegalMove", move));
  }
}
