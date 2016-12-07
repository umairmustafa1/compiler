/**
 * Enum for arithmetic commands
 */
public enum Command {
    ADD("add"), SUB("sub"), NEG("neg"), EQ("eq"), GT("gt"), LT("lt"), AND("and"), OR("or"), NOT("not");

    private final String description;

    Command(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
