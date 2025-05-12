import 'package:chess/services/websocket_service.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import 'game_screen.dart';

class AppSettings extends StatelessWidget {
  final TextEditingController _input = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(AppLocalizations.of(context)!.settings),
      ),
      body: Column(children: [
        TextField(
          controller: _input,
          decoration: InputDecoration(
            labelText: 'Server Address',
            border: OutlineInputBorder(),
          ),
        ),
        ElevatedButton(
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => GameScreen(
                    WebSocketService(),
                    _input.text,
                  ),
                ),
              );
            },
            child: Text("Connect to server"))
      ]),
    );
  }
}
