package tests;

import static com.github.stefanbirkner.systemlambda.SystemLambda.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import pdp.utils.CLIOptions;

public class CLIOptionsTest {
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

  @Test
  public void testHelp() {

    System.setOut(new PrintStream(outputStream));
    String expected =
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
            + " -h,--help                       Print this message and exit\n"
            + " -t,--time <TIME>                Specify time per round for blitz mode\n"
            + "                                 (default 30min)\n"
            + " -V,--version                    Print the version information and exit\n"
            + " -v,--verbose                    Display more information\n";

    Runtime mockRuntime = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"--help"}, mockRuntime);
    assertEquals(expected.trim(), outputStream.toString().trim());
    outputStream.reset();
    verify(mockRuntime).exit(0);

    Runtime mockRuntime2 = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"-h"}, mockRuntime2);
    assertEquals(expected.trim(), outputStream.toString().trim());
    outputStream.reset();
    verify(mockRuntime2).exit(0);
  }

  @Test
  public void testVersion() throws Exception {
    System.setOut(new PrintStream(outputStream));
    final Properties properties = new Properties();
    properties.load(CLIOptions.class.getClassLoader().getResourceAsStream(".properties"));
    String expected = "Version: "+ properties.getProperty("version");
    Runtime mockRuntime = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"--version"}, mockRuntime);
    assertEquals(expected.trim(), outputStream.toString().trim());
    outputStream.reset();
    verify(mockRuntime).exit(0);

    Runtime mockRuntime2 = mock(Runtime.class);
    CLIOptions.parseOptions(new String[] {"-V"}, mockRuntime2);
    assertEquals(expected.trim(), outputStream.toString().trim());
    outputStream.reset();
    verify(mockRuntime2).exit(0);
    outputStream.reset();
  }
}
