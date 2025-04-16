package pdp.exceptions;

import java.io.Serial;

/** Exception thrown for general errors related to the app. */
public class ChessException extends RuntimeException {
  /** Define serialization id to avoid serialization related bugs. */
  @Serial private static final long serialVersionUID = -8637891103627249042L;

  /** Creates the custom exception. */
  public ChessException(String message) {
    super(message);
  }
}
