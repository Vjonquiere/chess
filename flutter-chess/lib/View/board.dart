import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class Board extends StatelessWidget{

  int boardSize = 64;

  @override
  Widget build(BuildContext context) {
    return GridView.count(crossAxisCount: ,),
      itemCount: boardSize * boardSize,
      itemBuilder: (context, index) {
        final int row = index ~/ boardSize;
        final int col = index % boardSize;
        final bool isDark = (row + col) % 2 == 1;

        return Container(
          color: isDark ? Colors.brown : Colors.white,
        );
      },
    );
    throw UnimplementedError();
  }

}