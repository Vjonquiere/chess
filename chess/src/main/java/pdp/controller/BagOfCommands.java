package pdp.controller;

import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.concurrent.Task;
import pdp.model.Game;

public class BagOfCommands {
  private static BagOfCommands instance;
  private ConcurrentLinkedQueue<Command> commands = new ConcurrentLinkedQueue<>();
  private Game model;
  private GameController controller;
  private boolean isRunning;

  /**
   * private constructor needed for design pattern singleton Nothing done inside because every field
   * is initialized outside
   */
  private BagOfCommands() {}

  private void processCommands() {
    isRunning = true;
    Task<Void> task =
        new Task<>() {
          @Override
          protected Void call() throws Exception {
            while (!commands.isEmpty()) {
              Command command = commands.poll();
              if (command != null) {
                command.execute(model, controller);
              }
              Thread.sleep(1); // Delay between commands
            }
            return null;
          }

          @Override
          protected void succeeded() {
            isRunning = false;
            // Check for more commands after finishing the current batch
            if (!commands.isEmpty()) {
              processCommands();
            }
            super.succeeded();
          }

          @Override
          protected void failed() {
            isRunning = false;
            super.failed();
          }
        };

    new Thread(task).start();
  }

  public void executeFirst() {
    Command command = this.commands.poll();
    if (command == null) {
      return;
    }
    command.execute(this.model, this.controller);
  }

  public void executeAll() {
    for (Command command : this.commands) {
      command.execute(this.model, this.controller);
    }
  }

  public void addCommand(Command command) {
    this.commands.add(command);
    if (!this.isRunning) {
      processCommands();
    }
  }

  public void setModel(Game model) {
    this.model = model;
  }

  public void setController(GameController controller) {
    this.controller = controller;
  }

  public static BagOfCommands getInstance() {
    if (instance == null) {
      instance = new BagOfCommands();
    }
    return instance;
  }
}
