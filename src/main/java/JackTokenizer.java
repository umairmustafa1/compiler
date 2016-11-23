import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by MuhammadUmair on 11/21/2016.
 */
public class JackTokenizer {

    /*public static HashMap<String,Element> lexicalElements = new HashMap<>();

    static {
        lexicalElements.put("class",Element.KEYWORD);lexicalElements.put("constructor",Element.KEYWORD);lexicalElements.put("function",Element.KEYWORD);
        lexicalElements.put("method",Element.KEYWORD);lexicalElements.put("field",Element.KEYWORD);lexicalElements.put("static",Element.KEYWORD);
        lexicalElements.put("var",Element.KEYWORD);lexicalElements.put("int",Element.KEYWORD);lexicalElements.put("char",Element.KEYWORD);
        lexicalElements.put("boolean",Element.KEYWORD);lexicalElements.put("void",Element.KEYWORD);lexicalElements.put("true",Element.KEYWORD);
        lexicalElements.put("false",Element.KEYWORD);lexicalElements.put("null",Element.KEYWORD);lexicalElements.put("this",Element.KEYWORD);
        lexicalElements.put("let",Element.KEYWORD);lexicalElements.put("do",Element.KEYWORD);lexicalElements.put("if",Element.KEYWORD);
        lexicalElements.put("else",Element.KEYWORD);lexicalElements.put("while",Element.KEYWORD);lexicalElements.put("return",Element.KEYWORD);
        lexicalElements.put("{",Element.SYMBOL);lexicalElements.put("}",Element.SYMBOL);lexicalElements.put("(",Element.SYMBOL);lexicalElements.put(")",Element.SYMBOL);
        lexicalElements.put("[",Element.SYMBOL);lexicalElements.put("]",Element.SYMBOL);lexicalElements.put(".",Element.SYMBOL);lexicalElements.put(",",Element.SYMBOL);
        lexicalElements.put(";",Element.SYMBOL);lexicalElements.put("+",Element.SYMBOL);lexicalElements.put("-",Element.SYMBOL);lexicalElements.put("*",Element.SYMBOL);
        lexicalElements.put("/",Element.SYMBOL);lexicalElements.put("&",Element.SYMBOL);lexicalElements.put("|",Element.SYMBOL);lexicalElements.put("<",Element.SYMBOL);
        lexicalElements.put(">",Element.SYMBOL);lexicalElements.put("=",Element.SYMBOL);lexicalElements.put("~",Element.SYMBOL);
    }*/

    public static List<Token> tokenize(Path inputFilePath){

        List<Token> tokens = new ArrayList<>();

        try(Stream<String> lines = Files.lines(inputFilePath)){
            String keywordPattern = "class|constructor|function|method|field|static|var|int|char|boolean|void|true|false|null|this|let|do|if|else|while|return";
            String symbolPattern = "[\\{\\}\\(\\)\\[\\]\\.\\,\\;\\+\\-\\*\\/\\&\\|\\<\\>\\=\\~]";
            String integerPattern = "[0-9]+";
            String stringPattern = "\"[^\"\n]*\"";
            String identifierPattern = "[\\w_]+";

            Pattern tokenPattern = Pattern.compile(keywordPattern + "|" + symbolPattern + "|" + integerPattern + "|" + stringPattern + "|" + identifierPattern);

            for (String line : removeComments(lines).collect(Collectors.toList())) {
                Matcher matcher = tokenPattern.matcher(line);

                while(matcher.find()){
                    String token = matcher.group();
                    Element element;

                    if(token.matches(keywordPattern))
                        element = Element.KEYWORD;
                    else if(token.matches(symbolPattern))
                        element = Element.SYMBOL;
                    else if(token.matches(integerPattern))
                        element = Element.INTEGER_CONSTANT;
                    else if(token.matches(stringPattern)){
                        element = Element.STRING_CONSTANT;
                        token = token.substring(1, token.length() - 1);
                    }
                    else
                        element = Element.IDENTIFIER;

                    tokens.add(new Token(token, element));
                }
            }
        }catch (IOException ex) {
            System.out.println("No such file exists : " + inputFilePath);
        }
        return tokens;
    }

    /**
     * Method to remove comments from source file
     * @param input stream which contains all the lines of the file
     * @return stream with removed comments
     */
    public static Stream<String> removeComments(Stream<String> input){
        return input.filter(a -> !a.trim().startsWith("//"))
                .filter(a -> !a.trim().startsWith("/**"))
                .filter(a -> !a.trim().startsWith("*"))
                .filter(a -> !a.trim().startsWith("*/"))
                .filter(a -> !a.trim().isEmpty())
                .map(a -> a.substring(0, a.contains("//") ? a.indexOf("//") : a.length()).trim());
    }


}
