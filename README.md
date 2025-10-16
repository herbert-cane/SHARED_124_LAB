# AEVUM REPL

## Creator

Brethren Ace D. de la Gente  
Sophe Mae C. Dela Cruz

## Language Overview
Aevum is a high-level, general-purpose programming language designed specifically for creating interactive text-based RPGs. It allows developers to easily create branching stories, dialogue systems, and game flow mechanics using simple and intuitive constructs. 

Aevum simplifies the learning process for beginners by offering game-specific keywords like ```speak```, ```choice```, ```option```, ```action```, and ```endgame```, making it easier to focus on story development and interactive gameplay. Aevum's primary focus is on text processing and branching logic, allowing developers to design compelling narrative-driven games while learning foundational programming principles.
## Built-in Keywords

* ```and``` - a logical operator that returns _true_ if both statements are _true_. Otherwise, returns _false_.
* ```or``` - a logical operator that returns *true* if at least one of the statements is *true*
* ```nil``` - returns *true* if the statement is *false* and vice versa.
* ```true``` - a boolean value that evaluates to _true_ and allows a statement or condition to proceed to its code block.
* ```false``` - a boolean value that evaluates to _false_.
* ```if``` - a conditional statement that execute a code block if condition is met.
* ```else``` - an alternative code block if the ```if``` condition evaluates to _false_.
* ```for``` - a control flow statement that iterates a block of code.
* ```while``` - a control flow statement that loops through a block of code.
* ```do``` - a type of while loop that executes the code block once before evaluating the condition.
* ```print``` - is a method that is used to display text, variables, literal values on the terminal.
* ```class``` - an object-oriented programming keyword that is used to define a class.
* ```fun``` - this is used to define a function or method.
* ```return``` - returns the value and exits from the function to return back to the caller.
* ```super``` - a reference variables that refers to the superclass or parent class object.
* ```this``` - a reference variable that refers to the current object in the constructor or a method.
* ```var``` - used to define a variable inside a function or code block.

## Dialogue and Character Keywords

* ```speak``` - display dialogue or messages from characters.
* ```character``` - definesa game scene where actions and objects exist.
* ```say``` - used for character dialogue.

## Choices and Branching
* ```choice``` - present a choice to the player.
* ```option``` - option for the player to choose.
* ```spawn``` - spawn a new game object into the scene.

## Game Flow
* ```start``` - start the game or a new scene.
* ```continue``` - continue the game or move to the next scene.
* ```restart``` - restart the game from the beginning or a checkpoint.
* ```endgame``` - signify the end of the game or story.

## Actions and Triggers 
* ```action``` - represent a specific action or event in the game (like fighting, exploring, etc.).
* ```trigger``` - trigger an event or consequence in the game.
* ```win, lose``` - represent outcomes in the game.

## Inventory [OPTIONAL]
* ```inventory``` - list or view items in the inventory.
* ```item``` - represent an item in the inventory.
* ```add``` - add an item to the inventory.
* ```use``` - use an item in the game.

## Stats [OPTIONAL]
* ```HP``` - define the health of a character.
* ```ATK``` - define the attack points of a character.
* ```DEF``` - define the defense points of a character.

## Operators

### Arithmetic Operators

| Operators |      Name      |
|:---------:|:--------------:|
|     +     |    Addition    |
|     -     |  Subtraction   |
|     *     | Multiplication |
|     /     |    Division    |

### Comparison Operators

| Operators |         Name          | Example | 
|:---------:|:---------------------:|:-------:|
|    ==     |       Equal to        | x == y  |
|    !=     |       Not equal       | x != y  |
|     >     |     Greater than      |  x > y  |
|     <     |       Less than       |  x < y  |
|    >=     | Greater than or equal | x >= y  |
|    <=     |  Less than or equal   | x <= y  |

### Logical Operators

| Operators |    Name     | Description                                                      | 
|:---------:|:-----------:|:-----------------------------------------------------------------|
|    and    | Logical AND | Returns *true* if both statements are *true*. Otherwise, *false* |
|    or     | Logical OR  | Returns *true* if at least one of the statements is *true*       |
|    nil    | Logical NOT | Returns *true* if the statement is *false* and vice versa        |

## Literals

### Numeric Literals
* **Integer literals**
  * These are numbers without a decimal point.
  * For example, ```x = 1```, ```y = 500```

* **Floating-point literals**
  * These are numbers containing a decimal point.
  * For example, ```x = 3.14159```, ```y = 14.75```
 
### String Literals
* **Double-quoted String literal**
  * String literals that are enclosed in double quotes " "
  * For example, ```"Hello, world!"```
 
### Character Literals
A single character literal enclosed in single quotes ' '
  * For example, ```x = 'C'```, ```ch = 'a'```, ```ch = 'x'```

