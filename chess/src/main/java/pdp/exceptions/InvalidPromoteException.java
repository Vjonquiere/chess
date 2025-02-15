package pdp.exceptions;

public class InvalidPromoteException extends RuntimeException {
  public InvalidPromoteException() {
    super("Invalid promote format, please use the following format: " + "e7-e8=Q");
  }
}
