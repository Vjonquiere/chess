import 'dart:convert';
import 'package:web_socket_channel/web_socket_channel.dart';

class WebSocketService {
  WebSocketChannel? _channel;

  Future<void> connect(String url, void Function(dynamic) onMessage) async {
    _channel = WebSocketChannel.connect(Uri.parse(url));
    _channel!.stream.listen(onMessage);
  }

  void send(dynamic data) {
    if (_channel != null) {
      _channel!.sink.add(jsonEncode(data));
    }
  }

  void dispose() {
    _channel?.sink.close();
  }
}
