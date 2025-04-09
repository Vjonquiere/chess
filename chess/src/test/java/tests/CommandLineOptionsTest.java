package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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
import pdp.utils.CommandLineOptions;
import pdp.utils.Logging;
import pdp.utils.OptionType;
import pdp.utils.TextGetter;

public class CommandLineOptionsTest {
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private Logger logger;
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  String[] expectedHelp = {
    "usage: chess",
    " -a,--ai <COLOR>                      Launch the program in AI mode, with",
    "                                      artificial player with COLOR 'W',",
    "                                      'B' or 'A' (All),(W by default).",
    "    --ai-depth <DEPTH>                Specify the depth of the AI",
    "                                      algorithm or the number of",
    "                                      simulations for the MCTS AI",
    "                                      algorithm",
    "    --ai-depth-b <DEPTH>              Specify the depth of the AI",
    "                                      algorithm for the black player",
    "    --ai-depth-w <DEPTH>              Specify the depth of the AI",
    "                                      algorithm for the white player",
    "    --ai-endgame <HEURISTIC>          Choose the heuristic for the endgame",
    "                                      of the artificial players.",
    "    --ai-endgame-b <HEURISTIC>        Choose the heuristic for the endgame",
    "                                      of the black artificial player.",
    "    --ai-endgame-w <HEURISTIC>        Choose the heuristic for the endgame",
    "                                      of the white artificial player.",
    "    --ai-heuristic <HEURISTIC>        Choose the heuristic for the",
    "                                      artificial players.",
    "                                      Choose between these heuristic (case",
    "                                      sensitive)",
    "                                      - STANDARD : Aggregates multiple",
    "                                      heuristics to evaluate the board",
    "                                      during the start and middle game.",
    "                                      - STANDARD_LIGHT : A lighter version",
    "                                      of the STANDARD heuristic, taking",
    "                                      less parameters into account.",
    "                                      - SHANNON : Basic Heuristic from",
    "                                      Shannon.",
    "                                      - ENDGAME : Aggregates multiple",
    "                                      heuristics to evaluate the board",
    "                                      state during the endgame phase of",
    "                                      the match.",
    "                                      - BAD_PAWNS : Computes a score",
    "                                      according to the potential",
    "                                      weaknesses in the observed pawn",
    "                                      structures.",
    "                                      - BISHOP_ENDGAME : Computes a score",
    "                                      according to how performant bishops",
    "                                      are for an endgame position.",
    "                                      - DEVELOPMENT : Computes and returns",
    "                                      a score corresponding to the level",
    "                                      of development for each player.",
    "                                      - GAME_STATUS : Computes a score",
    "                                      based on the possible game endings.",
    "                                      - KING_ACTIVITY : Computes a score",
    "                                      based on the king's activity (is in",
    "                                      center and has a lot of possible",
    "                                      moves).",
    "                                      - KING_OPPOSITION : Computes a score",
    "                                      according to the (un)balance of the",
    "                                      kings position.",
    "                                      - KING_SAFETY : Assigns a score to a",
    "                                      player according to the safety of",
    "                                      his king.",
    "                                      - MATERIAL : Computes a score based",
    "                                      on the pieces on the board.",
    "                                      - MOBILITY : Computes a score based",
    "                                      on the available moves for each",
    "                                      player.",
    "                                      - PAWN_CHAIN : Computes a score",
    "                                      according to how strongly pawns are",
    "                                      connected.",
    "                                      - PROMOTION : Computes a score",
    "                                      according to closeness of pawns",
    "                                      promoting.",
    "                                      - SPACE_CONTROL : Gives a score",
    "                                      based on how much control over the",
    "                                      entire board the players have.",
    "    --ai-heuristic-b <HEURISTIC>      Choose the heuristic for the",
    "                                      artificial black player.",
    "    --ai-heuristic-w <HEURISTIC>      Choose the heuristic for the",
    "                                      artificial white player.",
    "    --ai-mode <ALGORITHM>             Choose the exploration algorithm for",
    "                                      the artificial players.",
    "                                      Available options:",
    "                                      - MINIMAX : Uses the MiniMax",
    "                                      algorithm.",
    "                                      - ALPHA_BETA : Uses the Alpha-Beta",
    "                                      Pruning algorithm (default).",
    "                                      - MCTS : Uses Monte Carlo Tree",
    "                                      Search for AI move exploration.",
    "    --ai-mode-b <ALGORITHM>           Choose the exploration algorithm for",
    "                                      the artificial black player.",
    "    --ai-mode-w <ALGORITHM>           Choose the exploration algorithm for",
    "                                      the artificial white player.",
    "    --ai-simulation <SIMULATION>      Specify the number of simulations",
    "                                      for the MCTS AI algorithm",
    "    --ai-simulation-b <SIMULATIONS>   Specify the number of simulations",
    "                                      for the Black MCTS AI algorithm",
    "    --ai-simulation-w <SIMULATIONS>   Specify the number of simulations",
    "                                      for the White MCTS AI algorithm",
    "    --ai-time <TIME>                  Specify the time of reflexion for AI",
    "                                      mode in seconds (default 5 seconds)",
    " -b,--blitz                           Play in blitz mode",
    " -c,--contest <FILENAME>              AI plays one move in the given file",
    "    --config <FILENAME>               Sets the configuration file to use",
    " -d,--debug                           Print debugging information",
    " -g,--gui                             Displays the game with a  graphical",
    "                                      interface.",
    " -h,--help                            Print this message and exit",
    "    --lang <LANGUAGE>                 Choose the language for the app (en",
    "                                      supported)",
    "    --load <FILENAME>                 The name of the file from which to",
    "                                      load the history",
    " -t,--time <TIME>                     Specify time per round for blitz",
    "                                      mode (default 30min)",
    " -V,--version                         Print the version information and",
    "                                      exit",
    " -v,--verbose                         Display more information"
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
    // Test that the option displays the right output & exit code with the long option name
    Runtime mockRuntime = mock(Runtime.class);
    CommandLineOptions.parseOptions(new String[] {"--help"}, mockRuntime);
    for (String s : expectedHelp) {
      assertTrue(outputStream.toString().contains(s));
    }
    outputStream.reset();
    verify(mockRuntime).exit(0);

    // Test that the option displays the right output & exit code with the short option name
    Runtime mockRuntime2 = mock(Runtime.class);
    CommandLineOptions.parseOptions(new String[] {"-h"}, mockRuntime2);
    for (String s : expectedHelp) {
      assertTrue(outputStream.toString().contains(s));
    }
    outputStream.reset();
    verify(mockRuntime2).exit(0);
  }

