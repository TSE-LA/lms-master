package mn.erin.lms.base.domain.usecase.assessment.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.base.domain.model.assessment.AssessmentId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UpdateQuizInput
{
  private String quizId;
  private List<QuestionInfo> questionInfos = new ArrayList<>();
  private Integer maxAttempts;
  private Double thresholdScore;
  private AssessmentId assessmentId;

  public UpdateQuizInput(String quizId)
  {
    this.quizId = Validate.notBlank(quizId, "Course test id cannot be blank!");
  }

  public String getQuizId()
  {
    return quizId;
  }

  public void setQuizId(String quizId)
  {
    this.quizId = quizId;
  }

  public List<QuestionInfo> getQuestionInfos()
  {
    return questionInfos;
  }

  public void addQuestionInfo(QuestionInfo questionInfo)
  {
    this.questionInfos.add(questionInfo);
  }

  public Integer getMaxAttempts()
  {
    return maxAttempts;
  }

  public void setMaxAttempts(Integer maxAttempts)
  {
    this.maxAttempts = maxAttempts;
  }

  public Double getThresholdScore()
  {
    return thresholdScore;
  }

  public void setThresholdScore(Double thresholdScore)
  {
    this.thresholdScore = thresholdScore;
  }

  public AssessmentId getAssessmentId() {
    return assessmentId;
  }

  public void setAssessmentId(AssessmentId assessmentId) {
    this.assessmentId = assessmentId;
  }
}
