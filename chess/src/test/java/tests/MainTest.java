package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import pdp.Main;

public class MainTest {

  @Test
  public void voidTest() {
    assertEquals(1, 1);
  }

  @Test
  public void equalsA() {
    assertEquals(Main.returnsA(), "A");
  }
}
