package mn.erin.lms.base.domain.model.report;

import java.util.List;

/**
 * @author Erdenetulga
 */
public class LearnerAssessment
{
  private String learnerId;
  private List<LearnerAnswer> answers;

  public LearnerAssessment()
  {
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }

  public List<LearnerAnswer> getAnswers()
  {
    return answers;
  }

  public void setAnswers(List<LearnerAnswer> answers)
  {
    this.answers = answers;
  }
}
