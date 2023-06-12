package mn.erin.lms.base.mongo.document.enrollment;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Document
public class MongoCourseEnrollment
{
  @Id
  private String id;

  @Indexed
  private String courseId;

  @Indexed
  private String learnerId;

  @Indexed
  private MongoEnrollmentState enrollmentState;

  @Indexed
  private LocalDateTime enrolledDate;

  public MongoCourseEnrollment()
  {
  }

  public MongoCourseEnrollment(String courseId, String learnerId, MongoEnrollmentState enrollmentState, LocalDateTime enrolledDate)
  {
    this.id = learnerId + "-" + courseId;
    this.courseId = courseId;
    this.learnerId = learnerId;
    this.enrollmentState = enrollmentState;
    this.enrolledDate = enrolledDate;
  }

  public void setEnrollmentState(MongoEnrollmentState enrollmentState)
  {
    this.enrollmentState = enrollmentState;
  }

  public String getId()
  {
    return id;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public MongoEnrollmentState getEnrollmentState()
  {
    return enrollmentState;
  }

  public LocalDateTime getEnrolledDate()
  {
    return enrolledDate;
  }
}
