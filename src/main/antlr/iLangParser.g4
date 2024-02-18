parser grammar iLangParser;

options { tokenVocab = iLangLexer; }

program : ( simpleDeclaration | routineDeclaration)*;

simpleDeclaration
   : variableDeclaration
   | typeDeclaration
   ;

variableDeclaration
   : VAR Identifier COLON type (IS expression)?
   | VAR Identifier IS expression
   ;

typeDeclaration
   : TYPE Identifier IS type
   ;

routineDeclaration
   : ROUTINE Identifier (L_PARENTHESIS parameters? R_PARENTHESIS)? (COLON type)? IS
     body
     END
   ;
parameters : parameterDeclaration ( COMMA parameterDeclaration )* ;

parameterDeclaration : Identifier COLON type ;

type
   : primitiveType
   | arrayType
   | recordType
   | Identifier
   ;

primitiveType
   : INTEGER
   | REAL
   | BOOLEAN
   ;

recordType
   : RECORD
     variableDeclaration+
     END;

arrayType : ARRAY L_BRACKET expression? R_BRACKET type;

body
   : (simpleDeclaration | statement)*
   | (simpleDeclaration | statement) SEMICOLON ((simpleDeclaration | statement) SEMICOLON )*
   ;

statement
   : assignment
   | routineCallStatement
   | whileLoop
   | forLoop
   /* | ForeachLoop */
   | ifStatement
   | returnStatement
   | BREAK
   | CONTINUE
   ;

returnStatement
   : RETURN expression?
   ;

assignment
   : modifiablePrimary ASSIGNMENT expression
   ;

routineCallStatement
   : Identifier (L_PARENTHESIS expression ( COMMA expression )* R_PARENTHESIS )?
   ;

routineCallExpression
   : Identifier L_PARENTHESIS expression ( COMMA expression )* R_PARENTHESIS
   ;

whileLoop
   : WHILE expression LOOP body END
   ;

forLoop
   : FOR Identifier range LOOP body END
   ;

range
   : IN REVERSE? expression DOUBLE_DOT expression
   ;

/*
ForeachLoop : foreach Identifier from ModifiablePrimary loop
 Body
 end
*/

ifStatement
   : IF expression THEN
     main_body=body (ELSE else_body=body)?
     END
   ;

expression
   : L_PARENTHESIS expression R_PARENTHESIS
   | expression op=(MUL | DIV | MOD) expression
   | expression op=(PLUS | MINUS) expression
   | MINUS expression
   | expression op=(EQ | NOT_EQ | LESS_EQ | LESS | GREAT_EQ | GREAT) expression
   | expression op=(AND | OR | XOR) expression
   | primary
   ;

primary
   : IntegralLiteral
   | RealLiteral
   | TRUE
   | FALSE
   | modifiablePrimary
   | routineCallExpression
   ;

modifiablePrimary
   : Identifier (DOT Identifier | L_BRACKET expression R_BRACKET )*
   ;
