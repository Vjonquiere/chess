package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pdp.utils.Timer;

class TimerTest {

  @BeforeAll
  public static void setUpLocale() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @Test
  void testTimerCallback() throws InterruptedException {
    Runnable callback = Mockito.mock(Runnable.class);
    Timer timer = new Timer(50, callback);
    timer.start();

    Thread.sleep(80);

    verify(callback, times(1)).run();
    assertEquals(0, timer.getTimeRemaining());
  }

  @Test
  void testStop() throws InterruptedException {
    Runnable callback = Mockito.mock(Runnable.class);
    Timer timer = new Timer(200, callback);
    timer.start();

    Thread.sleep(50);
    timer.stop();

    Thread.sleep(200);
    verify(callback, never()).run();
    assertTrue(timer.getTimeRemaining() > 0 && timer.getTimeRemaining() < 200);
  }

  @Test
  void testRunningTimeRemaining() throws InterruptedException {
    Runnable callback = Mockito.mock(Runnable.class);
    Timer timer = new Timer(200, callback);
    timer.start();
    Thread.sleep(50);
    assertTrue(timer.getTimeRemaining() > 0 && timer.getTimeRemaining() < 200);
    timer.stop();
    verify(callback, never()).run();
  }

  @Test
  void testGetTimeRemainingString() {
    Timer timer = new Timer(55000, null);
    String timeString = timer.getTimeRemainingString();
    assertEquals("00:55", timeString);
  }

  @Test
  void testGetTimeRemainingStringWithHours() {
    Timer timer = new Timer(4530000, null);
    String timeString = timer.getTimeRemainingString();
    assertEquals("01:15:30", timeString);
  }
}
