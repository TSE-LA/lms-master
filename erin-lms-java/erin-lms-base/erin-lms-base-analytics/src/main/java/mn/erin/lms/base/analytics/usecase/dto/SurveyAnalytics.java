package mn.erin.lms.base.analytics.usecase.dto;

import java.util.List;

import mn.erin.lms.base.analytics.model.survey.SurveyAnalytic;

/**
 * @author Munkh
 */
public class SurveyAnalytics
{
  private List<SurveyAnalytic> analytics;

  public SurveyAnalytics(List<SurveyAnalytic> analytics)
  {
    this.analytics = analytics;
  }

  public List<SurveyAnalytic> getAnalytics()
  {
    return analytics;
  }

  public void setAnalytics(List<SurveyAnalytic> analytics)
  {
    this.analytics = analytics;
  }
}
