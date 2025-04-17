package pdp.controller;

import static pdp.utils.Logging.error;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import pdp.model.Game;
import pdp.utils.Logging;

/** Variation of the design pattern command to execute several commands at once. */
public final class BagOfCommands {
  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(BagOfCommands.class.getName());

  /** Instance of the class, design pattern singleton. */
  private static BagOfCommands instance;

  /** Queue containing the commands to execute. */
  private final ConcurrentLinkedQueue<Command> commands = new ConcurrentLinkedQueue<>();

  /** Model of the MVC app. */
  private Game model;

  /** Controller of the MVC app. */
  private GameController controller;

  /** Indicates whether the bag of commands is currently executing commands. */
  private boolean bagRunning;

  /**
   * Private constructor needed for design pattern singleton Nothing done inside because every field
   * is initialized outside.
   */
  private BagOfCommands() {
    Logging.configureLogging(LOGGER);
  }

  /** Starts a new thread to process all commands in the queue. */
  private void processCommands() {
    bagRunning = true;
    final Thread thread =
        new Thread(
            () -> {
              try {
                while (!commands.isEmpty()) {
                  final Command command = commands.poll();
                  if (command != null) {
                    final Optional<Exception> exception = command.execute(model, controller);
                    if (exception.isPresent() && controller != null) {
                      controller.onErrorEvent(exception.get());
                    }
                  }
                  Thread.sleep(1); // Prevent CPU overuse
                }
              } catch (InterruptedException e) {
                error("Error in processCommands: " + e.getMessage());
              } finally {
                bagRunning = false;
                // If more commands are added while processing, restart the thread
                if (!commands.isEmpty()) {
                  processCommands();
                }
              }
            });

    thread.setDaemon(true);
    thread.start();
  }

  /**
   * Adds a command to the bag of commands and starts the bag of commands if not already running.
   *
   * @param command The command to add.
   */
  public void addCommand(final Command command) {
    this.commands.add(command);
    if (!this.bagRunning) {
      processCommands();
    }
  }

  /**
   * Sets the game model linked to the bag of commands.
   *
   * @param model The game model to set.
   */
  public void setModel(final Game model) {
    this.model = model;
    if (this.controller != null) {
      this.controller.setModel(model);
    }
  }

  /**
   * Sets the game controller linked to the bag of commands.
   *
   * @param controller The game controller to set.
   */
  public void setController(final GameController controller) {
    this.controller = controller;
  }

  /**
   * Indicates whether the bag of commands is running.
   *
   * @return isRunning field
   */
  public boolean isRunning() {
    return this.bagRunning;
  }

  /**
   * Gets the single instance of BagOfCommands and creates it if it doesn't exist.
   *
   * @return The single instance of BagOfCommands.
   */
  public static BagOfCommands getInstance() {
    if (instance == null) {
      instance = new BagOfCommands();
    }
    return instance;
  }

  /**
   * Sets the instance of BagOfCommands. Used for tests.
   *
   * @param instance The instance to be set as the singleton BagOfCommands.
   */
  public static void setInstance(final BagOfCommands instance) {
    BagOfCommands.instance = instance;
  }
}
