package pdp.model;

import java.util.Optional;
import java.util.Stack;

public class History {
  Stack<HistoryState> histStack;
  Stack<HistoryState> revertStack;

  public History() {
    histStack = new Stack<>();
    revertStack = new Stack<>();
  }

  public Optional<HistoryState> getPrevious() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public Optional<HistoryState> getNext() {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }

  public boolean addMove(HistoryState state) {
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }
}
