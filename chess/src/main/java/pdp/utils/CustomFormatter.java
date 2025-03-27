package pdp.utils;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/** Custom formatting for the logger class : YYYY-MM-DD hh:mm:ss message. */
public class CustomFormatter extends Formatter {
  @Override
  public String format(final LogRecord record) {
    return String.format("%1$tF %1$tT %2$s\n", record.getMillis(), formatMessage(record));
  }
}
