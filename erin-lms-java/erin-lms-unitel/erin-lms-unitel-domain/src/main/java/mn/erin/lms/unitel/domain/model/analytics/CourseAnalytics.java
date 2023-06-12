package mn.erin.lms.unitel.domain.model.analytics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.course.CourseId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseAnalytics implements Entity<CourseAnalytics>
{
  private final CourseId courseId;

  private Map<LearnerId, Object> analyticData = new HashMap<>();

  public CourseAnalytics(CourseId courseId)
  {
    this.courseId = Objects.requireNonNull(courseId, "Course ID cannot be null!");
  }

  public void addData(LearnerId learnerId, Object data)
  {
    this.analyticData.put(learnerId, data);
  }

  public CourseId getCourseId()
  {
    return courseId;
  }

  public Map<LearnerId, Object> getAnalyticData()
  {
    return Collections.unmodifiableMap(analyticData);
  }

  @Override
  public boolean sameIdentityAs(CourseAnalytics other)
  {
    return this.courseId.equals(other.courseId);
  }
}
