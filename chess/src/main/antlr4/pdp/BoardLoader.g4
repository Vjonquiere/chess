grammar BoardLoader;


board : (LINE_COMMENT | NEWLINE)? player fen? boardLine boardLine boardLine boardLine boardLine boardLine boardLine boardLine (LINE_COMMENT | NEWLINE)? EOF;

boardLine : piece WHITE_SPACE piece WHITE_SPACE piece WHITE_SPACE piece WHITE_SPACE piece WHITE_SPACE piece WHITE_SPACE piece WHITE_SPACE piece WHITE_SPACE? LINE_COMMENT? NEWLINE?;

player : PLAYER_COLOR WHITE_SPACE? LINE_COMMENT? NEWLINE?;

piece : WHITE_KING
      |WHITE_QUEEN
      |WHITE_BISHOP
      |WHITE_ROOK
      |WHITE_KNIGHT
      |WHITE_PAWN
      |BLACK_KING
      |BLACK_QUEEN
      |BLACK_BISHOP
      |BLACK_ROOK
      |BLACK_KNIGHT
      |BLACK_PAWN
      |PLAYER_COLOR
      |EMPTY_SQUARE;
castling : ((WHITE_KING? WHITE_QUEEN? BLACK_KING? BLACK_QUEEN?) | '-') WHITE_SPACE;
fen : castling (CHESS_SQUARE|'-')  WHITE_SPACE INT WHITE_SPACE INT WHITE_SPACE? LINE_COMMENT? NEWLINE?;

PLAYER_COLOR : 'W' | 'B';
CHESS_SQUARE : [abcdefg] [12345678];
INT : [0-9]+;
WHITE_KING : 'K';
WHITE_QUEEN : 'Q';
WHITE_BISHOP : 'B';
WHITE_ROOK : 'R';
WHITE_KNIGHT : 'N';
WHITE_PAWN : 'P';
BLACK_KING : 'k';
BLACK_QUEEN : 'q';
BLACK_BISHOP : 'b';
BLACK_ROOK : 'r';
BLACK_KNIGHT : 'n';
BLACK_PAWN : 'p';
EMPTY_SQUARE : '_';
LINE_COMMENT : '#' ~[\r\n]* -> skip ;
WHITE_SPACE : ' ';
NEWLINE : [\r\n]+ -> skip ;
