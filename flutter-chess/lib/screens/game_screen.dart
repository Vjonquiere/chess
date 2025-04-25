import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/websocket_service.dart';
import '../providers/game_provider.dart';
import 'dart:convert';

import '../widgets/chess_board.dart';

class GameScreen extends StatefulWidget {
  @override
  State<GameScreen> createState() => _GameScreenState();
}

class _GameScreenState extends State<GameScreen> {
  final _socketService = WebSocketService();

  @override
  void initState() {
    super.initState();
    _initWebSocket();
  }

  void _initWebSocket() async {
    await _socketService.connect(
      'ws://localhost:8080/ui',
      (message) {
        print("Socket received: " + message);
        try {
          Map<String, dynamic> decoded = jsonDecode(message);
          if (!decoded.containsKey("type"))
            throw Exception("Socket sent an update without type");
          switch (decoded["type"]) {
            case "update":
              Provider.of<GameProvider>(context, listen: false)
                  .updateFromSocket(decoded);
              break;
            default:
              print("unknown type");
          }
        } on Exception catch (_) {}
      },
    );
  }

  @override
  void dispose() {
    _socketService.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final gameState = Provider.of<GameProvider>(context).gameState;

    return Scaffold(
        appBar: AppBar(title: Text("Chess Game - ${gameState.currentPlayer}")),
        body: Column(
          children: [
            SizedBox(
              width: MediaQuery.of(context).size.height / 1.25,
              height: MediaQuery.of(context).size.height / 1.25,
              child: ChessBoardWidget(gameState.board),
            ),
            ElevatedButton(
              onPressed: () {
                _socketService.send({'type': 'init'});
              },
              child: Text("initialise board"),
            )
          ],
        ));
  }
}
