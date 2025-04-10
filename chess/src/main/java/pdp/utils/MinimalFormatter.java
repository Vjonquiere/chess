package pdp.utils;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

class MinimalFormatter extends SimpleFormatter {
  @Override
  public String format(LogRecord record) {
    return record.getMessage() + System.lineSeparator();
  }
}