  @Test
  public void testVersion() throws Exception {
    final Properties properties = new Properties();
    properties.load(CommandLineOptions.class.getClassLoader().getResourceAsStream(".properties"));
    String expected = "Version: " + properties.getProperty("version");

    /* Test that the option displays the right output & exit code with the long option name */
    Runtime mockRuntime = mock(Runtime.class);
    CommandLineOptions.parseOptions(new String[] {"--version"}, mockRuntime);
    assertEquals(expected.trim(), outputStream.toString().trim());
    outputStream.reset();
    verify(mockRuntime).exit(0);

    /* Test that the option displays the right output & exit code with the short option name */
    Runtime mockRuntime2 = mock(Runtime.class);
    CommandLineOptions.parseOptions(new String[] {"-V"}, mockRuntime2);
    assertEquals(expected.trim(), outputStream.toString().trim());
    outputStream.reset();
    verify(mockRuntime2).exit(0);
    outputStream.reset();
  }

  @Test
  public void testHelpFirst() {
    // Test that only help is displayed, even with several parameters
    Runtime mockRuntime = mock(Runtime.class);
    CommandLineOptions.parseOptions(new String[] {"-h", "-V"}, mockRuntime);

    for (String s : expectedHelp) {
      assertTrue(outputStream.toString().contains(s));
    }
    outputStream.reset();
    verify(mockRuntime).exit(0);

    Runtime mockRuntime2 = mock(Runtime.class);
    CommandLineOptions.parseOptions(new String[] {"-V", "-h"}, mockRuntime2);

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
    CommandLineOptions.parseOptions(new String[] {"-zgv"}, mockRuntime);
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
    CommandLineOptions.parseOptions(new String[] {"-hel"}, mockRuntime);
    for (String s : expectedHelp) {
      assertTrue(outputStream.toString().contains(s));
    }
    outputStream.reset();
    verify(mockRuntime).exit(0);
  }

