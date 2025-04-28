import 'package:chess/services/websocket_service.dart';
import 'package:flutter/material.dart';

class ChessInfos extends StatelessWidget {
  final WebSocketService _socketService;

  const ChessInfos(this._socketService, {super.key});

  @override
  Widget build(BuildContext context) {
    return Expanded(
        child: Column(children: [
      const Column(
        children: [
          Row(
            children: [
              Icon(
                Icons.person_rounded,
                size: 50.0,
              ),
              Text(
                "White player",
                style: TextStyle(fontSize: 25.0),
              )
            ],
          ),
          Row(
            children: [
              Icon(
                Icons.person_rounded,
                size: 50.0,
              ),
              Text(
                "Black player",
                style: TextStyle(fontSize: 25.0),
              )
            ],
          ),
        ],
      ),
      ElevatedButton(
        onPressed: () {
          _socketService.send({'type': 'init'});
        },
        child: Text("initialise board"),
      ),
      TextButton(
          onPressed: () {
            _socketService.send({'type': 'undo'});
          },
          child: Text("Undo")),
      TextButton(
          onPressed: () {
            _socketService.send({'type': 'redo'});
          },
          child: Text("Redo")),
      TextButton(
          onPressed: () {
            _socketService.send({'type': 'draw'});
          },
          child: Text("Draw")),
      TextButton(
          onPressed: () {
            _socketService.send({'type': 'resign'});
          },
          child: Text("Resign")),
      TextButton(
          onPressed: () {
            _socketService.send({'type': 'undraw'});
          },
          child: Text("Undraw")),
      TextButton(
          onPressed: () {
            _socketService.send({'type': 'restart'});
          },
          child: Text("Restart")),
    ]));
  }
}
