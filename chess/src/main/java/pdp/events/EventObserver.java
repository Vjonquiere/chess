package pdp.events;

public interface EventObserver {
  void onGameEvent();

  void onErrorEvent(Exception e);
}
