package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Logger;
import org.junit.jupiter.api.*;
import pdp.utils.Logging;

public class LoggingTest {
  private Logger logger;
  private ByteArrayOutputStream outContent;
  private PrintStream originalOut;
  private PrintStream originalErr;

  @BeforeEach
  public void setUp() {
    logger = Logger.getLogger("TestLogger");

    outContent = new ByteArrayOutputStream();
    originalOut = System.out;
    originalErr = System.err;
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(outContent));

    Logging.setDebug(false);
    Logging.setVerbose(false);
    Logging.configureLogging(logger);
  }

  @AfterEach
  public void reset() {
    Logging.setDebug(false);
    Logging.setVerbose(false);
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  @Test
  public void testDebugDefault() {
    Logging.debug(logger, "This is a debug message.");
    assertFalse(outContent.toString().contains("This is a debug message."));
  }

  @Test
  public void testVerboseDefault() {
    Logging.verbose(logger, "This is a verbose message.");
    assertFalse(outContent.toString().contains("This is a verbose message."));
  }

  @Test
  public void testDebugLoggingEnabled() {
    Logging.setDebug(true);
    Logging.configureLogging(logger);

    Logging.debug(logger, "Debug is enabled");
    assertTrue(outContent.toString().contains("Debug is enabled"));
  }

  @Test
  public void testVerboseLoggingEnabled() {
    Logging.setVerbose(true);
    Logging.configureLogging(logger);

    Logging.verbose(logger, "Verbose is enabled");
    assertTrue(outContent.toString().contains("Verbose is enabled"));
  }
}
