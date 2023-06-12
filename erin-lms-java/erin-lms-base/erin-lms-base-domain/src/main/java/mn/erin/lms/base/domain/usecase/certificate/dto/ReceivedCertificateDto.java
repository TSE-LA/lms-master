package mn.erin.lms.base.domain.usecase.certificate.dto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ReceivedCertificateDto
{
  private String learnerId;
  private String courseId;
  private String certificateId;
  private String courseName;
  private Integer score;
  private String date;
  private String courseType;

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

  public String getCertificateId()
  {
    return certificateId;
  }

  public void setCertificateId(String certificateId)
  {
    this.certificateId = certificateId;
  }

  public String getCourseName()
  {
    return courseName;
  }

  public void setCourseName(String courseName)
  {
    this.courseName = courseName;
  }

  public Integer getScore()
  {
    return score;
  }

  public void setScore(Integer score)
  {
    this.score = score;
  }

  public String getDate()
  {
    return date;
  }

  public void setDate(String date)
  {
    this.date = date;
  }

  public String getCourseType()
  {
    return courseType;
  }

  public void setCourseType(String courseType)
  {
    this.courseType = courseType;
  }
}
