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

  @BeforeEach
  public void setUp() {
    logger = Logger.getLogger("TestLogger");

    outContent = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(outContent));

    Logging.setDebug(false);
    Logging.setVerbose(false);
    Logging.configureLogging(logger);
  }

  @AfterEach
  public void reset() {
    System.setOut(originalOut);
  }

  @Test
  public void testDebugDefault() {
    setUp();
    Logging.DEBUG(logger, "This is a debug message.");
    assertFalse(outContent.toString().contains("This is a debug message."));
    reset();
  }

  @Test
  public void testVerboseDefault() {
    setUp();
    Logging.VERBOSE(logger, "This is a verbose message.");
    assertFalse(outContent.toString().contains("This is a verbose message."));
    reset();
  }

  @Test
  public void testDebugLoggingEnabled() {
    setUp();
    Logging.setDebug(true);
    Logging.configureLogging(logger);

    Logging.DEBUG(logger, "Debug is enabled");
    assertTrue(outContent.toString().contains("Debug is enabled"));
    reset();
  }

  @Test
  public void testVerboseLoggingEnabled() {
    setUp();
    Logging.setVerbose(true);
    Logging.configureLogging(logger);

    Logging.VERBOSE(logger, "Verbose is enabled");
    assertTrue(outContent.toString().contains("Verbose is enabled"));
    reset();
  }
}
