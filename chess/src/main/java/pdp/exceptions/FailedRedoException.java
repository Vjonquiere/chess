package pdp.exceptions;

import java.io.Serial;
import pdp.utils.TextGetter;

/** Exception thrown when it wasn't possible to redo a move. */
public class FailedRedoException extends RuntimeException {
  /** Define serialization id to avoid serialization related bugs */
  @Serial private static final long serialVersionUID = -906542981357987101L;

  /** Creates the custom exception. */
  public FailedRedoException() {
    super(TextGetter.getText("failedRedo"));
  }
}