  @Test
  public void testAmbiguous() throws Exception {
    String expectedAmbiguous =
        "Parsing failed.  Reason: Ambiguous option: '--ai-'  (could be: 'ai-mode', 'ai-mode-w', 'ai-mode-b', 'ai-simulation', 'ai-simulation-w', 'ai-simulation-b', 'ai-depth', 'ai-depth-w', 'ai-depth-b', 'ai-heuristic', 'ai-heuristic-w', 'ai-heuristic-b', 'ai-endgame', 'ai-endgame-w', 'ai-endgame-b', 'ai-time', 'ai-weight-w', 'ai-weight-b')";

    // Test ambiguous option (several options starting the same) (error)
    Runtime mockRuntime = mock(Runtime.class);
    CommandLineOptions.parseOptions(new String[] {"--ai-"}, mockRuntime);
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
    properties.load(CommandLineOptions.class.getClassLoader().getResourceAsStream(".properties"));
    String expected = "Version: " + properties.getProperty("version");

    /* Test that the option displays the right output & exit code with the long option name */
    Runtime mockRuntime = mock(Runtime.class);
    CommandLineOptions.parseOptions(new String[] {"--debug", "-V"}, mockRuntime);
    assertTrue(outputStream.toString().contains("[DEBUG]"));
    assertTrue(outputStream.toString().contains("Debug mode activated"));
    assertTrue(outputStream.toString().contains(expected));
    outputStream.reset();
    verify(mockRuntime).exit(0);

    /* Test that the option displays the right output & exit code with the short option name */
    Runtime mockRuntime2 = mock(Runtime.class);
    CommandLineOptions.parseOptions(new String[] {"-V", "-d"}, mockRuntime2);
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
    we have decided to display a debug message in CommandLineOptions
     */
    setUpLogging();
    Logging.setVerbose(true);

    final Properties properties = new Properties();
    properties.load(CommandLineOptions.class.getClassLoader().getResourceAsStream(".properties"));
    String expected = "Version: " + properties.getProperty("version");

    /* Test that the option displays the right output & exit code with the long option name */
    Runtime mockRuntime = mock(Runtime.class);
    CommandLineOptions.parseOptions(new String[] {"--verbose", "-V"}, mockRuntime);
    assertTrue(outputStream.toString().contains("[DEBUG]"));
    assertTrue(outputStream.toString().contains("Verbose mode activated"));
    assertTrue(outputStream.toString().contains(expected));
    outputStream.reset();
    verify(mockRuntime).exit(0);

    /* Test that the option displays the right output & exit code with the short option name */
    Runtime mockRuntime2 = mock(Runtime.class);
    CommandLineOptions.parseOptions(new String[] {"-V", "-v"}, mockRuntime2);
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
    CommandLineOptions.parseOptions(new String[] {"--debug", "--lang=en"}, mockRuntime);
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
    CommandLineOptions.parseOptions(new String[] {"--debug", "--lang=ru"}, mockRuntime);
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
    expectedMap.put(OptionType.AI, "A"); // Contest mode so switching to All IAs
    expectedMap.put(OptionType.CONFIG, "default.chessrc");
    expectedMap.put(
        OptionType.CONFIG, System.getProperty("user.home") + "/.chessSettings/default.chessrc");
    expectedMap.put(OptionType.AI_TIME, "5");
    expectedMap.put(OptionType.AI_DEPTH_B, "3");
    expectedMap.put(OptionType.AI_DEPTH_W, "3");
    expectedMap.put(OptionType.AI_MODE_B, "test");
    expectedMap.put(OptionType.AI_MODE_W, "test");
    expectedMap.put(OptionType.TIME, "12");
    expectedMap.put(OptionType.GUI, "");
    expectedMap.put(OptionType.AI_HEURISTIC_B, "test2");
    expectedMap.put(OptionType.AI_HEURISTIC_W, "test2");
    expectedMap.put(OptionType.AI_SIMULATION_W, "150");
    expectedMap.put(OptionType.AI_SIMULATION_B, "200");
    expectedMap.put(OptionType.AI_ENDGAME_B, "ENDGAME");
    expectedMap.put(OptionType.AI_ENDGAME_W, "STANDARD");

    Runtime mockRuntime = mock(Runtime.class);
    Map<OptionType, String> output =
        CommandLineOptions.parseOptions(
            new String[] {
              "-a=W",
              "-b",
              "-g",
              "-t=12",
              "--ai-mode=test",
              "--ai-heuristic=test2",
              "--ai-depth=3",
              "--ai-time=5",
              "--contest=myfile.chessrc",
              "--ai-simulation=500",
              "--ai-simulation-w=150",
              "--ai-simulation-b=200",
              "--ai-endgame-w=STANDARD"
            },
            mockRuntime);

    assertEquals(expectedMap, output);
  }

