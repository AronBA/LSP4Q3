# Q3 Language Documentation
This document describes the syntax features of the Q3 language in a rather formal and technical way. It is not thought as a programming course. It does not describe the functionality contained in the standard library, the ecosystem surrounding Q3 or the internal workings of the Q3Machine.

# Source File Organization
A compiled Q3 code file always contains one class that doesn't inherit from other classes and has a constructor with 0 parameters. The shortest valid Q3 program is an empty file, which is compiled to a class without members and methods, whose constructor returns an instance of that class. The second simplest kind of Q3 program is a single expression. The compiled unit of such a program is made of a class, whose constructor evaluates the expression and returns its value. Example for such a program:

```
"Hello World! " + Math.rnd(10)
```

The program above returns the string `Hello World!` followed by a random integer.

A program can also contain one or more statements. If the program doesn't end with an expression, `this` is returned. Example:

```
hw = "Hello World!";
println(hw);
```

Furthermore, a program can contain function declarations, class declarations, enum declarations, member declarations as well. Example:

```
@member = 1; # member declaration

$foo() -> this.member++; # function declaration

class! Bar(@otherMember); # immutable class declaration

class Car(someLocalVar, @someMember) {
  println(someLocalVar); # side effect in constructor body
  $someMethod() {
    someMember++;
    Math.rnd(someMember) # no semicolon at the last statement -> returns the value of this expression!
  }
}

enum Color {
  RED, GREEN, BLUE,
}

println(Car(1, 5).someMethod());
println(foo());
println(member);
```

As you may have noticed, the root scope of a Q3 program is basically the constructor of a class. Constructors in Q3 are allowed to return values other than `this`. This blurs the distinction between functions, classes and constructors, so that a variety of new syntactical shortcuts become available.

# Formal Syntax Definition
Upon compilation, the code is tokenized at first. This means that whitespaces and comments are removed and the code is broken down into tokens such as identifiers, operators, keywords, brackets and literals. The language is mostly whitespace insensitive, with the exception that there must be at least one whitespace between keywords and identifiers and at least one whitespace between certain operators. Keywords such as `class` or `if` serve special functions and can't be used as identifiers (e.g. for variables or function names).

## Comments
Comments are started by `#` outside string literals and are terminated by the next line break.

```
str = "Example #123"; # some string # still a comment
println(str);
```

Comments can serve special functions that are recognized by the compiler, such as the header of a code file or documentations for declarations:

```
# this is the header of the file
# still the header

# this comment stands by itself

# this function does some stuff
# params: int i
# returns: int
$foo(i) -> i + 1;
```

Note that the comment blocks are separated by empty lines. The comment block that a) comes immediately at the beginning of a file and b) is followed by an empty line makes up the header of the file. Comments that come immediately before declarations with no empty lines in between make up the documentation of that declaration.

## Keywords
Currently the following keywords are reserved, even if some of them currently have no use in the language:

```
class enum extends
if else while do for break continue return throw sleep
instanceof wait key nf want fork
null true false this ctask
match case default try catch finally static public private protected const abstract final
```

## Operators
The following operators are reserved:

```
; . $ @ ,
+ - * / %
++ --
= += -= *= /= %=
<<= >>= >>>= &= |= ^=
! == "= || && | & ^ ~
< > <= >= => !=> ->
>> >>> <<
? :
_
```

## Identifiers
An identifier must start with a letter and can contain letters, digits and underscores. Examples include `fooBar`, `FooBar1`, `FOO_BAR_`, but not `1fooBar`, `foo bar` or `_fooBar`.

## Literals
Literals for integers, floating point numbers, characters and strings will be discussed in the next section.

## Language Grammar
The grammar definition of the Q3 language is given as an Extended Backus Naur Form. Terminals such as keywords (**if** or **class**) or identifiers (**ID**) are written in bold text. This grammar definition doesn't reflect operator precedence for reasons of simplicity.

| Symbol   | Meaning                                                       |
| :------: | :------------------------------------------------------------ |
| ＊       | Repetition 0 to n.                                            |
| ？       | 0 or 1 times.                                                 |
| ＋       | Repetition 1 to n.                                            |
| ｜       | Alternative.                                                  |
| 、       | Repetition 0 to n separated by comma. Trailing comma allowed. |
| x        | Non-Terminal.                                                 |
| **x**    | Terminal.                                                     |

program = topBlock<br>
topBlock = (if ｜ while ｜ do ｜ for ｜ foreach ｜ return ｜ throw ｜ sleep ｜ key ｜ want ｜ memberDecl ｜ class ｜ method ｜ object ｜ enum ｜ stmtExpr ｜ testStmt)＊ expr？<br>
declarationBlock = **{** topBlock **}**<br>
block = **{** (if ｜ while ｜ do ｜ for ｜ foreach ｜ return ｜ throw ｜ sleep ｜ key ｜ want ｜ break ｜ continue ｜ stmtExpr ｜ testStmt)＊ **}**<br>
stmtExpr = expr？ **;**<br>
testStmt = **?** expr **;**<br>

memberDecl = **@** **ID** (**=** expr)？ **;**<br>
class = **class** **!**？ **ID** params superclass？ declarationBody<br>
params = **(** ((**@**？ **ID** (**=** expr)？) ｜ **_**)、 (**...** ｜ **:**) **)**<br>
superclass = **:** **ID** (**.** **ID**)＊ (**(** expr、 **)**)<br>
method = **$** **ID** params declarationBody<br>
object = **new** **!**？ **ID** superclass？ (**;** ｜ **{** declarationBlock **}**)<br>
declarationBody = (**{** declarationBlock **}**) | (**->** expr **;**) | **;**<br>
enum = **enum** **ID** **{** **ID**、 **}**<br>

if = **if** **(** expr **)** block (**else** **if** expr block)＊ (**else** block)<br>
while = **while** **(** expr **)** loopBody<br>
do = **do** block **while** **(** expr **)**<br>
for = **for** **(** (expr **;** expr **;** expr) **)** loopBody<br>
foreach = **for** **(** ((**ID** ｜ tuple) **:** expr) **)** loopBody<br>
loopBody = block ｜ **;**<br>
return = **return** expr? **;**<br>
throw = **throw** expr? **;**<br>
sleep = **sleep** expr **;**<br>
key = **key** ((**(** expr **,** expr **)**) ｜ expr) **;**<br>
want = **want** ((**(** expr **,** expr **)**) ｜ expr) **;**<br>
break = **break** **;**<br>
continue = **continue** **;**<br>

expr = anonFunction ｜ anonClass ｜ anonObject ｜ fork ｜ ternary ｜ unaryOp ｜ binaryOp ｜ incDec ｜ call | array ｜ member ｜ tuple ｜ map ｜ atom<br>
anonFunction = **$** ((params **{** declarationBlock **}**) ｜ ((params ｜ **ID**) **->** expr) ｜ expr)<br>
anonClass = **class** **!**？ params superclass？ (declarationBlock ｜ (**->** expr))？<br>
anonObject = **new** **!**？ superclass？ (**{** declarationBlock **}**)？<br>
fork = **fork** expr<br>
ternary = expr **?** expr **:** expr<br>
unaryOp = (**!** ｜ **~** ｜ **+** ｜ **-** ｜ **/**) expr<br>
binaryOp = expr (**\*** ｜ **/** ｜ **%** ｜ **+** ｜ **-** ｜ **<<** ｜ **>>** ｜ **>>>** ｜ **==** ｜ **!=** ｜ **=>** ｜ **!=>** ｜ **<** ｜ **<=** ｜ **>=** ｜ **>** ｜ **instanceof** ｜ **^** ｜ **&** ｜ **\|** ｜ **&&** ｜ **\|\|**) expr<br>
incDec = ((**++** ｜ **--**) expr) ｜ (expr (**++** ｜ **--**))<br>
call = expr **(** mapPair、 **)**<br>
postfixcall = expr **::** (**ID** ｜ braces ｜ anonFunction) **(** mapPair、 **)**？<br>
array = expr **[** expr、 **]**<br>
member = expr **.** (**ID** ｜ **wait** ｜ **nf** ｜ **class**)<br>
atom = **ID** ｜ **STRING** ｜ **INTEGER** ｜ **FLOAT** ｜ **CHAR** ｜ **_** ｜ **null** ｜ **false** ｜ **true** ｜ this ｜ **ctask** ｜ **wait** ｜ tuple ｜ map ｜ braces<br>
this = **this** **~**＊<br>
tuple = **[** expr、 **]**<br>
map = **{** mapPair、 **}** <br>
mapPair = (**ID** | **STRING** | **INTEGER** | **FLOAT** | **CHAR** | **null** | **false** | **true**) **:** expr<br>
braces = **(** expr **)**<br>

# Primitive Data Types
Primitive data types in Q3 form the basis of the language and are so deeply integrated that they have their own literals on the syntax-level. All primitive types are also immutable. Other types and functions contained in the standard library are introduced in another section later on.

## null
The constant null can be denoted as `null`, `false`. It has no type and no members. It can be used by the programmer to mean "no data", "boolean false" or "wildcard - match anything".

## true
The meaning of the constant true is "boolean true". It has the type Any and like null has no members.

## Integer
Integers in Q3 are always signed and 64bit. They can be written in decimal (e.g. `42` or `-13_37`), hexadecimal (e.g. `0xBA5EBA77`), octal (e.g. `07123`) or binary form (e.g. `0b1011`). The notation is identical to the integer notation in Java. Underscores serve no purpose other than for better legibility (e.g. `1_000_000`). Contrary to languages such as C, `0 != null`.

## Float
Floating point numbers always have 64bit precision. For a number to be interpreted as a floating point number, it must start with a digit and contain exactly one dot. Valid notations include `0.12`, `-2.0`, `12_34.56_78`, but not `.1`, `-.12`. The `NaN` constant ("not a number") in Q3 is the same as the NaN-constant in Java.

## Char
Characters represent single unicode characters. As in Java, a Char is a 16bit unsigned integer. They can be denoted using single quotes such as in `'a'` or `'\n'`.

## String
A sequence of 0 to n characters. Strings are denoted using double quotes such as in `"Hello World!"`. There must be no line-breaks within a string literal. For example,

```
"Hello
World!"
```

is not a valid string. Special characters within strings can be expressed using a preceding backslash. The following escape codes are supported:

| Escape Sequence | Meaning           |
| :-------------- | :---------------- |
| `\0`            | `(0).toChar`      |
| `\b`            | Backspace         |
| `\t`            | Tab               |
| `\n`            | Newline           |
| `\f`            | Formfeed          |
| `\r`            | Carriage return   |
| `\"`            | Double quote      |
| `\'`            | Single quote      |
| `\\`            | Backslash         |
| `\@`            | @                 |
| `\u0000`        | Unicode character |

The last escape code allows the representation of any unicode character by replacing `0000` with the hexadecimal representation of the id of the desired character. For example, `'\u3042'` represents the character あ (hiragana for A).

The class StringBuilder allows creation of strings by appending data to its end in a memory-friendly way.

The character @ allows the use of variables within strings.

```
myVar = "World";
return "Hello @myVar!";
```

The above program returns `"Hello World!"`.

## Converting between Integer, Float, Char and String
The first three of these data types have the members `toInt`, `toFloat`, `toChar` and `toString`. For example, `(12.3).toInt` evaluates to `12`. Strings have the members `toInt` and `toFloat` that return the respective data type on success and return `null` on failure.

