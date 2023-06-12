package mn.erin.lms.base.domain.usecase.exam.dto;

public class LearnerAnswerDto {

    private String answerId;
    private String answerValue;
    private int answerWeight;

    public LearnerAnswerDto(String answerId, String answerValue, int answerWeight) {
        this.answerId = answerId;
        this.answerValue = answerValue;
        this.answerWeight = answerWeight;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public String getAnswerValue() {
        return answerValue;
    }

    public void setAnswerValue(String answerValue) {
        this.answerValue = answerValue;
    }

    public int getAnswerWeight() {
        return answerWeight;
    }

    public void setAnswerWeight(int answerWeight) {
        this.answerWeight = answerWeight;
    }
}
