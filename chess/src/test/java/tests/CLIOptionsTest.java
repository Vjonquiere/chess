package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.utils.CLIOptions;
import pdp.utils.Logging;
import pdp.utils.OptionType;
import pdp.utils.TextGetter;

public class CLIOptionsTest {
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private Logger logger;
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  String[] expectedHelp = {
    "usage: chess",
    " -a,--ai <COLOR>                 Launch the program in AI mode, with",
    "                                 (All),(W by default).",
    "                                 artificial player with COLOR 'B' or 'A'",
    "    --ai-depth <DEPTH>           Specify the depth of the AI algorithm",
    "    --ai-heuristic <HEURISTIC>   Choose the heuristic for the artificial",
    "                                 player",
    "    --ai-mode <ALGORITHM>        Choose the exploration algorithm for the",
    "                                 artificial player.",
    "    --ai-time <TIME>             Specify the time of reflexion for AI mode",
    "                                 (default 5 seconds)",
    " -b,--blitz                      Play in blitz mode",
    " -c,--contest <FILENAME>         AI plays one move in the given file",
    " -d,--debug                      Print debugging information",
    " -g,--gui                        Displays the game with a  graphical",
    "                                 interface.",
    " -h,--help                       Print this message and exit",
    "    --lang <LANGUAGE>            Choose the language for the app (en",
    "                                 supported)",
    " -t,--time <TIME>                Specify time per round for blitz mode",
    "                                 (default 30min)",
    " -V,--version                    Print the version information and exit",
    " -v,--verbose                    Display more information"
  };

  @BeforeEach
  public void setUp() {
    outputStream.reset();
    System.setOut(new PrintStream(outputStream));
    System.setErr(new PrintStream(outputStream));
  }

  @AfterEach
  public void tearDown() {
    System.setOut(originalOut);
    System.setErr(originalErr);
    Logging.setDebug(false);
    Logging.setVerbose(false);
  }