## Tuples
A tuple is an immutable, ordered collection of 0 to n values. It is denoted using square brackets, such as in `[1, "hello", [], [null,]]`. Elements within a tuple can be accessed using their index and the subscript-operator.

```
myTuple = ["abc", "def", "ghi"];
return myTuple[1];
```

The above program returns `"abc"`.

## ImMap
Immutable maps are pairs of a key and a value, with each key being unique in the map. A value can be retrieved using the matching key. Immutable maps can be denoted using curly brackets, such as in `{abc: 1, def: 2, ["ghi", 3]}`. The notation `key: value` is basically a binary tuple `["key", value]` with the difference that strings must not necessarily be enclosed in quotes. Value access is possible using the subscript-operator. `null` is returned if no mapping exists for the given key.

```
myMap = {abc: 1, def: 2};
return [myMap["abc"], myMap["ghi"]];
```

The above program returns `[1, null]`.

## BinaryString
Binary strings are mutable, fixed-size arrays of 8bit integers. They neither are immutable nor have literals, hence they aren't primitive data types. However due to their importance, they should nevertheless be mentioned in this section. `BinaryString(length)` allocates a new instance filled with zeroes. Value access and manipulation is possible using the subscript-operator. The class BinaryStringBuilder allows creation of a BinaryString by appending data to its end in a memory-friendly way.

# Operators
This table shows the precedence of all operators, with the strongest binding operators being listed first. Operators that are in the same row have the same precedence.

| Operators | Associativity | Explanation |
| :-------: | :-----------: | :---------- |
| **() :: [] . ** | left | Function call, postfix function call, subscript operator and member access. |
| **++ --** | - | Incrementers and decrementers. |
| **! ~ + - /** | right | Unary operators. |
| **instanceof** | - | Tests whether an object is an instance of a class. |
| **/ \* %** | left | Division, multiplication and modulo. |
| **+ -** | left | Addition and subtraction. |
| **<< >> >>>** | left | Bitshift. |
| **^** | left | Bitwise XOr. |
| **&** | left | Bitwise And. |
| **\|** | left | Bitwise Or. |
| **== != => !=> < <= >= >** | - | Comparison. |
| **&&** | left | Logic And. |
| **\|\|** | left | Logic Or. |
| **= += -= \*= /= %= <<= >>= >>>= \|= &= ^= fork $ class ?:** | right | Assignment operators, anonymous functions and ternary operator. |
| **,** | - | Separation of arguments and parameters. |
| **() [] {}** | - | Braces. |

## Arithmetic Operators

### Binary +
If at least one of the two operands is a string, string concatenation is performed. Otherwise the two numbers are added. The resulting data type depends on the types of the operands.

| ↓ LHS / RHS → | Integer | Float | Char | String |
| :-: | :-----: | :---: | :--: | :----: |
| **Integer** | Integer | Float | Char | String |
| **Float** | Float | Float | Float | String |
| **Char** | Char | Float | Char | String |
| **String** | String | String | String | String |

### Binary -
Performs subtraction. The resulting data type depends on the types of the operands.

| ↓ LHS / RHS → | Integer | Float | Char |
| :-: | :-----: | :---: | :--: |
| **Integer** | Integer | Float | Char |
| **Float** | Float | Float | Float |
| **Char** | Char | Float | Char |

### Binary \*, Binary / and Binary %
Performs multiplication, division or modulo. The resulting data type depends on the types of the operands. Contrary to Java, the result of modulo has the same sign as the divisor. This means that `10 % 8 == 2` and `-10 % 8 == 6` and `10 % -8 == -6` and `-10 % -8 == -2`. The result of division for integers is always rounded down, meaning that `-10 / 8 == -2` and `-10 / -8 == 1`.

| ↓ LHS / RHS → | Integer | Float |
| :-: | :-----: | :---: |
| **Integer** | Integer | Float |
| **Float** | Float | Float |

### Unary + and -
Those two operators are defined for Integer and Float. `+` changes the sign to positive, while `-` inverts the sign. For example, `+5 == 5`, `+(-5) == 5`, `-(5) == -5` and `-(-5) == 5`.

### Unary /
This operator is defined for Integer and Float and returns the multiplicative inverse of the given number as a Float. For example, `/2` and `/2.0` both evaluate to `0.5`. This is equivalent to `1.0 / operand`.

## Comparison
Comparison operators are binary and evaluate to `true` or `false`. The following comparison operators are defined.

| Operator | Data types | Meaning |
| :------: | :--------: | :------ |
| **==** | Anything | True if both operands are equal. |
| **!=** | Anything | True if both operands are not equal. |
| **<=** | Numbers | True if the left operand is equal or less than the right operand. |
| **<**  | Numbers | True if the left operand is less than the right operand. |
| **>**  | Numbers | True if the left operand is greater than the right operand. |
| **>=** | Numbers | True if the left operand is equal or greater than the right operand. |
| **=>** | Anything | True if the left operand implies the right operand. |
| **!=>** | Anything | True if the left operand does not imply the right operand. |
| **instanceof** | Anything (lhs) and Class (rhs) | True if `rhs.rep => lhs`. |

The type implication operators are explained in a later section.

## Logic Operators
These operators are defined for any data type and can evaluate to any data type, unlike in other languages where those operators are only defined for booleans. This makes it easier to handle `null` in expressions, such as in `mightBeNull && mightBeNull.doStuff();` or `myVar = mightBeNull || "Not present.";`.

### Unary !
If the operand is `null`, it evaluates to `true`. Otherwise it evaluates to `null`. For example, `!123 == null` and `!null == true`.

### Binary &&
The left operand is evaluated. If it is `null`, the right operand is not evaluated and the whole expression evaluates to `null`. Otherwise it evaluates to the value of the right operand. This means that both operands must not be `null` in order for the expression to not evaluate to `null`. If the left operand is `null`, the right operand is not evaluated at all.

### Binary \|\|
The left operand is evaluated. If it is not `null`, the whole expression evaluates to the value of the left operand. Otherwise it evaluates to the value of the right operand. This means that at least one of the two operands must not be `null` in order for the expression to not evaluate to `null`. If the left operand is not `null`, the right operand is not evaluated at all.

### Ternary ?:
The first operand is evaluated. If it is `null`, the expression evaluates to the value of the third operand. Otherwise it evaluates to the value of the second operand. Either the second operand or the third operand is evaluated.

The program

```
$checkPw(pw) {
  if(pw == "secret") {
    println("Password correct.");
  } else {
    println("Wrong password.");
  }
}
```

can be simplified to

```
$checkPw(pw) {
  println(pw == "secret" ? "Password correct." : "Wrong password.");
}
```

using the ternary operator.

## Incrementers and Decrementers
Those operators are defined for Integer, Float and Char.

### Unary Prefix ++ and --
Increments or decrements the value of the given variable respectively and evaluates to the value after the operation. Example:

```
myVar = 1;
println(myVar); # 1
println(++myVar); # 2
println(myVar); # 2
println(--myVar); # 1
println(myVar); # 1
```

### Unary Suffix ++ and --
Increments or decrements the value of the given variable respectively and evaluates to the value before the operation. Example:

```
myVar = 1;
println(myVar); # 1
println(myVar++); # 1
println(myVar); # 2
println(myVar--); # 2
println(myVar); # 1
```

## Bitwise Operators
These operators are defined only for Integer and evaluate to Integer.

| Operator | Meaning |
| :------: | :------ |
| Unary **~** | Inverts the bits, i.e. switches 0 with 1. For example `~-2 == 1`. |
| Binary **<<** | Shifts each bit of the left operand n bits to the left, with the empty bits on the right being filled with 0. n is determined by `rhs & 63`. For example `-3 << 2 == -12` and `5 << -61 == 40`. |
| Binary **>>** | Shifts each bit of the left operand n bits to the right, with the empty bits on the left being filled with the value of the highest bit. n is determined by `rhs & 63`. For example `-13 >> 2 == -4` and `15 >> -63 == 7`. |
| Binary **>>>** | Shifts each bit of the left operand n bits to the right, with the empty bits on the left being filled 0. n is determined by `rhs & 63`. For example `-13 >>> 2 == 4611686018427387900` and `15 >>> -63 == 7`. |
| Binary **&** | Applies the and-operation to each pair of bits on the same index, so that *result[i] = and(left[i], right[i])*. For example `(4 + 2) & (2 + 1) == 2`. |
| Binary **\|** | Applies the or-operation to each pair of bits on the same index, so that *result[i] = or(left[i], right[i])*. For example `(4 + 2) & (2 + 1) == 4 + 2 + 1`. |
| Binary **^** | Applies the xor-operation to each pair of bits on the same index, so that *result[i] = xor(left[i], right[i])*. For example `(4 + 2) ^ (2 + 1) == 4 + 1`. |

## Call Operator
The n-ary operator `()` can be appended to the end of an expression to call the function that this expression evaluates to and retrieve the returned value. If has a syntax of the form `expr(arg0, arg1, argN)`. `expr` must evaluate to a function or a class. If the latter is the case, the constructor of the class is called and the resulting instance is returned. 0 to n arguments are allowed. In most cases, the correct argument count is checked at runtime.

```
$foo(a) -> a + 1;
$bar() -> foo;

println(foo(3)); # 4
println(bar()(1)); # 2
```

## Postfix Call Operator
This alternative syntax for function calls allows for placing the first argument before the function name. The most basic form of this syntax is `a::b`, where `a` is the argument and `b` the function. Aside from the order of evaluation which is left to right, it is equivalent to `b(a)`. It is also possible to provide more arguments using the form `a::b(c, d)`, which is equivalent to `b(a, c, d)`. Like the function call, subscript and member access operators, it is left-associative and has the same precedence. This means that `a::b::c` is equivalent to `c(b(a))`. The expression containing the function can be a single identifier, an expression within parentheses or a lambda expression.

Example 1:
```
$f(str) {
  println(str);
  return "foo@str";
}

f("bar")::f::println;
```

The output will be:
```
bar
foobar
foofoobar
```

Example 2:
```
1733667089800::(Time.toString)::println;
```

Is equivalent to:
```
println(Time.toString(1733667089800));
```

Example 3: `1::$ 2 + _` evaluates to `3`.

## Subscript Operator
The n-ary operator `[]` has a syntax of the form `expr[arg0, arg1, argN]`, with 0 to n arguments being allowed. This operator is mostly used to access elements in collections such as tuples, lists and maps, such as:

```
l = List(List("a", "b"), List("c", "d"));
```

A subscript operator with 0 arguments must always be on the left hand side of an assignment expression and can be used to append an element to the back of a list:

```
l[] = List("e", "f");
```

The subscript operator with 1 argument can be used on both hand sides for element access and manipulation:

```
println(l[0]); # [a, b]
println(l[1]); # [c, d]
l[1] = List("x", "y"); # element manipulation
println(l[1]); # [x, y]
```

It is also possible to use the subscript operator with more than 1 argument. The expression `expr[arg0, arg1, argN]` is equivalent to `expr[arg0][arg1][argN]`. Again, this syntax can be used on both hand sides:

```
println(l[0, 1]); # b
println(l[1, 0]); # c
l[1, 0] = "x";
println(l[1]); # [x, d]
println(l[1, 0]); # x
```

