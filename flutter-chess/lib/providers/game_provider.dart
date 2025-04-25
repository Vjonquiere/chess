import 'package:flutter/foundation.dart';
import '../models/game_state.dart';

class GameProvider extends ChangeNotifier {
  GameState _gameState = GameState(
    List.filled(64, ''),
    'w',
  );

  GameState get gameState => _gameState;

  void updateFromSocket(dynamic message) {
    final decoded = GameState.fromJson(message);
    _gameState = decoded;
    notifyListeners();
  }
}
