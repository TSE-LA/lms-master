package mn.erin.lms.base.rest.model;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestQuestion {
    private String question;
    private String questionType;
    private boolean isRequired;
    private List<RestAnswer> answers;
    private String assessmentId;

    public RestQuestion() {
    }

    public RestQuestion(String question, List<RestAnswer> answers) {
        this.question = question;
        this.answers = answers;
    }

    public RestQuestion(String question, String questionType, boolean isRequired, List<RestAnswer> answers)
    {
        this.question = question;
        this.questionType = questionType;
        this.isRequired = isRequired;
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public List<RestAnswer> getAnswers() {
        return answers;
    }

    public String getQuestionType()
    {
        return questionType;
    }

    public void setQuestion(String question)
    {
        this.question = question;
    }

    public void setQuestionType(String questionType)
    {
        this.questionType = questionType;
    }

    public void setAnswers(List<RestAnswer> answers)
    {
        this.answers = answers;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }
}