# Control Structures
Control structures are responsible for controlling the flow of information and order of execution inside a function body.

## Local Variables
Local Variables are declared by an initial assignment or a statement containing only the identifier. Please note that this always shadows non-local variables such as member variables. Parameters of functions also count as local variables.

```
myVar1; # declaration without initialization
myVar2 = "abc"; # declaration by assignment

# myVar1 is not initialized at this point
# this would not compile
# println(myVar1);

myVar1 = 123; # initialization
println(myVar1); # 123
println(myVar2); # abc

myVar1 += 321; # assign another value
myVar2 = myVar1 + myVar2; # assign another value
println(myVar1); # 444
println(myVar2); # 444abc
```

Local variables are only valid within the scope they're declared in. A visibility scope can be limited to a block or a control structure for example.

```
myVar1 = "abc";

if(myVar2 = foo()) { # assign the return value of foo() to newly declared myVar1 and check if it's not null
  myVar3 = 123;
  println("@myVar1 @myVar2 @myVar3"); # print the values of the two variables
}

# those variables aren't visible here anymore
# println("@myVar1 @myVar2");
println(myVar3); # still visible
```

Variable assignments count as expressions too. An assignment evaluates to the assigned value. Special assignment operators such as `+= -= *= /=` perform the respective binary operation on the current value of the variable and on the assigned value. It evaluates to the value assigned to the variable.

```
# myVar += 5;
# this would not compile since variables can only be initialized using =

println(myVar = 24); # 24
println(myVar /= 3); # 8
println(myVar); # 8
```

## Tuple Assignment
If the number of elements in a tuple is known, its elements can be stored to variables using the following syntax:

```
myTuple = [1, 2];
[firstValue, secondValue] = myTuple;
println(firstValue);
println(secondValue);
# this is equivalent to:
# firstValue = myTuple[0];
# secondValue = myTuple[1];
```

This is convenient for having functions with multiple return values or for swapping the values of two variables:

```
[a, b] = [b, a];
```

Tuple assignments can also be nested. `_` can be used to ignore a certain value.

```
myTuple = [1, [2, 3]];
[firstValue, [_, thirdValue]] = myTuple;
println(firstValue);
println(secondValue);
```

## If Conditions
An if-condition consists of a condition, a then-branch and an optional else-branch. If the condition is not `null`, the then-branch is executed. Otherwise the else-branch is executed if present. In contrast to other programming languages, those two branches must always be blocks in order to improve code readability. Another if-query can be in place of the else-branch.

```
# read a message
msg = readln();
# shorten the message if it's too long
if(msg.length > 64) {
  msg = msg.substring(0, 64);
  println("The message was too long. Shortened it to 64 characters.");
}
println("Your message: @msg");
```

```
# read an integer
i = readln().toInt;
if(i % 2 == 0) {
  println("Your number is even.");
} else {
  println("Your number is odd.");
}
```

```
# read the name of an animal
name = readln().toLowerCase;
if(name == "snake") {
  println("A snake has no legs.");
} else if(name == "trex") {
  println("A T-Rex has two legs.");
} else if(name == "lizard") {
  println("A lizard has four legs.");
} else {
  println("I've never heard of this animal.");
}
```

## While Loops
A while-loop consists of a condition and an optional body. Such a loop is executed as follows:

1. Evaluate the condition. If `null`, abort. Otherwise to to 2.
2. Execute the body if present.
3. Go back to 1.

```
# prints all numbers from 1 to 10.
i = 0;
while(i++ < 10) {
  println(i);
}
```

## Do-While Loops
A do-while-loop consists of a body and a condition. Such a loop is executed as follows:

1. Execute the body.
2. Evaluate the condition. If `null`, abort. Otherwise go to 1.

```
pw;
do {
  println("Enter the password:");
} while((pw = readln()) != "secret123")
println("Password correct.");
```

## For Loops
A for-loop consists of an initializer, a condition, an incrementer and an optional body. Such a loop is executed as follows:

1. Evaluate the initializer.
2. Evaluate the condition. If `null`, abort. Otherwise go to 3.
3. Execute the body if present.
4. Evaluate the incrementer.
5. Go back to 2.

The initializer and incrementer must not necessarily be a variable declaration or assignment. They can be any expression.

```
# print all numbers from 0 to 9
for(i = 0; i < 10; i++)  {
  println(i);
}

# i isn't valid in the outer scope
# println(i);

# loop with no body
# print all numbers from 0 to 9
for(i = 0; i < 10; println(i++));
```

## Foreach Loops
A foreach-loop as a special case of a for-loop where the body is executed for each value of a collection such as a tuple, list or map.

```
myTuple = ["red", "green", "blue"];
for(color: myTuple) { # assign each value in the tuple to the variable color
  println(color);
}
# this prints:
# red
# green
# blue
```

A foreach-loop is equivalent to the following code:

```
myTuple = ["red", "green", "blue"];
it = myTuple.iterator();
while(it.hasNext()) {
  color = it.next();
  println(color);
}
```

This means that the collection must provide the function `iterator`, which returns an objects that implements the methods `hasNext` and `next`.

Foreach-Loops also work with tuple assignment, which is convenient when iterating over maps for example.

```
myMap = {"Germany": "Berlin", "Austria": "Vienna", "Italy": "Rome"};
for([country, capital]: myMap) {
  println("The capital of @country is @capital.");
}
# this prints in undefined order:
# The capital of Germany is Berlin.
# The capital of Austria is Vienna.
# The capital of Italy is Rome.
```

## Break and Continue
Those statements can be used in the bodies of loops to alter the control flow. `break` leaves the loop body and aborts the innermost loop. `continue` leaves the loop body and goes back to the condition of the innermost loop. In for-loops, `continue` evaluates the incrementer first and then goes to the condition.

```
# this program reads at most 10 numbers into a list and aborts if 0 is entered
amount = 10;
list = List();
while(amount > 0) {
  number;
  if(!(number = readln().toInt)) {
    println("Please enter a valid number.");
    continue;
  }
  if(number == 0) {
    break;
  }
  list.add(number);
  amount--;
}
println(list);
# for example, inputting 3, 2, abc, 4, 0 gives the list [3, 2, 4].
```

## Return
A `return` statement aborts the execution of the function it occurs in and makes the function return the given value. `return` statements without an explicit return value return `true` in functions and `this` in class constructors. Functions that don't have a `return` statement or a final expression at their end behave as if there was `return;`.

The program `return 123;` is the same as the program `123` and `return;` is the same as `this`.

## Throw
A `throw` statement manually throws an exception. It aborts the execution of the task that it is executed by. If this task has an error handler set, the error handler is executed with the exception as argument. If no error handles is set, the task dies and the exception is delegated to the parent task. Only instances of the class Exception can be thrown.

## Null-Check
A statement expression preceded by `?` outside a loop makes the current function return `null` if the expression contained in it evaluates to `null`. If such a statement is inside a loop and the expression evaluates to `null`, the control flow jumps back to the loop condition.

Both programs shown below are equivalent.

```
$sumEven(n) {
  sum = 0;
  for(i = 0; i < n; i++) {
    ? i % 2 == 0;
    sum += i;
  }
  return sum;
}

foo = getMaybeNull();
? foo;
println("Not null: @foo");
```

```
$sumEven(n) {
  sum = 0;
  for(i = 0; i < n; i++) {
    if(!(i % 2 == 0)) {
      continue;
    }
    sum += i;
  }
  return sum;
}

foo = getMaybeNull();
if(!foo) {
  return null;
}
println("Not null: @foo");
```

# Functions, Classes and Enums

## Functions
A function can be used to abstract a recurring pattern in a program so that only the function needs to be called instead of copying and adapting the same code over and over.

```
number1;
do {
  println("Enter a number divisible by 2:");
  number1 = readln().toInt;
} while(number1 % 2 != 0)

number2;
do {
  println("Enter a number divisible by 3:");
  number2 = readln().toInt;
} while(number1 % 3 != 0)

println("You've entered: @number1 @number2");
```

The above program can be simplified to:

```
$readNumber(divisible) {
  number;
  do {
    println("Enter a number divisible by @divisible:");
    number = readln().toInt;
  } while(number1 % divisible != 0)
  return number;
}

number1 = readNumber(2);
number2 = readNumber(3);
println("You've entered: @number1 @number2");
```

Function declarations in Q3 always start with `$`. The order in which functions are declared doesn't matter. If the body of the function is a single expression, whose value should be returned, the following syntax can be used:

```
println(max(3, 5));

$max(a, b) -> a > b ? a : b;

println(max(4, 2));
```

When a function is called, the expression identifying the function is evaluated and then all argument expressions are evaluated left to right. The values of the arguments are assigned to the parameters of the function, which are essentially local variables. Within the scope of a function body, only its own parameters and local variables are accessible, along with member variables in the parent classes of the function. There are no void-functions in Q3, which are functions that don't have a return value. Functions that terminate with `return;` or without `return` always return `true`. Functions can also be passed as values, so that higher-order functions are possible. Member variables that are assigned must always be accessed via the `this` keyword, because otherwise a local variable would be created.

```
@myMember = 5;
myLocal = 1;

$manipulateMember(f) {
  # this would create the local variable myMember
  # myMember = f(myMember);
  
  this.myMember = f(myMember);
  
  # not allowed because it lies outside the scope of the function body
  # this would also create a new local variable
  # myLocal = f(myLocal);
}

$inc(i) -> i + 1;

println(myMember); # 5
manipulateMember(inc); # pass function inc as value
println(myMember); # 6
println(myLocal); # 1
```

## Accepting a Dynamic Number of Parameters
Functions can be configured so that excessive arguments are passed to the last parameter as tuple or map. If the parameter list of a function with *n* parameters ends with `...` or `:`, the function requires at least *n - 1* arguments. With `...`, all remaining arguments are packed into a tuple and assigned to the last parameter. With `:`, all remaining arguments are interpreted as an ImMap. This means that each of those arguments must be a binary tuple representing a key-value pair.

```
$foo(bar, rest ...) -> println("@bar @rest");

# invalid, requires at least 1 argument
# foo();

foo("empty"); # empty []
foo("one", 1); # one [1]
foo("two", 1, 2); # two [1, 2]
```

```
$foo(bar, rest :) -> println("@bar @rest");

# invalid, requires at least 1 argument
# foo();

foo("empty"); # empty {}
foo("one", a: 1); # one {a: 1}
foo("two", a: 1, ["b", 2]); # one {a: 1, b: 2}
```

## Unused Parameters
If a parameter is required in the function signature, but is never used in the function body, it can be omitted using `_` instead of the parameter name. Such parameters can't have default values. Unused parameters show up as `null` in the `Funclass.params` tuple.

```
$foo(a, _) -> println(a);
$fooo(_ ...) -> println("foobar");

bar = $_ -> println("World!");

foo("Hello", "abc"); # Hello
bar("xyz"); # World!

println(foo.params); # [a, null]
```

## Default Arguments
It is also possible to pass less arguments than declared to a function if the function has default values set for the missing parameters. Default parameters can be given in funclass declarations and all last *n* parameters must have a default value, except for the last parameter if `...` or `:` follows. The expressions for the default values must evaluate to a constant value and must not have side effects.

