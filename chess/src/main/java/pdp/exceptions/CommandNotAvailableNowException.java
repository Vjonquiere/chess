package pdp.exceptions;

import pdp.utils.TextGetter;

public class CommandNotAvailableNowException extends RuntimeException {
  public CommandNotAvailableNowException() {
    super(TextGetter.getText("commandNotAvailable"));
  }
}
