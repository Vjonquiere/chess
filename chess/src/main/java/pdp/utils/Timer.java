package pdp.utils;

import static pdp.utils.Logging.DEBUG;

import java.util.logging.Logger;

public class Timer implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(Timer.class.getName());
  private long duration; // In milliseconds
  private long remaining;
  private boolean running = false;
  private Thread thread;
  private Runnable timeOverCallback;
  private long startTime;

  static {
    Logging.configureLogging(LOGGER);
  }

  public Timer(long time, Runnable timeOverCallback) {
    this.duration = time;
    this.remaining = this.duration;
    this.timeOverCallback = timeOverCallback;
  }

  public Timer(long time) {
    this.duration = time;
    this.remaining = this.duration;
  }

  /**
   * Sets the callback to be executed when the timer reaches zero.
   *
   * @param timeOverCallback The Runnable to be executed when the time is over.
   */
  public void setCallback(Runnable timeOverCallback) {
    this.timeOverCallback = timeOverCallback;
  }

  /**
   * Run the timer. This method is run in a separate thread. When remaining time is 0, the
   * timeOverCallback is called.
   */
  @Override
  public void run() {
    this.running = true;
    this.startTime = System.currentTimeMillis();
    try {
      Thread.sleep(this.duration);
      this.remaining = 0;
      if (running && this.timeOverCallback != null) {
        this.timeOverCallback.run();
      }
    } catch (Exception e) {
      // System.err.println(e.getMessage());
    }
    running = false;
  }

  /** Creates nd starts the timer thread */
  public synchronized void start() {
    if (!running) {
      this.remaining = this.duration;
      this.thread = new Thread(this);
      this.thread.start();
    }
  }

  /** Stops the timer, updates the remaining time, and interrupts the timer thread if running. */
  public synchronized void stop() {
    this.running = false;
    this.remaining -= System.currentTimeMillis() - this.startTime;
    DEBUG(LOGGER, "Remaining time at stop: " + this.remaining);
    if (this.thread != null) {
      this.thread.interrupt();
    }
  }

  /**
   * Returns the time remaining on the timer.
   *
   * @return the time remaining in milliseconds.
   */
  public long getTimeRemaining() {
    if (running) {
      long elapsed = System.currentTimeMillis() - this.startTime;
      return Math.max(this.remaining - elapsed, 0);
    } else {
      return this.remaining;
    }
  }

  /**
   * Returns a string representation of the time remaining on the timer. If the duration is less
   * than an hour, the format is mm:ss. If the duration is an hour or more, the format is hh:mm:ss.
   *
   * @return A string representation of the time remaining on the timer.
   */
  public String getTimeRemainingString() {
    long totalSeconds = getTimeRemaining() / 1000;

    if (this.duration < 3600000) {
      long minutes = totalSeconds / 60;
      long seconds = totalSeconds % 60;
      return String.format("%02d:%02d", minutes, seconds);
    } else {
      long hours = totalSeconds / 3600;
      long minutes = (totalSeconds % 3600) / 60;
      long seconds = totalSeconds % 60;
      return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
  }
}
