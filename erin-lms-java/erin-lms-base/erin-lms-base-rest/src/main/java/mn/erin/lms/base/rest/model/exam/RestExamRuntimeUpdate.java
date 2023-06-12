package mn.erin.lms.base.rest.model.exam;

import java.util.List;


/**
 * @author Byambajav
 */
public class RestExamRuntimeUpdate
{
  private String examId;
  private List<RestLearnerQuestion> learnerQuestion;
  private long spentTime;

  public RestExamRuntimeUpdate()
  {
  }

  public double getSpentTime()
  {
    return spentTime;
  }

  public void setSpentTime(long spentTime)
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

  public List<RestLearnerQuestion> getLearnerQuestion()
  {
    return learnerQuestion;
  }

  public void setLearnerQuestion(List<RestLearnerQuestion> learnerQuestion)
  {
    this.learnerQuestion = learnerQuestion;
  }
}
