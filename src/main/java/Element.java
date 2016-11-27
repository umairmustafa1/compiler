/**
 * Enum for types of lexical elements
 */
public enum Element {
    KEYWORD("keyword"), SYMBOL("symbol"), INTEGER_CONSTANT("integerConstant"), STRING_CONSTANT("stringConstant"), IDENTIFIER("identifier");

    private final String description;

    Element(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
