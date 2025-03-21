package pdp.events;

/** Interface needed for the application of the design pattern observer. */
public interface EventObserver {
  void onGameEvent(EventType event);

  void onErrorEvent(Exception e);
}
