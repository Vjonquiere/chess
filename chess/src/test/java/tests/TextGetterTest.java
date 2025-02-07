package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdp.utils.TextGetter;

public class TextGetterTest {

  @BeforeEach
  public void setUp() {
    TextGetter.setLocale("en");
  }

  @AfterEach
  public void tearDown() {
    TextGetter.setLocale("en");
  }

  @Test
  public void testTitle() {
    /* Display the title in english */
    assertEquals("Chess game", TextGetter.getText("title"));

    /* Display the title in French */
    TextGetter.setLocale("fr");
    assertEquals("Jeu d'échecs", TextGetter.getText("title"));

    /* Takes the default locale for unrecognized language */
    TextGetter.setLocale("ru");
    assertEquals("Chess game", TextGetter.getText("title"));
  }

  @Test
  public void testParametrizedString() {
    /* Display a text with parameters*/
    String expected = "Test integer 1, string hello and float 1.7\n";
    assertEquals(expected.trim(), TextGetter.getText("parametrizedString", 1.7, 1, "hello").trim());
  }
}
