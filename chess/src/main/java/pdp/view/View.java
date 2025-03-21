package pdp.view;

import pdp.events.EventObserver;

/** Common interface for the view, to be able to choose between any of the implementations. */
public interface View extends EventObserver {
  public Thread start();
}
