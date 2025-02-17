package pdp.exceptions;

import pdp.utils.TextGetter;

public class FailedSaveException extends RuntimeException {
  public FailedSaveException(String file) {
    super(TextGetter.getText("failedSave", file));
  }
}
