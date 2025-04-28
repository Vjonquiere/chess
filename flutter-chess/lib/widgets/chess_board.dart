import 'package:chess/models/position.dart';
import 'package:flutter/material.dart';

import '../services/websocket_service.dart';

class ChessBoardWidget extends StatefulWidget {
  final List<String> board;
  final WebSocketService _socketService;

  const ChessBoardWidget(this.board, this._socketService, {super.key});

  @override
  State<StatefulWidget> createState() {
    return _ChessBoardWidget();
  }
}

class _ChessBoardWidget extends State<ChessBoardWidget> {
  Position? from;

  _ChessBoardWidget();

  @override
  Widget build(BuildContext context) {
    return GridView.builder(
        itemCount: 64,
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 8,
        ),
        itemBuilder: (context, index) {
          final row = index ~/ 8;
          final col = index % 8;
          final piece = widget.board[index];

          final isLightSquare = (row + col) % 2 == 0;
          var bgColor = isLightSquare ? Colors.white : Colors.grey;

          if (from != null && from!.x == col && from!.y == (7 - row)) {
            bgColor = Colors.blueAccent;
          }

          return GestureDetector(
            onTap: () {
              print("$col, ${7 - row} = $index");
              updateSelected(Position(col, 7 - row));
            },
            child: Container(
              color: bgColor,
              child: Center(
                child: piece == "_"
                    ? Container()
                    : piece.toLowerCase() == piece
                        ? Image.asset(
                            "assets/pieces/black/${piece.toLowerCase()}.png")
                        : Image.asset(
                            "assets/pieces/white/${piece.toLowerCase()}.png"),
                //Text(piece, style: const TextStyle(fontSize: 20)),
              ),
            ),
          );
        });
  }

  void updateSelected(Position clickedSquare) {
    setState(() {
      if (from == null) {
        from = clickedSquare;
      } else {
        sendMove(clickedSquare);
        from = null;
      }
    });
  }

  void sendMove(Position to) {
    print("Moving from (${from?.x}, ${from?.y}) to (${to.x},${to.y})");
    String move =
        "${String.fromCharCode(97 + from!.x)}${(from!.y) + 1}${String.fromCharCode(97 + to.x)}${(to.y) + 1}";
    widget._socketService.send({'type': 'move', 'move': move});
  }
}
