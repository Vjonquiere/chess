package pdp.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.PlayMoveCommand;
import pdp.controller.commands.SaveGameCommand;
import pdp.exceptions.IllegalMoveException;
import pdp.exceptions.MoveParsingException;
import pdp.model.Game;

public class CLIView implements View {
  private boolean running = false;
  private final Map<String, CommandEntry> commands = new HashMap<>();

  public CLIView() {
    commands.put(
        "move",
        new CommandEntry(this::moveCommand, "Make a move in the game (e.g., 'move e2-e4')"));
    commands.put(
        "save",
        new CommandEntry(this::saveCommand, "Save the game to a file (e.g., 'save game.txt')"));
    commands.put("help", new CommandEntry(this::helpCommand, "Show available commands"));
    commands.put("quit", new CommandEntry(this::quitCommand, "Exit the game"));
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

  @Override
  public void onErrorEvent(Exception e) {
    if (e instanceof IllegalMoveException || e instanceof MoveParsingException) {
      System.err.println(e.getMessage());
    } else {
      System.err.println("Uncaught Error received: " + e.getMessage());
    }
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
              System.out.println("Welcome to the game!");
              System.out.println("Type 'help' for a list of available commands.");
              Scanner scanner = new Scanner(System.in);
              while (running) {
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

    CommandEntry ce = commands.get(parts[0]);
    if (ce != null) {
      Consumer<String> command = commands.get(parts[0]).action();
      command.accept(parts.length > 1 ? parts[1] : "");
    } else {
      System.out.println("Unknown command: " + input);
      this.helpCommand("");
    }
  }

  /**
   * Handles the move command.
   *
   * @param args The move in standard text notation.
   */
  private void moveCommand(String args) {
    BagOfCommands.getInstance().addCommand(new PlayMoveCommand(args));
  }

  /**
   * Handles the help command.
   *
   * @param args Unused argument
   */
  private void helpCommand(String args) {
    System.out.println("Available commands:");
    for (Map.Entry<String, CommandEntry> entry : commands.entrySet()) {
      System.out.printf("  %-10s - %s%n", entry.getKey(), entry.getValue().description());
    }
  }

  /**
   * Handles the save command.
   *
   * @param args The path to where the game should be saved.
   */
  private void saveCommand(String args) {
    BagOfCommands.getInstance().addCommand(new SaveGameCommand(args));
  }

  /**
   * Handles the quit command.
   *
   * @param args Unused argument
   */
  private void quitCommand(String args) {
    System.out.println("Quitting...");
    this.running = false;
  }

  private record CommandEntry(Consumer<String> action, String description) {}
}
