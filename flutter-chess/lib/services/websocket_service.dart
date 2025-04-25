import 'dart:convert';
import 'dart:io';

class WebSocketService {
  WebSocket? _socket;

  Future<void> connect(String url, void Function(dynamic) onMessage) async {
    _socket = await WebSocket.connect(url);
    _socket!.listen(onMessage);
  }

  void send(dynamic data) {
    _socket?.add(jsonEncode(data));
  }

  void dispose() {
    _socket?.close();
  }
}
