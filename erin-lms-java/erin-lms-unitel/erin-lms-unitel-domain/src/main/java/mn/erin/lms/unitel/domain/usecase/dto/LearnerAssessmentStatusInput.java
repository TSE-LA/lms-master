package mn.erin.lms.unitel.domain.usecase.dto;

/**
 * @author Erdenetulga
 */
public class LearnerAssessmentStatusInput
{
  private String courseId;
  private String learnerId;

  public LearnerAssessmentStatusInput(String courseId, String learnerId)
  {
    this.courseId = courseId;
    this.learnerId = learnerId;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }
}
