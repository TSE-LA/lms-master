package mn.erin.lms.base.rest.model;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class RestCertificate
{
  private String certificateId;
  private String certificateName;
  private String recipient;
  private String courseId;
  private String courseName;
  private String targetPath;
  private String fileName;

  public RestCertificate()
  {
  }

  public String getCertificateName()
  {
    return certificateName;
  }

  public void setCertificateName(String certificateName)
  {
    this.certificateName = certificateName;
  }

  public String getRecipient()
  {
    return recipient;
  }

  public void setRecipient(String recipient)
  {
    this.recipient = recipient;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }

  public String getCourseName()
  {
    return courseName;
  }

  public void setCourseName(String courseName)
  {
    this.courseName = courseName;
  }

  public String getTargetPath()
  {
    return targetPath;
  }

  public void setTargetPath(String targetPath)
  {
    this.targetPath = targetPath;
  }

  public String getFileName()
  {
    return fileName;
  }

  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }

  public String getCertificateId()
  {
    return certificateId;
  }

  public void setCertificateId(String certificateId)
  {
    this.certificateId = certificateId;
  }
}
