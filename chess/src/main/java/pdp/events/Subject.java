package pdp.events;

import java.util.List;

public abstract class Subject {
  List<EventObserver> observers;
  List<EventObserver> errorObservers;

  public void addObserver(EventObserver observer) {
    observers.add(observer);
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

  public void notifyObservers() {
    for (EventObserver observer : observers) {
      observer.onGameEvent();
    }
  }

  public void notifyErrorObservers(Exception e) {
    for (EventObserver observer : errorObservers) {
      observer.onErrorEvent(e);
    }
  }
}
