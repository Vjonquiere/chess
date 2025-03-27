package pdp.exceptions;

import java.io.Serial;
import pdp.utils.TextGetter;

/** Exception thrown when it wasn't possible to save the game to a file. */
public class FailedSaveException extends RuntimeException {
  /** Define serialization id to avoid serialization related bugs */
  @Serial private static final long serialVersionUID = -4465965333917355871L;

  /** Creates the custom exception. */
  public FailedSaveException(final String file) {
    super(TextGetter.getText("failedSave", file));
  }
}
