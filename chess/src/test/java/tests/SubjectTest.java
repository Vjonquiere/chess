package tests;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.events.EventObserver;
import pdp.events.EventType;
import pdp.events.Subject;

class SubjectTest {
  private Subject subject;
  private EventObserver observer;
  private EventObserver errorObserver;

  @BeforeEach
  public void setUp() {
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
