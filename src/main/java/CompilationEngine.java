import java.util.ArrayList;
import java.util.List;

/**
 * Utility Class which provides static methods for compilation.
 */
public class CompilationEngine {

    private static List<String> compiledVM;
    private static SymbolTable symbolTable;
    private static VMWriter vmWriter;
    private static String className;
    private static List<Token> tokens;
    private static int counter;

    /**
     * Method for compilation of a class
     * @param incomingTokens List of tokens which needs to be compiled
     * @return List of vm commands
     */
    public static List<String> compileClass(List<Token> incomingTokens){
        compiledVM = new ArrayList<>();
        symbolTable = new SymbolTable();
        vmWriter = new VMWriter();
        tokens = incomingTokens;
        counter = 0;

        tokens.remove(0);//class
        className = tokens.remove(0).getToken();//className
        tokens.remove(0);//{

        while(tokens.get(0).getToken().equals("static") || tokens.get(0).getToken().equals("field"))
            compileClassVarDec();

        while(tokens.get(0).getToken().equals("constructor") || tokens.get(0).getToken().equals("function") || tokens.get(0).getToken().equals("method"))
            compileSubroutine();

        tokens.remove(0);//}
        return compiledVM;
    }

    private static void compileClassVarDec(){
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

    private static void compileSubroutine(){
        String functionType = tokens.remove(0).getToken();//constructor | function | method
        tokens.remove(0);//void | type
        String functionName = tokens.remove(0).getToken();//subRoutineName
        symbolTable.startSubroutine();

        if(functionType.equals("method"))
            symbolTable.define("this", className, IdentifierKind.ARG);

        tokens.remove(0);//(
        compileParameterList();
        tokens.remove(0);//)

        tokens.remove(0);//{
        //varDec*
        while (tokens.get(0).getToken().equals("var")){
            compileVarDec();
        }

        compiledVM.add(VMWriter.writeFunction(className + "." + functionName, symbolTable.varCount(IdentifierKind.VAR)));

        switch (functionType){
            case "method" :
                compiledVM.add(VMWriter.writePush(Segment.ARG, 0));
                compiledVM.add(VMWriter.writePop(Segment.POINTER, 0));
                break;
            case "constructor" :
                compiledVM.add(VMWriter.writePush(Segment.CONST, symbolTable.varCount(IdentifierKind.FIELD)));
                compiledVM.add(VMWriter.writeCall("Memory.alloc", 1));
                compiledVM.add(VMWriter.writePop(Segment.POINTER, 0));
                break;
        }

        //statements
        compileStatements();
        tokens.remove(0);//}
    }

    private static void compileParameterList(){
        if(!tokens.get(0).getToken().equals(")")){
            String varType = tokens.remove(0).getToken();//type
            String varName = tokens.remove(0).getToken();//varName
            symbolTable.define(varName, varType, IdentifierKind.ARG);
            while (tokens.get(0).getToken().equals(",")){
                tokens.remove(0);//,
                varType = tokens.remove(0).getToken();//type
                varName = tokens.remove(0).getToken();//varName
                symbolTable.define(varName, varType, IdentifierKind.ARG);
            }
        }
    }

    private static void compileVarDec(){
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

    private static void compileStatements(){
        while(tokens.get(0).getToken().equals("let") || tokens.get(0).getToken().equals("if") ||
                tokens.get(0).getToken().equals("while") || tokens.get(0).getToken().equals("do")
                || tokens.get(0).getToken().equals("return")){
            switch (tokens.get(0).getToken()){
                case "let": compileLetStatement();
                    break;
                case "if": compileIf();
                    break;
                case "while": compileWhile();
                    break;
                case "do": compileDoStatement();
                    break;
                case "return":compileReturnStatement();
                    break;
            }
        }
    }

    private static void compileLetStatement(){
        tokens.remove(0);//let
        String varName = tokens.remove(0).getToken();//varName
        boolean isArray = false;
        if(!tokens.get(0).getToken().equals("=")){
            isArray = true;
            tokens.remove(0);//[
            compiledVM.add(VMWriter.writePush(getSegment(symbolTable.kindOf(varName)), symbolTable.indexOf(varName)));
            compileExpression();
            tokens.remove(0);//]
            compiledVM.add(VMWriter.writeArithmetic(Command.ADD));
        }
        tokens.remove(0);//=
        compileExpression();
        tokens.remove(0);//;

        if (isArray){
            compiledVM.add(VMWriter.writePop(Segment.TEMP,0));
            compiledVM.add(VMWriter.writePop(Segment.POINTER,1));
            compiledVM.add(VMWriter.writePush(Segment.TEMP,0));
            compiledVM.add(VMWriter.writePop(Segment.THAT,0));
        }else {
            compiledVM.add(VMWriter.writePop(getSegment(symbolTable.kindOf(varName)), symbolTable.indexOf(varName)));
        }
    }

    private static void compileDoStatement(){
        tokens.remove(0);//do
        compileSubroutineCall();
        tokens.remove(0);//;
        compiledVM.add(VMWriter.writePop(Segment.TEMP,0));
    }

    private static void compileWhile(){
        String continueLabel =  "whileEndLabel" + counter++;
        String topLabel = "whileStartLabel" + counter++;

        compiledVM.add(VMWriter.writeLabel(topLabel));
        tokens.remove(0);//while
        tokens.remove(0);//(
        compileExpression();
        tokens.remove(0);//)
        compiledVM.add(VMWriter.writeArithmetic(Command.NOT));
        compiledVM.add(VMWriter.writeIf(continueLabel));
        tokens.remove(0);//{
        compileStatements();
        tokens.remove(0);//}
        compiledVM.add(VMWriter.writeGoto(topLabel));
        compiledVM.add(VMWriter.writeLabel(continueLabel));
    }

    private static void compileIf(){
        String elseLabel = "elseLabel" + counter++;
        String endLabel = "ifEndLabel" + counter++;

        tokens.remove(0);//if
        tokens.remove(0);//(
        compileExpression();
        compiledVM.add(VMWriter.writeArithmetic(Command.NOT));
        compiledVM.add(VMWriter.writeIf(elseLabel));
        tokens.remove(0);//)
        tokens.remove(0);//{
        compileStatements();
        tokens.remove(0);//}
        compiledVM.add(VMWriter.writeGoto(endLabel));
        compiledVM.add(VMWriter.writeLabel(elseLabel));
        if (tokens.get(0).getToken().equals("else")){
            tokens.remove(0);//else
            tokens.remove(0);//{
            compileStatements();
            tokens.remove(0);//}
        }
        compiledVM.add(VMWriter.writeLabel(endLabel));
    }

    private static void compileReturnStatement(){
        tokens.remove(0);//return
        if(tokens.get(0).getToken().equals(";"))
            compiledVM.add(VMWriter.writePush(Segment.CONST, 0));
        else
            compileExpression();
        tokens.remove(0);//;
        compiledVM.add(VMWriter.writeReturn());
    }

    private static void compileExpression(){
        compileTerm();
        while(tokens.get(0).isBinaryOperator()){
            String op = tokens.remove(0).getToken();//op
            String opCmd = "";
            switch (op){
                case "+":opCmd = VMWriter.writeArithmetic(Command.ADD);break;
                case "-":opCmd = VMWriter.writeArithmetic(Command.SUB);break;
                case "*":opCmd = "call Math.multiply 2";break;
                case "/":opCmd = "call Math.divide 2";break;
                case "<":opCmd = VMWriter.writeArithmetic(Command.LT);break;
                case ">":opCmd = VMWriter.writeArithmetic(Command.GT);break;
                case "=":opCmd = VMWriter.writeArithmetic(Command.EQ);break;
                case "&":opCmd = VMWriter.writeArithmetic(Command.AND);break;
                case "|":opCmd = VMWriter.writeArithmetic(Command.OR);break;
            }
            compileTerm();
            compiledVM.add(opCmd);
        }
    }

    private static void compileTerm(){
        if(tokens.get(0).getTokenType() == Element.IDENTIFIER){
            if(tokens.get(1).getToken().equals("[")){
                String identifier = tokens.remove(0).getToken();
                compiledVM.add(VMWriter.writePush(getSegment(symbolTable.kindOf(identifier)),
                        symbolTable.indexOf(identifier)));
                tokens.remove(0);//[
                compileExpression();
                tokens.remove(0);//]
                compiledVM.add(VMWriter.writeArithmetic(Command.ADD));
                compiledVM.add(VMWriter.writePop(Segment.POINTER, 1));
                compiledVM.add(VMWriter.writePush(Segment.THAT, 0));
            }else if(tokens.get(1).getToken().equals("(") || tokens.get(1).getToken().equals(".")){
                compileSubroutineCall();
            }
            else{
                String varName = tokens.remove(0).getToken();//varName
                compiledVM.add(VMWriter.writePush(getSegment(symbolTable.kindOf(varName)), symbolTable.indexOf(varName)));
            }
        }else {
            if (tokens.get(0).isKeyWordConstant() || tokens.get(0).getTokenType() == Element.STRING_CONSTANT ||
                    tokens.get(0).getTokenType() == Element.INTEGER_CONSTANT) {
                Token token = tokens.remove(0);//integerConstant | stringConstant | keyWordConstant

                if (token.isKeyWordConstant()) {
                    if (token.getToken().equals("true")) {
                        compiledVM.add(VMWriter.writePush(Segment.CONST, 0));
                        compiledVM.add(VMWriter.writeArithmetic(Command.NOT));
                    } else if (token.getToken().equals("false") || token.getToken().equals("null")) {
                        compiledVM.add(VMWriter.writePush(Segment.CONST, 0));
                    } else if (token.getToken().equals("this"))
                        compiledVM.add(VMWriter.writePush(Segment.POINTER, 0));
                } else if (token.getTokenType().equals(Element.STRING_CONSTANT)) {
                    String str = token.getToken();
                    compiledVM.add(VMWriter.writePush(Segment.CONST, str.length()));
                    compiledVM.add(VMWriter.writeCall("String.new", 1));
                    for (int i = 0; i < str.length(); i++) {
                        compiledVM.add(VMWriter.writePush(Segment.CONST, (int) str.charAt(i)));
                        compiledVM.add(VMWriter.writeCall("String.appendChar", 2));
                    }
                } else {
                    compiledVM.add(VMWriter.writePush(Segment.CONST, Integer.parseInt(token.getToken())));
                }

            } else if (tokens.get(0).getToken().equals("(")) {
                tokens.remove(0);//(
                compileExpression();
                tokens.remove(0);//)
            } else if (tokens.get(0).isUnaryOperator()) {
                String symbol = tokens.remove(0).getToken();//unaryOp
                compileTerm();

                if (symbol.equals("-"))
                    compiledVM.add(VMWriter.writeArithmetic(Command.NEG));
                else
                    compiledVM.add(VMWriter.writeArithmetic(Command.NOT));
            }
        }
    }

    private static int compileExpressionList(){
        int nArgs = 0;
        if(!tokens.get(0).getToken().equals(")")){
            compileExpression();
            nArgs++;
            while(tokens.get(0).getToken().equals(",")){
                tokens.remove(0);//,
                compileExpression();
                nArgs++;
            }
        }
        return nArgs;
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

    private static void compileSubroutineCall(){
        if (tokens.get(1).getToken().equals("(")){//TODO
            String subroutineName = tokens.remove(0).getToken();//subroutineName
            tokens.remove(0);//(
            compiledVM.add(VMWriter.writePush(Segment.POINTER, 0));
            int nArgs = compileExpressionList() + 1;
            tokens.remove(0);//)
            compiledVM.add(VMWriter.writeCall(className + '.' + subroutineName, nArgs));
        }else if (tokens.get(1).getToken().equals(".")) {//TODO
            String varName = tokens.remove(0).getToken();//className | varName
            tokens.remove(0);//.
            String subroutineName = tokens.remove(0).getToken();//subroutineName

            String type = symbolTable.typeOf(varName);
            int nArgs = 0;

            String methodName = varName + "." + subroutineName;

            if (!type.equals("")){
                nArgs = 1;
                compiledVM.add(VMWriter.writePush(getSegment(symbolTable.kindOf(varName)), symbolTable.indexOf(varName)));
                methodName = type + "." + subroutineName;
            }

            tokens.remove(0);//(
            nArgs += compileExpressionList();
            compiledVM.add(VMWriter.writeCall(methodName, nArgs));
            tokens.remove(0);//)
        }
    }

}
