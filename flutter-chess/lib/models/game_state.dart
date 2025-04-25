class GameState {
  List<String> board;
  String currentPlayer;

  GameState(this.board, this.currentPlayer);

  factory GameState.fromJson(Map<String, dynamic> data) {
    if (!data.containsKey("fen")) return GameState(List.filled(64, ''), 'w');
    String b = data["fen"].split(" ").first;
    List<String> foundPieces = [];
    for (int i = 0; i < b.length; i++) {
      if (b[i] == "/") continue;
      int? num = int.tryParse(b[i]);
      if (num != null) {
        for (int j = 0; j < num; j++) {
          foundPieces.add("_");
        }
      } else {
        foundPieces.add(b[i]);
      }
    }
    return GameState(foundPieces, 'w');
  }
}
