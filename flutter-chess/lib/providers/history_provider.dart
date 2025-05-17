import 'package:chess/models/history.dart';
import 'package:chess/services/websocket_service.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class HistoryProvider extends ChangeNotifier {
  History _history = History([]);

  History get history => _history;

  void updateFromSocket(
      dynamic message, BuildContext context, WebSocketService ws) {
    final decoded = History.fromJson(message, context, ws);
    _history = decoded;
    notifyListeners();
  }
}
