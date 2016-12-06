/**
 * Class with static methods to return VM commands
 */
public class VMWriter {

    public static String writePush(Segment segment, int index){
        return "push " + segment.getDescription() + " " + index;
    }

    public static String writePop(Segment segment, int index){
        return "pop " + segment.getDescription() + " " + index;
    }

    public static String writeArithmetic(Command command){
        return command.getDescription();
    }

    public static String writeLabel(String label){
         return "label " + label;
    }

    public static String writeGoto(String label){
        return "goto " + label;
    }

    public static String writeIf(String label){
        return "if-goto " + label;
    }

    public static String writeCall(String name, int nArgs){
        return "call " + name + " " + nArgs;
    }

    public static String writeFunction(String name, int nLocals){
        return "function " + name + " " + nLocals;
    }

    public static String writeReturn(){
        return "return";
    }

}
