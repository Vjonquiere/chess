package pdp.view.gui;

import javafx.scene.control.Button;

public class CustomButton extends Button {

  public CustomButton(String name) {
    super(name);
  }

  public CustomButton(String name, int width) {
    super(name);
    this.setMinWidth(100);
  }
}
