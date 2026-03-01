package asunfdiez.com.equilibra.models;

public class EmotionalState {
    private final String id;
    private final String text;
    private final String emoji;
    private final String validation;

    public EmotionalState(String id, String text, String emoji, String validation) {
        this.id = id;
        this.text = text;
        this.emoji = emoji;
        this.validation = validation;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getValidation() {
        return validation;
    }
}