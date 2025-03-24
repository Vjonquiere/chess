package pdp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/** Parser to translate a configuration file into a map of options/ configurations. */
public class IniParser {

  /**
   * Parses an INI-formatted file given as an InputStream.
   *
   * <p>The INI file is expected to have the following format:
   *
   * <p>[section_name] key1 = value1 key2 = value2
   *
   * <p>[section_name2] key3 = value3
   *
   * <p>The method returns a map where each key is a section name and the value is a map of
   * key-value pairs in the section.
   *
   * @param stream the INI-formatted file as an InputStream
   * @return the parsed INI file as a map of maps
   * @throws IOException if an error occurs while reading the input stream
   */
  public static Map<String, Map<String, String>> parseIni(InputStream stream) throws IOException {
    Map<String, Map<String, String>> iniMap = new HashMap<>();
    Map<String, String> currentSection = null;

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
      String line;
      String currentSectionName = null;

      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith(";")) {
          continue;
        }

        if (line.startsWith("[") && line.endsWith("]")) {
          currentSectionName = line.substring(1, line.length() - 1).trim();
          currentSection = new HashMap<>();
          iniMap.put(currentSectionName, currentSection);
        } else if (currentSection != null && line.contains("=")) {
          String[] parts = line.split("=", 2);
          currentSection.put(parts[0].trim(), parts[1].trim());
        }
      }
    }
    return iniMap;
  }
}
