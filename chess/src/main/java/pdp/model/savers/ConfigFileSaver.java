package pdp.model.savers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import pdp.exceptions.FailedSaveException;

public class ConfigFileSaver {

  public static void save(String path, String text) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
      writer.write(text);
    } catch (IOException e) {
      throw new FailedSaveException(path);
    }
  }
}
