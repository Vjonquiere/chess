import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class AppSettings extends StatelessWidget {
  final TextEditingController _input = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Settings"),
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
              /*setState(() {
                  WsAddress = _input.text;
                  _socketService.dispose();
                  _initWebSocket();
                });*/
            },
            child: Text("Connect to server"))
      ]),
    );
  }
}
