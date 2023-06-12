package mn.erin.lms.base.domain.model.certificate;

import java.time.LocalDateTime;

import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.LearnerId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LearnerCertificate
{
  private LearnerId learnerId;
  private CourseId courseId;
  private CertificateId certificateId;
  private LocalDateTime receivedDate;

  public LearnerCertificate(LearnerId learnerId, CourseId courseId, CertificateId certificateId, LocalDateTime receivedDate)
  {
    this.learnerId = learnerId;
    this.courseId = courseId;
    this.certificateId = certificateId;
    this.receivedDate = receivedDate;
  }

  public LearnerId getLearnerId()
  {
    return learnerId;
  }

  public CourseId getCourseId()
  {
    return courseId;
  }

  public CertificateId getCertificateId()
  {
    return certificateId;
  }

  public LocalDateTime getReceivedDate()
  {
    return receivedDate;
  }
}
