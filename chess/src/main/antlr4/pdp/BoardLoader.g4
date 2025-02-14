grammar BoardLoader;


board : boardLine{8};

boardLine: (piece (' ')){7} piece ('\n');

player : PLAYER_COLOR ('\n');

piece : EMPTY_SQUARE
      | BLACK_BISHOP;

BLACK_BISHOP : [b];
PLAYER_COLOR : [W,B]+;
EMPTY_SQUARE : [_];


expr   : expr ('+'|'-') expr
       | expr ('*'|'/') expr
       | '(' expr ')'
       | INT
       ;

INT    : [0-9]+ ;
WS     : [ \t\r\n]+ -> skip ;
