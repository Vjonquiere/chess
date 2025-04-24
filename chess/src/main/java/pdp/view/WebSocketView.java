package pdp.view;

import java.io.IOException;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import pdp.events.EventType;
import pdp.model.Game;
import pdp.model.parsers.FileBoard;
import pdp.model.savers.FenSaver;

public class WebSocketView implements View {
  private final WebSocketSession session;

  public WebSocketView(WebSocketSession session) {
    this.session = session;
  }

  private void sendToClient(String text) {
    try {
      session.sendMessage(new TextMessage(text));
    } catch (IOException e) {
      System.err.println("Failed to send message: " + e.getMessage());
    }
  }

  public void handleMessage(String text) {
    // sendToClient(text);
    JSONObject request = new JSONObject(text);
    String type = request.getString("type");
    JSONObject response = new JSONObject();

    switch (type) {
      case "init":
        response.put("type", "update");
        response.put("valid", true);
        response.put(
            "fen", FenSaver.saveBoard(new FileBoard(Game.getInstance().getBoard(), true, null)));
        response.put("message", "Game started");
        break;

      default:
        response.put("type", "error");
        response.put("valid", false);
        response.put("message", "Unknown command");
    }

    sendToClient(response.toString());
  }

  @Override
  public void onGameEvent(EventType event) {
    sendToClient(event.toString());
  }

  @Override
  public void onErrorEvent(Exception exception) {
    sendToClient(exception.toString());
  }

  @Override
  public Thread start() {
    return null;
  }
}
