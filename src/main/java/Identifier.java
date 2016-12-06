/**
 * Identifier class to encapsulate its properties
 */
public class Identifier {
    private String name;
    private String type;
    private IdentifierKind kind;
    private int index;

    public Identifier(String name, String type, IdentifierKind kind, int index) {
        this.name = name;
        this.type = type;
        this.kind = kind;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public IdentifierKind getKind() {
        return kind;
    }

    public void setKind(IdentifierKind kind) {
        this.kind = kind;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
