package mn.erin.lms.base.domain.usecase.certificate.dto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ReceiveCertificateInput
{
  private String learnerId;
  private String courseId;
  private String certificateId;
  private String certificatePdfId;

  public ReceiveCertificateInput(String learnerId, String courseId, String certificateId, String certificatePdfId)
  {
    this.learnerId = learnerId;
    this.courseId = courseId;
    this.certificateId = certificateId;
    this.certificatePdfId = certificatePdfId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getCertificateId()
  {
    return certificateId;
  }

  public String getCertificatePdfId()
  {
    return certificatePdfId;
  }
}
