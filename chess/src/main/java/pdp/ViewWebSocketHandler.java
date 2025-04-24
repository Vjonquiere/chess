package pdp;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import pdp.model.Game;
import pdp.view.WebSocketView;

public class ViewWebSocketHandler implements WebSocketHandler {

  private final ConcurrentHashMap<String, WebSocketView> sessions = new ConcurrentHashMap<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    System.out.println("WebSocket connected: " + session.getId());
    WebSocketView view = new WebSocketView(session);
    Game.getInstance().addObserver(view);
    sessions.put(session.getId(), view);
    view.start();
  }

  @Override
  public void handleMessage(WebSocketSession session, WebSocketMessage<?> message)
      throws Exception {
    WebSocketView view = sessions.get(session.getId());
    if (view != null && message.getPayload() instanceof String text) {
      try {
        view.handleMessage(text);
      } catch (Exception e) {
        view.onErrorEvent(new Exception("Error occurred: " + e.getMessage()));
      }
    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    System.err.println("Transport error: " + exception.getMessage());
    exception.printStackTrace();
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    System.out.println("WebSocket closed: " + session.getId());
    sessions.remove(session.getId());
  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }
}
