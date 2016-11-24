import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

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
                    Path outputFilePathTokens = inputFilePath.resolveSibling(inputFilePath.getFileName().toString()
                            .substring(0,inputFilePath.getFileName().toString().lastIndexOf(".")) + "T.xml");

                    Path outputFilePath = inputFilePath.resolveSibling(inputFilePath.getFileName().toString()
                            .substring(0,inputFilePath.getFileName().toString().lastIndexOf(".")) + ".xml");

                    List<Token> tokens = JackTokenizer.tokenize(inputFilePath);
                    Files.write(outputFilePathTokens, JackTokenizer.getXMLTokens(tokens), Charset.defaultCharset());//Output Tokens
                    Files.write(outputFilePath, CompilationEngine.compileClass(tokens), Charset.defaultCharset());//Output Compiled XML

                }
            }catch(IOException ex){
                System.out.println("No such directory exists : " + inputDirPath);
            }
        }
    }


}