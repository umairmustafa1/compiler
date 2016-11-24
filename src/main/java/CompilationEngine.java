import javafx.scene.input.TouchEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility Class for generating compiled XML with static methods
 */
public class CompilationEngine {

    public static List<String> compileClass(List<Token> tokens){
        List<String> compiledXML = new ArrayList<>();
        compiledXML.add("<class>");
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//class
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//className
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//{

        while(tokens.get(0).getToken().equals("static") || tokens.get(0).getToken().equals("field"))
            compileClassVarDec(tokens, compiledXML);

        while(tokens.get(0).getToken().equals("constructor") || tokens.get(0).getToken().equals("function") || tokens.get(0).getToken().equals("method"))
            compileSubroutine(tokens, compiledXML);

        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//}
        compiledXML.add("</class>");
        return compiledXML;
    }

    public static void compileClassVarDec(List<Token> tokens, List<String> compiledXML){
        compiledXML.add("<classVarDec>");
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//static | field
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//type
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//varName
        while (!tokens.get(0).getToken().equals(";")){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//,
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//varName
        }
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//;
        compiledXML.add("</classVarDec>");
        return;
    }

    public static void compileSubroutine(List<Token> tokens, List<String> compiledXML){
        compiledXML.add("<subroutineDec>");
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//constructor | function | method
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//void | type
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//subRoutineName
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//(

            compileParameterList(tokens, compiledXML);

        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//)

        compiledXML.add("<subroutineBody>");
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//{
        //varDec*
        while (tokens.get(0).getToken().equals("var")){
            compileVarDec(tokens, compiledXML);
        }
        //statements
        compileStatements(tokens, compiledXML);

        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//}
        compiledXML.add("</subroutineBody>");

        compiledXML.add("</subroutineDec>");
        return;
    }

    public static void compileParameterList(List<Token> tokens, List<String> compiledXML){
        compiledXML.add("<parameterList>");
        if(!tokens.get(0).getToken().equals(")")){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//type
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//varName
            while (tokens.get(0).getToken().equals(",")){
                compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//,
                compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//type
                compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//varName
            }
        }
        compiledXML.add("</parameterList>");
        return;
    }

    public static void compileVarDec(List<Token> tokens, List<String> compiledXML){
        compiledXML.add("<varDec>");
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//var
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//type
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//varName
        while (tokens.get(0).getToken().equals(",")){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//,
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//varName
        }
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//;
        compiledXML.add("</varDec>");
    }

    public static void compileStatements(List<Token> tokens, List<String> compiledXML){
        compiledXML.add("<statements>");
        while(tokens.get(0).getToken().equals("let") || tokens.get(0).getToken().equals("if") || tokens.get(0).getToken().equals("while") || tokens.get(0).getToken().equals("do")
                || tokens.get(0).getToken().equals("return")){
            switch (tokens.get(0).getToken()){
                case "let": compileLetStatement(tokens, compiledXML);
                    break;
                case "if": compileIf(tokens, compiledXML);
                    break;
                case "while": compileWhile(tokens, compiledXML);
                    break;
                case "do": compileDoStatement(tokens, compiledXML);
                    break;
                case "return":compileReturnStatement(tokens, compiledXML);
                    break;
            }
        }
        compiledXML.add("</statements>");
    }

    public static void compileLetStatement(List<Token> tokens, List<String> compiledXML){
        compiledXML.add("<letStatement>");
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//let
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//varName
        if(!tokens.get(0).getToken().equals("=")){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//[
            compileExpression(tokens, compiledXML);
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//]
        }
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//=
        compileExpression(tokens, compiledXML);
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//;
        compiledXML.add("</letStatement>");
        return;
    }

    public static void compileDoStatement(List<Token> tokens, List<String> compiledXML){
        compiledXML.add("<doStatement>");
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//do

        if(tokens.get(1).getToken().equals(".")){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//className | varName
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//.
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//subroutineName
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//(
            compileExpressionList(tokens, compiledXML);
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//)
        }else{
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//subroutineName
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//(
            compileExpressionList(tokens, compiledXML);
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//)
        }

        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//;
        compiledXML.add("</doStatement>");
        return;
    }

    public static void compileWhile(List<Token> tokens, List<String> compiledXML){
        compiledXML.add("<whileStatement>");
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//while
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//(
        compileExpression(tokens, compiledXML);
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//)
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//{
        compileStatements(tokens, compiledXML);
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//}
        compiledXML.add("</whileStatement>");
    }

    public static void compileIf(List<Token> tokens, List<String> compiledXML){
        compiledXML.add("<ifStatement>");
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//if
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//(
        compileExpression(tokens, compiledXML);
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//)
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//{
        compileStatements(tokens, compiledXML);
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//}
        if (tokens.get(0).getToken().equals("else")){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//else
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//{
            compileStatements(tokens, compiledXML);
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//}
        }
        compiledXML.add("</ifStatement>");
    }

    public static void compileReturnStatement(List<Token> tokens, List<String> compiledXML){
        compiledXML.add("<returnStatement>");
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//return
        if(!tokens.get(0).getToken().equals(";"))
            compileExpression(tokens, compiledXML);
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//;
        compiledXML.add("</returnStatement>");
    }

    public static void compileExpression(List<Token> tokens, List<String> compiledXML){
        compiledXML.add("<expression>");
        compileTerm(tokens, compiledXML);
        while(tokens.get(0).isBinaryOperator()){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//op
            compileTerm(tokens, compiledXML);
        }
        compiledXML.add("</expression>");
    }

    public static void compileTerm(List<Token> tokens, List<String> compiledXML){
        compiledXML.add("<term>");
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//term
        compiledXML.add("</term>");
    }



    public static void compileExpressionList(List<Token> tokens, List<String> compiledXML){//TODO
        compiledXML.add("<expressionList>");
        if(!tokens.get(0).getToken().equals(")")){
            compileExpression(tokens, compiledXML);
            while(tokens.get(0).getToken().equals(",")){
                compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//,
                compileExpression(tokens, compiledXML);
            }
        }
        compiledXML.add("</expressionList>");
    }

}
