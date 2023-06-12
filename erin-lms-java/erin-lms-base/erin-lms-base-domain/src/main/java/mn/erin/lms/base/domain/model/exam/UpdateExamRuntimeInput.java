package mn.erin.lms.base.domain.model.exam;

import java.util.List;

import mn.erin.lms.base.domain.model.exam.question.LearnerQuestionDto;

/**
 * @author Byambajav
 */
public class UpdateExamRuntimeInput
{
  private String examId;
  private double spentTime;
  private List<LearnerQuestionDto> learnerQuestion;

  public UpdateExamRuntimeInput(String examId, List<LearnerQuestionDto> learnerQuestion, double spentTime)
  {
    this.examId = examId;
    this.learnerQuestion = learnerQuestion;
    this.spentTime = spentTime;
  }

  public double getSpentTime()
  {
    return spentTime;
  }

  public void setSpentTime(double spentTime)
  {
    this.spentTime = spentTime;
  }

  public String getExamId()
  {
    return examId;
  }

  public void setExamId(String examId)
  {
    this.examId = examId;
  }

  public List<LearnerQuestionDto> getLearnerQuestion()
  {
    return learnerQuestion;
  }

  public void setLearnerQuestion(List<LearnerQuestionDto> learnerQuestion)
  {
    this.learnerQuestion = learnerQuestion;
  }
}
