package mn.erin.lms.unitel.mongo.document;

/**
 * @author Erdenetulga
 */
public class MongoLearnerCourseAssessment
{
  private String courseId;
  private String learnerId;
  private boolean sendStatus;
  private String sentDate;

  public MongoLearnerCourseAssessment(String courseId, String learnerId, boolean sendStatus, String sentDate)
  {
    this.courseId = courseId;
    this.learnerId = learnerId;
    this.sendStatus = sendStatus;
    this.sentDate = sentDate;
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

  public boolean isSendStatus()
  {
    return sendStatus;
  }

  public void setSendStatus(boolean sendStatus)
  {
    this.sendStatus = sendStatus;
  }

  public String getSentDate()
  {
    return sentDate;
  }

  public void setSentDate(String sentDate)
  {
    this.sentDate = sentDate;
  }
}
