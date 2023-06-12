package mn.erin.lms.unitel.mongo.document;

import java.util.Set;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoReportData
{
  private String title;
  private String category;
  private String state;
  private String authorId;
  private boolean hasCertificate;

  private float enrollmentCount;
  private Set<EnrolledGroup> enrolledGroups;
  private Set<String> enrolledGroupIds;

  private Integer viewersCount;
  private Float completedViewersCount;
  private Integer repeatedViewersCount;
  private Float receivedCertificateCount;
  private Integer survey;
  private Float status;

  public MongoReportData()
  {
  }

  public MongoReportData
      (
          String title,
          String category,
          String state,
          String authorId,
          boolean hasCertificate,
          float enrollmentCount,
          Set<EnrolledGroup> enrolledGroups,
          Set<String> enrolledGroupIds,
          Integer viewersCount,
          Float completedViewersCount,
          Integer repeatedViewersCount,
          Float receivedCertificateCount,
          Integer survey,
          Float status
      )
  {
    this.title = title;
    this.category = category;
    this.state = state;
    this.authorId = authorId;
    this.hasCertificate = hasCertificate;
    this.enrolledGroupIds = enrolledGroupIds;
    this.survey = survey;
    this.enrollmentCount = enrollmentCount;
    this.enrolledGroups = enrolledGroups;
    this.viewersCount = viewersCount;
    this.completedViewersCount = completedViewersCount;
    this.repeatedViewersCount = repeatedViewersCount;
    this.receivedCertificateCount = receivedCertificateCount;
    this.status = status;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getCategory()
  {
    return category;
  }

  public void setCategory(String category)
  {
    this.category = category;
  }

  public String getState()
  {
    return state;
  }

  public void setState(String state)
  {
    this.state = state;
  }

  public String getAuthorId()
  {
    return authorId;
  }

  public void setAuthorId(String authorId)
  {
    this.authorId = authorId;
  }

  public boolean isHasCertificate()
  {
    return hasCertificate;
  }

  public void setHasCertificate(boolean hasCertificate)
  {
    this.hasCertificate = hasCertificate;
  }

  public Integer getSurvey()
  {
    return survey;
  }

  public void setSurvey(Integer survey)
  {
    this.survey = survey;
  }

  public float getEnrollmentCount()
  {
    return enrollmentCount;
  }

  public void setEnrollmentCount(Integer enrollmentCount)
  {
    this.enrollmentCount = enrollmentCount;
  }

  public Integer getViewersCount()
  {
    return viewersCount;
  }

  public void setViewersCount(Integer viewersCount)
  {
    this.viewersCount = viewersCount;
  }

  public Float getCompletedViewersCount()
  {
    return completedViewersCount;
  }

  public void setCompletedViewersCount(Float completedViewersCount)
  {
    this.completedViewersCount = completedViewersCount;
  }

  public Integer getRepeatedViewersCount()
  {
    return repeatedViewersCount;
  }

  public void setRepeatedViewersCount(Integer repeatedViewersCount)
  {
    this.repeatedViewersCount = repeatedViewersCount;
  }

  public Float getReceivedCertificateCount()
  {
    return receivedCertificateCount;
  }

  public void setReceivedCertificateCount(Float receivedCertificateCount)
  {
    this.receivedCertificateCount = receivedCertificateCount;
  }

  public Float getStatus()
  {
    return status;
  }

  public void setStatus(Float status)
  {
    this.status = status;
  }

  public Set<String> getEnrolledGroupId()
  {
    return enrolledGroupIds;
  }

  public void setEnrolledGroupId(Set<String> enrolledGroupId)
  {
    this.enrolledGroupIds = enrolledGroupId;
  }

  public Set<EnrolledGroup> getEnrolledGroups()
  {
    return enrolledGroups;
  }

  public void setEnrolledGroups(Set<EnrolledGroup> enrolledGroups)
  {
    this.enrolledGroups = enrolledGroups;
  }
}
