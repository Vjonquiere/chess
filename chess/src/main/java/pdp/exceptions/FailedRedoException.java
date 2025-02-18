package pdp.exceptions;

import pdp.utils.TextGetter;

public class FailedRedoException extends RuntimeException {
  public FailedRedoException() {
    super(TextGetter.getText("failedRedo"));
  }
}
