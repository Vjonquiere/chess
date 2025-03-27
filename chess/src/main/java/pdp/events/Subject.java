package pdp.events;

import static pdp.utils.Logging.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import pdp.model.Game;
import pdp.utils.Logging;

/** Part of design pattern observer. */
public abstract class Subject {
  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(Subject.class.getName());

  /** List of observers. */
  private final List<EventObserver> observers = new ArrayList<>();

  /** List of error observers. */
  private final List<EventObserver> errorObservers = new ArrayList<>();

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Adds an observer to the list of observers of this subject.
   *
   * @param observer The observer to be added.
   */
  public void addObserver(final EventObserver observer) {
    observers.add(observer);
  }

  /**
   * Notifies a specific observer of a game event.
   *
   * @param observer The observer to be notified.
   * @param event The type of event that occurred.
   */
  public void notifyObserver(final EventObserver observer, final EventType event) {
    debug(LOGGER, "Notifying observer " + observer + " with event " + event);
    observer.onGameEvent(event);
  }

  /**
   * Removes an observer from the list of observers of this subject.
   *
   * @param observer The observer to be removed.
   */
  public void removeObserver(final EventObserver observer) {
    observers.remove(observer);
  }

  /**
   * Adds an observer to the list of observers of this subject for error events.
   *
   * @param observer The observer to be added.
   */
  public void addErrorObserver(final EventObserver observer) {
    errorObservers.add(observer);
  }

  /**
   * Removes an observer from the list of error observers of this subject.
   *
   * @param observer The observer to be removed.
   */
  public void removeErrorObserver(final EventObserver observer) {
    errorObservers.remove(observer);
  }

  /**
   * Notifies all normal observers of a game event (not error observers).
   *
   * @param event The type of event that occurred.
   */
  public void notifyObservers(final EventType event) {
    debug(LOGGER, "Notifying observers with event " + event);
    if (!Game.getInstance().isAiExploring()) {
      for (final EventObserver observer : observers) {
        notifyObserver(observer, event);
      }
    }
  }

  /**
   * Notifies all error observers of an exception that occurred.
   *
   * @param exception The exception that occurred.
   */
  public void notifyErrorObservers(final Exception exception) {
    if (!Game.getInstance().isAiExploring()) {
      for (final EventObserver observer : errorObservers) {
        observer.onErrorEvent(exception);
      }
    }
  }

  public List<EventObserver> getObservers() {
    return this.observers;
  }

  public List<EventObserver> getErrorObservers() {
    return this.errorObservers;
  }
}
