package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pdp.utils.IniParser;

class IniParserTest {

  @BeforeAll
  public static void setUpLocale() {
      Locale.setDefault(Locale.ENGLISH);
  }

  @Test
  void testParseValidIni() throws Exception {
    String iniContent =
        "[section1]\n" + "key1 = value1\n" + "key2=value2\n" + "[section2]\n" + "key3 = value3";
    ByteArrayInputStream inputStream = new ByteArrayInputStream(iniContent.getBytes());

    Map<String, Map<String, String>> result = IniParser.parseIni(inputStream);

    // Check that two sections were parsed
    assertEquals(2, result.size());

    // Validate section1 key-values
    assertTrue(result.containsKey("section1"));
    Map<String, String> section1 = result.get("section1");
    assertEquals("value1", section1.get("key1"));
    assertEquals("value2", section1.get("key2"));

    // Validate section2 key-values
    assertTrue(result.containsKey("section2"));
    Map<String, String> section2 = result.get("section2");
    assertEquals("value3", section2.get("key3"));
  }

  @Test
  void testParseIniWithCommentsAndBlankLines() throws Exception {
    String iniContent =
        "; Comment line\n"
            + "\n"
            + "[section1]\n"
            + "key1 = value1\n"
            + "; Other comment\n"
            + "key2=value2\n"
            + "\n"
            + "[section2]\n"
            + "key3=value3\n"
            + "   ; Comment with leading spaces\n"
            + "key4 = value4\n";
    ByteArrayInputStream inputStream = new ByteArrayInputStream(iniContent.getBytes());

    Map<String, Map<String, String>> result = IniParser.parseIni(inputStream);

    assertEquals(2, result.size());

    Map<String, String> section1 = result.get("section1");
    assertEquals(2, section1.size());
    assertEquals("value1", section1.get("key1"));
    assertEquals("value2", section1.get("key2"));

    Map<String, String> section2 = result.get("section2");
    assertEquals(2, section2.size());
    assertEquals("value3", section2.get("key3"));
    assertEquals("value4", section2.get("key4"));
  }

  @Test
  void testParseEmptyIni() throws Exception {
    String iniContent = "";
    ByteArrayInputStream inputStream = new ByteArrayInputStream(iniContent.getBytes());

    Map<String, Map<String, String>> result = IniParser.parseIni(inputStream);

    assertTrue(result.isEmpty());
  }

  @Test
  void testParseIniWithoutSection() throws Exception {
    String iniContent = "key1=value1\n" + "key2 = value2\n";
    ByteArrayInputStream inputStream = new ByteArrayInputStream(iniContent.getBytes());

    Map<String, Map<String, String>> result = IniParser.parseIni(inputStream);

    assertTrue(result.isEmpty());
  }
}
