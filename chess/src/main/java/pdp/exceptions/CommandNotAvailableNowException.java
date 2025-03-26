package pdp.exceptions;

import java.io.Serial;
import pdp.utils.TextGetter;

/** Exception thrown when the command asked for doesn't exist. */
public class CommandNotAvailableNowException extends RuntimeException {

  /** Define serialization id to avoid serialization related bugs */
  @Serial private static final long serialVersionUID = -1979527967900692376L;

  /** Creates the custom exception. */
  public CommandNotAvailableNowException() {
    super(TextGetter.getText("commandNotAvailable"));
  }
}
