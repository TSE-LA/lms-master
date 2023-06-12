package mn.erin.lms.jarvis.domain.report.usecase.analytics.dto;

import java.util.Set;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseCountDto
{
  private final String type;
  private final Set<CourseCountByCategory> courseCountByCategory;

  public CourseCountDto(String type, Set<CourseCountByCategory> courseCountByCategory)
  {
    this.type = type;
    this.courseCountByCategory = courseCountByCategory;
  }

  public String getType()
  {
    return type;
  }

  public Set<CourseCountByCategory> getCourseCountByCategory()
  {
    return courseCountByCategory;
  }
}
