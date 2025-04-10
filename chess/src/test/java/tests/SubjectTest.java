package tests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.events.EventObserver;
import pdp.events.EventType;
import pdp.events.Subject;
import pdp.model.Game;

class SubjectTest {
  private Subject subject;
  private EventObserver observer;
  private EventObserver errorObserver;

  @BeforeAll
  public static void setUpLocale() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @BeforeEach
  public void setUp() {
    Game.initialize(false, false, null, null, null, new HashMap<>());
    subject = new Subject() {};
    observer = mock(EventObserver.class);
    errorObserver = mock(EventObserver.class);
  }

  @Test
  public void testAddAndNotifyObserver() {
    subject.addObserver(observer);
    subject.notifyObservers(EventType.GAME_STARTED);
    verify(observer, times(1)).onGameEvent(EventType.GAME_STARTED);
  }

  @Test
  public void testRemoveObserver() {
    subject.addObserver(observer);
    subject.removeObserver(observer);
    subject.notifyObservers(EventType.MOVE_PLAYED);
    verify(observer, never()).onGameEvent(EventType.MOVE_PLAYED);
  }

  @Test
  public void testAddAndNotifyErrorObserver() {
    subject.addErrorObserver(errorObserver);
    Exception exception = new Exception("Test Exception");
    subject.notifyErrorObservers(exception);
    verify(errorObserver, times(1)).onErrorEvent(exception);
  }

  @Test
  public void testRemoveErrorObserver() {
    subject.addErrorObserver(errorObserver);
    subject.removeErrorObserver(errorObserver);
    subject.notifyErrorObservers(new Exception("Test"));
    verify(errorObserver, never()).onErrorEvent(any());
  }
}
