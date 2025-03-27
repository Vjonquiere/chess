package pdp.exceptions;

import java.io.Serial;
import pdp.utils.TextGetter;

/** Exception thrown when the format of the pawn promotion is incorrect. */
public class InvalidPromoteFormatException extends RuntimeException {
  /** Define serialization id to avoid serialization related bugs */
  @Serial private static final long serialVersionUID = -2783655381663570030L;

  /** Creates the custom exception. */
  public InvalidPromoteFormatException() {
    super(TextGetter.getText("invalidPromoteFormat", "e7-e8=Q"));
  }
}