## Identifiers  
* Identifiers can start with a letter and with the following characters: `, ~, @, #, $, %, ^, &
* To declare a variable, a _var_ keyword must be used at the start of the declaration. For example, ``` var x = 5 ```
* Identifiers, such as in variables and function names, should not have a whitespace and are **_case-sensitive_**.
* For good programming practice, identifiers must begin with an underscore (_) or with a lowercase letter.
* Reserved keywords cannot be used as identifiers.
* Identifiers with multiple words should be in camelcase format. For example, ```_convertToString()```

## Comments  
A single-line comment starts with double forward slash **//**.  
A multiline or block comment starts with a forward slash and an asterisk **/*** and ends with ***/**  
Nested comments are supported.
```
// This is a single-line comment

/* This is a multi-line comment that starts here
    and ends here */ 
```

## Syntax Style  
* Statements are terminated with a newline character for readability.
* For control flow statements and functions, curly braces {} are used for code blocks and () are used for control flow and function declarations, groupings of statements or expressions.
* For code block definition, tabs must be used for readability and maintainability.
### Example
  ```
  for(condition) {
    // code to execute
  }

  var x = (y + 10) - (z * 5)  
  ```
## Grammar

### Program Structure
program        → declaration* EOF

### Declarations
declaration    → varDecl | statement | gameDecl
varDecl        → "var" IDENTIFIER ( "=" expression )? newline
gameDecl       → sceneDecl | dialogueDecl | choiceDecl | actionDecl

### Game-specific Declarations
sceneDecl      → "start" STRING newline
               | "continue" STRING newline
               | "restart" newline

dialogueDecl   → "speak" expression newline
               | "say" expression newline

choiceDecl     → "choice" "{" newline option+ "}" newline
option         → "option" STRING "->" "{" newline statement* "}" newline

actionDecl     → "action" STRING "->" "{" newline statement* "}" newline
               | "trigger" STRING newline
               | "win" expression newline
               | "lose" expression newline
               | "endgame" expression newline

### Statements
statement      → exprStmt | ifStmt | forStmt | whileStmt | block | returnStmt | printStmt
exprStmt       → expression newline
ifStmt         → "if" "(" expression ")" statement ( "else" statement )?
forStmt        → "for" "(" ( varDecl | exprStmt | ";" ) expression? ";" expression? ")" statement
whileStmt      → "while" "(" expression ")" statement
               | "do" statement "while" "(" expression ")" newline
block          → "{" declaration* "}"
returnStmt     → "return" expression? newline
printStmt      → "print" expression newline

### Expressions
expression     → assignment
assignment     → IDENTIFIER "=" assignment | logic_or
logic_or       → logic_and ( "or" logic_and )*
logic_and      → equality ( "and" equality )*
equality       → comparison ( ( "!=" | "==" ) comparison )*
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )*
term           → factor ( ( "-" | "+" ) factor )*
factor         → unary ( ( "/" | "*" ) unary )*
unary          → ( "!" | "-" ) unary | call
call           → primary ( "(" arguments? ")" )*
primary        → "true" | "false" | "nil" | NUMBER | STRING | IDENTIFIER | "(" expression ")"
               | "character" | "spawn" | "inventory" | "item" | "add" | "use"
               | "HP" | "ATK" | "DEF"

### Rules

arguments      → expression ( "," expression )*
newline        → "\n"

## Sample Code

[Provide a few examples of valid code in your language to demonstrate the syntax and features]

```
var x = true
var y = false

print(nil x) //outputs false
print(x and y) // false
print(x or y) // true
```

```
for (x in list) {
  // code block
}
```

```
start "The Adventure Begins"

speak "You are standing in front of a dark forest. What will you do?"

choice {
    option "Enter the forest" -> {
        speak "You step into the forest, the trees whispering around you."
        action "Fight"
    }
    option "Stay outside" -> {
        speak "You decide to stay outside, wondering what lies within."
        action "Explore"
    }
}

action "Fight" -> {
    speak "A wild beast appears! Do you want to fight it?"
    choice {
        option "Fight" -> {
            attack 20
            speak "You defeat the beast!"
            endgame "Victory!"
        }
        option "Run" -> {
            speak "You run away safely."
            continue "The adventure continues."
        }
    }
}

action "Explore" -> {
    speak "You explore the surroundings and find a treasure chest!"
    choice {
        option "Open the chest" -> {
            item "Sword"
            speak "You find a powerful sword!"
            continue "The adventure continues."
        }
        option "Leave it" -> {
            speak "You decide to leave the chest untouched."
            continue "The adventure continues."
        }
    }
}

endgame "Game Over"

```

## Design Rationale  
Aevum is a beginner-friendly language made for creating interactive text-based RPGs. It focuses on simple commands for dialogue, choices, and branching stories, letting you dive straight into building your game while learning programming basics. 

The syntax is easy to understand, and while it's simple, it still allows for cool features like inventories and character stats. Aevum makes it easy to design immersive narratives without getting bogged down by complex mechanics.The creators of Aevum decided to create a high-level, object-oriented programming language with game development in mind. This allows programmers to easily integrate complex game mechanics such as movement, animations, collision detection, and physics while learning object-oriented programming principles. Aevum is designed to be beginner-friendly, while maintaining powerful features that can be used to create full-fledged games.
