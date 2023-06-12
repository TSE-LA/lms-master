package mn.erin.lms.base.rest.model;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ReceiveCertificateBody
{
  private String learnerId;
  private String courseId;
  private String documentId;

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
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

  public String getDocumentId()
  {
    return documentId;
  }

  public void setDocumentId(String documentId)
  {
    this.documentId = documentId;
  }
}
