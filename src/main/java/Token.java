/**
 * Created by MuhammadUmair on 11/23/2016.
 */
public class Token {
    private String tokenStr;
    private Element element;
    private boolean isBinaryOperator;
    private boolean isUnaryOperator;

    public Token(String tokenStr, Element element, boolean isBinaryOperator, boolean isUnaryOperator) {
        this.tokenStr = tokenStr;
        this.element = element;
        this.isBinaryOperator = isBinaryOperator;
        this.isUnaryOperator = isUnaryOperator;
    }

    public String getToken() {
        return tokenStr;
    }

    public void setToken(String tokenStr) {
        this.tokenStr = tokenStr;
    }

    public Element getTokenType() {
        return element;
    }

    public void setTokenType(Element element) {
        this.element = element;
    }

    public boolean isBinaryOperator() {
        return isBinaryOperator;
    }

    public void setBinaryOperator(boolean binaryOperator) {
        isBinaryOperator = binaryOperator;
    }

    public boolean isUnaryOperator() {
        return isUnaryOperator;
    }

    public void setUnaryOperator(boolean unaryOperator) {
        isUnaryOperator = unaryOperator;
    }
}
