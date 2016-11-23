/**
 * Created by MuhammadUmair on 11/23/2016.
 */
public enum Element {
    KEYWORD("keyword"), SYMBOL("symbol"), INTEGER_CONSTANT("integerConstant"), STRING_CONSTANT("stringConstant"), IDENTIFIER("identifier");

    private String description;

    Element(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