```
$foo(a, b = true, c = 2 + 3, d = [1, 2]) -> println("@a @b @c @d");

# incorrect, constant expression required
# $foo(a = MyClass.rep);

# incorrect, only parameters with default values must follow
# $foo(a = 1, b);

# foo(); # a has no default value
foo(1); # 1 true 5 [1, 2]
foo(1, 2); # 1 2 5 [1, 2]
foo(1, 2, 3); # 1 2 3 [1, 2]
foo(1, 2, 3, 4); # 1 2 3 4

$bar(a = 1, b ...) -> println("@a @b");

# incorrect, see above
# $bar(a = 1, b = 2 ...);

bar(); # 1 []
bar(2); # 2 []
bar(2, 3); # 2 [3]
```

## Passing a Dynamic Number of Arguments
The last section covered how a function can accept an arbitrary amount of parameters. So far it's only been possible to call a function with a fixed amount of arguments. The builtin function `invoke` takes a function as first parameter and a tuple as last parameter. It calls the given function with the values in the tuple as arguments and returns the value of the function.

```
$foo(args ...) -> args;

println(invoke(foo, [])); # []
println(invoke(foo, [1, 2])); # [1, 2]
println(invoke(foo, 1, 2, [])); # [1, 2]
println(invoke(foo, 1, 2, [3, 4])); # [1, 2, 3, 4]

println(invoke(invoke, invoke, foo, [1, 2, [3], []])); # [1, 2, 3]
```

## Documenting Funclasses
The comments preceding `$` or `class` without any line breaks in between them count as the documentation for the corresponding declaration. The comment should start with a formal definition of the behavior of the funclass, with parameters being referred to with a preceding `@`. The optional fields `params:`, `namedparams:` and `returns:` must be in separate lines. `params` lists the types of the arguments that should be passed to the funclass, separated by a semicolon. If excessive arguments are interpreted as a map, the field `namedparams:` allows documentation for named optional parameters. If the funclass returns a value other than `this` or `true`, the field `returns:` can be used to specify the return type.

There are several rules for expressing types within the funclass documentation. Primitive types should be written in lowercase letters, such as `any int string float boolean`. Tuples with known arity can be expressed using curly brackets, containing the types of the elements, such as `[int, string]`. Tuples with unknown arity contain the generic type of its elements followed by `...`, such as `[string ...]`. Generic types of collections can be written within arrows after the collection type, such as `List<string>` or `Map<string, int>`.

```
# does stuff
#
# params: string; int
# returns: [string, [int ...]]
$foo(a, b ...) -> [a, b];

# lorem ipsum
# dolor sit amet
# params: List<string>
# namedparams: int x: 0; string y: abc
$bar(list, map :) {
  x = map["x"] || 0;
  y = map["y"] || "abc";
  list.add(x);
  list.add(y);
}
```

## Mutable Classes
A class declaration offers all the features that a function declaration has, with both having a similar syntax. This means that a function can be rewritten as a class without a change in semantics, except for `this` keywords. A class can have the following that a function can't have:

* Instances of itself
* Member variables
* Nested functions, classes and enums
* Inheritance

Classes allow the programmer to define new data types with new properties. When the class is called like a function, a new instance of the class is allocated, containing the declared member variables. Afterwards the body of the class (the constructor) is executed and the new instance of the class is returned. Unlike in Java, where the class body, the constructor and the `Class<?>` object are separate entities, these concepts are one thing in Q3.

```
class MyClass(@myMember, myLocal) {
  otherLocal = myLocal * 2;
  println("New instance created. " + someMember);
  @someMember = otherLocal + 5;
  @otherMember; # initialized with null by default
  
  $getMyMember() -> myMember;
  $setMyMember(value) -> this.myMember = value;
  $doubleSomeMember() -> this.someMember *= 2;
  
  # doesn't work, as explained in a previous section
  # $getMyLocal() -> myLocal;
}

println(MyClass.params); # [myMember, myLocal]

myInstance = MyClass(10, 20); # allocate new instance

println(myInstance.getMyMember()); # 10
myInstance.setMyMember(15);
println(myInstance.myMember); # 15

println(myInstance.someMember); # 45
myInstance.doubleSomeMember();
println(myInstance.someMember); # 90

println(myInstance.otherMember); # null
myInstance.otherMember = "abc";
println(myInstance.otherMember); # abc
```

Member variables are declared using the `@` operator. One option is to prepend it to a parameter, so that the value of the parameter is assigned to a member variable with the same name. Such a parameter isn't accessible as a local variable, but only as a member variable. The second option is to declare a member variable in the class body without initializing it. Contrary to local variables, which can't be accessible when uninitialized, member variables are initialized with `null` right away. The third option is to declare and initialize member variables in the class body. Member variables and functions are accessible even before their declaration, so the order of declarations doesn't matter, but the order of statements does. Member variables and member functions of an object can be accessed using the `.` operator.

If a class shouldn't have any effects in its constructor or member functions, a shorter syntax can be used:

```
class OtherClass(@foo, @bar);
```

Mutable classes are always compared by reference, even if two instances share the same type and their member variables have the same values:

```
obj1 = MyClass(1, 2);
obj2 = MyClass(1, 2);

println(obj1 == obj2); # null
println(obj1.myMember == obj2.myMember); # true
println(obj1.setMyMember == obj1.setMyMember); # true
println(obj1.setMyMember == obj2.setMyMember); # null
```

Unlike methods, the body of a class returns `this` by default. However it must not necessarily return `this`, meaning that it can also return any other value. The naming convention says that the names of classes should start with a capital letter while those of functions should start with a lowercase letter. If a class doesn't behave like a class from the outside (i.e. doesn't return `this`), it should be named like a function.

If classes are nested, the operator `~` can be appended to `this` to give the instance of the outer class.

```
class Outer(@a) {
  class Inner() {
    $setA(a) {
      this~.a = a;
    }
    
    $getOuter() -> this~;
  }
}

outer = Outer(1);
inner = outer.Inner();
inner.setA(2);
println(outer.a); # 2
println(outer == inner.getOuter()); # true
```

## Immutable Classes
Immutable classes are declared using `class!` and are always compared by value. Their member variables are initialized in the constructor and their values can't be altered afterwards. The constructor can contain function or class declarations, but must contain no statements or additional member declarations.

```
class! MyImmutable(@a, @b);

obj1 = MyImmutable(1, 2);
obj2 = MyImmutable(1, 2);
obj3 = MyImmutable(3, 4);

println(obj1.a); # 1
println(obj1 == obj2); # true
println(obj1 == obj3); # null

# obj1.a = 2;
# this gives a runtime error
```

```
class! MyImmutableAdvanced(@a, @b) {
  # not allowed:
  # @c = 5;
  # println("Hello World!");
  # return this;
  
  # this is fine:
  $getA() -> a;
  class NestedMutable(@c);
  class! NestedImmutable(@d);
}
```

The values stored in an immutable class can be mutable:

```
class! Foo(@list);
foo = Foo(List());
println(foo.list); # []
foo.list.add(1);
println(foo.list); # [1]

println(foo == Foo(List(1))); # null
println(foo == Foo(foo.list)); # true
```

## Inheritance
Deriving a class from another class means that the derived class inherits all members and functions from the superclass. The derived class can add new members and functions, but can also override functions from the superclass. Classes that are already derived from another class can be derived from as well. A class can only inherit from 0 or 1 classes. The superclass must either be declared before the derived class in the same file or the superclass must already exist before the file is loaded. The superclass is resolved in the constructor of the outer class and the resulting derived class is stored as a member of the instance of the outer class.

```
class Superclass(@myMember, local) {
  println(local);
  
  $getMyMember() {
    doStuff();
    return myMember;
  }
  
  $doStuff() -> println("Superclass!");
}

class Derived(myMember, local, @otherMember) : Superclass(myMember, local * 2) {
  println("Side effect!");

  $doStuff() -> println("Derived!");
}

s = Superclass(2, 4);
# 4
println(s.getMyMember());
# Superclass!
# 2

d = Derived(2, 4, 6);
# 8
# Side effect!
println(d.getMyMember());
# Derived!
# 2
println(d.otherMember);
# 6
```

Members that are already declared as members in the superclass don't need to be declared as members in the derived class anymore, as shown with `myMember`. The constructor of the derived class can differ in structure from the superclass constructor, but the constructor of the superclass must be called before the derived constructor. The latter can decide which arguments are  passed to the superclass constructor. So upon instantiation of a derived class, the superconstructor arguments are evaluated at first, followed by a call to the superconstructor. Afterwards the derived constructor is executed.

Mutable classes can only inherit from other mutable classes and immutable classes can only inherit from immutable classes.

The syntax for inheritance for immutable classes is slightly different, since the superconstructor call is obvious.

```
class! Superclass(@a, @b);
class! Derived(@c) : Superclass;

instance = Derived(1, 2, 3);
println(instance.a); # 1
println(instance.b); # 2
println(instance.c); # 3
```

Because there are no computations allowed in the constructor of an immutable class, the parameters would be passed on as they are. This would give `class! Derived(@a, @b, @c) : Superclass(a, b)`, but to keep it short, no arguments must be passed to the superconstructor in immutable classes.

## Enums
An enum is a collection of semantically related, unique constants. An enum can be declared using the keyword `enum`, followed by its name and a block containing a list of its constants. The enum has its constants as members, along with the member `values` that holds a tuple of all constants. The subscript operator allows for constant access through their name. Enum constants have the members `ordinal` and `name`. The ordinal of an enum constant is unique within an enum, starting from 0 and follows the order in which the constants were declared.

```
enum Color {
  RED, GREEN, BLUE,
}

red = Color.RED;
println(Color.RED); # RED
println(Color.GREEN.ordinal); # 1
println(Color.BLUE.name == "BLUE"); # true
println(Color.RED instanceof Color); # true
println(Color.values); # [RED, GREEN, BLUE]
println(Color["RED"]); # RED
println(Color["GRUE"]); # null
println(Color["REPLE"]); # null
println(red == Color.RED); # true
println(red == Color.GREEN); # false
```

## Anonymous Functions and Classes
As explained in a previous section, functions can be passed as a regular data type. Regular functions and classes can only be declared in the top level of another declaration. Anonymous functions and classes however can be declared within an expression. The syntax and their behavior are slightly different. First of all, anonymous declaration don't have a name, hence they're anonymous. Furthermore, they can access, but not modify, local variables in the parent scope. And finally, anonymous declarations are compared by reference.

```
$mapList(list, f) {
  newList = List();
  for(l: list) {
    newList[] = f(l);
  }
  return newList;
}

println(mapList([1, 2, 3], $i -> i * 2)); # [2, 4, 6]
println(mapList([1, 2, 3], $i -> -i)); # [-1, -2, -3]
```

The following notations exist for anonymous functions:

```
# no parameters
function = $ "Hello World!";

# nameless parameters ＊
function = $ _ / (1 + _);
function = $ "Hello @_!";

# one parameter
function = $name -> "Hello @name!";

# 0 to n parameters
function = $(x, y) -> x / (1 + y); # equivalent to ＊
function = $(x, y) { return x + y; };
```

The notation `$ { return "Hello World!"; }` is syntactically incorrect because the block would be parsed as a map literal.

