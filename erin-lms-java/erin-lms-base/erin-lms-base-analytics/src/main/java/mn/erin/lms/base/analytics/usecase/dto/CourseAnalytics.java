package mn.erin.lms.base.analytics.usecase.dto;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.base.analytics.model.Analytic;

/**
 * @author Munkh
 */
public class CourseAnalytics
{
  private List<Analytic> analytics;
  private Map<String, Object> analyticData;

  public CourseAnalytics(List<Analytic> analytics)
  {
    this.analytics = analytics;
    analyticData = new HashMap<>();
  }

  public CourseAnalytics(List<Analytic> analytics, Map<String, Object> analyticData)
  {
    this.analytics = analytics;
    this.analyticData = analyticData;
  }

  public List<Analytic> getAnalytics()
  {
    return analytics;
  }

  public void setAnalytics(List<Analytic> analytics)
  {
    this.analytics = analytics;
  }

  public Map<String, Object> getAnalyticData()
  {
    return Collections.unmodifiableMap(analyticData);
  }

  public void addAnalyticData(String key, Object value)
  {
    Validate.notEmpty(key);
    Validate.notNull(value);
    analyticData.put(key, value);
  }

  public void setAnalyticData(Map<String, Object> analyticData)
  {
    this.analyticData = analyticData;
  }
}
