package mn.erin.lms.base.domain.usecase.assessment.dto;

/**
 *
 * @author Munkh
 */
public class UpdateAssessmentStatusInput
{
  private final String assessmentId;
  private final boolean activate;
  private String currentCourseId;

  public UpdateAssessmentStatusInput(String assessmentId, boolean activate)
  {
    this.assessmentId = assessmentId;
    this.activate = activate;
  }

  public String getAssessmentId()
  {
    return assessmentId;
  }

  public boolean isActivate()
  {
    return activate;
  }

  public String getCurrentCourseId()
  {
    return currentCourseId;
  }

  public void setCurrentCourseId(String currentCourseId)
  {
    this.currentCourseId = currentCourseId;
  }
}
