import 'package:flutter/material.dart';

import '../services/websocket_service.dart';

class History {
  final List<Card> historyNodes;

  History(this.historyNodes);

  factory History.fromJson(
      Map<String, dynamic> data, BuildContext context, WebSocketService ws) {
    if (!data.containsKey("history")) return History([]);
    if (data["history"] == "") return History([]);
    String historyString = data["history"];
    List<String> moves = historyString.split("\n");
    List<Card> nodes = [];
    moves.asMap().forEach((i, move) {
      nodes.add(Card(
        elevation: 0.0,
        shadowColor: Colors.transparent,
        margin: EdgeInsets.zero,
        child: ListTile(
          title: Text(move),
          tileColor: i % 2 == 0 ? Colors.white54 : Colors.white10,
          onTap: () {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text('Long press the tile to return to this move'),
                duration: Duration(seconds: 2),
              ),
            );
            print("tile touched: ${moves.length - i}");
          },
          onLongPress: () {
            ws.send({"type": "Mundo", "count": moves.length - i});
            print("try to undo ${moves.length - i} moves");
          },
        ),
      ));
    });

    return History(nodes);
  }
}
