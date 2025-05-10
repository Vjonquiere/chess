import 'dart:math';

import 'package:chess/screens/app_settings.dart';
import 'package:chess/screens/end_screen.dart';
import 'package:chess/screens/game_config_screen.dart';
import 'package:chess/widgets/chess_infos.dart';
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
  final TextEditingController _input = TextEditingController();
  String WsAddress = 'ws://localhost:8080/ui';

  @override
  void initState() {
    super.initState();
    _initWebSocket();
  }

  void _initWebSocket() async {
    await _socketService.connect(
      WsAddress,
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
        } on Exception catch (_) {
          if (message == "DRAW" ||
              message == "WIN_WHITE" ||
              message == "WIN_BLACK") {
            Navigator.push(context,
                MaterialPageRoute(builder: (context) => EndScreen(message)));
          }
        }
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
    final screenSize = MediaQuery.of(context).size;
    final ratio = screenSize.width > screenSize.height;
    double size = 1;
    if (ratio) {
      size = screenSize.height / 1.10;
    } else {
      size = screenSize.width / 1.30;
    }
    return Scaffold(
      drawer: Drawer(child:
      ListView(
        padding: EdgeInsets.zero,
        children: [
          /*const DrawerHeader(
            child: Text('Drawer Header'),
          ),*/
          ListTile(
            leading: Icon(Icons.sports_esports),
            title: const Text('Game'),
            onTap: () {
            },
          ),
          ListTile(
            leading: Icon(Icons.add),
            title: const Text('New Game'),
            onTap: () {
              Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (context) =>
                          GameConfigScreen(_socketService)));
            },
          ),
          ListTile(
            leading: Icon(Icons.replay),
            title: const Text('Restart'),
            onTap: () {
            },
          ),
          ListTile(
            leading: Icon(Icons.info, color: Colors.blue,),
            title: const Text('Infos'),
            onTap: () {
            },
          ),
        ],
      ),
      ),
        appBar: AppBar(
         // leading: IconButton(onPressed: () {Scaffold.of(context).openDrawer();}, icon: Icon(Icons.info)),
          title: Text("Chess Game - ${gameState.currentPlayer}"),
          actions: [
            IconButton(
                onPressed: () {
                  Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) =>
                              AppSettings()));
                },
                icon: Icon(Icons.settings))
          ],
        ),
        body: Row(
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            SizedBox(
              width: size,
              height: size,
              child: ChessBoardWidget(gameState.board, _socketService),
            ),
            //Padding(
            //padding: EdgeInsets.symmetric(vertical: 0.0, horizontal: 20.0)),
            Flexible(
              child: Column(
                children: [
                  ChessInfos(_socketService),
                  // SizedBox(height: 20),
                  TextField(
                    controller: _input,
                    decoration: InputDecoration(
                      labelText: 'Server Address',
                      border: OutlineInputBorder(),
                    ),
                  ),
                  ElevatedButton(
                      onPressed: () {
                        setState(() {
                          WsAddress = _input.text;
                          _socketService.dispose();
                          _initWebSocket();
                        });
                      },
                      child: Text("Connect to server"))
                ],
              ),
            )
          ],
        ));
  }
}
