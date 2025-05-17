class GameState {
  List<String> board;
  String currentPlayer;
  List<int> greenSquares;
  List<int> redSquares;
  List<int> hintSquares;

  GameState(this.board, this.currentPlayer, this.greenSquares, this.redSquares,
      this.hintSquares);

  factory GameState.fromJson(Map<String, dynamic> data) {
    if (!data.containsKey("fen"))
      return GameState(List.filled(64, ''), 'w', [], [], []);
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
    List<int> greenSquares = [];
    List<int> redSquares = [];
    List<int> hintSquares = [];

    if (data.containsKey("greenSquares"))
      greenSquares =
          (data['greenSquares'] as List<dynamic>).map((e) => e as int).toList();

    if (data.containsKey("redSquares"))
      redSquares =
          (data['redSquares'] as List<dynamic>).map((e) => e as int).toList();

    if (data.containsKey("hintSquares"))
      hintSquares =
          (data['hintSquares'] as List<dynamic>).map((e) => e as int).toList();

    // if (data.containsKey("redSquares")) greenSquares = data["redSquares"];

    return GameState(foundPieces, data["fen"].split(" ")[1], greenSquares,
        redSquares, hintSquares);
  }
}
