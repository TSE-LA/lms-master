package mn.erin.lms.base.domain.usecase.course.dto;

import mn.erin.lms.base.domain.model.course.CourseId;

/**
 * @author Temuulen Naranbold
 */
public class LearnerCourseHistoryInput
{
  private final CourseId courseId;
  private final String userId;
  private Float percentage;

  public LearnerCourseHistoryInput(CourseId courseId, String userId)
  {
    this.courseId = courseId;
    this.userId = userId;
  }

  public LearnerCourseHistoryInput(CourseId courseId, String userId, Float percentage)
  {
    this.courseId = courseId;
    this.userId = userId;
    this.percentage = percentage;
  }

  public CourseId getCourseId()
  {
    return courseId;
  }

  public String getUserId()
  {
    return userId;
  }

  public Float getPercentage()
  {
    return percentage;
  }

  public void setPercentage(Float percentage)
  {
    this.percentage = percentage;
  }
}
