package mn.erin.lms.base.analytics.usecase.dto;

import java.util.List;

import mn.erin.lms.base.aim.user.LearnerId;

/**
 * @author Munkh
 */
public class LearnerAnalytics
{
  private final LearnerId learnerId;
  private List<AnalyticDto> analytics;

  public LearnerAnalytics(LearnerId learnerId)
  {
    this.learnerId = learnerId;
  }

  public LearnerAnalytics(LearnerId learnerId, List<AnalyticDto> analytics)
  {
    this.learnerId = learnerId;
    this.analytics = analytics;
  }

  public LearnerId getLearnerId()
  {
    return learnerId;
  }

  public List<AnalyticDto> getAnalytics()
  {
    return analytics;
  }

  public void setAnalytics(List<AnalyticDto> analytics)
  {
    this.analytics = analytics;
  }
}

