/**
 * Enum Class for eight virtual memory segments
 */
public enum Segment {
    CONST("constant"), ARG("argument"), LOCAL("local"), STATIC("static"), THIS("this"), THAT("that"), POINTER("pointer"), TEMP("temp"), NONE("none");

    private String description;

    Segment(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
