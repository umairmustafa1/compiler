import java.util.ArrayList;
import java.util.List;

/**
 * Utility Class which provides static methods for compilation.
 */
public class CompilationEngine {

    /**
     * Method for compilation of a class
     * @param tokens List of tokens which needs to be compiled
     * @return List of XML compiled string tokens
     */
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

    private static void compileClassVarDec(List<Token> tokens, List<String> compiledXML){
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
    }

    private static void compileSubroutine(List<Token> tokens, List<String> compiledXML){
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
    }

    private static void compileParameterList(List<Token> tokens, List<String> compiledXML){
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
    }

    private static void compileVarDec(List<Token> tokens, List<String> compiledXML){
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

    private static void compileStatements(List<Token> tokens, List<String> compiledXML){
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

    private static void compileLetStatement(List<Token> tokens, List<String> compiledXML){
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
    }

    private static void compileDoStatement(List<Token> tokens, List<String> compiledXML){
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
    }

    private static void compileWhile(List<Token> tokens, List<String> compiledXML){
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

    private static void compileIf(List<Token> tokens, List<String> compiledXML){
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

    private static void compileReturnStatement(List<Token> tokens, List<String> compiledXML){
        compiledXML.add("<returnStatement>");
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//return
        if(!tokens.get(0).getToken().equals(";"))
            compileExpression(tokens, compiledXML);
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//;
        compiledXML.add("</returnStatement>");
    }

    private static void compileExpression(List<Token> tokens, List<String> compiledXML){
        compiledXML.add("<expression>");
        compileTerm(tokens, compiledXML);
        while(tokens.get(0).isBinaryOperator()){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//op
            compileTerm(tokens, compiledXML);
        }
        compiledXML.add("</expression>");
    }

    private static void compileTerm(List<Token> tokens, List<String> compiledXML){
        compiledXML.add("<term>");

        if(tokens.get(0).isKeyWordConstant() || tokens.get(0).getTokenType().equals(Element.STRING_CONSTANT) ||
                tokens.get(0).getTokenType().equals(Element.INTEGER_CONSTANT)){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//integerConstant | stringConstant | keyWordConstant
        }else if(tokens.get(0).isUnaryOperator()){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//unaryOp
            compileTerm(tokens, compiledXML);
        }else if(tokens.get(0).getToken().equals("(")){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//(
            compileExpression(tokens, compiledXML);
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//)
        }else if(tokens.get(1).getToken().equals("[")){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//varName
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//[
            compileExpression(tokens, compiledXML);
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//]
        }else if(tokens.get(1).getToken().equals("(")){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//subroutineName
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//(
            compileExpressionList(tokens, compiledXML);
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//)
        }else if(tokens.get(1).getToken().equals(".")){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//className | varName
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//.
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//subroutineName
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//(
            compileExpressionList(tokens, compiledXML);
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//)
        }else{
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//varName
        }

        compiledXML.add("</term>");
    }

    private static void compileExpressionList(List<Token> tokens, List<String> compiledXML){
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
