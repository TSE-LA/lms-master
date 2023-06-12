package mn.erin.lms.base.domain.usecase.exam.dto.question;

import java.util.HashSet;
import java.util.Set;

import mn.erin.lms.base.domain.usecase.exam.dto.LearnerAnswerDto;

public class QuestionAnswerDto
{
  private String questionId;
  private String questionValue;
  private int questionIndex;
  private Set<LearnerAnswerDto> learnerAnswerDto = new HashSet<>();
  private double totalScore;

  public String getQuestionId()
  {
    return questionId;
  }

  public void setQuestionId(String questionId)
  {
    this.questionId = questionId;
  }

  public String getQuestionValue()
  {
    return questionValue;
  }

  public void setQuestionValue(String questionValue)
  {
    this.questionValue = questionValue;
  }

  public int getQuestionIndex()
  {
    return questionIndex;
  }

  public void setQuestionIndex(int questionIndex)
  {
    this.questionIndex = questionIndex;
  }

  public Set<LearnerAnswerDto> getLearnerAnswerDto()
  {
    return learnerAnswerDto;
  }

  public void setLearnerAnswerDto(Set<LearnerAnswerDto> learnerAnswerDto)
  {
    this.learnerAnswerDto = learnerAnswerDto;
  }

  public double getTotalScore()
  {
    return totalScore;
  }

  public void setTotalScore(double totalScore)
  {
    this.totalScore = totalScore;
  }
}
