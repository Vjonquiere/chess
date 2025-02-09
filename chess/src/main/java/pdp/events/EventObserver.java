package pdp.events;

public interface EventObserver {
  void onGameEvent(EventType event);

  void onErrorEvent(Exception e);
}
