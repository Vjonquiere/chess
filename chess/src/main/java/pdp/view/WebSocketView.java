package pdp.view;

import java.io.IOException;
import java.util.List;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.*;
import pdp.events.EventType;
import pdp.model.Game;
import pdp.model.board.Move;
import pdp.model.parsers.FileBoard;
import pdp.model.piece.Color;
import pdp.model.savers.FenSaver;
import pdp.utils.Position;

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

    System.out.println(text);

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
      case "hint":
        BagOfCommands.getInstance().addCommand(new AskHintCommand());
        break;
      case "undo":
        BagOfCommands.getInstance().addCommand(new CancelMoveCommand());
        sendToClient(sendBoard().toString());
        break;
      case "Mundo":
        int count = request.getInt("count");
        BagOfCommands.getInstance().addCommand(new UndoMultipleMoveCommand(count));
        sendToClient(sendBoard().toString());
        break;
      case "redo":
        BagOfCommands.getInstance().addCommand(new RestoreMoveCommand());
        break;
      case "draw":
        BagOfCommands.getInstance()
            .addCommand(new ProposeDrawCommand(Game.getInstance().getGameState().isWhiteTurn()));
        break;
      case "surrender":
        BagOfCommands.getInstance().addCommand(new SurrenderCommand(true));
        break;
      case "restart":
        BagOfCommands.getInstance().addCommand(new RestartCommand());
        break;
      case "history":
        response.put("type", "history");
        response.put("history", Game.getInstance().getHistory());
        sendToClient(response.toString());
        break;
      default:
        response.put("type", "error");
        response.put("valid", false);
        response.put("message", "Unknown command");
        sendToClient(response.toString());
    }
  }

  JSONObject sendBoard() {
    JSONObject response = new JSONObject();
    response.put("type", "update");
    response.put(
        "fen",
        FenSaver.saveBoard(
            new FileBoard(
                Game.getInstance().getBoard(),
                Game.getInstance().getGameState().isWhiteTurn(),
                null)));

    Move lastMove = Game.getInstance().getHistory().getCurrentMove().get().getState().getMove();
    response.put(
        "greenSquares",
        List.of(
            lastMove.getSource().y() * 8 + lastMove.getSource().x(),
            lastMove.getDest().y() * 8 + lastMove.getDest().x()));
    if (Game.getInstance()
        .getGameState()
        .getBoard()
        .isCheck(Game.getInstance().getGameState().isWhiteTurn() ? Color.WHITE : Color.BLACK)) {
      Position kingPosition =
          Game.getInstance()
              .getBoard()
              .getKing(Game.getInstance().getGameState().isWhiteTurn())
              .get(0);
      response.put("redSquares", List.of(kingPosition.y() * 8 + kingPosition.x()));
    }
    return response;
  }

  JSONObject sendHint() {
    JSONObject board = sendBoard();
    List<Integer> sq = Game.getInstance().getGameState().getHintIntegers();
    board.put("hintSquares", List.of(sq.get(1) * 8 + sq.get(0), sq.get(3) * 8 + sq.get(2)));
    return board;
  }

  @Override
  public void onGameEvent(EventType event) {
    System.out.println("Event received: " + event.toString());
    switch (event) {
      case GAME_STARTED, MOVE_PLAYED, MOVE_REDO, GAME_RESTART:
        sendToClient(sendBoard().toString());
        break;
      case WIN_BLACK, WIN_WHITE, DRAW:
        sendToClient(event.toString());
        break;
      case MOVE_HINT:
        sendToClient(sendHint().toString());
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
