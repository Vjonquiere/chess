package pdp.exceptions;

import pdp.utils.TextGetter;

/** Exception thrown when it wasn't possible to undo a move. */
public class FailedUndoException extends RuntimeException {

  /** Creates the custom exception. */
  public FailedUndoException() {
    super(TextGetter.getText("failedUndo"));
  }
}
