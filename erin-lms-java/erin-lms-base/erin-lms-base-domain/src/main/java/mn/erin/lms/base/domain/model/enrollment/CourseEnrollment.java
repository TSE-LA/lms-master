package mn.erin.lms.base.domain.model.enrollment;

import java.time.LocalDateTime;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.LearnerId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseEnrollment implements Entity<CourseEnrollment>, Comparable<CourseEnrollment>
{
  protected final CourseId courseId;
  protected final LearnerId learnerId;

  private EnrollmentState enrollmentState = EnrollmentState.NEW;
  private LocalDateTime enrolledDate;

  public CourseEnrollment(CourseId courseId, LearnerId learnerId)
  {
    this.courseId = Objects.requireNonNull(courseId, "Course ID cannot be null!");
    this.learnerId = Objects.requireNonNull(learnerId, "Learner ID cannot be null!");
    this.enrolledDate = LocalDateTime.now();
  }

  public void changeEnrollmentState(EnrollmentState enrollmentState)
  {
    if (this.enrollmentState != null)
    {
      this.enrollmentState = enrollmentState;
    }
  }

  public LocalDateTime getEnrolledDate()
  {
    return enrolledDate;
  }

  public void setEnrolledDate(LocalDateTime enrolledDate)
  {
    this.enrolledDate = enrolledDate;
  }

  public CourseId getCourseId()
  {
    return courseId;
  }

  public LearnerId getLearnerId()
  {
    return learnerId;
  }

  public EnrollmentState getEnrollmentState()
  {
    return enrollmentState;
  }

  @Override
  public boolean sameIdentityAs(CourseEnrollment other)
  {
    return this.courseId.equals(other.courseId) && this.learnerId.equals(other.learnerId);
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    CourseEnrollment that = (CourseEnrollment) o;
    return courseId.equals(that.courseId) && learnerId.equals(that.learnerId);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(courseId, learnerId);
  }

  @Override
  public int compareTo(@NotNull CourseEnrollment other)
  {
    return enrolledDate.compareTo(other.enrolledDate);
  }
}
