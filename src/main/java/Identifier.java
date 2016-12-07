/**
 * Identifier class to encapsulate its properties
 */
public class Identifier {
    private final String type;
    private final IdentifierKind kind;
    private final int index;

    public Identifier(String type, IdentifierKind kind, int index) {
        this.type = type;
        this.kind = kind;
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public IdentifierKind getKind() {
        return kind;
    }

    public int getIndex() {
        return index;
    }

}
