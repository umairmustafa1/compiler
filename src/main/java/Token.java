/**
 * Created by MuhammadUmair on 11/23/2016.
 */
public class Token {
    private String tokenStr;
    private Element element;

    public Token(String tokenStr, Element element) {
        this.tokenStr = tokenStr;
        this.element = element;
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
}
