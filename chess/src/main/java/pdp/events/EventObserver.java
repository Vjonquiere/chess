package pdp.events;

/** Interface needed for the application of the design pattern observer. */
public interface EventObserver {
  /**
   * Called when a game event occurs.
   *
   * @param event The type of event that occurred.
   */
  void onGameEvent(EventType event);

  /**
   * Called when an exception happen after an action made in the view.
   *
   * @param exception The exception that was thrown.
   */
  void onErrorEvent(Exception exception);
}
