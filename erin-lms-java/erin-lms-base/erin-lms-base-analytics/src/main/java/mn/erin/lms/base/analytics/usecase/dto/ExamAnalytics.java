package mn.erin.lms.base.analytics.usecase.dto;

import java.util.List;

import mn.erin.lms.base.analytics.model.Analytic;

/**
 * @author Byambajav
 */
public class ExamAnalytics
{
  private List<Analytic> analytics;

  public ExamAnalytics(List<Analytic> analytics)
  {
    this.analytics = analytics;
  }

  public List<Analytic> getAnalytics()
  {
    return analytics;
  }

  public void setAnalytics(List<Analytic> analytics)
  {
    this.analytics = analytics;
  }
}
