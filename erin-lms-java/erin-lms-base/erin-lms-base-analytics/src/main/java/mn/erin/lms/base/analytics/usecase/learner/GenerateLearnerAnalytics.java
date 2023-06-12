package mn.erin.lms.base.analytics.usecase.learner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.analytics.exception.AnalyticsRepositoryException;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.model.course.online_course.OnlineCourseStatistics;
import mn.erin.lms.base.analytics.model.learner.LearnerAnalytic;
import mn.erin.lms.base.analytics.repository.AnalyticsRepositoryRegistry;
import mn.erin.lms.base.analytics.usecase.AnalyticsUseCase;
import mn.erin.lms.base.analytics.usecase.dto.LearnerAnalytics;
import mn.erin.lms.base.analytics.usecase.dto.LearnerFilter;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.service.CourseTypeResolver;

/**
 * @author Munkh
 */
public class GenerateLearnerAnalytics extends AnalyticsUseCase<LearnerFilter, LearnerAnalytics>
{
  public GenerateLearnerAnalytics(AnalyticsRepositoryRegistry analyticsRepositoryRegistry, CourseTypeResolver courseTypeResolver
  )
  {
    super(analyticsRepositoryRegistry, courseTypeResolver);
  }

  @Override
  public LearnerAnalytics execute(LearnerFilter input) throws UseCaseException
  {
    Validate.notNull(input);

    List<Analytic> analytics = analyticsRepositoryRegistry.getLearnerAnalyticsRepository().getAnalytics(
        LearnerId.valueOf(input.getLearnerId()),
        CourseCategoryId.valueOf(input.getCategoryId()),
        input.getDateFilter().getStartDate(),
        input.getDateFilter().getEndDate()
    );

    for (Analytic analytic : analytics)
    {
      LearnerAnalytic learnerAnalytic = (LearnerAnalytic) analytic;
      try
      {
        List<Analytic> statistics = analyticsRepositoryRegistry.getCourseStatisticsRepository().getStatistics(
            learnerAnalytic.getCourseId(),
            GroupId.valueOf(input.getGroupId()),
            input.getDateFilter().getStartDate(),
            input.getDateFilter().getEndDate()
        );
        int totalSpentTime = 0;
        int counter = 0;

        for (Analytic statistic : statistics)
        {
          OnlineCourseStatistics onlineCourseStatistics = (OnlineCourseStatistics) statistic;
          int spentTimeAsSecond = convertToSecond(onlineCourseStatistics.getSpentTime());
          if (spentTimeAsSecond != 0)
          {
            totalSpentTime += spentTimeAsSecond;
            counter++;
          }
        }
        String averageSpentTime = convertToTimeFormat(counter != 0 ? totalSpentTime / counter : 0);
        learnerAnalytic.setSpentTimeRatio(learnerAnalytic.getSpentTime() + "/" + averageSpentTime);
      }
      catch (AnalyticsRepositoryException e)
      {
        throw new UseCaseException(e.getMessage(), e);
      }
    }

    return new LearnerAnalytics(LearnerId.valueOf(input.getLearnerId()), toLearnerAnalyticDtos(analytics));
  }

  private int convertToSecond(String time)
  {
    String[] splittedTime = time.split(":");
    return (int) (Integer.parseInt(splittedTime[0]) * 3600 + Integer.parseInt(splittedTime[1]) * 60 + Double.parseDouble(splittedTime[2]));
  }

  private String convertToTimeFormat(int second)
  {
    Date d = new Date(second * 1000L);
    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    df.setTimeZone(TimeZone.getTimeZone("GMT"));
    return df.format(d);
  }
}
