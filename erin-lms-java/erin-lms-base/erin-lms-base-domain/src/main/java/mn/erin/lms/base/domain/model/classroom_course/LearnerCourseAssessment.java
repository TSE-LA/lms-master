package mn.erin.lms.base.domain.model.classroom_course;

import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.LearnerId;

/**
 * @author Erdenetulga
 */
public class LearnerCourseAssessment
{
  private LearnerId learnerId;
  private CourseId courseId;
  private String sentDate;
  private boolean sendStatus;

  public LearnerCourseAssessment(LearnerId learnerId, CourseId courseId, String sentDate, boolean sendStatus)
  {
    this.learnerId = learnerId;
    this.courseId = courseId;
    this.sentDate = sentDate;
    this.sendStatus = sendStatus;
  }

  public LearnerId getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(LearnerId learnerId)
  {
    this.learnerId = learnerId;
  }

  public CourseId getCourseId()
  {
    return courseId;
  }

  public void setCourseId(CourseId courseId)
  {
    this.courseId = courseId;
  }

  public String getSentDate()
  {
    return sentDate;
  }

  public void setSentDate(String sentDate)
  {
    this.sentDate = sentDate;
  }

  public boolean isSendStatus()
  {
    return sendStatus;
  }

  public void setSendStatus(boolean sendStatus)
  {
    this.sendStatus = sendStatus;
  }
}
