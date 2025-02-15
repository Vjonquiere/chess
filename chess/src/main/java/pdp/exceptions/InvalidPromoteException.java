package pdp.exceptions;

import pdp.utils.TextGetter;

public class InvalidPromoteException extends RuntimeException {
  public InvalidPromoteException() {
    super(TextGetter.getText("invalidPromoteFormat", "e7-e8=Q"));
  }
}
