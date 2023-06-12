package mn.erin.lms.base.analytics.usecase.dto;

/**
 * @author Munkh
 */
public class SurveyFilter
{
  private final String surveyId;
  private DateFilter dateFilter;
  private String courseId;

  public SurveyFilter(String surveyId, DateFilter dateFilter, String courseId)
  {
    this.surveyId = surveyId;
    this.dateFilter = dateFilter;
    this.courseId = courseId;
  }

  public String getSurveyId()
  {
    return surveyId;
  }

  public DateFilter getDateFilter()
  {
    return dateFilter;
  }

  public String getCourseId()
  {
    return courseId;
  }
}
