import 'package:flutter/material.dart';

class ChessBoardWidget extends StatelessWidget {
  final List<String> board;

  const ChessBoardWidget(this.board);

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
        index = col + (8 - 1 - row) * 8;
        final piece = board[index];

        final isLightSquare = (row + col) % 2 == 0;
        final bgColor = isLightSquare ? Colors.white : Colors.grey;

        return GestureDetector(
          onTap: () {
            print(index);
          },
          child: Container(
            color: bgColor,
            child: Center(
                child: Text(piece, style: const TextStyle(fontSize: 20))),
          ),
        );
      },
    );
  }
}
