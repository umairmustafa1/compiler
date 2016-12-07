import org.junit.Test;

/**
 * Created by MuhammadUmair on 11/21/2016.
 */
public class JackCompilerTest {

    @Test
    public void testSeven(){
        JackCompiler.main(new String[]{"D:\\Dropbox\\UChicago\\52011\\Software\\nand2tetris\\projects\\11\\Seven"});
    }

    @Test
    public void testConvertToBin(){
        JackCompiler.main(new String[]{"D:\\Dropbox\\UChicago\\52011\\Software\\nand2tetris\\projects\\11\\ConvertToBin"});
    }

    @Test
    public void testSquare(){
        JackCompiler.main(new String[]{"D:\\Dropbox\\UChicago\\52011\\Software\\nand2tetris\\projects\\11\\Square"});
    }

    @Test
    public void testAverage(){
        JackCompiler.main(new String[]{"D:\\Dropbox\\UChicago\\52011\\Software\\nand2tetris\\projects\\11\\Average"});
    }

    @Test
    public void testPong(){
        JackCompiler.main(new String[]{"D:\\Dropbox\\UChicago\\52011\\Software\\nand2tetris\\projects\\11\\Pong"});
    }

    @Test
    public void testComplexArrays(){
        JackCompiler.main(new String[]{"D:\\Dropbox\\UChicago\\52011\\Software\\nand2tetris\\projects\\11\\ComplexArrays"});
    }

}