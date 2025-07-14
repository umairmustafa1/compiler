# Compiler
Compiler implemented in Java  

## Structure
JackAnalyzer.java : Main class to run the program.  
JackTokenizer.java : Class with static utility methods to tokenize the input.  
CompilationEngine.java: Class with static utility method to compile the token list.  
Token.java: Class to capture parameters related to a token.  
Element.java: Enum to hold 5 types of lexical elements.  

## Compilation Instructions
javac JackAnalyzer.java  
java JackAnalyzer "[Dir Path]"