If an anonymous function has no parameter list following after `$`, then each `_` in the expression will be interpreted as an additional parameter from left to right, as shown in the code above. For example, the anonymous function `$ [_.member, _] = _` has 2 parameters, because the second underscore counts as wildcard for tuple assignment. The anonymous function `$ [_, $ _]` has one parameter, because the second underscore belongs to the inner anonymous function. `$(a) -> a + _` is invalid, because only anonymous functions without a parameter list can make use of this syntax.

Anonymous functions and classes can bind local variables from outside. Changes to local variables after the declaration of an anonymous declaration have no effect on the bound values.

```
addConstant = $c -> $x -> x + c;
add3 = addConstant(3);
println(add3(2)); # 5
```

```
foo = 1;
bar = $ foo;
foo = 2;
println(bar()); # 1
println(foo); # 2
```

Anonymous classes can be used as follows:

```
# mutable, no parameters
clazz = class();

# mutable, with parameters
clazz = class(@a, @b);
clazz = class(@a) {
  $getA() -> a;
};

# immutable, no parameters
clazz = class!();

# immutable, with parameters
clazz = class!(@a, @b);
clazz = class!(@a) {
  $getA() -> a;
};

# inheritance
derived = class(a, b, @c) : superclass(a, b);
derized = class!(@c) : superclass;
```

Note that anonymous classes can contain declarations that aren't anonymous, as shown with `getA`. Anonymous declarations are compared by reference. If the same anonymous declaration is evaluated twice, the resulting values are not equal, even if the same local variables were bound in the same parent object.

```
$getClass() -> class!();

clazz = getClass();
println(clazz == getClass()); # null
println(clazz() == getClass()()); # null
println(clazz() == clazz()); # true
```

## Objects
A syntactical shortcut exists for generating an anonymous class and getting an instance of this class without making the anonymous class accessible. This syntax utilizes the keyword `new`. These anonymous classes can be mutable or immutable and always have 0 parameters.

```
myObject0 = new {
  @foo = 1;
  $bar() -> foo++;
};

println(myObject0.foo); # 1
myObject0.bar();
println(myObject0.foo); # 2

# Is the same as:
# myObject = (class() {
#  @foo = 1;
#  $bar() -> foo++;
# })();

# Also works with inheritance:
myObject1 = new : MySuperclass(1, 2);

myObject2 = new : MySuperclass(1, 2) {
  @foo = 1;
};

# Immutable objects, doesn't work with inheritance:
myObject3 = new!;

myObject4 = new! {
  $foo() -> 1;
}
```

As shown in the code above, the syntax for inheritance involves the operator `:`, because without it inexperienced developers would make the mistake of writing `new MyClass()` when they would actually want to create a direct instance of the class instead of creating an instance of an anonymous class derived from it.

Similar to anonymous functions, evaluating the same object expression twice yields two different objects.

```
$getObject0() -> new;
$getObject1() -> new!;

println(getObject0() == getObject0()); # null
println(getObject1() == getObject!()); # null
```

A similar syntax can also be used to store objects directly as member variables.

```
new MyObject0; # semicolon required

new MyObject1 {
  @foo = 1;
  $bar() -> foo++;
} # no semicolon required

println(MyObject1.foo); # 1

new MyObject2 : MySuperclass(1, 2);

new! MyObject3;

new! MyObject4 {
  $foo() -> 1;
}
```

## Loaders
The class `Loader` is provided by Natlib and contains useful methods to gain dynamic access to the members of an object. So far, members could only be accessed using a static name using the `.` operator. `Loader` even allows injection of new members into mutable objects. This is useful for dynamically loading code and arranging namespaces. The methods of `Loader` are documented in the Natlib documentation, so this section only provides a brief overview.

```
loader = Loader(this); # creates a loader for the current object

loader.setValue("myValue", 1); # creates a new, immutable member variable
println(myValue); # 1

# not possible, because these dynamic members can only be manipulated via Loader
# myValue = 2;

loader.setValue("myValue", 2);
println(myValue); # 2
```

```
class Foo(@a, @b) {
  $getA() -> a;
}

obj = Foo(1, 2);
loader = Loader(obj);
println(loader["a"]); # 1
loader["a"] = 3; # change an existing member
println(obj.a); # 3
loader.setValue("c", 4);
println(loader.c); # 4
println(loader.allNames); # [a, b, getA, c]
println(loader.valueNames); # [c]
```

# Type System
This section goes into more detail on how the type system of Q3 works and how all data types relate to each other.

## The class Keyword
When `.class` is appended to a value, the expression evaluates to type of the value. This means that for all values `v` except `null` the expression `v instanceof v.class` is true. For example, `"abc".class == String`. `class` is not a member of all values, but only an operation that has a syntax similar to member accesses.

## Representative Instances
Each instance of `Funclass` has the member `rep` that returns an object that acts like an uninitialized, immutable instance of that class. The representative instance has all members and functions that a regular instance has. However, members are all `null` and functions can't be called. Most times, representative instances are needed for type implication checks, as explained in a following section. Another use case is accessing the methods of a class to retrieve documentation material without instantiating that class. The following expressions hold true for the given class:

```
class! Foo(@a) {
  # does stuff
  $bar() -> a;
}
```

* `Foo.rep.class == Foo`
* `Foo.rep.a == null`
* `Foo.rep.bar instanceof Funclass`
* `Foo.rep.bar.docs.text == "does stuff"`
* `Foo.rep == Foo.rep`

## Builtin Superclasses
The root of the class hierarchy in Q3 is the class `Any`. Primitive types like `String`, `Tuple` and `Number` are directly derived from `Any`. `Number` is an abstract class that inherits to `Integer`, `Float` and `Char`. Two special classes, namely `Funclass` and `Object` are also derived from `Any`. All functions and classes are instances of `Funclass`, but it is not their superclass. All objects are instances of `Object` and all user defined classes have `Object` as a superclass. There exist several class-object pairs in the type hierarchy.

| object | object.getSuperclass() | clazz | clazz.getSuperclass() | Purpose |
| :----: | :---------------: | :---: | :--------------: | :------ |
| | | Funclass | *Any* | All functions have `Funclass` as type. |
| Object | *Any* | Class | *Any* | All classes and enums. |
| UserObject | *Object* | UserClass | *Funclass* | All user defined classes. |
| UserObjectMutable | *UserObject* | UserClassMutable | *UserClass* | Mutable user classes. |
| UserObjectImmutable | *UserObject* | UserClassImmutable | *UserClass* | Immutable user classes. |
| NatObject | *Object* | NatClass | *Funclass* | Classes defined through Java code. |
| | | Enum | *Class* | An abstract superclass for enums. |
| UserEnumValue | *Object* | UserEnum | *Enum* | User defined enums. |
| NatEnumValue | *Object* | NatEnum | *Enum* | Enums defined through Java code. |

To illustrate these relations, the following expressions hold true, given the following code:

```
class! Foo();
$bar() -> true;
enum Color { RED, }
```

* `Foo().class == Foo`
* `Foo.class == UserClassImmutable`
* `Foo.getSuperclass() == UserObjectImmutable`
* `Foo().class.getSuperclass() == UserObjectImmutable`
* `Foo() instanceof UserObjectImmutable`
* `bar instanceof Funclass`
* `Color.RED.class == Color`
* `Color instanceof UserEnum`
* `Color.getSuperclass() == UserEnumValue`
* `Color.RED instanceof UserEnumValue`

## Equality for Enclosed Functions and Classes
Two non-anonymous funclasses originating from the same declaration are equal if their parent objects are equal. The following expressions hold true, given the following code:

```
class! Foo(@a) {
  class! Bar();
  $bar() -> a;
}

foo1 = Foo(1);
foo2 = Foo(1);
foo3 = Foo(2);
bar1 = foo1.Bar();
bar2 = foo2.Bar();
bar3 = foo3.Bar();
```

* `foo1.Bar == foo2.Bar`
* `foo1.bar == foo2.bar`
* `bar1 instanceof foo1.Bar`
* `bar1 instanceof foo2.Bar`
* `bar1 == bar2`

These expressions are false:

* `foo1.Bar == foo3.Bar`
* `foo1.bar == foo3.bar`
* `bar1 instanceof foo3.Bar`
* `bar1 == bar3`

## Type Implication
The Q3 language defines the binary type implication relation over all data types. The operator `=>` can be used to test if one value implies another. The operator `!=>` is the negation of the former operator, being equivalent to `!(x => y)`. The type implication relation has the following properties:

* **Reflexivity** - A value always implies itself, meaning `x => x` always holds true.
* **Antisymmetry** - If two values imply each other (`x => y && y => x`), they must be equal (`x == y`).
* **Transitivity** - If `x => y && y => z` holds, then `x => z` must also be true.
* For all values `x`, the expressions `null => x` and `x => true` hold true. This is analogous to the logic implication operator *→*, where *F → x* and *x → T* hold for all booleans.
* There is no `x` except `null` so that `x => null`.
* There is no `x` except `true` so that `true => x`.
* For all values `x` the expression `!x || Any.rep => x` holds.
* The type implication relation is false for all combinations that aren't defined here, according to the Closed World Assumption.

### Type Implication for Classes and Objects
For classes and tuples, some additional axioms are defined. To illustrate this, the following classes are given:

```
class! Superclass(@a, @b);
class! Derived(@c) : Superclass;
```

A class is implied by its superclass, meaning that `UserObjectImmutable => Superclass` and `Superclass => Derived`.

However, a class does not imply its instances (`Superclass !=> Superclass(1, 2)`). This is the purpose of representative instances, which imply all other instances of a class and of its derived classes:

* `Superclass.rep => Superclass(1, 2)`
* `Superclass.rep => Derived.rep`
* `Superclass.rep => Derived(1, 2, 3)`

An instance `a` of an immutable class implies another instance `b` of the same class or of a derived class if each member value of `a` implies the same member of `b`. This means that the following expressions hold true:

* `Superclass(_, 2) => Superclass(1, 2)`
* `Superclass(1, 2) => Derived(1, 2, 3)`
* `Superclass(1, _) !=> Superclass(_, 2)`
* `Superclass(3, 2) !=> Derived(2, 1, _)`
* `Derived(1, 2, 3) !=> Superclass(1, 2)`

### Type Implication for Tuples
The above axioms are only defined for immutable objects, but not for mutable objects. Similar axioms are defined for tuples and immutable maps. For one tuple `a` to imply tuple `b`, both tuples must have the same arity and for each index `i` the expression `a[i] => b[i]` must hold:

* `[] => []`
* `[_] => [1]`
* `[1, 2, _] => [true, 2, 3]`
* `[1] !=> [1, 2]`
* `[true] !=> [1]`

The class `AllTuple` represents an abstract tuple of arbitrary length that only contains one kind of element. This can be used to check if all elements in a tuple are of the desired type:

* `AllTuple(Integer.rep) => []`
* `AllTuple(Integer.rep) => [1]`
* `AllTuple(Integer.rep) => [1, 2]`
* `AllTuple(Integer.rep) => [true]`
* `AllTuple(Integer.rep) !=> [null]`
* `AllTuple(Integer.rep) !=> [1, "2", 3]`

### Type Implication for Immutable Maps
For an immutable map `a` to imply immutable map `b`, for all keys `k` in `a` the expression `a[k] => b[k]` must be true:

* `{} => {a: 1}`
* `{a: null} => {a: 1}`
* `{a: null, b: 2} => {a: 1, b: 2, c: 3}`
* `{a: 1} !=> {}`
* `{a: 1} !=> {a: null}`
* `{[String.rep, null]} !=> {a: 1}`

