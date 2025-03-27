package pdp.exceptions;

import java.io.Serial;
import pdp.utils.TextGetter;

/** Exception thrown when it wasn't possible to undo a move. */
public class FailedUndoException extends RuntimeException {
  /** Define serialization id to avoid serialization related bugs */
  @Serial private static final long serialVersionUID = 1280322163713635969L;

  /** Creates the custom exception. */
  public FailedUndoException() {
    super(TextGetter.getText("failedUndo"));
  }
}
