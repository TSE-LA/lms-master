package mn.erin.lms.base.rest.model.exam;

import java.util.Set;

/**
 * @author Galsan Bayart.
 */
public class RestExamEnrollment
{
  private String examId;
  private String learnerId;
  private Set<String> questionIds;

  public RestExamEnrollment()
  {
    //doNothing
  }

  public String getExamId()
  {
    return examId;
  }

  public void setExamId(String examId)
  {
    this.examId = examId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }

  public Set<String> getQuestionIds()
  {
    return questionIds;
  }

  public void setQuestionIds(Set<String> questionIds)
  {
    this.questionIds = questionIds;
  }
}
