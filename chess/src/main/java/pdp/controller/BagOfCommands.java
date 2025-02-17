package pdp.controller;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import pdp.model.Game;
import pdp.utils.Logging;

public class BagOfCommands {
  private static final Logger LOGGER = Logger.getLogger(BagOfCommands.class.getName());
  private static BagOfCommands instance;
  private ConcurrentLinkedQueue<Command> commands = new ConcurrentLinkedQueue<>();
  private Game model;
  private GameController controller;
  private boolean isRunning;

  /**
   * private constructor needed for design pattern singleton Nothing done inside because every field
   * is initialized outside
   */
  private BagOfCommands() {
    Logging.configureLogging(LOGGER);
  }

  /** Starts a new thread to process all commands in the queue. */
  private void processCommands() {
    isRunning = true;
    Thread thread =
        new Thread(
            () -> {
              try {
                while (!commands.isEmpty()) {
                  Command command = commands.poll();
                  if (command != null) {
                    Optional<Exception> exception = command.execute(model, controller);
                    if (exception.isPresent() && controller != null) {
                      controller.onErrorEvent(exception.get());
                    }
                  }
                  Thread.sleep(1); // Prevent CPU overuse
                }
              } catch (Exception e) {
                System.out.println("Error in processCommands: " + e.getMessage());
                e.printStackTrace();
              } finally {
                isRunning = false;
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
  public void addCommand(Command command) {
    this.commands.add(command);
    if (!this.isRunning) {
      processCommands();
    }
  }

  /**
   * Sets the game model linked to the bag of commands.
   *
   * @param model The game model to set.
   */
  public void setModel(Game model) {
    this.model = model;
  }

  /**
   * Sets the game controller linked to the bag of commands.
   *
   * @param controller The game controller to set.
   */
  public void setController(GameController controller) {
    this.controller = controller;
  }

  /**
   * Gets the single instance of BagOfCommands and creats it if it doesn't exist.
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
   * Sets the instance of BagOfCommands.
   *
   * @param instance The instance to be set as the singleton BagOfCommands.
   */
  public static void setInstance(BagOfCommands instance) {
    BagOfCommands.instance = instance;
  }
}
