package pdp.exceptions;

import pdp.utils.TextGetter;

public class FailedUndoException extends RuntimeException {
  public FailedUndoException() {
    super(TextGetter.getText("failedUndo"));
  }
}
