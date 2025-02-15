package pdp.events;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {
  List<EventObserver> observers = new ArrayList<>();
  List<EventObserver> errorObservers = new ArrayList<>();

  /**
   * Adds an observer to the list of observers of this subject.
   *
   * @param observer The observer to be added.
   */
  public void addObserver(EventObserver observer) {
    observers.add(observer);
  }

  /**
   * Notifies a specific observer of a game event.
   *
   * @param observer The observer to be notified.
   * @param event The type of event that occurred.
   */
  public void notifyObserver(EventObserver observer, EventType event) {
    observer.onGameEvent(event);
  }

  /**
   * Removes an observer from the list of observers of this subject.
   *
   * @param observer The observer to be removed.
   */
  public void removeObserver(EventObserver observer) {
    observers.remove(observer);
  }

  /**
   * Adds an observer to the list of observers of this subject for error events.
   *
   * @param observer The observer to be added.
   */
  public void addErrorObserver(EventObserver observer) {
    errorObservers.add(observer);
  }

  /**
   * Removes an observer from the list of error observers of this subject.
   *
   * @param observer The observer to be removed.
   */
  public void removeErrorObserver(EventObserver observer) {
    errorObservers.remove(observer);
  }

  /**
   * Notifies all normal observers of a game event (not error observers).
   *
   * @param event The type of event that occurred.
   */
  public void notifyObservers(EventType event) {
    for (EventObserver observer : observers) {
      notifyObserver(observer, event);
    }
  }

  /**
   * Notifies all error observers of an exception that occurred.
   *
   * @param e The exception that occurred.
   */
  public void notifyErrorObservers(Exception e) {
    for (EventObserver observer : errorObservers) {
      observer.onErrorEvent(e);
    }
  }
}
