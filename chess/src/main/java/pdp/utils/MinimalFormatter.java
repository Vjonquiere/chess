package pdp.utils;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/** Simple formatter for the logger class: message only. */
class MinimalFormatter extends SimpleFormatter {
  @Override
  public String format(final LogRecord record) {
    return record.getMessage() + System.lineSeparator();
  }
}
