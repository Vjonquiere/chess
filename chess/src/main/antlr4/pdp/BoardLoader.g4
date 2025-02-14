grammar BoardLoader;


board : player boardLine boardLine boardLine boardLine boardLine boardLine boardLine boardLine EOF;

boardLine : piece ' ' piece ' ' piece ' ' piece ' ' piece ' ' piece ' ' piece ' ' piece LINE_COMMENT? NEWLINE?;

player : PLAYER_COLOR LINE_COMMENT? NEWLINE?;

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
PLAYER_COLOR : 'W' | 'B';
EMPTY_SQUARE : '_';
LINE_COMMENT : '#' ~[\r\n]* -> skip ;
NEWLINE : [\r\n]+ -> skip ;
