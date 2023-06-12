package mn.erin.lms.unitel.domain.usecase.dto;

import java.time.LocalDate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetCourseAnalyticsInput
{
  private final String courseId;
  private final String departmentId;
  private String userId;
  private final LocalDate startDate;
  private final LocalDate endDate;

  public GetCourseAnalyticsInput(String courseId, String departmentId, LocalDate startDate, LocalDate endDate)
  {
    this.courseId = courseId;
    this.departmentId = departmentId;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public GetCourseAnalyticsInput(String courseId, String departmentId, LocalDate startDate, LocalDate endDate, String userId)
  {
    this.courseId = courseId;
    this.departmentId = departmentId;
    this.startDate = startDate;
    this.endDate = endDate;
    this.userId = userId;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getDepartmentId()
  {
    return departmentId;
  }

  public LocalDate getStartDate()
  {
    return startDate;
  }

  public LocalDate getEndDate()
  {
    return endDate;
  }

  public String getUserId()
  {
    return userId;
  }
}
