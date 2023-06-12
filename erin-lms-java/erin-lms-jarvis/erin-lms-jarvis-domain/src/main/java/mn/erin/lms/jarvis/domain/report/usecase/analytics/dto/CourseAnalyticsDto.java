package mn.erin.lms.jarvis.domain.report.usecase.analytics.dto;

import java.util.Collections;
import java.util.Map;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseAnalyticsDto
{
  private final String courseId;
  private final Map<String, Object> data;

  public CourseAnalyticsDto(String courseId, Map<String, Object> data)
  {
    this.courseId = courseId;
    this.data = data;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public Map<String, Object> getData()
  {
    return Collections.unmodifiableMap(data);
  }
}
