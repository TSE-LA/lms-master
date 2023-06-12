package mn.erin.lms.base.rest.model;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestAnswer {
    private String value;
    private boolean correct;

    public RestAnswer() {
    }

    public RestAnswer(String value, boolean correct) {
        this.value = value;
        this.correct = correct;
    }

    public String getValue() {
        return value;
    }

    public boolean isCorrect() {
        return correct;
    }
}
