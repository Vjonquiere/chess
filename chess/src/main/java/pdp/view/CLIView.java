package pdp.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.PlayMoveCommand;
import pdp.controller.commands.SaveGameCommand;
import pdp.model.Game;

public class CLIView implements View {
  private boolean running = false;
  private final Map<String, Consumer<String>> commands = new HashMap<>();

  public CLIView() {
    commands.put("move", this::moveCommand);
    commands.put("help", this::helpCommand);
    commands.put("save", this::saveCommand);
    commands.put("quit", this::quitCommand);
  }

  /**
   * Starts the CLI view, allowing the user to input commands.
   *
   * @return The thread that was started.
   */
  @Override
  public Thread start() {
    running = true;
    return startUserInputListener();
  }

  @Override
  public void onGameEvent() {
    System.out.println(Game.getInstance().getGameRepresentation());
  }

  /**
   * Starts a new thread that listens for user input from the console.
   *
   * @return The thread that listens for user input.
   */
  private Thread startUserInputListener() {
    Thread inputThread =
        new Thread(
            () -> {
              Scanner scanner = new Scanner(System.in);
              while (running) {
                System.out.print("Enter command: ");
                String input = scanner.nextLine();
                handleUserInput(input);
              }
              scanner.close();
            });

    inputThread.setDaemon(true);
    inputThread.start();

    return inputThread;
  }

  /**
   * Handles user input from the console.
   *
   * @param input The user's input.
   */
  private void handleUserInput(String input) {
    input = input.trim().toLowerCase();
    String[] parts = input.split(" ", 2);

    Consumer<String> command = commands.get(parts[0]);
    if (command != null) {
      command.accept(parts.length > 1 ? parts[1] : "");
    } else {
      System.out.println("Unknown command: " + input);
      this.helpCommand("");
    }
  }

  /** Handles the "move" command. */
  private void moveCommand(String args) {
    BagOfCommands.getInstance().addCommand(new PlayMoveCommand(args));
  }

  private void helpCommand(String args) {
    System.out.println("Available commands:");
    for (String command : commands.keySet()) {
      System.out.println(command);
    }
  }

  private void saveCommand(String args) {
    BagOfCommands.getInstance().addCommand(new SaveGameCommand(args));
  }

  private void quitCommand(String args) {
    System.out.println("Quitting...");
    this.running = false;
  }
}
