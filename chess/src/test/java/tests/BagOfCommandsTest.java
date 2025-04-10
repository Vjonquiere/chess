package tests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.lang.reflect.Constructor;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import pdp.controller.BagOfCommands;
import pdp.controller.Command;
import pdp.controller.GameController;
import pdp.model.Game;

public class BagOfCommandsTest {
  private GameController mockController = Mockito.mock(GameController.class);
  private Game mockGame = Mockito.mock(Game.class);

  @BeforeAll
  public static void setUpLocale() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @BeforeEach
  void setUp() throws Exception {
    // Reset the BagOfCommands instance
    Constructor<BagOfCommands> constructor = BagOfCommands.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    BagOfCommands newInstance = constructor.newInstance();
    newInstance.setModel(mockGame);
    newInstance.setController(mockController);
    BagOfCommands.setInstance(newInstance);
  }

  private void waitCommands(BagOfCommands bagOfCommands, long timeoutMs)
      throws InterruptedException {
    long start = System.currentTimeMillis();
    while ((bagOfCommands.isRunning()) && System.currentTimeMillis() - start < timeoutMs) {
      TimeUnit.MILLISECONDS.sleep(10);
    }
  }

  @Test
  void testCommandReportsError() throws InterruptedException {
    BagOfCommands bagOfCommands = BagOfCommands.getInstance();

    RuntimeException exception = new RuntimeException();

    Command errorCommand = Mockito.mock(Command.class);
    when(errorCommand.execute(any(Game.class), any(GameController.class)))
        .thenReturn(Optional.of(exception));

    bagOfCommands.addCommand(errorCommand);

    waitCommands(bagOfCommands, 500);

    verify(mockController).onErrorEvent(exception);
  }

  @Test
  void testCommandsOrder() throws InterruptedException {
    BagOfCommands bagOfCommands = BagOfCommands.getInstance();

    Command command1 = Mockito.mock(Command.class);
    Command command2 = Mockito.mock(Command.class);
    Command command3 = Mockito.mock(Command.class);

    when(command1.execute(any(Game.class), any(GameController.class))).thenReturn(Optional.empty());
    when(command2.execute(any(Game.class), any(GameController.class))).thenReturn(Optional.empty());
    when(command3.execute(any(Game.class), any(GameController.class))).thenReturn(Optional.empty());

    bagOfCommands.addCommand(command1);
    bagOfCommands.addCommand(command2);
    bagOfCommands.addCommand(command3);

    waitCommands(bagOfCommands, 500);

    InOrder inOrder = inOrder(command1, command2, command3);
    inOrder.verify(command1).execute(any(Game.class), any(GameController.class));
    inOrder.verify(command2).execute(any(Game.class), any(GameController.class));
    inOrder.verify(command3).execute(any(Game.class), any(GameController.class));
  }
}
