# AEVUM REPL

## Creator

Brethren Ace D. de la Gente  
Sophe Mae C. Dela Cruz

## Language Overview

[Provide a brief description of your programming language - what it's designed for, its main characteristics]  


## Keywords

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

## Operators

### Arithmetic Operators

| Operators | Name |
| :---: | :---: |
| + | Addition |
| - | Subtraction |
| * | Multiplication |
| / | Division |

### Comparison Operators

| Operators | Name | Example | 
| :---: | :---: | :---: |
| == | Equal to | x == y |
| != | Not equal | x != y |
| > | Greater than | x > y |
| < | Less than | x < y |
| >= | Greater than or equal | x >= y |
| <= | Less than or equal | x <= y |

### Logical Operators

| Operators | Name | Description | 
| :---: | :---: | :--- |
| and | Logical AND | Returns *true* if both statements are *true*. Otherwise, *false* |
| or | Logical OR | Returns *true* if at least one of the statements is *true* |
| nil | Logical NOT | Returns *true* if the statement is *false* and vice versa |

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

## Design Rationale

[Explain the reasoning behind your design choices]
