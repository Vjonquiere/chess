package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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

  String expectedHelp =
      "usage: chess\n"
          + " -a,--ai <COLOR>                 Launch the program in AI mode, with\n"
          + "                                 artificial player with COLOR ’B’ or ’A’\n"
          + "                                 (All),(W by default).\n"
          + "    --ai-depth <DEPTH>           Specify the depth of the AI algorithm\n"
          + "    --ai-heuristic <HEURISTIC>   Choose the heuristic for the artificial\n"
          + "                                 player\n"
          + "    --ai-mode <ALGORITHM>        Choose the exploration algorithm for the\n"
          + "                                 artificial player.\n"
          + "    --ai-time <TIME>             Specify the time of reflexion for AI mode\n"
          + "                                 (default 5 seconds)\n"
          + " -b,--blitz                      Play in blitz mode\n"
          + " -c,--contest <FILENAME>         AI plays one move in the given file\n"
          + " -d,--debug                      Print debugging information\n"
          + " -g,--gui                        Displays the game with a  graphical\n"
          + "                                 interface.\n"
          + " -h,--help                       Print this message and exit\n"
          + "    --lang <LANGUAGE>            Choose the language for the app (en\n"
          + "                                 supported)\n"
          + " -t,--time <TIME>                Specify time per round for blitz mode\n"
          + "                                 (default 30min)\n"
          + " -V,--version                    Print the version information and exit\n"
          + " -v,--verbose                    Display more information\n";

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
    assertEquals(expectedHelp.trim(), outputStream.toString().trim());
    outputStream.reset();
    verify(mockRuntime).exit(0);

    /* Test that the option displays the right output & exit code with the short option name */
    Runtime mockRuntime2 = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"-h"}, mockRuntime2);
    assertEquals(expectedHelp.trim(), outputStream.toString().trim());
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
    System.out.println(
        "Expected: [" + expected.trim().replace("\n", "\\n").replace("\r", "\\r") + "]");
    System.out.println(
        "Actual:   ["
            + outputStream.toString().trim().replace("\n", "\\n").replace("\r", "\\r")
            + "]");

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
    assertEquals(expectedHelp.trim(), outputStream.toString().trim());
    outputStream.reset();
    verify(mockRuntime).exit(0);

    Runtime mockRuntime2 = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"-V", "-h"}, mockRuntime2);
    assertEquals(expectedHelp.trim(), outputStream.toString().trim());
    outputStream.reset();
    verify(mockRuntime2).exit(0);
  }

  @Test
  public void testUnrecognized() {
    String expected = "Parsing failed.  Reason: Unrecognized option: -zgv\n";

    // Test with an unrecognized option (error)
    Runtime mockRuntime = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"-zgv"}, mockRuntime);
    assertEquals(expected + expectedHelp.trim(), outputStream.toString().trim());
    outputStream.reset();
    verify(mockRuntime).exit(1);
  }

  @Test
  public void testPartialMatching() {

    // Test partial matching (no error)
    Runtime mockRuntime = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"-hel"}, mockRuntime);
    assertEquals(expectedHelp.trim(), outputStream.toString().trim());
    outputStream.reset();
    verify(mockRuntime).exit(0);
  }

  @Test
  public void testAmbiguous() throws Exception {
    String expectedAmbiguous =
        "Parsing failed.  Reason: Ambiguous option: '--ai-'  (could be: 'ai-mode', 'ai-depth', 'ai-heuristic', 'ai-time')\n";

    // Test ambiguous option (several options starting the same) (error)
    Runtime mockRuntime = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"--ai-"}, mockRuntime);
    assertEquals(expectedAmbiguous + expectedHelp.trim(), outputStream.toString().trim());
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
    assertTrue(outputStream.toString().contains("Language option activated"));
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
    assertTrue(outputStream.toString().contains("Language option activated"));
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
}
