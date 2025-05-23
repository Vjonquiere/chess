package pdp.model.savers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import pdp.exceptions.FailedSaveException;

/** Save a string to the given path. */
public final class ConfigFileSaver {

  /** Private constructor to avoid instanciating a utility class. */
  private ConfigFileSaver() {}

  /**
   * Save the supplied text to the given path.
   *
   * @param path Where to store the file.
   * @param text The string to save.
   */
  public static void save(final String path, final String text) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
      writer.write(text);
    } catch (IOException e) {
      throw new FailedSaveException(path);
    }
  }
}
