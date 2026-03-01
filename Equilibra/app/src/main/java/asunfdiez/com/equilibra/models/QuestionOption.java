package asunfdiez.com.equilibra.models;

public class QuestionOption {
    private final String id;
    private final String text;

    public QuestionOption(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
