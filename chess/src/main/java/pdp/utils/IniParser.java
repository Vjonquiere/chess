package pdp.utils;

import java.io.*;
import java.util.*;

public class IniParser {

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
