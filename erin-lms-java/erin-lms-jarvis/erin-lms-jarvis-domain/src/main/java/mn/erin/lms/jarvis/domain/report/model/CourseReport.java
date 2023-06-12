package mn.erin.lms.jarvis.domain.report.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.LearnerId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseReport implements Entity<CourseReport>
{
  private final CourseId courseId;

  private Map<String, Object> reportData = new HashMap<>();
  private final Set<LearnerId> enrolledLearners = new HashSet<>();

  public CourseReport(CourseId courseId)
  {
    this.courseId = Objects.requireNonNull(courseId, "Course ID cannot be null!");
  }

  public void putReportData(String dataKey, Object dataValue)
  {
    Validate.notBlank(dataKey, "Data key cannot be null or blank!");
    Validate.notNull(dataValue, "Data value cannot be null!");
    this.reportData.put(dataKey, dataValue);
  }

  public void addLearner(LearnerId learnerId)
  {
    if (learnerId != null)
    {
      this.enrolledLearners.add(learnerId);
    }
  }

  public void setReportData(Map<String, Object> reportData)
  {
    this.reportData = reportData;
  }

  public CourseId getCourseId()
  {
    return courseId;
  }

  public Map<String, Object> getReportData()
  {
    return Collections.unmodifiableMap(reportData);
  }

  public Set<LearnerId> getEnrolledLearners()
  {
    return Collections.unmodifiableSet(enrolledLearners);
  }

  @Override
  public boolean sameIdentityAs(CourseReport other)
  {
    return this.courseId.equals(other.courseId);
  }
}
