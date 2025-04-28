package pdp.view;

import java.io.IOException;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.*;
import pdp.events.EventType;
import pdp.model.Game;
import pdp.model.board.Move;
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
    JSONObject request = new JSONObject(text);
    String type = request.getString("type");
    JSONObject response = new JSONObject();

    switch (type) {
      case "init":
        response.put("type", "update");
        response.put(
            "fen",
            FenSaver.saveBoard(
                new FileBoard(
                    Game.getInstance().getBoard(),
                    Game.getInstance().getGameState().isWhiteTurn(),
                    null)));
        response.put("message", "Game started");
        break;
      case "move":
        String move = request.getString("move");
        Game.getInstance().playMove(Move.fromUciString(move));
        /*try {

          response.put("type", "update");
          response.put(
                  "fen", FenSaver.saveBoard(new FileBoard(Game.getInstance().getBoard(), Game.getInstance().getGameState().isWhiteTurn(), null)));
          response.put("message", "Move played");
        } catch (Exception e) {
          System.out.println("error: " + e.getMessage());
          response.put("type", "error");
        }*/
        break;
      case "undo":
        BagOfCommands.getInstance().addCommand(new CancelMoveCommand());
        /*response.put("type", "update");
        response.put(
                "fen", FenSaver.saveBoard(new FileBoard(Game.getInstance().getBoard(), Game.getInstance().getGameState().isWhiteTurn(), null)));*/
        break;
      case "redo":
        BagOfCommands.getInstance().addCommand(new RestoreMoveCommand());
        /*response.put("type", "update");
        response.put(
                "fen", FenSaver.saveBoard(new FileBoard(Game.getInstance().getBoard(), Game.getInstance().getGameState().isWhiteTurn(), null)));*/
        break;
      case "draw":
        BagOfCommands.getInstance()
            .addCommand(new ProposeDrawCommand(Game.getInstance().getGameState().isWhiteTurn()));
        /*response.put("type", "update");
        response.put(
                "fen", FenSaver.saveBoard(new FileBoard(Game.getInstance().getBoard(), Game.getInstance().getGameState().isWhiteTurn(), null)));*/
        break;
      case "surrender":
        BagOfCommands.getInstance().addCommand(new SurrenderCommand(true));
        break;
      case "restart":
        BagOfCommands.getInstance().addCommand(new RestartCommand());
        break;
      default:
        response.put("type", "error");
        response.put("valid", false);
        response.put("message", "Unknown command");
    }

    // sendToClient(response.toString());
  }

  @Override
  public void onGameEvent(EventType event) {
    System.out.println("Event received: " + event.toString());
    switch (event) {
      case GAME_STARTED, MOVE_PLAYED, MOVE_UNDO, MOVE_REDO:
        JSONObject response = new JSONObject();
        response.put("type", "update");
        response.put(
            "fen",
            FenSaver.saveBoard(
                new FileBoard(
                    Game.getInstance().getBoard(),
                    Game.getInstance().getGameState().isWhiteTurn(),
                    null)));
        sendToClient(response.toString());
        break;
      case WIN_BLACK, WIN_WHITE, DRAW:
        sendToClient(event.toString());
        break;
    }
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
