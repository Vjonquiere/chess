package pdp.events;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {
  List<EventObserver> observers = new ArrayList<>();
  List<EventObserver> errorObservers = new ArrayList<>();

  public void addObserver(EventObserver observer) {
    observers.add(observer);
  }

  public void notifyObserver(EventObserver observer, EventType event) {
    observer.onGameEvent(event);
  }

  public void removeObserver(EventObserver observer) {
    observers.remove(observer);
  }

  public void addErrorObserver(EventObserver observer) {
    errorObservers.add(observer);
  }

  public void removeErrorObserver(EventObserver observer) {
    errorObservers.remove(observer);
  }

  public void notifyObservers(EventType event) {
    for (EventObserver observer : observers) {
      notifyObserver(observer, event);
    }
  }

  public void notifyErrorObservers(Exception e) {
    for (EventObserver observer : errorObservers) {
      observer.onErrorEvent(e);
    }
  }
}
