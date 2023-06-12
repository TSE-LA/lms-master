package mn.erin.lms.legacy.domain.unitel.model;

import java.util.Objects;

import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UserActivityData
{
  private final LearnerId learnerId;
  private final Float averageStatus;
  private final Long overallTime;

  public UserActivityData(LearnerId learnerId, Float averageStatus, Long overallTime)
  {
    this.learnerId = Objects.requireNonNull(learnerId, "Learner ID cannot be null!");
    this.averageStatus = Objects.requireNonNull(averageStatus, "Average status cannot be null!");
    this.overallTime = Objects.requireNonNull(overallTime, "Overall time cannot be null!");
  }

  public Float getAverageStatus()
  {
    return averageStatus;
  }

  public Long getOverallTime()
  {
    return overallTime;
  }

  public LearnerId getLearnerId()
  {
    return learnerId;
  }
}
