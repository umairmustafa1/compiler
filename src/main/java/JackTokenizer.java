import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility Class which provides static methods for tokenizing.
 */
public class JackTokenizer {

    /**
     * Tokenize the input file into list of Tokens
     * @param inputFilePath Path of input file
     * @return List of Tokens
     */
    public static List<Token> tokenize(Path inputFilePath){

        List<Token> tokens = new ArrayList<>();

        try(Stream<String> lines = Files.lines(inputFilePath)){
            String keywordPattern = "\\bclass\\b|\\bconstructor\\b|\\bfunction\\b|\\bmethod\\b|\\bfield\\b|\\bstatic\\b|\\bvar\\b|" +
                    "\\bint\\b|\\bchar\\b|\\bboolean\\b|\\bvoid\\b|\\btrue\\b|\\bfalse\\b|\\bnull\\b|\\bthis\\b|\\blet\\b|" +
                    "\\bdo\\b|\\bif\\b|\\belse\\b|\\bwhile\\b|\\breturn\\b";
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
                    boolean isBinaryOperator = false;
                    boolean isUnaryOperator = false;
                    boolean isKeywordConstant = false;

                    if(token.matches(keywordPattern)){
                        element = Element.KEYWORD;
                        if(token.matches("true|false|null|this"))
                            isKeywordConstant = true;
                    }

                    else if(token.matches(symbolPattern)){
                        element = Element.SYMBOL;
                        if(token.matches("[\\+\\-\\*\\/\\&\\|\\<\\>\\=]"))
                            isBinaryOperator = true;

                        if (token.matches("[\\-\\~]"))
                            isUnaryOperator = true;
                    }
                    else if(token.matches(integerPattern))
                        element = Element.INTEGER_CONSTANT;
                    else if(token.matches(stringPattern)){
                        element = Element.STRING_CONSTANT;
                        token = token.substring(1, token.length() - 1);
                    }
                    else
                        element = Element.IDENTIFIER;

                    tokens.add(new Token(token, element, isBinaryOperator, isUnaryOperator, isKeywordConstant));
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
    private static Stream<String> removeComments(Stream<String> input){
        return input.filter(a -> !a.trim().startsWith("//"))
                .filter(a -> !a.trim().startsWith("/**"))
                .filter(a -> !a.trim().startsWith("*"))
                .filter(a -> !a.trim().startsWith("*/"))
                .filter(a -> !a.trim().isEmpty())
                .map(a -> a.substring(0, a.contains("//") ? a.indexOf("//") : a.length()).trim());
    }
}