The class `AllImMap` represents an abstract map with an arbitrary number of key-value pairs that have a uniform type. This can be used to check if all elements in an immutable map are of the desired type:

* `AllImMap(String.rep, Integer.rep) => {}`
* `AllImMap(String.rep, Integer.rep) => {a: 1}`
* `AllImMap(String.rep, Integer.rep) => {a: 1, b: 2}`
* `AllImMap(String.rep, Integer.rep) => {true: true}`
* `AllImMap(String.rep, Integer.rep) !=> {a: "1"}`
* `AllImMap(String.rep, Integer.rep) !=> {a: null}`
* `AllImMap(String.rep, Integer.rep) !=> {a: 1, b: "2"}`

### Supertype and Subtype
The classes `Supertype` and `Subtype` can be used to create values that imply multiple values or that are implied by multiple values respectively. Both can be created using `Tuple.rep.toSupertype` and `Tuple.rep.toSubtype` or their own constructor. These classes implement the substript operator for element access and the members `size` and `toTuple`. Supertypes and subtypes don't imply instances of their own, because they are compared by reference. Note that instances of `Supertype` and `Subtype` are both instances of `Any` and not of their own classes. This means that they have no practical representative instances, i.e. `Supertype.rep !=> Supertype()`.

The class `Supertype` can hold 0 to n values. An instance `s` of `Supertype` implies a value `v` if there is an index `i` so that `s[i] => v`. Supertypes can be used to check if a value is an Integer or a String for example:

* `Supertype(Integer.rep, String.rep) => 1`
* `Supertype(Integer.rep, String.rep) => "a"`
* `Supertype(Integer.rep, String.rep) => true`
* `Supertype(Integer.rep, String.rep) !=> []`
* `Supertype(Integer.rep, String.rep) !=> Supertype(Integer.rep, String.rep, Float.rep)`
* `Supertype(Integer.rep) !=> Supertype(Integer.rep)`
* `Integer.rep !=> Supertype(1)`

The class `Subtype` does the opposite of `Supertype`. An instance `s` of `Subtype` is implied by a value `v` if there is an index `i` so that `v => s[i]`:

* `1 => Subtype(1, "a")`
* `"a" => Subtype(1, "a")`
* `Integer.rep => Subtype(1, "a")`
* `[] !=> Subtype(1, "a")`
* `Subtype(1, "a") !=> Subtype(1, "a", 2.0)`
* `Subtype(Integer.rep) !=> 1`

Following these definitions, an instance `sp` of `Supertype` implies an instance `sb` of `Subtype` if there are two indices `i` and `j` so that `sp[i] => sb[j]`:

* `Supertype(Integer.rep, String.rep) => Subtype(1, [])`
* `Supertype(Integer.rep, String.rep) !=> Subtype([])`

A special case is `Supertype(null)`. One would expect that `Supertype(null) => null`, but due to the axioms above `null => Supertype(null)` would follow, meaning that `Supertype(null) == null`, which is a contradiction. Therefore the behavior for `Supertype(null)` is undefined. The same goes for `true => Subtype(true)`, where `Subtype(true) == true` would follow.

### Use Cases for Type Implication
The major use case of type implication in Q3 is key matching. When a message (key) is broadcasted, all tasks that have subscribed to an object (lock) that implies the key, receive that message. This will be explained in a later section in detail.

Type implication can be used to check if an object is an instance of a class. The expressions `obj instanceof MyClass` and `MyClass.rep => obj` have similar semantics, with `instanceof` excluding subtypes and `true`.

Another obvious use case for the type implication relation is pattern matching, i.e. checking if the input matches the desired pattern. The definitions for tuples and maps can be used to validate a configuration schema for example.

```
employees:
  Alice:
    age: 31
    salary: 2900
  Bob:
    age: 24
    salary: 2400
weights:
- 25.3
- 0.5
- 3.14
```

After parsing the YAML configuration, the expression

```
{
  employees: AllMap(String.rep, {
    age: Integer.rep,
    salary: Integer.rep}),
  weights: AllTuple(Float.rep),
} => config
```

can be used to check whether the configuration file conforms to the desired schema. Note that the implication is still true if the configuration contains additional fields that aren't specified by the schema.

# Tasks
Tasks are one of the core concepts of Q3. Also referred to as coroutines or fibers in other programming languages, tasks are thread-like entities purely in the userspace that yield execution to other tasks manually rather than being interrupted by the system. The Q3M maintains a task queue and pops a task from the front of the queue and executes that task until the task yields. Tasks can start other tasks, resulting in a task tree or task hierarchy. The parent task inherits properties to its child tasks. If the parent task dies, all its children are forced to terminate.

A task can be in one of the following states:
* **QUEUED** - The task is queued up in the task queue and is executed soon.
* **RUNNING** - The task is being executed. The Q3M can execute at most one task at a time.
* **SLEEPING** - The task is sleeping for a finite amount of time.
* **WAITING** - The task is waiting for a future or for a message.
* **TERMINATED** - The task has terminated on its own or has been killed by another task.

## Sleep
The `sleep` keyword can be used as a statement, such as in `sleep 1000;`. A positive integer must be given indicating the time in milliseconds to sleep. The current task is suspended for the given amount of time and other tasks may be executed in the meantime. As soon as the timespan has passed, the task is placed in the task queue again. The statement `sleep;` is equivalent to `sleep 0;` and places the current task at the end of the task queue, yielding to the next task in the queue.

The code below prints either `Tick!` or `Tock!` every second:

```
while(true) {
  println("Tick!");
  sleep 1000;
  println("Tock!");
  sleep 1000;
}
```

## Futures
Futures are an intangible, transparent data type in Q3, whose value may not been known yet.

> Imagine going to a fast food restaurant and ordering food. You instantly receive a magical box at the counter. You take the box with you and go back to work/school/home. In the meantime, while you walk, the food is prepared. Once you've arrived, you decide to open the box and to eat your meal. Two scenarios can occur. A) The food has already been prepared and you can take it out of the box. B) You need to wait a few minutes until the food is finished and you can open the box.

Futures work in a similar fashion. They can be passed as variables and stored in collections, even if their value has not been filled yet. Only at the point when the exact value has to be known for some operation (equality check, method call, arithmetics ...), the program may block in order to wait for the future to be filled. The keyword `nf`, which stands for non-future, ensures that a value is not an unfilled future. It is used like `expr.nf` and doesn't block if the value is not a future or it is a future that has already been filled.

Some operations can be done on futures without blocking. A new future depending on the other future is created instead. Unary operations such as member access, unary `+` and `-`, `++` and `--` don't block on futures and give a so called indirect future.

It is also possible that a future can be filled with an exception. When trying to determine the value of the future, the current task crashes with the exception contained in the erroneous future. The keyword `catch` can be used to extract the exception from a future. If the future is indeed erroneous, the expression evaluates to the exception object. Otherwise it evaluates to `null`. It is used in a fashion similar to `nf`, such as `expr.catch`.

## fork
The keyword `fork` must be followed by an expression that evaluates to a function with 0 parameters. Upon execution, the given function is spawned as a child task of the current task and a future is returned. This future is later filled with the value returned by the given function.

```
value = fork $() {
  sleep 500;
  return "World";
};
sleep 250;
println("Hello");
println(value); # the main task blocks here, because the child task has not yet terminated
println("!");
```

The program above will print `Hello\nWorld\n!`.

In order to pass arguments to a function that needs to be forked, anonymous functions must be used, since fork only accepts parameterless functions:

```
$hello(name) -> println("Hello @name!");

a = fork $ hello("Alice!");
# b = fork hello("Bob!") # incorrect!
a.nf; # wait for the task to terminate
```

The following program repeatedly prints `Fizz` each 500ms and `Buzz` each 300ms. After 10sec, the program terminates, because the main task terminates and thus kills all child tasks.

```
fork $() {
  while(true) {
    println("Fizz");
    sleep 500;
  }
};
fork $ {
  while(true) {
    println("Buzz");
    sleep 300;
  }
};
sleep 10000;
```

## The Task Object
Properties of tasks can be manipulated through the `Task` object. `Task(function)` spawns a new task in the same way as `fork function` does, but instead of returning a future, an instance of `Task` is returned. This constructor offers other optional parameters, such as setting a timeout or an error handler. The `Task` object associated with the current task can always be accessed through the keyword `ctask`. `Task` has the member `retval` holding the future that will be filled with the return value of the task. The `Task` object can't be recovered from the future though.

```
task = Task(hello, args: ["Alice"], timeout: 1000, errorHandler: myErrorHandler);
```

Tasks can be killed forcefully by invoking the `task.kill()` function. This only works if the task is not the current task or a parent task of the current task. The return value of the killed task is then set to an erroneous future.

## Error Handling
When an exception is thrown either through a runtime error or by a `throw` statement, the Q3M checks if the current task has an error handler set. If no error handler is set, the exception is delegated to the parent tasks recursively. Before the error handler is invoked, all child tasks are killed. If not even the root task has an error handler, the Q3M crashes. The error handler must be a function with one parameter, to which the thrown exception is passed. A task invokes its error handler, the `errorHandler` member of that task is set to `null`.

The program below sets an error handler for the current task and outputs `Sum Tin Wong\nWi Tu Lo: Ho Lee Fuk`:

```
println("Sum Tin Wong");
ctask.errorHandler = $ex -> println("Wi Tu Lo: " + ex.getMessage());
throw Exception("Ho Lee Fuk");
println("Bang Ding Ow"); # not executed
```

The program below shows how an exception is delegated to the parent task if no error handler is set for the current task. The exception is thrown in the second task. The root task then kills all remaining tasks (the one that repeatedly prints `Beep`) and executes the error handler, printing `Oops null` and later `Bye`:

```
ctask.errorHandler = $(ex) {
  println("Oops " + ctask.errorHandler);
  sleep 1000;
  println("Bye");
};

fork $() {
  while(true) {
    println("Beep");
    sleep 100;
  }
};

fork $() {
  sleep 1000;
  throw Exception("Boop");
};

sleep 10000;
```

The function `toErrorFuture`, which is provided by Natlib, takes an exception object and converts it to an erroneous future filled with that exception. This function can be set directly as error handler for a task, causing it to return an erroneous future on error instead of delegating the error to its parent.

```
task = Task($() {
  throw Exception("foo");
}, errorHandler: toErrorFuture);

println(task.retval.catch.getMessage()); # foo
```

## Task Timeout
Q3 is a language with cooperative scheduling, meaning that tasks yield control to other tasks manually instead of being interrupted externally. The problem that arises is that a task could never give up control and thus block the entire Q3M from executing other tasks. This can be circumvented by setting a timeout for a task. The timeout value is automatically inherited to its child tasks unless these set their own timeouts. The timeout value tells for how many milliseconds the task can run without yielding. If this value is set to 0, the task is allowed run indefinitely. When the given timespan has elapsed, the task is interrupted forcefully at any place and an exception is thrown. A timeout can be set in the `Task` constructor or through the member variable `timeout`. If the latter method is used, the updated task must yield before the timeout takes effect.

The program below repeatedly prints `I'm busy!` until the timeout has elapsed, invoking the error handler:

