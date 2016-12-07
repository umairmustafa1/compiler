import java.util.Hashtable;

/**
 * Symbol table to store and manipulate identifiers
 */
public class SymbolTable {
    private final Hashtable<String, Identifier> classScope;
    private final Hashtable<String, Identifier> subroutineScope;
    private int staticCounter;
    private int fieldCounter;
    private int argumentCounter;
    private int localCounter;

    public SymbolTable() {
        classScope = new Hashtable<>();
        subroutineScope = new Hashtable<>();
        staticCounter = 0;
        fieldCounter = 0;
        argumentCounter = 0;
        localCounter = 0;
    }

    public void startSubroutine(){
        subroutineScope.clear();
        argumentCounter = 0;
        localCounter = 0;
    }

    public void define(String name, String type, IdentifierKind kind){
        switch (kind){
            case STATIC :
                classScope.put(name, new Identifier(type, kind, staticCounter++));
                break;
            case FIELD:
                classScope.put(name, new Identifier(type, kind, fieldCounter++));
                break;
            case ARG:
                subroutineScope.put(name, new Identifier(type, kind, argumentCounter++));
                break;
            case VAR:
                subroutineScope.put(name, new Identifier(type, kind, localCounter++));
                break;
        }
    }

    public int varCount(IdentifierKind kind){
        switch (kind){
            case STATIC :
                return staticCounter;
            case FIELD:
                return fieldCounter;
            case ARG:
                return argumentCounter;
            case VAR:
                return localCounter;
            default:
                return 0;
        }
    }

    public IdentifierKind kindOf(String name){
        if(subroutineScope.containsKey(name))
            return subroutineScope.get(name).getKind();
        else if(classScope.containsKey(name))
            return classScope.get(name).getKind();
        else
            return IdentifierKind.NONE;
    }

    public String typeOf(String name){
        if(subroutineScope.containsKey(name))
            return subroutineScope.get(name).getType();
        else if(classScope.containsKey(name))
            return classScope.get(name).getType();
        else
            return "";
    }

    public int indexOf(String name){
        if(subroutineScope.containsKey(name))
            return subroutineScope.get(name).getIndex();
        else if(classScope.containsKey(name))
            return classScope.get(name).getIndex();
        else
            return -1;
    }

}
