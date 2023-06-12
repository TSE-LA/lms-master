package mn.erin.lms.jarvis.domain.report.usecase.dto;

import java.time.LocalDate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetAssessmentReportInput
{
  private String assessmentId;
  private String courseId;
  private LocalDate startDate;
  private LocalDate endDate;
  private String requestType;

  public GetAssessmentReportInput(String assessmentId, String requestType, String courseId, LocalDate startDate, LocalDate endDate)
  {
    this.assessmentId = assessmentId;
    this.courseId = courseId;
    this.startDate = startDate;
    this.endDate = endDate;
    this.requestType = requestType;
  }

  public String getAssessmentId()
  {
    return assessmentId;
  }

  public LocalDate getStartDate()
  {
    return startDate;
  }

  public LocalDate getEndDate()
  {
    return endDate;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getRequestType()
  {
    return requestType;
  }
}
