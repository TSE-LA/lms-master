package mn.erin.lms.legacy.domain.lms.model.course;

import java.util.Objects;


/**
 * @author Zorig
 */
public class CourseGroup
{
  private final CourseGroupId courseGroupId;
  private final String groupId;
  private final CourseId courseId;

  public CourseGroup(CourseGroupId courseGroupId, String groupId, CourseId courseId)
  {
    this.courseGroupId = Objects.requireNonNull(courseGroupId, "Group Course ID cannot be null");
    this.groupId = Objects.requireNonNull(groupId, "Group ID cannot be null");
    this.courseId = Objects.requireNonNull(courseId, "Course ID cannot be null");
  }

  public CourseGroupId getCourseGroupId()
  {
    return courseGroupId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public CourseId getCourseId()
  {
    return courseId;
  }
}
