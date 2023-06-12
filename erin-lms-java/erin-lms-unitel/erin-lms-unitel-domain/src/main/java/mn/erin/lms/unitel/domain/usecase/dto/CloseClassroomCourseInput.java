package mn.erin.lms.unitel.domain.usecase.dto;

/**
 * @author Erdenetulga
 */
public class CloseClassroomCourseInput
{
  private final String courseId;
  private final String state;
  private String learnerId;
  private String certificateId;
  private String certificatePdfId;

  public CloseClassroomCourseInput(String courseId, String state)
  {
    this.courseId = courseId;
    this.state = state;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getState()
  {
    return state;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public String getCertificateId()
  {
    return certificateId;
  }

  public String getCertificatePdfId()
  {
    return certificatePdfId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }

  public void setCertificateId(String certificateId)
  {
    this.certificateId = certificateId;
  }

  public void setCertificatePdfId(String certificatePdfId)
  {
    this.certificatePdfId = certificatePdfId;
  }
}
