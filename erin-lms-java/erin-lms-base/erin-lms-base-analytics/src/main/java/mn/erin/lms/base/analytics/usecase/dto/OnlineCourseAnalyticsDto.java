package mn.erin.lms.base.analytics.usecase.dto;

import java.util.List;
import java.util.Map;

import mn.erin.lms.base.analytics.model.course.online_course.OnlineCourseAnalytic;

/**
 * @author Munkh
 */
public class OnlineCourseAnalyticsDto
{
  private LearnerFilter filter;
  private List<OnlineCourseAnalytic> analytics;
  private Map<String, Object> analyticData;

  public OnlineCourseAnalyticsDto()
  {
  }

  public OnlineCourseAnalyticsDto(LearnerFilter filter)
  {
    this.filter = filter;
  }

  public OnlineCourseAnalyticsDto(List<OnlineCourseAnalytic> analytics)
  {
    this.analytics = analytics;
  }
}
