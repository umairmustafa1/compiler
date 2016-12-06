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
        List<String> compiledVM = new ArrayList<>();
        SymbolTable symbolTable = new SymbolTable();
        VMWriter vmWriter = new VMWriter();

        tokens.remove(0);//class
        String className = tokens.remove(0).getToken();//className
        tokens.remove(0);//{

        while(tokens.get(0).getToken().equals("static") || tokens.get(0).getToken().equals("field"))
            compileClassVarDec(tokens, symbolTable);

        while(tokens.get(0).getToken().equals("constructor") || tokens.get(0).getToken().equals("function") || tokens.get(0).getToken().equals("method"))
            compileSubroutine(tokens, compiledVM, vmWriter, symbolTable, className);

        compiledVM.add(JackTokenizer.getXMLToken(tokens.remove(0)));//}
        return compiledVM;
    }

    private static void compileClassVarDec(List<Token> tokens, SymbolTable symbolTable){
        String kind = tokens.remove(0).getToken();//static | field
        String type = tokens.remove(0).getToken();//type
        String varName = tokens.remove(0).getToken();//varName

        IdentifierKind identifierKind = IdentifierKind.NONE;

        switch (kind) {
            case "static": identifierKind = IdentifierKind.STATIC;
                break;
            case "field": identifierKind = IdentifierKind.FIELD;
                break;
        }

        symbolTable.define(varName, type, identifierKind);

        while (!tokens.get(0).getToken().equals(";")){
            tokens.remove(0);//,
            symbolTable.define(tokens.remove(0).getToken(), type, identifierKind);//varName
        }
        tokens.remove(0);//;
    }

    private static void compileSubroutine(List<Token> tokens, List<String> compiledVM, VMWriter vmWriter,
                                          SymbolTable symbolTable, String className){
        String functionType = tokens.remove(0).getToken();//constructor | function | method
        String returnType = tokens.remove(0).getToken();//void | type
        String functionName = tokens.remove(0).getToken();//subRoutineName
        tokens.remove(0);//(
        if(functionType.equals("method"))
            symbolTable.define("this", className, IdentifierKind.ARG);

        switch (functionType){
            case "method" :
                compiledVM.add(vmWriter.writeFunction(className + "." + functionName, compileParameterList(tokens, symbolTable) + 1));
                compiledVM.add(vmWriter.writePush(Segment.ARG, 0));
                compiledVM.add(vmWriter.writePop(Segment.POINTER, 0));
                break;
            case "constructor" :
                compiledVM.add(vmWriter.writeFunction(className + "." + functionName, compileParameterList(tokens, symbolTable)));
                compiledVM.add(vmWriter.writePush(Segment.CONST, symbolTable.varCount(IdentifierKind.FIELD)));
                compiledVM.add(vmWriter.writeCall("Memory.alloc", 1));
                compiledVM.add(vmWriter.writePop(Segment.POINTER, 0));
                break;
            case "function":
                compiledVM.add(vmWriter.writeFunction(className + "." + functionName, compileParameterList(tokens, symbolTable)));
                break;
        }

        tokens.remove(0);//)

        tokens.remove(0);//{
        //varDec*
        while (tokens.get(0).getToken().equals("var")){
            compileVarDec(tokens, symbolTable);
        }
        //statements
        compileStatements(tokens, compiledVM, vmWriter, symbolTable);

        compiledVM.add(JackTokenizer.getXMLToken(tokens.remove(0)));//}
    }

    private static int compileParameterList(List<Token> tokens, SymbolTable symbolTable){
        int numOfParameters = 0;
        if(!tokens.get(0).getToken().equals(")")){
            String varType = tokens.remove(0).getToken();//type
            String varName = tokens.remove(0).getToken();//varName
            numOfParameters++;
            symbolTable.define(varName, varType, IdentifierKind.ARG);
            while (tokens.get(0).getToken().equals(",")){
                tokens.remove(0);//,
                varType = tokens.remove(0).getToken();//type
                varName = tokens.remove(0).getToken();//varName
                numOfParameters++;
                symbolTable.define(varName, varType, IdentifierKind.ARG);
            }
        }
        return numOfParameters;
    }

    private static void compileVarDec(List<Token> tokens, SymbolTable symbolTable){
        tokens.remove(0);//var
        String type = tokens.remove(0).getToken();//type
        String varName = tokens.remove(0).getToken();//varName
        symbolTable.define(varName, type, IdentifierKind.VAR);
        while (tokens.get(0).getToken().equals(",")){
            tokens.remove(0);//,
            varName = tokens.remove(0).getToken();//varName
            symbolTable.define(varName, type, IdentifierKind.VAR);
        }
        tokens.remove(0);//;
    }

    private static void compileStatements(List<Token> tokens, List<String> compiledVM, VMWriter vmWriter,
                                          SymbolTable symbolTable){
        while(tokens.get(0).getToken().equals("let") || tokens.get(0).getToken().equals("if") || tokens.get(0).getToken().equals("while") || tokens.get(0).getToken().equals("do")
                || tokens.get(0).getToken().equals("return")){
            switch (tokens.get(0).getToken()){
                case "let": compileLetStatement(tokens, compiledVM, vmWriter, symbolTable);
                    break;
                case "if": compileIf(tokens, compiledVM, vmWriter, symbolTable);
                    break;
                case "while": compileWhile(tokens, compiledVM, vmWriter, symbolTable);
                    break;
                case "do": compileDoStatement(tokens, compiledVM, vmWriter, symbolTable);
                    break;
                case "return":compileReturnStatement(tokens, compiledVM, vmWriter, symbolTable);
                    break;
            }
        }
    }

    private static void compileLetStatement(List<Token> tokens, List<String> compiledVM, VMWriter vmWriter,
                                            SymbolTable symbolTable){
        tokens.remove(0);//let
        String varName = tokens.remove(0).getToken();//varName
        boolean isArray = false;
        if(!tokens.get(0).getToken().equals("=")){
            isArray = true;
            tokens.remove(0);//[
            compiledVM.add(vmWriter.writePush(getSegment(symbolTable.kindOf(varName)), symbolTable.indexOf(varName)));
            compileExpression(tokens, compiledVM, vmWriter, symbolTable);
            tokens.remove(0);//]
            compiledVM.add(vmWriter.writeArithmetic(Command.ADD));
        }
        tokens.remove(0);//=
        compileExpression(tokens, compiledVM, vmWriter, symbolTable);
        tokens.remove(0);//;

        if (isArray){
            compiledVM.add(vmWriter.writePop(Segment.TEMP,0));
            compiledVM.add(vmWriter.writePop(Segment.POINTER,1));
            compiledVM.add(vmWriter.writePush(Segment.TEMP,0));
            compiledVM.add(vmWriter.writePop(Segment.THAT,0));
        }else {
            compiledVM.add(vmWriter.writePop(getSegment(symbolTable.kindOf(varName)), symbolTable.indexOf(varName)));
        }
    }

    private static void compileDoStatement(List<Token> tokens, List<String> compiledXML, VMWriter vmWriter,
                                           SymbolTable symbolTable){
        tokens.remove(0);//do

        if(tokens.get(1).getToken().equals(".")){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//className | varName
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//.
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//subroutineName
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//(
            compileExpressionList(tokens, compiledXML, vmWriter, symbolTable);
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//)
        }else{
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//subroutineName
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//(
            compileExpressionList(tokens, compiledXML, vmWriter, symbolTable);
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//)
        }

        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//;
    }

    private static void compileWhile(List<Token> tokens, List<String> compiledXML, VMWriter vmWriter,
                                     SymbolTable symbolTable){
        compiledXML.add("<whileStatement>");
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//while
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//(
        compileExpression(tokens, compiledXML, vmWriter, symbolTable);
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//)
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//{
        compileStatements(tokens, compiledXML, vmWriter, symbolTable);
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//}
        compiledXML.add("</whileStatement>");
    }

    private static void compileIf(List<Token> tokens, List<String> compiledXML, VMWriter vmWriter,
                                  SymbolTable symbolTable){
        compiledXML.add("<ifStatement>");
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//if
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//(
        compileExpression(tokens, compiledXML, vmWriter, symbolTable);
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//)
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//{
        compileStatements(tokens, compiledXML, vmWriter, symbolTable);
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//}
        if (tokens.get(0).getToken().equals("else")){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//else
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//{
            compileStatements(tokens, compiledXML, vmWriter, symbolTable);
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//}
        }
        compiledXML.add("</ifStatement>");
    }

    private static void compileReturnStatement(List<Token> tokens, List<String> compiledXML, VMWriter vmWriter,
                                               SymbolTable symbolTable){
        compiledXML.add("<returnStatement>");
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//return
        if(!tokens.get(0).getToken().equals(";"))
            compileExpression(tokens, compiledXML, vmWriter, symbolTable);
        compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//;
        compiledXML.add("</returnStatement>");
    }

    private static void compileExpression(List<Token> tokens, List<String> compiledXML, VMWriter vmWriter,
                                          SymbolTable symbolTable){
        compiledXML.add("<expression>");
        compileTerm(tokens, compiledXML, vmWriter, symbolTable);
        while(tokens.get(0).isBinaryOperator()){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//op
            compileTerm(tokens, compiledXML, vmWriter, symbolTable);
        }
        compiledXML.add("</expression>");
    }

    private static void compileTerm(List<Token> tokens, List<String> compiledXML, VMWriter vmWriter,
                                    SymbolTable symbolTable){
        compiledXML.add("<term>");

        if(tokens.get(0).isKeyWordConstant() || tokens.get(0).getTokenType().equals(Element.STRING_CONSTANT) ||
                tokens.get(0).getTokenType().equals(Element.INTEGER_CONSTANT)){
            tokens.remove(0);//integerConstant | stringConstant | keyWordConstant
        }else if(tokens.get(0).isUnaryOperator()){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//unaryOp
            compileTerm(tokens, compiledXML, vmWriter, symbolTable);
        }else if(tokens.get(0).getToken().equals("(")){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//(
            compileExpression(tokens, compiledXML, vmWriter, symbolTable);
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//)
        }else if(tokens.get(1).getToken().equals("[")){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//varName
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//[
            compileExpression(tokens, compiledXML, vmWriter, symbolTable);
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//]
        }else if(tokens.get(1).getToken().equals("(")){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//subroutineName
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//(
            compileExpressionList(tokens, compiledXML, vmWriter, symbolTable);
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//)
        }else if(tokens.get(1).getToken().equals(".")){
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//className | varName
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//.
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//subroutineName
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//(
            compileExpressionList(tokens, compiledXML, vmWriter, symbolTable);
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//)
        }else{
            compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//varName
        }

        compiledXML.add("</term>");
    }

    private static void compileExpressionList(List<Token> tokens, List<String> compiledXML, VMWriter vmWriter,
                                              SymbolTable symbolTable){
        compiledXML.add("<expressionList>");
        if(!tokens.get(0).getToken().equals(")")){
            compileExpression(tokens, compiledXML, vmWriter, symbolTable);
            while(tokens.get(0).getToken().equals(",")){
                compiledXML.add(JackTokenizer.getXMLToken(tokens.remove(0)));//,
                compileExpression(tokens, compiledXML, vmWriter, symbolTable);
            }
        }
        compiledXML.add("</expressionList>");
    }

    private static Segment getSegment(IdentifierKind kind){
        switch (kind){
            case FIELD:return Segment.THIS;
            case STATIC:return Segment.STATIC;
            case VAR:return Segment.LOCAL;
            case ARG:return Segment.ARG;
            default:return Segment.NONE;
        }
    }

}
