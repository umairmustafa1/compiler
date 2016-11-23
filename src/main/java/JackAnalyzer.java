import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JackAnalyzer {

    public static void main(String[] args) {

        if(args.length != 1){
            System.out.println("Usage : java JackAnalyzer \"[Dir Path]\"");
        }
        else{
            Path inputDirPath = Paths.get(args[0]);

            try{

                List<Path> inputFilePaths = Files.walk(inputDirPath)
                    .filter(path -> path.getFileName().toString().endsWith(".jack"))
                    .collect(Collectors.toList());

                for (Path inputFilePath : inputFilePaths) {

                    List<String> finalOutput = new ArrayList();

                    Path outputFilePath = inputFilePath.resolveSibling(inputFilePath.getFileName().toString()
                            .substring(0,inputFilePath.getFileName().toString().lastIndexOf(".")) + "T.xml");

                    List<Token> tokens = JackTokenizer.tokenize(inputFilePath);

                    finalOutput.add("<tokens>");
                    tokens.forEach(token -> {
                        String tokenType = token.getTokenType().getDescription();
                        String tokenStr = token.getToken();

                        if(tokenStr.equals("<"))
                            tokenStr = "&lt;";
                        else if (tokenStr.equals(">"))
                            tokenStr = "&gt;";
                        else if (tokenStr.equals("\""))
                            tokenStr = "&quot;";
                        else if (tokenStr.equals("&"))
                            tokenStr = "&amp;";

                        finalOutput.add("<" + tokenType + "> " + tokenStr + " </" + tokenType + ">");
                    });
                    finalOutput.add("</tokens>");

                    Files.write(outputFilePath, finalOutput, Charset.defaultCharset());
                }
            }catch(IOException ex){
                System.out.println("No such directory exists : " + inputDirPath);
            }
        }
    }


}