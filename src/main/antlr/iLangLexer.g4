lexer grammar iLangLexer;

VAR : 'var';

IS : 'is';

TYPE : 'type';

ROUTINE : 'routine';

RETURN : 'return';

BREAK : 'break';

CONTINUE : 'CONTINUE';

END : 'end';

COLON : ':';

SEMICOLON : ';';

L_PARENTHESIS : '(';

R_PARENTHESIS : ')';

L_BRACKET : '[';

R_BRACKET : ']';

COMMA : ',';

RECORD : 'record';

ARRAY : 'array';

ASSIGNMENT : ':=';

LOOP : 'loop';

FOR : 'for';

WHILE : 'while';

IN : 'in';

REVERSE : 'reverse';

DOUBLE_DOT : '..';

DOT : '.';

IF : 'if';

THEN : 'then';

ELSE : 'else';

LESS : '<';

GREAT : '>';

LESS_EQ : '<=';

GREAT_EQ : '>=';

EQ : '=';

NOT_EQ : '/=';

MUL : '*';

DIV : '/';

MOD : '%';

PLUS : '+';

MINUS : '-';

TRUE : 'true';

FALSE : 'false';

INTEGER : 'integer';

REAL : 'real';

BOOLEAN : 'boolean';

AND : 'and';

OR : 'or';

XOR : 'xor';

Identifier : LETTER+ [A-Za-z_0-9]*;

IntegralLiteral : DIGIT+;

RealLiteral : DIGIT+ DOT DIGIT+;

WHITESPACE : [ \t\r\n]+    -> channel(HIDDEN);
LINE_COMMENT : '//' ~[\r\n]* -> channel(HIDDEN);

fragment LETTER : [A-Za-z];
fragment DIGIT : [0-9];