```
ctask.timeout = 100; # set timeout to 100ms
ctask.errorHandler = $ex -> println("Timed out.");
sleep; # must yield before the timeout takes effect

while(true) {
  println("I'm busy!");
}
```

## The exec Function
The function `exec` is provided by Natlib and takes a function and a tuple as parameter. It kills all children of the current task and calls the given function with the values contained in the given tuple as arguments. After this function has returned, the task terminates with the return value of this function call.

The code below prints `ouch` instead of `nope` and the child task returns `2` instead of `1`:

```
$bar(num) {
  println("ouch");
  return num;
}

value = $() {
  println("let's call exec");
  exec(bar, [2]);
  println("nope"); # control flow doesn't come here
  return 1;
};

println(value); # 2
```

## Mailboxes
Each task has a mailbox that can subscribe to messages that should be received. The task can then take a message out of the mailbox or wait for a message to arrive in case there are no messages in the mailbox. A task can specify how many instances of a message can be received before the subscription expires. Other tasks can send a message to 1, n or all open mailboxes. However, it is not possible to send a message to a specific mailbox, meaning that all messages are broadcasted globally. Messages are only limited to instances of immutable objects. For the functionality described in this paragraph, the following statements are introduced:

* `want lock;` - Opens the mailbox of the current task for indefinitely many instances of messages that are implied by `lock`. Is the same as `want(lock, 0)`. Subscribing to the same object at the same time is undefined.
* `want(lock, n);` - Opens the mailbox of the current task for `n` instanceof of messages that are implied by `lock`. After the given amount of messages has been received, the subscription is cancelled. Does not cause an interrupt in the current task.
* `wait` - This expression takes a message out of the current task's mailbox and returns it. If no messages are in the mailbox, it waits for a message to arrive.
* `lock.wait` - This expression subscribes to `lock` and waits for the next message. Should not be used in combination with `want` in the same task, because it compiles to `want lock; wait`, leading to unexpected behavior!
* `key obj;` - Sends the message `obj` to the mailboxes of all tasks that have subscribed to `lock` so that `lock => key`. Is the same as `key(lock, 0)`.
* `key(obj, n);` - Sends the message `obj` to the mailboxes of at most `n` tasks. Does not cause an interrupt in the current task. If no mailbox has subscribed to the sent message at that time, the message is simply discarded.

The program below implements a classical producer-consumer pattern:

```
class! Payload(@receiver, @i);

@receiverNames = ["Bob", "Dave"]; # must be declared as member variable

$producer(name, speed) {
  while(true) {
    i = Math.rnd(9999);
    receiver = receiverNames[Math.rnd(receiverNames.size)];
    println("@name produces @i and sends it to @receiver.");
    key Payload(receiver, i); # broadcast message
    sleep Math.rnd(speed);
  }
}

$consumer(name, speed) {
  want Payload(name, null); # receive only messages for this consumer
  while(true) {
    payload = wait;
    println("@name received " + payload.i + ".");
    sleep Math.rnd(speed);
  }
}

fork $ producer("Alice", 500);
fork $ producer("Eve", 1500);
foreach(receiverNames, $receiver -> fork $ consumer(receiver, 1000));

ctask.retval.nf; # wait indefinitely
```

# Native Library
This section gives a brief overview over the classes provided by Natlib through short code samples. The complete documentation can be found in the Natlib documentation.

There are two major collections of functions provided by the default Q3 environment: Natlib and Stdlib. Natlib (*Native Library*) defines its functions and classes using Java code. It is integrated directly into the *q3-machine* Maven project. Stdlib (*Standard Library*) on the other hand is written in Q3 and is contained in the *q3-standalone* Maven project. Its source files are located in the `std/` directory of the Q3 git repository.

Q3 lives up to the philosophy that "everything" is an object, so even primitive data types have members and functions. The only value not behaving like an object is `null`.

## Asynchronous Functions and Members
Some functions and members in Natlib or external native libraries are marked as asynchronous. This means that when such an operation is accessed, a future is returned without blocking and the result is computed asynchronously in another thread. Such asynchronous operations include, but are not limited to, IO operations or HTTP requests. It can also be the case that the host system and the Q3M run in separate threads and that certain operations are only allowed in the host system thread.

Some host systems send events to the Q3M as messages and need a way for the Q3M to modify or cancel those events before they are further processed by the host system. Because messages are normally sent to the Q3M in an asynchronous way, this would normally not be possible. However, the host system can send the message to the Q3M and wait until all tasks, that have been waiting for that message, have completed one round. While the host system waits, asynchronous operations associated with the host system are executed synchronously.

## String
The `String` object contains many useful methods for string manipulation. Other string functions can be found under the `Strings` namespace.

```
i = 123;
str = "Hello World!";

println("@i"); # converts i to a string
println(String(i)); # same as above

println("321".toInt); # converts the string to an integer
println("abc".toInt); # is null if conversion is not possible

println(str.length); # 12
println(str.substring(6)); # World!
println(str.substring(6, 11)); # World

# removes whitespaces before and after the string
println("   hello\n".trim); # hello

println(str.toUpperCase); # HELLO WORLD!
println(str.toLowerCase); # hello world!

# access character at index
println(str[1]); # e
```

The class `StringBuilder` allows for the concatenation of many strings in a memory efficient way.

```
names = ["Alice", "Bob", "Chris"];
str = StringBuilder();

for(name: names) {
  str[] = name;
  str[] = ',';
}

println(str.toString()); # Alice,Bob,Chris,
```

## Numbers
`Integer`, `Float` and `Character` implement the members `toInt`, `toFloat`, `toChar` to convert between these three data types.

`Integer` has the function `format` to convert the number to different notations:

```
println((123).format(10)); # 123
println((123).format(16)); # 7b
println((123).format(2)); # 1111011

# print as unsigned hex integer
println((-123).format(16, false)); # ffffffffffffff85
```

`Character` has additional members that can be used to check whether a character falls into a certain category:

* `isDigit`
* `isLowercase`
* `isUppercase`
* `isWhitespace`
* `isLetter`
* `isAlphanumeric`

## Collections
Collections are objects that can store an arbitrary amount of other values of various types. Whether those values are stored in an ordered way or whether the collection is mutable depends on the kind of collection. All collections implement these members:

* `size` - The amount of elements present in this collection.
* `empty` - True if there are no elements in this collection.
* `toList` - Returns a new List with the same elements as in this collection.
* `toSet` - Returns a new Set with the same elements as in this collection.
* `toQueue` - Returns a new Queue with the same elements as in this collection.
* `toTuple` - Returns a tuple with the same elements as in this collection.
* `iterator()` - Returns a fresh iterator over the elements in this collection.

The conversion members only return a new instance of a collection, but don't copy the elements (shallow copy).

### Tuple
Is immutable and ordered. Elements can be accessed by their index.

```
println([].empty); # true
println([1].empty); # null
println(['a', 'b', 'c', 'd'].size); # 4
println(['a', 'b', 'c', 'd'].sublist(1)); # [b, c, d]
println(['a', 'b', 'c', 'd'].sublist(0, 3)); # [a, b, c]
```

### List
Is mutable and ordered. Elements can be accessed by their index. Provides more methods than `Tuple`.

```
l = List(1, 2, 3, 4);

println(l.contains(2)); # true
println(l.contains(5)); # null

l.remove(3);
println(l); # [1, 2, 4]
l.removeAt(1);
println(l); # [1, 4]
println(l.removeLast()); # 4
println(l); # [1]
l[] = 5;
l.add(6, "a");
println(l); # [1, 6, a]
l.clear();
println(l); # []
```

### Set
Is mutable and not ordered. Each element can only exist at most once in a Set.

```
s = Set(1, 2, 3);

println(s.contains(1)); # true
println(s.contains(4)); # null

println(s.add(1)); # null
println(s.add(4)); # true

println(s); # [1, 2, 3, 4]
```

### Queue
Is mutable and ordered. Elements can't be accessed by their index. Is optimized so that elements can be efficiently removed from the front and added to the back.

```
q = Queue(1, 2, 3);

println(q.remove()); # 1
println(q); # [2, 3]
println(q.remove()); # 2
q.add(4);
println(q); # [3, 4]
```

## Map and ImMap
A Map is not a collection because it contains key-value pairs instead of independent elements. Each key is immutable within a Map and values can be accessed by their keys. While `ImMap` is immutable and can be expressed as a literal using curly braces, `Map` is mutable. These two can be converted between each other using the members `toMap` and `toImMap`. All arguments of in the Map-constructor must be binary tuples of the key and its value. An equivalent syntax using `:` exists where identifiers are interpreted as strings.

Both `Map` and `ImMap` have members such as `keysToTuple`, `valuesToTuple` and `entriesToTuple` that give the content of the map as a tuple, as shown in the code below. The content of a map can be iterated over by using tuple-assignment in a foreach-loop:

```
bob = ["Bob", 24];
m = Map(Alice: 25, bob, ["Chris", 32]);

println(m.contains("Alice")); # true
println(m["Alice"]); # 25
println(m["Dave"]); # null
m.setDefault(0);
println(m["Dave"]); # 0
m["Alice"] = 26;
m.put("Dave", 24);
m.remove("Chris"); # 32
m.remove("Eve"); # 0
println(m); # {Alice: 26, Bob: 24, Dave: 24}

println(m.keysToTuple); # [Alice, Bob, Dave]
println(m.valuesToTuple); # [26, 24, 24]
println(m.entriesToTuple); # [[Alice, 26], [Bob, 24], [Dave, 24]]

for([name, age]: m) {
  println("@name: @age");
}
```

## Iterators
As explained in a previous section, foreach-loops work by calling `.iterator()` on the collection passed to them. This function must return an object that implements the methods `hasNext()` and `next()`. `hasNext()` returns true if the iterator still contains elements. `next()` advances the iterator by one element and returns the next element. This can be used to write custom iterators that can be used in a foreach-loop.

The program below prints all numbers from 0 to 9 by using a custom iterator and a foreach-loop:

```
class MyIterator() {
  @i = 0;
  
  $hasNext() -> i < 10;
  $next() -> i++;
  
  # shortcut for avoiding MyIterable
  # $iterator() -> this;
}

class MyIterable() {
  $iterator() -> MyIterator();
}

for(num: MyIterable()) {
  println(num);
}
```

## BinaryString
Because strings in Q3 are interpreted as unicode strings, another data structure is needed to represent binary data. The class `BinaryStringBuilder` can be used in a similar fashion as `StringBuilder` to create instances of `BinaryString`. Contrary to strings, binary strings are mutable, but not resizeable.

```
builder = BinaryStringBuilder();
builder.writeInt16(0xBEEF);
builder.writeBoolean(true);

str = builder.toBinaryString();
println(str); # BEEF01
println(str.length);

view = str.substringView(1);
view[1] = 2;
println(view); # EF02
println(str); # BEEF02
println(str[1]); # 190
```

## Base64
The namespace `Base64` contains methods for encoding binary strings into strings using the Base64 encoding scheme. URL encoding, where only URL friendly characters are used, is also supported.

```
str = "x/f8+2de";
binary = Base64.decode(str);
println(binary); # C7F7FCFB675E
urlstr = Base64.encodeUrl(binary);
println(urlstr); # x_f8-2de
```