  @Test
  public void testHelp() {
    /* Test that the option displays the right output & exit code with the long option name */
    Runtime mockRuntime = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"--help"}, mockRuntime);
    for (String s : expectedHelp) {
      assertTrue(outputStream.toString().contains(s));
    }
    outputStream.reset();
    verify(mockRuntime).exit(0);

    /* Test that the option displays the right output & exit code with the short option name */
    Runtime mockRuntime2 = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"-h"}, mockRuntime2);
    for (String s : expectedHelp) {
      assertTrue(outputStream.toString().contains(s));
    }
    outputStream.reset();
    verify(mockRuntime2).exit(0);
  }

  @Test
  public void testVersion() throws Exception {
    final Properties properties = new Properties();
    properties.load(CLIOptions.class.getClassLoader().getResourceAsStream(".properties"));
    String expected = "Version: " + properties.getProperty("version");

    /* Test that the option displays the right output & exit code with the long option name */
    Runtime mockRuntime = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"--version"}, mockRuntime);
    assertEquals(expected.trim(), outputStream.toString().trim());
    outputStream.reset();
    verify(mockRuntime).exit(0);

    /* Test that the option displays the right output & exit code with the short option name */
    Runtime mockRuntime2 = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"-V"}, mockRuntime2);
    assertEquals(expected.trim(), outputStream.toString().trim());
    outputStream.reset();
    verify(mockRuntime2).exit(0);
    outputStream.reset();
  }

  @Test
  public void testHelpFirst() {
    /* Test that only help is displayed, even with several parameters */
    Runtime mockRuntime = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"-h", "-V"}, mockRuntime);

    for (String s : expectedHelp) {
      assertTrue(outputStream.toString().contains(s));
    }
    outputStream.reset();
    verify(mockRuntime).exit(0);

    Runtime mockRuntime2 = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"-V", "-h"}, mockRuntime2);

    for (String s : expectedHelp) {
      assertTrue(outputStream.toString().contains(s));
    }
    outputStream.reset();
    verify(mockRuntime2).exit(0);
  }

  @Test
  public void testUnrecognized() {
    String expected = "Parsing failed.  Reason: Unrecognized option: -zgv";

    // Test with an unrecognized option (error)
    Runtime mockRuntime = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"-zgv"}, mockRuntime);
    assertTrue(outputStream.toString().contains(expected));
    for (String s : expectedHelp) {
      assertTrue(outputStream.toString().contains(s));
    }
    outputStream.reset();
    verify(mockRuntime).exit(1);
  }

  @Test
  public void testPartialMatching() {

    // Test partial matching (no error)
    Runtime mockRuntime = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"-hel"}, mockRuntime);
    for (String s : expectedHelp) {
      assertTrue(outputStream.toString().contains(s));
    }
    outputStream.reset();
    verify(mockRuntime).exit(0);
  }

  @Test
  public void testAmbiguous() throws Exception {
    String expectedAmbiguous =
        "Parsing failed.  Reason: Ambiguous option: '--ai-'  (could be: 'ai-mode', 'ai-depth', 'ai-heuristic', 'ai-time')";

    // Test ambiguous option (several options starting the same) (error)
    Runtime mockRuntime = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"--ai-"}, mockRuntime);
    assertTrue(outputStream.toString().contains(expectedAmbiguous));
    for (String s : expectedHelp) {
      assertTrue(outputStream.toString().contains(s));
    }
    outputStream.reset();
    verify(mockRuntime).exit(1);
  }

  public void setUpLogging() {
    logger = Logger.getLogger("TestLogger");

    Logging.setDebug(false);
    Logging.setVerbose(false);
    Logging.configureLogging(logger);
  }

  @Test
  public void testDebug() throws Exception {
    setUpLogging();
    Logging.setDebug(true);

    final Properties properties = new Properties();
    properties.load(CLIOptions.class.getClassLoader().getResourceAsStream(".properties"));
    String expected = "Version: " + properties.getProperty("version");

    /* Test that the option displays the right output & exit code with the long option name */
    Runtime mockRuntime = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"--debug", "-V"}, mockRuntime);
    assertTrue(outputStream.toString().contains("[DEBUG]"));
    assertTrue(outputStream.toString().contains("Debug mode activated"));
    assertTrue(outputStream.toString().contains(expected));
    outputStream.reset();
    verify(mockRuntime).exit(0);

    /* Test that the option displays the right output & exit code with the short option name */
    Runtime mockRuntime2 = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"-V", "-d"}, mockRuntime2);
    assertTrue(outputStream.toString().contains("[DEBUG]"));
    assertTrue(outputStream.toString().contains("Debug mode activated"));
    assertTrue(outputStream.toString().contains(expected));
    outputStream.reset();
    verify(mockRuntime2).exit(0);
    outputStream.reset();
  }

  @Test
  public void testVerbose() throws Exception {
    /*
    The tests display debug because verbose mode also displays debug, and
    we have decided to display a debug message in CLIOptions
     */
    setUpLogging();
    Logging.setVerbose(true);

    final Properties properties = new Properties();
    properties.load(CLIOptions.class.getClassLoader().getResourceAsStream(".properties"));
    String expected = "Version: " + properties.getProperty("version");

    /* Test that the option displays the right output & exit code with the long option name */
    Runtime mockRuntime = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"--verbose", "-V"}, mockRuntime);
    assertTrue(outputStream.toString().contains("[DEBUG]"));
    assertTrue(outputStream.toString().contains("Verbose mode activated"));
    assertTrue(outputStream.toString().contains(expected));
    outputStream.reset();
    verify(mockRuntime).exit(0);

    /* Test that the option displays the right output & exit code with the short option name */
    Runtime mockRuntime2 = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"-V", "-v"}, mockRuntime2);
    assertTrue(outputStream.toString().contains("[DEBUG]"));
    assertTrue(outputStream.toString().contains("Verbose mode activated"));
    assertTrue(outputStream.toString().contains(expected));
    outputStream.reset();
    verify(mockRuntime2).exit(0);
    outputStream.reset();
  }

  @Test
  public void testLanguageCorrect() {
    setUpLogging();
    Logging.setDebug(true);

    /* Test that asking for the app in english is the default and will display the debug message.*/
    Runtime mockRuntime = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"--debug", "--lang=en"}, mockRuntime);
    assertTrue(outputStream.toString().contains("lang option activated"));
    assertTrue(outputStream.toString().contains("Language = English (already set by default)"));
    assertEquals("Chess game", TextGetter.getText("title"));
    outputStream.reset();
  }

  @Test
  public void testLanguageWrong() {
    setUpLogging();
    Logging.setDebug(true);

    /* Test that a language not supported will display the information message and
     * that the default language of the app is english*/
    Runtime mockRuntime = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"--debug", "--lang=ru"}, mockRuntime);
    assertTrue(outputStream.toString().contains("lang option activated"));
    assertTrue(outputStream.toString().contains("Language ru not supported, language = english"));
    assertEquals("Chess game", TextGetter.getText("title"));
    outputStream.reset();
  }

  @Test
  public void returnedMap() {
    Map<OptionType, String> expectedMap = new HashMap<>();
    expectedMap.put(OptionType.BLITZ, "");
    expectedMap.put(OptionType.CONTEST, "myfile.chessrc");
    expectedMap.put(OptionType.AI, "W");
    expectedMap.put(OptionType.CONFIG, "default.chessrc");
    expectedMap.put(OptionType.AI_TIME, "5");
    expectedMap.put(OptionType.AI_DEPTH, "3");
    expectedMap.put(OptionType.AI_MODE, "test");
    expectedMap.put(OptionType.TIME, "12");
    expectedMap.put(OptionType.GUI, "");
    expectedMap.put(OptionType.AI_HEURISTIC, "test");

    Runtime mockRuntime = mock(Runtime.class);
    Map<OptionType, String> output =
        CLIOptions.parseOptions(
            new String[] {
              "-a=W",
              "-b",
              "-g",
              "-t=12",
              "--ai-mode=test",
              "--ai-heuristic=test",
              "--ai-depth=3",
              "--ai-time=5",
              "--contest=myfile.chessrc"
            },
            mockRuntime);

    assertEquals(expectedMap, output);
  }

  @Test
  public void testConfigFileWrongExtension() {
    Runtime mockRuntime = mock(Runtime.class);
    HashMap<OptionType, String> map =
        CLIOptions.parseOptions(new String[] {"--config=invalid.txt"}, mockRuntime);
    assertTrue(map.get(OptionType.CONFIG) == "default.chessrc");
  }

  @Test
  public void testDefaultFileOptions() throws Exception {
    Path tempConfig = Files.createTempFile("testConfig", ".chessrc");
    String iniContent = "[Default]\nai=true\ndebug=false\nverbose=true\n";
    Files.write(tempConfig, iniContent.getBytes(StandardCharsets.UTF_8));

    Runtime mockRuntime = mock(Runtime.class);
    Map<OptionType, String> activatedOptions =
        CLIOptions.parseOptions(new String[] {"--config=" + tempConfig.toString()}, mockRuntime);

    assertTrue(activatedOptions.containsKey(OptionType.AI));
    assertTrue(activatedOptions.containsKey(OptionType.VERBOSE));
    assertFalse(activatedOptions.containsKey(OptionType.DEBUG));

    Files.deleteIfExists(tempConfig);
  }

  @Test
  public void testCommandOverride() throws Exception {
    Path tempConfig = Files.createTempFile("testConfig", ".chessrc");
    String iniContent = "[Default]\ntime=100\ndebug=false\nverbose=true\n";
    Files.write(tempConfig, iniContent.getBytes(StandardCharsets.UTF_8));

    Runtime mockRuntime = mock(Runtime.class);
    Map<OptionType, String> activatedOptionsOverride =
        CLIOptions.parseOptions(
            new String[] {"--config=" + tempConfig.toString(), "--time=200", "-d"}, mockRuntime);

    assertTrue(activatedOptionsOverride.containsKey(OptionType.TIME));
    assertEquals("200", activatedOptionsOverride.get(OptionType.TIME));

    assertTrue(activatedOptionsOverride.containsKey(OptionType.DEBUG));
    assertTrue(activatedOptionsOverride.containsKey(OptionType.VERBOSE));

    Files.deleteIfExists(tempConfig);
  }

  @Test
  public void testFileNotFound() throws Exception {
    Path tempConfig = Files.createTempFile("testConfig", ".chessrc");
    Files.deleteIfExists(tempConfig);

    Runtime mockRuntime = mock(Runtime.class);
    Map<OptionType, String> activatedOptions =
        CLIOptions.parseOptions(new String[] {"--config=" + tempConfig.toString()}, mockRuntime);

    assertEquals("default.chessrc", activatedOptions.get(OptionType.CONFIG));
  }

  @Test
  public void testBothFilesNotFound() throws Exception {
    Path tempConfig = Files.createTempFile("testConfig", ".chessrc");
    Files.deleteIfExists(tempConfig);

    Path defaultConfig = Files.createTempFile("nonexistant", ".chessrc");
    Files.deleteIfExists(defaultConfig);

    Field defaultConfigField = CLIOptions.class.getDeclaredField("DEFAULT_CONFIG_FILE");
    defaultConfigField.setAccessible(true);
    String originalDefault = (String) defaultConfigField.get(null);
    defaultConfigField.set(null, defaultConfig.toString());

    Runtime mockRuntime = mock(Runtime.class);
    Map<OptionType, String> activatedOptions =
        CLIOptions.parseOptions(new String[] {"--config=" + tempConfig.toString()}, mockRuntime);
    assertNull(activatedOptions.get(OptionType.CONFIG));
    defaultConfigField.set(null, originalDefault);
  }
}
