import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class JackCompiler {

    public static void main(String[] args) {

        if(args.length != 1){
            System.out.println("Usage : java JackCompiler \"[Dir Path]\"");
        }
        else{
            Path inputDirPath = Paths.get(args[0]);

            try{

                List<Path> inputFilePaths = Files.walk(inputDirPath)
                    .filter(path -> path.getFileName().toString().endsWith(".jack"))
                    .collect(Collectors.toList());

                for (Path inputFilePath : inputFilePaths) {
                    Path outputFilePath = inputFilePath.resolveSibling(inputFilePath.getFileName().toString()
                            .substring(0,inputFilePath.getFileName().toString().lastIndexOf(".")) + ".vm");

                    List<Token> tokens = JackTokenizer.tokenize(inputFilePath);//Produces tokenize List
                    Files.write(outputFilePath, CompilationEngine.compileClass(tokens), Charset.defaultCharset());//Outputs Compiled VM File
                }
            }catch(IOException ex){
                System.out.println("No such directory exists : " + inputDirPath);
            }
        }
    }


}