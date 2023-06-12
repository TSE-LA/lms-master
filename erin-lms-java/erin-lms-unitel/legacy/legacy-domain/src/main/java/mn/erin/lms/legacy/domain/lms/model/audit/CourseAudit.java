package mn.erin.lms.legacy.domain.lms.model.audit;

import java.util.Objects;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseAudit implements Entity<CourseAudit>
{
  private final CourseAuditId id;
  private final CourseId courseId;
  private final LearnerId learnerId;

  public CourseAudit(CourseAuditId id, CourseId courseId, LearnerId learnerId)
  {
    this.id = Objects.requireNonNull(id, "Course audit ID is required!");
    this.courseId = Objects.requireNonNull(courseId, "Course ID cannot be null!");
    this.learnerId = Objects.requireNonNull(learnerId, "Learner ID cannot be null!");
  }

  @Override
  public boolean sameIdentityAs(CourseAudit other)
  {
    return false;
  }

  public CourseAuditId getId()
  {
    return id;
  }

  public CourseId getCourseId()
  {
    return courseId;
  }

  public LearnerId getLearnerId()
  {
    return learnerId;
  }
}
