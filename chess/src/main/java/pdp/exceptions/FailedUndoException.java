package pdp.exceptions;

import pdp.utils.TextGetter;

/** Exception thrown when it wasn't possible to undo a move. */
public class FailedUndoException extends RuntimeException {
  public FailedUndoException() {
    super(TextGetter.getText("failedUndo"));
  }
}