## Serialization
The namespace `Serializer` offers functions that allow conversion between primitive data types and binary data. For example, `Serializer.serializeToBinary([123, "abc"])` returns the binary string `51334F626A65637446696C65000003E8060000000203000000000000007B02000003616263`, which can be converted back to the original data using `Serializer.deserializeFromBinary(binary)`.

## ImmutableView
The class `ImmutableView` can wrap any value and behaves like the wrapped value, except that member assignments, loader modifications and subscript assignments are disabled. Changes to the original value are still reflected by the instance of `ImmutableView`. Methods that modify the wrapped value still work as intended. This can be used to protect the host system from unwanted modifications by untrustworthy code. All resources within the wrapped value have to be immutable views themselves for this to work correctly.

```
class MyClass(@a) {
  $setA(a) -> this.a = a;
}

obj = MyClass(3);
imm = ImmutableView(obj);

println(imm.a); # 3

# will throw an error
# imm.a = 4;
# Loader(imm).setValue("b", 6);

imm.setA(4); # fine
println(obj.a); # 4
println(imm.a); # 4

obj.a = 5;
Loader(obj).setValue("b", 6);

println(imm.a); # 5
println(imm.b); # 6
```

## Funclass
All functions and classes are instances of `Funclass`. This class offers various getters to gain access to information such as parameters or members of the function in question.

```
# does stuff
# params: int; string
# returns: MyClass
class MyClass(@a, b ...) {
  @c = b;
  $foo() -> a;
}

println(MyClass.name); # MyClass
println(MyClass.params); # [a, b]
println(MyClass.members); # [a, c]
println(MyClass.methods); # [foo]
println(MyClass.hasExtraParam); # true
println(MyClass.getSuperclass()); # UserObjectMutable
println(MyClass.docs.text); # does stuff
println(MyClass.docs.params); # [int, string]
println(MyClass.docs.returns); # MyClass
```

The class `NatClass` has an additional method `getVariableDoc(name)` that returns the documentation material for a member variable. The expression `String.getVariableDoc("length").operations[0].text` evaluates to the documentation for `String.empty` for example. Since members of native classes behave differently depending on the access operation (get, assign, +=, -=, ...), a tuple of documented operations is returned.

## Time
The namespace `Time` contains functions associated with time and date. The expression `Time.time()` returns the amount of milliseconds passed since 1970-01-01 00:00:00. `Time.timens()` returns a timestamp in nanoseconds, which should only be used for measuring timespans. The function `Time.format` uses the same format as specified in [JavaDocs for SimpleDateFormat](https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html).

## Math
The namespace `Math` contains various deterministic mathematical functions, as well as functions to generate random numbers with. Constants such as `Math.PI` or `Math.E` are also included. `Math.rnd()` returns a pseudorandom 64bit integer. `Math.rnd(n)` returns a pseudorandom integer between 0 and n - 1. `Math.rnd(i, j)` does the same with an interval of i to j - 1. `Math.rndFloat()` returns a random float between 0.0 and 1.0.

## Yaml
The functions `Yaml.toYaml` and `Yaml.parse` can be used to convert primitive types to and from the YAML configuration format. The root of a YAML configuration must always be a map. Maps are represented as `ImMap` and lists as tuples. The following map and configuration are equivalent in their contents:

```
{
  name: "Server",
  items: [1, 2, 3],
}
```

```
name: Server
items:
- 1
- 2
- 3
```

## Files
The class `File` represents a path to a file or directory which may or may not exist. `File("myFile.txt")` creates a file locator relative to the working directory (WD) of the current task and `File(directory, "myFile.txt")` does the same relative to the given directory. These file operations are synchronous to the Q3M thread and may thus block.

```
dir = File("myFolder");
file = File(dir, "myFile.txt");

println(file.name); # myFile.txt
println(file.parent); # myFolder
println(dir.isDirectory()); # true if the given file exists as a directory
println(dir.isFile()); # true if the given file exists as a file
println(dir.exists()); # true if the given file exists as a directory or a file

dir.mkdir(); # create new directory
println(dir.ls()); # files in directory as tuple

file.setText("Hello"); # set content of file to a string
file.addText(" World!"); # append to end of file
file.setBinary(Serializer.serializeToBinary(123)); # write binary data to file

println(file.getText()); # read file as string
println(file.getBinary()); # read file as binary string
println(file.getTime()); # get last modified timestamp
```

Furthermore, WDs can be assigned to tasks, meaning that a task can't access files located outside its WD. WDs are inherited to child tasks. A task can only assign its own WD or children within its WD to other tasks. If no WD is set, a task has access to the whole file system. The default WD is in this case the WD set to the Q3M. The function `File.hasPriviledge()` can be used to check if the current task has permission to read or write to that file.

## Compiling and Loading Code
The function `Compiler.compileCodeString` allows for the compilation of Q3 code at runtime. It has 3 parameters, namely the code given as a string, the name of the resulting class and a boolean that sets whether compiler warnings are treated as errors. It returns a tuple of a `CodeFile` and a string. The string contains the compiler log including errors and warnings. If an error occurred, the first element of the tuple is `null`.

The class `CodeFile` represents an uninitialized portion of compiled Q3 code. It can be serialized to or deserialized from a file. The member `meta` contains information about the compiled unit including the text in the header of the code. It also has a function called `createClass`, which takes an object as a parameter and creates a class from the compiled code. The resulting class behaves as if it was a member of the given object.

The function `createEmptyClass`, which is provided by Natlib, creates an instance of `UserClassMutable` without members or functions. Similar to `CodeFile.createClass`, it takes an object and a string for the class name.

In both functions, the object parameter can be null, which means that the loaded code has no access to outside resources by default.

The code below demonstrates the features discussed in this section:

```
[codeFile, log] = Compiler.compileCodeString("# hello\n\na + b()", "test", true);
println(log);

if(!codeFile) {
  println("Compilation failed.");
  return;
}

class OutsideClass(@a) {
  $b() -> 2;
}

obj = OutsideClass(1);
println(codeFile.meta["header"].trim); # hello

# the loaded code should have direct access to the members of obj
# this does not execute any of the loaded code yet
clazz = codeFile.createClass(obj);

# execute the loaded code
println(clazz()); # 3
```

## Loggers
A logger is an object that has the methods `print(msg)` and `readln()`. When `print` is called with a message as parameter, the message shall be printed without a line break. `readln` may return a future that will be filled with the next line read. The Q3 standalone client implements its own logger that interacts with the standard console. Each `Task` has the member `logger`, where any object that conforms to these specifications can be stored. The value of this member is inherited to child tasks. This feature can be used to redirect the logging output of a task. The `println` function that is used in this document so often is a function implemented by Stdlib the following way:

```
$println(msg) -> ctask.logger.print("@msg\n");
```

This means that `println` writes a line to the logger of the current task.

# Formatting Conventions
Even if the compiler doesn't care about the legibility of the code, some additional standards should be maintained to ensure some quality in the codebase.

Naming Conventions similar to those in Java apply to Q3 for identifiers:

| Type of Identifier | Convention |
| :----------------- | :--------: |
| Classes, enums, libraries | `ThisIsAnExample` |
| Local variables, member variables, function names | `thisIsAnExample` |
| Enum values, constants | `THIS_IS_AN_EXAMPLE` |

Trailing whitespaces, which are whitespaces immediately before a line break, should be avoided. The rules below are also overridden when no non-whitespace character precedes the token in the same line. Multiple whitespaces should only be used for indentation or aligning.

One space should be set where syntactically necessary. Otherwise spaces should only be set in the following cases:

* Before and after binary operators. Example `1 + 2`, counterexample `1+2`.
* After **unary** statement keywords such as `return`, `throw`, `want`. Example `return "abc";` or `want(msg, 1); # not an unary statement in this case`, counterexample `return"abc";`.
* After `;`. Example `sleep 10; return "abc";`, counterexample `sleep 10;return "abc";`.
* Before `#` if the line is not empty. Example `doStuff(); # does stuff`, counterexample `doStuff();#does stuff`.
* Before and after `->`.
* After the parameter list of a function.
* After `$` if no parameter list follows.
* After `,` unless no more items follow in the enumeration.
* In between block braces. Example `$foo() { return 1; }`, counterexample `$foo() {return 1;}`.
* After `:` in map literals and foreach loops.
* Before and after `?` and `:`. Example `true ? 1 : 2`, counterexample `true?1:2`.
* Before and after `:` in class declarations.
* `class! Foo()` is preferred over `class !Foo()`.
* After the name of an enum. Example `enum Color { RED, }`, counterexample `enum Color{ RED, }`.

Indentation should be increased by one level after entering a block and should be decreased before the closing brace of that block. Line breaks and indentation for aligning long function calls or tuple/map literals are ok. Tabs or spaces can be used for indentation, but NOT tabs and spaces mixed.

One line break should be set:

* After a statement, i.e. after `;`.
* After the opening brace of a block.
* At the end of the file.

Two line breaks should be set:

* After the closing brace of a non-anonymous function or class declaration.
* After a non-anonymous function declaration of the form `-> ...;`.

# Common Pitfalls

## Assigning Member Variables without `this` Keyword
The function `setMember` code below attempts to change the value of `member`. However, a local variable called `member` is allocated instead. To change the value of member variables, `this.` must precede the member variable, meaning that `this.member = value` would be correct.

```
@member = 5;

$getMember() -> member; # no problem
$setMember(value) -> member = value; # incorrect
$setMemberCorrect(value) -> this.member = value; # incorrect
```

## Forgetting `$` after `fork`
It can happen that instead of creating a new task, the program blocks or a runtime error is thrown saying that a function was expected. This can happen when trying to pass arguments to a function when forking. The solution is to put a `$` after `fork`, so an anonymous function is created.

```
$foo(name) {
  sleep 1000;
  println("Hello @name!");
}

fork foo("Alice"); # incorrect, executes the function before forking
fork $ foo("Alice"); # correct, hands an anonymous function to fork
```

## Expecting Your Q3 Program to be Thread Safe
While the internal workings of the Q3M are synchronized and thread safe, race conditions can occur in any environment where a task can be arbitrarily interrupted and another task is executed. Interrupts only occur in places that the programmer is aware of, such as in `wait`, `sleep` or when an unresolved future is encountered. It's still possible to get race conditions when not being careful and aware of these interrupts. It is good practice to make use of `nf` before transactions that should be atomic.

## Not Converting to Float when Dividing
An integer divided by an integer yields another integer and not a decimal number. So the expression `3 / 2` evaluates to `1` instead of `1.5`. To mitigate this, one can write `1.0 / i` or `1 / i.toFloat` for example.

## Assuming Tasks are Executed in a Specific Order
The order in which tasks are executed is not defined in Q3. So it can be possible that a message is sent before another task starts listening to that message. A solution is to listen to a message as early as possible:

```
want someMessage; # correct

fork $() {
  ...
  key someMessage;
};

...

want someMessage; # incorrect, might miss the message

received = wait;
```

## Letting the Parent Task Terminate before its Children
Keep in mind that all child tasks are forced to terminate when a task terminates. This can lead to the unintentional behavior that some tasks are only partially executed or not executed at all, because their parent terminated before them. The solution is to let the parent task either wait forever or wait for its children to terminate.

## Ignoring Compiler Warnings
If the compiler outputs an error or a warning, the code sure smells fishy. Compiler warnings are given when the code can be compiled, but when a runtime error is likely. It is good advice to use the -wError flag when compiling production code.

