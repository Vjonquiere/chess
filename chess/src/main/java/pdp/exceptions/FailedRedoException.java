package pdp.exceptions;

import pdp.utils.TextGetter;

/** Exception thrown when it wasn't possible to redo a move. */
public class FailedRedoException extends RuntimeException {
  public FailedRedoException() {
    super(TextGetter.getText("failedRedo"));
  }
}
