grammar Q3;

program
    : statement* EOF
    ;
statement
    : functionDeclaration
    | variableDeclaration ';'
    | classDeclaration
    | enumDeclaration
    | expressionStatement ';'
    | block
    | 'sleep' (INT)? ';'
    | 'fork' expression ';'
    | 'throw' expression ';'
    | 'return' expression? ';'
    | 'want' expression (';' | ',' expression)?
    | 'wait' ';'
    | 'key' expression (';' | ',' expression)?
    ;


variableDeclaration
    : ('@')? Identifier '=' expression
    ;

functionDeclaration
    : '$' Identifier parameterList block                  // named functions
    | '$' Identifier parameterList '->' expression ';'    // named arrow functions
    ;

classDeclaration
    : ('class' | 'class!') Identifier parameterList (':' superClassCall)? classBody
    ;

superClassCall
    : Identifier '(' argumentList? ')'
    ;


classBody
    : '{' classMember* '}'
    ;

classMember
    : functionDeclaration      // $foo(...) {...} or $() -> expr
    | statement
    ;


enumDeclaration
    : 'enum' Identifier '{' enumConstants '}'
    ;

enumConstants
    : Identifier (',' Identifier)* ','?
    ;

parameterList
    : '(' (parameter (',' parameter)*)? ')'
    ;


parameter
    : ('@')? Identifier ('=' expression)?
    | '_'
    ;


argumentList
    : expression (',' expression)*
    ;

block
    : '{' statement* '}'
    ;

expressionStatement
    : expression
    ;

expression
    : assignment
    ;

assignment
    : conditional (('=' | '+=') assignment)?
    ;

conditional
    : orExpr ('?' expression ':' expression)?
    ;

orExpr
    : andExpr ('||' andExpr)*
    ;

andExpr
    : equality ('&&' equality)*
    ;

equality
    : relational (('==' | '!=' | '=>' | '!=>') relational)*
    ;

relational
    : additive (('<' | '>' | '<=' | '>=') additive)*
    ;

additive
    : multiplicative (('+' | '-') multiplicative)*
    ;

multiplicative
    : unary (('*' | '/' | '%') unary)*
    ;

unary
    : ('+' | '-' | '!') unary
    | postfixExpression
    ;

literal
    : INT
    | STRING
    | 'true'
    | 'false'
    | 'null'
    ;


functionExpression
    : '$' parameterList '->' expression                  // anonymous arrow functions
    | '$' parameterList block                             // anonymous block functions
    ;

classExpression
    : 'class' parameterList (':' superClassCall)? classBody?
    | 'class!' parameterList (':' superClassCall)? classBody?
    ;

objectExpression
    : 'new' (':' superClassCall)? classBody?
    | 'new!' classBody?
    ;
primary
    : literal
    | Identifier ('(' argumentList? ')')?       // allow calling class constructors
    | '(' expression ')'
    | functionExpression
    | classExpression
    ;

postfixExpression
    : primary (memberAccessSuffix | subscriptSuffix | functionCallSuffix)*
    ;

functionCallSuffix
    : '(' argumentList? ')'
    ;


memberAccessSuffix
    : '.' Identifier
    ;

subscriptSuffix
    : '[' expression ']'
    ;

Identifier
    : [a-zA-Z_] [a-zA-Z_0-9]*
    ;

INT
    : [0-9]+
    ;

FLOAT
    : [0-9]+ '.' [0-9]+
    ;

STRING
    : '"' (~["\\] | '\\' .)* '"'
    ;

CHAR
    : '\'' . '\''
    ;

COMMENT
    : '#' ~[\r\n]* -> skip
    ;

WS
    : [ \t\r\n]+ -> skip
    ;

fragment
DIGIT : [0-9] ;