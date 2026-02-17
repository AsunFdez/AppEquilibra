package asunfdiez.com.equilibra.models;

public class OnboardingScreen {
    private int iconRes;
    private String title;
    private String description;
    private int backgroundColor;
    private boolean isEmotionalQuestion;

    public OnboardingScreen(int iconRes, String title, String description, int backgroundColor, boolean isEmotionalQuestion) {
        this.iconRes = iconRes;
        this.title = title;
        this.description = description;
        this.backgroundColor = backgroundColor;
        this.isEmotionalQuestion = isEmotionalQuestion;
    }

    public int getIconRes() { return iconRes; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getBackgroundColor() { return backgroundColor; }
    public boolean isEmotionalQuestion() { return isEmotionalQuestion; }
}
