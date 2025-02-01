package pdp.events;

import java.util.List;

public abstract class Subject {
  List<EventObserver> observers;

  public void addObserver(EventObserver observer) {
    observers.add(observer);
  }

  public void removeObserver(EventObserver observer) {
    observers.remove(observer);
  }

  public abstract void notifyObservers();
}
