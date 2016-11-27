/**
 * Class to encapsulate parameters of a token
 */
public class Token {
    private final String tokenStr;
    private final Element element;
    private final boolean isBinaryOperator;
    private final boolean isUnaryOperator;
    private final boolean isKeyWordConstant;

    public Token(String tokenStr, Element element, boolean isBinaryOperator, boolean isUnaryOperator, boolean isKeyWordConstant) {
        this.tokenStr = tokenStr;
        this.element = element;
        this.isBinaryOperator = isBinaryOperator;
        this.isUnaryOperator = isUnaryOperator;
        this.isKeyWordConstant = isKeyWordConstant;
    }

    public String getToken() {
        return tokenStr;
    }

    public Element getTokenType() {
        return element;
    }

    public boolean isBinaryOperator() {
        return isBinaryOperator;
    }

    public boolean isUnaryOperator() {
        return isUnaryOperator;
    }

    public boolean isKeyWordConstant() {
        return isKeyWordConstant;
    }

}