  @Test
  public void testConfigFileWrongExtension() {
    Runtime mockRuntime = mock(Runtime.class);
    HashMap<OptionType, String> map =
        CommandLineOptions.parseOptions(new String[] {"--config=invalid.txt"}, mockRuntime);
    assertTrue(map.get(OptionType.CONFIG).contains(".chessSettings/default.chessrc"));
  }

  @Test
  public void testDefaultFileOptions() throws Exception {
    Path tempConfig = Files.createTempFile("testConfig", ".chessrc");
    String iniContent = "[Default]\nai=true\ndebug=false\nverbose=true\n";
    Files.write(tempConfig, iniContent.getBytes(StandardCharsets.UTF_8));

    Runtime mockRuntime = mock(Runtime.class);
    Map<OptionType, String> activatedOptions =
        CommandLineOptions.parseOptions(
            new String[] {"--config=" + tempConfig.toString()}, mockRuntime);

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
        CommandLineOptions.parseOptions(
            new String[] {"--config=" + tempConfig.toString(), "-b", "--time=200", "-d"},
            mockRuntime);

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
        CommandLineOptions.parseOptions(
            new String[] {"--config=" + tempConfig.toString()}, mockRuntime);

    assertTrue(activatedOptions.get(OptionType.CONFIG).contains(".chessSettings/default.chessrc"));
  }

  @Test
  public void testAIActivation() {
    Runtime mockRuntime = mock(Runtime.class);
    HashMap<OptionType, String> activatedOptions;
    // activate AI option with white
    activatedOptions =
        CommandLineOptions.parseOptions(new String[] {"--debug", "--ai=W"}, mockRuntime);
    assertTrue(outputStream.toString().contains("ai option activated"));
    assertTrue(
        activatedOptions.containsKey(OptionType.AI)
            && activatedOptions.get(OptionType.AI).equals("W"));
    outputStream.reset();
    // activate AI option with black
    activatedOptions =
        CommandLineOptions.parseOptions(new String[] {"--debug", "--ai=B"}, mockRuntime);
    assertTrue(outputStream.toString().contains("ai option activated"));
    assertTrue(
        activatedOptions.containsKey(OptionType.AI)
            && activatedOptions.get(OptionType.AI).equals("B"));
    outputStream.reset();
    // activate AI option with black and white
    activatedOptions =
        CommandLineOptions.parseOptions(new String[] {"--debug", "--ai=A"}, mockRuntime);
    assertTrue(outputStream.toString().contains("ai option activated"));
    assertTrue(
        activatedOptions.containsKey(OptionType.AI)
            && activatedOptions.get(OptionType.AI).equals("A"));

    outputStream.reset();
  }

