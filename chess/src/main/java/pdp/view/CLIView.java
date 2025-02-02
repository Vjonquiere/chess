package pdp.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.PlayMoveCommand;
import pdp.model.Game;

public class CLIView implements View {
  private boolean running = false;
  private final Map<String, Consumer<String>> commands = new HashMap<>();

  public CLIView() {
    commands.put("move", this::moveCommand);
  }

  @Override
  public Thread start() {
    running = true;
    return startUserInputListener();
  }

  @Override
  public void onGameEvent() {
    System.out.println(Game.getInstance().getGameRepresentation());
  }

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

  private void handleUserInput(String input) {
    input = input.trim().toLowerCase();
    String[] parts = input.split(" ", 2);

    Consumer<String> command = commands.get(parts[0]);
    if (command != null) {
      command.accept(parts.length > 1 ? parts[1] : "");
    } else {
      System.out.println("Unknown command: " + input);
    }
  }

  private void moveCommand(String args) {
    BagOfCommands.getInstance().addCommand(new PlayMoveCommand(args));
  }
}
