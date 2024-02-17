grammar iLangParser;

import iLangLexer;

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

parameterDeclaration : Identifier COLON Identifier ;

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

arrayType : ARRAY L_BRACKET expression R_BRACKET type;

body : (simpleDeclaration | statement);

statement
   : assignment
   | routineCall
   | whileLoop
   | forLoop
   | /* ForeachLoop */
   | ifStatement
   ;

assignment
   : modifiablePrimary ASSIGNMENT expression
   ;

routineCall
   : Identifier (L_PARENTHESIS expression ( COMMA expression )* R_PARENTHESIS )?
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

expression : relation (op=(AND | OR | XOR) relation)?;

relation
   : simple (op=(LESS | LESS_EQ | GREAT | GREAT_EQ | EQ | NOT_EQ) simple)?
   ;

simple
   : factor (op=(MUL | DIV | MOD) factor)?
   ;

factor
   : summand (op=(PLUS | MINUS) summand)?
   ;

summand
   : primary
   | L_PARENTHESIS expression R_PARENTHESIS
   ;

primary
   : IntegralLiteral
   | RealLiteral
   | TRUE
   | FALSE
   | modifiablePrimary
   ;

modifiablePrimary
   : Identifier (DOT Identifier | L_BRACKET expression R_BRACKET )*
   ;