  @Test
  public void testMissingAIActivation() {
    Runtime mockRuntime = mock(Runtime.class);
    HashMap<OptionType, String> activatedOptions;
    // activate AI mode
    activatedOptions =
        CommandLineOptions.parseOptions(new String[] {"--debug", "--ai-mode=MINIMAX"}, mockRuntime);
    assertTrue(outputStream.toString().contains("Modifying ai-mode requires 'a' argument"));
    assertFalse(activatedOptions.containsKey(OptionType.AI_MODE));
    outputStream.reset();
    // activate AI heuristic
    activatedOptions =
        CommandLineOptions.parseOptions(
            new String[] {"--debug", "--ai-heuristic=MATERIAL"}, mockRuntime);
    assertTrue(outputStream.toString().contains("Modifying ai-heuristic requires 'a' argument"));
    assertFalse(activatedOptions.containsKey(OptionType.AI_HEURISTIC));
    outputStream.reset();
    // activate AI depth
    activatedOptions =
        CommandLineOptions.parseOptions(new String[] {"--debug", "--ai-depth=5"}, mockRuntime);
    assertTrue(outputStream.toString().contains("Modifying ai-depth requires 'a' argument"));
    assertFalse(activatedOptions.containsKey(OptionType.AI_DEPTH));

    outputStream.reset();
    // activate AI time
    activatedOptions =
        CommandLineOptions.parseOptions(new String[] {"--debug", "--ai-time=5"}, mockRuntime);
    assertTrue(outputStream.toString().contains("Modifying ai-time requires 'a' argument"));
    assertFalse(activatedOptions.containsKey(OptionType.AI_TIME));

    outputStream.reset();
  }

  @Test
  public void testBlitz() throws Exception {
    Runtime mockRuntime = mock(Runtime.class);
    HashMap<OptionType, String> activatedOptions;
    activatedOptions = CommandLineOptions.parseOptions(new String[] {"-b"}, mockRuntime);
    assertTrue(activatedOptions.containsKey(OptionType.BLITZ));
  }

  @Test
  public void testBlitzWithTime() throws Exception {
    Runtime mockRuntime = mock(Runtime.class);
    HashMap<OptionType, String> activatedOptions;
    activatedOptions = CommandLineOptions.parseOptions(new String[] {"-b", "-t=10"}, mockRuntime);
    assertTrue(activatedOptions.containsKey(OptionType.BLITZ));
    assertTrue(activatedOptions.containsKey(OptionType.TIME));
    assertTrue(activatedOptions.get(OptionType.TIME).equals("10"));
  }

  @Test
  public void testTimeWithoutBlitz() throws Exception {
    Runtime mockRuntime = mock(Runtime.class);
    HashMap<OptionType, String> activatedOptions;
    activatedOptions = CommandLineOptions.parseOptions(new String[] {"-t=10"}, mockRuntime);
    assertFalse(activatedOptions.containsKey(OptionType.BLITZ));
    assertFalse(activatedOptions.containsKey(OptionType.TIME));
  }

  @Test
  public void testGetShortOptionNull() {
    // Check that the options without a short name return null when queried.
    assertNull(OptionType.AI_MODE.getShort());
    assertNull(OptionType.AI_TIME.getShort());
    assertNull(OptionType.AI_DEPTH.getShort());
    assertNull(OptionType.AI_HEURISTIC.getShort());
    assertNull(OptionType.LOAD.getShort());
    assertNull(OptionType.CONFIG.getShort());
    assertNull(OptionType.LANG.getShort());
  }

  @Test
  void testGameInitializationContestModeMissingFilePath() {

    outputStream.reset();

    Runtime mockRuntime = mock(Runtime.class);
    HashMap<OptionType, String> activatedOptions;
    activatedOptions = CommandLineOptions.parseOptions(new String[] {"--contest"}, mockRuntime);
    assertFalse(activatedOptions.containsKey(OptionType.CONTEST));

    assertTrue(
        outputStream
            .toString()
            .contains("Parsing failed.  Reason: Missing argument for option: c"));
  }

  @Test
  void testGameInitializationContestModeEmptyFilePath() {

    outputStream.reset();

    Runtime mockRuntime = mock(Runtime.class);
    HashMap<OptionType, String> activatedOptions;
    activatedOptions = CommandLineOptions.parseOptions(new String[] {"--contest="}, mockRuntime);
    assertFalse(activatedOptions.containsKey(OptionType.CONTEST));

    verify(mockRuntime).exit(1);
  }
}
