import 'package:chess/services/websocket_service.dart';
import 'package:flutter/material.dart';

class ChessInfos extends StatelessWidget {
  final WebSocketService _socketService;

  const ChessInfos(this._socketService, {super.key});

  @override
  Widget build(BuildContext context) {
    return Expanded(
        child: Padding(
      padding: EdgeInsets.symmetric(horizontal: 8.0),
      child: Column(children: [
        Container(
            decoration: BoxDecoration(
                border: Border.all(
                    width: 2.0,
                    style: BorderStyle.solid,
                    color: Theme.of(context).primaryColor),
                borderRadius: const BorderRadius.all(Radius.circular(5.0))),
            child: const Column(
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
            )),
        Padding(padding: EdgeInsets.symmetric(vertical: 5.0)),
        Flexible(
            child: Container(
                decoration: BoxDecoration(
                    border: Border.all(
                        width: 2.0,
                        style: BorderStyle.solid,
                        color: Theme.of(context).primaryColor),
                    borderRadius: BorderRadius.all(Radius.circular(5.0))),
                child: ListView(
                  children:
                    List.filled(150, ListTile(title: Text("e2-e4"),)),
                ))),
        Padding(padding: EdgeInsets.symmetric(vertical: 5.0)),
        Flexible(
            child: Container(padding: EdgeInsets.symmetric(vertical: 5.0),decoration: BoxDecoration(
                border: Border.all(
                    width: 2.0,
                    style: BorderStyle.solid,
                    color: Theme.of(context).primaryColor),
                borderRadius: BorderRadius.all(Radius.circular(5.0))),
                child: Wrap(
                  runSpacing: 8.0,
          spacing: 8.0,
          alignment: WrapAlignment.center,
          children: [
            ElevatedButton(
              onPressed: () {
                _socketService.send({'type': 'init'});
              },
              child: Text("initialise board"),
            ),
            FilledButton(
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
          ],
        )))
      ]),
    ));
  }
}
