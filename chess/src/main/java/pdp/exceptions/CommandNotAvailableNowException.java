package pdp.exceptions;

import pdp.utils.TextGetter;

/** Exception thrown when the command asked for doesn't exist. */
public class CommandNotAvailableNowException extends RuntimeException {

  /** Creates the custom exception. */
  public CommandNotAvailableNowException() {
    super(TextGetter.getText("commandNotAvailable"));
  }
}
