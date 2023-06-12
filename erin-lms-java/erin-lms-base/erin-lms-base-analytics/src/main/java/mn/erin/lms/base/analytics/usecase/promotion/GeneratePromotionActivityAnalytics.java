package mn.erin.lms.base.analytics.usecase.promotion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mn.erin.common.datetime.TimeUtils;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.base.analytics.repository.mongo.PromotionAnalyticsRepository;
import mn.erin.lms.base.analytics.usecase.dto.LearnerActivity;
import mn.erin.lms.base.analytics.usecase.dto.PromotionActivityDto;
import org.apache.commons.lang3.Validate;

/**
 * @author Munkh
 */
public class GeneratePromotionActivityAnalytics implements UseCase<String, PromotionActivityDto>
{
  private final PromotionAnalyticsRepository promotionAnalyticsRepository;

  public GeneratePromotionActivityAnalytics(PromotionAnalyticsRepository promotionAnalyticsRepository)
  {
    this.promotionAnalyticsRepository = promotionAnalyticsRepository;
  }

  @Override
  public PromotionActivityDto execute(String input)
  {
    Validate.notBlank(input);
    List<LearnerActivity> activities = promotionAnalyticsRepository.getAllActivities(GroupId.valueOf(input));

    int perfectEmployees = 0;
    int perfectSupersAndManagers = 0;
    double totalProgress = 0;
    List<Long> spentTimes = new ArrayList<>();
    for (LearnerActivity activity: activities)
    {
      totalProgress += activity.getStatus();
      spentTimes.add(TimeUtils.convertToLongRepresentation(activity.getSpentTime()));
      if (activity.getStatus() >= 90.0)
      {
        if (activity.getRole().equals(LmsRole.LMS_USER.name()))
        {
          perfectEmployees++;
        }
        else if (activity.getRole().equals(LmsRole.LMS_SUPERVISOR.name()) ||
            activity.getRole().equals(LmsRole.LMS_MANAGER.name()))
        {
          perfectSupersAndManagers++;
        }
      }
    }

    Optional<Long> spentTimeSum = spentTimes.stream().reduce(Long::sum);

    long avgTime = spentTimeSum.map(aLong -> aLong / activities.size()).orElse(0L);
    double avgProgress = activities.isEmpty() ? 0 : totalProgress / activities.size();
    return new PromotionActivityDto(
        activities.size(),
        TimeUtils.convertToStringRepresentation(avgTime),
        perfectEmployees,
        perfectSupersAndManagers,
        avgProgress,
        activities
    );
  }
}
