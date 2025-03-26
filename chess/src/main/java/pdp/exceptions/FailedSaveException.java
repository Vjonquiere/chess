package pdp.exceptions;

import pdp.utils.TextGetter;

/** Exception thrown when it wasn't possible to save the game to a file. */
public class FailedSaveException extends RuntimeException {

  /** Creates the custom exception. */
  public FailedSaveException(final String file) {
    super(TextGetter.getText("failedSave", file));
  }
}
